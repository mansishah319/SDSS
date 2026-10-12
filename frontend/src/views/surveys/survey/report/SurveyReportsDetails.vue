<template lang="html">
  <div class="survey-report-page-shell">
    <ContentLoader
      v-if="pdfLoading"
      class="survey-report-page-shell__pdf-loader"
      variant="details"
      overlay
      compact-badge
      :heading="$t('common.downloadPdf')"
      :message="$t('common.loader.surveyReportDetails')"
      :description="$t('common.loader.surveyReportDetailsDescription')"
    />

    <v-container class="survey-report-page">
      <div
        id="survey-report-export-root"
        ref="reportExportRoot"
        class="survey-report-export-root"
        :class="isArabic ? 'survey-report-export-root--rtl' : 'survey-report-export-root--ltr'"
        :dir="isArabic ? 'rtl' : 'ltr'"
      >
        <section class="report-hero" :class="{ 'report-hero--rtl': employeeLang === 'ar' }">
          <div class="report-hero__main">
            <v-btn icon class="report-hero__back" aria-label="Back" @click="goBack">
              <v-icon>{{ employeeLang === 'ar' ? 'mdi-arrow-right' : 'mdi-arrow-left' }}</v-icon>
            </v-btn>
            <div class="report-hero__copy">
              <p class="report-hero__eyebrow">{{ $t('sdssSurvey.report.surveyDetails.main-title') }}</p>
              <h1 class="report-hero__title font-bukra-bold">
                {{ roundDetails.localTitle || roundReportResponse.localTitle }}</h1>
              <p class="report-hero__subtitle">
                {{ roundDetails.localDescription || roundDetails.survey?.localDescription || $t('common.loading') }}
              </p>
            </div>
          </div>

          <div class="report-hero__actions">
            <v-tooltip location="top">
              <template #activator="{ props }">
                <v-btn
                  aria-label="Export to Excel"
                  icon
                  color="white"
                  variant="flat"
                  v-bind="props"
                  class="report-hero__action-btn"
                  :loading="exportLoading"
                  @click="exportToExcel"
                >
                  <v-icon>mdi-file-excel</v-icon>
                </v-btn>
              </template>
              <span>{{ $t('common.exportToExcel') }}</span>
            </v-tooltip>

            <v-tooltip location="top">
              <template #activator="{ props }">
                <v-btn
                  aria-label="Download PDF"
                  icon
                  color="white"
                  variant="outlined"
                  v-bind="props"
                  class="report-hero__action-btn report-hero__action-btn--ghost"
                  :loading="pdfLoading"
                  @click="downloadRoundPdf"
                >
                  <v-icon>mdi-file-pdf-box</v-icon>
                </v-btn>
              </template>
              <span>{{ $t('common.downloadPdf') }}</span>
            </v-tooltip>
          </div>
        </section>

        <DubaiPoliceLoader
          :is-loading="!isDataLoaded"
          class="global-loader"
          variant="badge"
          :heading="$t('common.loader.surveyReportDetailsHeading')"
          :message="$t('common.loader.surveyReportDetails')"
          :description="$t('common.loader.surveyReportDetailsDescription')"
        />

        <template v-if="isDataLoaded">
          <section class="report-section">
            <v-card class="report-card">
              <div class="report-card__header">
                <div>
                  <p class="report-card__eyebrow">{{ $t('sdssSurvey.report.surveyDetails.title') }}</p>
                  <h2 class="report-card__title font-bukra-bold">{{ $t('sdssSurvey.report.surveyDetails.title') }}</h2>
                </div>
              </div>
              <v-card-text class="report-card__body">
                <div class="detail-grid detail-grid--four">
                  <article v-for="item in surveySummaryItems" :key="item.label" class="detail-block">
                    <span class="detail-block__label">{{ item.label }}</span>
                    <span class="detail-block__value">{{ item.value }}</span>
                  </article>
                </div>
              </v-card-text>
            </v-card>
          </section>

          <section class="report-section">
            <v-card class="report-card">
              <div class="report-card__header">
                <div>
                  <p class="report-card__eyebrow">{{ $t('sdssSurvey.report.questionDetails') }}</p>
                  <h2 class="report-card__title font-bukra-bold">
                    {{ $t('sdssSurvey.report.surveyDetails.questionDetails') }}</h2>
                </div>
              </div>
              <v-card-text class="report-card__body">
                <div class="detail-grid detail-grid--stats">
                  <article v-for="item in questionSummaryItems" :key="item.label" class="metric-card">
                    <span class="metric-card__label">{{ item.label }}</span>
                    <strong class="metric-card__value">{{ item.value }}</strong>
                  </article>
                </div>
              </v-card-text>
            </v-card>
          </section>

          <section class="report-section report-section--questions">
            <v-card class="report-card report-card--questions">
              <div class="report-card__header">
                <div>
                  <p class="report-card__eyebrow">{{ $t('sdssSurvey.report.questionDetails') }}</p>
                  <h2 class="report-card__title font-bukra-bold">
                    {{ $t('sdssSurvey.report.questionDetails') }}
                  </h2>
                  <p class="report-card__description">
                    {{ $t('sdssSurvey.report.surveyDetails.questionDetails') }}
                  </p>
                </div>
                <v-chip size="small" variant="flat" color="primary">{{ roundReportResponse.numberOfQuestions }}</v-chip>
              </div>
              <v-card-text class="report-card__body report-card__body--compact">
                <QuestionReports
                  v-if="roundReportResponse.questions && roundReportResponse.questions.length > 0"
                  ref="questionReportsRef"
                  :question-list="roundReportResponse.questions"
                  :count-all-invited-users="roundReportResponse.totalNumberOfInvitedUsers"
                  :round-id="roundId"
                  :expand-all-questions="forceExpandAllQuestions"
                />
                <EmptyDataTable
                  v-else
                  :image-size="128"
                  :message="$t('Messages.messageForEmptySurveyQuestionsIssue')"
                  text-color="red"
                />
              </v-card-text>
            </v-card>
          </section>

          <section class="report-section report-section--stacked">
            <EmployeeInfoPreview
              :employee-list="roundReportResponse.surveyUsers"
              :export-mode="pdfLoading || forceExpandAllQuestions"
            />
          </section>
        </template>
      </div>
    </v-container>
  </div>
</template>

