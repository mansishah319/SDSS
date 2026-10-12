<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { useLeftArrowIcon } from '@/utils/rtlUtils'

type DashboardHeaderProps = {
  titleEn: string
  titleAr: string
  subtitleEn?: string
  subtitleAr?: string
  icon?: string
  showBackButton?: boolean
  backRouteName?: string
  backFallbackRouteName?: string
  backLabelEn?: string
  backLabelAr?: string
  showRefreshButton?: boolean
  refreshLabelEn?: string
  refreshLabelAr?: string
  refreshLoading?: boolean
  refreshIcon?: string
  showPdfButton?: boolean
  pdfLabelEn?: string
  pdfLabelAr?: string
  pdfLoading?: boolean
  pdfIcon?: string
  totalValue?: string | number | null
  totalLabelEn?: string
  totalLabelAr?: string
}

const props = withDefaults(defineProps<DashboardHeaderProps>(), {
  subtitleEn: '',
  subtitleAr: '',
  icon: 'mdi-view-dashboard-outline',
  showBackButton: false,
  backRouteName: '',
  backFallbackRouteName: '',
  backLabelEn: 'Back',
  backLabelAr: 'العودة',
  showRefreshButton: false,
  refreshLabelEn: 'Refresh',
  refreshLabelAr: 'تحديث',
  refreshLoading: false,
  refreshIcon: 'mdi-refresh',
  showPdfButton: false,
  pdfLabelEn: 'Download PDF',
  pdfLabelAr: 'تنزيل PDF',
  pdfLoading: false,
  pdfIcon: 'mdi-file-pdf-box',
  totalValue: null,
  totalLabelEn: 'Total',
  totalLabelAr: 'الإجمالي'
})

const emit = defineEmits<{
  refresh: []
  'download-pdf': []
}>()

const { locale } = useI18n()
const router = useRouter()
const leftArrowIcon = useLeftArrowIcon()

const isArabic = computed(() => locale.value === 'ar')
const activeTitle = computed(() => (isArabic.value ? props.titleAr : props.titleEn))
const inactiveTitle = computed(() => (isArabic.value ? props.titleEn : props.titleAr))
const activeSubtitle = computed(() => (isArabic.value ? props.subtitleAr : props.subtitleEn))
const activeBackLabel = computed(() => (isArabic.value ? props.backLabelAr : props.backLabelEn))
const activeRefreshLabel = computed(() => (isArabic.value ? props.refreshLabelAr : props.refreshLabelEn))
const activePdfLabel = computed(() => (isArabic.value ? props.pdfLabelAr : props.pdfLabelEn))
const activeTotalLabel = computed(() => (isArabic.value ? props.totalLabelAr : props.totalLabelEn))
const showTotal = computed(() => props.totalValue !== null && props.totalValue !== undefined && props.totalValue !== '')

const handleBack = () => {
  if (props.backRouteName) {
    router.push({ name: props.backRouteName })
    return
  }

  if (window.history.length > 1) {
    router.go(-1)
    return
  }

  if (props.backFallbackRouteName) {
    router.push({ name: props.backFallbackRouteName })
  }
}

const handlePdfDownload = () => {
  emit('download-pdf')
}

const handleRefresh = () => {
  emit('refresh')
}
</script>

<template>
  <section class="generic-dashboard-header" :class="{ 'generic-dashboard-header--rtl': isArabic }">
    <span class="generic-dashboard-header__accent" aria-hidden="true"></span>

    <div class="generic-dashboard-header__icon-panel" aria-hidden="true">
      <div class="generic-dashboard-header__icon-box">
        <v-icon :icon="icon" class="generic-dashboard-header__icon" size="34" />
      </div>
    </div>

    <div class="generic-dashboard-header__content">
      <p class="generic-dashboard-header__kicker">{{ inactiveTitle }}</p>

      <h1 class="generic-dashboard-header__title">{{ activeTitle }}</h1>
      <p v-if="activeSubtitle" class="generic-dashboard-header__subtitle">{{ activeSubtitle }}</p>
    </div>

    <div
      v-if="showBackButton || showRefreshButton || showPdfButton || showTotal"
      class="generic-dashboard-header__actions"
    >
      <div v-if="showTotal" class="generic-dashboard-header__total-pill">
        <span class="generic-dashboard-header__total-label">{{ activeTotalLabel }}</span>
        <strong class="generic-dashboard-header__total-value">{{ totalValue }}</strong>
      </div>

      <v-btn
        v-if="showRefreshButton"
        class="generic-dashboard-header__refresh-btn"
        color="secondary"
        variant="tonal"
        :loading="refreshLoading"
        @click="handleRefresh"
      >
        <v-icon start>{{ refreshIcon }}</v-icon>
        {{ activeRefreshLabel }}
      </v-btn>

      <v-btn
        v-if="showPdfButton"
        class="generic-dashboard-header__download-btn"
        color="error"
        variant="tonal"
        :loading="pdfLoading"
        @click="handlePdfDownload"
      >
        <v-icon start>{{ pdfIcon }}</v-icon>
        {{ activePdfLabel }}
      </v-btn>

      <v-btn
        v-if="showBackButton"
        class="generic-dashboard-header__back-btn"
        color="primary"
        variant="tonal"
        @click="handleBack"
      >
        <v-icon start>{{ leftArrowIcon }}</v-icon>
        {{ activeBackLabel }}
      </v-btn>
    </div>
  </section>
