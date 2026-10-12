<template>
  <v-card class="dp-card custom-admin-dashboard-card strategic-dashboard-card" :class="{ 'strategic-dashboard-card--large': large }">
    <div class="custom-admin-dashboard-content-div strategic-dashboard-card__body">
      <div class="top-status-div">
        <h3 class="heading-1">{{ cardTitle }}</h3>
        <p>{{ totalStrategicGoalsChallenges }}</p>
      </div>
      <div v-if="hasChartData" id="strategicGoalsChart" class="chart-div-admin-dashboard strategic-chart-shell">
        <apexchart
          class="custom-apex-chart-design"
          type="donut"
          :options="chartOptions"
          :series="series"
          :width="chartWidth"
          :height="chartHeight"
          @data-point-selection="handleDataPointSelection"
        ></apexchart>
      </div>
      <div v-else class="strategic-empty-state">
        <span class="mdi mdi-chart-donut strategic-empty-state__icon"></span>
        <h4>{{ t('noDataTitle') }}</h4>
        <p>{{ t('noDataDescription') }}</p>
      </div>
    </div>

    <DialogDashboardScreen
      :dialog-value="dialog"
      :items="dialogItems"
      :headers="dialogHeaders"
      :appear-actions="false"
      :appear-just-view-actions="true"
      @close-dialog="handleCloseDialog"
      @view="viewChallengeUnderReview"
    ></DialogDashboardScreen>
  </v-card>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import apexchart from 'vue3-apexcharts'
import { format } from 'date-fns'
import { useI18n } from 'vue-i18n'
import DialogDashboardScreen from '@/views/dashboard/dialog/DialogMainDashboard.vue'
import { useRouter } from 'vue-router'
import { resolveLocalizedValue } from '@/utils/localizationUtils'

interface StrategicGoal {
  id: number
  nameAr: string
  nameEn: string
  localName: string
  countOfChallenges: number
  challengesResponseList: any[]
}

interface DialogItemResponse {
  head1: any
  head2: any
  head3: any
}

export default defineComponent({
  name: 'ChallengeStrategicDashboardChart',
  components: {
    apexchart,
    DialogDashboardScreen
  },
  props: {
    cardTitle: {
      type: String,
      required: true
    },
    goalsData: {
      type: Array as PropType<StrategicGoal[]>,
      required: true
    },
    large: {
      type: Boolean,
      default: false
    }
  },
  setup() {
    const { t, locale } = useI18n()
    const router = useRouter()

    const employeeLang: string = globalThis.localStorage.getItem('employeeLang') || locale.value || 'ar'

    return {
      t,
      router,
      employeeLang
    }
  },
  data() {
    return {
      dialog: false,
      dialogHeaders: [] as string[],
      dialogItems: [] as DialogItemResponse[],
      dialogTitle: '',
      dialogDescription: '',
      selectedGoalId: 0,
      challengeDialogHeaders: [
        this.t('adminDashboard.dialog.challenge.id'),
        this.t('adminDashboard.dialog.challenge.title'),
        this.t('adminDashboard.dialog.challenge.description')
      ],
      statusColors: {
        COMPLETED: '#27ae60',
        OPEN: '#3498db',
        CLOSED: '#e74c3c',
        VOTING: '#be09a6',
        REVISED: '#8c591d',
        PAUSED: '#120c7e',
        UNDER_REVIEW: '#c24671',
        NEW: '#6ceca3',
        URGENT: '#5e340e'
      }
    }
  },
  computed: {
    hasChartData() {
      return this.series.some((value) => Number(value) > 0)
    },
    chartOptions(): any {
      return {
        chart: {
          type: 'donut',
          parentHeightOffset: 0,
          toolbar: {
            show: false
          }
        },
        plotOptions: {
          pie: {
            customScale: this.large ? 1 : .8,
            donut: {
              size: '70%',
              labels: {
                show: true,
                name: {
                  fontSize: this.large ? '18px' : '16px'
                },
                value: {
                  fontSize: this.large ? '24px' : '20px'
                },
                total: {
                  show: true,
                  fontSize: this.large ? '18px' : '16px',
                  formatter: (w: any) => {
                    return w.globals.seriesTotals.reduce((a: any, b: any) => a + b, 0)
                  }
                }
              }
            }
          }
        },
        fill: {
          colors: this.generateColors(this.goalsData.length)
        },
        labels: this.goalsData.map((goal) => goal.localName),
        colors: this.generateColors(this.goalsData.length),
        stroke: {
          width: 2,
          colors: ['#ffffff']
        },
        dataLabels: {
          enabled: this.hasChartData
        },
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
          },
          {
            breakpoint: 600,
            options: {
              legend: {
                position: 'bottom',
                fontSize: '12px'
              }
            }
          }
        ],
        noData: {
          text: this.t('noData'),
          align: 'center',
          verticalAlign: 'middle'
        }
      }
    },
    series() {
      return this.goalsData.map((goal) => goal.countOfChallenges)
    },
    totalStrategicGoalsChallenges() {
      return this.series.reduce((a, b) => a + b, 0)
    },
    chartWidth() {
      return this.large ? 660 : 460
    },
    chartHeight() {
      return this.large ? 560 : 430
    }
  },
  methods: {
    isLanguageAr(): boolean {
      return this.employeeLang === 'ar'
    },
    generateColors(count: number): string[] {
      // Generate distinct colors for each goal
      const colors: string[] = []
      if (!count) {
        return ['#D6E9E0']
      }

      const hueStep = 360 / count

      for (let i = 0; i < count; i++) {
        const hue = (i * hueStep) % 360
        colors.push(`hsl(${hue}, 70%, 50%)`)
      }

      return colors
    },
    handleDataPointSelection(...args: any[]) {
      const config = args[2]
      const dataPointIndex = config.dataPointIndex
      const selectedGoal = this.goalsData[dataPointIndex]

      if (!selectedGoal) {
        return
      }

      this.dialogHeaders = this.challengeDialogHeaders
      this.dialogItems = selectedGoal.challengesResponseList.map((challenge) => ({
        head1: challenge.id,
        head2: resolveLocalizedValue({
          locale: this.employeeLang,
          ar: challenge.titleAr,
          en: challenge.titleEn,
          fallback: challenge.localTitle
        }),
        head3: resolveLocalizedValue({
          locale: this.employeeLang,
          ar: challenge.descriptionAr,
          en: challenge.descriptionEn,
          fallback: challenge.localDescription
        })
      }))

      this.dialogTitle = selectedGoal.localName
      this.dialogDescription = `${selectedGoal.countOfChallenges} ${this.t('adminDashboard.challenges')}`

      this.dialog = true
      this.selectedGoalId = selectedGoal.id
    },
    viewChallengeUnderReview(request: DialogItemResponse) {
      const challengeId = request.head1
      this.router.push({ name: 'ChallengeView', params: { challengeId } })
    },
    handleCloseDialog() {
      this.dialog = false
    },
    formatCreatedAt(createdAt: string) {
      return format(new Date(createdAt), 'dd/MM/yyyy HH:mm:ss')
    },
    getStatusColor(status: string): string {
      return this.statusColors[status] || '#000000'
    }
  }
})
</script>

