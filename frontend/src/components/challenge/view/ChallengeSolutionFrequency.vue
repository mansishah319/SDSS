<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<!-- SolutionFrequency.vue -->
<template>
  <v-card class="dp-card dp-card-height">
    <v-row align="center" justify="space-between">
      <v-col cols="auto">
        <h4 class="green-heading font-bukra-bold">
          {{ $t('challenge.viewInfo.solutionFrequency') }}
        </h4>
      </v-col>
      <v-col cols="auto">
        <v-tooltip location="top">
          <template #activator="{ props }">
            <v-btn
              color="#d32f2f"
              elevation="2"
              v-bind="props"
              class="download-pdf-btn custom-btn-pdf-style"
              @click="downloadRoundPdf"
            >
              <v-icon size="x-large" color="white">mdi-file-pdf-box</v-icon>
              <v-icon size="large" color="white">mdi mdi-download</v-icon>
            </v-btn>
          </template>
          <span>{{ $t('common.downloadPdf') }}</span>
        </v-tooltip>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12" md="12">
        <BarChart :chart-data="chartData" :options="chartOptions" />
      </v-col>
    </v-row>
  </v-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { BarChart } from 'vue-chart-3'
import { Chart, registerables } from 'chart.js'
import { URIS } from '@/constants/DpConstants.ts'
import { download, get } from '@/utils/GenericApiHelper.ts'
import type { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'

Chart.register(...registerables)

const { t } = useI18n()

const props = defineProps(['challengeId'])
const chartLabels = ref(t('challenge.viewInfo.numberOfVotes'))

const chartData = ref({
  labels: [],
  datasets: [
    {
      label: chartLabels.value,
      data: [],
      backgroundColor: '#008755',
      borderColor: '#008755',
      borderWidth: 1,
      borderRadius: 4,
      pointStyle: 'circle'
    }
  ]
})
const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      beginAtZero: true
    }
  }
})

const downloadRoundPdf = async () => {
  await download(
    URIS.CHALLENGES.DOWNLOAD_PDF_FILE(Number(props.challengeId)),
    `survey-report-round-${props.challengeId}.pdf`
  )
}

onMounted(async () => {
  try {
    const response = await get<SuccessResponse>(URIS.VOTING.BASE + URIS.VOTING.RESULT + `${props.challengeId}`)
    // Update chartData with the response data
    chartData.value.labels = response.data.solutionTextList
    chartData.value.datasets[0].data = response.data.countOfVotes
  } catch (error) {
    console.error('Error fetching chart data:', error)
  }
})
</script>
