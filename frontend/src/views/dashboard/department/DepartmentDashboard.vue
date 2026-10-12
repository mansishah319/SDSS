<template>
  <v-card class="department-custom-dp-card custom-admin-dashboard-card department-dashboard-card">
    <div class="top-status-div">
      <h3 class="heading-1">{{ $t(title) }}</h3>
      <p>{{ totalChallenges }}</p>
    </div>
    <div v-if="hasChartData" class="department-card-layout" :class="{ 'department-card-layout--rtl': isRtl }">
      <div class="department-chart-area">
        <CustomApexChart
          :chart-options="chartOptions"
          :series="series"
          :chart-type="chartType"
          :chart-width="420"
          :chart-height="420"
          @data-point-selection="handleChartPointSelection"
        ></CustomApexChart>
      </div>
      <div class="department-legend-panel">
        <div class="department-legend-list">
          <div v-for="(item, index) in legendItems" :key="`${item.label}-${index}`" class="department-legend-item">
            <span class="department-legend-swatch" :style="{ backgroundColor: item.color }"></span>
            <span class="department-legend-text">{{ item.label }}</span>
            <span class="department-legend-value">{{ item.value }}</span>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="department-empty-state">
      <span class="mdi mdi-domain department-empty-state__icon"></span>
      <h4>{{ $t('noDataTitle') }}</h4>
      <p>{{ $t('noDataDescription') }}</p>
    </div>
  </v-card>
</template>

<script lang="ts">
import { computed, defineComponent, PropType } from 'vue'
import { DepartmentChallengeStatsResponse } from '@/dto/dashboard/AdminDashboardResponse'
import CustomApexChart from '@/components/charts/CustomApexChart.vue'
import { useI18n } from 'vue-i18n'

export default defineComponent({
  name: 'DepartmentDashboard',
  components: { CustomApexChart },
  props: {
    title: {
      type: String,
      required: true
    },
    chartType: {
      type: String,
      default: 'donut'
    },
    chartWidth: {
      type: Number,
      default: undefined
    },
    chartHeight: {
      type: Number,
      default: 430
    },
    data: {
      type: Array as PropType<DepartmentChallengeStatsResponse[]>,
      required: true
    }
  },
  setup(props) {
    const { locale } = useI18n()
    const series = computed(() => props.data.map((dept) => dept.challengeCount))
    const labels = computed(() => props.data.map((dept) => dept.departmentName))
    const totalChallenges = computed(() => series.value.reduce((total, value) => total + value, 0))
    const hasChartData = computed(() => series.value.some((value) => Number(value) > 0))
    const chartColors = computed(() => {
      const palette = [
        '#14D997',
        '#2E90FA',
        '#FDB022',
        '#F04438',
        '#7A5AF8',
        '#12B76A',
        '#F670C7',
        '#38BDF8',
        '#F79009',
        '#6172F3',
        '#039855',
        '#EF6820',
        '#0BA5EC',
        '#9E77ED',
        '#EE46BC',
        '#16A34A'
      ]

      return props.data.map((_, index) => palette[index % palette.length])
    })
    const legendItems = computed(() => {
      return props.data.map((item, index) => ({
        label: item.departmentName,
        value: item.challengeCount,
        color: chartColors.value[index]
      }))
    })
    const isRtl = computed(() => (window.localStorage.getItem('employeeLang') || locale.value || 'ar') === 'ar')

    const chartOptions = computed(() => {
      return {
        chart: {
          type: 'donut',
          toolbar: {
            show: false
          }
        },
        labels: labels.value,
        colors: chartColors.value,
        fill: {
          colors: chartColors.value
        },
        stroke: {
          width: 2,
          colors: ['#ffffff']
        },
        plotOptions: {
          pie: {
            donut: {
              size: '68%',
              labels: {
                show: true,
                total: {
                  show: true,
                  formatter: () => totalChallenges.value
                }
              }
            }
          }
        },
        legend: {
          show: false
        },
        responsive: [
          {
            breakpoint: 960,
            options: {
              chart: {
                width: 380,
                height: 380
              }
            }
          },
          {
            breakpoint: 600,
            options: {
              chart: {
                width: 320,
                height: 320
              }
            }
          }
        ],
        noData: {
          text: ''
        }
      }
    })

    const handleChartPointSelection = (..._args: any[]) => {
    }

    return {
      chartOptions,
      series,
      totalChallenges,
      hasChartData,
      legendItems,
      isRtl,
      handleChartPointSelection
    }
  }
})
</script>
<style scoped lang="scss">
.department-custom-dp-card {
  border-radius: 22px;
  box-shadow: 0 4px 8px -2px #1018281a;
  height: fit-content;
  padding: 24px;
  position: relative;
  width: auto;

  &.card-height {
    height: 470px;
  }

  .apexcharts-canvas {
    margin: 0 auto;
  }
}

.department-dashboard-card {
  min-height: 590px;
  display: flex;
  flex-direction: column;
}

.department-card-layout {
  align-items: center;
  display: grid;
  flex: 1;
  gap: 24px;
  grid-template-columns: minmax(0, 1.2fr) minmax(280px, 0.8fr);
  min-height: 0;

  &--rtl {
    direction: rtl;
  }
}

.department-chart-area {
  align-items: center;
  display: flex;
  justify-content: center;
  min-height: 420px;
  width: 100%;
}

.department-legend-panel {
  align-self: stretch;
  background: rgba(248, 250, 252, 0.92);
  border: 1px solid rgba(0, 135, 85, 0.12);
  border-radius: 18px;
  min-height: 420px;
  padding: 18px;
}

.department-legend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: 100%;
  max-height: 420px;
  overflow-y: auto;
  padding-inline-end: 6px;
}

.department-legend-item {
  align-items: flex-start;
  display: grid;
  gap: 10px;
  grid-template-columns: auto 1fr auto;
}

.department-legend-swatch {
  border-radius: 999px;
  display: inline-flex;
  flex: 0 0 auto;
  height: 12px;
  margin-top: 4px;
  width: 12px;
}

.department-legend-text {
  color: #1f2937;
  font-size: 13px;
  font-weight: 600;
  line-height: 1.55;
  word-break: break-word;
}

.department-legend-value {
  color: #008759;
  font-family: 'bukrabold', serif;
  font-size: 14px;
  line-height: 1.4;
}

.department-empty-state {
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
  .department-custom-dp-card {
    padding: 16px;
  }

  .department-dashboard-card {
    min-height: 520px;
  }

  .department-card-layout {
    gap: 16px;
    grid-template-columns: 1fr;
  }

  .department-chart-area {
    min-height: 360px;
  }

  .department-legend-panel {
    min-height: 220px;
  }

  .department-legend-list {
    max-height: 220px;
  }

  .department-empty-state {
    min-height: 240px;
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .department-dashboard-card {
    min-height: 460px;
  }

  .department-chart-area :deep(.chart-dashboard-style .apexcharts-canvas),
  .department-chart-area :deep(.chart-dashboard-style .apexcharts-svg) {
    transform: scale(0.86);
    transform-origin: center top;
  }

  .department-legend-item {
    grid-template-columns: auto 1fr;
  }

  .department-legend-value {
    grid-column: 2;
  }

  .department-empty-state {
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
