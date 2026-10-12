// Main Vue Component for Survey Summary Page
<template>
  <v-container class="py-4">
    <!-- Header Section -->
    <v-card class="mb-4">
      <v-card-title>
        {{ surveyData.title }}
        <v-spacer></v-spacer>
        <span>{{ surveyData.purpose }}</span>
      </v-card-title>
      <v-card-subtitle>
        Total Participants: {{ surveyData.metadata.totalParticipants }} | Start Date:
        {{ surveyData.metadata.startDate }} | End Date: {{ surveyData.metadata.endDate }} | Response Rate:
        {{ surveyData.metadata.responseRate }}%
      </v-card-subtitle>
    </v-card>

    <!-- Overall Summary Cards -->
    <v-row class="mb-4" dense>
      <v-col v-for="(card, index) in summaryCards" :key="index" cols="12" md="3">
        <v-card outlined>
          <v-card-title>{{ card.title }}</v-card-title>
          <v-card-subtitle>{{ card.value }}</v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <!-- Participation Trends Chart -->
    <v-card class="mb-4">
      <v-card-title>Participation Trends</v-card-title>
      <v-card-subtitle>Response activity over time</v-card-subtitle>
      <LineChart :data="surveyData.participationTrends" />
    </v-card>

    <!-- Question-by-Question Analysis -->
    <v-expansion-panels>
      <v-expansion-panel v-for="(question, index) in surveyData.questions" :key="index">
        <v-expansion-panel-title> {{ question.title }} ({{ question.type }})</v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row>
            <v-col cols="12" md="6">
              <BarChart v-if="question.type === 'Multiple Choice'" :data="question.chartData" />
              <PieChart v-else-if="question.type === 'Rating Scale'" :data="question.chartData" />
            </v-col>
            <v-col cols="12" md="6">
              <h3>Insights</h3>
              <ul>
                <li v-for="(insight, insightIndex) in question.insights" :key="insightIndex">
                  {{ insight }}
                </li>
              </ul>
            </v-col>
          </v-row>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>

    <!-- Filters and Sorting Options -->
    <v-card class="my-4">
      <v-card-title>Filters and Sorting Options</v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="4">
            <v-select v-model="filters.questionType" label="Filter by Question Type" :items="questionTypes"></v-select>
          </v-col>
          <v-col cols="12" md="4">
            <v-select
              v-model="filters.demographic"
              label="Filter by Demographics"
              :items="surveyData.demographics"
            ></v-select>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- Export and Share Section -->
    <v-card class="my-4">
      <v-card-title>Export and Share</v-card-title>
      <v-card-text>
        <v-btn color="primary" @click="exportReport('pdf')">Export as PDF</v-btn>
        <v-btn color="primary" @click="exportReport('excel')">Export as Excel</v-btn>
        <v-btn color="primary" @click="exportReport('csv')">Export as CSV</v-btn>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script lang="ts">
import LineChart from '@/components/charts/LineChart.vue'
import BarChart from '@/components/charts/BarChart.vue'
import PieChart from '@/components/charts/PieChart.vue'
// import WordCloud from '@/components/WordCloud.vue'

export default {
  name: 'SurveySummaryPage',
  components: { LineChart, BarChart, PieChart },
  data() {
    return {
      surveyData: {
        title: 'Customer Feedback Survey Results',
        purpose: 'Analyze customer satisfaction.',
        metadata: {
          totalParticipants: 500,
          startDate: '2024-01-01',
          endDate: '2024-01-15',
          responseRate: 80
        },
        participationTrends: [
          { date: '2024-01-01', responses: 20 },
          { date: '2024-01-02', responses: 50 }
        ],
        questions: [
          {
            title: 'How satisfied are you with our service?',
            type: 'Rating Scale',
            chartData: [
              { label: 'Very Satisfied', value: 150 },
              { label: 'Satisfied', value: 200 },
              { label: 'Neutral', value: 50 },
              { label: 'Dissatisfied', value: 50 }
            ],
            insights: ['80% of users are satisfied or very satisfied', '10% of users are dissatisfied']
          }
        ],
        demographics: ['Age', 'Gender', 'Region']
      },
      filters: {
        questionType: null,
        demographic: null
      },
      summaryCards: [
        { title: 'Total Responses', value: 400 },
        { title: 'Average Satisfaction Score', value: '4.5 / 5' },
        { title: 'Highest Scoring Question', value: 'Customer Service Quality' },
        { title: 'Lowest Scoring Question', value: 'Product Availability' }
      ],
      questionTypes: ['Multiple Choice', 'Rating Scale', 'Open-Ended']
    }
  },
  methods: {
    exportReport(format) {
      console.log(`Exporting report in ${format} format`)
    }
  }
}
</script>
