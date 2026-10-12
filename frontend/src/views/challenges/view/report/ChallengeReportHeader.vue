<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import type { ChallengeHeaderViewModel } from './challengeReport.types'

const props = defineProps<{
  header: ChallengeHeaderViewModel
}>()

const { locale } = useI18n()

const labels = computed(() => {
  const isArabic = locale.value.startsWith('ar')

  return isArabic
    ? {
      final: 'نهائي',
      immediateAccess: 'وصول فوري',
      currentStage: 'الجولة الحالية',
      lifecycle: 'التقدم',
      status: 'الحالة',
      sector: 'القطاع',
      department: 'الإدارة العامة'
    }
    : {
      final: 'Final',
      immediateAccess: 'Immediate access',
      currentStage: 'Current Round',
      lifecycle: 'Lifecycle',
      status: 'Status',
      sector: 'Sector',
      department: 'Department'
    }
})

const stageProgressLabel = computed(
  () => `${props.header.currentStage}/${props.header.totalStages || props.header.currentStage || 1}`
)

type HeaderChip = {
  key: string
  label: string
  color: string
  variant: 'flat' | 'outlined' | 'tonal'
}

const chips = computed<HeaderChip[]>(() => [
  {
    key: 'status',
    label: props.header.status,
    color: props.header.statusTone,
    variant: 'flat'
  },
  {
    key: 'visibility',
    label: props.header.visibility,
    color: 'surface',
    variant: 'outlined'
  },
  {
    key: 'survey',
    label: props.header.surveyStatus,
    color: 'info',
    variant: 'tonal'
  },
  ...(props.header.isFinal
    ? [
      {
        key: 'final',
        label: labels.value.final,
        color: 'success',
        variant: 'tonal'
      }
    ]
    : []),
  ...(props.header.immediateAccess
    ? [
      {
        key: 'access',
        label: labels.value.immediateAccess,
        color: 'primary',
        variant: 'tonal'
      }
    ]
    : [])
])
</script>

<template>
  <v-card class="challenge-report-header" rounded="xl" elevation="0">
    <div class="challenge-report-header__mesh" />

    <v-card-text class="challenge-report-header__content">
      <div class="challenge-report-header__main">
        <div class="challenge-report-header__eyebrow">{{ header.eyebrow }}</div>

        <div class="challenge-report-header__chips">
          <v-chip
            v-for="chip in chips"
            :key="chip.key"
            :color="chip.color"
            :variant="chip.variant"
            size="small"
            class="challenge-report-header__chip"
          >
            {{ chip.label }}
          </v-chip>
        </div>

        <div>
          <h1 class="challenge-report-header__title font-bukra-bold">{{ header.title }}</h1>
          <p
            v-if="header.secondaryTitle && header.secondaryTitle !== header.title"
            class="challenge-report-header__secondary font-dubai-bold"
          >
            {{ header.secondaryTitle }}
          </p>
          <p class="challenge-report-header__description font-dubai-regular">
            {{ header.description }}
          </p>

          <div class="challenge-report-header__organization">
            <div class="challenge-report-header__organization-item">
              <span class="challenge-report-header__organization-icon">
                <v-icon size="20">mdi-domain</v-icon>
              </span>
              <span class="challenge-report-header__organization-copy">
                <small>{{ labels.sector }}</small>
                <strong class="font-dubai-bold">{{ header.sector }}</strong>
              </span>
            </div>

            <v-icon class="challenge-report-header__organization-arrow" size="18">
              {{ locale.startsWith('ar') ? 'mdi-chevron-left' : 'mdi-chevron-right' }}
            </v-icon>

            <div class="challenge-report-header__organization-item">
              <span class="challenge-report-header__organization-icon">
                <v-icon size="20">mdi-office-building-outline</v-icon>
              </span>
              <span class="challenge-report-header__organization-copy">
                <small>{{ labels.department }}</small>
                <strong class="font-dubai-bold">{{ header.department }}</strong>
              </span>
            </div>
          </div>
        </div>

      </div>

      <aside class="challenge-report-header__side">
        <div class="challenge-report-header__stage-pill">
          <span class="challenge-report-header__stage-label">{{ labels.currentStage }}</span>
          <strong class="challenge-report-header__stage-value font-bukra-bold">{{ stageProgressLabel }}</strong>
        </div>

        <v-progress-linear
          :model-value="header.progressPercent"
          color="white"
          bg-color="rgba(255, 255, 255, 0.18)"
          rounded
          height="10"
        />

        <div class="challenge-report-header__legend">
          <div>
            <span class="challenge-report-header__legend-label">{{ labels.lifecycle }}</span>
            <strong class="challenge-report-header__legend-value">{{ Math.round(header.progressPercent) }}%</strong>
          </div>
          <div>
            <span class="challenge-report-header__legend-label">{{ labels.status }}</span>
            <strong class="challenge-report-header__legend-value">{{ header.status }}</strong>
          </div>
        </div>
      </aside>
    </v-card-text>
  </v-card>