<style scoped lang="scss">
.strategic-dashboard-card {
  min-height: 560px;
  padding: 20px;

  &--large {
    min-height: 640px;

    .chart-div-admin-dashboard {
      min-height: 560px;
    }

    .strategic-chart-shell {
      :deep(.apexcharts-legend) {
        font-size: 14px !important;
      }
    }
  }

  &__body {
    display: flex;
    flex: 1;
    flex-direction: column;
    min-height: inherit;
  }

  .top-status-div {
    margin-bottom: 20px;

    h3 {
      font-size: 16px;
      margin-bottom: 5px;
    }

    p {
      font-size: 20px;
      font-weight: bold;
    }
  }

  .chart-div-admin-dashboard {
    display: flex;
    justify-content: center;
    align-items: center;
    flex: 1;
    min-height: 430px;
    overflow: hidden;
  }
}

.strategic-chart-shell {
  width: 100%;
  min-width: 0;

  :deep(.apexcharts-canvas),
  :deep(.apexcharts-svg) {
    margin: 0 auto;
    max-width: 100%;
  }
}

.strategic-empty-state {
  align-items: center;
  background: linear-gradient(180deg, rgba(0, 135, 85, 0.04) 0%, rgba(0, 135, 85, 0.09) 100%);
  border: 1px dashed rgba(0, 135, 85, 0.22);
  border-radius: 18px;
  color: #35584e;
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 10px;
  justify-content: center;
  min-height: 300px;
  padding: 24px;
  text-align: center;

  h4 {
    color: #174636;
    font-family: 'bukrabold', serif;
    font-size: 18px;
    margin: 0;
  }

  p {
    font-size: 14px;
    margin: 0;
    max-width: 320px;
  }

  &__icon {
    align-items: center;
    background: rgba(0, 135, 85, 0.12);
    border-radius: 50%;
    color: #008759;
    display: inline-flex;
    font-size: 28px;
    height: 56px;
    justify-content: center;
    width: 56px;
  }
}

@media (max-width: 768px) {
  .strategic-dashboard-card {
    min-height: 520px;
    padding: 16px;

    &--large {
      min-height: 560px;

      .chart-div-admin-dashboard {
        min-height: 460px;
      }
    }
  }

  .strategic-empty-state {
    min-height: 240px;
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .strategic-dashboard-card {
    min-height: 360px;

    &--large {
      min-height: 440px;

      .chart-div-admin-dashboard {
        min-height: 360px;
      }
    }
  }

  .strategic-chart-shell {
    :deep(.apexcharts-canvas),
    :deep(.apexcharts-svg) {
      transform: scale(0.78);
      transform-origin: center top;
    }
  }

  .strategic-empty-state {
    min-height: 220px;

    h4 {
      font-size: 16px;
    }

    p {
      font-size: 13px;
    }
  }
}
</style>
