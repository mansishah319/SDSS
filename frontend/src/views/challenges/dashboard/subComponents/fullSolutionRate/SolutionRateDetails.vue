<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { SurveyForSolutionResponse } from '@/dto/survey/SurveyResponse.ts'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import { ChallengeSolutionResponse } from '@/dto/challenge/ChallengeSolutionResponse.ts'

export default defineComponent({
  components: { EmptyDataTable },
  props: {
    isOpen: {
      type: Boolean as PropType<boolean>,
      required: true
    },
    solutionsRateList: {
      type: Array as PropType<SurveyForSolutionResponse[]>,
      required: true
    },
    solution: {
      type: {} as PropType<ChallengeSolutionResponse>,
      required: true
    }
  },
  emits: ['update:isOpen', 'close-popup'],
  computed: {
    dialogModel: {
      get() {
        return this.isOpen
      },
      set(value: boolean) {
        this.$emit('update:isOpen', value)
      }
    }
  },
  methods: {
    closePopup() {
      this.$emit('close-popup')
    },
    handleOutsideClick() {
      this.closePopup()
    }
  }
})
</script>

<template src="./SolutionRateDetails.html"></template>
<style src="./SolutionRateDetails.scss" lang="scss" scoped></style>
