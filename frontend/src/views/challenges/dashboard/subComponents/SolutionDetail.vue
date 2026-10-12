<script lang="ts">
import { onMounted, ref } from 'vue'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import { create, del, get, update } from '@/utils/GenericApiHelper.ts'
import { Characters, SecurityConstants, URIS } from '@/constants/DpConstants.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { SolutionCommentResponse } from '@/dto/challenge/SolutionCommentResponse.ts'
import Employee from '@/dto/Employee.ts'
import jwt_decode from 'jwt-decode'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'

export enum ConfirmationType {
  _DELETE_COMMENT = '_DELETE_COMMENT',
  _DELETE_SOLUTION = '_DELETE_SOLUTION',
}

export default {
  components: { ConfirmationDialog },
  props: {
    selectedSolution: {
      type: Object,
      required: true
    }
  },
  emits: ['close', 'delete-solution'],
  setup(props, { emit }) {
    const { t } = useI18n()
    const newComment = ref('')
    const isConfirmationDialogVisible = ref<boolean>(false)
    const currentlyEditing = ref<number | null>(null)
    const inUpdateProgress = ref<boolean>(false)
    const token = localStorage.getItem(SecurityConstants.TOKEN)
    const deleteDialogTitle = ref<string>('')
    const deleteDialogMsg = ref<string>('')
    const confirmationType = ref<ConfirmationType>()
    const userData: Employee = jwt_decode(token)
    const canComment = ref(true)
    const selectedCommentToDelete = ref<number>(0)
    const phase = ref('answering')
    const headers = [
      { text: 'Comment', align: 'start', value: 'solComment' },
      { text: 'Actions', value: 'actions', sortable: false }
    ]
    // Sample data for comments
    const comments = ref<SolutionCommentResponse[]>([])

    const updateSolution = async () => {
      const solutionRequest = {
        queLevel: props.selectedSolution.queLevel,
        solutionText: props.selectedSolution.solutionText,
        isFinal: props.selectedSolution.isFinal,
        isAdminAgree: props.selectedSolution.isAdminAgree
      }
      try {
        // Define the API endpoint for updateing solution
        const solutionEndpoint =
          URIS.SOLUTIONS.BASE + Characters.QUESTION_MARK + 'solutionId=' + props.selectedSolution.id
        // Use the update method to make the put request
        const response = await update<any>(solutionEndpoint, solutionRequest)
        if (response?.success) {
          console.log('Updation success:' + response)
        } else {
          console.error('Failed to update solution:', response?.message)
          // Handle error scenario
        }
        console.log('تم تحديث بنجاح')
        toast.success(t('Messages.solution.updated'))
        // Optionally, display a success message using a notification library or other means
      } catch (error) {
        console.error('هنالك خطأ في عملية تحديث:', error)
        // Optionally, display an error message using a notification library or other means
      }
    }
    const deleteSolution = async () => {
      try {
        const response = await del<SuccessResponse<string>>(
          URIS.SOLUTIONS.BASE + Characters.QUESTION_MARK + 'solutionId=' + props.selectedSolution.id
        )
        if (response?.success) {
          console.log('Deletion success:' + response)
          emit('delete-solution', props.selectedSolution.id)
        } else {
          console.error('Failed to delete solution:', response?.message)
          toast.error(response ? response?.message : '')
          // Handle error scenario
        }
        console.log('تم الحذف بنجاح')
        toast.success(t('Messages.success.solution.deleted'))
      } catch (error: any) {
        console.error('هنالك خطأ في عملية الحذف:', error)
        toast.success(error.message)
      }
    }

    const submitComment = async () => {
      console.log('In submit comment:' + newComment.value)
      if (newComment.value.trim() && canComment.value) {
        const commentPayload = {
          solComment: newComment.value.trim(),
          solutionId: props.selectedSolution.id
        }
        try {
          const response = await create<any>(URIS.SOLUTION_COMMENTS.BASE, commentPayload)
          if (response.success) {
            // You may need to adjust the response handling based on your API
            console.log(response.data)
            comments.value.push(response.data)
            console.log('New comment list:' + comments.value)
            // answers.value.push({
            //     content: newAnswer.value,
            //     id: response.data.id,
            //     votes: 0,
            // })

            newComment.value = ''
            toast.success(t('Messages.success.solution.createdComment'))
          }
        } catch (error) {
          // Handle error scenarios (display an error message, log, etc.)
          console.error('Error submitting answer:', error)
        }
      }
    }
    const fetchComments = async () => {
      try {
        const response = await get<SolutionCommentResponse[]>(
          URIS.SOLUTION_COMMENTS.BASE +
          URIS.SOLUTION_COMMENTS.GET_BY_SOLUTION_ID +
          Characters.QUESTION_MARK +
          'solutionId=' +
          props.selectedSolution.id
        )
        if (response.success) {
          comments.value = response.data
        }
      } catch (error) {
        console.error('Error fetching comments:', error)
        // Handle error scenarios (display an error message, log, etc.)
      }
    }
    const editComment = (comment: any) => {
      // Handle edit action
      console.log('Editing comment:', comment)
      currentlyEditing.value = comment.id
      inUpdateProgress.value = true
    }

    const submitUpdatedComment = async (comment: any) => {
      const commentPayload = {
        solComment: comment.solComment
      }

      try {
        // Make the API request to update the comment
        const response = await update<any>(URIS.SOLUTION_COMMENTS.BASE + '?commentId=' + comment.id, commentPayload)

        if (response.success) {
          // Find the index of the comment in the array
          const commentIndex = comments.value.findIndex((c) => c.id === comment.id)

          if (commentIndex !== -1) {
            // Update the comment with the new data
            comments.value[commentIndex] = {
              ...comments.value[commentIndex],
              solComment: comment.solComment
            }
          }

          console.log('Comment updated successfully:', response)
          toast.success(t('Messages.success.solution.updatedComment'))
          inUpdateProgress.value = false
          currentlyEditing.value = null
        } else {
          console.error('Failed to update comment:', response.message)
          toast.error(response.message)
        }
      } catch (error: any) {
        console.error('Error updating comment:', error)
        toast.error(error.message)
      }
    }

    const showDeleteSolutionDialog = () => {
      deleteDialogTitle.value = t('Messages.success.solution.Confirm.DeleteTitle')
      deleteDialogMsg.value = t('Messages.success.solution.Confirm.DeleteMessage')
      confirmationType.value = ConfirmationType._DELETE_SOLUTION
      isConfirmationDialogVisible.value = true
    }

    const showDeleteCommentDialog = (comment: any) => {
      console.log(comment)
      selectedCommentToDelete.value = comment.id
      deleteDialogTitle.value = t('Messages.success.solution.commentConfirm.DeleteTitle')
      deleteDialogMsg.value = t('Messages.success.solution.commentConfirm.DeleteMessage')
      confirmationType.value = ConfirmationType._DELETE_COMMENT
      isConfirmationDialogVisible.value = true
    }

    const closeUpdatedComment = (comment: any) => {
      console.log(comment.solComment)
      inUpdateProgress.value = false
    }

    const handleConfirmation = () => {
      console.log(confirmationType.value)
      if (confirmationType.value === ConfirmationType._DELETE_SOLUTION) {
        deleteSolution()
      }

      if (confirmationType.value === ConfirmationType._DELETE_COMMENT) {
        deleteComment(selectedCommentToDelete.value)
      }
    }

    const handleCancelConfirmation = () => {
      isConfirmationDialogVisible.value = false
    }

    const deleteComment = async (commentId: number) => {
      // Handle delete action
      console.log('Deleting comment ID:', commentId)
      try {
        const response = await del<SuccessResponse<string>>(
          URIS.SOLUTION_COMMENTS.BASE + Characters.QUESTION_MARK + 'commentId=' + commentId
        )
        if (response?.success) {
          console.log('Deletion success:' + response)

          // Remove the deleted comment from the comments array
          comments.value = comments.value.filter((comment) => comment.id !== commentId)
          toast.success(t('Messages.success.solution.deletedComment'))
        } else {
          console.error('Failed to delete solution:', response?.message)
          toast.error(response ? response?.message : '')
          // Handle error scenario
        }
      } catch (error: any) {
        console.error('هنالك خطأ في عملية الحذف:', error)
        toast.error(error.message)
      }
    }

    const userAbleToEdit = (comment: any) => {
      return Number(userData.grpNumber) === Number(comment.createdBy)
    }

    const cancel = () => {
      emit('close')
    }
    onMounted(fetchComments)
    return {
      newComment,
      canComment,
      phase,
      updateSolution,
      deleteSolution,
      submitComment,
      cancel,
      headers,
      comments,
      editComment,
      deleteComment,
      userAbleToEdit,
      currentlyEditing,
      inUpdateProgress,
      submitUpdatedComment,
      closeUpdatedComment,
      isConfirmationDialogVisible,
      handleConfirmation,
      showDeleteSolutionDialog,
      deleteDialogTitle,
      deleteDialogMsg,
      showDeleteCommentDialog,
      handleCancelConfirmation
    }
  }
}
</script>

<template src="./SolutionDetail.html"></template>

<style src="./SolutionDetail.scss" lang="scss" scoped></style>
