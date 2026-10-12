<template>
  <v-container class="custom-surveys-report-container">
    <!-- Header Section with Animation -->
    <v-fade-transition>
      <v-row class="mb-3 header-section">
        <v-col cols="auto">
          <CustomGoBackButton :is-arabic="isLanguageAr()" route-to="MainUsersSurveyHomeScreen"></CustomGoBackButton>
        </v-col>
        <v-col>
          <h1 class="text-h4 font-bukra-bold custom-survey-report-main-title mt-0">
            <v-icon color="green" class="title-icon">mdi mdi-chart-arc</v-icon>
            {{ $t('sdssSurvey.report.main-title') }}
          </h1>
        </v-col>
      </v-row>
    </v-fade-transition>

    <!-- Search Field with Animation -->
    <v-slide-y-transition>
      <div class="mb-3 search-section d-flex p-2">
        <v-col cols="12" md="6" lg="4">
          <v-text-field
            v-model="searchQuery"
            :label="$t('common.search')"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            clearable
            class="enhanced-search-field"
            @input="applySearch"
          >
            <template #prepend-inner>
              <v-icon color="primary" class="search-icon">mdi-magnify</v-icon>
            </template>
          </v-text-field>
        </v-col>
        <v-col cols="12" md="6" lg="8" class="d-flex align-center">
          <div class="search-stats">
            <v-chip color="primary" variant="outlined" class="stats-chip">
              {{ $t('common.totalResults') }}: {{ filteredSurveys.length }}
            </v-chip>
            <v-chip v-if="searchQuery" color="info" variant="outlined" class="stats-chip">
              {{ $t('common.searchResults') }}: {{ filteredSurveys.length }}
            </v-chip>
          </div>
        </v-col>
      </div>
    </v-slide-y-transition>

    <!-- Loading State -->
    <DubaiPoliceLoader
      :is-loading="isLoading"
      variant="badge"
      :heading="$t('common.loader.surveyReportsHeading')"
      :message="$t('common.loader.surveyReports')"
      :description="$t('common.loader.surveyReportsDescription')"
    />

    <!-- Responsive grid layout for surveys with staggered animations -->
    <v-slide-y-transition group>
      <v-row v-if="filteredSurveys.length > 0 && !isLoading" class="custom-survey-report-first-row">
        <v-col
          v-for="(round, index) in paginatedSurveys"
          :key="round.id"
          cols="12"
          class="survey-card-col"
          :style="{ animationDelay: `${index * 0.1}s` }"
        >
          <v-card class="custom-survey-report-info-card enhanced-card" elevation="4">
            <!-- Card Header -->
            <v-card-title class="custom-survey-report-info-card-title font-bukra-regular card-header">
              <div class="title-section">
                <h2 class="survey-title font-bukra-regular">{{ round.survey.localTitle }}</h2>
                <v-chip
                  class="custom-survey-report-info-card-chip status-chip"
                  :color="statusColor(round.status)"
                  text-color="white"
                  size="small"
                >
                  <v-icon start size="small">{{ getStatusIcon(round.status) }}</v-icon>
                  {{ round.localStatus }}
                </v-chip>
                <v-btn
                  icon
                  variant="text"
                  size="small"
                  class="action-btn"
                  @click="toggleCardDetails(getRoundId(round))"
                >
                  <v-icon class="custom-details-chevron-icon">
                    {{ isCardExpanded(getRoundId(round)) ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
                  </v-icon>
                </v-btn>
              </div>
              <v-spacer />
            </v-card-title>

            <!-- Card Subtitle -->
            <v-card-subtitle class="custom-survey-report-info-card-subtitle font-dubai-bold">
              <v-chip
                class="custom-survey-report-info-card-chip-local-title"
                color="blue"
                text-color="white"
                size="small"
              >
                <v-icon start size="small">mdi-tag</v-icon>
                {{ round.localTitle }}
              </v-chip>
            </v-card-subtitle>

            <!-- Card Content with Expandable Details -->
            <v-expand-transition>
              <div v-show="isCardExpanded(getRoundId(round))">
                <v-card-text class="custom-survey-report-info-card-text enhanced-content">
                  <v-row class="info-grid" no-gutters>
                    <!-- Survey Description -->
                    <v-col cols="12" md="6" lg="3" class="info-col">
                      <div class="info-field-container">
                        <div class="info-field-header">
                          <v-icon size="small" color="primary">mdi-text</v-icon>
                          <p class="info-field-label font-bukra-regular">
                            {{ $t('sdssSurvey.report.surveyDescription') }}
                          </p>
                        </div>
                        <p
                          class="info-field-value font-dubai-regular-italic text-subtitle-2 common-allow-text-wrapping"
                        >
                          {{ round.survey.localDescription || $t('common.notAvailable') }}
                        </p>
                      </div>
                    </v-col>

                    <!-- Round Description -->
                    <v-col cols="12" md="6" lg="3" class="info-col">
                      <div class="info-field-container">
                        <div class="info-field-header">
                          <v-icon size="small" color="primary">mdi-information</v-icon>
                          <p class="info-field-label font-bukra-regular">
                            {{ $t('sdssSurvey.report.roundDescription') }}
                          </p>
                        </div>
                        <p class="info-field-value font-dubai-regular-italic common-allow-text-wrapping">
                          {{ round.localDescription || $t('common.notAvailable') }}
                        </p>
                      </div>
                    </v-col>

                    <!-- Created Date -->
                    <v-col cols="12" md="6" lg="2" class="info-col">
                      <div class="info-field-container">
                        <div class="info-field-header">
                          <v-icon size="small" color="primary">mdi-calendar</v-icon>
                          <p class="info-field-label font-bukra-regular">
                            {{ $t('common.updatedAt') }}
                          </p>
                        </div>
                        <p class="info-field-value font-dubai-regular">
                          {{ formatDate(round.updatedAt || '') }}
                        </p>
                      </div>
                    </v-col>

                    <!-- Created By -->
                    <v-col cols="12" md="6" lg="2" class="info-col">
                      <div class="info-field-container">
                        <div class="info-field-header">
                          <v-icon size="small" color="primary">mdi-account</v-icon>
                          <p class="info-field-label font-bukra-regular">
                            {{ $t('common.createdBy') }}
                          </p>
                        </div>
                        <p class="info-field-value font-dubai-regular">
                          {{ round.createdBy || $t('common.unknown') }}
                        </p>
                      </div>
                    </v-col>

                    <!-- View Report Button -->
                    <v-col cols="12" lg="2" class="info-col action-col">
                      <div class="action-container">
                        <!-- Button moved to quick actions -->
                      </div>
                    </v-col>
                  </v-row>
                </v-card-text>
              </div>
            </v-expand-transition>

            <!-- Quick View Button (always visible) -->
            <v-card-actions class="quick-actions">
              <v-btn
                variant="text"
                color="primary"
                size="small"
                class="expand-btn mt-0"
                @click="toggleCardDetails(getRoundId(round))"
              >
                <v-icon>{{ isCardExpanded(getRoundId(round)) ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
                {{ isCardExpanded(getRoundId(round)) ? $t('common.showLess') : $t('common.showMore') }}
              </v-btn>
              <v-spacer />
              <v-btn class="dp-primary-sm-btn" elevation="2" @click="viewSurvey(round)">
                <v-icon class="custom-details-icon">mdi-eye</v-icon> &nbsp;
                {{ $t('common.viewReport') }}
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-slide-y-transition>

    <!-- Empty State with Animation -->
    <v-fade-transition>
      <v-row v-if="filteredSurveys.length === 0 && !isLoading">
        <v-col cols="12">
          <EmptyDataTable :image-size="128" :message="$t('sdssSurvey.report.noReportTillNow')"></EmptyDataTable>
        </v-col>
      </v-row>
    </v-fade-transition>

    <!-- Enhanced Pagination Controls -->
    <v-slide-y-transition>
      <v-row v-if="filteredSurveys.length > itemsPerPage" class="mt-3 pagination-section">
        <v-col cols="12" class="d-flex justify-center align-center flex-wrap">
          <div class="pagination-info">
            <span class="pagination-text">
              {{ $t('common.showing') }} {{ (currentPage - 1) * itemsPerPage + 1 }} -
              {{ Math.min(currentPage * itemsPerPage, filteredSurveys.length) }}
              {{ $t('common.of') }} {{ filteredSurveys.length }}
            </span>
          </div>
          <v-pagination
            v-model="currentPage"
            :length="totalPages"
            :total-visible="7"
            circle
            class="enhanced-pagination"
          ></v-pagination>
        </v-col>
      </v-row>
    </v-slide-y-transition>
  </v-container>
</template>

<script lang="ts">
import { computed, PropType, ref, watchEffect } from 'vue'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import { RoundReportMainResponse } from '@/dto/surveys/round/RoundReportMainResponse'
import CustomGoBackButton from '@/components/custom/CustomGoBackButton.vue'
import { useI18n } from 'vue-i18n'
import { Fields } from '@/constants/DpConstants'
import { format } from 'date-fns'

export default {
  name: 'SurveyReportsList',
  components: { CustomGoBackButton, EmptyDataTable },
  props: {
    roundSurveyResponse: {
      type: Array as PropType<RoundReportMainResponse[]>,
      required: true
    },
    defaultExpanded: {
      type: Boolean,
      default: true
    },
    allowCollapse: {
      type: Boolean,
      default: true
    }
  },
  emits: ['view-survey'],
  setup(props) {
    const { locale, t } = useI18n()
    const employeeLang: string = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'

    // Search and pagination variables
    const searchQuery = ref('')
    const currentPage = ref(1)
    const itemsPerPage = ref(5)
    const isLoading = ref(false)
    const expandedCards = ref<string[]>([])

    // Initialize expanded cards based on defaultExpanded prop
    watchEffect(() => {
      if (props.defaultExpanded && props.roundSurveyResponse.length > 0) {
        expandedCards.value = props.roundSurveyResponse.map((round) => (round as any).id)
      }
    })

    const filteredSurveys = computed(() => {
      if (!searchQuery.value) return props.roundSurveyResponse
      const query = searchQuery.value.toLowerCase()
      return props.roundSurveyResponse.filter(
        (round) =>
          round.survey.localTitle?.toLowerCase().includes(query) ||
          round.localTitle?.toLowerCase().includes(query) ||
          round.survey?.localDescription?.toLowerCase().includes(query) ||
          round?.localDescription?.toLowerCase().includes(query) ||
          round?.localStatus?.toLowerCase().includes(query) ||
          (round as any)?.createdBy?.toLowerCase().includes(query) ||
          (round as any)?.createdAt?.includes(query) ||
          (round as any)?.updatedAt?.includes(query)
      )
    })

    const totalPages = computed(() => Math.ceil(filteredSurveys.value.length / itemsPerPage.value))
    const paginatedSurveys = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage.value
      const end = start + itemsPerPage.value
      return filteredSurveys.value.slice(start, end)
    })

    const applySearch = () => {
      currentPage.value = 1 // Reset to the first page when searching
    }

    const formatDate = (dateString: string) => {
      if (!dateString) return t('common.notAvailable')
      try {
        return format(new Date(dateString), 'dd MMM yyyy HH:mm')
      } catch {
        return dateString
      }
    }

    const toggleCardDetails = (cardId: string) => {
      if (!props.allowCollapse) return // Don't allow collapsing if disabled

      const index = expandedCards.value.indexOf(cardId)
      if (index > -1) {
        expandedCards.value.splice(index, 1)
      } else {
        expandedCards.value.push(cardId)
      }
    }

    const getRoundId = (round: any) => {
      return round.id || round.roundId || round.surveyId || String(Math.random())
    }

    const isCardExpanded = (cardId: string) => {
      return expandedCards.value.includes(cardId)
    }

    const getStatusIcon = (status: string) => {
      const icons = {
        COMPLETED: 'mdi-check-circle',
        PENDING: 'mdi-clock',
        FAILED: 'mdi-close-circle',
        IN_PROGRESS: 'mdi-progress-clock'
      }
      return icons[status as keyof typeof icons] || 'mdi-help-circle'
    }

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    const isLanguageAr = (): boolean => {
      return employeeLang === 'ar'
    }

    return {
      isLanguageAr,
      searchQuery,
      currentPage,
      itemsPerPage,
      filteredSurveys,
      paginatedSurveys,
      totalPages,
      isLoading,
      expandedCards,
      applySearch,
      formatDate,
      toggleCardDetails,
      isCardExpanded,
      getRoundId,
      getStatusIcon
    }
  },
  methods: {
    statusColor(status) {
      return (
        {
          COMPLETED: 'green',
          PENDING: 'orange',
          FAILED: 'red',
          IN_PROGRESS: 'blue'
        }[status] || 'grey'
      )
    },
    viewSurvey(survey) {
      this.$emit('view-survey', survey)
    }
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/css/variables' as *;
@use 'sass:color';

// Enhanced Container Styles
.custom-surveys-report-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem 1rem;
  //background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  min-height: 100vh;
}

// Header Section with Enhanced Animation
.header-section {
  margin-bottom: 2rem;

  .animated-title {
    animation: slideInFromLeft 0.8s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;

    &::after {
      content: '';
      position: absolute;
      bottom: -8px;
      left: 0;
      width: 0;
      height: 3px;
      background: linear-gradient(90deg, $sdss-primary-color, color.adjust($sdss-primary-color, $lightness: -20%));
      animation: expandWidth 1s ease-out 0.5s forwards;
      border-radius: 2px;
    }

    .title-icon {
      margin-right: 0.75rem;
      animation: pulse 2s infinite;
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
    }
  }
}

// Enhanced Search Section
.search-section {
  margin-bottom: 2rem;
  animation: fadeInUp 0.6s ease-out 0.2s both;
  background-color: #fff;
  border-radius: 12px;

  .enhanced-search-field {
    position: relative;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:focus-within {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba($sdss-primary-color, 0.15);
    }

    .search-icon {
      transition: all 0.3s ease;
    }

    &:focus-within .search-icon {
      transform: scale(1.1) rotate(5deg);
      color: $sdss-primary-color;
    }
  }

  .search-stats {
    display: flex;
    gap: 1rem;
    flex-wrap: wrap;
    animation: fadeInRight 0.6s ease-out 0.4s both;

    .stats-chip {
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
        transition: left 0.5s ease;
      }

      &:hover {
        transform: translateY(-3px) scale(1.05);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);

        &::before {
          left: 100%;
        }
      }
    }
  }
}

// Enhanced Loading State
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 6rem 2rem;
  animation: fadeIn 0.5s ease-out;

  .loading-text {
    margin-top: 1.5rem;
    color: $sdss-primary-color;
    font-size: 1.2rem;
    font-weight: 500;
    animation: pulse 2s infinite;
  }
}