<script lang="ts">
import { computed, nextTick, onMounted, ref, watchEffect } from 'vue'
import { buildPdfExportStyles, exportElementToPdf } from '@/utils/pdfExportService'
import { get } from '@/utils/GenericApiHelper'
import { SuccessResponse } from '@/dto/generic/SuccessResponse'
import { Characters, Fields, SecurityConstants, URIS } from '@/constants/DpConstants'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import { RoundReportMainResponse } from '@/dto/surveys/round/RoundReportMainResponse'
import {
  ReportSdssSurveyQuestionResponse,
  ReportSdssSurveyRoundResponse,
  SurveyUser
} from '@/dto/report/ReportSdssSurveyRoundResponse'
import QuestionReports from '@/views/surveys/survey/report/questions/QuestionReports.vue'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import EmployeeInfoPreview from '@/views/surveys/survey/report/users/EmployeeInfoPreview.vue'
import { useRouter } from 'vue-router'
import DubaiPoliceLoader from '@/components/common/Loader.vue'
import ContentLoader from '@/components/loader/ContentLoader.vue'
import {
  buildPdfFileName,
  createCommonPdfCloneHandler,
  createPdfFontResources,
  sanitizePdfFileSegment,
  sanitizeUnsupportedColorFunctionsInClone
} from '@/utils/pdfExportUtils'
import { buildTimestampedExcelFileName, exportWorkbookToExcel } from '@/utils/excelExportUtils'

const pdfScopeSelector = '#survey-report-export-root'
const bukraRegularFontUrl = new URL('../../../../assets/fonts/Bukra/TTF/29ltbukraregular.ttf', import.meta.url).href
const bukraBoldFontUrl = new URL('../../../../assets/fonts/Bukra/TTF/29ltbukrabold.ttf', import.meta.url).href
const dubaiRegularFontUrl = new URL('../../../../assets/fonts/Dubai/DubaiRegular.ttf', import.meta.url).href

