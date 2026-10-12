<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import type { ChallengeStageViewModel } from './challengeReport.types'

const props = defineProps<{
  stages: ChallengeStageViewModel[]
  modelValue?: string[]
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string[]]
}>()

const { locale } = useI18n()

const activePanels = computed({
  get: () => props.modelValue || [],
  set: (value: string[]) => emit('update:modelValue', value)
})

const labels = computed(() => {
  const isArabic = locale.value.startsWith('ar')

  return isArabic
    ? {
      title: 'تفاصيل الجولات',
      subtitle: 'الحلول والتصويت داخل كل جولة',
      current: 'الجولة الحالية',
      winner: 'الحل المتصدر',
      votes: 'الأصوات',
      solutions: 'الحلول',
      queue: 'مستوى الجولة',
      final: 'نهائي',
      winning: 'فائز',
      share: 'نسبة التصويت',
      empty: 'لا توجد جولات متاحة لهذا التحدي.',
      solutionId: 'رقم الحل',
      noStageSolutions: 'لا توجد حلول أو بيانات تصويت لهذه الجولة.',
      answerEntryNote: 'قد تكون هذه الجولة مخصصة لإدخال الإجابات، لذلك لا تظهر بيانات التصويت بعد.'
    }
    : {
      title: 'Round Breakdown',
      subtitle: 'Solutions and voting within each Round',
      current: 'Current Round',
      winner: 'Leading solution',
      votes: 'Votes',
      solutions: 'Solutions',
      queue: 'Queue level',
      final: 'Final',
      winning: 'Winner',
      share: 'Vote share',
      empty: 'No Round data is available for this challenge.',
      solutionId: 'Solution ID',
      noStageSolutions: 'No solutions or voting data is available for this Round.',
      answerEntryNote: 'This may be an answer-entry Round, so voting data may not appear yet.'
    }
})
</script>

