<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { SurveyCreationRequest } from '@/dto/survey/SurveyCreationRequest.ts'
import AppRichTextEditor from '@/components/common/richText/AppRichTextEditor.vue'

export default defineComponent({
  components: {
    AppRichTextEditor
  },
  props: {
    isOpen: {
      type: Boolean as PropType<boolean>,
      required: true
    },
    challengeId: {
      type: String as PropType<string>,
      required: true
    },
    solutionId: {
      type: String as PropType<string>,
      required: true
    },
    initialData: {
      type: Object as PropType<{
        rating: number
        isApplicable: boolean
        feedback: string
        applicationInstructions: string
      }>,
      default: () => ({
        rating: 0,
        isApplicable: false,
        feedback: '',
        applicationInstructions: ''
      })
    }
  },
  emits: ['submit-rating', 'close-popup'],
  data() {
    return {
      rating: 0,
      isApplicable: false,
      feedback: '',
      applicationInstructions: ''
    }
  },
  watch: {
    isOpen(newVal: boolean) {
      if (newVal) {
        this.initializeData()
      }
    }
  },
  methods: {
    submitRating() {
      this.$emit('submit-rating', {
        challengeId: Number(this.challengeId),
        solutionId: Number(this.solutionId),
        rating: this.rating,
        applicableToApply: this.isApplicable,
        comments: this.feedback,
        howToApply: this.applicationInstructions
      } as SurveyCreationRequest)
      this.closePopup()
    },
    closePopup() {
      this.$emit('close-popup')

      // Clear/reset form data
      this.rating = 0
      this.isApplicable = false
      this.feedback = ''
      this.applicationInstructions = ''
    },
    handleOutsideClick() {
      this.closePopup()
    },
    initializeData() {
      const { rating, isApplicable, feedback, applicationInstructions } = this.initialData
      this.rating = rating
      this.isApplicable = isApplicable
      this.feedback = feedback
      this.applicationInstructions = applicationInstructions
    }
  }
})
</script>

<template src="./RatingPopup.html"></template>
<style src="./RatingPopup.scss"></style>
