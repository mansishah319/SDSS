<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->
<script lang="ts">
import { computed, defineComponent, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { IMessage, StompSubscription } from '@stomp/stompjs'
import Attendees from '@/views/challenges/dashboard/subComponents/attendees/Attendees.vue'
import SolutionChat from '@/views/challenges/dashboard/subComponents/SolutionChat.vue'
import SolutionDetail from '@/views/challenges/dashboard/subComponents/SolutionDetail.vue'
import VotingRound from '@/views/challenges/dashboard/subComponents//VotingRound.vue'
import RatingPopup from '@/views/challenges/dashboard/subComponents/rating/RatingPopup.vue'
import { toast } from 'vue3-toastify'
import {
  create,
  createWithActiveSocket,
  deleteRequest,
  get,
  httpGenericWebSocketRequest,
  update
} from '@/utils/GenericApiHelper.js'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.js'
import { Characters, Fields, SecurityConstants, URIS } from '@/constants/DpConstants.js'
import { ChallengeDashboardResponse } from '@/dto/challenge/ChallengeDashboardResponse.js'
import { SolutionFullResponse } from '@/dto/challenge/SolutionFullResponse.js'
import { WebsocketClient } from '@/utils/WebsocketClient.js'
import { useI18n } from 'vue-i18n'
import { ChallengeStatus } from '@/dto/challenge/ChallengeStatus.js'
import { ChallengesResponse } from '@/dto/challenge/ChallengesResponse.js'
import { VotingResponse } from '@/dto/voting/VotingResponse.js'
import jwt_decode from 'jwt-decode'
import Employee from '@/dto/Employee.js'
import {
  isAdminOrModerator,
  isCommanderOnly,
  isHasAnyRole,
  isModeratorOnly,
  isValidatorOnly
} from '@/utils/roleUtils.js'
import { SurveyCreationRequest } from '@/dto/survey/SurveyCreationRequest.js'
import { SurveyForSolutionResponse } from '@/dto/survey/SurveyResponse.js'
import { ChallengeSolutionResponse } from '@/dto/challenge/ChallengeSolutionResponse.js'
import { Countdown } from 'vue3-flip-countdown'
import { DateTime } from 'luxon'
import { ChallengeDashboardSearchType } from '@/enums/SearchTypes'
import SearchComponent from '@/views/challenges/dashboard/subComponents/search/SearchCard.vue'
import MemberFinalSolutionsDialog
  from '@/views/challenges/dashboard/subComponents/final/member/MemberFinalSolutionsDialog.vue'
import SolutionRateDetails from '@/views/challenges/dashboard/subComponents/fullSolutionRate/SolutionRateDetails.vue'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import { ChallengeUsersResponse } from '@/dto/challenge/ChallengeUsersResponse'
import { ChallengeAttendeesUsersResponse } from '@/dto/challenge/ChallengeAttendeesUsersResponse'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import {
  formatChallengeStatusLabel,
  getChallengeStatusAppearance,
  normalizeChallengeStatus
} from '@/utils/challengeStatusUtils'

type ChallengeStatusSocketPayload = {
  challengeId?: number
  status?: string
  sender?: string
}

type ChallengeAttendeesSocketPayload = {
  challengeId?: number
  attendeesResponse?: ChallengeAttendeesUsersResponse
  onlineCount?: number
  offlineCount?: number
  allAttendeesUsers?: ChallengeAttendeesUsersResponse['allAttendeesUsers']
}

const MODERATOR_VOTE_COLORS = [
  '#E8F5E9',
  '#F0F4C3',
  '#B2EBF2',
  '#FFFDE7',
  '#ECEFF1',
  '#D7CCC8',
  '#E0E0E0',
  '#BDBDBD',
  '#FFCC80',
  '#FFE0B2',
  '#80CBC4',
  '#C5CAE9',
  '#E8EAF6',
  '#D1C4E9',
  '#E3F2FD',
  '#90CAF9',
  '#E1BEE7',
  '#F3E5F5',
  '#FCE4EC',
  '#F8BBD0',
  '#F48FB1',
  '#EF9A9A',
  '#FFCDD2',
  '#FFEBEE',
  '#B2DFDB'
]

export default defineComponent({
  name: 'ChallengeDashboard',
  components: {
    GenericDashboardHeaderView,
    EmptyDataTable,
    ConfirmationDialog,
    SolutionRateDetails,
    MemberFinalSolutionsDialog,
    SearchComponent,
    Attendees,
    SolutionChat,
    SolutionDetail,
    VotingRound,
    RatingPopup,
    Countdown
  },
  props: {
    challengeId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const stompsocket = WebsocketClient.getInstance().getStompClient()
    const websocketClient = WebsocketClient.getInstance()
    const solutionSubscription = ref<StompSubscription | null>(null)
    const statusSubscription = ref<StompSubscription | null>(null)
    const attendeesCountSubscription = ref<StompSubscription | null>(null)

    const token = localStorage.getItem(SecurityConstants.TOKEN)
    const userData: Employee | null = token ? jwt_decode(token) : null

    const isTheChallengeModerator = ref<boolean>(false)

    const { t, locale } = useI18n()
    const employeeLang: string = localStorage.getItem(Fields.EMPLOYEE_LANG) || locale.value || 'ar'

    const isLoading = ref(false)
    const DUBAI_TIMEZONE = 'Asia/Dubai'

    const challengeDashboardResponse = ref<ChallengeDashboardResponse>({
      id: 0,
      isFinal: 0,
      startTime: '',
      endTime: '',
      challengeTitle: '',
      challengeDetails: '',
      youtubeUrls: [],
      challengeSolutions: [],
      status: '',
      statusLocal: '',
      queLevel: 0,
      moderator: {} as ChallengeUsersResponse,
      assistantModerator: {} as ChallengeUsersResponse
    })

    const answers = ref<ChallengeSolutionResponse[]>([])
    const solutionForModeratorRateView = ref<ChallengeSolutionResponse>({
      id: 0,
      solutionText: '',
      isFinal: false,
      isAdminAgree: false,
      queLevel: 0,
      solutionColor: 0,
      voteColor: 0,
      voteCount: 0,
      point: 0,
      rank: 0,
      isCurrentUserVoted: false,
      finalRate: 0,
      initPublic: false
    })

    const modifySolution = ref(false)
    const finishRound = ref(false)
    const selectedSolution = ref({})
    const currentRound = ref(1)
    const newAnswer = ref('')
    const canVote = ref(false) // Set to true when answer time ends
    const canAnswer = ref(true)
    const voteResults = ref({})

    const roundOver = ref(false)

    const searchQuery = ref('') // Model for the search input
    const internalSearchResults = ref([]) // To store internal search results

    const searchType = ref<ChallengeDashboardSearchType>(ChallengeDashboardSearchType.GOOGLE)

    const finalResults = ref([])

    const startTimeDubai = ref()
    const endTimeDubai = ref()

    const countdown = ref('')
    const countdownIntervalId = ref<number | null>(null)

    const initialSearchType = ref(ChallengeDashboardSearchType.INTERNAL)
    const initialSearchQuery = ref('')
    const initialSearchResults = ref([])

    const dataLoaded = ref(false)
    const isFinalSolutionsDialogOpen = ref(false)

    const isConfirmationDialogVisible = ref(false)
    const dialogTitle = ref<string>('')
    const dialogMsg = ref<string>('')

    const solutionForDialog = ref<Partial<SolutionFullResponse>>({})

    const socialMediaPosts = ref([{ content: 'Post 1 content here' }])

    const challengeStatus = ref('')
    const isRatingPopupOpen = ref(false)
    const isRatingFullSolutionPopupDetailsOpen = ref(false)
    const selectedSolutionIdForRating = ref<number>(0)

    const currentUserGrpNumber = ref<number>(userData?.grpNumber as number)

    const ratingSolutionList = ref<SurveyForSolutionResponse[]>([])

    const finalSolutions = ref<ChallengeSolutionResponse[]>([])

    const ratingInitialData = ref({})

    const employeeJson: string | null = localStorage.getItem(Fields.EMPLOYEE)
    const currentEmployee: Employee = employeeJson ? JSON.parse(employeeJson) : new Employee()

    const onlineCount = ref(0)
    const offlineCount = ref(0)
    const showAttendees = ref(false)
    let pendingSolutionsScroll = false

    // Solutions list auto-scroll functionality
    const solutionsList = ref<any>(null)

    const scrollSolutionsToBottom = (behavior: ScrollBehavior = 'smooth') => {
      if (pendingSolutionsScroll) {
        return
      }

      pendingSolutionsScroll = true
      nextTick(() => {
        requestAnimationFrame(() => {
          pendingSolutionsScroll = false
          const listElement = (solutionsList.value?.$el as HTMLElement | undefined) || (solutionsList.value as HTMLElement | null)

          if (listElement) {
            listElement.scrollTo({
              top: listElement.scrollHeight,
              behavior
            })
          }
        })
      })
    }

    const syncSolutionsState = (solutions: ChallengeSolutionResponse[]) => {
      const currentQueueLevel = challengeDashboardResponse.value.queLevel === 0 ? 1 : challengeDashboardResponse.value.queLevel
      answers.value = solutions.filter((solution: ChallengeSolutionResponse) => solution.queLevel === currentQueueLevel)
      finalSolutions.value =
        challengeDashboardResponse.value.isFinal === 1
          ? solutions.filter((solution: ChallengeSolutionResponse) => solution.isFinal)
          : []
    }

    // Stepper logic
    const stepperSteps = computed(() => {
      const steps: string[] = []
      const totalRounds = challengeDashboardResponse.value.queLevel || 1
      for (let i = 1; i <= totalRounds; i++) {
        steps.push(`${t('challenge.dashboard.round')} ${i}`)
      }
      return steps
    })
    const currentStep = computed(() => challengeDashboardResponse.value.queLevel || 1)
    const isFinalStage = computed(
      () => challengeDashboardResponse.value.isFinal === 1 || challengeDashboardResponse.value.isFinal === Number(true)
    )
    const canToggleAttendees = computed(() => isTheChallengeModerator.value === true)
    const canViewFinalSolutionsButton = computed(
      () => isFinalStage.value && (isTheChallengeModerator.value || isCommanderOnly() || isValidatorOnly())
    )

    const startCountdownTimer = () => {
      if (countdownIntervalId.value !== null) {
        window.clearInterval(countdownIntervalId.value)
      }

      calculateCountdown()
      currentDubaiTime.value = convertToDubaiTime(Date.now())

      countdownIntervalId.value = window.setInterval(() => {
        calculateCountdown()
        currentDubaiTime.value = convertToDubaiTime(Date.now())
      }, 1000)
    }

    const stopCountdownTimer = () => {
      if (countdownIntervalId.value !== null) {
        clearInterval(countdownIntervalId.value)
        countdownIntervalId.value = null
      }
    }

    // retrieve from the backend
    const loadChallengeDashboardData = async (challengeId: any, preferredStatus?: string) => {
      try {
        isLoading.value = true
        const response = await get<SuccessResponse<ChallengeDashboardResponse>>(
          URIS.CHALLENGES.BASE + URIS.CHALLENGES.DASHBOARD + Characters.QUESTION_MARK + 'challengeId=' + challengeId
        )
        if (response?.success) {
          challengeDashboardResponse.value = response.data
          const currentEmployeeGrpNumber = userData?.grpNumber
          startTimeDubai.value = convertToDubaiTime(challengeDashboardResponse.value.startTime)
          endTimeDubai.value = convertToDubaiTime(challengeDashboardResponse.value.endTime)
          isTheChallengeModerator.value =
            response.data.moderator.grpNumber == currentEmployeeGrpNumber ||
            response.data?.assistantModerator?.grpNumber == currentEmployeeGrpNumber

          if (preferredStatus) {
            challengeDashboardResponse.value.status = preferredStatus
          }

          challengeStatus.value = preferredStatus || challengeDashboardResponse.value.status
          if (challengeDashboardResponse.value.challengeSolutions) {
            syncSolutionsState(challengeDashboardResponse.value.challengeSolutions)
          }
          startCountdownTimer()
        } else {
          console.error('API request was not successful:', response?.message)
          // Handle error scenario
        }
      } catch (error) {
        console.error('Error loading challenge dashboard data:', error)
        // Handle error scenario
      } finally {
        isLoading.value = false
        dataLoaded.value = true
        // Auto-scroll to bottom after loading challenge data
        scrollSolutionsToBottom('auto')
      }
    }

    const convertToDubaiTime = (dateString: any) => {
      // Convert the date string to a JavaScript Date object
      const date = new Date(dateString)
      // Get the timestamp in milliseconds
      const timestamp = date.valueOf()
      return DateTime.fromMillis(timestamp).setZone(DUBAI_TIMEZONE)
    }

    const currentDubaiTime = ref(convertToDubaiTime(Date.now()))

    const calculateCountdown = () => {
      const endDate = new Date(challengeDashboardResponse.value.endTime).valueOf()
      const now = Date.now()
      const timeRemaining = endDate - now
      if (timeRemaining <= 0) {
        countdown.value = t('common.expired')
      } else {
        const days = Math.floor(timeRemaining / (1000 * 60 * 60 * 24))
        const hours = Math.floor((timeRemaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
        const minutes = Math.floor((timeRemaining % (1000 * 60 * 60)) / (1000 * 60))
        const seconds = Math.floor((timeRemaining % (1000 * 60)) / 1000)

        countdown.value = days + 'D:' + hours + 'H:' + minutes + 'M:' + seconds + 'S'
      }
    }

    const goToVideo = (link: any) => {
      window.open(link, '_blank')
    }

    const setSearchType = (type: ChallengeDashboardSearchType) => {
      searchType.value = type
    }

    const timerState = ref('stopped')

    const timerButtonLabel = computed(() => {
      if (challengeStatus.value === ChallengeStatus.VOTING) {
        return t('challenge.dashboard.pause')
      }
      return challengeStatus.value === ChallengeStatus.PAUSED
        ? t('challenge.dashboard.resume')
        : t('challenge.dashboard.start')
    })

    const titleBasedOnStatus = computed(() => {
      return challengeDashboardResponse.value.statusLocal?.trim() || formatChallengeStatusLabel(challengeStatus.value)
    })

    const challengeTitleDisplay = computed(
      () => challengeDashboardResponse.value.challengeTitle?.trim() || t('challenge.dashboard.challengeTitle')
    )

    const challengeStatusAppearance = computed(() =>
      getChallengeStatusAppearance(challengeStatus.value || challengeDashboardResponse.value.status)
    )

    const normalizedChallengeStatus = computed(
      () =>
        normalizeChallengeStatus(challengeStatus.value || challengeDashboardResponse.value.status) ||
        ChallengeStatus.NEW
    )

    const isSolutionStage = computed(
      () =>
        normalizedChallengeStatus.value === ChallengeStatus.OPEN ||
        normalizedChallengeStatus.value === ChallengeStatus.NEW
    )

    const isVotingStage = computed(() => normalizedChallengeStatus.value === ChallengeStatus.VOTING)
    const isPausedStage = computed(() => normalizedChallengeStatus.value === ChallengeStatus.PAUSED)
    const isClosedStage = computed(() => normalizedChallengeStatus.value === ChallengeStatus.CLOSED)
    const isVotingBoardVisible = computed(() => isVotingStage.value || isPausedStage.value)

    const participantStatusTitle = computed(() => {
      if (isPausedStage.value) {
        return isLanguageAr() ? 'تم إيقاف التصويت مؤقتاً' : 'Voting is paused'
      }

      if (isVotingStage.value) {
        return isLanguageAr() ? 'التصويت جارٍ الآن' : 'Voting is live now'
      }

      if (isClosedStage.value) {
        return isLanguageAr() ? 'تم إغلاق التحدي' : 'Challenge is closed'
      }

      return isLanguageAr() ? 'بانتظار بدء التصويت' : 'Waiting for voting to start'
    })

    const participantStatusDescription = computed(() => {
      if (isPausedStage.value) {
        return t('common.status.paused.description')
      }

      if (isVotingStage.value) {
        return t('common.status.voting.description')
      }

      if (isClosedStage.value) {
        return t('common.status.closed.description')
      }

      return t('common.status.open.description')
    })

    const challengeStatusChipStyle = computed(() => ({
      '--challenge-status-accent': challengeStatusAppearance.value.color,
      '--challenge-status-bg': challengeStatusAppearance.value.backgroundColor,
      '--challenge-status-border': challengeStatusAppearance.value.borderColor,
      '--challenge-status-text': challengeStatusAppearance.value.textColor
    }))

    const challengeStatusIcon = computed(() => challengeStatusAppearance.value.icon)

    const applyAttendeesCountPayload = (payload?: ChallengeAttendeesSocketPayload | ChallengeAttendeesUsersResponse | null) => {
      if (!payload) {
        return
      }

      const scopedChallengeId =
        'challengeId' in payload && payload.challengeId !== undefined ? Number(payload.challengeId) : null
      if (scopedChallengeId !== null && scopedChallengeId !== Number(props.challengeId)) {
        return
      }

      const attendeesResponse =
        'attendeesResponse' in payload && payload.attendeesResponse ? payload.attendeesResponse : payload

      const allAttendeesUsers = attendeesResponse?.allAttendeesUsers || []
      let onlineUsers = 0
      let offlineUsers = 0

      for (const attendee of allAttendeesUsers) {
        if (attendee.onlineStatus) {
          onlineUsers += 1
        } else {
          offlineUsers += 1
        }
      }

      onlineCount.value = onlineUsers || attendeesResponse?.onlineCount || 0
      offlineCount.value = offlineUsers || attendeesResponse?.offlineCount || 0
    }

    const loadAttendeesCounts = async () => {
      const challengeId = Number(props.challengeId)
      if (!challengeId) {
        return
      }

      try {
        const response = await httpGenericWebSocketRequest<SuccessResponse<ChallengeAttendeesUsersResponse>>(
          'get',
          URIS.WEB_SOCKET.CHALLENGES.BASE +
          URIS.WEB_SOCKET.CHALLENGES.USER_ATTENDEES +
          Characters.QUESTION_MARK +
          'challengeId=' +
          challengeId
        )

        if (response?.success) {
          applyAttendeesCountPayload(response.data)
          return
        }
      } catch (error) {
        console.error('Error loading attendee counts via websocket request:', error)
      }

      try {
        const response = await get<SuccessResponse<ChallengeAttendeesUsersResponse>>(
          URIS.CHALLENGES.BASE + URIS.CHALLENGES.USER_ATTENDEES + Characters.QUESTION_MARK + 'challengeId=' + challengeId
        )

        if (response?.success) {
          applyAttendeesCountPayload(response.data)
        }
      } catch (error) {
        console.error('Error loading attendee counts:', error)
      }
    }

    const publishAttendancePresence = (status: 'online' | 'offline') => {
      const challengeId = Number(props.challengeId)
      if (!challengeId) {
        return
      }

      websocketClient.sendUserStatus(status, challengeId)
    }

    const handleDashboardVisibilityChange = () => {
      if (document.visibilityState === 'hidden') {
        publishAttendancePresence('offline')
        return
      }

      publishAttendancePresence('online')
    }

    const handleDashboardBeforeUnload = () => {
      publishAttendancePresence('offline')
    }

    const toggleTimer = async () => {
      // Toggle the timer state between 'running', 'paused', and 'stopped'
      if (challengeStatus.value == ChallengeStatus.OPEN) {
        const response = await get<SuccessResponse<any>>(
          URIS.CHALLENGES.BASE +
          URIS.CHALLENGES.SEND_WELCOME_EMAIL +
          Characters.QUESTION_MARK +
          Fields.CHALLENGE_ID +
          Characters.EQUAL +
          props.challengeId
        )
        const data = response?.data
        console.log(data.message)
      }
      if (challengeStatus.value === ChallengeStatus.VOTING) {
        challengeStatus.value = ChallengeStatus.PAUSED
        await updateChallengeStatus(ChallengeStatus.PAUSED)
        await loadChallengeDashboardData(props.challengeId, ChallengeStatus.PAUSED)
      } else {
        challengeStatus.value = ChallengeStatus.VOTING
        challengeDashboardResponse.value.status = ChallengeStatus.VOTING
        await updateChallengeStatus(ChallengeStatus.VOTING)
        await loadChallengeDashboardData(props.challengeId, ChallengeStatus.VOTING)
      }
    }

    const broadcastChallengeStatus = async (status: ChallengeStatus) => {
      const challengeId = Number(props.challengeId)
      const published = await websocketClient.sendChallengeStatus(status, challengeId)

      if (!published) {
        await createWithActiveSocket<SuccessResponse<ChallengeStatusSocketPayload>>(
          URIS.WEB_SOCKET.CHALLENGES.BASE + URIS.WEB_SOCKET.CHALLENGES.STATUS_SYNC,
          {
            challengeId,
            status,
            sender: String(currentEmployee?.id || currentUserGrpNumber.value || '')
          }
        )
      }
    }

    const updateChallengeStatus = async (status: ChallengeStatus) => {
      try {
        const response = await update<SuccessResponse<ChallengesResponse>>(
          URIS.CHALLENGES.BASE +
          URIS.CHALLENGES.STATUS +
          Characters.QUESTION_MARK +
          Fields.CHALLENGE_ID +
          Characters.EQUAL +
          props.challengeId +
          Characters.AND +
          Fields.STATUS +
          Characters.EQUAL +
          status,
          null
        )
        console.log(response?.data)
        await broadcastChallengeStatus(status)
      } catch (error) {
        console.error('Error updating challenge status:', error)
        // Handle error scenarios as needed
      }
    }

    const submitAnswer = async () => {
      const isAnswerAllowed = currentRound.value === 1 || roundOver.value === false
      if (!isSolutionStage.value) {
        toast.warning(
          isLanguageAr()
            ? 'إضافة الحلول غير متاحة في هذه الجولة.'
            : 'Adding solutions is not available in this stage.'
        )
        return
      }

      if (newAnswer.value.trim() && isAnswerAllowed) {
        const answerPayload = {
          queLevel: currentRound.value,
          challengeId: props.challengeId,
          solutionText: newAnswer.value.trim()
        }

        try {
          // Define the API endpoint for submitting answers
          const submitAnswerEndpoint = URIS.SOLUTIONS.BASE
          // Use the create method to make the POST request
          const response = await create<any>(submitAnswerEndpoint, answerPayload)
          if (response.success) {
            if (stompsocket) {
              stompsocket.publish({
                destination: '/topic/solution',
                body: JSON.stringify({
                  statusCode: 'CREATED',
                  data: response.data
                })
              })
            }
            newAnswer.value = ''
            toast.success('تمت إضافة الحل الجديد بنجاح', {})
          }
        } catch (error) {
          // Handle error scenarios (display an error message, log, etc.)
          console.error('Error submitting answer:', error)
        }
      }
    }

    const showSolutionEditDialog = (solution: any) => {
      selectedSolution.value = solution
      modifySolution.value = true
    }
    const removeAnswer = (index: any) => {
      answers.value.splice(index, 1)
    }

    const checkTheCurrentUserIsVoted = async (solutionId: number) => {
      try {
        const isCurrentUserVoted = await get<SuccessResponse<any>>(
          URIS.VOTING.BASE + URIS.VOTING.CHECK_CURRENT_USER + solutionId
        )
        return Boolean(isCurrentUserVoted?.data)
      } catch (error) {
        console.error('هنالك خطأ في عملية تحديث:', error)
      }
    }

    const openVoteDialog = async (solution: SolutionFullResponse) => {
      dialogTitle.value = t('challenge.dashboard.votingConfirmation')
      dialogMsg.value = t('challenge.dashboard.confirmVoteMessage', {
        solutionText: solution.solutionText
      })
      solutionForDialog.value = solution
      isConfirmationDialogVisible.value = true
    }

    const handleConfirmation = async () => {
      try {
        const solution = solutionForDialog.value as SolutionFullResponse
        const isCurrentUserVoted = await checkTheCurrentUserIsVoted(solution.id)

        if (isCurrentUserVoted) {
          await handleRemoveVote(solution)
        } else {
          await handleVote(solution)
        }
      } catch (error) {
        console.error('هنالك خطأ في عملية تحديث:', error)
      } finally {
        await loadChallengeDashboardData(props.challengeId)
        isConfirmationDialogVisible.value = false
      }
    }

    const handleCloseConfirmation = () => {
      isConfirmationDialogVisible.value = false
      dialogTitle.value = ''
      dialogMsg.value = ''
      solutionForDialog.value = {}
    }

    // Handle vote removal
    const handleRemoveVote = async (solution: SolutionFullResponse) => {
      const removeVote = await deleteRequest<SuccessResponse<string>>(
        URIS.VOTING.BASE + URIS.VOTING.REMOVE_MY_VOTE + solution.id
      )

      if (removeVote?.success) {
        const solutionResponse = removeVote.data
        if (solutionResponse) {
          publishVoteStatus(solutionResponse)
        }
      } else {
        console.error('Failed to update solution for voting:', removeVote?.message)
      }
    }

    // Handle a new vote
    const handleVote = async (solution: SolutionFullResponse) => {
      const votingRequest = {
        queLevel: solution.queLevel,
        solutionId: solution.id,
        challengeId: props.challengeId
      }
      const votingResponse = await create<SuccessResponse<VotingResponse>>(URIS.VOTING.BASE, votingRequest)

      if (votingResponse?.success) {
        const solutionResponse = votingResponse.data?.solution as ChallengeSolutionResponse | undefined
        if (solutionResponse) {
          publishVoteStatus(solutionResponse)
        }
        toast.success('تم تحديث بنجاح', {})
      } else {
        console.error('Failed to update solution for voting:', votingResponse?.message)
      }
    }

    // Publish vote status
    const publishVoteStatus = (solutionResponse: string | ChallengeSolutionResponse | SolutionFullResponse) => {
      if (stompsocket) {
        stompsocket.publish({
          destination: '/topic/solution',
          body: JSON.stringify({
            statusCode: 'VOTED',
            data: solutionResponse
          })
        })
      }
    }

    const totalVoteCount = computed(() => {
      return answers.value.reduce((sum, solution) => sum + solution.voteCount, 0)
    })
    const subscribeToMessages = () => {
      if (stompsocket && !solutionSubscription.value) {
        solutionSubscription.value = stompsocket.subscribe('/topic/solution', (message: any) => {
          const messageObject = JSON.parse(message.body)
          if (challengeDashboardResponse.value.challengeSolutions && messageObject.data) {
            if (messageObject.statusCode === 'CREATED') {
              const existingSolutionIndex = challengeDashboardResponse.value.challengeSolutions.findIndex(
                (solution) => solution.id === messageObject.data.id
              )
              if (existingSolutionIndex === -1) {
                challengeDashboardResponse.value.challengeSolutions.push(messageObject.data)
              } else {
                challengeDashboardResponse.value.challengeSolutions[existingSolutionIndex] = messageObject.data
              }
              syncSolutionsState(challengeDashboardResponse.value.challengeSolutions)
              scrollSolutionsToBottom()
            } else if (messageObject.statusCode === 'VOTED') {
              const index = challengeDashboardResponse.value.challengeSolutions.findIndex(
                (solution) => solution.id === messageObject.data.id
              )
              if (index !== -1) {
                challengeDashboardResponse.value.challengeSolutions[index] = {
                  ...challengeDashboardResponse.value.challengeSolutions[index],
                  ...messageObject.data,
                  isCurrentUserVoted: challengeDashboardResponse.value.challengeSolutions[index].isCurrentUserVoted
                }
                syncSolutionsState(challengeDashboardResponse.value.challengeSolutions)
              }
            }
          }
        })
      }
    }

    const subscribeToChallengeStatus = () => {
      if (statusSubscription.value) {
        return
      }

      statusSubscription.value =
        websocketClient.getStompClient()?.subscribe('/topic/challenge-status', (message: IMessage) => {
          const payload = JSON.parse(message.body) as ChallengeStatusSocketPayload
          if (Number(payload.challengeId) !== Number(props.challengeId) || !payload.status) {
            return
          }

          challengeStatus.value = payload.status
          challengeDashboardResponse.value.status = payload.status
        }) || null
    }

    const subscribeToAttendeeCounts = () => {
      if (attendeesCountSubscription.value) {
        return
      }

      attendeesCountSubscription.value =
        websocketClient.getStompClient()?.subscribe('/topic/public', (message: IMessage) => {
          const payload = JSON.parse(message.body) as ChallengeAttendeesSocketPayload
          applyAttendeesCountPayload(payload)
        }) || null
    }

    const handleSocketConnected = () => {
      publishAttendancePresence('online')
      subscribeToAttendeeCounts()
      loadAttendeesCounts()
      subscribeToMessages()
      subscribeToChallengeStatus()
    }

    onMounted(async () => {
      document.addEventListener('visibilitychange', handleDashboardVisibilityChange)
      window.addEventListener('beforeunload', handleDashboardBeforeUnload)
      window.addEventListener('pagehide', handleDashboardBeforeUnload)

      if (props.challengeId) {
        publishAttendancePresence('online')
        await loadAttendeesCounts()
        await loadChallengeDashboardData(props.challengeId)
      }
      if (websocketClient.isConnected()) {
        publishAttendancePresence('online')
        subscribeToAttendeeCounts()
        subscribeToMessages()
        subscribeToChallengeStatus()
      }
      websocketClient.setOnConnectCallback(handleSocketConnected)
    })

    onBeforeUnmount(() => {
      stopCountdownTimer()
      publishAttendancePresence('offline')
      websocketClient.removeOnConnectCallback(handleSocketConnected)
      solutionSubscription.value?.unsubscribe()
      statusSubscription.value?.unsubscribe()
      attendeesCountSubscription.value?.unsubscribe()
      document.removeEventListener('visibilitychange', handleDashboardVisibilityChange)
      window.removeEventListener('beforeunload', handleDashboardBeforeUnload)
      window.removeEventListener('pagehide', handleDashboardBeforeUnload)
    })

    const isLanguageAr = (): boolean => {
      return employeeLang === 'ar'
    }

    const openRatingPopup = async (solutionId: number) => {
      const response = await get<SuccessResponse<SurveyForSolutionResponse>>(
        URIS.SURVEY.BASE + URIS.SURVEY.BY_SOLUTION + Characters.QUESTION_MARK + 'solutionId=' + solutionId
      )
      if (response?.success) {
        const surveyResponse = response.data
        console.log(surveyResponse)
        if (surveyResponse)
          ratingInitialData.value = {
            rating: surveyResponse.rating,
            isApplicable: surveyResponse.applicableToApply,
            feedback: surveyResponse.comments,
            applicationInstructions: surveyResponse.howToApply
          }
      }
      selectedSolutionIdForRating.value = solutionId
      isRatingPopupOpen.value = true
    }

    const openModeratorRatingListPopup = async (solution: ChallengeSolutionResponse) => {
      solutionForModeratorRateView.value = solution as ChallengeSolutionResponse
      const response = await get<SuccessResponse<SurveyForSolutionResponse[]>>(
        URIS.SURVEY.BASE + URIS.SURVEY.ALL + Characters.QUESTION_MARK + 'solutionId=' + solution.id
      )
      if (response?.success) {
        const surveyResponse = response.data as SurveyForSolutionResponse[]
        console.log(surveyResponse)
        if (surveyResponse) ratingSolutionList.value = surveyResponse as SurveyForSolutionResponse[]
      }
      isRatingFullSolutionPopupDetailsOpen.value = true
    }

    const handleRatingSubmit = async (requestData: SurveyCreationRequest) => {
      try {
        const response = await create<any>(URIS.SURVEY.BASE, requestData)
        if (response) {
          // Display success message
          toast.success(t('Messages.success.user.created'))
        }
      } catch (error: any) {
        toast.error(error.message)
      }
    }

    const handleOutsideClick = () => {
      modifySolution.value = false
    }

    const sortedSolutions = computed(() => {
      if (!isTheChallengeModerator.value) {
        return answers.value.slice().sort((a, b) => {
          if (a.isCurrentUserVoted === b.isCurrentUserVoted) {
            return 0
          }

          return a.isCurrentUserVoted ? -1 : 1
        })
      }

      return answers.value.slice().sort((a, b) => b.voteCount - a.voteCount).map((solution, index) => {
        return {
          ...solution,
          voteColor: solution.voteCount > 0 ? MODERATOR_VOTE_COLORS[index] || '#FFF8E1' : 'white'
        }
      })
    })

    const handleUpdateIsOpenForAllRateDetails = (value: boolean) => {
      isRatingFullSolutionPopupDetailsOpen.value = value
    }

    const handleClosePopupAllRateDetails = () => {
      isRatingFullSolutionPopupDetailsOpen.value = false
    }

    const openFinalSolutionsDialog = () => {
      isFinalSolutionsDialogOpen.value = true
    }

    const handleUpdateAnswersAfterMergeSolutions = async () => {
      await loadChallengeDashboardData(props.challengeId)
    }

    const handleCloseRatingPopup = () => {
      isRatingPopupOpen.value = false
      ratingInitialData.value = {
        rating: 0,
        isApplicable: false,
        feedback: '',
        applicationInstructions: ''
      }
    }

    const toggleAttendeesPanel = () => {
      if (!canToggleAttendees.value) {
        return
      }
      showAttendees.value = !showAttendees.value
    }

    return {
      isLanguageAr,
      answers,
      solutionsList,
      scrollSolutionsToBottom,
      canVote,
      showSolutionEditDialog,
      selectedSolution,
      modifySolution,
      finishRound,
      newAnswer,
      handleCloseRatingPopup,
      canAnswer,
      challengeDashboardResponse,
      countdown,
      openVoteDialog,
      totalVoteCount,
      submitAnswer,
      removeAnswer,
      searchQuery,
      internalSearchResults,
      searchType,
      setSearchType,
      goToVideo,
      socialMediaPosts,
      currentRound,
      voteResults,
      finalResults,
      roundOver,
      timerButtonLabel,
      toggleTimer,
      timerState,
      challengeStatus,
      titleBasedOnStatus,
      challengeTitleDisplay,
      challengeStatusChipStyle,
      challengeStatusIcon,
      normalizedChallengeStatus,
      isSolutionStage,
      isVotingStage,
      isPausedStage,
      isClosedStage,
      isVotingBoardVisible,
      participantStatusTitle,
      participantStatusDescription,
      isAdminOrModerator,
      isModeratorOnly,
      isTheChallengeModerator,
      isHasAnyRole,
      isRatingPopupOpen,
      handleRatingSubmit,
      openRatingPopup,
      ratingInitialData,
      selectedSolutionIdForRating,
      sortedSolutions,
      startTimeDubai,
      endTimeDubai,
      currentDubaiTime,
      handleOutsideClick,
      initialSearchType,
      initialSearchQuery,
      initialSearchResults,
      dataLoaded,
      isFinalSolutionsDialogOpen,
      openFinalSolutionsDialog,
      finalSolutions,
      ratingSolutionList,
      openModeratorRatingListPopup,
      isRatingFullSolutionPopupDetailsOpen,
      handleUpdateIsOpenForAllRateDetails,
      handleClosePopupAllRateDetails,
      solutionForModeratorRateView,
      handleUpdateAnswersAfterMergeSolutions,
      isConfirmationDialogVisible,
      dialogTitle,
      dialogMsg,
      handleConfirmation,
      handleCloseConfirmation,
      currentUserGrpNumber,
      currentEmployee,
      stepperSteps,
      currentStep,
      isFinalStage,
      canViewFinalSolutionsButton,
      onlineCount,
      offlineCount,
      showAttendees,
      canToggleAttendees,
      toggleAttendeesPanel
    }
  },
  computed: {
    ChallengeDashboardSearchType() {
      return ChallengeDashboardSearchType
    }
  },
  watch: {
    'challengeDashboardResponse.attendees': {
      immediate: true,
      handler(attendees) {
        if (attendees) {
          this.onlineCount = attendees.filter((a) => a.isOnline).length
          this.offlineCount = attendees.filter((a) => !a.isOnline).length
        }
      }
    }
  },
  methods: {
    removeSolutionFromList(solutionId: string) {
      const index = this.challengeDashboardResponse.challengeSolutions.findIndex(
        (solution: any) => Number(solution.id) === Number(solutionId)
      )
      if (index !== -1) {
        this.challengeDashboardResponse.challengeSolutions.splice(index, 1)
        const activeQueueLevel = this.challengeDashboardResponse.queLevel === 0 ? 1 : this.challengeDashboardResponse.queLevel
        this.answers = this.challengeDashboardResponse.challengeSolutions.filter(
          (solution: any) => solution.queLevel === activeQueueLevel
        )
      }
      this.modifySolution = false
    }
  }
})
</script>

<template src="./ChallengeDashboard.html" v-if="isHasAnyRole"></template>
<style src="./ChallengeDashboard.scss" lang="scss"></style>
