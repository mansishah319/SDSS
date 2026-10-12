<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->
<script lang="ts">
import { defineComponent, onMounted, ref, watch } from 'vue'
import { get, update } from '@/utils/GenericApiHelper'
import type { SuccessResponse } from '@/dto/generic/SuccessResponse'
import { Characters, Fields, URIS } from '@/constants/DpConstants'
import { useRoute, useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import StatCardOverview from '@/components/challenge/view/StatCardOverview.vue'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import ValidatorDashboard from '@/views/validator/requests/ValidatorDashboard.vue'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'
import {
  ChallengeForTheUserDashboard,
  MyMainDashboardResponse,
  TheUserDashboardResponse
} from '@/dto/userDashboard/TheUserDashboardResponse'
import { UserInvitationStatus } from '@/dto/userDashboard/UserInvitationStatus'
import { TheValidatorDashboardResponse } from '@/dto/userDashboard/TheValidatorDashboardResponse'
import { isValidatorOnly } from '@/utils/roleUtils'
import { useSidebarStore } from '@/store/sidebar'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'

export default defineComponent({
  components: { StatCardOverview, EmptyDataTable, ValidatorDashboard, GenericDashboardHeaderView, ConfirmationDialog },
  setup() {
    const { t, locale } = useI18n()
    const router = useRouter()
    const route = useRoute()
    const sidebarStore = useSidebarStore()
    const tab = ref<string | null>(null)
    const totalInvitationsCount = ref<number>(0)
    const totalAcceptedCount = ref<number>(0)
    const totalPendingCount = ref<number>(0)
    const totalRejectedCount = ref<number>(0)

    const isLoading = ref<boolean>(true)

    const votingParticipatedCount = ref<number>(0)
    const votingNotParticipatedCount = ref<number>(0)

    const pendingInvitationList = ref<ChallengeForTheUserDashboard[]>([])
    const acceptedInvitationList = ref<ChallengeForTheUserDashboard[]>([])
    const rejectedInvitationsList = ref<ChallengeForTheUserDashboard[]>([])
    const completedInvitationsList = ref<ChallengeForTheUserDashboard[]>([])

    const employeeLang: string = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || locale.value || 'ar'

    const cancelReason = ref<string>('') // This will hold the reason
    const isActionDialogVisible = ref(false)
    const isActionSubmitting = ref(false)

    const validatorMainResponse = ref<TheValidatorDashboardResponse>({
      acceptedInvitationList: [],
      closedChallengesList: [],
      completedInvitationsList: [],
      pendingInvitationList: [],
      rejectedInvitationsList: [],
      requestsUnderReview: [],
      reviewedChallengesList: [],
      totalAcceptedCount: 0,
      totalInvitationsCount: 0,
      totalPendingCount: 0,
      totalRejectedCount: 0,
      votingNotParticipatedCount: 0,
      votingParticipatedCount: 0
    })

    const selectedItemId = ref<number>(0)
    const popupType = ref<UserInvitationStatus>()

    const loadData = async () => {
      const theMainDashboardResponse = await get<SuccessResponse<MyMainDashboardResponse>>(
        URIS.LOOKUP.BASE + URIS.LOOKUP.MY_DASHBOARD
      )
      const mainResponse = theMainDashboardResponse?.data.userDashboardResponse as TheUserDashboardResponse

      validatorMainResponse.value = theMainDashboardResponse?.data
        .validatorsDashboardResponse as TheValidatorDashboardResponse

      totalInvitationsCount.value = mainResponse?.totalInvitationsCount
      totalPendingCount.value = mainResponse?.totalPendingCount
      totalRejectedCount.value = mainResponse?.totalRejectedCount
      totalAcceptedCount.value = mainResponse?.totalAcceptedCount

      votingParticipatedCount.value = mainResponse?.votingParticipatedCount
      votingNotParticipatedCount.value = mainResponse?.votingNotParticipatedCount

      pendingInvitationList.value = mainResponse?.pendingInvitationList as ChallengeForTheUserDashboard[]
      acceptedInvitationList.value = mainResponse?.acceptedInvitationList as ChallengeForTheUserDashboard[]
      rejectedInvitationsList.value = mainResponse?.rejectedInvitationsList as ChallengeForTheUserDashboard[]
      completedInvitationsList.value = mainResponse?.completedInvitationsList as ChallengeForTheUserDashboard[]
    }

    const openApprovedConfirmationDialog = (requestId: number) => {
      handleOpenDialogForConfirmation(requestId, UserInvitationStatus.ACCEPTED)
    }

    const openCanceledConfirmationDialog = (requestId: number) => {
      handleOpenDialogForConfirmation(requestId, UserInvitationStatus.REJECTED)
    }

    const performAction = async () => {
      const challengeId = selectedItemId.value
      const reason = cancelReason.value
      if (!popupType.value) {
        return
      }

      try {
        isActionSubmitting.value = true
        await updateTheStatus(
          challengeId,
          popupType.value,
          popupType.value === UserInvitationStatus.REJECTED ? reason : undefined
        )
        closeActionDialog()
      } finally {
        isActionSubmitting.value = false
      }
    }

    const handleOpenDialogForConfirmation = (requestId: number, thePopupType: UserInvitationStatus) => {
      selectedItemId.value = requestId
      popupType.value = thePopupType
      if (thePopupType !== UserInvitationStatus.REJECTED) {
        cancelReason.value = ''
      }
      isActionDialogVisible.value = true
    }

    const closeActionDialog = () => {
      isActionDialogVisible.value = false
      selectedItemId.value = 0
      popupType.value = undefined
      cancelReason.value = ''
    }

    const handleAdminRequestRowClick = (item: any) => {
      console.log('Clicked item:', item)
      const challengeId = item.id
      console.log(challengeId)
      router.push({ name: 'ChallengeView', params: { challengeId } })
    }

    const updateTheStatus = async (challengeId: number, status: UserInvitationStatus, reason?: string) => {
      let url = `${URIS.USER_INVITATION.BASE}${URIS.USER_INVITATION.UPDATE_STATUS}${Characters.QUESTION_MARK}${Fields.CHALLENGE_ID}${Characters.EQUAL}${challengeId}${Characters.AND}${Fields.INVITATION_STATUS}${Characters.EQUAL}${status}`

      if (reason) {
        url += `${Characters.AND}${Fields.REASON}${Characters.EQUAL}${encodeURIComponent(reason)}`
      }

      const response = await update<SuccessResponse<any>>(url, null)
      if (response?.success) {
        toast.success(t('Messages.success.challenge.updated'))
        await loadData()
      } else {
        toast.success(response ? t(response.message) : '')
      }
    }

    const viewChallengeDashboard = (challengeId: number) => {
      console.log('challengeId is -> ' + challengeId)
      router.push({
        name: 'ChallengeDashboard',
        params: { challengeId }
      })
    }

    const setCurrentTab = (index: any) => {
      tab.value = index
      localStorage.setItem('currentUserDashboardTab', index)
      router.push({
        name: 'UserRequestsDashboard',
        params: { tab: index }
      })
    }

    // Function to initialize tab from the route
    const initializeTabFromRoute = () => {
      const routeTab = route.params.tab as string | undefined
      if (routeTab) {
        tab.value = routeTab // Use the tab from the URL
      } else {
        tab.value = 'one' // Default to 'one' if no tab in the URL
        // Optionally, push the default tab to the route
        router.replace({
          name: 'UserRequestsDashboard',
          params: { tab: tab.value }
        })
      }
    }

    const handleRefresh = async () => {
      await loadData()
    }

    onMounted(async () => {
      sidebarStore.disable()
      initializeTabFromRoute()
      await loadData()
      isLoading.value = false
    })

    const updateCurrentTab = (newTab: any) => {
      tab.value = newTab
    }

    watch(
      () => route.params.tab,
      (newTab: any) => {
        if (newTab) {
          tab.value = newTab as string
          localStorage.setItem('currentUserDashboardTab', newTab)
        }
      }
    )

    const isLanguageAr = (): boolean => {
      return employeeLang === 'ar'
    }

    const actionDialogTitle = ref('')
    const actionDialogMessage = ref('')

    watch(
      popupType,
      (newValue) => {
        if (newValue === UserInvitationStatus.ACCEPTED) {
          actionDialogTitle.value = t('myDashboard.dialog.accept.title')
          actionDialogMessage.value = t('myDashboard.dialog.accept.message')
          return
        }

        if (newValue === UserInvitationStatus.REJECTED) {
          actionDialogTitle.value = t('myDashboard.dialog.reject.title')
          actionDialogMessage.value = t('myDashboard.dialog.reject.message')
          return
        }

        actionDialogTitle.value = ''
        actionDialogMessage.value = ''
      },
      { immediate: true }
    )

    const actionDialogIcon = ref('mdi-help-circle-outline')
    const actionDialogColor = ref('primary')
    const actionConfirmText = ref('')

    watch(
      popupType,
      (newValue) => {
        if (newValue === UserInvitationStatus.ACCEPTED) {
          actionDialogIcon.value = 'mdi-check-decagram-outline'
          actionDialogColor.value = 'success'
          actionConfirmText.value = t('myDashboard.dialog.accept.confirm')
          return
        }

        if (newValue === UserInvitationStatus.REJECTED) {
          actionDialogIcon.value = 'mdi-close-octagon-outline'
          actionDialogColor.value = 'error'
          actionConfirmText.value = t('myDashboard.dialog.reject.confirm')
          return
        }

        actionDialogIcon.value = 'mdi-help-circle-outline'
        actionDialogColor.value = 'primary'
        actionConfirmText.value = t('common.confirm')
      },
      { immediate: true }
    )

    return {
      tab,
      dialog: false,
      isLanguageAr,
      totalInvitationsCount,
      totalAcceptedCount,
      totalPendingCount,
      totalRejectedCount,
      viewChallengeDashboard,
      votingParticipatedCount,
      votingNotParticipatedCount,
      pendingInvitationList,
      acceptedInvitationList,
      rejectedInvitationsList,
      completedInvitationsList,
      handleAdminRequestRowClick,
      openApprovedConfirmationDialog,
      openCanceledConfirmationDialog,
      performAction,
      setCurrentTab,
      validatorMainResponse,
      isValidatorOnly,
      handleRefresh,
      updateCurrentTab,
      cancelReason,
      popupType,
      isLoading,
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

<template src="./UserRequestsDashboard.html" lang="html"></template>
<style src="@/assets/css/requestsDashboard.scss" lang="scss"></style>
