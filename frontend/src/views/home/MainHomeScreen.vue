<template>
  <v-container v-if="isHasAnyRole()" v-show="isHasAnyRole()" fluid class="custom-main-user-home-screen-container">
    <!-- Hero Section -->
    <v-row class="custom-main-user-view-screen" align="center" justify="center">
      <v-col cols="12" class="text-center custom-main-user-view-screen-column-style">
        <div class="custom-app-main-home-details">
          <h1 class="font-dubai-bold main-home-screen-title-headline animate__animated animate__bounce">
            <span class="main-home-title">{{ $t('home.content.main-home-title') }}</span>
            <br />
            <!-- This will ensure the second line appears below the first one -->
            <span class="system-title-shortcut">{{ $t('home.content.system-title-shortcut') }}</span>
          </h1>
        </div>
        <div class="custom-app-main-home-company-info-details">
          <p class="font-dubai-regular custom-survey-dashboard-subhead animate__fadeInUp font-dubai-regular">
            {{ $t('home.content.main-page-description') }}
          </p>
        </div>
      </v-col>
    </v-row>

    <!-- Categories arranged in a flexible column layout -->
    <v-row class="custom-main-user-view-screen-category-rows">
      <v-col
        v-for="category in categories"
        :key="category.titleAr"
        :cols="12"
        :sm="6"
        :md="4"
        :lg="3"
        :xl="2"
        :xxl="1"
        width="20%"
        class="custom-main-home-category-card"
      >
        <hexagon-component
          :title-ar="category.titleAr"
          :title-en="category.titleEn"
          :route="category.route"
          :icon="category?.icon"
          :icon-color="category?.color"
          :icon-size="category?.iconSize"
          :is-authorized="category?.isAuthorized"
          @navigate="handleWhenAppearSideBar"
        />
      </v-col>
    </v-row>
    <!-- Download User Manual Button -->
    <v-row justify="center" class="mt-4">
      <v-btn
        v-if="isAdminOrModeratorOrValidator()"
        v-show="isAdminOrModeratorOrValidator()"
        outlined
        large
        target="_blank"
        download="User_Manual.pdf"
        class="custom-download-button"
        @click="downloadUserManualFile()"
      >
        <v-icon left>mdi-file-download</v-icon>
        {{ $t('home.content.download-user-manual') }}
      </v-btn>
      <v-tooltip bottom>
        <template #activator="{ props, on }">
          <v-icon color="info" class="custom-info-icon" v-bind="props" v-on="on"> mdi-information-outline</v-icon>
        </template>
        <span>{{ $t('home.content.download-tooltip') }}</span>
      </v-tooltip>
    </v-row>
  </v-container>
  <!-- Access Denied View -->
  <AccessDeniedView
    v-else
    v-show="!isHasAnyRole()"
    :back-icon="backButtonIcon"
    :back-title="backButtonTitle"
    :back-color="backColor"
    :back-redirect-path="redirectToPage"
  ></AccessDeniedView>
  <!-- Footer -->
  <FooterComponent :justify-content="'justify-content-start'"></FooterComponent>
</template>

<script lang="ts">
import { onMounted, ref } from 'vue'
import { useSidebarStore } from '@/store/sidebar'
import HexagonComponent from '@/components/shapes/HexagonShape.vue'
import FooterComponent from '@/components/common/FooterComponent.vue'
import { HexagonComponentCategories } from '@/dto/common/HexagonComponentCategories'
import { isAdminOrModerator, isAdminOrModeratorOrValidator, isCommanderOnly, isHasAnyRole } from '@/utils/roleUtils'
import AccessDeniedView from '@/views/common/AccessDeniedView.vue'
import { download } from '@/utils/GenericApiHelper.ts'
import { URIS } from '@/constants/DpConstants.ts'
import { translate } from '@/utils/TranslatorHelper.ts'

