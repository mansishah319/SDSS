<script lang="ts">
import { defineComponent, onMounted, ref, watch, watchEffect } from 'vue'
import AppHeader from '@/views/home/header/Header.vue'
import BannerView from '@/views/home/banner/Banner.vue'
import { HexagonComponentCategories } from '@/dto/common/HexagonComponentCategories'
import { isDelphiModerator, isHasAnyRole } from '@/utils/roleUtils'
import { Fields, SecurityConstants, URIS } from '@/constants/DpConstants.ts'
import { useI18n } from 'vue-i18n'
import { useSidebarStore } from '@/store/sidebar.ts'
import { useSurveyTokenStore } from '@/store/useSurveyTokenStore.ts'
import AccessSurveyCard from '@/views/surveys/security/AccessSurvey.vue'
import AccessDeniedView from '@/views/common/AccessDeniedView.vue'
import FooterComponent from '@/components/common/FooterComponent.vue'
import bannerImg from '@/assets/images/banner/slider-img.png'
import { get } from '@/utils/GenericApiHelper.ts'
import type { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { MainSurveyHomeScreenResponse } from '@/dto/home/MainHomeScreenResponse.ts'
import { translate } from '@/utils/TranslatorHelper.ts'
import BackButton from '@/components/common/BackButton.vue'
import AppActivityIndicator from '@/components/common/AppActivityIndicator.vue'

export default defineComponent({
  name: 'SurveyHomepage',
  components: {
    AppActivityIndicator,
    BackButton,
    FooterComponent,
    AccessDeniedView,
    AccessSurveyCard,
    AppHeader,
    BannerView
  },
  setup() {
    const { locale } = useI18n()
    const sidebarStore = useSidebarStore()
    // survey Authentication Handling
    const surveyTokenStore = useSurveyTokenStore()

    const authDialog = ref(!isHasAnyRole())
    const accessKey = ref('')
    const grpNumber = ref<number>()

    const isAuthorized = ref(isHasAnyRole())
    const hasAttempted = ref(false)
    const passwordVisible = ref(false)

    const redirectToPage = ref<string>('home')
    const backButtonTitle = ref<string>('sdssSurvey.backButton')
    const backButtonForHomePageTitle = ref<string>('home.backButton')
    const backButtonIcon = ref<string>('mdi mdi-home-lightbulb')
    const backColor = ref<string>('#008b8b')

    const isLoading = ref<boolean>(true)

    const sdssMainToken: string | null = localStorage.getItem(SecurityConstants.TOKEN)

    const mainHomeScreenResponse = ref<MainSurveyHomeScreenResponse>({
      totalSurveysCount: 0,
      totalSurveysUsersOnTheSystemCount: 0
    } as MainSurveyHomeScreenResponse)

    const surveyToken = ref(surveyTokenStore.surveyToken || localStorage.getItem(SecurityConstants.Survey_TOKEN))
    const employeeLang: string = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'

    const surveyStats = ref({
      completed: 50,
      pending: 10,
      total: 60
    })

    const surveyImage = new URL('../../../assets/images/surveys/survey.svg', import.meta.url).href
    const appsIconUrl = new URL('../../../assets/images/variety/apps-icon.svg', import.meta.url).href
    const infoIconUrl = new URL('../../../assets/images/actions/info-icon.svg', import.meta.url).href
    const statsIconUrl = new URL('../../../assets/images/variety/auto_graph-icon.svg', import.meta.url).href

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    const categories = ref<HexagonComponentCategories[]>([
      {
        titleEn: 'My Dashboard',
        titleAr: 'شاشتي التفاعليه',
        title: translate('home.surveyMainTitles.dashboard'),
        route: '/surveys/my-dashboard',
        isAuthorized: true,
        icon: 'mdi-tablet-dashboard',
        color: '#0D47A1'
      },
      {
        titleEn: 'Active Surveys',
        titleAr: 'الاستبيانات الجارية',
        title: translate('home.surveyMainTitles.activeSurveys'),
        route: '/surveys/live',
        isAuthorized: true,
        activityIndicator: true,
        activityIndicatorColor: '#ffffff',
        activityIndicatorSize: 30
      },
      {
        titleEn: 'Archive Surveys',
        titleAr: 'أرشيف الاستبيانات',
        title: translate('home.surveyMainTitles.archiveSurveys'),
        route: '/surveys/history',
        isAuthorized: true,
        icon: 'mdi-history',
        color: '#606060'
      },
      {
        titleEn: 'Survey Reports',
        titleAr: 'تقارير الاستبيانات',
        title: translate('home.surveyMainTitles.surveysReports'),
        route: '/surveys/admin-reports',
        icon: 'mdi-shield-crown',
        color: '#FFD700',
        isAuthorized: isDelphiModerator(),
        iconSize: 'x-large',
        isPremium: true
      }
    ])

    const authorizedCategories = ref<HexagonComponentCategories[]>([])

    watchEffect(() => {
      authorizedCategories.value = categories.value.filter((category) => category.isAuthorized)
    })

    if (surveyToken.value) {
      authorizedUser()
    } else {
      // You can also handle the absence of a token here.
      userNotAuthorized()
    }

    watch(
      () => surveyTokenStore.surveyToken,
      (newToken: string) => {
        surveyToken.value = newToken
      }
    )

    watch(
      authDialog,
      (newValue) => {
        console.log('authDialog changed:', newValue)
      },
      { immediate: true }
    )

    async function authorizedUser(funSurveyToken?: string) {
      if (funSurveyToken) surveyToken.value = funSurveyToken
      isAuthorized.value = true
      hasAttempted.value = false
      authDialog.value = false
    }

    function userNotAuthorized() {
      isAuthorized.value = false
      hasAttempted.value = true
      authDialog.value = true
      if (sdssMainToken) {
        redirectToPage.value = 'home'
        backButtonTitle.value = 'home.backButton'
        backButtonIcon.value = 'mdi mdi-home'
        backColor.value = 'primary'
      }
    }

    const handleCloseAccessSurveyCard = () => {
      isAuthorized.value = false
      hasAttempted.value = true
      authDialog.value = false
    }

    onMounted(async () => {
      sidebarStore.disable()
      if (isHasAnyRole())
        await get<SuccessResponse<MainSurveyHomeScreenResponse>>(URIS.LOOKUP.MAIN_SURVEY_HOME_SCREEN_STATISTICS).then(
          (response) => {
            if (response?.success) {
              mainHomeScreenResponse.value = response.data as MainSurveyHomeScreenResponse
            }
          }
        )
      isLoading.value = false
    })

    return {
      grpNumber,
      passwordVisible,
      accessKey,
      isAuthorized,
      hasAttempted,
      authDialog,
      surveyStats,
      categories,
      authorizedCategories,
      surveyImage,
      appsIconUrl,
      infoIconUrl,
      statsIconUrl,
      userNotAuthorized,
      authorizedUser,
      handleCloseAccessSurveyCard,
      redirectToPage,
      backButtonTitle,
      backButtonIcon,
      backColor,
      backButtonForHomePageTitle,
      bannerImg,
      mainHomeScreenResponse,
      isLoading
    }
  },
  methods: { isHasAnyRole }
})
</script>

<template src="./SurveyHomepage.html"></template>
<style src="./SurveyHomepage.scss" lang="scss" scoped></style>
