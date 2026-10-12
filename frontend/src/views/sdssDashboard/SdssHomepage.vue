<script lang="ts">
import { defineComponent, onMounted, ref, watchEffect } from 'vue'
import AppHeader from '@/views/home/header/Header.vue'
import BannerView from '@/views/home/banner/Banner.vue'
import { HexagonComponentCategories } from '@/dto/common/HexagonComponentCategories'
import { isHasAnyRole } from '@/utils/roleUtils'
import { Fields, URIS } from '@/constants/DpConstants.ts'
import { useI18n } from 'vue-i18n'
import AccessSurveyCard from '@/views/surveys/security/AccessSurvey.vue'
import AccessDeniedView from '@/views/common/AccessDeniedView.vue'
import FooterComponent from '@/components/common/FooterComponent.vue'
import bannerImg from '@/assets/images/banner/slider-img.png'
import { get } from '@/utils/GenericApiHelper.ts'
import type { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { MainHomeScreenResponse } from '@/dto/home/MainHomeScreenResponse.ts'
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
    const { locale, t } = useI18n()
    const error = ref<string | null>(null)

    const grpNumber = ref<number>()

    const backButtonForHomePageTitle = ref<string>('home.backButton')

    const isLoading = ref<boolean>(true)

    const mainHomeScreenResponse = ref<MainHomeScreenResponse>({} as MainHomeScreenResponse)

    const employeeLang: string = localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    const loadHomeScreenData = async () => {
      try {
        isLoading.value = true
        error.value = null

        const response = await get<SuccessResponse<MainHomeScreenResponse>>(URIS.LOOKUP.MAIN_HOME_SCREEN_STATISTICS)

        if (response?.success) {
          mainHomeScreenResponse.value = response.data as MainHomeScreenResponse
        } else {
          error.value = t('Messages.error.general.serviceDown')
        }
      } catch (err) {
        console.error('Error loading home screen data:', err)
        error.value = t('Messages.error.general.serviceDown')
      } finally {
        isLoading.value = false
      }
    }

    const categories = ref<HexagonComponentCategories[]>([
      {
        titleAr: 'شاشتي التفاعليه',
        titleEn: 'My Dashboard',
        title: translate('home.mainTitles.myDashboardPage'),
        route: '/MainDashboard',
        icon: 'mdi-monitor-dashboard',
        color: '#0D47A1',
        isAuthorized: true
      },
      {
        titleAr: 'تحدياتي',
        titleEn: 'My Challenges',
        title: translate('home.mainTitles.myChallenges'),
        route: '/challenge/MyChallenges',
        icon: 'mdi-fencing',
        color: '#8B0000',
        iconSize: 'x-large',
        isAuthorized: true
      },
      {
        titleAr: 'طلبات الانضمام',
        titleEn: 'Invitation Requests',
        title: translate('home.mainTitles.invitationRequests'),
        route: '/MyRequestsDashboard',
        icon: 'mdi-email-search-outline',
        color: '#9C27B0',
        isAuthorized: true
      }
    ])

    const authorizedCategories = ref<HexagonComponentCategories[]>([])

    watchEffect(() => {
      authorizedCategories.value = categories.value.filter((category) => category.isAuthorized)
    })

    onMounted(async () => {
      await loadHomeScreenData()
    })

    return {
      grpNumber,
      categories,
      authorizedCategories,
      backButtonForHomePageTitle,
      bannerImg,
      mainHomeScreenResponse,
      isLoading
    }
  },
  methods: { isHasAnyRole }
})
</script>

<template src="./SdssHomepage.html"></template>
<style src="./SdssHomepage.scss" lang="scss" scoped></style>
