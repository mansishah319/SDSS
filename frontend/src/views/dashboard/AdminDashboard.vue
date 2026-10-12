<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { format } from 'date-fns'
import { get, update } from '@/utils/GenericApiHelper'
import { Characters, Fields, URIS } from '@/constants/DpConstants'
import { useI18n } from 'vue-i18n'
import DialogDashboardScreen from '@/views/dashboard/dialog/DialogMainDashboard.vue'
import { useRouter } from 'vue-router'
import { ChallengeStatus } from '@/dto/challenge/ChallengeStatus'
import { onMounted, ref } from 'vue'
import { toast } from 'vue3-toastify'
import { useSidebarStore } from '@/store/sidebar.ts'
import DPDatePicker from '@/components/date/DPDatePicker.vue'
import {
  AdminDashboardResponse,
  Challenge,
  DepartmentChallengeStatsResponse,
  User
} from '@/dto/dashboard/AdminDashboardResponse.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import DepartmentDashboard from '@/views/dashboard/department/DepartmentDashboard.vue'
import UserInvitationsDashboardCard from '@/views/dashboard/userInvitations/UserInvitationsDashboardCard.vue'
import ChallengeStrategicDashboardChart from '@/views/dashboard/charts/ChallengeStrategicDashboardChart.vue'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import ContentLoader from '@/components/loader/ContentLoader.vue'

interface DialogItemResponse {
  head1: any
  head2: any
  head3: any
}

type StatusKeys = 'Completed' | 'Open' | 'Closed' | 'Voting' | 'Revised' | 'Paused' | 'UnderReview' | 'New' | 'Urgent'

