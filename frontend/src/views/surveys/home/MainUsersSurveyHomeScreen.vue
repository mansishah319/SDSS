<template lang="html">
  <!-- Authorization Popup -->
  <v-dialog v-model="authDialog" persistent max-width="600">
    <AccessSurveyCard
      :grp-number="grpNumber || ''"
      :access-key="accessKey"
      :password-visible="passwordVisible"
      :is-has-any-role="isHasAnyRole"
      @update:grp-number="grpNumber = $event"
      @update:access-key="accessKey = $event"
      @update:password-visible="passwordVisible = $event"
      @user-not-authorized="userNotAuthorized"
      @user-authorized="authorizedUser"
      @close-popup="handleCloseAccessSurveyCard"
    />
  </v-dialog>
  <v-container v-if="isAuthorized" class="custom-survey-home-page-screen-container" fluid>
    <!-- Hero Section -->
    <v-row
      class="custom-survey-home-page-screen-main-info-area"
      :style="{ backgroundImage: `url(${surveyHeroBackgroundUrl})` }"
      align="center"
      justify="center"
    >
      <v-col cols="12" class="text-center custom-survey-home-page-screen-column-style">
        <div class="custom-survey-home-page-screen-main-title-div">
          <h1 class="font-dubai-bold custom-survey-home-page-screen-headline animate__animated animate__bounce">
            {{ $t('surveyHome.content.main-title') }}
          </h1>
        </div>
        <div class="font-dubai-regular custom-survey-home-page-screen-main-subtitle-div">
          <p class="custom-survey-home-page-screen-subhead">
            {{ $t('surveyHome.content.main-description') }}
          </p>
        </div>
      </v-col>
    </v-row>

    <!-- Categories arranged in a flexible column layout -->
    <v-row class="main-home-survey-view-screen-content-row" align="stretch" no-gutters>
      <!-- Services Card -->
      <v-col cols="12" md="6" class="main-home-survey-view-services-card">
        <v-row class="main-home-survey-view-screen-category-rows">
          <v-col
            v-for="category in categories"
            :key="category.titleAr"
            :cols="12"
            :sm="6"
            :md="6"
            class="custom-main-survey-home-category-card"
          >
            <hexagon-component
              :title-en="category.titleEn"
              :title-ar="category.titleAr"
              :route="category.route"
              :icon="category?.icon"
              :icon-color="category?.color"
              :is-authorized="category?.isAuthorized"
              :activity-indicator="category?.activityIndicator"
              :activity-indicator-size="category?.activityIndicatorSize"
              :activity-indicator-color="category?.activityIndicatorColor"
            />
          </v-col>
        </v-row>
      </v-col>
      <!-- Statistics Card -->
      <v-col cols="12" md="6" class="main-home-survey-view-statistics-card">
        <div class="statistics-content">
          <h3 class="statistics-title">{{ $t('home.statistics') }}</h3>
          <div class="statistics-list">
            <div class="statistics-item">
              <span class="statistics-label">{{ $t('common.completed') }}</span>
              <span class="statistics-value">{{ surveyStats.completed }}</span>
            </div>
            <div class="statistics-item">
              <span class="statistics-label">{{ $t('common.pending') }}</span>
              <span class="statistics-value">{{ surveyStats.pending }}</span>
            </div>
            <div class="statistics-item">
              <span class="statistics-label">{{ $t('common.total') }}</span>
              <span class="statistics-value">{{ surveyStats.total }}</span>
            </div>
          </div>
        </div>
      </v-col>
    </v-row>
    <v-row
      v-if="isHasAnyRole()"
      v-show="isHasAnyRole()"
      class="live-survey-card-back-btn-div"
      justify="center"
      align="center"
    >
      <v-col cols="12" class="d-flex justify-center">
        <div class="mt-3">
          <BackButton :title="backButtonForHomePageTitle" />
        </div>
      </v-col>
    </v-row>
  </v-container>
  <!-- Access Denied View -->
  <AccessDeniedView
    v-else-if="!isAuthorized || hasAttempted"
    :back-icon="backButtonIcon"
    :back-title="backButtonTitle"
    :back-color="backColor"
    :back-redirect-path="redirectToPage"
  ></AccessDeniedView>
  <!-- Footer -->
  <FooterComponent :justify-content="'justify-content-start'"></FooterComponent>
</template>

<script lang="ts">
import { onMounted, ref, watch, watchEffect } from 'vue'
import HexagonComponent from '@/components/shapes/HexagonShape.vue'
import FooterComponent from '@/components/common/FooterComponent.vue'
import { HexagonComponentCategories } from '@/dto/common/HexagonComponentCategories'
import AccessSurveyCard from '@/views/surveys/security/AccessSurvey.vue'
import { isHasAnyRole } from '@/utils/roleUtils.ts'
import { useSurveyTokenStore } from '@/store/useSurveyTokenStore.ts'
import { Fields, SecurityConstants } from '@/constants/DpConstants.ts'
import { useI18n } from 'vue-i18n'
import AccessDeniedView from '@/views/common/AccessDeniedView.vue'
import { useSidebarStore } from '@/store/sidebar.ts'
import BackButton from '@/components/common/BackButton.vue'

