<template>
  <section class="survey-dashboard-panel" :style="{ '--accent': accent }">
    <header class="survey-dashboard-panel__header">
      <div class="survey-dashboard-panel__title-group">
        <div class="survey-dashboard-panel__icon">
          <v-icon :icon="icon" />
        </div>
        <div>
          <h2 class="survey-dashboard-panel__title">{{ title }}</h2>
          <p v-if="subtitle" class="survey-dashboard-panel__subtitle">{{ subtitle }}</p>
        </div>
      </div>
      <slot name="header-extra" />
    </header>

    <div class="survey-dashboard-panel__body">
      <slot />
    </div>
  </section>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'SurveyDashboardPanel',
  props: {
    title: {
      type: String,
      required: true
    },
    subtitle: {
      type: String,
      default: ''
    },
    icon: {
      type: String,
      default: 'mdi-chart-donut'
    },
    accent: {
      type: String,
      default: '#0b6e72'
    }
  }
})
</script>

<style scoped lang="scss">
.survey-dashboard-panel {
  padding: 1.35rem;
  border-radius: 28px;
  background: #fff;
  border: 1px solid rgba(255, 255, 255, 0.55);
  box-shadow: 0 22px 56px rgba(15, 23, 42, 0.08);
}

.survey-dashboard-panel__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 2.05rem;
}

.survey-dashboard-panel__title-group {
  display: flex;
  align-items: center;
  gap: 0.9rem;
}

.survey-dashboard-panel__icon {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: color-mix(in srgb, var(--accent) 12%, #ffffff);
  color: var(--accent);
}

.survey-dashboard-panel__title,
.survey-dashboard-panel__subtitle {
  margin: 0;
}

.survey-dashboard-panel__title {
  color: #101828;
  font-size: 1.04rem;
  font-weight: 800;
}

.survey-dashboard-panel__subtitle {
  margin-top: 0.25rem;
  color: #667085;
  font-size: 0.88rem;
  line-height: 1.5;
}
</style>