export default {
  name: 'MainHomeScreen',
  components: {
    AccessDeniedView,
    FooterComponent,
    HexagonComponent
  },
  setup() {
    const sidebarStore = useSidebarStore()
    const surveyStats = ref({
      completed: 50,
      pending: 10,
      total: 60
    })

    const myDashboardPage = isCommanderOnly() ? '/MainDashboard' : '/MyRequestsDashboard'

    const surveyImage = new URL('@/assets/images/variety/dubai-city.png', import.meta.url).href

    const categories = ref<HexagonComponentCategories[]>([
      {
        titleAr: 'شاشتي التفاعليه',
        titleEn: 'My Dashboard',
        title: translate('home.mainTitles.myDashboardPage'),
        route: myDashboardPage,
        icon: 'mdi-monitor-dashboard',
        color: 'blue',
        isAuthorized: true
      },
      {
        titleAr: 'تحدياتي',
        titleEn: 'My Challenges',
        title: translate('home.mainTitles.myChallenges'),
        route: '/challenge/MyChallenges',
        icon: 'mdi-fencing',
        color: 'red',
        iconSize: 'x-large',
        isAuthorized: true
      },
      {
        titleAr: 'الإستطلاع المستقبلي',
        titleEn: 'Future Poll',
        title: translate('home.mainTitles.futurePoll'),
        route: '/future-poll',
        icon: 'mdi-poll',
        color: '#E07B39',
        iconSize: 'x-large',
        isAuthorized: true
      },
      {
        titleAr: 'الأثر',
        titleEn: 'Impact',
        title: translate('home.mainTitles.impact'),
        route: '/impact',
        icon: 'mdi-weather-hail',
        color: '#81DAE3',
        iconSize: 'x-large',
        isAuthorized: true
      },
      {
        titleAr: 'برنامج دلفاي X',
        titleEn: 'Delphi X',
        title: translate('home.mainTitles.delphiSurvey'),
        route: '/surveys/home',
        icon: 'mdi-checkbox-multiple-outline',
        isAuthorized: true
      },
      {
        titleAr: 'لوحة التحكم الرئيسية',
        titleEn: 'Admin Panel',
        title: translate('home.mainTitles.adminPanel'),
        route: '/users',
        icon: 'mdi-shield-crown',
        color: 'orange',
        isAuthorized: isAdminOrModerator(),
        iconSize: 'x-large'
      }
    ])

    const handleWhenAppearSideBar = (routeNavigation: string) => {
      if (routeNavigation === '/users') {
        sidebarStore.show()
      }
    }

    const downloadUserManualFile = async () => {
      await download(URIS.FILES.USER_MANUAL, 'User_Manual.pdf')
    }

    const redirectToPage = ref<string>('/')
    const backButtonTitle = ref<string>('sdssSurvey.backButton')
    const backButtonIcon = ref<string>('mdi mdi-home-lightbulb')
    const backColor = ref<string>('#008b8b')

    onMounted(() => {
      sidebarStore.isVisible = false
      localStorage.setItem('currentUserSideBarVisibility', 'false')
    })

    return {
      handleWhenAppearSideBar,
      downloadUserManualFile,
      surveyStats,
      categories,
      surveyImage,
      redirectToPage,
      backButtonTitle,
      backButtonIcon,
      backColor
    }
  },
  methods: { isAdminOrModeratorOrValidator, isHasAnyRole }
}
</script>

<style scoped lang="scss">
@use '@/assets/css/variables' as *;

.custom-main-user-view-screen {
  display: flex;
  position: relative;
  flex-direction: column;
  background-image: url('@/assets/images/variety/dubai-city-sky-view.png');
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

.custom-main-user-view-screen-category-rows {
  display: flex;
  flex-wrap: wrap;
  margin-top: 3%;
  justify-content: center;
  margin-bottom: 1%;
}

.custom-main-user-view-screen-column-style {
  display: contents;
}

.custom-main-home-category-card {
  margin: 2%;
  transition: transform 0.3s,
  box-shadow 0.3s;
}

.rtl .custom-main-home-category-card {
  margin-left: 5%;
}

.ltr .custom-main-home-category-card {
  margin-right: 5%;
}

.main-home-screen-title-headline {
  font-size: 3.5rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  background-clip: text;
  color: white;
  -webkit-background-clip: text;
  text-shadow: 4px 4px 6px rgba(0, 0, 0, 0.25);
  margin: 20px 0;
  padding: 10px;
  text-align: center;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.custom-survey-dashboard-subhead {
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

.custom-app-main-home-details {
  width: 50%;
}

.custom-app-main-home-company-info-details {
  width: 70%;
}

.custom-main-user-home-screen-container {
  overflow-y: auto;
  background-color: #e8f5e9;
  color: #2e7d32;
}

.system-title-shortcut {
  display: block;
}

.custom-download-button {
  margin: 1rem;
  text-transform: uppercase;
  border-radius: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background-color: $sdss-questions-color;
  color: $white-color;
}

.custom-info-icon {
  margin-top: 0.9%;
}
</style>
