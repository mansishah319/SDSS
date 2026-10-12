<template>
  <DubaiPoliceLoader :is-loading="isLoading" variant="badge" :message="loaderMessage" />

  <v-container v-if="isAuthorized" fluid class="survey-user-dashboard">
    <div
      id="survey-dashboard-export-root"
      class="survey-user-dashboard__shell survey-user-dashboard__export-root"
      :ref="(element) => (surveyDashboardPdf.reportExportRoot.value = element as HTMLElement | null)"
    >
      <GenericDashboardHeaderView
        :title-en="headerCopy.titleEn"
        :title-ar="headerCopy.titleAr"
        :subtitle-en="headerCopy.subtitleEn"
        :subtitle-ar="headerCopy.subtitleAr"
        icon="mdi-chart-box-outline"
        :show-back-button="true"
        back-route-name="MainUsersSurveyHomeScreen"
        :show-refresh-button="true"
        :refresh-label-en="headerCopy.refreshLabelEn"
        :refresh-label-ar="headerCopy.refreshLabelAr"
        :refresh-loading="isLoading"
        :show-pdf-button="canExportPdf"
        :pdf-label-en="headerCopy.pdfLabelEn"
        :pdf-label-ar="headerCopy.pdfLabelAr"
        :pdf-loading="surveyDashboardPdf.isPdfExporting.value"
        :total-value="formatNumber(dashboardResponse.totalSurveys)"
        :total-label-en="headerCopy.totalLabelEn"
        :total-label-ar="headerCopy.totalLabelAr"
        @refresh="handleRetry"
        @download-pdf="surveyDashboardPdf.handlePdfDownload"
      />

      <section class="survey-user-dashboard__hero">
        <div class="survey-user-dashboard__hero-grid">
          <div class="survey-user-dashboard__hero-card">
            <div class="survey-user-dashboard__eyebrow">
              <span class="survey-user-dashboard__eyebrow-dot"></span>
              {{ copy.dashboardLabel }}
            </div>

            <div class="survey-user-dashboard__hero-main">
              <div class="survey-user-dashboard__hero-copy">
                <h1>{{ copy.dashboardTitle }}</h1>
                <p>{{ copy.dashboardDescription }}</p>
              </div>

              <div class="survey-user-dashboard__hero-badges">
                <span class="survey-user-dashboard__badge">{{ userTypeBadge }}</span>
                <span class="survey-user-dashboard__badge survey-user-dashboard__badge--muted">{{
                    moderatorBadge
                  }}</span>
              </div>
            </div>

            <div class="survey-user-dashboard__profile-grid">
              <div class="survey-user-dashboard__profile-item survey-user-dashboard__profile-item--wide">
                <span>{{ displayName }}</span>
                <small>{{ copy.profileIdentity }}</small>
              </div>
              <div class="survey-user-dashboard__profile-item">
                <span>{{ dashboardResponse.jobTitle || '--' }}</span>
                <small>{{ copy.profileJobTitle }}</small>
              </div>
              <div class="survey-user-dashboard__profile-item">
                <span>{{ dashboardResponse.departmentName || '--' }}</span>
                <small>{{ copy.profileDepartment }}</small>
              </div>
              <div class="survey-user-dashboard__profile-item">
                <span>{{
                    dashboardResponse.employeeGrpNumber ? formatNumber(dashboardResponse.employeeGrpNumber) : '--'
                  }}</span>
                <small>{{ copy.profileGrp }}</small>
              </div>
              <div class="survey-user-dashboard__profile-item survey-user-dashboard__profile-item--wide">
                <span>{{ formatDateTime(dashboardResponse.lastActivityDate) }}</span>
                <small>{{ copy.profileLastActivity }}</small>
              </div>
            </div>
          </div>

          <SurveyDashboardPanel
            class="survey-user-dashboard__spotlight"
            :title="copy.spotlightTitle"
            :subtitle="copy.spotlightSubtitle"
            icon="mdi-target-account"
            accent="#1570ef"
          >
            <div class="survey-user-dashboard__spotlight-list">
              <div v-for="item in spotlightItems" :key="item.label" class="survey-user-dashboard__spotlight-item">
                <div class="survey-user-dashboard__spotlight-icon" :style="{ '--accent': item.accent }">
                  <v-icon :icon="item.icon" />
                </div>
                <div class="survey-user-dashboard__spotlight-copy">
                  <span>{{ item.label }}</span>
                  <strong>{{ item.value }}</strong>
                </div>
              </div>
            </div>
          </SurveyDashboardPanel>
        </div>
      </section>

      <v-alert
        v-if="dashboardError && !isLoading"
        type="error"
        variant="tonal"
        border="start"
        class="survey-user-dashboard__alert"
      >
        <div class="survey-user-dashboard__alert-body">
          <span>{{ dashboardError }}</span>
          <v-btn color="error" variant="outlined" size="small" @click="handleRetry">{{ copy.retry }}</v-btn>
        </div>
      </v-alert>

      <SurveyDashboardSkeleton v-if="isLoading" />

      <template v-else-if="!isDashboardEmpty">
        <div class="survey-user-dashboard__kpis">
          <SurveyDashboardKpiCard
            v-for="card in kpiCards"
            :key="card.title"
            :title="card.title"
            :value="card.value"
            :subtitle="card.subtitle"
            :icon="card.icon"
            :accent="card.accent"
            :badge="card.badge"
            :progress="card.progress"
          />
        </div>

        <div class="survey-user-dashboard__analytics">
          <SurveyDashboardPanel
            :title="copy.participationTitle"
            :subtitle="copy.participationSubtitle"
            icon="mdi-chart-arc"
            accent="#0b6e72"
          >
            <div class="survey-user-dashboard__chart-shell survey-user-dashboard__chart-shell--radial">
              <apexchart
                type="radialBar"
                height="310"
                :options="participationChartOptions"
                :series="participationSeries"
              />
            </div>

            <div class="survey-user-dashboard__chart-summary">
              <div>
                <strong>{{ safeParticipationRate }}%</strong>
                <span>{{ copy.participationTitle }}</span>
              </div>
              <div>
                <strong>{{ formatNumber(dashboardResponse.answeredSurveys) }}</strong>
                <span>{{ copy.answeredSurveys }}</span>
              </div>
              <div>
                <strong>{{ formatNumber(dashboardResponse.pendingSurveys) }}</strong>
                <span>{{ copy.pendingSurveys }}</span>
              </div>
            </div>
          </SurveyDashboardPanel>

          <SurveyDashboardPanel
            :title="copy.distributionTitle"
            :subtitle="copy.distributionSubtitle"
            icon="mdi-chart-donut"
            accent="#1570ef"
          >
            <div v-if="hasDistributionData" class="survey-user-dashboard__chart-shell">
              <apexchart type="donut" height="310" :options="distributionChartOptions" :series="distributionSeries" />
            </div>
            <div v-else class="survey-user-dashboard__inline-empty">
              <v-icon size="28" icon="mdi-chart-donut-variant" />
              <p>{{ copy.noVisualData }}</p>
            </div>
          </SurveyDashboardPanel>

          <SurveyDashboardPanel
            :title="copy.activityTitle"
            :subtitle="copy.activitySubtitle"
            icon="mdi-lightning-bolt-outline"
            accent="#b54708"
          >
            <div class="survey-user-dashboard__activity-list">
              <div v-for="metric in activityMetrics" :key="metric.label" class="survey-user-dashboard__activity-item">
                <div class="survey-user-dashboard__activity-icon" :style="{ '--accent': metric.accent }">
                  <v-icon :icon="metric.icon" />
                </div>
                <div class="survey-user-dashboard__activity-copy">
                  <span>{{ metric.label }}</span>
                  <strong>{{ metric.value }}</strong>
                </div>
              </div>
            </div>
          </SurveyDashboardPanel>
        </div>

        <SurveyDashboardPanel
          v-if="showModeratorPanel"
          :title="copy.moderatorTitle"
          :subtitle="copy.moderatorSubtitle"
          icon="mdi-shield-account-outline"
          accent="#6941c6"
        >
          <div class="survey-user-dashboard__moderator-grid">
            <div class="survey-user-dashboard__moderator-stats">
              <div v-for="item in reviewHighlights" :key="item.label" class="survey-user-dashboard__moderator-stat">
                <span>{{ item.label }}</span>
                <strong :style="{ color: item.accent }">{{ item.value }}</strong>
              </div>
            </div>

            <div
              v-if="hasReviewData"
              class="survey-user-dashboard__chart-shell survey-user-dashboard__chart-shell--review"
            >
              <apexchart type="bar" height="250" :options="reviewChartOptions" :series="reviewSeries" />
            </div>
            <div v-else class="survey-user-dashboard__inline-empty">
              <video autoplay muted loop playsinline preload="metadata" style="width: 200px">
                <source :src="emptyStateVideoUrl" type="video/mp4" />
              </video>
              <p class="mt-n8">{{ copy.noReviewData }}</p>
            </div>
          </div>
        </SurveyDashboardPanel>

        <div class="survey-user-dashboard__lists">
          <SurveyDashboardSurveyList
            :title="copy.activeListTitle"
            :subtitle="copy.activeListSubtitle"
            :count="dashboardResponse.activeSurveysCount"
            :surveys="dashboardResponse.activeSurveysList"
            :is-arabic="isArabic()"
            tone="#1570ef"
            :empty-message="copy.noActiveListData"
          />
          <SurveyDashboardSurveyList
            :title="copy.pendingListTitle"
            :subtitle="copy.pendingListSubtitle"
            :count="dashboardResponse.pendingSurveysCount"
            :surveys="dashboardResponse.pendingSurveysList"
            :is-arabic="isArabic()"
            tone="#f79009"
            :empty-message="copy.noPendingListData"
          />
          <SurveyDashboardSurveyList
            :title="copy.completedListTitle"
            :subtitle="copy.completedListSubtitle"
            :count="dashboardResponse.completedSurveysCount"
            :surveys="dashboardResponse.completedSurveysList"
            :is-arabic="isArabic()"
            tone="#0f9f76"
            :empty-message="copy.noCompletedListData"
          />
        </div>
      </template>

      <div v-else class="survey-user-dashboard__empty-state">
        <div class="survey-user-dashboard__empty-icon">
          <v-icon size="54" icon="mdi-chart-box-outline" />
        </div>
        <h2>{{ copy.emptyStateTitle }}</h2>
        <p>{{ copy.emptyStateDescription }}</p>
        <v-btn color="primary" variant="elevated" @click="handleRetry">{{ copy.retry }}</v-btn>
      </div>
    </div>

    <ContentLoader
      v-if="surveyDashboardPdf.isPdfExporting.value"
      class="survey-user-dashboard__pdf-loader"
      variant="analytics"
      overlay
      compact-badge
      :message="pdfLoaderMessage"
    />
  </v-container>

  <AccessDeniedView
    v-else-if="!isAuthorized"
    :back-icon="backButtonIcon"
    :back-title="backButtonTitle"
    :back-color="backButtonColor"
    :back-redirect-path="redirectToSurveyHomePage"
  ></AccessDeniedView>

  <FooterComponent :justify-content="'justify-content-start'"></FooterComponent>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref, watchEffect } from 'vue'
