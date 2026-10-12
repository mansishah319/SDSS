<template>
  <article class="survey-dashboard-kpi-card" :style="{ '--accent': accent }">
    <div class="survey-dashboard-kpi-card__head">
      <div class="survey-dashboard-kpi-card__icon">
        <v-icon :icon="icon" />
      </div>
      <span v-if="badge" class="survey-dashboard-kpi-card__badge">{{ badge }}</span>
    </div>

    <p class="survey-dashboard-kpi-card__title">{{ title }}</p>
    <div class="survey-dashboard-kpi-card__value-row">
      <strong class="survey-dashboard-kpi-card__value">{{ value }}</strong>
      <span v-if="suffix" class="survey-dashboard-kpi-card__suffix">{{ suffix }}</span>
    </div>
    <p v-if="subtitle" class="survey-dashboard-kpi-card__subtitle">{{ subtitle }}</p>

    <div v-if="typeof progress === 'number'" class="survey-dashboard-kpi-card__track" aria-hidden="true">
      <span
        class="survey-dashboard-kpi-card__fill"
        :style="{ width: `${Math.max(0, Math.min(progress, 100))}%` }"
      ></span>
    </div>
  </article>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'SurveyDashboardKpiCard',
  props: {
    title: {
      type: String,
      required: true
    },
    value: {
      type: String,
      required: true
    },
    subtitle: {
      type: String,
      default: ''
    },
    icon: {
      type: String,
      default: 'mdi-chart-box-outline'
    },
    accent: {
      type: String,
      default: '#0b6e72'
    },
    badge: {
      type: String,
      default: ''
    },
    suffix: {
      type: String,
      default: ''
    },
    progress: {
      type: Number,
      default: null
    }
  }
})
</script>

<style scoped lang="scss">
.survey-dashboard-kpi-card {
  position: relative;
  overflow: hidden;
  min-height: 196px;
  padding: 1.2rem;
  border-radius: 24px;
  border: 1px solid color-mix(in srgb, var(--accent) 16%, #d9e2ec);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(246, 250, 251, 0.98) 100%),
  linear-gradient(135deg, color-mix(in srgb, var(--accent) 8%, transparent), transparent);
  box-shadow: 0 18px 48px rgba(16, 24, 40, 0.08);
  display: flex;
  flex-direction: column;
  gap: 0.85rem;

  &::after {
    content: '';
    position: absolute;
    inset-inline-end: -42px;
    top: -42px;
    width: 132px;
    height: 132px;
    border-radius: 50%;
    background: radial-gradient(circle, color-mix(in srgb, var(--accent) 16%, transparent), transparent 72%);
    pointer-events: none;
  }
}

.survey-dashboard-kpi-card__head {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
}

.survey-dashboard-kpi-card__icon {
  width: 50px;
  height: 50px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  color: #ffffff;
  background: linear-gradient(135deg, var(--accent), color-mix(in srgb, var(--accent) 72%, #ffffff));
}

.survey-dashboard-kpi-card__badge {
  display: inline-flex;
  align-items: center;
  height: 36px;
  padding: 0.3rem 0.7rem;
  border-radius: 999px;
  background: color-mix(in srgb, var(--accent) 10%, #ffffff);
  color: var(--accent);
  font-size: 0.75rem;
  font-weight: 700;
  font-family: bukraregular;
}

.survey-dashboard-kpi-card__title,
.survey-dashboard-kpi-card__subtitle {
  margin: 0;
}

.survey-dashboard-kpi-card__title {
  color: #475467;
  font-size: 0.95rem;
}

.survey-dashboard-kpi-card__value-row {
  display: flex;
  align-items: baseline;
  gap: 0.35rem;
}

.survey-dashboard-kpi-card__value {
  color: #101828;
  font-size: clamp(1.8rem, 3vw, 2.35rem);
  line-height: 1;
  font-weight: 800;
}

.survey-dashboard-kpi-card__suffix {
  color: #667085;
  font-size: 0.88rem;
  font-weight: 700;
}

.survey-dashboard-kpi-card__subtitle {
  color: #667085;
  font-size: 0.84rem;
  line-height: 1.55;
}

.survey-dashboard-kpi-card__track {
  margin-top: auto;
  height: 8px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.06);
  overflow: hidden;
}

.survey-dashboard-kpi-card__fill {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, var(--accent), color-mix(in srgb, var(--accent) 65%, #ffffff));
}
</style>
