<template>
  <v-card class="dp-card">
    <h4 class="tab-title mb-4">{{ $t('challenge.info.solutionsRateList') }}</h4>
    <div class="solutions-input-section mb-4">
      <div class="d-flex gap-4 align-center">
        <v-text-field
          v-model="inputSolution"
          :label="$t('challenge.info.solutionText')"
          variant="outlined"
          density="compact"
          class="flex-grow-1 challenge-text-direction-field"
          :dir="inputDirection"
          :placeholder="$t('challenge.info.enterSolutionAndPressEnter')"
          prepend-inner-icon="mdi-lightbulb-on"
          @keydown.enter.prevent="addSolutionFromInput"
        />
        <v-btn
          :icon="true"
          color="primary"
          size="large"
          class="custom-add-solution-plus"
          :disabled="!inputSolution.trim()"
          @click="addSolutionFromInput"
        >
          <v-icon>mdi-plus</v-icon>
        </v-btn>
      </div>
    </div>
    <div v-if="validSolutions.length > 0" class="solutions-tags-section">
      <h6 class="mb-3 text-subtitle-2 custom-added-solutions-text">
        {{ $t('challenge.info.addedSolutions') }} ({{ validSolutions.length }})
      </h6>
      <div class="solutions-tags-container">
        <v-chip
          v-for="(solution, index) in validSolutions"
          :key="index"
          class="ma-1 mb-2"
          closable
          color="primary"
          variant="elevated"
          size="large"
          @click:close="removeSolution(index)"
        >
          <v-icon start class="mr-1">mdi-lightbulb</v-icon>
          <span :dir="getTextDirection(solution)" class="challenge-text-direction-content">{{ solution }}</span>
        </v-chip>
      </div>
    </div>
    <div v-else class="empty-solutions-state text-center pa-6">
      <v-icon size="64" color="grey-lighten-1" class="mb-3">mdi-lightbulb-outline</v-icon>
      <p class="text-grey-lighten-1">{{ $t('challenge.info.noSolutionsAdded') }}</p>
      <p class="text-caption text-grey-lighten-2">{{ $t('challenge.info.startAddingSolutions') }}</p>
    </div>
  </v-card>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from 'vue'
import { getTextDirection } from '@/utils/textDirection'

export default defineComponent({
  name: 'SolutionsTab',
  props: {
    solutionsTexts: {
      type: Array as () => { value: string }[],
      required: true
    },
    rules: {
      type: Object,
      required: false
    }
  },
  emits: ['update:solutionsTexts'],
  setup(props, { emit }) {
    const inputSolution = ref('')
    const validSolutions = computed(() => props.solutionsTexts.map((s) => s.value).filter((s) => s && s.trim() !== ''))
    const inputDirection = computed(() => getTextDirection(inputSolution.value))

    const addSolutionFromInput = () => {
      const value = inputSolution.value.trim()
      if (value) {
        emit('update:solutionsTexts', [...props.solutionsTexts, { value }])
        inputSolution.value = ''
      }
    }

    const removeSolution = (index: number) => {
      const newSolutions = props.solutionsTexts.slice()
      newSolutions.splice(index, 1)
      emit('update:solutionsTexts', newSolutions)
    }

    return {
      inputSolution,
      inputDirection,
      validSolutions,
      addSolutionFromInput,
      getTextDirection,
      removeSolution
    }
  }
})
</script>

<style scoped lang="scss">
.custom-added-solutions-text {
  margin: 0 2%;
}

.solutions-tags-container {
  margin: 0 3% 1% 3%;
}

.custom-add-solution-plus {
  margin-left: 2%;
  margin-bottom: 1.3%;
}

.challenge-text-direction-field :deep(input) {
  text-align: start;
}

.challenge-text-direction-content {
  display: inline-block;
  text-align: start;
  unicode-bidi: plaintext;
}

:deep(.v-chip__content),
:deep(.v-btn__content) {
  align-items: center;
  display: inline-flex;
  gap: 8px;
}
</style>