import type { ApexOptions } from 'apexcharts'
import { useI18n } from 'vue-i18n'
import { toast } from 'vue3-toastify'
import FooterComponent from '@/components/common/FooterComponent.vue'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import ContentLoader from '@/components/loader/ContentLoader.vue'
import AccessDeniedView from '@/views/common/AccessDeniedView.vue'
import { Fields, SecurityConstants, URIS } from '@/constants/DpConstants'
import { get, getPublicSurveyAuth } from '@/utils/GenericApiHelper'
import { SuccessResponse } from '@/dto/generic/SuccessResponse'
import type { SurveyUserDashboardResponse } from '@/dto/survey/SurveyUserDashboardResponse.ts'
import { useSurveyTokenStore } from '@/store/useSurveyTokenStore'
import { useSidebarStore } from '@/store/sidebar'
import { isDelphiModerator, isHasAnyRole } from '@/utils/roleUtils'
import SurveyDashboardKpiCard from '@/views/surveys/dashboard/components/SurveyDashboardKpiCard.vue'
import SurveyDashboardPanel from '@/views/surveys/dashboard/components/SurveyDashboardPanel.vue'
import SurveyDashboardSurveyList from '@/views/surveys/dashboard/components/SurveyDashboardSurveyList.vue'
import SurveyDashboardSkeleton from '@/views/surveys/dashboard/components/SurveyDashboardSkeleton.vue'
import { useSurveyDashboardPdf } from '@/views/surveys/dashboard/useSurveyDashboardPdf'