export default {
  name: 'SurveyReportsDetails',
  components: {
    ContentLoader,
    EmployeeInfoPreview,
    EmptyDataTable,
    QuestionReports,
    DubaiPoliceLoader
  },
  props: {
    roundId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const { locale, t } = useI18n()
    const router = useRouter()

    const employeeLang: string = localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'
    const token = ref(localStorage.getItem(SecurityConstants.TOKEN))
    const isDataLoaded = ref<boolean>(false)
    const exportLoading = ref<boolean>(false)
    const pdfLoading = ref<boolean>(false)
    const reportExportRoot = ref<HTMLElement | null>(null)
    const questionReportsRef = ref<{
      ensureQuestionDetailsLoaded?: () => Promise<void>
    } | null>(null)
    const forceExpandAllQuestions = ref<boolean>(false)

    const roundDetails = ref<RoundReportMainResponse>({} as RoundReportMainResponse)
    const roundReportResponse = ref<ReportSdssSurveyRoundResponse>({} as ReportSdssSurveyRoundResponse)
    const detailedQuestionReportMap = ref<Record<number, ReportSdssSurveyQuestionResponse>>({})
    const isArabic = computed(() => employeeLang === 'ar')
    const pdfFontResources = createPdfFontResources({
      bukraRegularFontUrl,
      bukraBoldFontUrl,
      dubaiRegularFontUrl
    })
    let detailedQuestionReportsPromise: Promise<ReportSdssSurveyQuestionResponse[]> | null = null

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    const pdfReportLabel = computed(() => t('sdssSurvey.report.surveyDetails.main-title'))
    const pdfLocalizedTitle = computed(
      () => roundDetails.value.localTitle || roundReportResponse.value.localTitle || t('sdssSurvey.report.surveyDetails.title')
    )

    const handlePdfClone = createCommonPdfCloneHandler({
      isArabic: () => isArabic.value,
      wordSpacingSelectors: [
        '.report-hero__eyebrow',
        '.report-hero__title',
        '.report-hero__subtitle',
        '.report-card__eyebrow',
        '.report-card__title',
        '.report-card__description',
        '.detail-block__label',
        '.detail-block__value',
        '.metric-card__label',
        '.metric-card__value',
        '.question-report-card__title',
        '.question-report-card__subtitle',
        '.question-panel__title',
        '.question-panel__eyebrow',
        '.answer-bar__label',
        '.answer-bar__value',
        '.metric-row__label',
        '.metric-row__value',
        '.text-answer-list__question',
        '.text-answer-list__value',
        '.pdf-employee-table th',
        '.pdf-employee-table td',
        '.pdf-grade-pill',
        '.pdf-status-pill'
      ],
      afterClone: ({ doc, clonedElement }) => {
        sanitizeUnsupportedColorFunctionsInClone(doc, clonedElement)
        // Set proper direction on the cloned table
        const table = clonedElement.querySelector('.pdf-employee-table')
        if (table) {
          if (isArabic.value) {
            table.setAttribute('dir', 'rtl')
          } else {
            table.setAttribute('dir', 'ltr')
          }
        }
      }
    })

    const pdfFileName = computed(() => {
      const reportName = sanitizePdfFileSegment(pdfReportLabel.value, 'survey-report')
      const title = sanitizePdfFileSegment(pdfLocalizedTitle.value, 'survey-report')

      return buildPdfFileName([reportName, title], 'survey-report')
    })

    const pdfInjectedStyle = computed(() => {
      return buildPdfExportStyles({
        scopeSelector: pdfScopeSelector,
        fontFaces: pdfFontResources.fontFaces,
        baseFontFamily: isArabic.value ? '\'DubaiRegular\', sans-serif' : '\'bukraregular\', \'DubaiRegular\', sans-serif',
        direction: isArabic.value ? 'rtl' : 'ltr',
        textAlign: isArabic.value ? 'right' : 'left',
        unicodeBidi: 'plaintext',
        autoHeightSelectors: [
          `${pdfScopeSelector}`,
          `${pdfScopeSelector} .report-section`,
          `${pdfScopeSelector} .report-card`,
          `${pdfScopeSelector} .report-card__body`,
          `${pdfScopeSelector} .detail-grid`,
          `${pdfScopeSelector} .question-report-list`,
          `${pdfScopeSelector} .question-report-card`,
          `${pdfScopeSelector} .question-report-card__content`,
          `${pdfScopeSelector} .question-report-card__insights`,
          `${pdfScopeSelector} .question-panel`,
          `${pdfScopeSelector} .question-panel__body`,
          `${pdfScopeSelector} .question-report-card__insights`,
          `${pdfScopeSelector} .survey-question-analysis`,
          `${pdfScopeSelector} .question-summary-grid`,
          `${pdfScopeSelector} .analysis-grid`,
          `${pdfScopeSelector} .analysis-card`,
          `${pdfScopeSelector} .analysis-card__body`,
          `${pdfScopeSelector} .insight-panel`,
          `${pdfScopeSelector} .text-answer-panel`,
          `${pdfScopeSelector} .text-answer-panel__preview`,
          `${pdfScopeSelector} .text-answer-panel__preview .v-field`,
          `${pdfScopeSelector} .text-answer-panel__preview textarea`,
          `${pdfScopeSelector} .pdf-employee-table-wrapper`,
          `${pdfScopeSelector} .pdf-employee-table`,
          `${pdfScopeSelector} .pdf-employee-table thead`,
          `${pdfScopeSelector} .pdf-employee-table tbody`,
          `${pdfScopeSelector} .pdf-employee-table tr`,
          `${pdfScopeSelector} .pdf-employee-table th`,
          `${pdfScopeSelector} .pdf-employee-table td`,
          `${pdfScopeSelector} .pdf-grade-pill`,
          `${pdfScopeSelector} .pdf-status-pill`,
          `${pdfScopeSelector} .apexcharts-canvas`,
          `${pdfScopeSelector} .apexcharts-svg`,
          `${pdfScopeSelector} .apexcharts-inner`,
          `${pdfScopeSelector} svg`
        ],
        avoidBreakSelectors: [
          `${pdfScopeSelector} .report-hero`,
          `${pdfScopeSelector} .report-card`,
          `${pdfScopeSelector} .detail-block`,
          `${pdfScopeSelector} .metric-card`,
          `${pdfScopeSelector} .question-report-card`,
          `${pdfScopeSelector} .question-panel`,
          `${pdfScopeSelector} .answer-bar`,
          `${pdfScopeSelector} .metric-row`,
          `${pdfScopeSelector} .text-answer-list__item`
        ],
        hideSelectors: [
          `${pdfScopeSelector} .report-hero__actions`,
          `${pdfScopeSelector} .report-hero__back`,
          `${pdfScopeSelector} .question-report-card__action`,
          `${pdfScopeSelector} .question-page-header__back`,
          `${pdfScopeSelector} .content-loader`,
          `${pdfScopeSelector} .v-tooltip`,
          `${pdfScopeSelector} .v-overlay-container`
        ],
        customCss: `
        // Force font embedding and rendering
@page {
  size: A4;
  margin: 2cm;
}
${pdfScopeSelector} {
  background: #ffffff !important;
  color: #101828 !important;
  padding: 0 0 42px !important;
  -webkit-font-smoothing: antialiased !important;
  text-rendering: optimizeLegibility !important;
}

${pdfScopeSelector} .survey-report-page,
${pdfScopeSelector}.survey-report-page {
  gap: 16px !important;
}

// Ensure all text uses the correct font
${pdfScopeSelector},
${pdfScopeSelector} *,
${pdfScopeSelector} .v-chip,
${pdfScopeSelector} .v-chip__content,
${pdfScopeSelector} .pdf-employee-table th,
${pdfScopeSelector} .pdf-employee-table td,
${pdfScopeSelector} .report-card__title,
${pdfScopeSelector} .report-card__description,
${pdfScopeSelector} .detail-block__value,
${pdfScopeSelector} .metric-card__value {
  font-family: ${isArabic.value ? '\'DubaiRegular\', \'Segoe UI\', Tahoma, Arial, sans-serif' : '\'bukraregular\', \'DubaiRegular\', Arial, Helvetica, sans-serif'} !important;
  font-weight: normal !important;
}

// Fix for numbers and English text within Arabic
${pdfScopeSelector}.report-hero--rtl .pdf-employee-table td,
${pdfScopeSelector}.report-hero--rtl .detail-block__value,
${pdfScopeSelector}.report-hero--rtl .metric-card__value {
  direction: ltr !important;
  text-align: right !important;
  unicode-bidi: embed !important;
}

${pdfScopeSelector} .report-section,
${pdfScopeSelector} .report-card,
${pdfScopeSelector} .question-report-card__insights,
${pdfScopeSelector} .detail-grid,
${pdfScopeSelector} .question-report-card__content,
${pdfScopeSelector} .question-report-card__sidebar,
${pdfScopeSelector} .survey-question-analysis,
${pdfScopeSelector} .question-summary-grid,
${pdfScopeSelector} .analysis-grid,
${pdfScopeSelector} .analysis-card,
${pdfScopeSelector} .analysis-card__body,
${pdfScopeSelector} .insight-panel,
${pdfScopeSelector} .text-answer-panel,
${pdfScopeSelector} .text-answer-panel__preview,
${pdfScopeSelector} .text-answer-panel__preview .v-field,
${pdfScopeSelector} .text-answer-panel__preview textarea,
${pdfScopeSelector} .v-field__field,
${pdfScopeSelector} .v-field__input {
  overflow: visible !important;
  height: auto !important;
  min-height: 0 !important;
  max-height: none !important;
}

${pdfScopeSelector} .v-chip {
  display: inline-flex !important;
  width: auto !important;
  max-width: 100% !important;
  min-width: auto !important;
  height: auto !important;
  min-height: 32px !important;
  padding: 8px 16px !important;
  margin: 4px 0 !important;
  white-space: normal !important;
  word-wrap: break-word !important;
  word-break: break-word !important;
  overflow: visible !important;
  line-height: 1.4 !important;
  font-size: 0.875rem !important;
}

${pdfScopeSelector} .v-chip .v-chip__content {
  white-space: normal !important;
  word-wrap: break-word !important;
  overflow: visible !important;
  display: block !important;
  width: 100% !important;
  line-height: 1.4 !important;
}

${pdfScopeSelector} .report-card__header {
  align-items: flex-start !important;
  flex-wrap: wrap !important;
  gap: 12px !important;
}

${pdfScopeSelector} .report-card__header > div {
  flex: 1 !important;
  min-width: 0 !important;
}

${pdfScopeSelector} .report-card__header .v-chip {
  flex-shrink: 0 !important;
  margin-top: 8px !important;
  align-self: flex-start !important;
}

${pdfScopeSelector} .report-data-card__header {
  min-height: 106px !important;
}

${pdfScopeSelector} .report-data-card__header-content {
  align-items: center !important;
  min-height: 66px !important;
}

${pdfScopeSelector} .report-data-card__title-wrap {
  display: flex !important;
  align-items: center !important;
  align-self: stretch !important;
}

${pdfScopeSelector} .pdf-employee-table {
  width: 100% !important;
  border-collapse: collapse !important;
  table-layout: auto !important;
  background: #ffffff !important;
  margin: 0 !important;
  display: table !important;
}

${pdfScopeSelector} .pdf-employee-table-wrapper {
  width: 100% !important;
  overflow-x: visible !important;
  margin: 0 !important;
  padding: 0 !important;
  page-break-inside: avoid !important;
}

${pdfScopeSelector} .pdf-employee-table thead {
  display: table-header-group !important;
}

${pdfScopeSelector} .pdf-employee-table tbody {
  display: table-row-group !important;
}

${pdfScopeSelector} .pdf-employee-table tr {
  display: table-row !important;
  page-break-inside: avoid !important;
  break-inside: avoid !important;
}

${pdfScopeSelector} .pdf-employee-table th {
  background: #f8fafc !important;
  color: #344054 !important;
  -webkit-text-fill-color: #344054 !important;
  font-weight: 700 !important;
  font-size: 0.75rem !important;
  text-transform: uppercase !important;
  letter-spacing: 0.5px !important;
}

${pdfScopeSelector} .pdf-employee-table__header-cell {
  text-transform: none !important;
  letter-spacing: 0 !important;
}

${pdfScopeSelector} .pdf-employee-table__header-label {
  display: block !important;
  font-family: ${isArabic.value ? '\'DubaiRegular\', \'Segoe UI\', Tahoma, Arial, sans-serif' : '\'bukraregular\', \'DubaiRegular\', Arial, Helvetica, sans-serif'} !important;
  font-size: 0.78rem !important;
  font-weight: 400 !important;
  letter-spacing: 0 !important;
  line-height: 1.35 !important;
  text-transform: none !important;
  unicode-bidi: plaintext !important;
}

${pdfScopeSelector} .pdf-employee-table td {
  background: #ffffff !important;
  color: #101828 !important;
  -webkit-text-fill-color: #101828 !important;
}

${pdfScopeSelector} .pdf-employee-table th,
${pdfScopeSelector} .pdf-employee-table td {
  display: table-cell !important;
  padding: 12px 10px !important;
  vertical-align: middle !important;
  border-bottom: 1px solid #eaecf0 !important;
  word-wrap: break-word !important;
  word-break: break-word !important;
  white-space: normal !important;
  font-size: 0.8rem !important;
}

// LTR (English) - text align left
${pdfScopeSelector}:not(.report-hero--rtl) .pdf-employee-table th,
${pdfScopeSelector}:not(.report-hero--rtl) .pdf-employee-table td {
  text-align: left !important;
}

// RTL (Arabic) - text align right
${pdfScopeSelector}.report-hero--rtl .pdf-employee-table th,
${pdfScopeSelector}.report-hero--rtl .pdf-employee-table td,
${pdfScopeSelector} .report-hero--rtl ~ .survey-report-export-root .pdf-employee-table th,
${pdfScopeSelector} .report-hero--rtl ~ .survey-report-export-root .pdf-employee-table td {
  text-align: right !important;
}

${pdfScopeSelector}.report-hero--rtl .pdf-employee-table__header-label,
${pdfScopeSelector}[dir="rtl"] .pdf-employee-table__header-label {
  direction: rtl !important;
  text-align: right !important;
}

// Column widths - work for both LTR and RTL
${pdfScopeSelector} .pdf-employee-table th:nth-child(1),
${pdfScopeSelector} .pdf-employee-table td:nth-child(1) {
  width: 10% !important;
}

${pdfScopeSelector} .pdf-employee-table th:nth-child(2),
${pdfScopeSelector} .pdf-employee-table td:nth-child(2) {
  width: 18% !important;
}

${pdfScopeSelector} .pdf-employee-table th:nth-child(3),
${pdfScopeSelector} .pdf-employee-table td:nth-child(3) {
  width: 15% !important;
}

${pdfScopeSelector} .pdf-employee-table th:nth-child(4),
${pdfScopeSelector} .pdf-employee-table td:nth-child(4) {
  width: 18% !important;
}

${pdfScopeSelector} .pdf-employee-table th:nth-child(5),
${pdfScopeSelector} .pdf-employee-table td:nth-child(5) {
  width: 15% !important;
}

${pdfScopeSelector} .pdf-employee-table th:nth-child(6),
${pdfScopeSelector} .pdf-employee-table td:nth-child(6) {
  width: 10% !important;
}

${pdfScopeSelector} .pdf-employee-table th:nth-child(7),
${pdfScopeSelector} .pdf-employee-table td:nth-child(7) {
  width: 14% !important;
}

// Grade pill - works for both languages
${pdfScopeSelector} .pdf-grade-pill {
  display: inline-block !important;
  background: #f2f4f7 !important;
  border: 1px solid #d0d5dd !important;
  color: #344054 !important;
  padding: 4px 8px !important;
  border-radius: 16px !important;
  font-size: 0.75rem !important;
  font-weight: 500 !important;
  white-space: nowrap !important;
}

// Status pill - works for both languages
${pdfScopeSelector} .pdf-status-pill {
  display: inline-block !important;
  padding: 4px 10px !important;
  border-radius: 16px !important;
  font-size: 0.75rem !important;
  font-weight: 500 !important;
  white-space: nowrap !important;
}

${pdfScopeSelector} .pdf-status-pill--success {
  background: rgba(18, 183, 106, 0.12) !important;
  border: 1px solid rgba(18, 183, 106, 0.24) !important;
  color: #027a48 !important;
}

${pdfScopeSelector} .pdf-status-pill--warning {
  background: rgba(247, 144, 9, 0.12) !important;
  border: 1px solid rgba(247, 144, 9, 0.24) !important;
  color: #b54708 !important;
}

// RTL specific adjustments for pills
${pdfScopeSelector}.report-hero--rtl .pdf-grade-pill,
${pdfScopeSelector}.report-hero--rtl .pdf-status-pill {
  display: inline-block !important;
}

// Prevent page breaks inside rows
${pdfScopeSelector} .pdf-employee-table tr {
  page-break-inside: avoid !important;
  break-inside: avoid !important;
}

${pdfScopeSelector} .pdf-employee-table thead {
  display: table-header-group !important;
}

// Ensure proper overflow
${pdfScopeSelector} .report-section--stacked {
  overflow: visible !important;
}

// Responsive table for PDF - ensure it doesn't get squished
${pdfScopeSelector} .pdf-employee-table {
  min-width: 100% !important;
}

// Better text handling for Arabic
${pdfScopeSelector}.report-hero--rtl .pdf-employee-table th,
${pdfScopeSelector}.report-hero--rtl .pdf-employee-table td {
  font-family: 'DubaiRegular', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif !important;
}

${pdfScopeSelector}.report-hero--rtl .v-data-table-header__content span,
${pdfScopeSelector}[dir="rtl"] .v-data-table-header__content span {
  font-family: 'bukrabold', 'DubaiRegular', 'Segoe UI', Tahoma, Arial, sans-serif !important;
  font-weight: 700 !important;
  text-align: right !important;
}

// Force English table to use LTR when needed
${pdfScopeSelector}[dir="ltr"] .pdf-employee-table th,
${pdfScopeSelector}[dir="ltr"] .pdf-employee-table td {
  text-align: left !important;
  direction: ltr !important;
}

// Force Arabic table to use RTL
${pdfScopeSelector}[dir="rtl"] .pdf-employee-table th,
${pdfScopeSelector}[dir="rtl"] .pdf-employee-table td {
  text-align: right !important;
  direction: rtl !important;
}

${pdfScopeSelector} .v-expand-transition-enter-active,
${pdfScopeSelector} .v-expand-transition-leave-active,
${pdfScopeSelector} .v-expand-transition-enter-from,
${pdfScopeSelector} .v-expand-transition-leave-to {
  transition: none !important;
  height: auto !important;
}
`
      })
    })

    const rafAnimationFrame = () => new Promise(requestAnimationFrame)

    const waitForPdfRenderStability = async () => {
      await nextTick()
      await nextTick()
      await rafAnimationFrame()
      await rafAnimationFrame()
      await new Promise((r) => setTimeout(r, 250))
    }

    const surveySummaryItems = computed(() => {
      const details = roundDetails.value
      return [
        { label: t('sdssSurvey.report.surveyDetails.surveyTitle'), value: details?.survey?.localTitle || '-' },
        {
          label: t('sdssSurvey.report.surveyDetails.surveyDescription'),
          value: details?.survey?.localDescription || '-'
        },
        { label: t('sdssSurvey.report.surveyDetails.roundTitle'), value: details?.localTitle || '-' },
        { label: t('sdssSurvey.report.surveyDetails.roundDescription'), value: details?.localDescription || '-' },
        { label: t('common.createdBy'), value: details?.createdBy || '-' },
        { label: t('common.createdAt'), value: details?.createdAt || '-' },
        { label: t('common.updatedBy'), value: details?.updatedBy || '-' },
        { label: t('common.updatedAt'), value: details?.updatedAt || '-' },
        { label: t('sdssSurvey.status'), value: details?.active ? t('common.active') : t('common.notActive') },
        { label: t('sdssSurvey.mode'), value: details?.localStatus || '-' }
      ]
    })

    const questionSummaryItems = computed(() => [
      {
        label: t('sdssSurvey.report.surveyDetails.totalQuestions'),
        value: roundReportResponse.value.numberOfQuestions || 0
      },
      {
        label: t('sdssSurvey.report.surveyDetails.totalTextQuestions'),
        value: roundReportResponse.value.totalTextQuestions || 0
      },
      {
        label: t('sdssSurvey.report.surveyDetails.totalMultiSelectQuestions'),
        value: roundReportResponse.value.totalMultipleChoiceQuestions || 0
      },
      {
        label: t('sdssSurvey.report.surveyDetails.totalSingleSelectQuestions'),
        value: roundReportResponse.value.totalSingleSelectQuestions || 0
      },
      {
        label: t('sdssSurvey.report.surveyDetails.totalTrueFalseQuestions'),
        value: roundReportResponse.value.totalTrueFalseQuestions || 0
      },
      {
        label: t('sdssSurvey.report.employeeDetails'),
        value: roundReportResponse.value.totalNumberOfInvitedUsers || 0
      }
    ])

    const answeredEmployeeCount = computed(() => roundReportResponse.value.surveyUsers?.filter((item) => item.hasAnswered).length || 0)
    const pendingEmployeeCount = computed(
      () => (roundReportResponse.value.surveyUsers?.length || 0) - answeredEmployeeCount.value
    )
    const responseRate = computed(() => {
      const total = roundReportResponse.value.totalNumberOfInvitedUsers || 0
      if (!total) {
        return '0%'
      }

      return `${Math.round((answeredEmployeeCount.value / total) * 100)}%`
    })

    const toDisplayValue = (value: unknown) => {
      if (value === null || value === undefined || value === '') {
        return '-'
      }

      return value
    }

    const formatDateTime = (value: string | null | undefined) => {
      if (!value) {
        return '-'
      }

      const parsed = new Date(value)
      if (Number.isNaN(parsed.getTime())) {
        return value
      }

      return new Intl.DateTimeFormat(employeeLang === 'ar' ? 'ar-AE' : 'en-GB', {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }).format(parsed)
    }

    const getQuestionTotalResponses = (question: ReportSdssSurveyQuestionResponse) =>
      question?.userAnswersStatistics?.totalCount || question?.usersSurveyAnswers?.length || 0

    const getQuestionResponseRate = (question: ReportSdssSurveyQuestionResponse) => {
      const totalInvited = roundReportResponse.value.totalNumberOfInvitedUsers || 0
      if (!totalInvited) {
        return '0%'
      }

      return `${Math.round((getQuestionTotalResponses(question) / totalInvited) * 100)}%`
    }

    const buildQuestionAnalysisRows = (questions: ReportSdssSurveyQuestionResponse[]) => {
      const analysisRows: Array<Record<string, any>> = []

      questions.forEach((question) => {
        const analysisGroups = [
          {
            type: t('sdssSurvey.report.questionAnalysis.responseRate.responseRateAnalysisBasedOnQualification'),
            entries: Object.entries(question.questionAnalysis?.userQualificationSpecificationAnalysis || {})
          },
          {
            type: t('sdssSurvey.report.questionAnalysis.responseRate.responseRateAnalysisBasedOnNationality'),
            entries: Object.entries(question.questionAnalysis?.userNationalityAnalysis || {})
          },
          {
            type: t('sdssSurvey.report.questionAnalysis.responseRate.responseRateAnalysisBasedOnJob'),
            entries: Object.entries(question.questionAnalysis?.userJobAnalysis || {})
          },
          {
            type: t('sdssSurvey.report.questionAnalysis.responseRate.responseRateAnalysisBasedOnDepartment'),
            entries: Object.entries(question.questionAnalysis?.userDepartmentAnalysis || {})
          }
        ]

        analysisGroups.forEach((group) => {
          group.entries.forEach(([category, count]) => {
            analysisRows.push({
              questionId: question.id,
              questionTitle: question.localTitle || '-',
              questionType: question.localQuestionType || question.questionTypeKey || '-',
              analysisType: group.type,
              category,
              responses: count
            })
          })
        })
      })

      return analysisRows
    }

    const buildOptionDistributionRows = (questions: ReportSdssSurveyQuestionResponse[]) => {
      const optionRows: Array<Record<string, any>> = []

      questions.forEach((question) => {
        const totalResponses = getQuestionTotalResponses(question)
        const optionStatistics = question.userAnswersStatistics?.optionStatistics || []

        optionStatistics.forEach((optionStat: any) => {
          const option = question.options?.find((item) => item.id === optionStat.id)
          const percentage = totalResponses ? Math.round((optionStat.count / totalResponses) * 100) : 0

          optionRows.push({
            questionId: question.id,
            questionTitle: question.localTitle || '-',
            questionType: question.localQuestionType || question.questionTypeKey || '-',
            optionLabel: option?.localOption || option?.optionAr || option?.optionEn || '-',
            responses: optionStat.count || 0,
            percentage: `${percentage}%`
          })
        })

        if (question.questionTypeKey?.trim().toLowerCase() === 'true-false') {
          const trueCount = question.userAnswersStatistics?.trueCount || 0
          const falseCount = question.userAnswersStatistics?.falseCount || 0

          optionRows.push(
            {
              questionId: question.id,
              questionTitle: question.localTitle || '-',
              questionType: question.localQuestionType || question.questionTypeKey || '-',
              optionLabel: t('common.true'),
              responses: trueCount,
              percentage: `${totalResponses ? Math.round((trueCount / totalResponses) * 100) : 0}%`
            },
            {
              questionId: question.id,
              questionTitle: question.localTitle || '-',
              questionType: question.localQuestionType || question.questionTypeKey || '-',
              optionLabel: t('common.false'),
              responses: falseCount,
              percentage: `${totalResponses ? Math.round((falseCount / totalResponses) * 100) : 0}%`
            }
          )
        }
      })

      return optionRows
    }

    const buildTextAnswerRows = (questions: ReportSdssSurveyQuestionResponse[]) => {
      const textRows: Array<Record<string, any>> = []

      questions.forEach((question) => {
        ;(question.usersSurveyAnswers || []).forEach((answer) => {
          if (!answer.responseText) {
            return
          }

          textRows.push({
            questionId: question.id,
            questionTitle: question.localTitle || '-',
            grpNumber: answer.user?.employeeGrpNumber || '-',
            answer: answer.responseText
          })
        })
      })

      return textRows
    }

    const buildEmployeeRows = (employees: SurveyUser[]) =>
      employees.map((employee) => ({
        grpNumber: employee.reportUserResponse.employeeInfo.id || '-',
        fullName: employee.reportUserResponse.employeeInfo.fullName || '-',
        jobTitle: employee.reportUserResponse.employeeInfo.jobName || '-',
        email: employee.reportUserResponse.employeeInfo.emailAddress || '-',
        department: employee.reportUserResponse.employeeInfo.departmentName || '-',
        grade: employee.reportUserResponse.employeeInfo.gradeName || '-',
        answeredStatus: employee.hasAnswered ? t('common.answered') : t('common.notAnswered')
      }))

    const loadDetailedQuestionReports = async () => {
      const questions = roundReportResponse.value.questions || []
      if (!questions.length) {
        return []
      }

      const hasFullCache = questions.every((question) => detailedQuestionReportMap.value[question.id])
      if (hasFullCache) {
        return questions.map((question) => detailedQuestionReportMap.value[question.id])
      }

      if (detailedQuestionReportsPromise) {
        return detailedQuestionReportsPromise
      }

      detailedQuestionReportsPromise = (async () => {
        const responses = await Promise.allSettled(
          questions.map((question) =>
            get<SuccessResponse<ReportSdssSurveyQuestionResponse>>(URIS.SURVEYS.QUESTION_REPORTS.DETAILS_REPORT(Number(question.id)))
          )
        )

        const nextMap = { ...detailedQuestionReportMap.value }

        responses.forEach((result, index) => {
          const fallbackQuestion = questions[index] as ReportSdssSurveyQuestionResponse

          if (result.status === 'fulfilled' && result.value?.success && result.value.data) {
            nextMap[fallbackQuestion.id] = result.value.data
            return
          }

          nextMap[fallbackQuestion.id] = fallbackQuestion
        })

        detailedQuestionReportMap.value = nextMap
        return questions.map((question) => nextMap[question.id] || (question as ReportSdssSurveyQuestionResponse))
      })()

      try {
        return await detailedQuestionReportsPromise
      } finally {
        detailedQuestionReportsPromise = null
      }
    }

    async function retrieveCurrentSurveyRoundDetails() {
      if (!token.value) {
        return
      }

      await get<SuccessResponse<ReportSdssSurveyRoundResponse>>(
        URIS.SURVEYS.SURVEY_REPORTS.BY_ROUND +
        Characters.QUESTION_MARK +
        Fields.ROUND_ID +
        Characters.EQUAL +
        props.roundId
      ).then((response: SuccessResponse<ReportSdssSurveyRoundResponse>) => {
        if (response.success) {
          roundReportResponse.value = response.data as ReportSdssSurveyRoundResponse
          roundDetails.value = response.data.roundDetails
        } else {
          toast.error(response.message)
        }
      })
    }

    onMounted(async () => {
      await retrieveCurrentSurveyRoundDetails()
      isDataLoaded.value = true
    })

    const exportToExcel = async () => {
      exportLoading.value = true

      try {
        const detailedQuestions = await loadDetailedQuestionReports()
        const employees = roundReportResponse.value.surveyUsers || []

        const overviewRows = [
          ...surveySummaryItems.value.map((item) => ({
            section: t('sdssSurvey.report.surveyDetails.title'),
            metric: item.label,
            value: toDisplayValue(item.value)
          })),
          ...questionSummaryItems.value.map((item) => ({
            section: t('sdssSurvey.report.questionDetails'),
            metric: item.label,
            value: toDisplayValue(item.value)
          })),
          {
            section: t('sdssSurvey.report.employeeDetails'),
            metric: t('common.answered'),
            value: answeredEmployeeCount.value
          },
          {
            section: t('sdssSurvey.report.employeeDetails'),
            metric: t('common.pending'),
            value: pendingEmployeeCount.value
          },
          {
            section: t('sdssSurvey.report.employeeDetails'),
            metric: t('common.responseRate'),
            value: responseRate.value
          }
        ]

        const questionRows = detailedQuestions.map((question, index) => ({
          sequence: index + 1,
          questionId: question.id,
          questionTitle: question.localTitle || '-',
          questionType: question.localQuestionType || question.questionTypeKey || '-',
          publicQuestion: question.showPublicQuestions ? t('common.yes') : t('common.no'),
          optionsCount: question.options?.length || 0,
          totalResponses: getQuestionTotalResponses(question),
          responseRate: getQuestionResponseRate(question),
          averageProbability: toDisplayValue(question.userAnswersStatistics?.averageProbabilityOfOccurrence),
          averageImpact: toDisplayValue(question.userAnswersStatistics?.averageImpactSeverity),
          textAnswersCount: (question.usersSurveyAnswers || []).filter((item) => item.responseText).length
        }))

        const fileName = buildTimestampedExcelFileName(
          `${pdfReportLabel.value} ${pdfLocalizedTitle.value}`
        )

        await exportWorkbookToExcel({
          isArabic: isArabic.value,
          fileName,
          sheets: [
            {
              sheetName: isArabic.value ? 'ملخص التقرير' : 'Report Overview',
              title: `${pdfReportLabel.value} - ${pdfLocalizedTitle.value}`,
              metadata: [
                { label: t('sdssSurvey.report.surveyDetails.roundTitle'), value: pdfLocalizedTitle.value },
                { label: t('common.id'), value: props.roundId },
                { label: t('common.createdAt'), value: formatDateTime(roundDetails.value.createdAt) },
                { label: t('common.updatedAt'), value: formatDateTime(roundDetails.value.updatedAt) },
                { label: t('common.export'), value: formatDateTime(new Date().toISOString()) }
              ],
              headers: {
                section: isArabic.value ? 'القسم' : 'Section',
                metric: isArabic.value ? 'المؤشر' : 'Metric',
                value: isArabic.value ? 'القيمة' : 'Value'
              },
              data: overviewRows,
              columnWidths: [24, 38, 28]
            },
            {
              sheetName: isArabic.value ? 'ملخص الأسئلة' : 'Question Summary',
              title: isArabic.value ? 'إحصائيات الأسئلة' : 'Question Statistics',
              headers: {
                sequence: isArabic.value ? 'الترتيب' : 'No.',
                questionId: isArabic.value ? 'معرف السؤال' : 'Question ID',
                questionTitle: isArabic.value ? 'السؤال' : 'Question',
                questionType: isArabic.value ? 'النوع' : 'Type',
                publicQuestion: isArabic.value ? 'سؤال عام' : 'Public Question',
                optionsCount: isArabic.value ? 'عدد الخيارات' : 'Options',
                totalResponses: isArabic.value ? 'إجمالي الإجابات' : 'Total Responses',
                responseRate: isArabic.value ? 'نسبة الاستجابة' : 'Response Rate',
                averageProbability: isArabic.value ? 'متوسط الاحتمالية' : 'Average Probability',
                averageImpact: isArabic.value ? 'متوسط الأثر' : 'Average Impact',
                textAnswersCount: isArabic.value ? 'إجابات نصية' : 'Text Answers'
              },
              data: questionRows,
              columnWidths: [10, 12, 40, 18, 16, 14, 16, 16, 18, 16, 14]
            },
            {
              sheetName: isArabic.value ? 'توزيع الإجابات' : 'Answer Distribution',
              title: isArabic.value ? 'توزيع الإجابات حسب الخيار' : 'Answer Distribution by Option',
              headers: {
                questionId: isArabic.value ? 'معرف السؤال' : 'Question ID',
                questionTitle: isArabic.value ? 'السؤال' : 'Question',
                questionType: isArabic.value ? 'النوع' : 'Type',
                optionLabel: isArabic.value ? 'الخيار' : 'Option',
                responses: isArabic.value ? 'عدد الإجابات' : 'Responses',
                percentage: isArabic.value ? 'النسبة' : 'Percentage'
              },
              data: buildOptionDistributionRows(detailedQuestions),
              columnWidths: [12, 42, 18, 28, 16, 14]
            },
            {
              sheetName: isArabic.value ? 'التحليلات' : 'Analysis',
              title: isArabic.value ? 'تحليلات الأسئلة' : 'Question Analysis',
              headers: {
                questionId: isArabic.value ? 'معرف السؤال' : 'Question ID',
                questionTitle: isArabic.value ? 'السؤال' : 'Question',
                questionType: isArabic.value ? 'النوع' : 'Type',
                analysisType: isArabic.value ? 'نوع التحليل' : 'Analysis Type',
                category: isArabic.value ? 'الفئة' : 'Category',
                responses: isArabic.value ? 'عدد الاستجابات' : 'Responses'
              },
              data: buildQuestionAnalysisRows(detailedQuestions),
              columnWidths: [12, 36, 18, 34, 28, 16]
            },
            {
              sheetName: isArabic.value ? 'إجابات نصية' : 'Text Answers',
              title: isArabic.value ? 'الإجابات النصية' : 'Text Responses',
              headers: {
                questionId: isArabic.value ? 'معرف السؤال' : 'Question ID',
                questionTitle: isArabic.value ? 'السؤال' : 'Question',
                grpNumber: isArabic.value ? 'الرقم العسكري' : 'Grp Number',
                answer: isArabic.value ? 'الإجابة' : 'Answer'
              },
              data: buildTextAnswerRows(detailedQuestions),
              columnWidths: [12, 38, 16, 64]
            },
            {
              sheetName: isArabic.value ? 'الموظفون' : 'Employees',
              title: isArabic.value ? 'مشاركة الموظفين' : 'Employee Participation',
              metadata: [
                { label: t('common.total'), value: employees.length },
                { label: t('common.answered'), value: answeredEmployeeCount.value },
                { label: t('common.pending'), value: pendingEmployeeCount.value },
                { label: t('common.responseRate'), value: responseRate.value }
              ],
              headers: {
                grpNumber: isArabic.value ? 'الرقم العسكري' : 'Grp Number',
                fullName: isArabic.value ? 'الاسم الكامل' : 'Full Name',
                jobTitle: isArabic.value ? 'المسمى الوظيفي' : 'Job Title',
                email: isArabic.value ? 'البريد الإلكتروني' : 'Email',
                department: isArabic.value ? 'الإدارة' : 'Department',
                grade: isArabic.value ? 'الدرجة' : 'Grade',
                answeredStatus: isArabic.value ? 'حالة الإجابة' : 'Answer Status'
              },
              data: buildEmployeeRows(employees),
              columnWidths: [16, 28, 24, 34, 24, 16, 18],
              statusColumns: ['answeredStatus']
            }
          ]
        })

        toast.success(isArabic.value ? 'تم تصدير تقرير الإكسل بنجاح' : 'Excel report exported successfully')
      } catch (error) {
        console.error('Error exporting survey report Excel', error)
        toast.error(isArabic.value ? 'فشل تصدير تقرير الإكسل' : 'Failed to export Excel report')
      } finally {
        exportLoading.value = false
      }
    }

    const downloadRoundPdf = async () => {
      if (!reportExportRoot.value || pdfLoading.value) {
        return
      }

      pdfLoading.value = true
      try {
        await questionReportsRef.value?.ensureQuestionDetailsLoaded?.()
        forceExpandAllQuestions.value = true
        await waitForPdfRenderStability()

        await exportElementToPdf({
          target: reportExportRoot.value,
          fileName: pdfFileName.value,
          fontLoads: pdfFontResources.fontLoads,
          injectedStyle: pdfInjectedStyle.value,
          canvasScale: 2,
          beforeCapture: async () => {
            await waitForPdfRenderStability()
          },
          onClone: handlePdfClone,
          pageChrome: {
            headerTitle: pdfReportLabel.value,
            headerDescription: pdfLocalizedTitle.value,
            fontLoads: pdfFontResources.fontLoads
          }
        })
      } catch (error) {
        console.error('Error exporting survey report PDF', error)
      } finally {
        forceExpandAllQuestions.value = false
        pdfLoading.value = false
      }
    }

    const goBack = () => {
      router.push({ name: 'SurveyReports' })
    }

    return {
      roundReportResponse,
      roundDetails,
      surveySummaryItems,
      questionSummaryItems,
      exportToExcel,
      employeeLang,
      isArabic,
      isDataLoaded,
      exportLoading,
      forceExpandAllQuestions,
      pdfLoading,
      questionReportsRef,
      reportExportRoot,
      goBack,
      downloadRoundPdf
    }
  }
}
</script>

