<template>
  <div class="survey-question-analysis">
    <v-row class="analysis-row">
      <!-- Main Question Statistics Card -->
      <v-col cols="12" sm="12" md="12" lg="12">
        <v-card class="analysis-card">
          <div class="card-header">
            <h3 class="card-title">{{ questionData.localTitle }}</h3>
            <p class="card-subtitle">{{ questionData.localDescription }}</p>
          </div>

          <!-- Question Type Specific Visualization -->
          <div class="chart-container">
            <!-- Multiple Choice or Single Choice -->
            <template v-if="isMultipleChoice || isSingleChoice">
              <apexchart
                type="donut"
                :options="optionsChartOptions"
                :series="optionsSeries"
                width="100%"
                @data-point-selection="handleOptionSelection"
              ></apexchart>
            </template>

            <!-- True/False Question -->
            <template v-else-if="isTrueFalse">
              <apexchart
                type="donut"
                :options="trueFalseChartOptions"
                :series="trueFalseSeries"
                width="100%"
                @data-point-selection="handleTrueFalseSelection"
              ></apexchart>
            </template>

            <!-- Text Question -->
            <template v-else-if="isText">
              <div class="text-responses-container">
                <h4>{{ $t('survey.textResponses') }}</h4>
                <p class="response-count">{{ totalResponses }} {{ $t('survey.responses') }}</p>
                <v-textarea readonly outlined :value="textResponsesPreview" class="text-responses-preview"></v-textarea>
                <v-btn color="primary" @click="showAllTextResponses">
                  {{ $t('survey.viewAllResponses') }}
                </v-btn>
              </div>
            </template>
          </div>
        </v-card>
      </v-col>

      <!-- Average Statistics -->
      <v-col v-if="hasAverageData" cols="12" sm="12" md="12" lg="12">
        <v-card class="analysis-card">
          <div class="card-header">
            <h3 class="card-title">{{ $t('survey.questionAnalysis.averages') }}</h3>
          </div>
          <div class="stats-container">
            <div v-if="hasProbabilityData" class="stat-item">
              <div class="stat-meta">
                <span class="stat-label">{{ $t('survey.averageProbability') }}</span>
                <span class="stat-value">{{ Math.ceil(probabilityValue) }}%</span>
              </div>
              <v-progress-linear
                :model-value="probabilityValue"
                color="var(--color-success)"
                height="12"
                rounded
                striped
              ></v-progress-linear>
            </div>
            <div v-if="hasImpactData" class="stat-item">
              <div class="stat-meta">
                <span class="stat-label">{{ $t('survey.averageImpact') }}</span>
                <span class="stat-value">{{ Math.ceil(impactValue) }}%</span>
              </div>
              <v-progress-linear
                :model-value="impactValue"
                color="var(--color-primary)"
                height="12"
                rounded
                striped
              ></v-progress-linear>
            </div>
          </div>
        </v-card>
      </v-col>

      <!-- User Qualification Analysis -->
      <v-col v-if="hasQualificationData" cols="12" sm="6" md="6" lg="6">
        <v-card class="analysis-card">
          <div class="card-header">
            <h3 class="card-title">{{ $t('survey.questionAnalysis.qualification') }}</h3>
            <p class="card-subtitle">{{ qualificationCount }} {{ $t('survey.qualifications') }}</p>
          </div>
          <div class="chart-container">
            <apexchart
              type="bar"
              :options="qualificationChartOptions"
              :series="qualificationSeries"
              width="400"
              @data-point-selection="handleQualificationSelection"
            ></apexchart>
          </div>
        </v-card>
      </v-col>

      <!-- Enhanced User Nationality Analysis -->
      <v-col v-if="hasNationalityData" cols="12" sm="6" md="6" lg="6">
        <v-card class="analysis-card">
          <div class="card-header">
            <h3 class="card-title">{{ $t('survey.questionAnalysis.nationality') }}</h3>
            <p class="card-subtitle">{{ nationalityCount }} {{ $t('survey.nationalities') }}</p>
          </div>
          <div class="chart-container">
            <apexchart
              type="bar"
              :options="nationalityChartOptions"
              :series="nationalitySeries"
              width="100%"
              @data-point-selection="handleNationalitySelection"
            ></apexchart>
          </div>
        </v-card>
      </v-col>

      <!-- User Job Analysis -->
      <v-col v-if="hasJobData" cols="12" sm="6" md="6" lg="6">
        <v-card class="analysis-card">
          <div class="card-header">
            <h3 class="card-title">{{ $t('survey.questionAnalysis.jobTitle') }}</h3>
            <p class="card-subtitle">{{ jobCount }} {{ $t('survey.jobTitles') }}</p>
          </div>
          <div class="chart-container">
            <apexchart
              type="bar"
              :options="jobChartOptions"
              :series="jobSeries"
              width="100%"
              height="350"
              @data-point-selection="handleJobSelection"
            ></apexchart>
          </div>
        </v-card>
      </v-col>

      <!-- User Department Analysis -->
      <v-col v-if="hasDepartmentData" cols="12" sm="6" md="6" lg="6">
        <v-card class="analysis-card">
          <div class="card-header">
            <h3 class="card-title">{{ $t('survey.questionAnalysis.department') }}</h3>
            <p class="card-subtitle">{{ departmentCount }} {{ $t('survey.departments') }}</p>
          </div>
          <div class="chart-container">
            <apexchart
              type="bar"
              :options="departmentChartOptions"
              :series="departmentSeries"
              width="100%"
              height="350"
              @data-point-selection="handleDepartmentSelection"
            ></apexchart>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog for showing details -->
    <v-dialog v-model="detailsDialog" max-width="600" class="details-dialog">
      <v-card>
        <v-card-title class="dialog-title">{{ detailsDialogTitle }}</v-card-title>
        <v-card-text>
          <v-list class="details-list">
            <v-list-item v-for="(item, index) in detailsDialogItems" :key="index" class="details-item">
              <v-list-item-content>
                <v-list-item-title class="detail-label">{{ item.label }}</v-list-item-title>
                <v-list-item-subtitle v-if="item.value" class="detail-value">{{ item.value }}</v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-action>
                <span class="detail-count">{{ item.count }}</span>
              </v-list-item-action>
            </v-list-item>
          </v-list>
        </v-card-text>
        <v-card-actions class="dialog-actions">
          <v-spacer></v-spacer>
          <v-btn color="primary" variant="flat" @click="detailsDialog = false">
            {{ $t('common.close') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Text Responses Dialog -->
    <v-dialog v-model="textResponsesDialog" max-width="800">
      <v-card>
        <v-card-title>{{ $t('survey.allTextResponses') }}</v-card-title>
        <v-card-text>
          <div v-for="(response, index) in allTextResponses" :key="index" class="text-response-item">
            <p class="response-text">{{ response }}</p>
            <v-divider v-if="index < allTextResponses.length - 1"></v-divider>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="textResponsesDialog = false">
            {{ $t('common.close') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import type { ReportSdssSurveyQuestionResponse } from '@/dto/report/ReportSdssSurveyRoundResponse'
import { mockQuestionData } from '@/views/test/mocks/questionData.mock'

export default defineComponent({
  name: 'SurveyQuestionAnalysis',
  props: {
    questionData: {
      type: Object as () => ReportSdssSurveyQuestionResponse,
      required: true,
      default: () => mockQuestionData
    }
  },
  setup(props) {
    const { t } = useI18n()
    const detailsDialog = ref(false)
    const detailsDialogTitle = ref('')
    const detailsDialogItems = ref<Array<{ label: string; value: string; count: number }>>([])
    const textResponsesDialog = ref(false)

    // Question type checks
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

    // Text responses handling
    const textResponsesPreview = computed(() => {
      if (!isText.value || !props.questionData.textResponses) return ''
      const responses = props.questionData.textResponses.slice(0, 3)
      return responses.join('\n\n---\n\n')
    })

    const allTextResponses = computed(() => {
      return isText.value ? props.questionData.textResponses || [] : []
    })

    const showAllTextResponses = () => {
      textResponsesDialog.value = true
    }

    // True/False chart data
    const trueFalseSeries = computed(() => {
      if (!isTrueFalse.value) return []
      return [
        props.questionData.userAnswersStatistics.trueCount || 0,
        props.questionData.userAnswersStatistics.falseCount || 0
      ]
    })

    const trueFalseChartOptions = computed(() => ({
      chart: {
        type: 'donut',
        animations: {
          enabled: true,
          easing: 'easeinout',
          speed: 800
        }
      },
      labels: [t('common.true'), t('common.false')],
      colors: ['#008755', '#FF0000'],
      plotOptions: {
        pie: {
          donut: {
            size: '70%',
            labels: {
              show: true,
              total: {
                show: true,
                label: t('sdssSurvey.report.questionAnalysis.totalResponses'),
                formatter: () => totalResponses.value.toString(),
                color: 'var(--text-primary)'
              }
            }
          }
        }
      },
      dataLabels: {
        enabled: true,
        style: {
          fontSize: '12px',
          fontWeight: 500
        }
      },
      legend: {
        position: 'bottom'
      }
    }))

    // Handle true/false selection
    const handleTrueFalseSelection = (event: any, chartContext: any, config: any) => {
      if (!isTrueFalse.value) return
      const selectedIndex = config.dataPointIndex
      const isTrue = selectedIndex === 0

      detailsDialogTitle.value = isTrue ? t('common.true') : t('common.false')
      detailsDialogItems.value = [
        {
          label: t('survey.responses'),
          value: '',
          count: isTrue
            ? props.questionData.userAnswersStatistics.trueCount
            : props.questionData.userAnswersStatistics.falseCount
        }
      ]
      detailsDialog.value = true
    }

    // Computed properties for data checks
    const hasOptionsData = computed(() => {
      return (
        props.questionData?.options?.length > 0 &&
        props.questionData?.userAnswersStatistics?.optionStatistics?.length > 0
      )
    })

    const hasQualificationData = computed(() => {
      return (
        props.questionData?.questionAnalysis?.userQualificationSpecificationAnalysis &&
        Object.keys(props.questionData.questionAnalysis.userQualificationSpecificationAnalysis).length > 0
      )
    })

    const hasNationalityData = computed(() => {
      return (
        props.questionData?.questionAnalysis?.userNationalityAnalysis &&
        Object.keys(props.questionData.questionAnalysis.userNationalityAnalysis).length > 0
      )
    })

    const hasJobData = computed(() => {
      return (
        props.questionData?.questionAnalysis?.userJobAnalysis &&
        Object.keys(props.questionData.questionAnalysis.userJobAnalysis).length > 0
      )
    })

    const hasDepartmentData = computed(() => {
      return (
        props.questionData?.questionAnalysis?.userDepartmentAnalysis &&
        Object.keys(props.questionData.questionAnalysis.userDepartmentAnalysis).length > 0
      )
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

    // Computed properties for counts
    const totalResponses = computed(() => {
      return props.questionData?.userAnswersStatistics?.totalCount || 0
    })

    const qualificationCount = computed(() => {
      return hasQualificationData.value
        ? Object.keys(props.questionData.questionAnalysis.userQualificationSpecificationAnalysis).length
        : 0
    })

    const nationalityCount = computed(() => {
      return hasNationalityData.value
        ? Object.keys(props.questionData.questionAnalysis.userNationalityAnalysis).length
        : 0
    })

    const jobCount = computed(() => {
      return hasJobData.value ? Object.keys(props.questionData.questionAnalysis.userJobAnalysis).length : 0
    })

    const departmentCount = computed(() => {
      return hasDepartmentData.value
        ? Object.keys(props.questionData.questionAnalysis.userDepartmentAnalysis).length
        : 0
    })

    const probabilityValue = computed(() => {
      return props.questionData?.userAnswersStatistics?.averageProbabilityOfOccurrence || 0
    })

    const impactValue = computed(() => {
      return props.questionData?.userAnswersStatistics?.averageImpactSeverity || 0
    })

    // Chart data
    const optionsSeries = computed(() => {
      if (!hasOptionsData.value) return []
      return props.questionData.userAnswersStatistics.optionStatistics.map((opt) => opt.count)
    })

    const optionsChartOptions = computed(() => ({
      chart: {
        type: 'donut',
        animations: {
          enabled: true,
          easing: 'easeinout',
          speed: 800
        }
      },
      labels: hasOptionsData.value ? props.questionData.options.map((opt) => opt.localOption) : [],
      colors: ['var(--color-success)', 'var(--color-primary)', 'var(--color-error)', '#AE1368', '#B35F00'],
      plotOptions: {
        pie: {
          donut: {
            size: '70%',
            labels: {
              show: true,
              total: {
                show: true,
                label: t('sdssSurvey.report.questionAnalysis.totalResponses'),
                formatter: () => totalResponses.value.toString(),
                color: 'var(--text-primary)'
              },
              value: {
                color: 'var(--text-primary)',
                fontSize: '16px',
                fontWeight: 600
              }
            }
          }
        }
      },
      dataLabels: {
        enabled: true,
        style: {
          fontSize: '12px',
          fontWeight: 500
        },
        dropShadow: {
          enabled: false
        }
      },
      legend: {
        position: 'bottom',
        horizontalAlign: 'center',
        fontSize: '12px',
        markers: {
          width: 10,
          height: 10,
          radius: 10
        },
        itemMargin: {
          horizontal: 8,
          vertical: 4
        }
      },
      tooltip: {
        enabled: true,
        y: {
          formatter: (val: number) => `${val} (${Math.round((val / totalResponses.value) * 100)}%)`
        }
      }
    }))

    const qualificationSeries = computed(() => [
      {
        name: t('survey.responses'),
        data: hasQualificationData.value
          ? Object.values(props.questionData.questionAnalysis.userQualificationSpecificationAnalysis)
          : []
      }
    ])

    const qualificationChartOptions = computed(() => ({
      chart: {
        type: 'bar',
        animations: {
          enabled: true,
          easing: 'easeinout',
          speed: 800
        }
      },
      plotOptions: {
        bar: {
          horizontal: true,
          borderRadius: 4,
          barHeight: '70%',
          distributed: false
        }
      },
      colors: ['var(--color-primary)'],
      xaxis: {
        categories: hasQualificationData.value
          ? Object.keys(props.questionData.questionAnalysis.userQualificationSpecificationAnalysis)
          : [],
        labels: {
          formatter: (value: string) => (value.length > 15 ? `${value.substring(0, 15)}...` : value),
          style: {
            colors: 'var(--text-secondary)'
          }
        },
        axisBorder: {
          show: false
        },
        axisTicks: {
          show: false
        }
      },
      yaxis: {
        labels: {
          style: {
            colors: 'var(--text-primary)'
          }
        }
      },
      dataLabels: {
        enabled: true,
        textAnchor: 'start',
        style: {
          colors: ['#fff']
        },
        offsetX: 10
      },
      tooltip: {
        enabled: true,
        y: {
          formatter: (val: number) => `${val} responses`
        }
      },
      grid: {
        borderColor: 'var(--border-color)'
      }
    }))

    // Enhanced Nationality Analysis - changed from pie to bar chart for better readability
    const nationalitySeries = computed(() => [
      {
        name: t('survey.responses'),
        data: hasNationalityData.value
          ? Object.values(props.questionData.questionAnalysis.userNationalityAnalysis)
          : []
      }
    ])

    const nationalityChartOptions = computed(() => ({
      chart: {
        type: 'bar',
        animations: {
          enabled: true,
          easing: 'easeinout',
          speed: 800
        }
      },
      plotOptions: {
        bar: {
          horizontal: true,
          borderRadius: 4,
          barHeight: '70%',
          distributed: true
        }
      },
      colors: ['#FDBA8C', '#F97316', '#EA580C', '#C2410C', '#9A3412', '#7C2D12', '#5C1D09'],
      xaxis: {
        categories: hasNationalityData.value
          ? Object.keys(props.questionData.questionAnalysis.userNationalityAnalysis)
          : [],
        labels: {
          formatter: (value: string) => value, // Show full country names
          style: {
            colors: 'var(--text-secondary)'
          }
        },
        axisBorder: {
          show: false
        },
        axisTicks: {
          show: false
        }
      },
      yaxis: {
        labels: {
          style: {
            colors: 'var(--text-primary)'
          }
        }
      },
      dataLabels: {
        enabled: true,
        textAnchor: 'start',
        style: {
          colors: ['#fff']
        },
        offsetX: 10
      },
      tooltip: {
        enabled: true,
        y: {
          formatter: (val: number) => `${val} responses (${Math.round((val / totalResponses.value) * 100)}%)`
        }
      },
      grid: {
        borderColor: 'var(--border-color)'
      },
      legend: {
        show: false
      }
    }))

    const jobSeries = computed(() => [
      {
        name: t('survey.responses'),
        data: hasJobData.value ? Object.values(props.questionData.questionAnalysis.userJobAnalysis) : []
      }
    ])

    const jobChartOptions = computed(() => ({
      chart: {
        type: 'bar',
        animations: {
          enabled: true,
          easing: 'easeinout',
          speed: 800
        }
      },
      plotOptions: {
        bar: {
          horizontal: true,
          borderRadius: 4,
          barHeight: '70%'
        }
      },
      colors: ['var(--color-primary)'],
      xaxis: {
        categories: hasJobData.value ? Object.keys(props.questionData.questionAnalysis.userJobAnalysis) : [],
        labels: {
          formatter: (value: string) => (value.length > 15 ? `${value.substring(0, 15)}...` : value),
          style: {
            colors: 'var(--text-secondary)'
          }
        },
        axisBorder: {
          show: false
        },
        axisTicks: {
          show: false
        }
      },
      yaxis: {
        labels: {
          style: {
            colors: 'var(--text-primary)'
          }
        }
      },
      dataLabels: {
        enabled: true,
        textAnchor: 'start',
        style: {
          colors: ['#fff']
        },
        offsetX: 10
      },
      tooltip: {
        enabled: true,
        y: {
          formatter: (val: number) => `${val} responses`
        }
      },
      grid: {
        borderColor: 'var(--border-color)'
      }
    }))

    const departmentSeries = computed(() => [
      {
        name: t('survey.responses'),
        data: hasDepartmentData.value ? Object.values(props.questionData.questionAnalysis.userDepartmentAnalysis) : []
      }
    ])

    const departmentChartOptions = computed(() => ({
      chart: {
        type: 'bar',
        animations: {
          enabled: true,
          easing: 'easeinout',
          speed: 800
        }
      },
      plotOptions: {
        bar: {
          horizontal: true,
          borderRadius: 4,
          barHeight: '70%'
        }
      },
      colors: ['var(--color-success)'],
      xaxis: {
        categories: hasDepartmentData.value
          ? Object.keys(props.questionData.questionAnalysis.userDepartmentAnalysis)
          : [],
        labels: {
          formatter: (value: string) => (value.length > 15 ? `${value.substring(0, 15)}...` : value),
          style: {
            colors: 'var(--text-secondary)'
          }
        },
        axisBorder: {
          show: false
        },
        axisTicks: {
          show: false
        }
      },
      yaxis: {
        labels: {
          style: {
            colors: 'var(--text-primary)'
          }
        }
      },
      dataLabels: {
        enabled: true,
        textAnchor: 'start',
        style: {
          colors: ['#fff']
        },
        offsetX: 10
      },
      tooltip: {
        enabled: true,
        y: {
          formatter: (val: number) => `${val} responses`
        }
      },
      grid: {
        borderColor: 'var(--border-color)'
      }
    }))

    // Handler methods
    const handleOptionSelection = (event: any, chartContext: any, config: any) => {
      if (!hasOptionsData.value) return
      const selectedIndex = config.dataPointIndex
      const selectedOption = props.questionData.options[selectedIndex]
      const stats = props.questionData.userAnswersStatistics.optionStatistics.find(
        (opt: any) => opt.id === selectedOption.id
      )
      detailsDialogTitle.value = selectedOption.localOption
      detailsDialogItems.value = [{ label: t('survey.responses'), value: '', count: stats?.count || 0 }]
      detailsDialog.value = true
    }

    const handleQualificationSelection = (event: any, chartContext: any, config: any) => {
      if (!hasQualificationData.value) return
      const qualifications = Object.entries(props.questionData.questionAnalysis.userQualificationSpecificationAnalysis)
      detailsDialogTitle.value = t('survey.questionAnalysis.qualification')
      detailsDialogItems.value = qualifications.map(([qualification, count]) => ({
        label: qualification,
        value: '',
        count: count as number
      }))
      detailsDialog.value = true
    }

    const handleNationalitySelection = (event: any, chartContext: any, config: any) => {
      if (!hasNationalityData.value) return
      const nationalities = Object.entries(props.questionData.questionAnalysis.userNationalityAnalysis)
      detailsDialogTitle.value = t('survey.questionAnalysis.nationality')
      detailsDialogItems.value = nationalities.map(([nation, count]) => ({
        label: nation,
        value: '',
        count: count as number
      }))
      detailsDialog.value = true
    }

    const handleJobSelection = (event: any, chartContext: any, config: any) => {
      if (!hasJobData.value) return
      const jobs = Object.entries(props.questionData.questionAnalysis.userJobAnalysis)
      detailsDialogTitle.value = t('survey.questionAnalysis.jobTitle')
      detailsDialogItems.value = jobs.map(([job, count]) => ({
        label: job,
        value: '',
        count: count as number
      }))
      detailsDialog.value = true
    }

    const handleDepartmentSelection = (event: any, chartContext: any, config: any) => {
      if (!hasDepartmentData.value) return
      const departments = Object.entries(props.questionData.questionAnalysis.userDepartmentAnalysis)
      detailsDialogTitle.value = t('survey.questionAnalysis.department')
      detailsDialogItems.value = departments.map(([dept, count]) => ({
        label: dept,
        value: '',
        count: count as number
      }))
      detailsDialog.value = true
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
      hasAverageData,
      hasProbabilityData,
      hasImpactData,
      totalResponses,
      qualificationCount,
      nationalityCount,
      jobCount,
      departmentCount,
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
      handleOptionSelection,
      handleQualificationSelection,
      handleNationalitySelection,
      handleJobSelection,
      handleDepartmentSelection,
      isMultipleChoice,
      isSingleChoice,
      isTrueFalse,
      isText,
      textResponsesPreview,
      allTextResponses,
      textResponsesDialog,
      showAllTextResponses,
      trueFalseSeries,
      trueFalseChartOptions,
      handleTrueFalseSelection
    }
  }
})
</script>

<style lang="scss">
.survey-question-analysis {
  --color-primary: #1570ef;
  --color-success: #008755;
  --color-error: #d92d20;
  --color-warning: #b35f00;
  --text-primary: #1d2939;
  --text-secondary: #667085;
  --border-color: #eaecf0;
  --card-shadow: 0 1px 3px rgba(16, 24, 40, 0.1), 0 1px 2px rgba(16, 24, 40, 0.06);
  --card-radius: 12px;

  .analysis-row {
    margin: -8px;
  }

  .analysis-card {
    height: 100%;
    margin: 8px;
    border-radius: var(--card-radius);
    box-shadow: var(--card-shadow) !important;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 6px -1px rgba(16, 24, 40, 0.1),
      0 2px 4px -1px rgba(16, 24, 40, 0.06) !important;
    }

    .card-header {
      padding: 16px 16px 8px;
      border-bottom: 1px solid var(--border-color);

      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 4px;
      }

      .card-subtitle {
        font-size: 14px;
        color: var(--text-secondary);
        margin: 0;
      }
    }

    .chart-container {
      padding: 12px;
      min-height: 280px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .stats-container {
      padding: 16px;

      .stat-item {
        margin-bottom: 24px;

        &:last-child {
          margin-bottom: 0;
        }

        .stat-meta {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;

          .stat-label {
            font-size: 14px;
            color: var(--text-primary);
            font-weight: 500;
          }

          .stat-value {
            font-size: 14px;
            color: var(--text-secondary);
            font-weight: 600;
          }
        }
      }
    }
  }

  .details-dialog {
    .dialog-title {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
      padding-bottom: 12px;
      border-bottom: 1px solid var(--border-color);
    }

    .details-list {
      padding: 0;

      .details-item {
        padding: 12px 16px;
        border-bottom: 1px solid var(--border-color);

        &:last-child {
          border-bottom: none;
        }

        .detail-label {
          font-size: 14px;
          font-weight: 500;
          color: var(--text-primary);
        }

        .detail-value {
          font-size: 13px;
          color: var(--text-secondary);
        }

        .detail-count {
          background-color: var(--color-primary);
          color: white;
          border-radius: 16px;
          padding: 4px 10px;
          font-size: 12px;
          font-weight: 600;
          min-width: 36px;
          text-align: center;
        }
      }
    }

    .dialog-actions {
      padding: 16px;
      border-top: 1px solid var(--border-color);
    }
  }

  @media (max-width: 959px) {
    .analysis-card {
      .chart-container {
        min-height: 240px;
      }
    }
  }

  @media (max-width: 599px) {
    .analysis-row {
      margin: -4px;
    }

    .analysis-card {
      margin: 4px;
    }
  }
}

.apexcharts-text {
  text-anchor: start;
}
</style>
