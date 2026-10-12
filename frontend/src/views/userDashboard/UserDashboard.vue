<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script>
import { format, isSameDay } from 'date-fns'
import { get } from '@/utils/GenericApiHelper'
import { URIS } from '@/constants/DpConstants'
import { useI18n } from 'vue-i18n'
import UserDashboardDialogScreen from '@/views/userDashboard/dialog/UserDashboardDialogScreen.vue'
import { useRouter } from 'vue-router'

export default {
  components: { UserDashboardDialogScreen },
  setup() {
    const { t } = useI18n()
    const router = useRouter()

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

    const handleRowClick = (item) => {
      console.log('Clicked item:', item)
      const challengeId = item.id
      console.log(challengeId)
      router.push({ name: 'ChallengeView', params: { challengeId } })
    }

    const approveItem = (item) => {
      // Handle approve action
      console.log('Item approved:', item)
    }
    const rejectItem = (item) => {
      // Handle reject action
      console.log('Item rejected:', item)
    }

    return {
      isLoading: true,
      approveItem,
      rejectItem,
      handleRowClick,
      challengeDialogHeaders: challengeDialogHeaders,
      internalUserDialogHeaders: internalUserDialogHeaders,

      completedChallengesList: [],
      openChallengesList: [],
      closedChallengesList: [],
      votingChallengesList: [],
      revisedChallengesList: [],
      pausedChallengesList: [],
      underReviewChallengesList: [],
      newChallengesList: [],
      urgentChallengesList: [],
      challengesResponseList: [],

      activeUsersList: [],
      deletedUsersList: [],
      notActiveUsersList: [],

      externalUsersList: [],
      internalUsersList: [],

      series: [0, 0, 0, 0, 0, 0, 0, 0, 0],
      totalChallenges: 0,
      totalInternalUsers: 0,
      totalUsers: 0,
      chartOptions: {
        chart: {
          type: 'donut'
        },
        plotOptions: {
          pie: {
            total: {
              show: true,
              formatter: function(w) {
                return w.globals.seriesTotals.reduce((a, b) => {
                  return a + b
                }, 0)
              }
            }
          }
        },
        fill: {
          colors: ['#008755', '#1570EF', '#D92D20', '#AE1368', '#B35F00', '#4E5BA6', '#E31B54', '#56bd9a', '#C54D01']
        },
        labels: theLabels,
        colors: ['#008755', '#1570EF', '#D92D20', '#AE1368', '#B35F00', '#4E5BA6', '#E31B54', '#56bd9a', '#C54D01']
      },

      internalUsersSeries: [0, 0, 0],
      chartOptions2: {
        chart: {
          type: 'donut'
        },

        plotOptions: {
          pie: {
            total: {
              show: true,
              formatter: function(w) {
                return w.globals.seriesTotals.reduce((a, b) => {
                  return a + b
                }, 0)
              }
            }
          }
        },
        fill: {
          colors: ['#D92D20', '#008755', '#1570EF']
        },
        labels: internalUsersLabels,
        colors: ['#D92D20', '#008755', '#1570EF']
      },

      allUsersTypesSeries: [0, 0],
      chartOptions3: {
        chart: {
          type: 'donut'
        },

        plotOptions: {
          pie: {
            total: {
              show: true,
              formatter: function(w) {
                return w.globals.seriesTotals.reduce((a, b) => {
                  return a + b
                }, 0)
              }
            }
          }
        },
        fill: {
          colors: ['#D92D20', '#008755']
        },
        labels: userTypesLabels,
        colors: ['#D92D20', '#008755']
      },

      recentOpenPercentage: 0,

      completedDecision: 0,
      completedDecisionValue: 0,
      completedDecisionList: [],

      validatorRevisedDecision: 0,
      validatorRevisedDecisionValue: 0,
      validatorRevisedDecisionList: [],

      closedDecision: 0,
      closedDecisionValue: 0,
      closedDecisionList: [],

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
      },

      dialogHeaders: [],
      dialogItems: [],
      challengeList: [],
      selectedStatus: '',
      selectedDate: '',
      dialogFlag: false
    }
  },
  computed: {
    filteredStatusList() {
      // Format selectedDate to 'YYYY-MM-DD' format
      const formattedDate = this.selectedDate ? format(new Date(this.selectedDate), 'dd/MM/yyyy') : ''

      // Filter the statusList based on selectedStatus and selectedDate
      return this.challengeList.filter((item) => {
        // Check if item matches the selected status
        const statusMatch = !this.selectedStatus || item.statusLocal === this.selectedStatus

        // Check if item matches the selected date
        const createdAtDate = format(new Date(item.createdAt), 'dd/MM/yyyy')

        const dateMatch = !formattedDate || isSameDay(new Date(createdAtDate), new Date(formattedDate))

        // Return true only if both status and date match
        return statusMatch && dateMatch
      })
    }
  },
  async created() {
    await this.fetchData()
    this.isLoading = false
  },
  methods: {
    handleCompletedDecisionsData() {
      this.dialogHeaders = this.challengeDialogHeaders
      this.dialogItems = this.completedDecisionList.map((item) => ({
        head1: item.id,
        head2: item.localTitle,
        head3: item.localDescription
      }))
      console.log('Opening dialog')
      this.dialogFlag = true
    },
    handleValidatorRevisedDecisionsData() {
      this.dialogHeaders = this.challengeDialogHeaders
      this.dialogItems = this.validatorRevisedDecisionList.map((item) => ({
        head1: item.id,
        head2: item.localTitle,
        head3: item.localDescription
      }))
      this.dialogFlag = true
    },
    handleClosedDecisionsData() {
      this.dialogHeaders = this.challengeDialogHeaders
      this.dialogItems = this.closedDecisionList.map((item) => ({
        head1: item.id,
        head2: item.localTitle,
        head3: item.localDescription
      }))
      this.dialogFlag = true
    },
    handleUsersTypesData(event, chartContext, config) {
      const dataPoint = config.dataPointIndex
      console.log(dataPoint)

      this.dialogHeaders = this.internalUserDialogHeaders

      if (dataPoint === 0) {
        this.dialogItems = this.externalUsersList.map((item) => ({
          head1: item.grpNumber,
          head2: item.fullName,
          head3: item.email
        }))
      } else if (dataPoint === 1) {
        this.dialogItems = this.internalUsersList.map((item) => ({
          head1: item.grpNumber,
          head2: item.fullName,
          head3: item.email
        }))
      }

      console.log(config)
      console.log(chartContext)
      this.dialogFlag = true
    },
    handleInternalUsersData(event, chartContext, config) {
      const dataPoint = config.dataPointIndex
      console.log(dataPoint)

      this.dialogHeaders = this.internalUserDialogHeaders

      if (dataPoint === 1) {
        this.dialogItems = this.activeUsersList.map((item) => ({
          head1: item.grpNumber,
          head2: item.fullName,
          head3: item.email
        }))
      } else if (dataPoint === 0) {
        this.dialogItems = this.deletedUsersList.map((item) => ({
          head1: item.grpNumber,
          head2: item.fullName,
          head3: item.email
        }))
      } else if (dataPoint === 2) {
        this.dialogItems = this.notActiveUsersList.map((item) => ({
          head1: item.grpNumber,
          head2: item.fullName,
          head3: item.email
        }))
      }

      console.log(config)
      console.log(chartContext)
      this.dialogFlag = true
    },
    handleChallengeDataPointSelection(event, chartContext, config) {
      // Get the data point that was selected
      const dataPoint = config.dataPointIndex

      console.log(dataPoint)

      this.dialogHeaders = this.challengeDialogHeaders

      if (dataPoint === 1) {
        this.dialogItems = this.openChallengesList.map((item) => ({
          head1: item.id,
          head2: item.localTitle,
          head3: item.localDescription
        }))
      } else if (dataPoint === 0) {
        this.dialogItems = this.completedChallengesList.map((item) => ({
          head1: item.id,
          head2: item.localTitle,
          head3: item.localDescription
        }))
      } else if (dataPoint === 2) {
        this.dialogItems = this.closedChallengesList.map((item) => ({
          head1: item.id,
          head2: item.localTitle,
          head3: item.localDescription
        }))
      } else if (dataPoint === 3) {
        this.dialogItems = this.votingChallengesList.map((item) => ({
          head1: item.id,
          head2: item.localTitle,
          head3: item.localDescription
        }))
      } else if (dataPoint === 4) {
        this.dialogItems = this.revisedChallengesList.map((item) => ({
          head1: item.id,
          head2: item.localTitle,
          head3: item.localDescription
        }))
      } else if (dataPoint === 5) {
        this.dialogItems = this.pausedChallengesList.map((item) => ({
          head1: item.id,
          head2: item.localTitle,
          head3: item.localDescription
        }))
      } else if (dataPoint === 6) {
        this.dialogItems = this.underReviewChallengesList.map((item) => ({
          head1: item.id,
          head2: item.localTitle,
          head3: item.localDescription
        }))
      } else if (dataPoint === 7) {
        this.dialogItems = this.newChallengesList.map((item) => ({
          head1: item.id,
          head2: item.localTitle,
          head3: item.localDescription
        }))
      } else if (dataPoint === 8) {
        this.dialogItems = this.urgentChallengesList.map((item) => ({
          head1: item.id,
          head2: item.localTitle,
          head3: item.localDescription
        }))
      }

      console.log(config)
      console.log(chartContext)
      this.dialogFlag = true
    },
    async fetchData() {
      // Define the endpoint URL
      const endpoint = URIS.LOOKUP.BASE + URIS.LOOKUP.DASHBOARD

      // Call the get method to fetch data
      const response = await get(endpoint)
      if (response) {
        const responseData = response.data

        const challengeDashboardResponse = responseData.challengeDashboardResponse

        const usersDashboardResponse = responseData.usersDashboardResponse

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
        this.completedDecision = ((completedCount / totalChallenges) * 100).toFixed(1)

        this.validatorRevisedDecisionValue = urgentCount // This mean under admin review
        this.validatorRevisedDecisionList = challengeDashboardResponse.urgentChallengesList
        this.validatorRevisedDecision = ((urgentCount / totalChallenges) * 100).toFixed(1)

        this.closedDecisionValue = closedCount
        this.closedDecisionList = challengeDashboardResponse.closedChallengesList
        this.closedDecision = ((closedCount / totalChallenges) * 100).toFixed(1)

        // init Internal Users Values
        this.totalInternalUsers = internalUsersCount
        this.totalUsers = totalUsersCount
        this.internalUsersSeries = [deletedUsersCount, activeUsersCount, notActiveUsersCount]
        console.log('internalUsersSeries' + this.internalUsersSeries)

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

        this.recentOpenPercentage = ((openCount / totalChallenges) * 100).toFixed(1)
      } else {
        // Handle error or show a message
        console.error('Error fetching data')
      }
    },
    formatCreatedAt(createdAt) {
      // Assuming createdAt is a valid date string
      return format(new Date(createdAt), 'dd/MM/yyyy HH:mm:ss')
    },
    getStatusColor(status) {
      return this.statusColors[status] || '#000'
    },
    handleCloseDialog() {
      this.dialogFlag = false
    }
  }
}
</script>

<template src="./UserDashboard.html"></template>
<style src="./UserDashboard.scss" lang="scss"></style>