const createEmptyDashboard = (): SurveyUserDashboardResponse => ({
  internalUser: false,
  delphiModerator: false,
  employeeGrpNumber: 0,
  employeeFullName: '',
  departmentName: '',
  jobTitle: '',
  totalSurveys: 0,
  answeredSurveys: 0,
  pendingSurveys: 0,
  participationRate: 0,
  recentActivity: 0,
  lastActivityDate: '',
  activeSurveysCount: 0,
  pendingSurveysCount: 0,
  completedSurveysCount: 0,
  totalAnswersCount: 0,
  totalReviewsMade: 0,
  reviewedSolutionsCount: 0,
  reviewedChallengesCount: 0,
  activeSurveysList: [],
  pendingSurveysList: [],
  completedSurveysList: []
})

export default defineComponent({
  name: 'SurveysDashboard',
  components: {
    GenericDashboardHeaderView,
    ContentLoader,
    SurveyDashboardSkeleton,
    SurveyDashboardSurveyList,
    SurveyDashboardPanel,
    SurveyDashboardKpiCard,
    FooterComponent,
    AccessDeniedView
  },
  setup() {
    const { locale, t } = useI18n()
    const sidebarStore = useSidebarStore()
    const surveyTokenStore = useSurveyTokenStore()

    const employeeLang = localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'
    const surveyToken = ref(surveyTokenStore.surveyToken || localStorage.getItem(SecurityConstants.Survey_TOKEN))
    const isAuthorized = ref<boolean>(false)
    const isLoading = ref<boolean>(true)
    const dashboardError = ref('')
    const dashboardResponse = ref<SurveyUserDashboardResponse>(createEmptyDashboard())

    const redirectToSurveyHomePage = ref<string>('MainUsersSurveyHomeScreen')
    const backButtonTitle = ref<string>('sdssSurvey.backButton')
    const backButtonIcon = ref<string>('mdi mdi-home-lightbulb')
    const backButtonColor = ref<string>('#008b8b')
    const dashboardEndpoint = URIS.SURVEYS.SURVEY.BASE + URIS.SURVEYS.SURVEY.USER_DASHBOARD_UTI
    const emptyStateVideoUrl = new URL('../../../assets/images/No-record.mp4', import.meta.url).href
    const loaderMessage = computed(() => t('common.loader.surveyAnalytics'))
    const pdfLoaderMessage = computed(() =>
      isArabic() ? 'جاري تجهيز ملف PDF، يرجى الانتظار...' : 'Preparing the PDF file, please wait...'
    )
    const isArabicLanguage = computed(() => employeeLang === 'ar')
    const headerCopy = {
      titleEn: 'My Survey Dashboard',
      titleAr: 'لوحة الاستبيان الشخصية',
      subtitleEn: 'Survey Participation Command Center',
      subtitleAr: 'مركز متابعة مشاركاتك في الاستبيانات',
      refreshLabelEn: 'Refresh',
      refreshLabelAr: 'تحديث',
      pdfLabelEn: 'Download PDF',
      pdfLabelAr: 'تنزيل PDF',
      totalLabelEn: 'Total surveys',
      totalLabelAr: 'إجمالي الاستبيانات'
    }

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    const isArabic = (): boolean => employeeLang === 'ar'

    const copy = computed(() =>
      isArabic()
        ? {
          dashboardLabel: 'لوحة الاستبيان الشخصية',
          dashboardTitle: 'مركز متابعة مشاركاتك في الاستبيانات',
          dashboardDescription:
            'لوحة تنفيذية متقدمة تعرض مؤشرات الأداء، النشاط الأخير، إحصاءات المراجعة، وقوائم الاستبيانات الجارية والمعلقة والمكتملة.',
          spotlightTitle: 'مؤشرات سريعة',
          spotlightSubtitle: 'أهم ما يحتاج انتباهك الآن',
          participationTitle: 'صحة المشاركة',
          participationSubtitle: 'نسبة مساهمتك الحالية من إجمالي الاستبيانات المسندة إليك.',
          distributionTitle: 'توزيع حالة الاستبيانات',
          distributionSubtitle: 'مشهد واضح لكيفية توزع الاستبيانات بين النشطة والمعلقة والمكتملة.',
          activityTitle: 'نبض النشاط',
          activitySubtitle: 'آخر التفاعلات وإجمالي الإجابات وحجم النشاط الحديث.',
          moderatorTitle: 'إحصاءات المراجعة الإشرافية',
          moderatorSubtitle: 'تحليل مراجعات التحديات والحلول للمشرفين على دلفي.',
          activeListTitle: 'الاستبيانات النشطة',
          activeListSubtitle: 'جاهزة الآن للمشاركة',
          pendingListTitle: 'الاستبيانات المعلقة',
          pendingListSubtitle: 'بانتظار استجابتك',
          completedListTitle: 'الاستبيانات المكتملة',
          completedListSubtitle: 'تم إنجازها',
          totalSurveys: 'إجمالي الاستبيانات',
          answeredSurveys: 'الاستبيانات المجاب عنها',
          pendingSurveys: 'الاستبيانات المعلقة',
          activeSurveys: 'الاستبيانات النشطة',
          recentActivity: 'النشاط الأخير',
          totalAnswers: 'إجمالي الإجابات',
          lastActivity: 'آخر تفاعل',
          totalReviews: 'إجمالي المراجعات',
          reviewedSolutions: 'الحلول المراجعة',
          reviewedChallenges: 'التحديات المراجعة',
          internalUser: 'مستخدم داخلي',
          publicUser: 'مستخدم مدعو',
          moderator: 'مشرف دلفي',
          participant: 'مشارك',
          profileIdentity: 'هوية المستخدم',
          profileJobTitle: 'الوظيفة',
          profileDepartment: 'الإدارة',
          profileGrp: 'الرقم العام',
          profileLastActivity: 'آخر نشاط',
          noVisualData: 'لا توجد بيانات كافية لعرض الرسم البياني حالياً.',
          noReviewData: 'لا توجد مراجعات إشرافية متاحة حالياً.',
          noActiveListData: 'لا توجد استبيانات نشطة حالياً.',
          noPendingListData: 'لا توجد استبيانات معلقة حالياً.',
          noCompletedListData: 'لا توجد استبيانات مكتملة حالياً.',
          emptyStateTitle: 'لا توجد بيانات كافية لعرض لوحة الاستبيانات الآن.',
          emptyStateDescription:
            'سيتم تحديث هذه اللوحة تلقائياً بمجرد توفر استبيانات أو نشاط أو إحصاءات مراجعة مرتبطة بحسابك.',
          retry: 'إعادة المحاولة',
          backHome: 'العودة إلى الرئيسية',
          noRecentActivity: 'لا يوجد نشاط حديث'
        }
        : {
          dashboardLabel: 'My Survey Dashboard',
          dashboardTitle: 'Survey Participation Command Center',
          dashboardDescription:
            'A premium dashboard that brings together KPI performance, recent activity, moderator review analytics, and beautifully structured active, pending, and completed survey queues.',
          spotlightTitle: 'Priority Signals',
          spotlightSubtitle: 'The metrics that deserve attention right now',
          participationTitle: 'Participation Health',
          participationSubtitle: 'Your current contribution rate across all surveys assigned to you.',
          distributionTitle: 'Survey Status Mix',
          distributionSubtitle:
            'A clear view of how your survey workload is split across active, pending, and completed items.',
          activityTitle: 'Activity Pulse',
          activitySubtitle: 'Recent motion, answer volume, and your latest dashboard touchpoint.',
          moderatorTitle: 'Moderator Review Analytics',
          moderatorSubtitle: 'Review distribution across challenge and solution moderation for Delphi moderators.',
          activeListTitle: 'Active Surveys',
          activeListSubtitle: 'Ready for contribution now',
          pendingListTitle: 'Pending Surveys',
          pendingListSubtitle: 'Waiting for your response',
          completedListTitle: 'Completed Surveys',
          completedListSubtitle: 'Already finished',
          totalSurveys: 'Total surveys',
          answeredSurveys: 'Answered surveys',
          pendingSurveys: 'Pending surveys',
          activeSurveys: 'Active surveys',
          recentActivity: 'Recent activity',
          totalAnswers: 'Total answers',
          lastActivity: 'Last activity',
          totalReviews: 'Total reviews',
          reviewedSolutions: 'Reviewed solutions',
          reviewedChallenges: 'Reviewed challenges',
          internalUser: 'Internal user',
          publicUser: 'Invited user',
          moderator: 'Delphi moderator',
          participant: 'Participant',
          profileIdentity: 'User profile',
          profileJobTitle: 'Job title',
          profileDepartment: 'Department',
          profileGrp: 'GRP Number',
          profileLastActivity: 'Last activity',
          noVisualData: 'There is not enough data to render this chart yet.',
          noReviewData: 'No moderator review activity is available yet.',
          noActiveListData: 'No active surveys are available right now.',
          noPendingListData: 'No pending surveys are waiting right now.',
          noCompletedListData: 'No completed surveys are available yet.',
          emptyStateTitle: 'There is not enough data to render your survey dashboard yet.',
          emptyStateDescription:
            'This dashboard will populate automatically as survey assignments, activity, and moderation statistics become available for your account.',
          retry: 'Retry',
          backHome: 'Back to Home',
          noRecentActivity: 'No recent activity'
        }
    )

    const formatNumber = (value: number | string) =>
      new Intl.NumberFormat(isArabic() ? 'ar-AE' : 'en-US').format(Number(value || 0))

    const formatDateTime = (value?: string) => {
      if (!value) return copy.value.noRecentActivity
      const date = new Date(value)
      if (Number.isNaN(date.getTime())) return copy.value.noRecentActivity
      return new Intl.DateTimeFormat(isArabic() ? 'ar-AE' : 'en-US', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date)
    }

    const safeParticipationRate = computed(() =>
      Math.max(0, Math.min(Number(dashboardResponse.value.participationRate) || 0, 100))
    )
    const totalSurveyStates = computed(
      () =>
        Number(dashboardResponse.value.activeSurveysCount || 0) +
        Number(dashboardResponse.value.pendingSurveysCount || 0) +
        Number(dashboardResponse.value.completedSurveysCount || 0)
    )

    const displayName = computed(
      () => dashboardResponse.value.employeeFullName || (isArabic() ? 'مستخدم الاستبيان' : 'Survey User')
    )
    const userTypeBadge = computed(() =>
      dashboardResponse.value.internalUser || isHasAnyRole() ? copy.value.internalUser : copy.value.publicUser
    )
    const moderatorBadge = computed(() =>
      dashboardResponse.value.delphiModerator || isDelphiModerator() ? copy.value.moderator : copy.value.participant
    )

    const isDashboardEmpty = computed(() => {
      const data = dashboardResponse.value
      return (
        !data.employeeFullName &&
        data.totalSurveys === 0 &&
        data.totalAnswersCount === 0 &&
        data.totalReviewsMade === 0 &&
        data.activeSurveysList.length === 0 &&
        data.pendingSurveysList.length === 0 &&
        data.completedSurveysList.length === 0
      )
    })

    const spotlightItems = computed(() => [
      {
        label: copy.value.recentActivity,
        value: formatNumber(dashboardResponse.value.recentActivity),
        icon: 'mdi-chart-timeline-variant',
        accent: '#1570ef'
      },
      {
        label: copy.value.totalAnswers,
        value: formatNumber(dashboardResponse.value.totalAnswersCount),
        icon: 'mdi-message-reply-text-outline',
        accent: '#0f9f76'
      },
      {
        label: copy.value.lastActivity,
        value: formatDateTime(dashboardResponse.value.lastActivityDate),
        icon: 'mdi-calendar-clock-outline',
        accent: '#b54708'
      }
    ])

    const activityMetrics = computed(() => spotlightItems.value)

    const kpiCards = computed(() => [
      {
        title: copy.value.totalSurveys,
        value: formatNumber(dashboardResponse.value.totalSurveys),
        subtitle: isArabic()
          ? 'إجمالي الاستبيانات المرتبطة بحسابك.'
          : 'All survey assignments currently associated with your account.',
        icon: 'mdi-view-dashboard-outline',
        accent: '#0b6e72',
        badge: userTypeBadge.value,
        progress:
          dashboardResponse.value.totalSurveys > 0
            ? (dashboardResponse.value.answeredSurveys / dashboardResponse.value.totalSurveys) * 100
            : 0
      },
      {
        title: copy.value.answeredSurveys,
        value: formatNumber(dashboardResponse.value.answeredSurveys),
        subtitle: isArabic()
          ? 'استبيانات تمت الإجابة عليها بنجاح.'
          : 'Surveys where your response has already been captured.',
        icon: 'mdi-check-decagram-outline',
        accent: '#0f9f76',
        progress: safeParticipationRate.value
      },
      {
        title: copy.value.pendingSurveys,
        value: formatNumber(dashboardResponse.value.pendingSurveys),
        subtitle: isArabic() ? 'استبيانات تحتاج إلى استكمال مشاركتك.' : 'Surveys still waiting for your contribution.',
        icon: 'mdi-timer-sand',
        accent: '#f79009',
        progress:
          dashboardResponse.value.totalSurveys > 0
            ? (dashboardResponse.value.pendingSurveys / dashboardResponse.value.totalSurveys) * 100
            : 0
      },
      {
        title: copy.value.activeSurveys,
        value: formatNumber(dashboardResponse.value.activeSurveysCount),
        subtitle: isArabic()
          ? 'استبيانات مفتوحة حالياً وجاهزة للمشاركة.'
          : 'Surveys currently open and ready for participation.',
        icon: 'mdi-rocket-launch-outline',
        accent: '#1570ef',
        badge: moderatorBadge.value,
        progress:
          totalSurveyStates.value > 0
            ? (dashboardResponse.value.activeSurveysCount / totalSurveyStates.value) * 100
            : 0
      }
    ])

    const reviewHighlights = computed(() => [
      {
        label: copy.value.totalReviews,
        value: formatNumber(dashboardResponse.value.totalReviewsMade),
        accent: '#0b6e72'
      },
      {
        label: copy.value.reviewedSolutions,
        value: formatNumber(dashboardResponse.value.reviewedSolutionsCount),
        accent: '#1570ef'
      },
      {
        label: copy.value.reviewedChallenges,
        value: formatNumber(dashboardResponse.value.reviewedChallengesCount),
        accent: '#f79009'
      }
    ])

    const participationSeries = computed(() => [safeParticipationRate.value])
    const participationChartOptions = computed<ApexOptions>(() => ({
      chart: {
        type: 'radialBar',
        sparkline: { enabled: true },
        toolbar: { show: false }
      },
      colors: ['#0b6e72'],
      stroke: {
        lineCap: 'round'
      },
      plotOptions: {
        radialBar: {
          startAngle: -130,
          endAngle: 130,
          hollow: {
            size: '64%',
            background: '#ffffff'
          },
          track: {
            background: '#e4edf0',
            strokeWidth: '100%'
          },
          dataLabels: {
            name: { show: false },
            value: {
              offsetY: 8,
              color: '#101828',
              fontSize: '2rem',
              fontWeight: 800,
              formatter: (value: number) => `${Math.round(value)}%`
            }
          }
        }
      },
      fill: {
        type: 'gradient',
        gradient: {
          shade: 'light',
          type: 'horizontal',
          gradientToColors: ['#12b76a'],
          stops: [0, 100]
        }
      }
    }))

    const distributionSeries = computed(() => [
      Number(dashboardResponse.value.activeSurveysCount || 0),
      Number(dashboardResponse.value.pendingSurveysCount || 0),
      Number(dashboardResponse.value.completedSurveysCount || 0)
    ])

    const distributionChartOptions = computed<ApexOptions>(() => ({
      chart: {
        type: 'donut',
        toolbar: { show: false }
      },
      labels: [isArabic() ? 'نشط' : 'Active', isArabic() ? 'معلق' : 'Pending', isArabic() ? 'مكتمل' : 'Completed'],
      colors: ['#1570ef', '#f79009', '#0f9f76'],
      stroke: {
        width: 4,
        colors: ['#ffffff']
      },
      legend: {
        position: 'bottom',
        fontSize: '13px'
      },
      dataLabels: {
        enabled: true,
        formatter: (value: number) => `${Math.round(value)}%`
      },
      plotOptions: {
        pie: {
          donut: {
            size: '68%',
            labels: {
              show: true,
              total: {
                show: true,
                label: isArabic() ? 'الإجمالي' : 'Total',
                formatter: () => formatNumber(totalSurveyStates.value)
              },
              value: {
                formatter: (value: string) => formatNumber(value)
              }
            }
          }
        }
      }
    }))

    const reviewSeries = computed(() => [
      {
        name: isArabic() ? 'المراجعات' : 'Reviews',
        data: [
          Number(dashboardResponse.value.totalReviewsMade || 0),
          Number(dashboardResponse.value.reviewedSolutionsCount || 0),
          Number(dashboardResponse.value.reviewedChallengesCount || 0)
        ]
      }
    ])

    const reviewChartOptions = computed<ApexOptions>(() => ({
      chart: {
        type: 'bar',
        toolbar: { show: false }
      },
      colors: ['#0b6e72', '#1570ef', '#f79009'],
      plotOptions: {
        bar: {
          horizontal: true,
          distributed: true,
          borderRadius: 8,
          barHeight: '46%'
        }
      },
      grid: {
        borderColor: '#eaecf0',
        strokeDashArray: 4
      },
      dataLabels: {
        enabled: true
      },
      legend: {
        show: false
      },
      xaxis: {
        categories: [copy.value.totalReviews, copy.value.reviewedSolutions, copy.value.reviewedChallenges]
      },
      tooltip: {
        y: {
          formatter: (value: number) => formatNumber(value)
        }
      }
    }))

    const hasDistributionData = computed(() => totalSurveyStates.value > 0)
    const hasReviewData = computed(
      () =>
        Number(dashboardResponse.value.totalReviewsMade || 0) > 0 ||
        Number(dashboardResponse.value.reviewedSolutionsCount || 0) > 0 ||
        Number(dashboardResponse.value.reviewedChallengesCount || 0) > 0
    )
    const showModeratorPanel = computed(
      () => dashboardResponse.value.delphiModerator || isDelphiModerator() || hasReviewData.value
    )
    const canExportPdf = computed(
      () => isAuthorized.value && !isLoading.value && !dashboardError.value && !isDashboardEmpty.value
    )
    const surveyDashboardPdf = useSurveyDashboardPdf({
      isArabic: isArabicLanguage,
      reportLabel: computed(() => (isArabic() ? headerCopy.titleAr : headerCopy.titleEn)),
      localizedTitle: computed(() => displayName.value)
    })

    const userNotAuthorized = () => {
      isAuthorized.value = false
    }

    const authorizedUser = () => {
      isAuthorized.value = true
    }

    const fetchDashboardData = async () => {
      dashboardError.value = ''
      dashboardResponse.value = createEmptyDashboard()

      if (isHasAnyRole()) {
        authorizedUser()
        const response = await get<SuccessResponse<SurveyUserDashboardResponse>>(dashboardEndpoint)
        if (response?.success) {
          dashboardResponse.value = response.data || createEmptyDashboard()
          return
        }

        dashboardError.value =
          response?.message ||
          (isArabic() ? 'تعذر تحميل بيانات لوحة الاستبيانات.' : 'Unable to load survey dashboard data.')
        return
      }

      if (surveyToken.value) {
        authorizedUser()
        const response = await getPublicSurveyAuth<SuccessResponse<SurveyUserDashboardResponse>>(
          dashboardEndpoint,
          employeeLang
        )
        const payload = response?.data

        if (payload?.success) {
          dashboardResponse.value = payload.data || createEmptyDashboard()
          return
        }

        dashboardError.value =
          payload?.message ||
          (isArabic() ? 'تعذر تحميل بيانات لوحة الاستبيانات.' : 'Unable to load survey dashboard data.')
        return
      }

      userNotAuthorized()
    }

    const loadDashboard = async () => {
      isLoading.value = true
      try {
        await fetchDashboardData()
      } finally {
        isLoading.value = false
      }
    }

    const handleRetry = async () => {
      await loadDashboard()
      if (dashboardError.value) {
        toast.error(dashboardError.value)
      }
    }

    onMounted(async () => {
      sidebarStore.disable()
      await loadDashboard()
    })

    return {
      isLoading,
      isAuthorized,
      loaderMessage,
      pdfLoaderMessage,
      redirectToSurveyHomePage,
      backButtonTitle,
      backButtonIcon,
      backButtonColor,
      dashboardResponse,
      dashboardError,
      headerCopy,
      copy,
      isArabic,
      canExportPdf,
      surveyDashboardPdf,
      displayName,
      userTypeBadge,
      moderatorBadge,
      spotlightItems,
      activityMetrics,
      kpiCards,
      reviewHighlights,
      formatNumber,
      formatDateTime,
      safeParticipationRate,
      participationSeries,
      participationChartOptions,
      distributionSeries,
      distributionChartOptions,
      reviewSeries,
      reviewChartOptions,
      hasDistributionData,
      hasReviewData,
      showModeratorPanel,
      isDashboardEmpty,
      emptyStateVideoUrl,
      handleRetry
    }
  }
})
</script>

