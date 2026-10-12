<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->
<script lang="ts">
import { defineComponent, onMounted, PropType, ref, watch } from 'vue'
import { update } from '@/utils/GenericApiHelper'
import { Characters, Fields, URIS } from '@/constants/DpConstants'
import { useRoute, useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import StatCardOverview from '@/components/challenge/view/StatCardOverview.vue'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'
import { ChallengeForTheUserDashboard } from '@/dto/userDashboard/TheUserDashboardResponse.ts'
import { UserInvitationStatus } from '@/dto/userDashboard/UserInvitationStatus'
import { ChallengeAdminDashboardResponse } from '@/dto/challenge/ChallengeAdminDashboardResponse.ts'
import { TheValidatorDashboardResponse } from '@/dto/userDashboard/TheValidatorDashboardResponse.ts'
import { ChallengeStatus } from '@/dto/challenge/ChallengeStatus.ts'
import { isValidatorOnly } from '@/utils/roleUtils.ts'
import { useSidebarStore } from '@/store/sidebar.ts'

export default defineComponent({
  components: { StatCardOverview, EmptyDataTable, ConfirmationDialog },
  props: {
    mainResponse: {
      type: Object as PropType<TheValidatorDashboardResponse>,
      required: true
    },
    currentTab: {
      type: String,
      required: false,
      default: 'one'
    }
  },
  emits: ['refreshData', 'update:currentTab'],
  setup(props, { emit }) {
    const { t } = useI18n()
    const router = useRouter()
    const route = useRoute()
    const sidebarStore = useSidebarStore()
    const tab = ref<string | undefined>(props?.currentTab)
    const totalInvitationsCount = ref<number>(0)
    const totalAcceptedCount = ref<number>(0)
    const totalPendingCount = ref<number>(0)
    const totalRejectedCount = ref<number>(0)

    const pendingInvitationList = ref<ChallengeForTheUserDashboard[]>([])
    const acceptedInvitationList = ref<ChallengeForTheUserDashboard[]>([])
    const rejectedInvitationsList = ref<ChallengeForTheUserDashboard[]>([])
    const completedInvitationsList = ref<ChallengeAdminDashboardResponse[]>([])

    const requestsUnderReview = ref<ChallengeAdminDashboardResponse[]>([])
    const reviewedChallengesList = ref<ChallengeAdminDashboardResponse[]>([])
    const closedChallengesList = ref<ChallengeAdminDashboardResponse[]>([])

    const selectedItemId = ref<number>(0)
    const cancelReason = ref<string>('')
    const actionType = ref<UserInvitationStatus | ChallengeStatus>()
    const activeDialog = ref<'invitation' | 'review' | null>(null)
    const isActionDialogVisible = ref(false)
    const isActionSubmitting = ref(false)
    const actionDialogTitle = ref('')
    const actionDialogMessage = ref('')
    const actionDialogIcon = ref('mdi-help-circle-outline')
    const actionDialogColor = ref('primary')
    const actionConfirmText = ref('')

    const loadData = () => {
      const mainResponse = props.mainResponse

      totalInvitationsCount.value = mainResponse?.totalInvitationsCount
      totalPendingCount.value = mainResponse?.totalPendingCount
      totalRejectedCount.value = mainResponse?.totalRejectedCount
      totalAcceptedCount.value = mainResponse?.totalAcceptedCount

      pendingInvitationList.value = mainResponse?.pendingInvitationList as ChallengeForTheUserDashboard[]
      acceptedInvitationList.value = mainResponse?.acceptedInvitationList as ChallengeForTheUserDashboard[]
      rejectedInvitationsList.value = mainResponse?.rejectedInvitationsList as ChallengeForTheUserDashboard[]
      completedInvitationsList.value = mainResponse?.completedInvitationsList as ChallengeAdminDashboardResponse[]

      requestsUnderReview.value = mainResponse?.requestsUnderReview as ChallengeAdminDashboardResponse[]
      reviewedChallengesList.value = mainResponse?.reviewedChallengesList as ChallengeAdminDashboardResponse[]
      closedChallengesList.value = mainResponse?.closedChallengesList as ChallengeAdminDashboardResponse[]
    }

    watch(
      () => props.mainResponse,
      (newValue, oldValue) => {
        if (newValue !== oldValue) {
          loadData()
        }
      }
    )

    watch(
      () => props.currentTab,
      (newValue) => {
        if (newValue) {
          tab.value = newValue
          updateRoute(newValue)
        }
      }
    )

    watch(
      () => route.params.tab,
      (newTab) => {
        if (newTab) {
          tab.value = newTab as string
        }
      }
    )

    const openApprovedConfirmationDialog = (requestId: number) => {
      handleOpenDialogForConfirmation(requestId, UserInvitationStatus.ACCEPTED)
    }

    const openCanceledConfirmationDialog = (requestId: number) => {
      handleOpenDialogForConfirmation(requestId, UserInvitationStatus.REJECTED)
    }

    const handleOpenDialogForConfirmation = (requestId: number, thePopupType: UserInvitationStatus) => {
      selectedItemId.value = requestId
      activeDialog.value = 'invitation'
      actionType.value = thePopupType
      if (thePopupType !== UserInvitationStatus.REJECTED) {
        cancelReason.value = ''
      }
      isActionDialogVisible.value = true
    }

    const performAction = async () => {
      const challengeId = selectedItemId.value
      const reason = cancelReason.value

      try {
        isActionSubmitting.value = true

        if (actionType.value === UserInvitationStatus.ACCEPTED) {
          await updateTheStatus(challengeId, UserInvitationStatus.ACCEPTED)
        }

        if (actionType.value === UserInvitationStatus.REJECTED) {
          await updateTheStatus(challengeId, UserInvitationStatus.REJECTED, reason)
        }

        emit('refreshData')
        closeActionDialog()
      } finally {
        isActionSubmitting.value = false
      }
    }

    const openApprovedConfirmationDialogForChangeChallengeStatus = (requestId: number) => {
      handleOpenDialogForConfirmationForChangeChallengeStatus(requestId, ChallengeStatus.UNDER_ADMIN_REVIEW)
    }

    const openCanceledConfirmationDialogForChangeChallengeStatus = (requestId: number) => {
      handleOpenDialogForConfirmationForChangeChallengeStatus(requestId, ChallengeStatus.CLOSED)
    }

    const handleOpenDialogForConfirmationForChangeChallengeStatus = (
      requestId: number,
      thePopupType: ChallengeStatus
    ) => {
      selectedItemId.value = requestId
      activeDialog.value = 'review'
      actionType.value = thePopupType
      cancelReason.value = ''
      isActionDialogVisible.value = true
    }

    const performActionForChangeChallengeStatus = async () => {
      try {
        isActionSubmitting.value = true

        if (actionType.value === ChallengeStatus.UNDER_ADMIN_REVIEW) {
          await updateChallengeStatusToAdminReview(selectedItemId.value)
        }

        if (actionType.value === ChallengeStatus.CLOSED) {
          await updateChallengeStatusToClosed(selectedItemId.value)
        }

        closeActionDialog()
      } finally {
        isActionSubmitting.value = false
      }
    }

    const updateChallengeStatusToAdminReview = async (challengeId: number) => {
      await updateChallengeStatus(challengeId, ChallengeStatus.UNDER_ADMIN_REVIEW)
    }

    const updateChallengeStatusToClosed = async (challengeId: number) => {
      await updateChallengeStatus(challengeId, ChallengeStatus.CLOSED)
    }

    const updateChallengeStatus = async (challengeId: number, status: ChallengeStatus) => {
      const response: any = await update(
        `${URIS.CHALLENGES.BASE}${URIS.CHALLENGES.STATUS}${Characters.QUESTION_MARK}${Fields.CHALLENGE_ID}${Characters.EQUAL}${challengeId}${Characters.AND}${Fields.STATUS}${Characters.EQUAL}${status}`,
        null
      )
      if (response.success) {
        toast.success(t('Messages.success.challenge.updated'))
        loadData()
      } else toast.success(t(response.message))
    }

    const setCurrentTab = (index: any) => {
      // Emit an event to the parent to update the currentTab
      emit('update:currentTab', index)
      localStorage.setItem('currentValidatorDashboardTab', index)
    }

    const handleAdminRequestRowClick = (item: any) => {
      console.log('Clicked item:', item)
      const challengeId = item.id
      console.log(challengeId)
      router.push({ name: 'ChallengeView', params: { challengeId } })
    }

    const updateTheStatus = async (challengeId: number, status: UserInvitationStatus, reason?: string) => {
      let url = `${URIS.VALIDATOR.BASE}${URIS.VALIDATOR.UPDATE_STATUS}${Characters.QUESTION_MARK}${Fields.CHALLENGE_ID}${Characters.EQUAL}${challengeId}${Characters.AND}${Fields.INVITATION_STATUS}${Characters.EQUAL}${status}`
      if (reason) {
        url += `${Characters.AND}${Fields.REASON}${Characters.EQUAL}${encodeURIComponent(reason)}`
      }
      const response: any = await update(url, null)
      if (response.success) {
        toast.success(t('Messages.success.challenge.updated'))
        loadData()
      } else toast.success(t(response.message))
    }

    const viewChallengeDashboard = (challengeId: number) => {
      console.log('challengeId is -> ' + challengeId)
      router.push({
        name: 'ChallengeDashboard',
        params: { challengeId }
      })
    }

    const updateRoute = (tab: any) => {
      router.push({ name: 'UserRequestsDashboard', params: { tab: tab } })
    }

    const closeActionDialog = () => {
      isActionDialogVisible.value = false
      activeDialog.value = null
      actionType.value = undefined
      selectedItemId.value = 0
      cancelReason.value = ''
    }

    watch(
      [activeDialog, actionType],
      ([dialog, newType]) => {
        if (dialog === 'invitation' && newType === UserInvitationStatus.ACCEPTED) {
          actionDialogTitle.value = t('myDashboard.dialog.accept.title')
          actionDialogMessage.value = t('myDashboard.dialog.accept.message')
          actionDialogIcon.value = 'mdi-check-decagram-outline'
          actionDialogColor.value = 'success'
          actionConfirmText.value = t('myDashboard.dialog.accept.confirm')
          return
        }

        if (dialog === 'invitation' && newType === UserInvitationStatus.REJECTED) {
          actionDialogTitle.value = t('myDashboard.dialog.reject.title')
          actionDialogMessage.value = t('myDashboard.dialog.reject.message')
          actionDialogIcon.value = 'mdi-close-octagon-outline'
          actionDialogColor.value = 'error'
          actionConfirmText.value = t('myDashboard.dialog.reject.confirm')
          return
        }

        if (dialog === 'review' && newType === ChallengeStatus.UNDER_ADMIN_REVIEW) {
          actionDialogTitle.value = t('adminRequests.dialog.accept.title')
          actionDialogMessage.value = t('adminRequests.dialog.accept.message')
          actionDialogIcon.value = 'mdi-check-decagram-outline'
          actionDialogColor.value = 'success'
          actionConfirmText.value = t('adminRequests.dialog.accept.confirm')
          return
        }

        if (dialog === 'review' && newType === ChallengeStatus.CLOSED) {
          actionDialogTitle.value = t('adminRequests.dialog.reject.title')
          actionDialogMessage.value = t('adminRequests.dialog.reject.message')
          actionDialogIcon.value = 'mdi-close-octagon-outline'
          actionDialogColor.value = 'error'
          actionConfirmText.value = t('adminRequests.dialog.reject.confirm')
          return
        }

        actionDialogTitle.value = ''
        actionDialogMessage.value = ''
        actionDialogIcon.value = 'mdi-help-circle-outline'
        actionDialogColor.value = 'primary'
        actionConfirmText.value = t('common.confirm')
      },
      { immediate: true }
    )

    onMounted(() => {
      sidebarStore.disable()
      const savedTab = localStorage.getItem('currentValidatorDashboardTab')
      if (savedTab) {
        tab.value = savedTab
        updateRoute(savedTab)
      } else if (props.currentTab) {
        tab.value = props.currentTab
        updateRoute(props.currentTab)
      } else {
        tab.value = 'one' // Default tab
        updateRoute('one')
      }
      loadData()
    })

    return {
      tab,
      setCurrentTab,
      dialog: false,
      totalInvitationsCount,
      totalAcceptedCount,
      totalPendingCount,
      totalRejectedCount,
      viewChallengeDashboard,
      pendingInvitationList,
      acceptedInvitationList,
      rejectedInvitationsList,
      completedInvitationsList,
      handleAdminRequestRowClick,
      openApprovedConfirmationDialog,
      openCanceledConfirmationDialog,
      performAction,
      closedChallengesList,
      requestsUnderReview,
      reviewedChallengesList,
      openApprovedConfirmationDialogForChangeChallengeStatus,
      openCanceledConfirmationDialogForChangeChallengeStatus,
      performActionForChangeChallengeStatus,
      isValidatorOnly,
      updateRoute,
      cancelReason,
      actionType,
      activeDialog,
      isActionDialogVisible,
      isActionSubmitting,
      closeActionDialog,
      actionDialogTitle,
      actionDialogMessage,
      actionDialogIcon,
      actionDialogColor,
      actionConfirmText
    }
  }
})
</script>

<template src="./ValidatorDashboard.html" v-if="isValidatorOnly()" lang="html"></template>
<style src="@/assets/css/requestsDashboard.scss" lang="scss"></style>