export default {
  name: 'MainUsersSurveyHomeScreen',
  components: {
    BackButton,
    AccessDeniedView,
    AccessSurveyCard,
    FooterComponent,
    HexagonComponent
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

    const sdssMainToken: string | null = localStorage.getItem(SecurityConstants.TOKEN)

    const surveyToken = ref(surveyTokenStore.surveyToken || localStorage.getItem(SecurityConstants.Survey_TOKEN))
    const employeeLang: string = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'

    const surveyStats = ref({
      completed: 50,
      pending: 10,
      total: 60
    })

    const surveyImage = new URL('../../../assets/images/surveys/survey.svg', import.meta.url).href
    const surveyHeroBackgroundUrl = new URL('../../../assets/images/variety/survey-view.png', import.meta.url).href

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    const categories = ref<HexagonComponentCategories[]>([
      {
        titleEn: 'My Survey Dashboard',
        titleAr: 'شاشة الإستبيان خاصتي',
        route: '/surveys/my-dashboard',
        isAuthorized: true,
        icon: 'mdi-tablet-dashboard',
        color: 'blue'
      },
      {
        titleEn: 'Active Surveys',
        titleAr: 'الاستبيانات الجارية',
        route: '/surveys/live',
        isAuthorized: true,
        activityIndicator: true,
        activityIndicatorColor: '#ffffff',
        activityIndicatorSize: 30
      },
      {
        titleEn: 'Survey Archive',
        titleAr: 'أرشيف الاستبيانات',
        route: '/surveys/history',
        isAuthorized: true,
        icon: 'mdi-history',
        color: '#606060'
      },
      {
        titleEn: 'Survey Reports',
        titleAr: 'تقارير الاستبيانات',
        route: '/surveys/admin-reports',
        icon: 'mdi-shield-crown',
        color: 'orange',
        isAuthorized: isHasAnyRole(),
        iconSize: 'x-large'
      }
    ])

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
        redirectToPage.value = '/'
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

    onMounted(() => {
      sidebarStore.disable()
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
      surveyImage,
      surveyHeroBackgroundUrl,
      userNotAuthorized,
      authorizedUser,
      handleCloseAccessSurveyCard,
      redirectToPage,
      backButtonTitle,
      backButtonIcon,
      backColor,
      backButtonForHomePageTitle
    }
  },
  methods: { isHasAnyRole }
}
</script>

<style scoped lang="scss">
.custom-survey-home-page-screen-container {
  overflow-y: auto;
  background-color: #e8f5e9;
  color: #2e7d32;
}

.custom-survey-home-page-screen-main-info-area {
  display: flex;
  position: relative;
  flex-direction: column;
  background-size: 110% 115%;
  background-repeat: no-repeat;
  background-position: center;
  background-blend-mode: color-burn;
  background-color: rgba(56, 142, 60, 0.5);
  padding: 2% 0;
  text-align: center;
  min-height: 35vh;
  transition: transform 0.3s ease-in-out;
}

.custom-survey-home-page-screen-column-style {
  display: contents;
}

.custom-survey-home-page-screen-main-title-div {
  width: 50%;
}

.custom-survey-home-page-screen-headline {
  font-size: 2.5rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  background: linear-gradient(90deg, #fff176, #ff7043);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 4px 4px 6px rgba(0, 0, 0, 0.25);
  margin: 20px 0;
  padding: 10px;
  text-align: center;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.custom-survey-home-page-screen-main-subtitle-div {
  width: 70%;
}

.custom-survey-home-page-screen-subhead {
  font-size: 1.3rem;
  font-weight: 400;
  color: #e8f5e9;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  margin: 15px auto;
  line-height: 1.8;
  padding: 0 15px;
  letter-spacing: 0.02em;
  text-align: center;
  border-radius: 6px;
  background-color: rgba(64, 92, 64, 0.1);
  -webkit-backdrop-filter: blur(4px);
  backdrop-filter: blur(4px);
  text-wrap: balance;
  width: 100%;
}

.main-home-survey-view-screen-category-rows {
  display: flex;
  flex-wrap: wrap;
  margin-top: 3%;
  justify-content: center;
  margin-bottom: 2%;
}

.custom-main-survey-home-category-card {
  margin: 2%;
  transition: transform 0.3s,
  box-shadow 0.3s;
}

.rtl .custom-main-survey-home-category-card {
  margin-left: 5%;
}

.ltr .custom-main-survey-home-category-card {
  margin-right: 5%;
}

.main-home-survey-view-screen-content-row {
  display: flex;
  align-items: stretch;
  gap: 24px;
  margin-top: 2rem;
}

.main-home-survey-view-services-card,
.main-home-survey-view-statistics-card {
  display: flex;
  flex-direction: column;
  flex: 1 1 0;
  min-width: 0;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  padding: 24px;
}

.statistics-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100%;
}

.statistics-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  color: #2e7d32;
}

.statistics-list {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.statistics-item {
  display: flex;
  justify-content: space-between;
  font-size: 1.2rem;
  padding: 0.8rem 0;
  border-bottom: 1px solid #e0e0e0;
}

.statistics-label {
  color: #757575;
}

.statistics-value {
  font-weight: 600;
  color: #388e3c;
}
</style>
