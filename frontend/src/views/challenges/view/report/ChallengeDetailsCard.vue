<script setup lang="ts">
import type {
  ChallengeDetailsGroupViewModel,
  ChallengeHierarchyItemViewModel
} from './challengeReport.types'

defineProps<{
  title: string
  subtitle: string
  hierarchyTitle: string
  hierarchy: ChallengeHierarchyItemViewModel[]
  groups: ChallengeDetailsGroupViewModel[]
}>()
</script>

<template>
  <v-card class="challenge-details-card" rounded="xl" elevation="0">
    <v-card-text class="challenge-details-card__body">
      <header class="challenge-details-card__header">
        <p class="challenge-details-card__eyebrow">{{ subtitle }}</p>
        <h3 class="challenge-details-card__title font-bukra-bold">{{ title }}</h3>
      </header>

      <section class="challenge-details-card__hierarchy">
        <div class="challenge-details-card__section-heading">
          <v-icon size="22">mdi-sitemap-outline</v-icon>
          <h4 class="font-bukra-bold">{{ hierarchyTitle }}</h4>
        </div>

        <div class="challenge-hierarchy">
          <article v-for="(item, index) in hierarchy" :key="item.key" class="challenge-hierarchy__item">
            <div class="challenge-hierarchy__icon">
              <v-icon size="20">{{ item.icon }}</v-icon>
            </div>
            <div class="challenge-hierarchy__content">
              <span>{{ item.label }}</span>
              <strong class="font-dubai-bold">{{ item.value }}</strong>
            </div>
            <v-icon v-if="index < hierarchy.length - 1" class="challenge-hierarchy__arrow" size="20">
              mdi-chevron-left
            </v-icon>
          </article>
        </div>
      </section>

      <section
        v-for="group in groups"
        :key="group.key"
        class="challenge-details-card__group"
        :class="`challenge-details-card__group--${group.key}`"
      >
        <div class="challenge-details-card__group-header">
          <div class="challenge-details-card__group-icon">
            <v-icon>{{ group.icon }}</v-icon>
          </div>
          <div>
            <h4 class="font-bukra-bold">{{ group.title }}</h4>
            <p>{{ group.subtitle }}</p>
          </div>
        </div>

        <div class="challenge-details-card__grid">
          <article v-for="item in group.items" :key="item.key" class="challenge-detail-item">
            <v-icon class="challenge-detail-item__icon" size="20">{{ item.icon }}</v-icon>
            <div>
              <span class="challenge-detail-item__label">{{ item.label }}</span>
              <strong class="challenge-detail-item__value font-dubai-bold">{{ item.value }}</strong>
            </div>
          </article>
        </div>
      </section>
    </v-card-text>
  </v-card>
</template>

<style scoped lang="scss">
.challenge-details-card {
  border: 1px solid rgba(13, 82, 62, 0.1);
  background: linear-gradient(180deg, #ffffff 0%, #f8fcfa 100%);
  margin-bottom: 20px;
}

.challenge-details-card__body {
  display: grid;
  gap: 22px;
  padding: 24px;
}

.challenge-details-card__eyebrow,
.challenge-details-card__group-header p,
.challenge-detail-item__label,
.challenge-hierarchy__content span {
  color: #73837d;
}

.challenge-details-card__eyebrow,
.challenge-details-card__group-header p {
  margin: 0;
  font-size: 0.82rem;
}

.challenge-details-card__title {
  margin: 8px 0 0;
  color: #16362d;
}

.challenge-details-card__hierarchy,
.challenge-details-card__group {
  border: 1px solid rgba(13, 82, 62, 0.08);
  border-radius: 22px;
  background: #fff;
  padding: 18px;
}

.challenge-details-card__section-heading,
.challenge-details-card__group-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.challenge-details-card__section-heading {
  color: #0d6b4d;
  margin-bottom: 16px;
}

.challenge-details-card__section-heading h4,
.challenge-details-card__group-header h4 {
  margin: 0;
}

.challenge-details-card__group-header {
  align-items: flex-start;
  margin-bottom: 16px;
}

.challenge-details-card__group-icon,
.challenge-hierarchy__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #0d6b4d;
  background: rgba(13, 107, 77, 0.09);
}

.challenge-details-card__group-icon {
  width: 42px;
  height: 42px;
  border-radius: 14px;
}

.challenge-hierarchy {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.challenge-hierarchy__item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  padding: 14px;
  border-radius: 18px;
  background: #f5faf7;
}

.challenge-hierarchy__icon {
  width: 38px;
  height: 38px;
  border-radius: 12px;
}

.challenge-hierarchy__content {
  min-width: 0;
}

.challenge-hierarchy__content span,
.challenge-hierarchy__content strong {
  display: block;
}

.challenge-hierarchy__content span {
  font-size: 0.76rem;
}

.challenge-hierarchy__content strong {
  margin-top: 4px;
  color: #17362d;
  overflow-wrap: anywhere;
}

.challenge-hierarchy__arrow {
  position: absolute;
  inset-inline-end: -15px;
  z-index: 1;
  color: #8aa399;
  background: #fff;
  border-radius: 50%;
}

.challenge-details-card__grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.challenge-detail-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  min-width: 0;
  padding: 14px;
  border: 1px solid rgba(13, 82, 62, 0.06);
  border-radius: 16px;
  background: #fbfdfc;
}

.challenge-detail-item__icon {
  color: #0d6b4d;
  flex-shrink: 0;
}

.challenge-detail-item__label,
.challenge-detail-item__value {
  display: block;
}

.challenge-detail-item__label {
  font-size: 0.76rem;
}

.challenge-detail-item__value {
  margin-top: 5px;
  color: #17362d;
  overflow-wrap: anywhere;
}

.challenge-details-card__group--classification {
  background: linear-gradient(135deg, rgba(13, 107, 77, 0.04), #fff 54%);
}

@media (max-width: 1100px) {
  .challenge-hierarchy,
  .challenge-details-card__grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 600px) {
  .challenge-details-card__body {
    padding: 18px;
  }

  .challenge-hierarchy,
  .challenge-details-card__grid {
    grid-template-columns: 1fr;
  }

  .challenge-hierarchy__arrow {
    display: none;
  }
}
</style>