<style scoped lang="scss">
.survey-user-dashboard {
  min-height: 100vh;
  padding: 1.25rem 0 2.5rem;
  // background:
  //   radial-gradient(circle at top left, rgba(21, 112, 239, 0.16), transparent 26%),
  //   radial-gradient(circle at top right, rgba(11, 110, 114, 0.18), transparent 30%),
  //   linear-gradient(180deg, #eef4f7 0%, #f7f9fb 42%, #eef5f4 100%);
}

.survey-user-dashboard__shell {
  width: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 1.3rem;
  position: relative;
}

.survey-user-dashboard__hero {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.survey-user-dashboard__alert-body {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.survey-user-dashboard__alert-body {
  justify-content: space-between;
}

.survey-user-dashboard__hero-grid,
.survey-user-dashboard__analytics,
.survey-user-dashboard__lists {
  display: grid;
  gap: 1.15rem;
}

.survey-user-dashboard__hero-grid {
  grid-template-columns: minmax(0, 1.5fr) minmax(320px, 0.85fr);
}

.survey-user-dashboard__hero-card {
  position: relative;
  overflow: hidden;
  padding: 1.7rem;
  border-radius: 30px;
  background: #0da06e;
  box-shadow: 0 26px 70px rgba(15, 23, 42, 0.14);
  color: #ffffff;

  &::before,
  &::after {
    content: '';
    position: absolute;
    border-radius: 50%;
    pointer-events: none;
  }

  &::before {
    inset-inline-end: -80px;
    top: -48px;
    width: 220px;
    height: 220px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.15), transparent 72%);
  }

  &::after {
    inset-inline-start: -60px;
    bottom: -120px;
    width: 240px;
    height: 240px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.12), transparent 72%);
  }
}

