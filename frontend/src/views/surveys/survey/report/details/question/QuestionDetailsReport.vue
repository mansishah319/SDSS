<script lang="ts">
import { computed, defineComponent, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  ReportSdssSurveyQuestionResponse,
  UserSurveyAnswersReportResponse
} from '@/dto/report/ReportSdssSurveyRoundResponse'
import DialogDashboardScreen from '@/views/dashboard/dialog/DialogMainDashboard.vue'
import { DialogItemResponse } from '@/dto/DialogResponseDto.ts'

export default defineComponent({
  name: 'SurveyQuestionAnalysisInline',
  components: { DialogDashboardScreen },
  props: {
    questionData: {
      type: Object as () => ReportSdssSurveyQuestionResponse,
      required: true
    }
  },
  setup(props) {
    const { t } = useI18n()
    const detailsDialog = ref(false)
    const detailsDialogTitle = ref('')
    const detailsDialogItems = ref<Array<{ label: string; value: string; count: number }>>([])

    const dialog = ref<boolean>(false)
    const mainDialogTitle = ref<string>('')
    const dialogHeaders = ref<string[]>([])
    const dialogItems = ref<DialogItemResponse[]>([])

    const isMultipleChoice = computed(() => {
      return props.questionData?.questionTypeKey?.trim().toLowerCase() === 'multiple-choice'
    })

    const isSingleChoice = computed(() => {
      return props.questionData?.questionTypeKey?.trim().toLowerCase() === 'single-choice'
    })

    const isTrueFalse = computed(() => {
      return props.questionData?.questionTypeKey?.trim().toLowerCase() === 'true-false'
    })

    const isText = computed(() => {
      return props.questionData?.questionTypeKey?.trim().toLowerCase() === 'text'
    })

    const textResponsesPreview = computed(() => {
      if (!isText.value) return ''
      const responses = (props.questionData.usersSurveyAnswers || [])
        .map((item) => item.responseText)
        .filter(Boolean)
        .slice(0, 3)

      return responses.join('\n\n---\n\n')
    })

    const showAllTextResponses = () => {
      mainDialogTitle.value = t('sdssSurvey.report.userQuestionAnswer')
      dialogHeaders.value = [t('common.grpNumber'), t('common.question'), t('common.answer')]
      dialogItems.value = props.questionData.usersSurveyAnswers.map((item: UserSurveyAnswersReportResponse) => ({
        head1: item?.user?.employeeGrpNumber,
        head2: item?.question?.localTitle,
        head3: item.responseText
      }))
      dialog.value = true
    }

    const trueFalseSeries = computed(() => {
      if (!isTrueFalse.value) return []
      return [
        props.questionData.userAnswersStatistics.trueCount || 0,
        props.questionData.userAnswersStatistics.falseCount || 0
      ]
    })

    const totalResponses = computed(() => {
      return props.questionData?.userAnswersStatistics?.totalCount || 0
    })

    const hasOptionsData = computed(() => {
      return (
        props.questionData?.options?.length > 0 &&
        props.questionData?.userAnswersStatistics?.optionStatistics?.length > 0
      )
    })

    const hasQualificationData = computed(() => {
      return !!Object.keys(props.questionData?.questionAnalysis?.userQualificationSpecificationAnalysis || {}).length
    })

    const hasNationalityData = computed(() => {
      return !!Object.keys(props.questionData?.questionAnalysis?.userNationalityAnalysis || {}).length
    })

    const hasJobData = computed(() => {
      return !!Object.keys(props.questionData?.questionAnalysis?.userJobAnalysis || {}).length
    })

    const hasDepartmentData = computed(() => {
      return !!Object.keys(props.questionData?.questionAnalysis?.userDepartmentAnalysis || {}).length
    })

    const hasMaritalStatusData = computed(() => {
      return !!Object.keys(props.questionData?.questionAnalysis?.userMaritalStatusAnalysis || {}).length
    })

    const hasGenderData = computed(() => {
      return !!Object.keys(props.questionData?.questionAnalysis?.userGenderAnalysis || {}).length
    })

    const hasAverageData = computed(() => {
      return hasProbabilityData.value || hasImpactData.value
    })

    const hasProbabilityData = computed(() => {
      return props.questionData?.userAnswersStatistics?.averageProbabilityOfOccurrence !== undefined
    })

    const hasImpactData = computed(() => {
      return props.questionData?.userAnswersStatistics?.averageImpactSeverity !== undefined
    })

    const sumAnalysisCounts = (analysis?: Record<string, number>) =>
      Object.values(analysis || {}).reduce((total, count) => total + Number(count || 0), 0)

    const toPercentage = (count: number, total: number) =>
      total > 0 ? Math.round((Number(count || 0) / total) * 1000) / 10 : 0

    const qualificationCount = computed(() =>
      sumAnalysisCounts(props.questionData.questionAnalysis?.userQualificationSpecificationAnalysis)
    )

    const nationalityCount = computed(() => {
      return sumAnalysisCounts(props.questionData.questionAnalysis?.userNationalityAnalysis)
    })

    const jobCount = computed(() => {
      return sumAnalysisCounts(props.questionData.questionAnalysis?.userJobAnalysis)
    })

    const departmentCount = computed(() => {
      return sumAnalysisCounts(props.questionData.questionAnalysis?.userDepartmentAnalysis)
    })

    const maritalStatusCount = computed(() =>
      sumAnalysisCounts(props.questionData.questionAnalysis?.userMaritalStatusAnalysis)
    )

    const genderCount = computed(() =>
      sumAnalysisCounts(props.questionData.questionAnalysis?.userGenderAnalysis)
    )

    const probabilityValue = computed(() => {
      return props.questionData?.userAnswersStatistics?.averageProbabilityOfOccurrence || 0
    })

    const impactValue = computed(() => {
      return props.questionData?.userAnswersStatistics?.averageImpactSeverity || 0
    })

    const optionsSeries = computed(() => {
      if (!hasOptionsData.value) return []
      return props.questionData.userAnswersStatistics.optionStatistics.map((opt) => opt.count)
    })

    const optionsChartOptions = computed(() => ({
      chart: {
        type: 'donut',
        toolbar: { show: false }
      },
      labels: hasOptionsData.value ? props.questionData.options.map((opt) => opt.localOption) : [],
      colors: ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899'],
      plotOptions: {
        pie: {
          donut: {
            size: '62%',
            labels: {
              show: true,
              total: {
                show: true,
                label: t('sdssSurvey.report.questionAnalysis.totalResponses'),
                formatter: () => totalResponses.value.toString()
              }
            }
          }
        }
      },
      legend: {
        position: 'bottom',
        fontSize: '12px'
      },
      tooltip: {
        y: {
          formatter: (val: number) => `${val} (${Math.round((val / (totalResponses.value || 1)) * 100)}%)`
        }
      }
    }))

    const trueFalseChartOptions = computed(() => ({
      chart: {
        type: 'donut',
        toolbar: { show: false }
      },
      labels: [t('common.true'), t('common.false')],
      colors: ['#10B981', '#EF4444'],
      plotOptions: {
        pie: {
          donut: {
            size: '62%',
            labels: {
              show: true,
              total: {
                show: true,
                label: t('sdssSurvey.report.questionAnalysis.totalResponses'),
                formatter: () => totalResponses.value.toString()
              }
            }
          }
        }
      },
      legend: {
        position: 'bottom',
        fontSize: '12px'
      },
      tooltip: {
        y: {
          formatter: (val: number) => `${val} (${Math.round((val / (totalResponses.value || 1)) * 100)}%)`
        }
      }
    }))

    const qualificationSeries = computed(() => [
      {
        name: t('sdssSurvey.report.percentageTitle'),
        data: Object.values(props.questionData.questionAnalysis?.userQualificationSpecificationAnalysis || {})
          .map((count) => toPercentage(count, qualificationCount.value))
      }
    ])

    const qualificationChartOptions = computed(() => ({
      chart: { type: 'bar', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, borderRadius: 6, barHeight: '72%' } },
      colors: ['#F59E0B'],
      xaxis: {
        categories: Object.keys(props.questionData.questionAnalysis?.userQualificationSpecificationAnalysis || {}),
        max: 100,
        labels: { formatter: (val: number) => `${Math.round(val)}%` }
      },
      dataLabels: { enabled: true, formatter: (val: number) => `${val}%` },
      tooltip: {
        y: {
          formatter: (val: number, context: any) => {
            const counts = Object.values(props.questionData.questionAnalysis?.userQualificationSpecificationAnalysis || {})
            return `${val}% (${counts[context.dataPointIndex] || 0} ${t('common.response')})`
          }
        }
      }
    }))

    const nationalitySeries = computed(() => [
      {
        data: Object.entries(props.questionData.questionAnalysis?.userNationalityAnalysis || {}).map(([nation, count]) => ({
          x: nation,
          y: toPercentage(count, nationalityCount.value),
          rawCount: count
        }))
      }
    ])

    const nationalityChartOptions = computed(() => ({
      chart: { type: 'treemap', toolbar: { show: false } },
      colors: ['#3B82F6', '#1D4ED8', '#2563EB', '#1E40AF', '#1E3A8A', '#3730A3'],
      plotOptions: {
        treemap: {
          distributed: true,
          enableShades: true
        }
      },
      legend: { show: false },
      dataLabels: { enabled: true, formatter: (_text: string, opts: any) => `${opts.value}%` },
      tooltip: {
        y: {
          formatter: (val: number, context: any) => {
            const counts = Object.values(props.questionData.questionAnalysis?.userNationalityAnalysis || {})
            return `${val}% (${counts[context.dataPointIndex] || 0} ${t('common.response')})`
          }
        }
      }
    }))

    const jobSeries = computed(() => [
      {
        name: t('sdssSurvey.report.percentageTitle'),
        data: Object.values(props.questionData.questionAnalysis?.userJobAnalysis || {})
          .map((count) => toPercentage(count, jobCount.value))
      }
    ])

    const jobChartOptions = computed(() => ({
      chart: { type: 'bar', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: false, borderRadius: 6 } },
      colors: ['#8B5CF6'],
      xaxis: {
        categories: Object.keys(props.questionData.questionAnalysis?.userJobAnalysis || {})
      },
      yaxis: { max: 100, labels: { formatter: (val: number) => `${Math.round(val)}%` } },
      dataLabels: { enabled: true, formatter: (val: number) => `${val}%` },
      tooltip: {
        y: {
          formatter: (val: number, context: any) => {
            const counts = Object.values(props.questionData.questionAnalysis?.userJobAnalysis || {})
            return `${val}% (${counts[context.dataPointIndex] || 0} ${t('common.response')})`
          }
        }
      }
    }))

    const departmentSeries = computed(() => [
      {
        name: t('sdssSurvey.report.percentageTitle'),
        data: Object.values(props.questionData.questionAnalysis?.userDepartmentAnalysis || {})
          .map((count) => toPercentage(count, departmentCount.value))
      }
    ])

    const departmentChartOptions = computed(() => ({
      chart: { type: 'bar', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, borderRadius: 6, barHeight: '72%' } },
      colors: ['#14B8A6'],
      xaxis: {
        categories: Object.keys(props.questionData.questionAnalysis?.userDepartmentAnalysis || {}),
        max: 100,
        labels: { formatter: (val: number) => `${Math.round(val)}%` }
      },
      dataLabels: { enabled: true, formatter: (val: number) => `${val}%` },
      tooltip: {
        y: {
          formatter: (val: number, context: any) => {
            const counts = Object.values(props.questionData.questionAnalysis?.userDepartmentAnalysis || {})
            return `${val}% (${counts[context.dataPointIndex] || 0} ${t('common.response')})`
          }
        }
      }
    }))

    const createDemographicSeries = (analysis: Record<string, number>, total: number) => [{
      name: t('sdssSurvey.report.percentageTitle'),
      data: Object.values(analysis).map((count) => toPercentage(count, total))
    }]

    const createDemographicChartOptions = (analysis: Record<string, number>, color: string) => ({
      chart: { type: 'bar', toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, borderRadius: 6, barHeight: '72%' } },
      colors: [color],
      xaxis: {
        categories: Object.keys(analysis),
        max: 100,
        labels: { formatter: (val: number) => `${Math.round(val)}%` }
      },
      dataLabels: { enabled: true, formatter: (val: number) => `${val}%` },
      tooltip: {
        y: {
          formatter: (val: number, context: any) =>
            `${val}% (${Object.values(analysis)[context.dataPointIndex] || 0} ${t('common.response')})`
        }
      }
    })

    const maritalStatusSeries = computed(() => createDemographicSeries(
      props.questionData.questionAnalysis?.userMaritalStatusAnalysis || {}, maritalStatusCount.value
    ))
    const maritalStatusChartOptions = computed(() => createDemographicChartOptions(
      props.questionData.questionAnalysis?.userMaritalStatusAnalysis || {}, '#EC4899'
    ))
    const genderSeries = computed(() => createDemographicSeries(
      props.questionData.questionAnalysis?.userGenderAnalysis || {}, genderCount.value
    ))
    const genderChartOptions = computed(() => createDemographicChartOptions(
      props.questionData.questionAnalysis?.userGenderAnalysis || {}, '#6366F1'
    ))

    const handleOptionSelection = (config: any) => {
      if (!hasOptionsData.value) return
      const selectedOption = props.questionData.options[config.dataPointIndex]
      const stats = props.questionData.userAnswersStatistics.optionStatistics.find((opt: any) => opt.id === selectedOption.id)
      detailsDialogTitle.value = selectedOption.localOption || ''
      detailsDialogItems.value = [{ label: t('common.responseRate'), value: '', count: stats?.count || 0 }]
      detailsDialog.value = true
    }

    const handleTrueFalseSelection = (config: any) => {
      if (!isTrueFalse.value) return
      const isTrue = config.dataPointIndex === 0
      detailsDialogTitle.value = isTrue ? t('common.true') : t('common.false')
      detailsDialogItems.value = [
        {
          label: t('common.responseRate'),
          value: '',
          count: isTrue ? props.questionData.userAnswersStatistics.trueCount : props.questionData.userAnswersStatistics.falseCount
        }
      ]
      detailsDialog.value = true
    }

    const handleQualificationSelection = () => {
      detailsDialogTitle.value = t('sdssSurvey.report.questionAnalysis.responseRate.responseRateAnalysisBasedOnQualification')
      detailsDialogItems.value = Object.entries(props.questionData.questionAnalysis?.userQualificationSpecificationAnalysis || {}).map(
        ([qualification, count]) => ({ label: qualification, value: '', count: count as number })
      )
      detailsDialog.value = true
    }

    const handleNationalitySelection = () => {
      detailsDialogTitle.value = t('sdssSurvey.report.questionAnalysis.responseRate.responseRateAnalysisBasedOnNationality')
      detailsDialogItems.value = Object.entries(props.questionData.questionAnalysis?.userNationalityAnalysis || {}).map(
        ([nation, count]) => ({ label: nation, value: '', count: count as number })
      )
      detailsDialog.value = true
    }

    const handleJobSelection = () => {
      detailsDialogTitle.value = t('sdssSurvey.report.questionAnalysis.responseRate.responseRateAnalysisBasedOnJob')
      detailsDialogItems.value = Object.entries(props.questionData.questionAnalysis?.userJobAnalysis || {}).map(
        ([job, count]) => ({ label: job, value: '', count: count as number })
      )
      detailsDialog.value = true
    }

    const handleDepartmentSelection = () => {
      detailsDialogTitle.value = t('sdssSurvey.report.questionAnalysis.responseRate.responseRateAnalysisBasedOnDepartment')
      detailsDialogItems.value = Object.entries(props.questionData.questionAnalysis?.userDepartmentAnalysis || {}).map(
        ([department, count]) => ({ label: department, value: '', count: count as number })
      )
      detailsDialog.value = true
    }

    const handleMaritalStatusSelection = () => {
      detailsDialogTitle.value = t('sdssSurvey.report.questionAnalysis.responseRate.responseRateAnalysisBasedOnMaritalStatus')
      detailsDialogItems.value = Object.entries(props.questionData.questionAnalysis?.userMaritalStatusAnalysis || {}).map(
        ([status, count]) => ({ label: status, value: '', count: count as number })
      )
      detailsDialog.value = true
    }

    const handleGenderSelection = () => {
      detailsDialogTitle.value = t('sdssSurvey.report.questionAnalysis.responseRate.responseRateAnalysisBasedOnGender')
      detailsDialogItems.value = Object.entries(props.questionData.questionAnalysis?.userGenderAnalysis || {}).map(
        ([gender, count]) => ({ label: gender, value: '', count: count as number })
      )
      detailsDialog.value = true
    }

    const handleCloseDialog = () => {
      dialog.value = false
      mainDialogTitle.value = ''
      dialogHeaders.value = []
      dialogItems.value = []
    }

    return {
      detailsDialog,
      detailsDialogTitle,
      detailsDialogItems,
      hasOptionsData,
      hasQualificationData,
      hasNationalityData,
      hasJobData,
      hasDepartmentData,
      hasMaritalStatusData,
      hasGenderData,
      hasAverageData,
      hasProbabilityData,
      hasImpactData,
      totalResponses,
      qualificationCount,
      nationalityCount,
      jobCount,
      departmentCount,
      maritalStatusCount,
      genderCount,
      probabilityValue,
      impactValue,
      optionsSeries,
      optionsChartOptions,
      qualificationSeries,
      qualificationChartOptions,
      nationalitySeries,
      nationalityChartOptions,
      jobSeries,
      jobChartOptions,
      departmentSeries,
      departmentChartOptions,
      maritalStatusSeries,
      maritalStatusChartOptions,
      genderSeries,
      genderChartOptions,
      handleOptionSelection,
      handleQualificationSelection,
      handleNationalitySelection,
      handleJobSelection,
      handleDepartmentSelection,
      handleMaritalStatusSelection,
      handleGenderSelection,
      isMultipleChoice,
      isSingleChoice,
      isTrueFalse,
      isText,
      textResponsesPreview,
      showAllTextResponses,
      trueFalseSeries,
      trueFalseChartOptions,
      handleTrueFalseSelection,
      dialog,
      dialogHeaders,
      dialogItems,
      mainDialogTitle,
      handleCloseDialog
    }
  }
})
</script>

<style src="./QuestionDetailsReport.scss" lang="scss"></style>
<template src="./QuestionDetailsReport.html" lang="html"></template>