// Enhanced Card Styles with Improved Animations
.survey-card-col {
  animation: fadeInUp 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  opacity: 0;
  transform: translateY(40px);
  margin-bottom: 1.5rem;
}

.enhanced-card {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 20px;
  overflow: hidden;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(
      90deg,
      $sdss-primary-color,
      color.adjust($sdss-primary-color, $lightness: -20%),
      $sdss-primary-color
    );
    background-size: 200% 100%;
    animation: shimmer 3s ease-in-out infinite;
  }

  &:hover {
    // transform: translateY(-8px) scale(1.02);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);

    &::before {
      animation: shimmer 1s ease-in-out infinite;
    }
  }

  .card-header {
    background: #0da06e;
    color: white;
    padding: 12px;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover::before {
      opacity: 1;
    }

    .title-section {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
      gap: 1rem;
      width: 100%;
      position: relative;
      z-index: 1;

      .survey-title {
        font-size: 1.3rem;
        margin: 0;
        line-height: 1.4;
        flex: 1;
        font-weight: 700;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }

      .status-chip {
        font-weight: 600;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        flex-shrink: 0;
        position: relative;
        overflow: hidden;

        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
          transition: left 0.5s ease;
        }

        &:hover {
          transform: scale(1.1) rotate(2deg);

          &::before {
            left: 100%;
          }
        }
      }
    }

    .header-actions {
      position: relative;
      z-index: 1;

      .action-btn {
        color: white;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        border-radius: 50%;

        &:hover {
          background: rgba(255, 255, 255, 0.2);
          transform: scale(1.15) rotate(180deg);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        }
      }
    }
  }

  .custom-survey-report-info-card-subtitle {
    padding: 1.25rem 1.75rem 0.75rem;
    background: rgba($sdss-primary-color, 0.03);
    border-bottom: 1px solid rgba($sdss-primary-color, 0.1);

    .custom-survey-report-info-card-chip-local-title {
      font-weight: 600;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
        transition: left 0.5s ease;
      }

      &:hover {
        transform: scale(1.05);

        &::before {
          left: 100%;
        }
      }
    }
  }

  .enhanced-content {
    padding: 2rem;

    .info-grid {
      gap: 2rem;

      .info-col {
        .info-field-container {
          background: linear-gradient(135deg, rgba($sdss-primary-color, 0.02) 0%, rgba($sdss-primary-color, 0.05) 100%);
          border-radius: 16px;
          padding: 1.5rem;
          border: 1px solid rgba($sdss-primary-color, 0.08);
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          position: relative;
          overflow: hidden;

          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 3px;
            background: linear-gradient(
              90deg,
              $sdss-primary-color,
              color.adjust($sdss-primary-color, $lightness: -20%)
            );
            transform: scaleX(0);
            transition: transform 0.3s ease;
          }

          &:hover {
            background: linear-gradient(
              135deg,
              rgba($sdss-primary-color, 0.05) 0%,
              rgba($sdss-primary-color, 0.08) 100%
            );
            border-color: rgba($sdss-primary-color, 0.2);
            // transform: translateY(-4px) scale(1.02);
            box-shadow: 0 12px 24px rgba($sdss-primary-color, 0.1);

            &::before {
              transform: scaleX(1);
            }
          }

          .info-field-header {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            margin-bottom: 0.75rem;

            .info-field-label {
              font-size: 0.9rem;
              color: $sdss-primary-color;
              font-weight: 700;
              margin: 0;
              text-transform: uppercase;
              letter-spacing: 0.5px;
            }
          }

          .info-field-value {
            font-size: 0.95rem;
            color: #444;
            line-height: 1.5;
            margin: 0;
            font-weight: 500;
          }
        }

        &.action-col {
          .action-container {
            display: flex;
            justify-content: center;
            align-items: center;
            //height: 100%
            //min-height: 120px
          }
        }
      }
    }
  }

  .quick-actions {
    padding: 10px;
    background: linear-gradient(135deg, rgba($sdss-primary-color, 0.02) 0%, rgba($sdss-primary-color, 0.05) 100%);
    border-top: 1px solid rgba($sdss-primary-color, 0.1);
    position: relative;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, transparent, $sdss-primary-color, transparent);
    }

    .expand-btn {
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      border-radius: 8px;
      font-weight: 600;

      &:hover {
        background: rgba($sdss-primary-color, 0.15);
        transform: translateX(-4px);
        box-shadow: 0 4px 12px rgba($sdss-primary-color, 0.2);
      }
    }
  }
}

