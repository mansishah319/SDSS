<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { computed, onBeforeMount, ref } from 'vue'
import { SdssRoundStatus, sdssRoundStatusFromString } from '@/enums/SdssRoundStatus.ts'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { create, deleteRequest, enhancedUpdate, get, update } from '@/utils/GenericApiHelper.ts'
import { Characters, Fields, URIS } from '@/constants/DpConstants.ts'
import {
  SdssSurveyByIdResponse,
  SurveyInvitedUserEmailStatusResponse
} from '@/dto/surveys/survey/SdssSurveyByIdResponse.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { SdssSurveyRoundResponse } from '@/dto/surveys/round/SdssSurveyRoundResponse.ts'
import CreateSingleRound from '@/views/surveys/round/create/CreateSingleRound.vue'
import { toast } from 'vue3-toastify'
import { SdssSurveyRoundRequest } from '@/dto/surveys/round/SdssSurveyRoundRequest.ts'
import { AxiosResponse } from 'axios'
import { useSidebarStore } from '@/store/sidebar.ts'
import CustomGoBackButton from '@/components/custom/CustomGoBackButton.vue'
import AppActivityIndicator from '@/components/common/AppActivityIndicator.vue'
import AppDialogShell from '@/components/common/AppDialogShell.vue'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'

interface SurveyInvitationEmailSendResultResponse {
  employeeGrpNumber?: number
  fullName?: string
  emailAddress?: string
  sent?: boolean
  message?: string
  reason?: string
}

interface SurveyInvitationEmailSendSummaryResponse {
  totalCount?: number
  successCount?: number
  failedCount?: number
  allSent?: boolean
  message?: string
  reason?: string
  results?: SurveyInvitationEmailSendResultResponse[]
}