<style scoped lang="scss">
.survey-report-page-shell {
  position: relative;
}

.survey-report-page-shell__pdf-loader {
  border-radius: 24px;
  z-index: 5;
}

.survey-report-page {
  padding: 16px;
}

.survey-report-export-root {
  display: grid;
  gap: 16px;
}

.survey-report-export-root--rtl {
  direction: rtl;
  text-align: right;
}

.survey-report-export-root--ltr {
  direction: ltr;
  text-align: left;
}

.report-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 24px;
  background: linear-gradient(135deg, #006c44 0%, #0b8a58 58%, #0b4f87 100%);
  color: #fff;
}

.report-hero--rtl {
  direction: rtl;
}

.report-hero__main {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.report-hero__back {
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
}

.report-hero__copy {
  min-width: 0;
}

.report-hero__eyebrow,
.report-card__eyebrow {
  margin: 0 0 6px;
  font-size: 0.8rem;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  opacity: 0.78;
}

.report-hero__title,
.report-card__title {
  margin: 0;
  line-height: 1.25;
  word-break: break-word;
}

.report-hero__title {
  font-size: clamp(1.35rem, 2vw, 2rem);
  color: #fff;
  display: flex;
}

.report-hero__subtitle {
  margin: 8px 0 0;
  max-width: 720px;
  color: rgba(255, 255, 255, 0.88);
  line-height: 1.6;
  word-break: break-word;
}

.report-hero__actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 12px;
}