.survey-user-dashboard__eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
  padding: 0.42rem 0.8rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  font-size: 0.85rem;
  font-weight: 700;
}

.survey-user-dashboard__eyebrow-dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: #98f5e1;
}

.survey-user-dashboard__hero-main {
  margin-top: 1.3rem;
  display: flex;
  justify-content: space-between;
  gap: 1.2rem;
}

.survey-user-dashboard__hero-copy {
  max-width: 44rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;

  h1,
  p {
    margin: 0;
    display: flex;
  }

  h1 {
    font-size: 25px;
    line-height: 1.18;
    font-weight: 800;
    color: #fff;
    text-align: right;
  }

  p {
    margin-top: 0.85rem;
    color: rgba(255, 255, 255, 0.86);
    line-height: 1.8;
  }
}

.survey-user-dashboard__hero-badges {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  justify-content: flex-end;
  gap: 0.7rem;
}

.survey-user-dashboard__badge {
  display: inline-flex;
  align-items: center;
  min-height: 38px;
  padding: 0.55rem 0.95rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  font-size: 0.6rem;
  font-family: bukraregular;
}

.survey-user-dashboard__badge--muted {
  background: rgba(255, 255, 255, 0.08);
}

.survey-user-dashboard__profile-grid,
.survey-user-dashboard__kpis {
  display: grid;
  gap: 1rem;
}