<template>
  <v-card class="challenge-stage-card" rounded="xl" elevation="0">
    <v-card-text class="challenge-stage-card__body">
      <div class="challenge-stage-card__header">
        <div>
          <p class="challenge-stage-card__eyebrow">{{ labels.subtitle }}</p>
          <h3 class="challenge-stage-card__title font-bukra-bold">{{ labels.title }}</h3>
        </div>
      </div>

      <div v-if="!stages.length" class="challenge-stage-card__empty">
        {{ labels.empty }}
      </div>

      <div v-if="stages.length" class="challenge-stage-card__pdf-panels" aria-hidden="true">
        <section v-for="stage in stages" :key="stage.id" class="challenge-stage-card__pdf-panel">
          <div class="challenge-stage-card__panel-title-row">
            <div>
              <div class="challenge-stage-card__stage-title font-bukra-bold">{{ stage.title }}</div>
              <div class="challenge-stage-card__stage-subtitle">{{ stage.subtitle }}</div>
            </div>

            <div class="challenge-stage-card__panel-chips">
              <v-chip v-if="stage.isCurrent" color="info" variant="tonal" size="small">{{ labels.current }}</v-chip>
              <v-chip color="primary" variant="tonal" size="small">{{ stage.solutionCount }} {{ labels.solutions }}
              </v-chip>
              <v-chip color="success" variant="tonal" size="small">{{ stage.totalVotes }} {{ labels.votes }}</v-chip>
            </div>
          </div>

          <div class="challenge-stage-card__insight-row">
            <v-alert v-if="stage.tieLabel" type="warning" variant="tonal" density="comfortable">
              {{ stage.tieLabel }}
            </v-alert>
            <v-alert v-else-if="stage.winnerLabel" type="success" variant="tonal" density="comfortable">
              {{ labels.winner }}: {{ stage.winnerLabel }}
            </v-alert>
          </div>

          <div v-if="!stage.solutions.length" class="challenge-stage-card__stage-empty">
            <div class="challenge-stage-card__stage-empty-table" role="table">
              <div class="challenge-stage-card__stage-empty-row challenge-stage-card__stage-empty-row--header" role="row">
                <span role="columnheader">{{ labels.solutionId }}</span>
                <span role="columnheader">{{ labels.solutions }}</span>
                <span role="columnheader">{{ labels.votes }}</span>
                <span role="columnheader">{{ labels.share }}</span>
              </div>
              <div class="challenge-stage-card__stage-empty-row" role="row">
                <span role="cell" class="challenge-stage-card__stage-empty-cell">
                  {{ labels.noStageSolutions }}
                </span>
              </div>
            </div>
            <v-alert type="info" variant="tonal" density="comfortable" class="challenge-stage-card__stage-empty-note">
              {{ labels.answerEntryNote }}
            </v-alert>
          </div>

          <div v-else class="challenge-stage-card__solutions-grid">
            <article v-for="solution in stage.solutions" :key="solution.id" class="challenge-stage-card__solution">
              <div class="challenge-stage-card__solution-head">
                <div class="challenge-stage-card__solution-id">#{{ solution.id }}</div>
                <div class="challenge-stage-card__solution-chips">
                  <v-chip v-if="solution.isWinner" color="success" size="x-small" variant="tonal">
                    {{ labels.winning }}
                  </v-chip>
                  <v-chip v-if="solution.isFinal" color="primary" size="x-small" variant="outlined">
                    {{ labels.final }}
                  </v-chip>
                </div>
              </div>

              <p class="challenge-stage-card__solution-text font-dubai-regular">{{ solution.text }}</p>

              <div class="challenge-stage-card__solution-stats">
                <div>
                  <span class="challenge-stage-card__stat-label">{{ labels.votes }}</span>
                  <strong class="challenge-stage-card__stat-value">{{ solution.voteCount }}</strong>
                </div>
                <div>
                  <span class="challenge-stage-card__stat-label">{{ labels.queue }}</span>
                  <strong class="challenge-stage-card__stat-value">{{ solution.queueLevel }}</strong>
                </div>
                <div>
                  <span class="challenge-stage-card__stat-label">Share</span>
                  <strong class="challenge-stage-card__stat-value">{{ solution.voteShare.toFixed(0) }}%</strong>
                </div>
              </div>

              <v-progress-linear
                :model-value="solution.voteShare"
                :color="solution.isWinner ? 'success' : 'primary'"
                bg-color="rgba(12, 58, 44, 0.08)"
                height="8"
                rounded
              />
              <div class="challenge-stage-card__share-label">{{ labels.share }}: {{ solution.voteShare.toFixed(0) }}%
              </div>
            </article>
          </div>
        </section>
      </div>

      <v-expansion-panels
        v-if="stages.length"
        v-model="activePanels"
        variant="accordion"
        multiple
        class="challenge-stage-card__panels"
      >
        <v-expansion-panel
          v-for="stage in stages"
          :key="stage.id"
          :value="stage.id"
          :title="stage.title"
          :text="stage.subtitle"
          rounded="xl"
          elevation="0"
          class="challenge-stage-card__panel"
          eager
        >
          <template #title>
            <div class="challenge-stage-card__panel-title-row">
              <div>
                <div class="challenge-stage-card__stage-title font-bukra-bold">{{ stage.title }}</div>
                <div class="challenge-stage-card__stage-subtitle">{{ stage.subtitle }}</div>
              </div>

              <div class="challenge-stage-card__panel-chips">
                <v-chip v-if="stage.isCurrent" color="info" variant="tonal" size="small">{{ labels.current }}</v-chip>
                <v-chip color="primary" variant="tonal" size="small"
                >{{ stage.solutionCount }} {{ labels.solutions }}
                </v-chip
                >
                <v-chip color="success" variant="tonal" size="small">{{ stage.totalVotes }} {{ labels.votes }}</v-chip>
              </div>
            </div>
          </template>

          <template #text>
            <div class="challenge-stage-card__insight-row">
              <v-alert v-if="stage.tieLabel" type="warning" variant="tonal" density="comfortable">
                {{ stage.tieLabel }}
              </v-alert>
              <v-alert v-else-if="stage.winnerLabel" type="success" variant="tonal" density="comfortable">
                {{ labels.winner }}: {{ stage.winnerLabel }}
              </v-alert>
            </div>

            <div v-if="!stage.solutions.length" class="challenge-stage-card__stage-empty">
              <div class="challenge-stage-card__stage-empty-table" role="table">
                <div class="challenge-stage-card__stage-empty-row challenge-stage-card__stage-empty-row--header" role="row">
                  <span role="columnheader">{{ labels.solutionId }}</span>
                  <span role="columnheader">{{ labels.solutions }}</span>
                  <span role="columnheader">{{ labels.votes }}</span>
                  <span role="columnheader">{{ labels.share }}</span>
                </div>
                <div class="challenge-stage-card__stage-empty-row" role="row">
                  <span role="cell" class="challenge-stage-card__stage-empty-cell">
                    {{ labels.noStageSolutions }}
                  </span>
                </div>
              </div>
              <v-alert type="info" variant="tonal" density="comfortable" class="challenge-stage-card__stage-empty-note">
                {{ labels.answerEntryNote }}
              </v-alert>
            </div>

            <div v-else class="challenge-stage-card__solutions-grid">
              <article v-for="solution in stage.solutions" :key="solution.id" class="challenge-stage-card__solution">
                <div class="challenge-stage-card__solution-head">
                  <div class="challenge-stage-card__solution-id">#{{ solution.id }}</div>
                  <div class="challenge-stage-card__solution-chips">
                    <v-chip v-if="solution.isWinner" color="success" size="x-small" variant="tonal">
                      {{ labels.winning }}
                    </v-chip>
                    <v-chip v-if="solution.isFinal" color="primary" size="x-small" variant="outlined">
                      {{ labels.final }}
                    </v-chip>
                  </div>
                </div>

                <p class="challenge-stage-card__solution-text font-dubai-regular">{{ solution.text }}</p>

                <div class="challenge-stage-card__solution-stats">
                  <div>
                    <span class="challenge-stage-card__stat-label">{{ labels.votes }}</span>
                    <strong class="challenge-stage-card__stat-value">{{ solution.voteCount }}</strong>
                  </div>
                  <div>
                    <span class="challenge-stage-card__stat-label">{{ labels.queue }}</span>
                    <strong class="challenge-stage-card__stat-value">{{ solution.queueLevel }}</strong>
                  </div>
                  <div>
                    <span class="challenge-stage-card__stat-label">{{ labels.share }}</span>
                    <strong class="challenge-stage-card__stat-value">{{ solution.voteShare.toFixed(0) }}%</strong>
                  </div>
                </div>

                <v-progress-linear
                  :model-value="solution.voteShare"
                  :color="solution.isWinner ? 'success' : 'primary'"
                  bg-color="rgba(12, 58, 44, 0.08)"
                  height="8"
                  rounded
                />
                <div class="challenge-stage-card__share-label"
                >{{ labels.share }}: {{ solution.voteShare.toFixed(0) }}%
                </div
                >
              </article>
            </div>
          </template>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-card-text>
  </v-card>