.report-hero__action-btn {
  width: 44px;
  height: 44px;
  min-width: 44px;
}

.report-hero__action-btn--ghost {
  border-color: rgba(255, 255, 255, 0.45);
  color: #fff;
}

.report-section {
  display: grid;
}

.report-section--stacked {
  gap: 16px;
}

.report-card {
  border: 1px solid #e4e7ec;
  border-radius: 24px;
  box-shadow: 0 10px 28px rgba(16, 24, 40, 0.06);
}

.report-card--questions {
  background: radial-gradient(circle at top right, rgba(21, 112, 239, 0.06), transparent 28%),
  linear-gradient(180deg, #ffffff 0%, #f9fbff 100%);
}

.report-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 16px 16px 0;
}

.report-card__body {
  padding: 16px !important;
}

.report-card__body--compact {
  padding-top: 8px !important;
}

.report-card__description {
  margin: 8px 0 0;
  color: #667085;
  line-height: 1.6;
  word-break: break-word;
}

.detail-grid {
  display: grid;
  gap: 12px;
}

.detail-grid--four {
  grid-template-columns: repeat(1, minmax(0, 1fr));
}

.detail-grid--stats {
  grid-template-columns: repeat(1, minmax(0, 1fr));
}

.detail-block,
.metric-card {
  display: grid;
  gap: 6px;
  padding: 16px;
  border: 1px solid #eaecf0;
  border-radius: 16px;
  background: #fcfcfd;
}

