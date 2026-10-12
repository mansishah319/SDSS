<template>
  <div v-if="isAuthorized">
    <SurveyHistory v-if="!selectedSurvey" :surveys-data="surveys" @view-survey="handleViewSurvey" />
    <SurveyDetails v-else :survey="selectedSurvey" @go-back="handleGoBack" />
    <v-col cols="12" class="d-flex justify-center">
      <div class="mt-3 custom-main-survey-history-app-back-button">
        <BackButton
          :redirect-path="redirectToSurveyHomePage"
          :title="backButtonTitle"
          :icon="backButtonIcon"
          :color="backButtonColor"
        />
      </div>
    </v-col>
  </div>
  <!-- Access Denied View -->
  <AccessDeniedView
    v-else-if="!isAuthorized"
    :back-icon="backButtonIcon"
    :back-title="backButtonTitle"
    :back-color="backButtonColor"
    :back-redirect-path="redirectToSurveyHomePage"
  ></AccessDeniedView>
  <!-- Footer -->
  <FooterComponent :justify-content="'justify-content-start'"></FooterComponent>
</template>

<script lang="ts">
import SurveyHistory from '@/views/surveys/survey/history/SurveysHistory.vue'
import SurveyDetails from '@/views/surveys/survey/history/SurveyDetails.vue'
import FooterComponent from '@/components/common/FooterComponent.vue'
import { useSurveyTokenStore } from '@/store/useSurveyTokenStore.ts'
import { useI18n } from 'vue-i18n'
import { onMounted, ref, watchEffect } from 'vue'
import { Fields, SecurityConstants, URIS } from '@/constants/DpConstants'
import PublicEndUserSurveyResponse from '@/dto/surveys/public/PublicEndUserSurveyResponse'
import { getPublicSurveyAuth } from '@/utils/GenericApiHelper.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { AxiosResponse } from 'axios'
import { toast } from 'vue3-toastify'
import AccessDeniedView from '@/views/common/AccessDeniedView.vue'
import BackButton from '@/components/common/BackButton.vue'
import { useSidebarStore } from '@/store/sidebar.ts'

export default {
  name: 'SurveyHistoryApp',
  components: { BackButton, AccessDeniedView, FooterComponent, SurveyDetails, SurveyHistory },
  data() {
    const sidebarStore = useSidebarStore()
    const surveyTokenStore = useSurveyTokenStore()
    const { locale } = useI18n()
    const isAuthorized = ref<boolean>(false)
    const surveyToken = ref(surveyTokenStore.surveyToken || localStorage.getItem(SecurityConstants.Survey_TOKEN))
    const employeeLang: string = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'

    const historySurveyRoundsResponse = ref<PublicEndUserSurveyResponse[]>([] as PublicEndUserSurveyResponse[])

    const redirectToSurveyHomePage = ref<string>('MainUsersSurveyHomeScreen')
    const backButtonTitle = ref<string>('sdssSurvey.backButton')
    const backButtonIcon = ref<string>('mdi mdi-home-lightbulb')
    const backButtonColor = ref<string>('#008b8b')

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    if (surveyToken.value) {
      authorizedUser()
    } else {
      // You can also handle the absence of a token here.
      userNotAuthorized()
    }

    async function handleAuthorizedUserResponseData() {
      if (surveyToken.value) {
        await getPublicSurveyAuth<SuccessResponse<PublicEndUserSurveyResponse[]>>(
          URIS.SURVEYS.SURVEY_USER_PROFILE.HISTORY_SURVEYS,
          employeeLang
        ).then((response: AxiosResponse<SuccessResponse<PublicEndUserSurveyResponse[]>>) => {
          if (response.data.success) {
            const data = response.data.data
            console.log(data)
            historySurveyRoundsResponse.value = data
          } else {
            toast.error(response.data.message)
          }
        })
      } else {
        userNotAuthorized()
      }
    }

    function userNotAuthorized() {
      isAuthorized.value = false
    }

    function authorizedUser() {
      isAuthorized.value = true
    }

    onMounted(async () => {
      sidebarStore.disable()
      await handleAuthorizedUserResponseData()
    })

    return {
      isAuthorized,
      surveys: historySurveyRoundsResponse,
      redirectToSurveyHomePage,
      backButtonTitle,
      backButtonIcon,
      backButtonColor,
      selectedSurvey: null
    }
  },
  methods: {
    handleViewSurvey(survey) {
      this.selectedSurvey = survey
    },
    handleGoBack() {
      this.selectedSurvey = null
    }
  }
}
</script>
<style scoped lang="scss">
.custom-main-survey-history-app-back-button {
  margin-bottom: 2%;
}
</style>