</template>

<style scoped lang="scss">
.challenge-stage-card {
  border: 1px solid rgba(13, 82, 62, 0.1);
  background: #fff;
}

.challenge-stage-card__body {
  padding: 22px;
}

.challenge-stage-card__eyebrow {
  margin: 0;
  color: #7a8b84;
  font-size: 0.8rem;
}

.challenge-stage-card__title {
  margin: 8px 0 0;
  color: #16362d;
}

.challenge-stage-card__empty {
  margin-top: 18px;
  padding: 20px;
  border-radius: 20px;
  background: #f8fbfa;
  color: #62746d;
  text-align: center;
}

.challenge-stage-card__panels {
  margin-top: 18px;
  display: flex;
  gap: 14px;
}

.challenge-stage-card__pdf-panels {
  display: none;
}

.challenge-stage-card__panel {
  border: 1px solid rgba(13, 82, 62, 0.08);
  border-radius: 22px;
  overflow: hidden;
}

.challenge-stage-card__panel-title-row {
  width: 100%;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.challenge-stage-card__stage-title {
  color: #16362d;
  margin-bottom: 10%;
}

.challenge-stage-card__stage-subtitle,
.challenge-stage-card__stat-label {
  color: #6e7f78;
  font-size: 0.82rem;
}

.challenge-stage-card__panel-chips,
.challenge-stage-card__solution-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.challenge-stage-card__panel-chips {
  justify-content: flex-start;
}

.challenge-stage-card__insight-row {
  margin-bottom: 14px;
}

.challenge-stage-card__solutions-grid {
  display: grid;
  gap: 12px;
}

.challenge-stage-card__stage-empty {
  display: grid;
  gap: 12px;
  padding: 14px;
  border: 1px dashed rgba(21, 112, 239, 0.42);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(21, 112, 239, 0.08) 0%, rgba(255, 255, 255, 0.96) 100%);
}

