<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { create, update } from '@/utils/GenericApiHelper.ts'
import { Characters, URIS } from '@/constants/DpConstants.ts'
import { toast } from 'vue3-toastify'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'
import { MergeSolutionByStageRequest } from '@/dto/solution/MergeSolutionByStageRequest.ts'
import { SolutionResponse } from '@/dto/survey/SurveyResponse.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'

export default {
  components: { ConfirmationDialog },
  props: {
    answers: {
      type: Array,
      required: true
    },
    challengeId: {
      type: String,
      required: true
    }
  },
  emits: ['close', 'update-answers'],
  data() {
    return {
      selectedAnswers: [],
      isFinalDecision: false,
      mergePopupVisible: false,
      mergedSolutionTitle: '',
      isConfirmationDialogVisible: false,
      dialogTitle: '',
      dialogMsg: '',
      isFinalConfirmationDialogVisible: false,
      finalDialogTitle: '',
      finalDialogMsg: ''
    }
  },
  computed: {
    showMergeButton(): boolean {
      return this.selectedAnswers.length > 1
    }
  },
  methods: {
    toggleSelection(answerId) {
      const index = this.selectedAnswers.indexOf(answerId)
      if (index === -1) {
        this.selectedAnswers.push(answerId)
      } else {
        this.selectedAnswers.splice(index, 1)
      }
    },
    openFinalDecisionDialog() {
      if (this.isFinalDecision) {
        this.finalDialogTitle = this.$t('challenge.dashboard.roundMsg.confirmationDialogTitle')
        this.finalDialogMsg = this.$t('challenge.dashboard.roundMsg.submitAnswersDialogMsg')
        this.isFinalDecision = false
        this.isFinalConfirmationDialogVisible = true
      } else {
        this.isFinalDecision = false
      }
    },
    handleCloseFinalDialogConfirmation() {
      this.isFinalConfirmationDialogVisible = false
      this.finalDialogTitle = ''
      this.finalDialogMsg = ''
    },
    openMergePopup() {
      this.mergePopupVisible = true
    },
    async confirmMerge() {
      const roundLevel = this.getQueLevelOfFirstAnswer()
      if (this.mergedSolutionTitle.trim()) {
        const mergedData = {
          newTitle: this.mergedSolutionTitle,
          selectedAnswers: this.selectedAnswers
        }
        const request: MergeSolutionByStageRequest = {
          newSolutionText: mergedData.newTitle,
          mergedSolutionIds: mergedData.selectedAnswers,
          queLevel: roundLevel,
          challengeId: this.challengeId
        }
        const response = await create<SuccessResponse<SolutionResponse>>(
          URIS.SOLUTIONS.BASE + URIS.SOLUTIONS.MERGE,
          request
        )
        if (response?.success) {
          console.log(response.data)
          this.$emit('update-answers', mergedData)
          this.mergePopupVisible = false
        }
      } else {
        toast.error('Please enter a valid title for the merged solution')
      }
    },
    cancelMerge() {
      this.mergePopupVisible = false
    },
    async updateSolution() {
      let roundLevel = this.getQueLevelOfFirstAnswer()
      try {
        roundLevel += 1
        // Define the API endpoint for updating solution
        const isFinal = this.isFinalDecision ? 1 : 0
        const solutionEndpoint =
          URIS.SOLUTIONS.BASE +
          URIS.SOLUTIONS.UPDATE_ROUND +
          Characters.QUESTION_MARK +
          'roundLevel=' +
          roundLevel +
          '&isFinal=' +
          isFinal +
          '&challengeId=' +
          this.challengeId +
          '&solutionsListIds=' +
          this.selectedAnswers.join(',')

        // Use the update method to make the put request
        const response = await update<any>(solutionEndpoint, null)
        if (response?.success) {
          console.log('Round updated successfully:' + response)
          toast.success('Round updated successfully', {})
          location.reload()
        } else {
          console.error('Failed to update solution for voting:', response?.message)
          // Handle error scenario
        }
      } catch (error) {
        console.error('هنالك خطأ في عملية تحديث:', error)
      } finally {
        this.handleCloseConfirmation()
      }
    },
    confirmFinalDecision() {
      this.isFinalDecision = true
      this.handleCloseFinalDialogConfirmation()
    },
    getQueLevelOfFirstAnswer(): number {
      if (this.answers.length > 0) {
        return this.answers[0].queLevel // Access the `queLevel` property of the first element
      } else {
        return 1 // Return undefined if `answers` is empty
      }
    },
    cancel() {
      // Add your cancel logic here
      this.$emit('close')
    },
    openUpdateDialog() {
      this.dialogTitle = this.$t('challenge.dashboard.roundMsg.confirmationDialogTitle')
      this.dialogMsg = this.$t('challenge.dashboard.roundMsg.submitAnswersDialogMsg')
      this.isConfirmationDialogVisible = true
    },
    handleCloseConfirmation() {
      this.isConfirmationDialogVisible = false
      this.dialogTitle = ''
      this.dialogMsg = ''
    }
  }
}
</script>

<template src="./VotingRound.html"></template>
<style src="./VotingRound.scss"></style>