// Enhanced Button Styles
.custom-details-btn {
  height: auto !important;
  min-height: 56px !important;
  padding: 1rem 1.5rem !important;
  border-radius: 16px;
  margin-bottom: 0;
  position: relative;
  overflow: hidden;
  font-weight: 600;
  letter-spacing: 0.5px;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    transition: left 0.6s ease;
  }

  &:hover::before {
    left: 100%;
  }

  .custom-details-btn-content {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    gap: 0.75rem;
    position: relative;
    z-index: 1;
  }

  .custom-details-icon {
    font-size: 1.2rem;
    color: inherit;
    transition: all 0.3s ease;
  }

  .custom-details-text {
    font-size: 0.9rem;
    line-height: 1;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    font-weight: 600;
    font-family: bukrabold, serif !important;
  }

  &:hover {
    transform: translateY(-3px) scale(1.05);
    box-shadow: 0 12px 24px rgba($sdss-primary-color, 0.3);

    .custom-details-icon {
      transform: scale(1.2) rotate(10deg);
    }

    .custom-details-text {
      transform: translateX(2px);
    }
  }
}

// Enhanced Pagination
.pagination-section {
  margin-top: 3rem;
  animation: fadeInUp 0.6s ease-out 0.8s both;

  .pagination-info {
    margin-bottom: 0;

    .pagination-text {
      color: #666;
      font-size: 1rem;
      font-weight: 500;
    }
  }

  .enhanced-pagination {
    :deep(.v-pagination__item) {
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      border-radius: 12px;
      margin: 0 0.25rem;

      &:hover {
        transform: scale(1.15);
        box-shadow: 0 4px 12px rgba($sdss-primary-color, 0.2);
      }

      &.v-pagination__item--active {
        background: linear-gradient(
          135deg,
          $sdss-primary-color 0%,
          color.adjust($sdss-primary-color, $lightness: -15%) 100%
        );
        color: white;
        box-shadow: 0 6px 16px rgba($sdss-primary-color, 0.3);
        transform: scale(1.1);
      }
    }
  }
}