.challenge-stage-card__stage-empty-table {
  overflow: hidden;
  border: 1px solid rgba(13, 82, 62, 0.1);
  border-radius: 14px;
  background: #fff;
}

.challenge-stage-card__stage-empty-row {
  display: grid;
  grid-template-columns: minmax(90px, 0.8fr) minmax(180px, 2fr) minmax(90px, 0.8fr) minmax(110px, 0.9fr);
  align-items: center;
}

.challenge-stage-card__stage-empty-row--header {
  background: #0d6b4d;
  color: #fff;
  font-family: BukraBold, sans-serif;
  font-size: 0.78rem;
}

.challenge-stage-card__stage-empty-row span {
  min-height: 42px;
  display: flex;
  align-items: center;
  padding: 10px 12px;
  border-inline-end: 1px solid rgba(13, 82, 62, 0.08);
  line-height: 1.5;
}

.challenge-stage-card__stage-empty-row span:last-child {
  border-inline-end: 0;
}

.challenge-stage-card__stage-empty-cell {
  grid-column: 1 / -1;
  justify-content: center;
  color: #506861;
  text-align: center;
  font-family: DubaiRegular, sans-serif;
  background: #fbfdfc;
}

.challenge-stage-card__stage-empty-note {
  margin: 0;
}

.challenge-stage-card__solution {
  display: grid;
  gap: 12px;
  border: 1px solid rgba(13, 82, 62, 0.08);
  border-radius: 20px;
  background: #fbfdfc;
  padding: 16px;
}

.challenge-stage-card__solution-head,
.challenge-stage-card__solution-stats {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.challenge-stage-card__solution-id {
  color: #0d6b4d;
  font-weight: 700;
}

.challenge-stage-card__solution-text {
  margin: 0;
  color: #203b34;
  line-height: 1.7;
}

.challenge-stage-card__solution-stats {
  align-items: flex-start;
}

.challenge-stage-card__stat-value {
  display: block;
  color: #16362d;
}

.challenge-stage-card__share-label {
  color: #6e7f78;
  font-size: 0.8rem;
}

@media (max-width: 600px) {
  .challenge-stage-card__body {
    padding: 18px;
  }

  .challenge-stage-card__panel-title-row,
  .challenge-stage-card__solution-head,
  .challenge-stage-card__solution-stats {
    flex-direction: column;
    align-items: flex-start;
  }

  .challenge-stage-card__stage-empty-row {
    grid-template-columns: minmax(76px, 0.8fr) minmax(150px, 1.5fr) minmax(76px, 0.8fr) minmax(88px, 0.9fr);
  }
}
</style>
