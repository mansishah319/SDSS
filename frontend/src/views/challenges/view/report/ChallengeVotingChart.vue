<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import type { ApexOptions } from 'apexcharts'
import type { ChallengeVotingChartModel } from './challengeReport.types'

const props = defineProps<{
  model: ChallengeVotingChartModel
}>()

const { locale } = useI18n()

const labels = computed(() => {
  return locale.value.startsWith('ar')
    ? {
      eyebrow: 'تحليل التصويت',
      totalVotes: 'إجمالي الأصوات'
    }
    : {
      eyebrow: 'Voting analytics',
      totalVotes: 'Total votes'
    }
})

const chartSeries = computed(() => [
  {
    name: props.model.title,
    data: props.model.items.map((item) => item.votes)
  }
])

const chartOptions = computed<ApexOptions>(() => ({
  chart: {
    type: 'bar',
    toolbar: { show: false },
    fontFamily: 'bukraregular',
    foreColor: '#5a6c65',
    parentHeightOffset: 0
  },
  colors: props.model.items.map((item) => (item.isWinner ? '#129f63' : '#b7c6bf')),
  plotOptions: {
    bar: {
      horizontal: true,
      borderRadius: 8,
      distributed: true,
      barHeight: '58%'
    }
  },
  grid: {
    strokeDashArray: 4,
    borderColor: 'rgba(12, 58, 44, 0.08)'
  },
  xaxis: {
    categories: props.model.items.map((item) => item.label),
    axisBorder: { show: false },
    axisTicks: { show: false }
  },
  yaxis: {
    labels: {
      maxWidth: 220,
      style: {
        fontSize: '12px'
      }
    }
  },
  dataLabels: {
    enabled: true,
    formatter: (_value, options) => {
      const item = props.model.items[options.dataPointIndex]
      return `${item.votes} • ${item.percentage.toFixed(0)}%`
    },
    style: {
      colors: ['#17362d'],
      fontWeight: 600
    },
    offsetX: 10
  },
  legend: { show: false },
  tooltip: {
    y: {
      formatter: (value, options) => {
        const item = props.model.items[options.dataPointIndex]
        return `${value} (${item.percentage.toFixed(1)}%)`
      }
    }
  },
  noData: {
    text: props.model.emptyLabel
  },
  states: {
    hover: {
      filter: {
        type: 'darken',
        value: 0.12
      }
    }
  },
  responsive: [
    {
      breakpoint: 600,
      options: {
        chart: {
          height: 280
        },
        yaxis: {
          labels: {
            maxWidth: 120
          }
        }
      }
    }
  ]
}))

const topSummary = computed(() => props.model.tieLabel || props.model.leadingLabel)
const isRtl = computed(() => locale.value.startsWith('ar'))
</script>

<template>
  <v-card class="challenge-voting-card" rounded="xl" elevation="0">
    <v-card-text class="challenge-voting-card__body">
      <div class="challenge-voting-card__header">
        <div>
          <p class="challenge-voting-card__eyebrow">{{ labels.eyebrow }}</p>
          <h3 class="challenge-voting-card__title font-bukra-bold">{{ model.title }}</h3>
        </div>

        <div class="challenge-voting-card__total">
          <span class="challenge-voting-card__total-label">{{ labels.totalVotes }}</span>
          <strong class="challenge-voting-card__total-value font-bukra-bold">{{ model.totalVotes }}</strong>
        </div>
      </div>

      <v-alert
        v-if="topSummary"
        :type="model.tieLabel ? 'warning' : 'success'"
        variant="tonal"
        density="comfortable"
        class="challenge-voting-card__summary"
      >
        {{ topSummary }}
      </v-alert>

      <apexchart height="320" type="bar" :dir="isRtl ? 'rtl' : 'ltr'" :options="chartOptions" :series="chartSeries" />

      <div v-if="model.items.length" class="challenge-voting-card__legend">
        <div v-for="item in model.items" :key="item.id" class="challenge-voting-card__legend-row">
          <div class="challenge-voting-card__legend-main">
            <span class="challenge-voting-card__legend-dot" :class="{ 'is-winner': item.isWinner }" />
            <span class="challenge-voting-card__legend-label">{{ item.label }}</span>
          </div>

          <strong class="challenge-voting-card__legend-value"
          >{{ item.votes }} / {{ item.percentage.toFixed(0) }}%</strong
          >
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>

<style scoped lang="scss">
.challenge-voting-card {
  border: 1px solid rgba(13, 82, 62, 0.1);
  background: linear-gradient(180deg, #ffffff 0%, #f9fcfb 100%);
}

.challenge-voting-card__body {
  padding: 22px;
}

.challenge-voting-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.challenge-voting-card__eyebrow,
.challenge-voting-card__total-label {
  margin: 0;
  font-size: 0.76rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #7a8b84;
}

.challenge-voting-card__title {
  margin: 8px 0 0;
  color: #16362d;
}

.challenge-voting-card__total {
  min-width: 100px;
  text-align: end;
}

.challenge-voting-card__total-value {
  display: block;
  margin-top: 8px;
  font-size: 1.5rem;
  color: #0d6b4d;
}

.challenge-voting-card__summary {
  margin-top: 18px;
}

.challenge-voting-card__legend {
  display: grid;
  gap: 10px;
  margin-top: 8px;
}

.challenge-voting-card__legend-row,
.challenge-voting-card__legend-main {
  display: flex;
  align-items: center;
}

.challenge-voting-card__legend-row {
  justify-content: space-between;
  gap: 14px;
}

.challenge-voting-card__legend-main {
  gap: 10px;
  min-width: 0;
}

.challenge-voting-card__legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #b7c6bf;
  flex-shrink: 0;
}

.challenge-voting-card__legend-dot.is-winner {
  background: #129f63;
}

.challenge-voting-card__legend-label {
  color: #314843;
  overflow-wrap: anywhere;
}

.challenge-voting-card__legend-value {
  color: #16362d;
  white-space: nowrap;
}

@media (max-width: 600px) {
  .challenge-voting-card__body {
    padding: 18px;
  }

  .challenge-voting-card__header,
  .challenge-voting-card__legend-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .challenge-voting-card__total {
    text-align: start;
  }
}
</style>
