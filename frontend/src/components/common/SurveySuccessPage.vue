<template>
  <v-container class="d-flex justify-center align-center" fluid>
    <!-- Success Card -->
    <v-card class="pa-5 success-survey-page-view-card" elevation="2" rounded>
      <v-card-title class="text-center">
        <v-icon color="green" size="200">mdi-check-circle</v-icon>
      </v-card-title>

      <v-card-subtitle class="text-center mb-4 success-survey-page-view-card-title">
        <span class="display-1 font-dubai-bold">
          {{ $t('sdssSurvey.userAnswers.surveySubmitted') }}
        </span>
      </v-card-subtitle>

      <v-card-text class="text-center success-survey-page-view-card-text">
        <p class="font-dubai-regular">
          {{ $t('sdssSurvey.userAnswers.surveyMessage') }}
        </p>
        <v-col cols="12"></v-col>
        <v-divider class="success-survey-page-view-card-text-divider"></v-divider>
        <v-col cols="12"></v-col>
        <p class="text-success-survey-hint font-dubai-regular-italic">
          {{ $t('sdssSurvey.userAnswers.updateHint') }}
        </p>
      </v-card-text>

      <!-- Action Buttons -->
      <v-row class="justify-center">
        <v-col cols="12" sm="5">
          <v-btn block class="success-survey-page-view-go-to-home-btn" @click="goToHome">
            {{ $t('sdssSurvey.userAnswers.goToHome') }}
          </v-btn>
        </v-col>
      </v-row>
      <v-col cols="12"></v-col>
      <FooterComponent :justify-content="'justify-content-start'"></FooterComponent>
    </v-card>

    <!-- Snackbar for Success Message -->
    <v-snackbar v-model="snackbar" color="green" timeout="3000" top right>
      {{ $t('sdssSurvey.userAnswers.snackbarMessage') }}
      <v-btn class="success-survey-page-circle-btn" text @click="snackbar = false">
        {{ $t('common.close') }}
      </v-btn>
    </v-snackbar>

    <!-- Footer -->
  </v-container>
</template>

<script lang="ts">
import FooterComponent from '@/components/common/FooterComponent.vue'
import { Fields } from '@/constants/DpConstants.ts'
import { watchEffect } from 'vue'
import { useI18n } from 'vue-i18n'

export default {
  name: 'SurveySuccessPage',
  components: { FooterComponent },
  data() {
    const { locale } = useI18n()
    const employeeLang: string = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    return {
      snackbar: true // This will control the display of the snackbar
    }
  },
  methods: {
    goToHome() {
      // Redirect to the homepage
      this.$router.push({ name: 'MainUsersSurveyHomeScreen' })
    }
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/css/variables' as *;

/* Ensure responsiveness for mobile view */
@media (max-width: 600px) {
  .success-survey-page-view-card {
    padding: 20px;
  }
  .display-1 {
    font-size: 1.5rem;
  }
}

.success-survey-page-view-card {
  width: 80%;
}

.success-survey-page-view-go-to-home-btn {
  background-color: $sdss-questions-color;
  color: white;
}

.text-success-survey-hint {
  font-size: 0.9rem;
  color: gray;
}

.success-survey-page-view-card-text-divider {
  width: 35%;
  justify-self: center;
}

.success-survey-page-circle-btn {
  border-radius: 50%;
  background-color: $sdss-questions-color;
  color: $white-color;
}

.success-survey-page-circle-btn:hover {
  background-color: #17a2b8;
}
</style>
