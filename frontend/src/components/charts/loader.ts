import { defineAsyncComponent } from 'vue'

// Async chart components with loading states
export const PieChart = defineAsyncComponent({
  loader: () => import(/* webpackChunkName: "chart-pie" */ './PieChart.vue'),
  loadingComponent: () => import(/* webpackChunkName: "chart-loading" */ '@/components/common/LoadingSpinner.vue'),
  delay: 200,
  timeout: 5000
})

export const BarChart = defineAsyncComponent({
  loader: () => import(/* webpackChunkName: "chart-bar" */ './BarChart.vue'),
  loadingComponent: () => import(/* webpackChunkName: "chart-loading" */ '@/components/common/LoadingSpinner.vue'),
  delay: 200,
  timeout: 5000
})

export const LineChart = defineAsyncComponent({
  loader: () => import(/* webpackChunkName: "chart-line" */ './LineChart.vue'),
  loadingComponent: () => import(/* webpackChunkName: "chart-loading" */ '@/components/common/LoadingSpinner.vue'),
  delay: 200,
  timeout: 5000
})

export const CustomApexChart = defineAsyncComponent({
  loader: () => import(/* webpackChunkName: "chart-apex" */ './CustomApexChart.vue'),
  loadingComponent: () => import(/* webpackChunkName: "chart-loading" */ '@/components/common/LoadingSpinner.vue'),
  delay: 200,
  timeout: 5000
})

// Utility function to preload all chart components
// export const preloadCharts
// PieChart
// BarChart
// LineChart
// CustomApexChart
//

// Load chart library dynamically
export const loadChartLibrary = async () => {
  const { Chart, registerables } = await import(/* webpackChunkName: "chart-js" */ 'chart.js')
  Chart.register(...registerables)
  return Chart
}