.detail-block__label,
.metric-card__label {
  font-size: 0.75rem;
  color: #667085;
}

.detail-block__value,
.metric-card__value {
  color: #101828;
  line-height: 1.5;
  word-break: break-word;
}

.metric-card__value {
  font-size: 1.35rem;
}

.report-section--questions :deep(.question-report-list) {
  gap: 18px;
}

.report-section--questions :deep(.question-report-card) {
  border-radius: 22px;
  border-color: #d9e2f0;
  box-shadow: 0 14px 34px rgba(15, 23, 42, 0.06);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(247, 250, 255, 0.98) 100%);
}

.report-section--questions :deep(.question-report-card__header) {
  padding: 16px 18px;
  background: linear-gradient(135deg, #0b6f72 0%, #0b8a58 56%, #1570ef 100%);
}

.report-section--questions :deep(.question-report-card__title) {
  font-size: 1.02rem;
}

.report-section--questions :deep(.question-report-card__subtitle) {
  max-width: 72ch;
}

.report-section--questions :deep(.question-report-card__content) {
  gap: 14px;
  padding: 18px;
}

.report-section--questions :deep(.question-report-card__insights) {
  padding: 0 18px 18px;
}

.report-section--questions :deep(.question-panel),
.report-section--questions :deep(.summary-card),
.report-section--questions :deep(.analysis-card) {
  border-radius: 18px;
}

.report-section--questions :deep(.question-panel) {
  border-color: #dde3ee;
  background: linear-gradient(180deg, #ffffff 0%, #fbfcfe 100%);
}

.report-section--questions :deep(.question-panel__header),
.report-section--questions :deep(.analysis-card__header) {
  padding-top: 14px;
}

.report-section--questions :deep(.question-panel__body),
.report-section--questions :deep(.analysis-card__body) {
  padding: 16px;
}

.report-section--questions :deep(.answer-bars),
.report-section--questions :deep(.metric-stack),
.report-section--questions :deep(.analysis-grid),
.report-section--questions :deep(.question-summary-grid),
.report-section--questions :deep(.insight-panels) {
  gap: 14px;
}

.report-section--questions :deep(.answer-bar),
.report-section--questions :deep(.metric-row),
.report-section--questions :deep(.text-answer-list__item),
.report-section--questions :deep(.progress-metric),
.report-section--questions :deep(.insight-summary-card),
.report-section--questions :deep(.insight-panel) {
  border-color: #e2e8f0;
  background: #fff;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.85);
}

.report-section--questions :deep(.answer-bar__label),
.report-section--questions :deep(.metric-row__label),
.report-section--questions :deep(.insight-panel__label) {
  max-width: 56ch;
}

.report-section--questions :deep(.apexcharts-canvas),
.report-section--questions :deep(.apexcharts-svg) {
  width: 100% !important;
  max-width: 100%;
}

.report-section--questions :deep(.analysis-card__body .apexcharts-legend) {
  justify-content: center !important;
}

.report-section--questions :deep(.v-progress-linear) {
  border-radius: 999px;
}

@media (min-width: 600px) {
  .detail-grid--four,
  .detail-grid--stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (min-width: 960px) {
  .detail-grid--four {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .detail-grid--stats {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 959px) {
  .survey-report-page {
    padding: 12px;
  }

  .report-hero {
    flex-direction: column;
    align-items: stretch;
  }

  .report-hero__actions {
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 600px) {
  .survey-report-page {
    padding: 8px;
  }

  .report-hero,
  .report-card__header,
  .report-card__body {
    padding-left: 16px;
    padding-right: 16px;
  }

  .report-section--questions :deep(.question-report-card__header),
  .report-section--questions :deep(.question-report-card__content),
  .report-section--questions :deep(.question-report-card__insights) {
    padding-left: 14px;
    padding-right: 14px;
  }

  .report-hero__main {
    align-items: flex-start;
  }

  .report-hero__actions {
    display: flex;
    justify-content: flex-start;
  }

  .report-hero__action-btn {
    width: 42px;
  }
}
</style>
