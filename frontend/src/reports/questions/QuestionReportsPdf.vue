<template lang="html">
  <v-row class="mt-6">
    <v-col v-for="question in questionList" :key="question.id" cols="12">
      <v-card variant="outlined" class="custom-question-report-view-user-answers-card flex-column">
        <v-card-title class="custom-question-report-view-user-answers-card-title font-bukra-bold">
          <div class="report-questions-title-container">
            <h1 class="font-dubai-bold custom-pdf-report-table-content-title custom-header-common-color">
              {{ question.localTitle }}
            </h1>

            <div class="report-question-tooltip-container">
              <v-tooltip location="top">
                <template #activator="{ props }">
                  <!-- Chart as header -->
                  <CustomProgressCircular
                    :percentage="getPercentageDirectly(question.userAnswersStatistics.totalCount, countAllInvitedUsers)"
                    v-bind="props"
                    :custom-size="60"
                    :custom-width="5"
                    custom-progress-class="circle-invited-user-answered-number"
                    color="black"
                  />
                </template>
                <span>{{ $t('sdssSurvey.report.userPercentageMessage') }}</span>
              </v-tooltip>
              <v-tooltip location="top">
                <template #activator="{ props }">
                  <span class="circle-invited-user-answered-number" v-bind="props">
                    {{ question.userAnswersStatistics.totalCount }} / {{ countAllInvitedUsers }}
                  </span>
                </template>
                <span>{{ $t('sdssSurvey.report.userTotalNumbersMessage') }}</span>
              </v-tooltip>
            </div>
          </div>
        </v-card-title>
        <v-card-subtitle class="custom-question-report-view-user-answers-card-sub-title font-bukra-regular">
          <h1 class="custom-question-report-view-user-answers-card-sub-title-span custom-pdf-report-table-content-body">
            {{ question.localDescription }}
          </h1>
        </v-card-subtitle>
        <v-card-text class="custom-question-report-view-user-answers-card-text flex-grow-1 font-bukra-regular">
          <!-- Multi-Select and Single-Choice -->
          <template
            v-if="
              question?.questionTypeKey.trim().toLowerCase() === 'multiple-choice' ||
              question?.questionTypeKey.trim().toLowerCase() === 'single-choice'
            "
          >
            <v-row>
              <v-col
                v-for="option in question.options"
                :key="option.id"
                cols="12"
                sm="4"
                md="4"
                lg="4"
                class="d-flex align-center custom-question-report-view-user-answers-card-option-col"
              >
                <!-- Chart as header -->
                <CustomProgressCircular
                  :percentage="getPercentage(option, question.userAnswersStatistics, question)"
                  :custom-size="100"
                  :custom-width="10"
                  color="#008755"
                />

                <!-- Text under the chart -->
                <h1
                  class="font-dubai-regular text-center mt-2 custom-text custom-text-block-view custom-pdf-report-table-content-body"
                >
                  {{ option.localOption }}
                </h1>
              </v-col>
            </v-row>
          </template>

          <template v-if="question?.questionTypeKey.trim().toLowerCase() === 'true-false'">
            <v-row class="custom-question-report-chart-view">
              <CustomApexChart
                :chart-options="getTrueFalseChartOptions(question.userAnswersStatistics)"
                :series="[question.userAnswersStatistics.trueCount, question.userAnswersStatistics.falseCount]"
                :chart-height="250"
                @data-point-selection="handleDataPointSelection"
              />
            </v-row>
          </template>
          <!-- Text Answers -->
          <template v-else-if="question?.questionTypeKey.trim().toLowerCase() === 'text'">
            <p class="custom-question-report-view-user-answers-scrollable-text font-dubai-regular">
              <!-- Show the card of chart -->
            </p>
          </template>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>
<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { ReportSdssSurveyQuestionResponse } from '@/dto/report/ReportSdssSurveyRoundResponse'
import CustomProgressCircular from '@/components/charts/CustomProgressCircular.vue'
import CustomApexChart from '@/components/charts/CustomApexChart.vue'
import { useI18n } from 'vue-i18n'

export default defineComponent({
  name: 'QuestionReportsPdf',
  components: { CustomApexChart, CustomProgressCircular },
  props: {
    questionList: {
      type: Array as PropType<ReportSdssSurveyQuestionResponse[]>,
      default: () => []
    },
    countAllInvitedUsers: {
      type: Number,
      default: 1
    }
  },
  setup() {
    const { t } = useI18n()
    const handleDataPointSelection = (_, chartContext: any, config: any) => {
      console.log(chartContext, config)
      const dataPoint = config.dataPointIndex
      if (dataPoint === 0) {
        console.log('Handle in true answers')
      } else if (dataPoint === 1) {
        console.log('Handle in false answers')
      }
    }

    const getTrueFalseChartOptions = (stats: any): any => {
      return {
        labels: [t('common.true'), t('common.false')],
        colors: ['#008755', '#FF0000'],
        legend: { position: 'bottom' }
      }
    }

    return {
      handleDataPointSelection,
      getTrueFalseChartOptions
    }
  },
  methods: {
    getPercentage(option: any, stats: any, question: any): number {
      const optionStats = stats?.optionStatistics?.find((stat: any) => stat.id === option.id)
      const count = optionStats?.count || 0
      const totalUsersAnswered = question?.usersSurveyAnswers?.length || stats?.totalCount || 1
      return Math.round((count / totalUsersAnswered) * 100)
    },
    getPercentageDirectly(count: any, total: any): number {
      return Math.round((count / total) * 100)
    }
  }
})
</script>
<style scoped lang="scss">
@use '@/assets/css/variables' as *;

.custom-question-report-view-user-answers-card {
  border-radius: 1.5rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: auto;
  min-height: 25rem;
  max-height: 40rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.custom-question-report-view-user-answers-card-title {
  background-color: $sdss-questions-color;
  color: $white-color;
  font-size: 1rem;
  overflow-wrap: break-word;
  text-overflow: ellipsis;
  white-space: normal;

  .report-questions-title-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
}

.custom-question-report-view-user-answers-card-sub-title {
  background-color: $sdss-description-color;
  font-style: italic;
}

.rtl .custom-question-report-view-user-answers-card-sub-title-span {
  padding-right: 2%;
}

.ltr .custom-question-report-view-user-answers-card-sub-title-span {
  padding-right: 2%;
}

.custom-question-report-view-user-answers-card-text {
  align-self: center;
  width: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 1rem;
}

@media (max-width: 768px) {
  .custom-question-report-view-user-answers-card {
    margin-bottom: 16px;
  }
}

.custom-question-report-chart-view {
  text-align: center;
  justify-self: center;
}

.custom-question-report-view-user-answers-card-option-col {
  flex-direction: column;
}

.custom-text-block-view {
  display: block;
}

.circle-invited-user-answered-number {
  display: inline-block;
  width: 60px;
  height: 60px;
  text-align: center;
  line-height: 60px;
  background-color: $sdss-primary-color;
  color: $white-color;
  border-radius: 50%;
  font-weight: bold;
}

.report-question-tooltip-container {
  display: flex;
  gap: 1rem; /* Adjust spacing between the tooltips */
  align-items: center;
}

.circle-invited-user-public-answered-number {
  display: inline-block;
  width: 60px;
  height: 60px;
  text-align: center;
  line-height: 60px;
  background-color: $multiple-choice-color;
  color: $white-color;
  border-radius: 50%;
  font-weight: bold;
}
</style>