.survey-user-dashboard__profile-grid {
  margin-top: 1.45rem;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.survey-user-dashboard__profile-item {
  min-height: 96px;
  padding: 1rem;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  span,
  small {
    display: block;
  }

  span {
    font-size: 12px;
    line-height: 1.3;
    word-break: break-word;
    font-family: bukraregular;
    margin-bottom: 10px;
  }

  small {
    color: rgba(255, 255, 255, 0.72);
    font-size: 0.78rem;
  }
}

.survey-user-dashboard__profile-item--wide {
  grid-column: span 2;
}

.survey-user-dashboard__spotlight {
  height: 100%;
}

.survey-user-dashboard__spotlight-list,
.survey-user-dashboard__activity-list,
.survey-user-dashboard__moderator-stats {
  display: grid;
  gap: 0.9rem;
}

.survey-user-dashboard__spotlight-list {
  gap: 1.9rem;
}

.survey-user-dashboard__activity-list {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.survey-user-dashboard__spotlight-item,
.survey-user-dashboard__activity-item,
.survey-user-dashboard__moderator-stat {
  padding: 1rem;
  border-radius: 20px;
  background: rgba(15, 23, 42, 0.03);
}

.survey-user-dashboard__spotlight-item,
.survey-user-dashboard__activity-item {
  display: flex;
  align-items: center;
  gap: 0.9rem;
}

.survey-user-dashboard__spotlight-icon,
.survey-user-dashboard__activity-icon {
  --accent: #1570ef;

  width: 50px;
  height: 50px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: color-mix(in srgb, var(--accent) 12%, #ffffff);
  color: var(--accent);
  flex-shrink: 0;
}

.survey-user-dashboard__spotlight-copy,
.survey-user-dashboard__activity-copy {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;

  span {
    color: #667085;
    font-size: 0.82rem;
    font-family: bukraregular;
  }

  strong {
    color: #101828;
    font-size: 1.08rem;
    font-weight: 800;
  }
}

.survey-user-dashboard__kpis {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.survey-user-dashboard__analytics {
  grid-template-columns: minmax(0, 1.05fr) minmax(0, 0.95fr);

  > :last-child {
    grid-column: span 2;
  }
}

.survey-user-dashboard__chart-shell {
  min-height: 290px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.survey-user-dashboard__chart-shell--radial,
.survey-user-dashboard__chart-shell--review {
  min-height: 250px;
}

.survey-user-dashboard__chart-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.8rem;
  text-align: center;

  > div {
    padding: 0.95rem;
    border-radius: 18px;
    background: rgba(15, 23, 42, 0.03);
    display: flex;
    flex-direction: column;
    gap: 0.22rem;
    align-items: center;
  }

  strong {
    color: #101828;
    font-size: 1.1rem;
    font-weight: 800;
  }

  span {
    color: #667085;
    font-size: 0.8rem;
    font-family: bukraregular;
  }
}

.survey-user-dashboard__moderator-grid {
  display: grid;
  grid-template-columns: minmax(280px, 0.85fr) minmax(0, 1.15fr);
  gap: 1rem;
  align-items: center;
}

.survey-user-dashboard__moderator-stat {
  display: flex;
  justify-content: space-between;
  gap: 1rem;

  span {
    color: #667085;
    font-size: 0.82rem;
    font-family: bukraregular;
  }

  strong {
    font-size: 1.08rem;
    font-weight: 800;
  }
}

.survey-user-dashboard__lists {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.survey-user-dashboard__inline-empty,
.survey-user-dashboard__empty-state {
  text-align: center;
}

.survey-user-dashboard__inline-empty {
  min-height: 250px;
  display: grid;
  place-items: center;
  gap: 0.8rem;
  color: #667085;

  p {
    margin: 0;
  }
}

.survey-user-dashboard__empty-state {
  padding: 2.6rem 1.5rem;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 20px 56px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.9rem;

  h2,
  p {
    margin: 0;
  }

  h2 {
    color: #101828;
    font-size: 1.4rem;
    font-weight: 800;
  }

  p {
    max-width: 42rem;
    color: #667085;
    line-height: 1.8;
  }
}

.survey-user-dashboard__empty-icon {
  width: 92px;
  height: 92px;
  border-radius: 28px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, rgba(11, 110, 114, 0.12), rgba(21, 112, 239, 0.12));
  color: #0b6e72;
}

.survey-user-dashboard__footer-action {
  display: flex;
  justify-content: center;
}

.survey-user-dashboard__pdf-loader {
  border-radius: 30px;
}

.survey-user-dashboard :deep(.apexcharts-legend-text),
.survey-user-dashboard :deep(.apexcharts-text),
.survey-user-dashboard :deep(.apexcharts-datalabel-label),
.survey-user-dashboard :deep(.apexcharts-datalabel-value) {
  font-family: BukraRegular, sans-serif !important;
}

.survey-user-dashboard :deep(.apexcharts-canvas),
.survey-user-dashboard :deep(.apexcharts-svg) {
  max-width: 100%;
}

.survey-user-dashboard :deep(.apexcharts-datalabels text),
.survey-user-dashboard :deep(.apexcharts-text) {
  text-anchor: middle !important;
}

@media (max-width: 1280px) {
  .survey-user-dashboard__hero-grid,
  .survey-user-dashboard__moderator-grid {
    grid-template-columns: 1fr;
  }

  .survey-user-dashboard__kpis,
  .survey-user-dashboard__lists {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .survey-user-dashboard__analytics {
    grid-template-columns: 1fr;

    > :last-child {
      grid-column: auto;
    }
  }
}

@media (max-width: 900px) {
  .survey-user-dashboard__shell {
    width: min(100%, calc(100% - 20px));
  }

  .survey-user-dashboard__hero-main,
  .survey-user-dashboard__alert-body {
    flex-direction: column;
    align-items: flex-start;
  }

  .survey-user-dashboard__hero-badges {
    justify-content: flex-start;
  }

  .survey-user-dashboard__profile-grid,
  .survey-user-dashboard__kpis,
  .survey-user-dashboard__lists,
  .survey-user-dashboard__chart-summary {
    grid-template-columns: 1fr;
  }

  .survey-user-dashboard__profile-item--wide {
    grid-column: auto;
  }
}

@media (max-width: 600px) {
  .survey-user-dashboard__hero-card {
    padding: 1.15rem;
    border-radius: 24px;
  }
}
</style>
