<template>
  <section class="survey-dashboard-list" :style="{ '--tone': tone }">
    <header class="survey-dashboard-list__header">
      <div>
        <p class="survey-dashboard-list__eyebrow">{{ subtitle }}</p>
        <h3 class="survey-dashboard-list__title">{{ title }}</h3>
      </div>
      <div class="survey-dashboard-list__count">
        <strong>{{ count }}</strong>
      </div>
    </header>

    <div v-if="surveys.length" class="survey-dashboard-list__items">
      <article v-for="survey in surveys" :key="survey.surveyId" class="survey-dashboard-list__item">
        <div class="survey-dashboard-list__item-top">
          <div class="survey-dashboard-list__item-copy">
            <h4>{{ surveyName(survey) }}</h4>
            <p>{{ surveyDescription(survey) }}</p>
          </div>
          <v-chip size="small" :color="tone" variant="flat" class="dp-chip">{{ survey.status }}</v-chip>
        </div>

        <div class="survey-dashboard-list__item-meta">
          <span>
            <v-icon size="16" icon="mdi-layers-outline" />
            {{ roundsLabel }} {{ survey.activeRounds }}/{{ survey.totalRounds }}
          </span>
          <span>
            <v-icon size="16" :icon="survey.hasAnswered ? 'mdi-check-decagram' : 'mdi-clock-outline'" />
            {{ survey.hasAnswered ? answeredLabel : waitingLabel }}
          </span>
        </div>
      </article>
    </div>

    <div v-else class="survey-dashboard-list__empty">
      <video autoplay muted loop playsinline preload="metadata" type="video/mp4" style="width: 200px">
        <source :src="emptyStateVideoUrl" type="video/mp4" />
      </video>
      <p class="mt-n8">{{ emptyMessage }}</p>
    </div>
  </section>
</template>

<script lang="ts">
import { computed, defineComponent, PropType } from 'vue'
import type { SurveyUserActiveResponse } from '@/dto/survey/SurveyUserDashboardResponse.ts'

export default defineComponent({
  name: 'SurveyDashboardSurveyList',
  props: {
    title: {
      type: String,
      required: true
    },
    subtitle: {
      type: String,
      default: ''
    },
    surveys: {
      type: Array as PropType<SurveyUserActiveResponse[]>,
      default: () => []
    },
    count: {
      type: Number,
      default: 0
    },
    tone: {
      type: String,
      default: '#0b6e72'
    },
    isArabic: {
      type: Boolean,
      default: false
    },
    emptyMessage: {
      type: String,
      default: ''
    }
  },
  setup(props) {
    const emptyStateVideoUrl = new URL('../../../../assets/images/No-record.mp4', import.meta.url).href
    const roundsLabel = computed(() => (props.isArabic ? 'الجولات' : 'Rounds'))
    const answeredLabel = computed(() => (props.isArabic ? 'تمت المشاركة' : 'Answered'))
    const waitingLabel = computed(() => (props.isArabic ? 'بانتظار المشاركة' : 'Awaiting response'))

    const surveyName = (survey: SurveyUserActiveResponse) =>
      props.isArabic ? survey.surveyNameAr || survey.surveyNameEn : survey.surveyNameEn || survey.surveyNameAr

    const surveyDescription = (survey: SurveyUserActiveResponse) =>
      props.isArabic ? survey.descriptionAr || survey.descriptionEn : survey.descriptionEn || survey.descriptionAr

    return {
      roundsLabel,
      answeredLabel,
      waitingLabel,
      emptyStateVideoUrl,
      surveyName,
      surveyDescription
    }
  }
})
</script>

<style scoped lang="scss">
.survey-dashboard-list {
  height: 100%;
  padding: 1.25rem;
  border-radius: 26px;
  border: 1px solid color-mix(in srgb, var(--tone) 14%, #d9e2ec);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.98) 100%),
  linear-gradient(135deg, color-mix(in srgb, var(--tone) 8%, transparent), transparent);
  box-shadow: 0 22px 56px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.survey-dashboard-list__header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.survey-dashboard-list__eyebrow,
.survey-dashboard-list__title,
.survey-dashboard-list__item-copy h4,
.survey-dashboard-list__item-copy p,
.survey-dashboard-list__empty p {
  margin: 0;
}

.survey-dashboard-list__eyebrow {
  color: var(--tone);
  font-size: 0.78rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.survey-dashboard-list__title {
  margin-top: 0.2rem;
  color: #101828;
  font-size: 1.05rem;
  font-weight: 800;
}

.survey-dashboard-list__count {
  min-width: 72px;
  min-height: 72px;
  border-radius: 22px;
  display: grid;
  place-items: center;
  background: color-mix(in srgb, var(--tone) 12%, #ffffff);
  color: var(--tone);

  strong {
    font-size: 1.45rem;
    font-weight: 800;
  }
}

.survey-dashboard-list__items {
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
  min-height: 340px;
  overflow: auto;
  padding: 0 15px;
  max-height: 500px;
}

.survey-dashboard-list__item {
  padding: 1rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(15, 23, 42, 0.05);
}

.survey-dashboard-list__item-top {
  display: flex;
  justify-content: space-between;
  gap: 0.85rem;
  margin-bottom: 0.75rem;
}

.survey-dashboard-list__item-copy {
  min-width: 0;

  h4 {
    color: #101828;
    font-size: 0.98rem;
    font-weight: 800;
  }

  p {
    margin-top: 0.35rem;
    color: #667085;
    font-size: 0.84rem;
    line-height: 1.55;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.survey-dashboard-list__item-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem 1rem;
  color: #475467;
  font-size: 0.8rem;

  span {
    display: inline-flex;
    align-items: center;
    gap: 0.35rem;
  }
}

.survey-dashboard-list__empty {
  flex: 1;
  min-height: 180px;
  display: grid;
  place-items: center;
  text-align: center;
  gap: 0.75rem;
  color: #667085;
}

.dp-chip {
  min-width: 60px;
}
</style>
