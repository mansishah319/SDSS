<template>
  <v-card class="dp-card common-challenge-card-container">
    <h4 class="tab-title mb-4">{{ $t('challenge.info.challengeSettings') }}</h4>
    <v-row>
      <v-col cols="12" md="4">
        <v-autocomplete
          v-model="challengeRequest.topicClassification"
          :items="topicClassificationListLookupResponse"
          item-value="id"
          item-title="name"
          variant="outlined"
          density="compact"
          :label="$t('challenge.info.topicClassification')"
          @update:model-value="
            $emit('update:challengeRequest', {
              ...challengeRequest,
              topicClassification: challengeRequest.topicClassification,
            })
          "
        />
      </v-col>
      <v-col cols="12" md="4">
        <v-autocomplete
          v-model="challengeRequest.levelOfDecision"
          :items="levelOfDecisionListLookupResponse"
          item-value="id"
          item-title="name"
          variant="outlined"
          density="compact"
          :label="$t('challenge.info.levelOfDecision')"
          @update:model-value="
            $emit('update:challengeRequest', { ...challengeRequest, levelOfDecision: challengeRequest.levelOfDecision })
          "
        />
      </v-col>
      <v-col cols="12" md="4">
        <v-autocomplete
          v-model="challengeRequest.importanceOfDecision"
          :clearable="true"
          :items="importanceOfDecisionListLookupResponse"
          item-value="id"
          item-title="name"
          variant="outlined"
          density="compact"
          :label="$t('challenge.info.impactOfDecision')"
          @update:model-value="
            $emit('update:challengeRequest', {
              ...challengeRequest,
              importanceOfDecision: challengeRequest.importanceOfDecision,
            })
          "
        />
      </v-col>
      <v-col cols="12" md="4">
        <v-autocomplete
          v-model="challengeRequest.impact"
          :clearable="true"
          :items="impactListLookupResponse"
          variant="outlined"
          density="compact"
          item-value="id"
          item-title="name"
          :label="$t('challenge.info.impact')"
          @update:model-value="
            $emit('update:challengeRequest', { ...challengeRequest, impact: challengeRequest.impact })
          "
        />
      </v-col>
      <v-col cols="12" md="4">
        <v-autocomplete
          v-model="challengeRequest.field"
          :clearable="true"
          :items="fieldListLookupResponse"
          variant="outlined"
          density="compact"
          item-value="id"
          item-title="name"
          :label="$t('challenge.info.field')"
          @update:model-value="$emit('update:challengeRequest', { ...challengeRequest, field: challengeRequest.field })"
        />
      </v-col>
    </v-row>
  </v-card>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'ChallengeSettingsTab',
  props: {
    challengeRequest: { type: Object, required: true },
    topicClassificationListLookupResponse: { type: Array, required: true },
    levelOfDecisionListLookupResponse: { type: Array, required: true },
    importanceOfDecisionListLookupResponse: { type: Array, required: true },
    impactListLookupResponse: { type: Array, required: true },
    fieldListLookupResponse: { type: Array, required: true },
    rules: { type: Object, required: false }
  },
  emits: ['update:challengeRequest']
})
</script>

<style scoped lang="scss"></style>
