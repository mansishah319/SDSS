<template>
  <v-card class="dp-card custom-admin-dashboard-card card-height dp-chart-card invitations-dashboard-card">
    <div class="custom-admin-dashboard-content-div invitations-dashboard-card__body">
      <div class="top-status-div">
        <h3 class="heading-1">
          {{ $t('common.joinRequests') }}
        </h3>
        <div class="d-flex ga-5 align-center">
          <div class="card-action-strip">
            <v-tooltip location="top">
              <template #activator="{ props }">
                <v-btn class="view-requests-btn" icon color="primary" v-bind="props" @click="navigateToRequests">
                  <v-icon>mdi mdi-briefcase-eye</v-icon>
                </v-btn>
              </template>
              <span>{{ $t('common.viewRequests') }}</span>
            </v-tooltip>
          </div>
          <p>{{ totalInvitationsCount }}</p>
        </div>
      </div>

      <div v-if="hasChartData" id="chart" class="chart-div-admin-dashboard invitations-chart-shell">
        <apexchart
          type="donut"
          :options="userInvitationsChartOptions"
          :series="userInvitationsSeries"
          :rotate="360"
          :width="500"
          :height="430"
          @data-point-selection="handleUserInvitationsDataPointSelection"
        ></apexchart>
      </div>
      <div v-else class="dashboard-empty-state invitations-empty-state">
        <span class="mdi mdi-email-outline dashboard-empty-icon"></span>
        <h4>{{ $t('noData') }}</h4>
        <p>{{ $t('common.joinRequests') }}</p>
      </div>
    </div>
  </v-card>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { TheUserDashboardResponse } from '@/dto/userDashboard/TheUserDashboardResponse'

export default defineComponent({
  name: 'UserInvitationsDashboardCard',
  props: {
    userDashboardResponse: {
      type: Object as () => TheUserDashboardResponse,
      required: true
    }
  },
  emits: ['show-details'],
  setup(props, { emit }) {
    const { t } = useI18n()
    const router = useRouter()

    // Chart labels
    const userInvitationsLabels = [
      t('myDashboard.totalAcceptedInvitations'),
      t('myDashboard.totalPendingInvitations'),
      t('myDashboard.totalRejectedInvitations')
    ]

    // Chart colors
    const userInvitationsColors = [
      '#008755', // Green for accepted
      '#1570EF', // Blue for pending
      '#D92D20' // Red for rejected
    ]

    // Computed series data
    const userInvitationsSeries = computed(() => [
      props.userDashboardResponse?.totalAcceptedCount ?? 0,
      props.userDashboardResponse?.totalPendingCount ?? 0,
      props.userDashboardResponse?.totalRejectedCount ?? 0
    ])
    const totalInvitationsCount = computed(() => props?.userDashboardResponse?.totalInvitationsCount ?? 0)
    const hasChartData = computed(() => userInvitationsSeries.value.some((value) => Number(value) > 0))

    // Chart options
    const userInvitationsChartOptions = {
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
        colors: userInvitationsColors
      },
      labels: userInvitationsLabels,
      colors: userInvitationsColors,
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
    }

    // Handle click on chart segments
    const handleUserInvitationsDataPointSelection = (event: any, chartContext: any, config: any) => {
      const dataPoint = config.dataPointIndex
      let items = []

      switch (dataPoint) {
        case 0: // Accepted
          items = props.userDashboardResponse.acceptedInvitationList.map((item) => ({
            head1: item.id,
            head2: item.localTitle,
            head3: item.startTime + t('common.toWithSpace') + item.endTime
          }))
          break
        case 1: // Pending
          items = props.userDashboardResponse.pendingInvitationList.map((item) => ({
            head1: item.id,
            head2: item.localTitle,
            head3: item.startTime + t('common.toWithSpace') + item.endTime
          }))
          break
        case 2: // Rejected
          items = props.userDashboardResponse.rejectedInvitationsList.map((item) => ({
            head1: item.id,
            head2: item.localTitle,
            head3: item.startTime + t('common.toWithSpace') + item.endTime
          }))
          break
      }

      emit('show-details', {
        title: userInvitationsLabels[dataPoint],
        items,
        headers: [t('common.id'), t('adminDashboard.dialog.challenge.title'), t('common.challengeTimeFromTo')]
      })
    }

    // Navigation function
    const navigateToRequests = () => {
      router.push({ name: 'UserRequestsDashboard' })
    }

    return {
      userInvitationsSeries,
      userInvitationsChartOptions,
      handleUserInvitationsDataPointSelection,
      totalInvitationsCount,
      hasChartData,
      navigateToRequests
    }
  }
})
</script>

<style lang="scss" scoped>
.dp-card {
  border-radius: 22px;
  box-shadow: 0 4px 8px -2px #1018281a;
  height: fit-content;
  padding: 24px;
  position: relative;
  width: auto;

  &.card-height {
    height: 400px;
  }

  .apexcharts-canvas {
    margin: 0 auto;
  }

  .chart-div-admin-dashboard {
    align-items: center;
    display: flex;
    flex: 1;
    height: auto;
    justify-content: center;
    margin-top: 1rem;
    width: auto;

    &.apexchart {
      height: auto;
      width: auto;
    }
  }
}

.invitations-dashboard-card {
  min-height: 460px;

  &__body {
    display: flex;
    flex: 1;
    flex-direction: column;
  }
}

.top-status-div {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #d0d0d0;
  height: 40px;
  margin-bottom: 19px;

  h3 {
    font-size: 16px;
    border-radius: 100px;
    font-weight: 900;
    font-family: 'bukrabold', serif;
    color: #008759;
  }

  p {
    font-size: 16px;
    border-radius: 100px;
    font-weight: 900;
    font-family: 'bukrabold', serif;
    color: #000;
  }

  margin-bottom: 10px;
  padding-bottom: 10px;
}

.card-action-strip {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 6px;

  .view-requests-btn {
    transition: all 0.3s ease;
    width: 34px;
    height: 34px;

    &:hover {
      transform: translateX(2px);
    }
  }
}

.invitations-chart-shell {
  min-height: 300px;

  :deep(.apexcharts-canvas),
  :deep(.apexcharts-svg) {
    margin: 0 auto;
    max-width: 100%;
  }
}

.invitations-empty-state {
  min-height: 430px;
}

@media (max-width: 768px) {
  .invitations-dashboard-card {
    min-height: 520px;
  }

  .invitations-chart-shell,
  .invitations-empty-state {
    min-height: 400px;
  }
}

@media (max-width: 480px) {
  .invitations-dashboard-card {
    min-height: 460px;
  }

  .invitations-chart-shell :deep(.apexcharts-canvas),
  .invitations-chart-shell :deep(.apexcharts-svg) {
    transform: scale(0.78);
    transform-origin: center top;
  }
}
</style>