export default {
  name: 'SdssSurveyView',
  components: { AppActivityIndicator, CustomGoBackButton, AppDialogShell, ConfirmationDialog, CreateSingleRound },
  setup() {
    const { t, locale } = useI18n()
    const router = useRouter()
    const sidebarStore = useSidebarStore()
    const surveyId = router.currentRoute.value.params.surveyId

    // Reactive Data Properties
    const isCreateRoundDialogOpen = ref(false)
    const isUpdateRoundDialogOpen = ref(false)
    const roundIdToDelete = ref<number>(0)
    const deleteRoundDialog = ref<boolean>(false)
    const survey = ref<SdssSurveyByIdResponse>({} as SdssSurveyByIdResponse)
    const selectedRoundForUpdate = ref<SdssSurveyRoundResponse>({} as SdssSurveyRoundResponse)
    const invitedUsersSearch = ref('')
    const invitedUsersLoading = ref(false)
    const resendAllLoading = ref(false)
    const resendLoadingByGrp = ref<Record<number, boolean>>({})
    const employeeLang: string = (window.localStorage.getItem(Fields.EMPLOYEE_LANG) ?? locale.value) || 'ar'

    const invitedUsersHeaders = computed(() => [
      { title: t('common.grpNumber'), key: 'employeeGrpNumber', value: 'employeeGrpNumber', sortable: true },
      { title: t('common.fullName'), key: 'fullName', value: 'fullName', sortable: true },
      { title: t('common.department'), key: 'departmentName', value: 'departmentName', sortable: true },
      { title: t('sdssSurvey.selectJobs'), key: 'jobName', value: 'jobName', sortable: true },
      { title: t('user.search.email'), key: 'emailAddress', value: 'emailAddress', sortable: true },
      { title: t('common.emailSentStatusTitle'), key: 'sent', value: 'sent', sortable: true, align: 'center' },
      { title: t('common.actions'), key: 'actions', value: 'actions', sortable: false, align: 'center' }
    ])

    const invitedUsers = computed(() => survey.value.invitedUsers || [])
    const missedInvitationsCount = computed(
      () => invitedUsers.value.filter(user => !user.sent).length
    )

    const isLanguageAr = (): boolean => {
      return employeeLang === 'ar'
    }

    const resolveInvitationEmailMessage = (
      result?: SurveyInvitationEmailSendResultResponse | SurveyInvitationEmailSendSummaryResponse
    ): string => {
      // const message = result?.message
      // const reason = result?.reason
      //
      // return [message, reason].filter(Boolean).join(' ')
      return result?.message || ''
    }

    const isRtl = ref(locale.value === 'ar')

    // Methods
    const loadSurveyData = async () => {
      invitedUsersLoading.value = true
      await get<SuccessResponse<SdssSurveyByIdResponse>>(URIS.SURVEYS.SURVEY.BASE + '?surveyId=' + surveyId).then(
        (res: SuccessResponse<any>) => {
          if (res.success) {
            survey.value = res.data as SdssSurveyByIdResponse
          }
        }
      ).finally(() => {
        invitedUsersLoading.value = false
      })
    }

    const resendInvitationEmail = async (user: SurveyInvitedUserEmailStatusResponse) => {
      resendLoadingByGrp.value = {
        ...resendLoadingByGrp.value,
        [user.employeeGrpNumber]: true
      }
      try {
        await update<SuccessResponse<SurveyInvitationEmailSendResultResponse>>(
          URIS.SURVEYS.SURVEY.BASE +
          URIS.SURVEYS.SURVEY.RESEND_INVITATION_EMAIL +
          Characters.QUESTION_MARK +
          Fields.SURVEY_ID +
          Characters.EQUAL +
          surveyId +
          Characters.AND +
          Fields.EMPLOYEE_GRP_NUMBER +
          Characters.EQUAL +
          user.employeeGrpNumber,
          null
        ).then(async response => {
          if (response?.success) {
            const notificationMessage = resolveInvitationEmailMessage(response.data) || response.message
            if (response.data?.sent) {
              toast.success(notificationMessage || 'Email sent successfully')
            } else {
              toast.error(notificationMessage || 'Email was not sent')
            }
            await loadSurveyData()
          } else {
            toast.error(response?.message ?? 'An unexpected error occurred')
          }
        })
      } finally {
        resendLoadingByGrp.value = {
          ...resendLoadingByGrp.value,
          [user.employeeGrpNumber]: false
        }
      }
    }

    const resendMissingInvitationEmails = async () => {
      resendAllLoading.value = true
      try {
        await update<SuccessResponse<SurveyInvitationEmailSendSummaryResponse>>(
          URIS.SURVEYS.SURVEY.BASE +
          URIS.SURVEYS.SURVEY.RESEND_MISSING_INVITATION_EMAILS +
          Characters.QUESTION_MARK +
          Fields.SURVEY_ID +
          Characters.EQUAL +
          surveyId,
          null
        ).then(async response => {
          if (response?.success) {
            const notificationMessage = resolveInvitationEmailMessage(response.data) || response.message
            if (response.data?.allSent) {
              toast.success(notificationMessage || 'Emails sent successfully')
            } else if ((response.data?.successCount ?? 0) > 0) {
              toast.warning(notificationMessage || 'Some emails were not sent')
            } else {
              toast.error(notificationMessage || 'Emails were not sent')
            }
            await loadSurveyData()
          } else {
            toast.error(response?.message ?? 'An unexpected error occurred')
          }
        })
      } finally {
        resendAllLoading.value = false
      }
    }

    const viewDetails = async (round: SdssSurveyRoundResponse) => {
      const roundId = round.id
      const targetRoute = {
        name: 'SurveyRoundView',
        params: { surveyId, roundId }
      }

      try {
        await router.push(targetRoute)
      } catch (error) {
        console.error('Failed to navigate to survey round details:', error)
        window.location.assign(router.resolve(targetRoute).href)
      }
    }

    const openCreateNewRound = () => {
      isCreateRoundDialogOpen.value = true
    }

    const publishRoundIfStatusPending = async (round: SdssSurveyRoundResponse) => {
      await enhancedUpdate<SuccessResponse<SdssSurveyByIdResponse>>(
        URIS.SURVEYS.SURVEY.ACTIVE_ROUND_SURVEY +
        Characters.QUESTION_MARK +
        Fields.SURVEY_ID +
        Characters.EQUAL +
        surveyId +
        Characters.AND +
        Fields.ROUND_NUMBER +
        Characters.EQUAL +
        round.roundNumber,
        null
      ).then((response: AxiosResponse<SuccessResponse<SdssSurveyByIdResponse>, any> | null) => {
        if (response && response.data.success) {
          console.log('Round published successfully')
          toast.success(t('Messages.success.sdssSurveyRound.published'))
          loadSurveyData()
        } else if (response) toast.error(response.data.message)
      })
    }

    const publishRoundIfStatusInProgress = async (round: SdssSurveyRoundResponse) => {
      await enhancedUpdate<SuccessResponse<SdssSurveyByIdResponse>>(
        URIS.SURVEYS.SURVEY.COMPLETED_ROUND_SURVEY +
        Characters.QUESTION_MARK +
        Fields.SURVEY_ID +
        Characters.EQUAL +
        surveyId +
        Characters.AND +
        Fields.ROUND_NUMBER +
        Characters.EQUAL +
        round.roundNumber,
        null
      ).then((response: AxiosResponse<SuccessResponse<SdssSurveyByIdResponse>, any> | null) => {
        if (response && response.data.success) {
          console.log('Round published successfully')
          toast.success(t('Messages.success.sdssSurveyRound.published'))
          loadSurveyData()
        } else if (response) {
          toast.error(response.data.message)
        }
      })
    }

    const openUpdateRoundScreen = (round: SdssSurveyRoundResponse) => {
      selectedRoundForUpdate.value = round
      isUpdateRoundDialogOpen.value = true
    }

    const openDeleteDialog = (id: number) => {
      roundIdToDelete.value = id
      deleteRoundDialog.value = true
    }

    const cancelRoundDeletion = () => {
      deleteRoundDialog.value = false
    }

    const confirmRoundDeletion = async () => {
      await deleteRequest(
        URIS.SURVEYS.ROUND.BASE +
        Characters.QUESTION_MARK +
        Fields.SURVEY_ROUND_ID +
        Characters.EQUAL +
        roundIdToDelete.value
      ).then(async (res: SuccessResponse<string>) => {
        if (res.success) {
          deleteRoundDialog.value = false
          console.log('deleted successfully.')
          toast.success(t('Messages.success.sdssSurvey.deleted'))
          await loadSurveyData()
        } else {
          toast.error(res.message)
        }
      })
    }

    const editSurvey = () => {
      // Logic to edit the survey
      alert(`Editing survey: ${survey.value.titleEn}`)
    }

    onBeforeMount(async () => {
      sidebarStore.show()
      await loadSurveyData()
    })

    const submitCreateRound = async (data: SdssSurveyRoundRequest) => {
      try {
        const creationResponse = (await create<SuccessResponse<SdssSurveyRoundResponse>>(
          URIS.SURVEYS.ROUND.BASE + '?surveyId=' + surveyId,
          data
        )) as SuccessResponse<SdssSurveyRoundResponse>
        if (creationResponse.success) {
          toast.success(t('Messages.success.sdssSurveyRound.created'))
          await closeCreateRoundDialog()
        } else {
          console.log('There is an issue with Survey Round Creation')
        }
      } catch (error: any) {
        // Assuming the error response has a message property
        console.log(error)
      }
    }

    const submitUpdateRound = async (data: SdssSurveyRoundRequest) => {
      try {
        const creationResponse = (await update<SuccessResponse<SdssSurveyRoundResponse>>(
          URIS.SURVEYS.ROUND.BASE + '?surveyId=' + surveyId + '&surveyRoundId=' + selectedRoundForUpdate.value.id,
          data
        )) as SuccessResponse<SdssSurveyRoundResponse>
        if (creationResponse.success) {
          toast.success(t('Messages.success.sdssSurveyRound.updated'))
          await closeUpdateRoundDialog()
        } else {
          console.log('There is an issue with update the SurveyRound')
        }
      } catch (error: any) {
        // Assuming the error response has a message property
        console.log(error)
      }
    }

    const closeCreateRoundDialog = async () => {
      isCreateRoundDialogOpen.value = false
      await loadSurveyData()
    }

    const closeUpdateRoundDialog = async () => {
      isUpdateRoundDialogOpen.value = false
      await loadSurveyData()
    }

    const isModeratorAndAllowedToUpdateOrDelete = (round: SdssSurveyRoundResponse) => {
      return round.currentUserIsTheSurveyModerator && round.isAllowedToUpdateOrDelete
    }

    const getRoundActivityColor = (localStatus: string) => {
      switch (localStatus) {
        case 'PENDING':
          return 'orange'
        case 'IN_PROGRESS':
          return 'white'
        case 'COMPLETED':
          return 'green'
        default:
          return 'gray'
      }
    }

    return {
      isRtl,
      survey,
      viewDetails,
      getRoundActivityColor,
      openDeleteDialog,
      editSurvey,
      openUpdateRoundScreen,
      publishRoundIfStatusPending,
      openCreateNewRound,
      isCreateRoundDialogOpen,
      submitCreateRound,
      closeCreateRoundDialog,
      confirmRoundDeletion,
      cancelRoundDeletion,
      deleteRoundDialog,
      isUpdateRoundDialogOpen,
      closeUpdateRoundDialog,
      selectedRoundForUpdate,
      submitUpdateRound,
      isModeratorAndAllowedToUpdateOrDelete,
      publishRoundIfStatusInProgress,
      isLanguageAr,
      invitedUsers,
      invitedUsersHeaders,
      invitedUsersLoading,
      invitedUsersSearch,
      missedInvitationsCount,
      resendAllLoading,
      resendLoadingByGrp,
      resendInvitationEmail,
      resendMissingInvitationEmails
    }
  },
  computed: {
    SdssRoundStatus() {
      return SdssRoundStatus
    }
  },
  methods: {
    sdssRoundStatusFromString,
    icon(status: string) {
      switch (status) {
        case 'PENDING':
          return 'mdi-clock-outline' // Icon for Pending
        case 'IN_PROGRESS':
          return 'mdi-cast-audio-variant' // Icon for Published
        case 'COMPLETED':
          return 'mdi-check-circle' // Icon for Completed
        default:
          return 'mdi-help-circle' // Fallback icon
      }
    },
    iconColor(status: string) {
      switch (status) {
        case 'PENDING':
          return 'warning' // Color for Pending
        case 'IN_PROGRESS':
          return 'white' // Color for Published
        case 'COMPLETED':
          return 'success' // Color for Completed
        default:
          return 'grey' // Fallback color
      }
    }
  }
}
</script>

<template src="./SdssSurveyView.html"></template>
<style src="./SdssSurveyView.scss" scoped></style>
