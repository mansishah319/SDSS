<script setup lang="ts">
import { computed } from 'vue'
import type { ChallengeMetricCard } from './challengeReport.types'

defineProps<{
  items: ChallengeMetricCard[]
}>()

const emit = defineEmits<{
  select: [key: string]
}>()

const toneClasses = computed<Record<ChallengeMetricCard['tone'], string>>(() => ({
  primary: 'challenge-kpi-card--primary',
  success: 'challenge-kpi-card--success',
  warning: 'challenge-kpi-card--warning',
  error: 'challenge-kpi-card--error',
  info: 'challenge-kpi-card--info',
  neutral: 'challenge-kpi-card--neutral'
}))

const handleSelect = (item: ChallengeMetricCard) => {
  if (!item.actionable) {
    return
  }

  emit('select', item.key)
}
</script>

<template>
  <v-row class="challenge-kpi-grid" dense>
    <v-col
      v-for="item in items"
      :key="item.key"
      cols="12"
      sm="6"
      md="4"
      xl="3"
      :class="`challenge-kpi-grid__column--${item.key}`"
    >
      <v-card
        class="challenge-kpi-card"
        :class="[
          toneClasses[item.tone],
          `challenge-kpi-card--${item.key}`,
          { 'challenge-kpi-card--actionable': item.actionable }
        ]"
        :ripple="item.actionable"
        :tabindex="item.actionable ? 0 : -1"
        rounded="xl"
        elevation="0"
        @click="handleSelect(item)"
        @keydown.enter.prevent="handleSelect(item)"
        @keydown.space.prevent="handleSelect(item)"
      >
        <v-card-text class="challenge-kpi-card__body">
          <div class="challenge-kpi-card__icon-wrap">
            <v-icon>{{ item.icon }}</v-icon>
          </div>

          <div class="challenge-kpi-card__content">
            <span class="challenge-kpi-card__label">{{ item.label }}</span>
            <strong class="challenge-kpi-card__value font-bukra-bold">{{ item.value }}</strong>
            <span v-if="item.caption" class="challenge-kpi-card__caption">{{ item.caption }}</span>
          </div>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<style scoped lang="scss">
.challenge-kpi-grid {
  margin: 0 -6px;
}

.challenge-kpi-card {
  height: 100%;
  border-radius: 24px;
  border: 1px solid rgba(13, 82, 62, 0.1);
  background: #fff;
  transition: transform 0.22s ease,
  box-shadow 0.22s ease,
  border-color 0.22s ease;
}

.challenge-kpi-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 32px rgba(9, 39, 31, 0.08);
}

.challenge-kpi-card--actionable {
  cursor: pointer;
}

.challenge-kpi-card--actionable:focus-visible {
  outline: 2px solid #0d6b4d;
  outline-offset: 2px;
}

.challenge-kpi-card__body {
  display: flex;
  gap: 14px;
  align-items: center;
  padding: 18px;
}

.challenge-kpi-card__icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.challenge-kpi-card__content {
  min-width: 0;
  display: grid;
  gap: 4px;
}

.challenge-kpi-card__label,
.challenge-kpi-card__caption {
  color: #6a7a73;
}

.challenge-kpi-card__label {
  font-size: 0.82rem;
}

.challenge-kpi-card__value {
  color: #17362d;
  font-size: 1.35rem;
  line-height: 1.2;
}

.challenge-kpi-card__caption {
  font-size: 0.78rem;
}

.challenge-kpi-card--primary .challenge-kpi-card__icon-wrap {
  background: rgba(21, 112, 239, 0.12);
  color: #1570ef;
}

.challenge-kpi-card--success .challenge-kpi-card__icon-wrap {
  background: rgba(18, 183, 106, 0.12);
  color: #129f63;
}

.challenge-kpi-card--warning .challenge-kpi-card__icon-wrap {
  background: rgba(247, 144, 9, 0.14);
  color: #d97706;
}

.challenge-kpi-card--error .challenge-kpi-card__icon-wrap {
  background: rgba(240, 68, 56, 0.12);
  color: #d9362c;
}

.challenge-kpi-card--info .challenge-kpi-card__icon-wrap {
  background: rgba(14, 165, 233, 0.12);
  color: #0284c7;
}

.challenge-kpi-card--neutral .challenge-kpi-card__icon-wrap {
  background: rgba(15, 23, 42, 0.08);
  color: #334155;
}
</style>
