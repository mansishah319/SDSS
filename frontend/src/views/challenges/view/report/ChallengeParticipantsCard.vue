<script setup lang="ts">
import { computed } from 'vue'
import type {
  ChallengeInvitationSummaryViewModel,
  ChallengeParticipantsCardLabels,
  ChallengeParticipantViewModel
} from './challengeReport.types'

const props = defineProps<{
  summary: ChallengeInvitationSummaryViewModel
  participants: ChallengeParticipantViewModel[]
  labels: ChallengeParticipantsCardLabels
}>()

const stateMap = computed(() => ({
  pending: { label: props.labels.pendingState, color: 'warning' },
  accepted: { label: props.labels.acceptedState, color: 'success' },
  rejected: { label: props.labels.rejectedState, color: 'error' },
  invited: { label: props.labels.invitedState, color: 'info' }
}))

const compactStats = computed(() => [
  { key: 'invited', label: props.labels.invited, value: props.summary.invited, color: 'primary' },
  { key: 'pending', label: props.labels.pending, value: props.summary.pending, color: 'warning' },
  { key: 'accepted', label: props.labels.accepted, value: props.summary.accepted, color: 'success' },
  { key: 'rejected', label: props.labels.rejected, value: props.summary.rejected, color: 'error' }
])
</script>

<template>
  <v-card class="challenge-participants-card" rounded="xl" elevation="0">
    <v-card-text class="challenge-participants-card__body">
      <div class="challenge-participants-card__header">
        <div>
          <p class="challenge-participants-card__eyebrow">{{ labels.subtitle }}</p>
          <h3 class="challenge-participants-card__title font-bukra-bold">{{ labels.title }}</h3>
        </div>

        <v-chip color="success" variant="tonal" size="small"> {{ summary.online }} {{ labels.online }}</v-chip>
      </div>

      <div class="challenge-participants-card__stats">
        <article v-for="item in compactStats" :key="item.key" class="challenge-participants-card__stat">
          <span class="challenge-participants-card__stat-label">{{ item.label }}</span>
          <strong class="challenge-participants-card__stat-value font-bukra-bold">{{ item.value }}</strong>
        </article>
      </div>

      <div v-if="participants.length" class="challenge-participants-card__list">
        <article v-for="participant in participants" :key="participant.id" class="challenge-participants-card__row">
          <div class="challenge-participants-card__identity">
            <span class="challenge-participants-card__status-dot" :class="{ 'is-online': participant.online }" />
            <div>
              <strong class="challenge-participants-card__name font-dubai-bold">{{ participant.name }}</strong>
              <div class="challenge-participants-card__meta"
              >#{{ participant.grpNumber }} • {{ participant.email }}
              </div
              >
            </div>
          </div>

          <div class="challenge-participants-card__states">
            <span class="challenge-participants-card__presence">{{
                participant.online ? labels.online : labels.offline
              }}</span>
            <v-chip :color="stateMap[participant.state].color" variant="tonal" size="x-small">
              {{ stateMap[participant.state].label }}
            </v-chip>
          </div>
        </article>
      </div>

      <div v-else class="challenge-participants-card__empty">
        {{ labels.empty }}
      </div>
    </v-card-text>
  </v-card>
</template>

<style scoped lang="scss">
.challenge-participants-card {
  border: 1px solid rgba(13, 82, 62, 0.1);
  background: #fff;
}

.challenge-participants-card__body {
  padding: 22px;
  display: grid;
  gap: 18px;
}

.challenge-participants-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.challenge-participants-card__eyebrow,
.challenge-participants-card__stat-label,
.challenge-participants-card__meta,
.challenge-participants-card__presence {
  color: #73837d;
}

.challenge-participants-card__eyebrow {
  margin: 0;
  font-size: 0.8rem;
}

.challenge-participants-card__title {
  margin: 8px 0 0;
  color: #16362d;
}

.challenge-participants-card__stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.challenge-participants-card__stat {
  padding: 14px;
  border-radius: 18px;
  background: #f8fbfa;
  border: 1px solid rgba(13, 82, 62, 0.06);
}

.challenge-participants-card__stat-value {
  display: block;
  margin-top: 6px;
  color: #17362d;
}

.challenge-participants-card__list {
  display: grid;
  gap: 12px;
}

.challenge-participants-card__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid rgba(13, 82, 62, 0.08);
  border-radius: 20px;
}

.challenge-participants-card__identity,
.challenge-participants-card__states {
  display: flex;
  align-items: center;
  gap: 10px;
}

.challenge-participants-card__identity {
  min-width: 0;
}

.challenge-participants-card__name,
.challenge-participants-card__meta {
  display: block;
}

.challenge-participants-card__meta {
  font-size: 0.8rem;
  overflow-wrap: anywhere;
}

.challenge-participants-card__status-dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #d1d5db;
  flex-shrink: 0;
}

.challenge-participants-card__status-dot.is-online {
  background: #12b76a;
  box-shadow: 0 0 0 4px rgba(18, 183, 106, 0.12);
}

.challenge-participants-card__empty {
  padding: 18px;
  text-align: center;
  color: #6f817a;
  background: #f8fbfa;
  border-radius: 18px;
}

@media (max-width: 600px) {
  .challenge-participants-card__body {
    padding: 18px;
  }

  .challenge-participants-card__stats {
    grid-template-columns: 1fr 1fr;
  }

  .challenge-participants-card__header,
  .challenge-participants-card__row,
  .challenge-participants-card__states {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
