<template lang="html">
  <div v-if="isAuthorized">
    <SurveyReportsList :round-survey-response="surveys" @view-survey="handleViewSurvey"></SurveyReportsList>
    <v-col cols="12" class="d-flex justify-center">
      <div class="mt-3 custom-main-survey-report-app-back-button">
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
import FooterComponent from '@/components/common/FooterComponent.vue'
import { useI18n } from 'vue-i18n'
import { onMounted, ref, watchEffect } from 'vue'
import { Fields, SecurityConstants, URIS } from '@/constants/DpConstants'
import { get } from '@/utils/GenericApiHelper.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { toast } from 'vue3-toastify'
import AccessDeniedView from '@/views/common/AccessDeniedView.vue'
import BackButton from '@/components/common/BackButton.vue'
import { RoundReportMainResponse } from '@/dto/surveys/round/RoundReportMainResponse'
import SurveyReportsList from '@/views/surveys/survey/report/SurveyReportsList.vue'
import { useSidebarStore } from '@/store/sidebar.ts'
import { useRouter } from 'vue-router'

export default {
  name: 'SurveyReports',
  components: { BackButton, AccessDeniedView, FooterComponent, SurveyReportsList },
  data() {
    const { locale } = useI18n()
    const router = useRouter()
    const sidebarStore = useSidebarStore()
    const isAuthorized = ref<boolean>(false)
    const token = ref(localStorage.getItem(SecurityConstants.TOKEN))
    const employeeLang: string = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'

    const reportSurveyRoundsResponse = ref<RoundReportMainResponse[]>([] as RoundReportMainResponse[])

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

    if (token.value) {
      authorizedUser()
    } else {
      // You can also handle the absence of a token here.
      userNotAuthorized()
    }

    async function loadSurveyReportResponse() {
      if (token.value) {
        await get<SuccessResponse<RoundReportMainResponse[]>>(URIS.SURVEYS.ROUND.SURVEYS_REPORTS).then(
          (response: SuccessResponse<RoundReportMainResponse[]>) => {
            if (response.success) {
              reportSurveyRoundsResponse.value = response.data as RoundReportMainResponse[]
            } else {
              toast.error(response.message)
            }
          }
        )
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

    const handleViewSurvey = (survey: RoundReportMainResponse) => {
      router.push({
        name: 'SurveyReportsDetails',
        params: { roundId: survey.id }
      })
    }

    onMounted(async () => {
      sidebarStore.disable()
      await loadSurveyReportResponse()
    })

    return {
      isAuthorized,
      surveys: reportSurveyRoundsResponse,
      redirectToSurveyHomePage,
      backButtonTitle,
      backButtonIcon,
      backButtonColor,
      selectedSurvey: null,
      handleViewSurvey
    }
  },
  methods: {
    handleGoBack() {
      this.selectedSurvey = null
    }
  }
}
</script>
<style scoped lang="scss">
.custom-main-survey-report-app-back-button {
  margin-bottom: 2%;
}
</style>
