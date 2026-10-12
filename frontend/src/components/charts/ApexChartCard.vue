<template>
  <v-card class="dp-card custom-admin-dashboard-card card-height dp-chart-card">
    <div class="custom-admin-dashboard-content-div">
      <div class="top-status-div">
        <h3 class="heading-1">{{ title }}</h3>
        <p>{{ value }}</p>
      </div>
      <slot name="chart">
        <div v-if="chartOptions && series" class="chart-div-admin-dashboard">
          <apexchart
            :type="chartType"
            :options="chartOptions"
            :series="series"
            :rotate="360"
            :size="chartSize"
            :width="chartWidth"
            :height="chartHeight"
            @data-point-selection="handleDataPointSelection"
          ></apexchart>
        </div>
      </slot>
      <slot></slot>
    </div>
  </v-card>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import apexchart from 'vue3-apexcharts'

export default defineComponent({
  name: 'ApexChartCard',
  components: { apexchart },
  props: {
    title: {
      type: String,
      required: true
    },
    value: {
      type: [String, Number],
      required: true
    },
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
  methods: {
    handleDataPointSelection(event: any, chartContext: any, config: any) {
      this.$emit('data-point-selection', event, chartContext, config)
    }
  }
})
</script>