export default {
  components: {
    DialogDashboardScreen,
    DepartmentDashboard,
    UserInvitationsDashboardCard,
    ChallengeStrategicDashboardChart,
    DPDatePicker,
    GenericDashboardHeaderView,
    ContentLoader
  },
  data() {
    const { t, locale } = useI18n()
    const router = useRouter()
    const sidebarStore = useSidebarStore()

    const dialog2 = ref(false)
    const selectedItemId = ref(0)
    const popupType = ref<ChallengeStatus>(ChallengeStatus.OPEN)
    const dialogTitle = ref('')
    const listDialogTitle = ref('')
    const dialogDescription = ref('')
    const employeeLang: string = localStorage.getItem(Fields.EMPLOYEE_LANG) || locale.value || 'ar'
    const isLoading = ref<boolean>(true)

    const theLabels = [
      t('common.labels.completed'),
      t('common.labels.open'),
      t('common.labels.closed'),
      t('common.labels.voting'),
      t('common.labels.revised'),
      t('common.labels.paused'),
      t('common.labels.underReview'),
      t('common.labels.new'),
      t('common.labels.urgent')
    ]

    const challengeDialogHeaders = [
      t('adminDashboard.dialog.challenge.id'),
      t('adminDashboard.dialog.challenge.title'),
      t('adminDashboard.dialog.challenge.description')
    ]
    const internalUserDialogHeaders = [
      t('adminDashboard.dialog.users.grpNumber'),
      t('adminDashboard.dialog.users.name'),
      t('adminDashboard.dialog.users.email')
    ]
    const internalUsersLabels = [
      t('adminDashboard.dialog.users.dialog.deleted'),
      t('adminDashboard.dialog.users.dialog.active'),
      t('adminDashboard.dialog.users.dialog.notActive')
    ]
    const userTypesLabels = [
      t('adminDashboard.dialog.users.dialog.external'),
      t('adminDashboard.dialog.users.dialog.internal')
    ]

    const approveChallengeUnderReview = (request: DialogItemResponse) => {
      dialogTitle.value = t('adminRequests.approvedDialogTitle')
      dialogDescription.value = t('adminRequests.approvedDialogDescription')
      handleOpenDialogForConfirmation(request.head1, ChallengeStatus.COMPLETED)
    }

    const rejectChallengeUnderReview = (request: DialogItemResponse) => {
      dialogTitle.value = t('adminRequests.closedDialogTitle')
      dialogDescription.value = t('adminRequests.closedDialogDescription')
      handleOpenDialogForConfirmation(request.head1, ChallengeStatus.CLOSED)
    }

    const handleOpenDialogForConfirmation = (requestId: number, thePopupType: ChallengeStatus) => {
      selectedItemId.value = requestId
      dialog2.value = true
      popupType.value = thePopupType
    }

    const handleRowClick = (item: any) => {
      const challengeId = item.id
      router.push({ name: 'ChallengeView', params: { challengeId } })
    }

    const viewChallengeUnderReview = (request: DialogItemResponse) => {
      const challengeId = request.head1
      router.push({ name: 'ChallengeView', params: { challengeId } })
    }

    const performAction = () => {
      if (popupType.value === ChallengeStatus.COMPLETED) {
        updateChallengeStatusToCompleted(selectedItemId.value)
      }
      if (popupType.value === ChallengeStatus.CLOSED) {
        updateChallengeStatusToClosed(selectedItemId.value)
      }
      selectedItemId.value = 0
      dialog2.value = false
    }

    const updateChallengeStatusToCompleted = async (challengeId: number) => {
      await updateChallengeStatus(challengeId, ChallengeStatus.COMPLETED)
    }

    const updateChallengeStatusToClosed = async (challengeId: number) => {
      await updateChallengeStatus(challengeId, ChallengeStatus.CLOSED)
    }

    const updateChallengeStatus = async (challengeId: number, status: ChallengeStatus) => {
      await update(
        `${URIS.CHALLENGES.BASE}${URIS.CHALLENGES.STATUS}${Characters.QUESTION_MARK}${Fields.CHALLENGE_ID}${Characters.EQUAL}${challengeId}${Characters.AND}${Fields.STATUS}${Characters.EQUAL}${status}`,
        null
      ).then(async (response: any) => {
        if (response.success) {
          toast.success(t('Messages.success.challenge.updated'))
          this.challengeDialog(false)
          await this.fetchData()
        } else toast.success(t(response.message))
      })
    }

    const isLanguageAr = (): boolean => {
      return employeeLang === 'ar'
    }

    onMounted(() => {
      sidebarStore.disable()
    })

    return {
      translate: t,
      dialog2,
      dialogTitle,
      listDialogTitle,
      dialogDescription,
      isLoading,
      isLanguageAr,
      performAction,
      approveChallengeUnderReview,
      rejectChallengeUnderReview,
      viewChallengeUnderReview,
      handleRowClick,
      challengeDialogHeaders: challengeDialogHeaders,
      internalUserDialogHeaders: internalUserDialogHeaders,

      completedChallengesList: [] as Challenge[],
      openChallengesList: [] as Challenge[],
      closedChallengesList: [] as Challenge[],
      votingChallengesList: [] as Challenge[],
      revisedChallengesList: [] as Challenge[],
      pausedChallengesList: [] as Challenge[],
      underReviewChallengesList: [] as Challenge[],
      newChallengesList: [] as Challenge[],
      urgentChallengesList: [] as Challenge[],
      challengesResponseList: [] as Challenge[],

      activeUsersList: [] as User[],
      deletedUsersList: [] as User[],
      notActiveUsersList: [] as User[],

      externalUsersList: [] as User[],
      internalUsersList: [] as User[],
      appearAllActions: false,
      appearViewActions: false,

      series: [0, 0, 0, 0, 0, 0, 0, 0, 0],
      totalChallenges: 0,
      totalInternalUsers: 0,
      totalUsers: 0,
      chartOptions: {
        chart: {
          type: 'donut',
          toolbar: {
            show: false
          }
        },
        plotOptions: {
          pie: {
            donut: {
              size: '70%',
              labels: {
                show: true,
                total: {
                  show: true,
                  formatter: function(w: any) {
                    return w.globals.seriesTotals.reduce((a: any, b: any) => {
                      return a + b
                    }, 0)
                  }
                }
              }
            }
          }
        },
        stroke: {
          width: 2,
          colors: ['#ffffff']
        },
        dataLabels: {
          enabled: true
        },
        fill: {
          colors: ['#008755', '#1570EF', '#D92D20', '#AE1368', '#B35F00', '#4E5BA6', '#E31B54', '#56bd9a', '#C54D01']
        },
        labels: theLabels,
        colors: ['#008755', '#1570EF', '#D92D20', '#AE1368', '#B35F00', '#4E5BA6', '#E31B54', '#56bd9a', '#C54D01'],
        legend: {
          position: 'right',
          horizontalAlign: 'center',
          fontSize: '13px',
          itemMargin: {
            horizontal: 8,
            vertical: 6
          }
        },
        responsive: [
          {
            breakpoint: 960,
            options: {
              legend: {
                position: 'bottom'
              }
            }
          }
        ]
      },

      internalUsersSeries: [0, 0, 0],
      chartOptions2: {
        chart: {
          type: 'donut',
          toolbar: {
            show: false
          }
        },

        plotOptions: {
          pie: {
            donut: {
              size: '70%',
              labels: {
                show: true,
                total: {
                  show: true,
                  formatter: function(w: any) {
                    return w.globals.seriesTotals.reduce((a: any, b: any) => {
                      return a + b
                    }, 0)
                  }
                }
              }
            }
          }
        },
        stroke: {
          width: 2,
          colors: ['#ffffff']
        },
        dataLabels: {
          enabled: true
        },
        fill: {
          colors: ['#D92D20', '#008755', '#1570EF']
        },
        labels: internalUsersLabels,
        colors: ['#D92D20', '#008755', '#1570EF'],
        legend: {
          position: 'right',
          horizontalAlign: 'center',
          fontSize: '13px',
          itemMargin: {
            horizontal: 8,
            vertical: 6
          }
        },
        responsive: [
          {
            breakpoint: 960,
            options: {
              legend: {
                position: 'bottom'
              }
            }
          }
        ]
      },

      allUsersTypesSeries: [0, 0],
      chartOptions3: {
        chart: {
          type: 'donut',
          toolbar: {
            show: false
          }
        },

        plotOptions: {
          pie: {
            donut: {
              size: '70%',
              labels: {
                show: true,
                total: {
                  show: true,
                  formatter: function(w: any) {
                    return w.globals.seriesTotals.reduce((a: any, b: any) => {
                      return a + b
                    }, 0)
                  }
                }
              }
            }
          }
        },
        stroke: {
          width: 2,
          colors: ['#ffffff']
        },
        dataLabels: {
          enabled: true
        },
        fill: {
          colors: ['#D92D20', '#008755']
        },
        labels: userTypesLabels,
        colors: ['#D92D20', '#008755'],
        legend: {
          position: 'right',
          horizontalAlign: 'center',
          fontSize: '13px',
          itemMargin: {
            horizontal: 8,
            vertical: 6
          }
        },
        responsive: [
          {
            breakpoint: 960,
            options: {
              legend: {
                position: 'bottom'
              }
            }
          }
        ]
      },

      recentOpenPercentage: 0,

      decisionType: '',
      departmentChallengeStatsResponseList: [] as DepartmentChallengeStatsResponse[],

      completedDecision: 0,
      completedDecisionValue: 0,
      completedDecisionList: [] as Challenge[],

      mainDashboardFullResponse: {} as AdminDashboardResponse,

      validatorRevisedDecision: 0,
      validatorRevisedDecisionValue: 0,
      validatorRevisedDecisionList: [] as Challenge[],

      closedDecision: 0,
      closedDecisionValue: 0,
      closedDecisionList: [] as Challenge[],

      statusColors: {
        Completed: '#27ae60', // Green
        Open: '#3498db', // Blue
        Closed: '#e74c3c', // Red
        Voting: '#be09a6', // Purple
        Revised: '#8c591d', // Yellow
        Paused: '#120c7e', // Dark Red
        UnderReview: '#c24671', // Emerald Green
        New: '#6ceca3', // Orange
        Urgent: '#5e340e' // Dark Orange
      } as Record<StatusKeys, string>,

      dialogHeaders: [] as string[],
      dialogItems: [] as DialogItemResponse[],
      challengeList: [] as Challenge[],
      selectedStatus: '',
      selectedDate: '',
      dialog: false,
      userDialog: false,
      userStatusDialog: false
    }
  },
  computed: {
    primaryCardsColumnSpan() {
      return this.mainDashboardFullResponse.isCommander ? 4 : 6
    },
    secondaryCardsColumnSpan() {
      const visibleCards = [
        this.hasUserInvitationsData,
        this.mainDashboardFullResponse.isModeratorOrAdminOrCommander,
        this.mainDashboardFullResponse.isModeratorOrAdminOrCommander
      ].filter(Boolean).length

      if (visibleCards <= 1) {
        return 12
      }

      if (visibleCards === 2) {
        return 6
      }

      return 4
    },
    filteredStatusList() {
      // Filter the statusList based on selectedStatus and selectedDate
      return this.challengeList.filter((item: any) => {
        // Check if item matches the selected status
        const statusMatch = !this.selectedStatus || item.statusLocal === this.selectedStatus

        // Check if item matches the selected date
        let dateMatch = true
        if (this.selectedDate) {
          try {
            // DPDatePicker returns date in 'DD/MM/YYYY' format
            const selectedDateFormatted = format(
              new Date(this.selectedDate.split('/').reverse().join('-')),
              'dd/MM/yyyy'
            )
            const itemDateFormatted = format(new Date(item.createdAt), 'dd/MM/yyyy')
            dateMatch = selectedDateFormatted === itemDateFormatted
          } catch (error) {
            console.error('Error parsing date:', error)
            dateMatch = false
          }
        }

        // Return true only if both status and date match
        return statusMatch && dateMatch
      })
    },
    hasChallengesChartData() {
      return this.hasSeriesData(this.series)
    },
    hasInternalUsersChartData() {
      return this.hasSeriesData(this.internalUsersSeries)
    },
    hasUsersTypesChartData() {
      return this.hasSeriesData(this.allUsersTypesSeries)
    },
    hasRecentOpenData() {
      return this.totalChallenges > 0
    },
    hasUserInvitationsData() {
      return !!this.mainDashboardFullResponse.userDashboardResponse
    },
    hasStrategicGoalsData() {
      return this.hasChartItems(this.mainDashboardFullResponse.futureStrategicGoalsResponseList, 'countOfChallenges')
    },
    hasStrategicDirectionsData() {
      return this.hasChartItems(
        this.mainDashboardFullResponse.futureStrategicDirectionsResponseList,
        'countOfChallenges'
      )
    },
    hasDepartmentStatsData() {
      return this.hasChartItems(this.departmentChallengeStatsResponseList, 'challengeCount')
    }
  },
  async created() {
    await this.fetchData()
    this.isLoading = false
  },
  methods: {
    mapChallengeItems(challenges: Challenge[]) {
      return challenges.map((item: Challenge) => ({
        head1: item.id,
        head2: item.localTitle,
        head3: item.localDescription
      })) as DialogItemResponse[]
    },
    mapUserItems(users: User[]) {
      return users.map((item: User) => ({
        head1: item.grpNumber,
        head2: item.fullName,
        head3: item.email
      })) as DialogItemResponse[]
    },
    openDashboardDialog({
                          headers,
                          items,
                          title,
                          appearAllActions = false,
                          appearViewActions = false
                        }: {
      headers: string[]
      items: DialogItemResponse[]
      title: string
      appearAllActions?: boolean
      appearViewActions?: boolean
    }) {
      this.dialogHeaders = headers
      this.dialogItems = items
      this.listDialogTitle = title
      this.appearAllActions = appearAllActions
      this.appearViewActions = appearViewActions
      this.dialog = true
    },
    hasSeriesData(series: number[]) {
      return Array.isArray(series) && series.some((value) => Number(value) > 0)
    },
    hasItems(items: unknown) {
      return Array.isArray(items) && items.length > 0
    },
    hasChartItems(items: unknown, countKey: string) {
      return Array.isArray(items) && items.some((item) => Number(item?.[countKey]) > 0)
    },
    calculatePercentage(value: number, total: number) {
      if (!total) {
        return 0
      }

      return Number(((value / total) * 100).toFixed(1))
    },
    handleShowUserInvitationsDetails(details) {
      // You can reuse your existing dialog logic here
      this.openDashboardDialog({
        headers: details.headers,
        items: details.items,
        title: details.title
      })
    },
    challengeDialog(updatedDialog: boolean) {
      this.dialog = updatedDialog
    },
    handleCompletedDecisionsData() {
      this.openDashboardDialog({
        headers: this.challengeDialogHeaders,
        items: this.mapChallengeItems(this.completedDecisionList),
        title: this.translate('adminDashboard.decision.completed') as string,
        appearViewActions: true
      })
    },
    handleValidatorRevisedDecisionsData() {
      this.openDashboardDialog({
        headers: this.challengeDialogHeaders,
        items: this.mapChallengeItems(this.validatorRevisedDecisionList),
        title: this.translate('adminDashboard.decision.revisedByValidator') as string,
        appearAllActions: true
      })
    },
    handleClosedDecisionsData() {
      this.openDashboardDialog({
        headers: this.challengeDialogHeaders,
        items: this.mapChallengeItems(this.closedDecisionList),
        title: this.translate('adminDashboard.decision.closed') as string,
        appearViewActions: true
      })
    },
    handleUsersTypesData(...args: any[]) {
      const config = args[2]
      const dataPoint = config.dataPointIndex
      const usersByType = [this.externalUsersList, this.internalUsersList]
      const selectedUsers = usersByType[dataPoint] || []
      const selectedLabel = this.chartOptions3.labels?.[dataPoint] || this.translate('adminDashboard.totalUsers')

      this.openDashboardDialog({
        headers: this.internalUserDialogHeaders,
        items: this.mapUserItems(selectedUsers),
        title: `${this.translate('adminDashboard.totalUsers')} - ${selectedLabel}`
      })
    },
    handleInternalUsersData(...args: any[]) {
      const config = args[2]
      const dataPoint = config.dataPointIndex
      const usersByStatus = [this.deletedUsersList, this.activeUsersList, this.notActiveUsersList]
      const selectedUsers = usersByStatus[dataPoint] || []
      const selectedLabel =
        this.chartOptions2.labels?.[dataPoint] || this.translate('adminDashboard.totalInternalUsers')

      this.openDashboardDialog({
        headers: this.internalUserDialogHeaders,
        items: this.mapUserItems(selectedUsers),
        title: `${this.translate('adminDashboard.totalInternalUsers')} - ${selectedLabel}`
      })
    },
    handleChallengeDataPointSelection(...args: any[]) {
      const config = args[2]
      // Get the data point that was selected
      const dataPoint = config.dataPointIndex
      const challengesByStatus = [
        this.completedChallengesList,
        this.openChallengesList,
        this.closedChallengesList,
        this.votingChallengesList,
        this.revisedChallengesList,
        this.pausedChallengesList,
        this.underReviewChallengesList,
        this.newChallengesList,
        this.urgentChallengesList
      ]
      const selectedChallenges = challengesByStatus[dataPoint] || []
      const selectedLabel = this.chartOptions.labels?.[dataPoint] || this.translate('adminDashboard.totalChallenges')

      this.openDashboardDialog({
        headers: this.challengeDialogHeaders,
        items: this.mapChallengeItems(selectedChallenges),
        title: `${this.translate('adminDashboard.totalChallenges')} - ${selectedLabel}`,
        appearAllActions: dataPoint === 8
      })
    },
    async fetchData() {
      // Set loading state to true
      this.isLoading = true

      try {
        // Define the endpoint URL
        const endpoint = URIS.LOOKUP.BASE + URIS.LOOKUP.DASHBOARD

        // Call the get method to fetch data
        await get(endpoint).then((response: SuccessResponse<AdminDashboardResponse>) => {
          if (response.success) {
            const responseData = response.data

            this.mainDashboardFullResponse = responseData

            const challengeDashboardResponse = responseData.challengeDashboardResponse

            const usersDashboardResponse = responseData.usersDashboardResponse
            this.departmentChallengeStatsResponseList = responseData.departmentChallengeStatsResponseList

            const {
              totalChallenges,
              completedCount,
              openCount,
              closedCount,
              votingCount,
              revisedCount,
              pausedCount,
              underReviewCount,
              newCount,
              urgentCount
            } = challengeDashboardResponse

            const {
              activeUsersCount,
              deletedUsersCount,
              notActiveUsersCount,
              totalUsersCount,
              externalUsersCount,
              internalUsersCount,
              internalAdminUsersList,
              externalGuestUsersList,
              activeUsersList,
              deletedUsersList,
              notActiveUsersList
            } = usersDashboardResponse

            // Decisions Data
            this.completedDecisionValue = completedCount
            this.completedDecisionList = challengeDashboardResponse.completedChallengesList
            this.completedDecision = this.calculatePercentage(completedCount, totalChallenges)

            this.validatorRevisedDecisionValue = urgentCount // This mean under admin review
            this.validatorRevisedDecisionList = challengeDashboardResponse.urgentChallengesList
            this.validatorRevisedDecision = this.calculatePercentage(urgentCount, totalChallenges)

            this.closedDecisionValue = closedCount
            this.closedDecisionList = challengeDashboardResponse.closedChallengesList
            this.closedDecision = this.calculatePercentage(closedCount, totalChallenges)

            // init Internal Users Values
            this.totalInternalUsers = internalUsersCount
            this.totalUsers = totalUsersCount
            this.internalUsersSeries = [deletedUsersCount, activeUsersCount, notActiveUsersCount]
            this.activeUsersList = activeUsersList
            this.deletedUsersList = deletedUsersList
            this.notActiveUsersList = notActiveUsersList

            this.externalUsersList = externalGuestUsersList
            this.internalUsersList = internalAdminUsersList

            this.allUsersTypesSeries = [externalUsersCount, internalUsersCount]

            // init Challenges values
            this.totalChallenges = totalChallenges

            this.completedChallengesList = challengeDashboardResponse.completedChallengesList
            this.openChallengesList = challengeDashboardResponse.openChallengesList
            this.closedChallengesList = challengeDashboardResponse.closedChallengesList
            this.votingChallengesList = challengeDashboardResponse.votingChallengesList
            this.revisedChallengesList = challengeDashboardResponse.revisedChallengesList
            this.pausedChallengesList = challengeDashboardResponse.pausedChallengesList
            this.underReviewChallengesList = challengeDashboardResponse.underReviewChallengesList
            this.newChallengesList = challengeDashboardResponse.newChallengesList
            this.urgentChallengesList = challengeDashboardResponse.urgentChallengesList
            this.challengesResponseList = challengeDashboardResponse.challengesResponseList

            this.challengeList = challengeDashboardResponse.challengesResponseList

            this.series = [
              completedCount,
              openCount,
              closedCount,
              votingCount,
              revisedCount,
              pausedCount,
              underReviewCount,
              newCount,
              urgentCount
            ]

            this.recentOpenPercentage = this.calculatePercentage(openCount, totalChallenges)
          } else {
            // Handle error or show a message
            console.error('Error fetching data')
            toast.error(this.translate('Messages.error.general.serviceDown') as string)
          }
        })
      } catch (error) {
        console.error('Error fetching dashboard data:', error)
        toast.error(this.translate('Messages.error.general.serviceDown') as string)
      }
    },
    formatCreatedAt(createdAt: any) {
      // Assuming createdAt is a valid date string
      return format(new Date(createdAt), 'dd/MM/yyyy HH:mm:ss')
    },
    getStatusColor(status: string): string {
      return this.statusColors[status as StatusKeys] || '#000000'
    },
    handleCloseDialog() {
      this.dialog = false
      this.listDialogTitle = ''
      this.dialogItems = []
      this.appearAllActions = false
      this.appearViewActions = false
    }
  }
}
</script>

<template src="./AdminDashboard.html"></template>
<style src="./AdminDashboard.scss" lang="scss"></style>
