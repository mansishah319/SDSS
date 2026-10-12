<script lang="ts">
import FooterComponent from '@/components/common/FooterComponent.vue'
import { defineComponent, onMounted, ref, watchEffect } from 'vue'
import { Fields, SecurityConstants, URIS } from '@/constants/DpConstants'
import { getPublicSurveyAuth } from '@/utils/GenericApiHelper'
import { SuccessResponse } from '@/dto/generic/SuccessResponse'
import PublicEndUserSurveyResponse from '@/dto/surveys/public/PublicEndUserSurveyResponse'
import { AxiosResponse } from 'axios'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import { useSurveyTokenStore } from '@/store/useSurveyTokenStore'
import AccessDeniedView from '@/views/common/AccessDeniedView.vue'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import BackButton from '@/components/common/BackButton.vue'
import CustomCountdown from '@/components/common/CustomCountdown.vue'
import CustomDynamicTimeCounter from '@/components/common/DynamicTimeCounterDesign.vue'
import { useRouter } from 'vue-router'
import { useSidebarStore } from '@/store/sidebar'
import CustomGoBackButton from '@/components/custom/CustomGoBackButton.vue'

export default defineComponent({
  name: 'LiveSurveys',
  components: {
    CustomGoBackButton,
    CustomDynamicTimeCounter,
    CustomCountdown,
    BackButton,
    EmptyDataTable,
    AccessDeniedView,
    FooterComponent
  },
  data() {
    const router = useRouter()
    const sidebarStore = useSidebarStore()
    const surveyTokenStore = useSurveyTokenStore()
    const { locale } = useI18n()
    const isAuthorized = ref<boolean>(false)
    const surveyToken = ref(surveyTokenStore.surveyToken || localStorage.getItem(SecurityConstants.Survey_TOKEN))
    const employeeLang: string = localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'

    const liveSurveyRoundsResponse = ref<PublicEndUserSurveyResponse[]>([])

    const redirectToSurveyHomePage = ref<string>('MainUsersSurveyHomeScreen')
    const backButtonTitle = ref<string>('sdssSurvey.backButton')
    const backButtonIcon = ref<string>('mdi mdi-home-lightbulb')
    const backButtonColor = ref<string>('#008b8b')
    const liveSurveysIconUrl = new URL('../../../../assets/images/logo/online-survey2.png', import.meta.url).href

    const isLoading = ref(true)

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    const openSurveyScreen = (surveyId: number) => {
      router.push({ name: 'MainSurveyEndUserView', params: { surveyId: surveyId } })
    }

    if (surveyToken.value) {
      authorizedUser()
    } else {
      // You can also handle the absence of a token here.
      userNotAuthorized()
    }

    async function handleAuthorizedUserResponseData() {
      if (surveyToken.value) {
        await getPublicSurveyAuth<SuccessResponse<PublicEndUserSurveyResponse>>(
          URIS.SURVEYS.SURVEY_USER_PROFILE.LIVE_SURVEYS,
          employeeLang
        ).then((response: AxiosResponse<SuccessResponse<PublicEndUserSurveyResponse>>) => {
          if (response.data.success) {
            const data = response.data.data
            console.log(data)
            liveSurveyRoundsResponse.value = Array.isArray(data) ? data : [data]
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
      isLoading.value = false
    })

    const isLanguageAr = (): boolean => {
      return employeeLang === 'ar'
    }

    return {
      isAuthorized,
      isLanguageAr,
      surveys: liveSurveyRoundsResponse,
      redirectToSurveyHomePage,
      backButtonTitle,
      backButtonIcon,
      backButtonColor,
      liveSurveysIconUrl,
      openSurveyScreen,
      isLoading
    }
  }
})
</script>

<style lang="scss" src="./LiveSurveys.scss"></style>
<template src="./LiveSurveys.html" lang="html"></template>
