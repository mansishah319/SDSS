<script lang="ts">
import { computed, defineComponent, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import AppHeader from '@/views/home/header/Header.vue'
import BannerView from '@/views/home/banner/Banner.vue'
import { HexagonComponentCategories } from '@/dto/common/HexagonComponentCategories'
import { isAdminOrModerator, isHasAnyRole } from '@/utils/roleUtils'
import { translate } from '@/utils/TranslatorHelper'
import { get } from '@/utils/GenericApiHelper'
import type { SuccessResponse } from '@/dto/generic/SuccessResponse'
import { URIS } from '@/constants/DpConstants'
import { MainHomeScreenResponse } from '@/dto/home/MainHomeScreenResponse'
import { useSidebarStore } from '@/store/sidebar'
import bannerImg from '@/assets/images/banner/slider-img.png'
import FooterComponent from '@/components/common/FooterComponent.vue'

export default defineComponent({
  name: 'SdssHomePage',
  components: {
    AppHeader,
    BannerView,
    FooterComponent
  },
  setup() {
    const { t, locale } = useI18n()
    const sidebarStore = useSidebarStore()
    const mainHomeScreenResponse = ref<MainHomeScreenResponse>({} as MainHomeScreenResponse)
    const isLoading = ref(true)
    const error = ref<string | null>(null)

    // Computed properties for responsive behavior
    const isMobile = computed(() => {
      return window.innerWidth <= 768
    })

    const isTablet = computed(() => {
      return window.innerWidth > 768 && window.innerWidth <= 1200
    })

    const isRTL = computed(() => {
      return locale.value === 'ar'
    })

    const sdssImage = new URL('@/assets/images/logo/decision-support-system.png', import.meta.url).href
    const canAccessSdssHomepage = isHasAnyRole()

    const categories = ref<HexagonComponentCategories[]>([
      {
        titleAr: 'SDSS',
        titleEn: 'SDSS',
        title: translate('home.mainTitles.sdssSystem'),
        route: '/sdssDashboard/SdssHomepage',
        icon: sdssImage,
        color: '#FFD700',
        isAuthorized: canAccessSdssHomepage,
        iconSize: 'x-large'
      },
      {
        titleAr: 'برنامج دلفاي X',
        titleEn: 'Delphi X',
        title: translate('home.mainTitles.delphiSurvey'),
        route: '/surveys/home',
        icon: 'mdi-checkbox-multiple-outline',
        color: '#022902',
        isAuthorized: true
      },
      // {
      //   titleAr: 'الإستطلاع المستقبلي',
      //   titleEn: 'Future Poll',
      //   title: translate('home.mainTitles.futurePoll'),
      //   route: '/future-poll',
      //   icon: 'mdi-poll',
      //   color: '#FF8C00',
      //   iconSize: 'x-large',
      //   isAuthorized: true,
      // },
      // {
      //   titleAr: 'الأثر',
      //   titleEn: 'Impact',
      //   title: translate('home.mainTitles.impact'),
      //   route: '/impact',
      //   icon: 'mdi-weather-hail',
      //   color: '#203d59',
      //   iconSize: 'x-large',
      //   isAuthorized: true,
      // },
      {
        titleAr: 'لوحة التحكم الرئيسية',
        titleEn: 'Admin Panel',
        title: translate('home.mainTitles.adminPanel'),
        route: '/admin',
        icon: 'mdi-shield-crown',
        color: '#FFD700',
        isAuthorized: isAdminOrModerator(),
        iconSize: 'x-large',
        isPremium: true
      }
    ] as HexagonComponentCategories[])

    const authorizedCategories = computed(() => categories.value.filter((category) => category.isAuthorized))

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

    onMounted(async () => {
      await loadHomeScreenData()
    })

    const refreshData = async () => {
      await loadHomeScreenData()
    }

    const handleWhenAppearSideBar = (routeNavigation: string) => {
      if (routeNavigation === '/admin' || routeNavigation === '/users') {
        sidebarStore.show()
      }
    }

    return {
      t,
      locale,
      bannerImg,
      categories,
      authorizedCategories,
      canAccessSdssHomepage,
      mainHomeScreenResponse,
      isLoading,
      error,
      isMobile,
      isTablet,
      isRTL,
      handleWhenAppearSideBar,
      refreshData
    }
  }
})
</script>

<template src="./Homepage.html"></template>
<style src="./Homepage.scss" lang="scss" scoped></style>
