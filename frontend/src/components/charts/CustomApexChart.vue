<template lang="html">
  <div v-if="chartOptions && series" class="chart-dashboard-style">
    <apexchart
      class="apexchart-el"
      :type="chartType"
      :options="chartOptions"
      :series="series"
      :rotate="360"
      :size="chartSize"
      :width="resolvedChartWidth"
      :height="resolvedChartHeight"
      @data-point-selection="handleDataPointSelection"
    ></apexchart>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import apexchart from 'vue3-apexcharts'

export default defineComponent({
  name: 'CustomApexChart',
  components: { apexchart },
  props: {
    chartOptions: {
      type: Object as PropType<object>,
      default: null
    },
    series: {
      type: Array as PropType<number[]>,
      default: () => []
    },
    chartType: {
      type: String,
      default: 'donut'
    },
    chartSize: {
      type: Number,
      default: 300
    },
    chartWidth: {
      type: Number,
      default: 430
    },
    chartHeight: {
      type: Number,
      default: 430
    }
  },
  emits: ['data-point-selection'],
  computed: {
    resolvedChartWidth() {
      return this.chartWidth ?? '100%'
    },
    resolvedChartHeight() {
      return this.chartHeight ?? 360
    }
  },
  methods: {
    handleDataPointSelection(event: any, chartContext: any, config: any) {
      this.$emit('data-point-selection', event, chartContext, config)
    }
  }
})
</script>
<style scoped lang="scss">
.chart-dashboard-style {
  align-items: center;
  display: flex;
  height: 100%;
  justify-content: center;
  margin-top: 1rem;
  width: 100%;

  .apexchart-el {
    height: auto;
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .chart-dashboard-style {
    margin-top: 0.5rem;
  }
}
</style>