// Enhanced Animations
@keyframes slideInFromLeft {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes expandWidth {
  from {
    width: 0;
  }
  to {
    width: 100%;
  }
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

// Enhanced Responsive Design
@media (max-width: 1200px) {
  .enhanced-content .info-grid .info-col {
    margin-bottom: 1.5rem;
  }
}

@media (max-width: 960px) {
  .custom-surveys-report-container {
    padding: 1rem 0.5rem;
  }

  .enhanced-card {
    .card-header {
      padding: 1.25rem;

      .title-section .survey-title {
        font-size: 1.1rem;
      }
    }

    .enhanced-content {
      padding: 1.5rem;

      .info-grid {
        gap: 1.5rem;

        .info-col {
          .info-field-container {
            padding: 1.25rem;
          }
        }
      }
    }
  }

  .search-section .search-stats {
    justify-content: center;
  }
}

@media (max-width: 600px) {
  .enhanced-card {
    .card-header {
      flex-direction: column;
      gap: 1rem;
      text-align: center;

      .title-section {
        align-items: center;
        flex-direction: column;
        gap: 0.75rem;
      }
    }

    .quick-actions {
      flex-direction: column;
      gap: 1rem;
      text-align: center;

      .expand-btn {
        align-self: center;
      }
    }
  }

  .pagination-section {
    .pagination-info {
      text-align: center;
    }
  }
}

// Legacy styles for backward compatibility
.text-success {
  color: green;
}

.text-warning {
  color: orange;
}

.text-error {
  color: red;
}

.custom-survey-report-main-title {
  margin-top: 1%;
}

.v-btn {
  margin-top: 10px;
  padding: 8px;
}

.v-icon {
  font-size: 24px;
}

.custom-survey-report-info-card-chip {
  background-color: $white-color;
}

.v-pagination {
  :deep(.v-pagination__item) {
    box-shadow: none;
    border: 1px solid #ddd;
  }

  :deep(.v-pagination__item--active) {
    background-color: $sdss-primary-color;
    color: white;
  }
}

.custom-details-btn-col {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.info-field-container {
  display: grid;
  grid-template-rows: auto 1fr;
  height: 100%;
  min-height: 80px;

  .info-field-label {
    font-size: 0.75rem;
    color: $sdss-primary-color;
    display: flex;
    align-items: flex-end;
    padding-bottom: 4px;
    line-height: 1.2;
  }

  .info-field-value {
    font-size: 0.85rem;
    color: #555;
    align-self: start;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: normal;
  }
}

@media (max-width: 960px) {
  .custom-survey-report-info-card-text {
    .v-col {
      margin-bottom: 16px;
    }
  }

  .custom-details-btn-col {
    justify-content: center;
    margin-top: 16px;
  }
}

.custom-details-chevron-icon {
  color: #000000;
}

</style>
