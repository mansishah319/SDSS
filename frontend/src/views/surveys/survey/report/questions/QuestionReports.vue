<template src="./QuestionReports.html" lang="html"></template>
<script lang="ts">
import { computed, defineComponent, onMounted, PropType, ref, watch } from 'vue'
import { ReportSdssSurveyQuestionResponse } from '@/dto/report/ReportSdssSurveyRoundResponse'
import { useI18n } from 'vue-i18n'
import { DialogItemResponse } from '@/dto/DialogResponseDto'
import { get } from '@/utils/GenericApiHelper'
import { SuccessResponse } from '@/dto/generic/SuccessResponse'
import { SdssSurveyUserQuestionAnswersResponse } from '@/dto/surveys/answers/SdssSurveyUserQuestionAnswersResponse'
import { Characters, Fields, URIS } from '@/constants/DpConstants'
import DialogDashboardScreen from '@/views/dashboard/dialog/DialogMainDashboard.vue'
import QuestionDetailsReport from '@/views/surveys/survey/report/details/question/QuestionDetailsReport.vue'
import { translate } from '@/utils/TranslatorHelper.ts'

export default defineComponent({
  name: 'QuestionReports',
  components: { DialogDashboardScreen, QuestionDetailsReport },
  props: {
    roundId: {
      type: String,
      required: true
    },
    questionList: {
      type: Array as PropType<ReportSdssSurveyQuestionResponse[]>,
      default: () => []
    },
    countAllInvitedUsers: {
      type: Number,
      default: 1
    },
    expandAllQuestions: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const { t } = useI18n()
    const detailsDialog = ref<boolean>(false)
    const dialogHeaders = ref<string[]>([] as string[])
    const mainDialogTitle = ref<string>('' as string)
    const dialogItems = ref<DialogItemResponse[]>([] as DialogItemResponse[])
    const expandedQuestions = ref<Record<number, boolean>>({})
    const questionDetailsMap = ref<Record<number, ReportSdssSurveyQuestionResponse>>({})
    const isAnalysisLoading = ref<boolean>(true)
    let fetchAllQuestionDetailsPromise: Promise<void> | null = null
    const shouldRenderWithoutTransition = computed(() => props.expandAllQuestions)

    const fetchAllQuestionDetails = async () => {
      if (fetchAllQuestionDetailsPromise) {
        return fetchAllQuestionDetailsPromise
      }

      fetchAllQuestionDetailsPromise = (async () => {
        if (!props.questionList.length) {
          questionDetailsMap.value = {}
          isAnalysisLoading.value = false
          return
        }

        isAnalysisLoading.value = true

        try {
          const responses = await Promise.all(
            props.questionList.map((question) =>
              get<SuccessResponse<ReportSdssSurveyQuestionResponse>>(
                URIS.SURVEYS.QUESTION_REPORTS.DETAILS_REPORT(Number(question.id))
              )
            )
          )

          const nextMap: Record<number, ReportSdssSurveyQuestionResponse> = {}
          responses.forEach((response, index) => {
            if (response && response.success) {
              nextMap[props.questionList[index].id] = response.data
            }
          })
          questionDetailsMap.value = nextMap
        } finally {
          isAnalysisLoading.value = false
          fetchAllQuestionDetailsPromise = null
        }
      })()

      return fetchAllQuestionDetailsPromise
    }

    const ensureQuestionDetailsLoaded = async () => {
      await fetchAllQuestionDetails()
    }

    onMounted(fetchAllQuestionDetails)

    watch(
      () => props.questionList,
      async () => {
        await fetchAllQuestionDetails()
      },
      { deep: false }
    )

    const handleProbabilityClick = async (question: ReportSdssSurveyQuestionResponse) => {
      mainDialogTitle.value = translate('sdssSurvey.report.probabilityMainTitle')
      dialogHeaders.value = [
        t('adminDashboard.dialog.users.grpNumber'),
        t('common.question'),
        t('common.probabilityOfOccurrencePercentage')
      ] as string[]

      await get<SuccessResponse<SdssSurveyUserQuestionAnswersResponse[]>>(
        URIS.SURVEYS.SURVEY_QUESTION_ANSWERS.RETRIEVE_ALL_DETAILS_FOR_NON_ZERO_PROBABILITY_BY_QUESTION +
        Characters.QUESTION_MARK +
        Fields.QUESTION_ID +
        Characters.EQUAL +
        question.id
      ).then((response: SuccessResponse<SdssSurveyUserQuestionAnswersResponse[]>) => {
        if (response.success) {
          dialogItems.value = response.data.map((item: SdssSurveyUserQuestionAnswersResponse) => ({
            head1: item.user.employeeGrpNumber,
            head2: item.question.localTitle,
            head3: item.probabilityOfOccurrence
          }))
        }
      })

      detailsDialog.value = true
    }

    const handleImpactClick = async (question: ReportSdssSurveyQuestionResponse) => {
      mainDialogTitle.value = translate('sdssSurvey.report.impactMainTitle')
      dialogHeaders.value = [
        t('adminDashboard.dialog.users.grpNumber'),
        t('common.question'),
        t('common.impactSeverityPercentage')
      ] as string[]

      await get<SuccessResponse<SdssSurveyUserQuestionAnswersResponse[]>>(
        URIS.SURVEYS.SURVEY_QUESTION_ANSWERS.RETRIEVE_ALL_DETAILS_FOR_NON_ZERO_IMPACT_BY_QUESTION +
        Characters.QUESTION_MARK +
        Fields.QUESTION_ID +
        Characters.EQUAL +
        question.id
      ).then((response: SuccessResponse<SdssSurveyUserQuestionAnswersResponse[]>) => {
        if (response.success) {
          dialogItems.value = response.data.map((item: SdssSurveyUserQuestionAnswersResponse) => ({
            head1: item.user.employeeGrpNumber,
            head2: item.question.localTitle,
            head3: item.impactSeverity
          }))
        }
      })

      detailsDialog.value = true
    }

    const handleCloseDialog = () => {
      detailsDialog.value = false
      mainDialogTitle.value = ''
      dialogHeaders.value = []
      dialogItems.value = []
    }

    const toggleQuestionInsights = (questionId: number) => {
      expandedQuestions.value[questionId] = !expandedQuestions.value[questionId]
    }

    const isQuestionExpanded = (questionId: number) => {
      return props.expandAllQuestions || !!expandedQuestions.value[questionId]
    }

    const getTextPreviewAnswers = (question: ReportSdssSurveyQuestionResponse) => {
      return (question.usersSurveyAnswers || []).slice(0, 3).map((item) => ({
        question: item.user?.employeeGrpNumber ? `${t('common.grpNumber')}: ${item.user.employeeGrpNumber}` : t('common.answer'),
        answer: item.responseText || '-'
      }))
    }

    const getOptionTooltip = (option: any, question: ReportSdssSurveyQuestionResponse) => {
      const percentage = Math.round(
        ((question.userAnswersStatistics?.optionStatistics?.find((stat: any) => stat.id === option.id)?.count || 0) /
          (question?.usersSurveyAnswers?.length || question.userAnswersStatistics?.totalCount || 1)) *
        100
      )

      return `${option.localOption}: ${percentage}%`
    }

    return {
      getTextPreviewAnswers,
      getOptionTooltip,
      isAnalysisLoading,
      shouldRenderWithoutTransition,
      isQuestionExpanded,
      questionDetailsMap,
      toggleQuestionInsights,
      handleProbabilityClick,
      handleCloseDialog,
      detailsDialog,
      dialogItems,
      dialogHeaders,
      ensureQuestionDetailsLoaded,
      handleImpactClick,
      mainDialogTitle
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
<style src="./QuestionReports.scss" scoped lang="scss"></style>
