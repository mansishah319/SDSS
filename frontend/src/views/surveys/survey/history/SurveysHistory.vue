<template>
  <v-container class="custom-surveys-history-container">
    <v-row class="mb-3">
      <v-col cols="auto">
        <CustomGoBackButton :is-arabic="isLanguageAr()" route-to="MainUsersSurveyHomeScreen"></CustomGoBackButton>
      </v-col>
      <v-col>
        <h1 class="text-h4 font-dubai-bold custom-surveys-history-main-title">
          <v-icon color="green">mdi-history</v-icon>
          {{ $t('sdssSurvey.history.main-title') }}
        </h1>
      </v-col>
    </v-row>

    <!-- Responsive grid layout for surveys -->
    <v-row v-if="surveysData.length > 0" class="custom-survey-history-first-row">
      <v-col v-for="survey in surveysData" :key="survey.id" cols="12">
        <v-card class="custom-survey-history-info-card">
          <v-card-title class="custom-survey-history-info-card-title font-dubai-bold">
            <div>
              <h2>{{ survey.localTitle }}</h2>
            </div>
            <v-spacer />
            <!-- Chip for survey status -->
            <v-chip
              class="custom-survey-history-info-card-chip"
              :color="statusColor(survey.activeRoundResponse.status)"
              text-color="white"
              small
            >
              {{ survey.activeRoundResponse.localStatus }}
            </v-chip>
          </v-card-title>
          <v-card-subtitle class="custom-survey-history-info-card-subtitle font-bukra-bold">
            <!-- Chip for active round response localTitle -->
            <v-chip class="custom-survey-history-info-card-chip-local-title" color="blue" text-color="white" small>
              {{ survey.activeRoundResponse.localTitle }}
            </v-chip>
          </v-card-subtitle>
          <v-card-text class="custom-survey-history-info-card-text">
            <v-row class="d-flex" align="center">
              <v-col cols="4">
                <p class="text-subtitle-2 font-bukra-regular">
                  {{ survey.localDescription }}
                </p>
              </v-col>

              <v-col cols="6">
                <p class="font-bukra-regular">{{ survey.activeRoundResponse.localDescription }}</p>
              </v-col>
              <v-col class="d-flex justify-end" cols="2">
                <v-btn icon @click="viewSurvey(survey)">
                  <v-icon>mdi-eye</v-icon>
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <v-row v-else>
      <v-col cols="12">
        <EmptyDataTable :image-size="128" :message="$t('sdssSurvey.history.noHistoryTillNow')"></EmptyDataTable>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { PropType, watchEffect } from 'vue'
import PublicEndUserSurveyResponse from '@/dto/surveys/public/PublicEndUserSurveyResponse.ts'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import CustomGoBackButton from '@/components/custom/CustomGoBackButton.vue'
import { useI18n } from 'vue-i18n'
import { Fields } from '@/constants/DpConstants.ts'

export default {
  name: 'SurveyHistory',
  components: { CustomGoBackButton, EmptyDataTable },
  props: {
    surveysData: {
      type: Array as PropType<PublicEndUserSurveyResponse[]>,
      required: true
    }
  },
  emits: ['view-survey'],
  setup() {
    const { locale } = useI18n()

    const employeeLang: string = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'

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
      isLanguageAr
    }
  },
  methods: {
    statusColor(status) {
      // Return color for the chip based on the status
      return (
        {
          COMPLETED: 'green',
          PENDING: 'orange',
          FAILED: 'red'
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

.text-success {
  color: green;
}

.text-warning {
  color: orange;
}

.text-error {
  color: red;
}

.custom-surveys-history-container {
  max-width: 1200px;
  margin: 0 auto;
}

.custom-survey-history-info-card {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  margin-bottom: 1rem;
}

.custom-survey-history-info-card-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 7px 16px;
  background-color: #0da06e;
  color: $white-color;
}

.custom-survey-history-info-card-subtitle {
  font-size: 0.875rem;
  font-weight: 500;
  padding-top: 2%;
  justify-self: center;
}

.custom-survey-history-info-card-text {
  font-size: 0.9rem;
  color: #555;
}

.v-btn {
  margin-top: 10px;
  padding: 8px;
}

.v-icon {
  font-size: 24px;
}

.custom-survey-history-info-card-chip {
  background-color: $white-color;
}

.custom-surveys-history-main-title {
  margin-top: 1.2%;
  font-family: bukrabold;
}
</style>
