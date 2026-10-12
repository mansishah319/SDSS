<template lang="html">
  <v-container>
    <v-row class="mb-4">
      <v-col cols="auto">
        <h1
          class="custom-report-view-user-answers-main-title-h1 d-inline-block font-dubai-bold custom-header-common-color"
        >
          {{ $t('sdssSurvey.report.surveyDetails.main-title') }}
        </h1>
      </v-col>
    </v-row>

    <!-- Survey Details Card -->
    <v-card variant="outlined" class="custom-user-questions-and-answers-report-view-main-card">
      <v-card-title class="custom-user-questions-and-answers-report-view-card-title font-bukra-bold">
        <h1 class="custom-pdf-report-table-header custom-header-common-color">
          {{ $t('sdssSurvey.report.surveyDetails.title') }}
        </h1>
      </v-card-title>
      <v-card-text class="custom-user-questions-and-answers-report-view-card-text justify-space-between">
        <v-row>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.report.surveyDetails.surveyTitle') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundDetails.localTitle }}
              </h1>
            </v-col>
          </v-col>
          <v-col v-show="roundDetails.localDescription" cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.report.surveyDetails.surveyDescription') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundDetails.localDescription }}
              </h1>
            </v-col>
          </v-col>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.report.surveyDetails.roundTitle') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundDetails.localTitle }}
              </h1>
            </v-col>
          </v-col>
          <v-col v-show="roundDetails.localDescription" cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.report.surveyDetails.roundDescription') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundDetails.localDescription }}
              </h1>
            </v-col>
          </v-col>
        </v-row>
        <v-divider :thickness="2" color="info"></v-divider>
        <v-row>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('common.createdBy') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundDetails.createdBy }}
              </h1>
            </v-col>
          </v-col>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-title">
                {{ $t('common.createdAt') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundDetails.createdAt }}
              </h1>
            </v-col>
          </v-col>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-title">
                {{ $t('common.updatedBy') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">{{ roundDetails.updatedBy }}</h1>
            </v-col>
          </v-col>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-title">
                {{ $t('common.updatedAt') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">{{ roundDetails.updatedAt }}</h1>
            </v-col>
          </v-col>
        </v-row>
        <v-divider :thickness="2" color="info"></v-divider>
        <v-row>
          <v-col v-show="roundDetails.startTime" v-if="roundDetails.startTime" cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.report.surveyDetails.currentUserGrpNumber') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundDetails.startTime }}
              </h1>
            </v-col>
          </v-col>

          <v-col v-show="roundDetails.endTime" v-if="roundDetails.endTime" cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.report.surveyDetails.currentUserName') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundDetails.endTime }}
              </h1>
            </v-col>
          </v-col>

          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.status') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <v-chip :color="roundDetails.active ? 'green' : 'red'">
                <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                  {{ roundDetails.active ? $t('common.active') : $t('common.notActive') }}
                </h1>
              </v-chip>
            </v-col>
          </v-col>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.mode') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">{{ roundDetails.localStatus }}</h1>
            </v-col>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    <v-col cols="12"></v-col>
    <v-card variant="outlined" class="custom-user-questions-and-answers-report-view-main-card">
      <v-card-title class="custom-user-questions-and-answers-report-view-card-title font-bukra-bold">
        <h1 class="font-dubai-bold custom-pdf-report-table-content-title font-dubai-bold custom-header-common-color">
          {{ $t('sdssSurvey.report.surveyDetails.questionDetails') }}
        </h1>
      </v-card-title>
      <v-card-text class="custom-user-questions-and-answers-report-view-card-text justify-space-between">
        <v-row>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.report.surveyDetails.totalQuestions') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundReportResponse.numberOfQuestions }}
              </h1>
            </v-col>
          </v-col>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.report.surveyDetails.totalTextQuestions') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundReportResponse.totalTextQuestions }}
              </h1>
            </v-col>
          </v-col>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.report.surveyDetails.totalMultiSelectQuestions') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundReportResponse.totalMultipleChoiceQuestions }}
              </h1>
            </v-col>
          </v-col>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.report.surveyDetails.totalSingleSelectQuestions') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundReportResponse.totalSingleSelectQuestions }}
              </h1>
            </v-col>
          </v-col>
        </v-row>
        <v-divider :thickness="2" color="info"></v-divider>
        <v-row>
          <v-col cols="3">
            <v-col cols="12">
              <h1 class="font-dubai-bold custom-pdf-report-table-content-title">
                {{ $t('sdssSurvey.report.surveyDetails.totalTrueFalseQuestions') }}
              </h1>
            </v-col>
            <v-col cols="12">
              <h1 class="font-dubai-regular custom-pdf-report-table-content-body">
                {{ roundReportResponse.totalTrueFalseQuestions }}
              </h1>
            </v-col>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    <v-card>
      <v-card-title class="custom-user-questions-and-answers-report-view-card-title">
        <h1 class="font-dubai-bold custom-pdf-report-table-content-title custom-header-common-color">
          {{ $t('sdssSurvey.report.questionDetails') }}
        </h1>
      </v-card-title>
      <v-card-text class="custom-user-questions-and-answers-report-dashboard-view-card-text">
        <QuestionReportsPdf
          v-if="roundReportResponse.questions && roundReportResponse.questions.length > 0"
          :question-list="roundReportResponse.questions"
          :count-all-invited-users="roundReportResponse.totalNumberOfInvitedUsers"
        ></QuestionReportsPdf>
        <EmptyDataTable
          v-else
          :image-size="128"
          :message="$t('Messages.messageForEmptySurveyQuestionsIssue')"
          text-color="red"
        />
      </v-card-text>
    </v-card>
    <!-- Existing code for Survey Details Card and QuestionReports -->
    <ReportEmployeeInfoPreview :employee-list="roundReportResponse.surveyUsers" />
    <ReportUserQuestionAnswerPreview :user-question-answers="roundReportResponse.usersSurveyAnswers" />
  </v-container>
  <FooterPdfReportComponent :justify-content="'justify-content-start'"></FooterPdfReportComponent>
</template>

<script lang="ts">
import { PropType } from 'vue'
import { RoundReportMainResponse } from '@/dto/surveys/round/RoundReportMainResponse'
import { ReportSdssSurveyRoundResponse } from '@/dto/report/ReportSdssSurveyRoundResponse'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import QuestionReportsPdf from '@/reports/questions/QuestionReportsPdf.vue'
import FooterPdfReportComponent from '@/reports/component/FooterReportComponent.vue'
import ReportEmployeeInfoPreview from '@/reports/users/ReportEmployeeInfoPreview.vue'
import ReportUserQuestionAnswerPreview from '@/reports/answers/ReportUserQuestionAnswerPreview.vue'

export default {
  name: 'SurveyReportDownloadPdf',
  components: {
    ReportUserQuestionAnswerPreview,
    ReportEmployeeInfoPreview,
    FooterPdfReportComponent,
    QuestionReportsPdf,
    EmptyDataTable
  },
  props: {
    roundDetails: {
      type: Object as PropType<RoundReportMainResponse>,
      required: true
    },
    roundReportResponse: {
      type: Object as PropType<ReportSdssSurveyRoundResponse>,
      required: true
    }
  },
  setup() {
    return {}
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/css/variables' as *;

.custom-user-questions-and-answers-report-view-card-title {
  background-color: #0da06e;
  color: $white-color;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.custom-report-view-user-answers-chips-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.custom-report-view-user-answers-v-chip {
  width: 100%;
  white-space: normal;
  word-wrap: break-word;
  overflow: hidden;
  word-break: break-word;
  padding: 8px;
}

.custom-report-view-user-answers-scrollable-text {
  max-height: 150px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.custom-report-view-user-answers-main-title-h1 {
  font-size: 1.5rem;
  margin-top: 4%;
  color: black;
}

.custom-user-questions-and-answers-report-dashboard-view-card-text {
  width: 95%;
  justify-self: center;
}

.circle-number {
  display: inline-block;
  width: 30px; /* Customize the size */
  height: 30px; /* Customize the size */
  text-align: center;
  line-height: 30px; /* Center the number vertically */
  background-color: #008755; /* Customize the background color */
  color: white; /* Customize the text color */
  border-radius: 50%; /* Makes it a circle */
  font-weight: bold; /* Optional: Make the number bold */
}

.download-pdf-btn,
.export-excel-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  white-space: nowrap;
  margin-top: 5%;
}

.custom-font-style-for-pdf-title {
  color: $white-color;
}

.custom-user-questions-and-answers-report-view-card-text {
  background: #fff;
  border: none !important;
}

.custom-user-questions-and-answers-report-view-main-card {
  border: none !important;
  border-radius: 12px;
}
</style>