</template>

<style  lang="scss">
.challenge-report-header {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(13, 85, 64, 0.12);
  background: radial-gradient(circle at top right, rgba(255, 255, 255, 0.18), transparent 30%),
  linear-gradient(135deg, #0b6b4e 0%, #14805e 48%, #0d5c45 100%);
  color: #fff;
}

.challenge-report-header__mesh {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(rgba(255, 255, 255, 0.06) 1px, transparent 1px),
  linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 28px 28px;
  opacity: 0.45;
  pointer-events: none;
}

.challenge-report-header__content {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 2.1fr) minmax(280px, 0.9fr);
  gap: 24px;
  padding: 28px;
}

.challenge-report-header__main {
  display: grid;
  gap: 18px;
}

.challenge-report-header__eyebrow,
.challenge-report-header__meta-label,
.challenge-report-header__stage-label,
.challenge-report-header__legend-label {
  font-size: 0.76rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.78);
}

.challenge-report-header__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.challenge-report-header__chip {
  font-family: bukraregular, sans-serif;
  border: 1px solid #fff;
  .v-chip__content{
    color: #fff !important;
  }
}

.challenge-report-header__title {
  margin: 0;
  font-size: clamp(1.9rem, 3vw, 2.8rem);
  line-height: 1.1;
  color: #fff;
  display: flex;

}

.challenge-report-header__secondary {
  margin: 10px 0 0;
  font-size: 1rem;
  opacity: 0.94;
}

.challenge-report-header__description {
  margin: 12px 0 0;
  max-width: 72ch;
  font-size: 0.98rem;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.92);
}

.challenge-report-header__organization {
  display: flex;
  align-items: stretch;
  gap: 10px;
  margin-top: 18px;
}

.challenge-report-header__organization-item {
  display: flex;
  align-items: center;
  min-width: 0;
  flex: 1 1 0;
  gap: 11px;
  padding: 11px 14px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.11);
}

.challenge-report-header__organization-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 36px;
  width: 36px;
  height: 36px;
  border-radius: 11px;
  background: rgba(255, 255, 255, 0.16);
  color: #fff;
}

.challenge-report-header__organization-copy {
  display: grid;
  min-width: 0;
  gap: 2px;
}

.challenge-report-header__organization-copy small {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.72rem;
}

.challenge-report-header__organization-copy strong {
  overflow-wrap: anywhere;
  color: #fff;
  font-size: 0.92rem;
  line-height: 1.35;
}

.challenge-report-header__organization-arrow {
  align-self: center;
  flex: 0 0 auto;
  color: rgba(255, 255, 255, 0.58);
}

.challenge-report-header__meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.challenge-report-header__meta-card,
.challenge-report-header__side {
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 24px;
}

.challenge-report-header__meta-card {
  padding: 16px 18px;
}

.challenge-report-header__meta-value,
.challenge-report-header__stage-value,
.challenge-report-header__legend-value {
  display: block;
  margin-top: 6px;
  color: #fff;
}

.challenge-report-header__meta-note {
  display: block;
  margin-top: 6px;
  font-size: 0.82rem;
  color: rgba(255, 255, 255, 0.82);
  overflow-wrap: anywhere;
}

.challenge-report-header__side {
  display: grid;
  align-content: start;
  gap: 18px;
  padding: 22px;
}

.challenge-report-header__stage-pill {
  display: grid;
  gap: 6px;
}

.challenge-report-header__stage-value {
  font-size: 2rem;
}

.challenge-report-header__legend {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

@media (max-width: 960px) {
  .challenge-report-header__content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .challenge-report-header__content {
    padding: 20px;
  }

  .challenge-report-header__meta-grid,
  .challenge-report-header__legend {
    grid-template-columns: 1fr;
  }

  .challenge-report-header__organization {
    display: grid;
  }

  .challenge-report-header__organization-arrow {
    display: none;
  }
}
</style>