</template>

<style scoped lang="scss">
.generic-dashboard-header {
  align-items: center;
  background: radial-gradient(circle at top left, rgba(0, 135, 85, 0.08), transparent 26%),
  linear-gradient(180deg, #ffffff 0%, #f9fbff 100%);
  border: 1px solid rgba(0, 0, 0, 0.04);
  border-radius: 28px;
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.06);
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  overflow: hidden;
  padding: 24px 20px 24px 28px;
  position: relative;
}

.generic-dashboard-header__icon-panel {
  align-items: center;
  display: flex;
  flex: 0 0 auto;
  justify-content: center;
}

.generic-dashboard-header__icon-box {
  align-items: center;
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 18px;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
  display: flex;
  height: 72px;
  justify-content: center;
  width: 72px;
}

.generic-dashboard-header__icon {
  color: #008755;
  font-size: 34px;
}

.generic-dashboard-header__content {
  flex: 1;
  min-width: 0;
  text-align: left;
}

.generic-dashboard-header__actions {
  align-items: center;
  display: flex;
  gap: 12px;
  inset-inline-end: 20px;
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
}

.generic-dashboard-header__kicker {
  color: #667085;
  font-size: 0.82rem;
  letter-spacing: 0.08em;
  margin: 0 0 6px;
  text-transform: uppercase;
}

.generic-dashboard-header__title {
  color: #1f2937;
  font-family: 'bukrabold', serif;
  font-size: clamp(1.6rem, 2vw, 2.5rem);
  line-height: 1.2;
  margin: 0;
  text-align: start;
}

.generic-dashboard-header__subtitle {
  color: #6b7280;
  font-size: 0.96rem;
  line-height: 1.6;
  margin: 10px 0 0;
  max-width: 820px;
}

.generic-dashboard-header__total-pill {
  align-items: center;
  background: linear-gradient(180deg, rgba(0, 135, 85, 0.08) 0%, rgba(0, 135, 85, 0.14) 100%);
  border: 1px solid rgba(0, 135, 85, 0.12);
  border-radius: 999px;
  color: #0b6b4e;
  display: inline-flex;
  gap: 8px;
  min-height: 38px;
  padding: 6px 12px;
}

.generic-dashboard-header__total-label {
  color: #4d6b61;
  font-size: 0.8rem;
}

.generic-dashboard-header__total-value {
  color: #0d6b4d;
  font-family: 'bukrabold', serif;
}

.generic-dashboard-header__refresh-btn {
  border-radius: 999px;
  flex-shrink: 0;
  font-family: 'bukrabold', serif;
  min-width: 0;
}

.generic-dashboard-header__back-btn {
  border-radius: 999px;
  flex-shrink: 0;
  font-family: 'bukrabold', serif;
  min-width: 0;
}

.generic-dashboard-header__download-btn {
  border-radius: 999px;
  flex-shrink: 0;
  font-family: 'bukrabold', serif;
  min-width: 0;
}

.generic-dashboard-header__accent {
  align-self: stretch;
  background: linear-gradient(180deg, #11a36b 0%, #008755 100%);
  border-radius: 999px;
  flex: 0 0 10px;
  min-height: 96px;
}

.generic-dashboard-header--rtl {
  direction: rtl;
}

.generic-dashboard-header--rtl .generic-dashboard-header__content {
  text-align: right;
}

.generic-dashboard-header--rtl .generic-dashboard-header__kicker {
  letter-spacing: normal;
  text-transform: none;
}

@media (max-width: 768px) {
  .generic-dashboard-header {
    border-radius: 22px;
    gap: 16px;
    padding: 18px;
    padding-bottom: 84px;
  }

  .generic-dashboard-header__icon-box {
    border-radius: 16px;
    height: 60px;
    width: 60px;
  }

  .generic-dashboard-header__icon {
    font-size: 28px;
  }

  .generic-dashboard-header__accent {
    flex-basis: 8px;
  }
}

@media (max-width: 600px) {
  .generic-dashboard-header {
    align-items: flex-start;
    flex-wrap: wrap;
    padding-bottom: 18px;
  }

  .generic-dashboard-header__content {
    order: 3;
    width: calc(100% - 24px);
  }

  .generic-dashboard-header__actions {
    order: 2;
    align-self: flex-end;
    inset-inline-end: auto;
    position: static;
    top: auto;
    transform: none;
    width: 100%;
    justify-content: flex-end;
  }

  .generic-dashboard-header__accent {
    min-height: 72px;
  }
}
</style>
