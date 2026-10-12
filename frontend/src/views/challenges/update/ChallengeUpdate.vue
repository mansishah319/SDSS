<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { computed, defineComponent, nextTick, onMounted, ref, watch } from 'vue'
import { useSidebarStore } from '@/store/sidebar.ts'
import { useChallengeForm } from '@/composables/useChallengeForm.ts'
import { useSkeletonLoader } from '@/composables/useSkeletonLoader'
import ChallengeProgressIndicator from '@/components/challenge/ChallengeProgressIndicator.vue'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import ChallengeSkeletonLoader from '@/components/common/ChallengeSkeletonLoader.vue'
import ContentLoader from '@/components/loader/ContentLoader.vue'
import powerpointImage from '@/assets/images/fileTypes/powerpoint.png'
import documentImage from '@/assets/images/fileTypes/document.png'
import excelImage from '@/assets/images/fileTypes/excel.png'
import wordImage from '@/assets/images/fileTypes/word.png'
import pdfImage from '@/assets/images/fileTypes/pdf.png'
import { Characters, URIS } from '@/constants/DpConstants.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { ChallengesResponse } from '@/dto/challenge/ChallengesResponse.ts'
import { mapFormState } from './mapper/ChallengeMapper.ts'
import { DocumentsChallengeResponse } from '@/dto/challenge/DocumentsChallengeResponse.ts'
import { deleteRequest, get } from '@/utils/GenericApiHelper'
import { DocumentsCreationResponse } from '@/dto/documents/DocumentsCreationResponse.ts'
import { useRoute } from 'vue-router'
import TeamMembersTab from '../tabs/TeamMembersTab.vue'
import ValidatorsTab from '../tabs/ValidatorsTab.vue'
import GuestInvitationTab from '../tabs/GuestInvitationTab.vue'
import ChallengeInfoTab from '../tabs/ChallengeInfoTab.vue'
import SolutionsTab from '../tabs/SolutionsTab.vue'
import ChallengeSettingsTab from '../tabs/ChallengeSettingsTab.vue'
import ResourcesTab from '../tabs/ResourcesTab.vue'

export default defineComponent({
  name: 'ChallengeUpdate',
  components: {
    GenericDashboardHeaderView,
    ChallengeProgressIndicator,
    ChallengeSkeletonLoader,
    ContentLoader,
    TeamMembersTab,
    ValidatorsTab,
    GuestInvitationTab,
    ChallengeInfoTab,
    SolutionsTab,
    ChallengeSettingsTab,
    ResourcesTab
  },
  setup() {
    const route = useRoute()
    const challengeId = route.params.id
    const sidebarStore = useSidebarStore()

    // Use the skeleton loader composable
    const { showSkeleton, startAllLoading, stopAllLoading } = useSkeletonLoader()

    // Use the enhanced challenge form composable in update mode
    const {
      // Form state
      currentTab,
      progress,
      isSubmitting,
      isLoading,
      validationErrors,
      completedTabs,
      pendingTabs,
      validateField,
      updatePendingTabs,
      formState,

      // Form data
      challengeRequest,

      // User selection
      selectedUsers,
      selectedValidatorUsers,
      selectedGuestUsers,
      selectedValidatorsItems,
      selectedTeamMembersItems,
      isAllSelected,
      isAllValidatorsSelected,
      isAllGuestUsersSelected,
      isTeamMembersLoading,
      isValidatorsLoading,
      teamMembersPagination,
      validatorsPagination,

      // Department selection
      selectedDepartment,
      selectedSubDepartment,
      selectedSection,
      selectedDepartmentForSearch,
      selectedDepartmentForValidators,

      // File state
      files,
      docsIds,
      totalSize,
      totalSizePercent,

      // Dynamic content
      inputText,
      newReminder,
      solutionsTexts,
      youtubeLinkInput,
      hashtagTexts,
      addYoutubeLink,
      removeYoutubeLink,
      youtubeLinks,

      // Lookup data
      teamMembersLookupResponse,
      validatorsLookupResponse,
      moderatorsLookupResponse,
      isModeratorsLoading,
      moderatorsSearchTerm,
      hasMoreModerators,
      expertisesResponseListLookupResponse,
      strategicGoalsResponseListLookupResponse,
      strategicDirectionResponseListLookupResponse,
      futureStrategicGoalsResponseListLookupResponse,
      futureStrategicDirectionResponseListLookupResponse,
      departmentDtoResponseList,
      subDepartmentDtoResponseList,
      sectionsDtoResponseList,
      committeeListDropListResponse,

      // Settings
      topicClassificationListLookupResponse,
      levelOfDecisionListLookupResponse,
      importanceOfDecisionListLookupResponse,
      impactListLookupResponse,
      fieldListLookupResponse,

      // Expert selection
      selectedExpertLevel,
      selectedExpertise,
      selectedExpertLevelForValidators,
      selectedExpertiseForValidators,

      // Committee selection
      selectedCommittee,

      // UI state
      isInvitationSectionCollapsed,

      // Guest users
      guestUsers,

      // Computed
      tabs,
      currentTabIndex,
      isFirstTab,
      isLastTab,
      currentTabConfig,
      nextTab,
      previousTab,
      canProceed,

      // Methods
      validateCurrentTab,
      validateAllTabs,
      goToNextTab,
      goToPreviousTab,
      goToTab,
      updateProgress,
      formatReminder,
      extractIdIfObject,
      formatSize,
      isFieldValid,

      // API operations
      challengeCreationLookup,
      committeeListEndPoint,
      fetchTeamMembers,
      fetchValidators,
      fetchModerators,
      seedModerators,
      retrieveAllSubDepartmentsByDepartmentId,
      findAllSectionBySubDepartmentId,
      onStrategicDirectionChange,

      // Form submission
      submitForm,
      cancelForm,

      // User selection
      selectAll,
      selectAllValidators,
      selectAllGuestUsers,

      // Dynamic content
      addSolutionText,
      removeSolutionText,
      addHashtag,
      removeHashtag,
      addReminder,
      clearReminders,

      // File handling
      onSelectedFiles,
      onRemoveTemplatingFile,
      uploadEvent,

      // UI helpers
      toggleInvitationSection,
      validateMandatoryField,
      validateMandatoryArrayField,

      // Rules for validation
      rules,

      // Additional state for update mode
      // File handling
      filesModel,
      uploadedFiles,
      removeFile,
      clearAllFiles,
      getFileIcon,
      getFileColor
    } = useChallengeForm(true, String(challengeId))

    const challengeDocsResponseList = ref<DocumentsChallengeResponse[]>([] as DocumentsChallengeResponse[])

    // File type helpers
    const getFileTypeImage = (fileType: string) => {
      switch (fileType.toLowerCase()) {
        case 'ppt':
        case 'pptx':
          return powerpointImage
        case 'doc':
        case 'docx':
          return wordImage
        case 'xls':
        case 'xlsx':
          return excelImage
        case 'pdf':
          return pdfImage
        default:
          return documentImage
      }
    }

    const isImage = (fileType: string) => {
      return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(fileType.toLowerCase())
    }

    const isTheTypeIsImage = (file: any) => {
      return isImage(file.type.split('/')[1])
    }

    const isDocument = (fileType: string) => {
      return ['pdf', 'doc', 'docx', 'ppt', 'pptx', 'xls', 'xlsx'].includes(fileType.toLowerCase())
    }

    // Load challenge data for update
    const viewChallengeDetailsForUpdate = async () => {
      try {
        const url = URIS.CHALLENGES.BASE + Characters.SLASH + challengeId
        const response = await get<SuccessResponse<ChallengesResponse>>(url)
        const viewResponseData = response?.data as ChallengesResponse

        // Log mapping summary for debugging
        // const mappingSummary = createMappingSummary(viewResponseData)

        // Map department and section selections
        if (viewResponseData.departmentId != undefined && !isNaN(Number(viewResponseData.departmentId))) {
          await retrieveAllSubDepartmentsByDepartmentId(Number(viewResponseData.departmentId))
        }
        if (viewResponseData.subDepartmentId != 'undefined' && !isNaN(Number(viewResponseData.subDepartmentId))) {
          await findAllSectionBySubDepartmentId(Number(viewResponseData.subDepartmentId))
        }

        // Use the comprehensive mapping function
        const mappedData = mapFormState(
          viewResponseData,
          departmentDtoResponseList.value,
          subDepartmentDtoResponseList.value,
          sectionsDtoResponseList.value,
          formatReminder,
          committeeListDropListResponse.value
        )

        // Apply mapped data to form a state
        challengeRequest.value = mappedData.challengeRequest
        solutionsTexts.value = mappedData.solutionsTexts
        docsIds.value = mappedData.docsIds
        challengeDocsResponseList.value = mappedData.challengeDocsResponseList
        selectedUsers.value = mappedData.selectedUsers
        selectedValidatorUsers.value = mappedData.selectedValidatorUsers
        selectedTeamMembersItems.value =
          viewResponseData.userInvitationCountedResponse?.invitedInternalUserResponse?.map((user) => ({
            ...user,
            grpNumber: String(user.grpNumber)
          })) || []
        selectedValidatorsItems.value =
          viewResponseData.validatorList?.map((user) => ({
            ...user,
            grpNumber: String(user.grpNumber)
          })) || []
        seedModerators(
          [viewResponseData.moderator, viewResponseData.assistantModerator].filter(Boolean).map((user: any) => ({
            ...user,
            grpNumber: String(user.grpNumber)
          }))
        )
        selectedDepartment.value = mappedData.selectedDepartment
        selectedSubDepartment.value = mappedData.selectedSubDepartment
        selectedSection.value = mappedData.selectedSection
        selectedDepartmentForSearch.value = null
        selectedDepartmentForValidators.value = null
        selectedExpertLevel.value = null
        selectedExpertise.value = null
        selectedExpertLevelForValidators.value = null
        selectedExpertiseForValidators.value = null
        selectedCommittee.value = mappedData.selectedCommittee || null
        youtubeLinks.value = mappedData.youtubeLinks

        // Map content arrays
        if (Array.isArray(mappedData.hashtagTexts)) {
          challengeRequest.value.hashtagTexts = mappedData.hashtagTexts
          hashtagTexts.value = mappedData.hashtagTexts
        }
        if (Array.isArray(mappedData.youtubeLinks)) {
          challengeRequest.value.youtubeLinks = mappedData.youtubeLinks
        }

        // Clear input fields for new input
        inputText.value = ''
        newReminder.value = ''
        youtubeLinkInput.value = ''
        filesModel.value = []
        uploadedFiles.value = []

        // Update progress after loading data
        updateProgress()

        // Validate all tabs to show errors for invalid tabs
        validateAllTabs()

        // Mark all tabs as completed and set progress to 100% if all fields are valid
        if (
          tabs.every((tab) =>
            tab.required ? !tab.validationFields.some((field) => validateField(field) !== null) : true
          )
        ) {
          formState.value.completedTabs = tabs.map((tab) => tab.id)
          formState.value.progress = 100
          updatePendingTabs()
        }
      } catch (error) {
        console.error('Error loading challenge details:', error)
      }
    }

    // Initialization function
    const initializeChallengeData = async () => {
      try {
        startAllLoading()

        // Step 1: Load lookup data first
        await challengeCreationLookup()
        await committeeListEndPoint()
        await fetchModerators({ reset: true, searchAny: '' })

        // Step 2: Then load challenge details
        await viewChallengeDetailsForUpdate()
      } catch (error) {
        console.error('Error initializing challenge data:', error)
        // You might want to show a user-friendly error message here
      } finally {
        stopAllLoading()
      }
    }

    // Document operations
    const removeDoc = async (index: number, doc?: DocumentsChallengeResponse) => {
      if (!doc || !doc.id) {
        console.warn('removeDoc called without valid doc:', doc)
        return
      }

      try {
        const response = await deleteRequest<SuccessResponse<DocumentsCreationResponse>>(
          `${URIS.DOCUMENTS.BASE}?documentId=${doc.id}`
        )
        if (response?.success) {
          challengeDocsResponseList.value.splice(index, 1)

          // Remove from docsIds
          const docIndex = docsIds.value.indexOf(doc.id)
          if (docIndex > -1) {
            docsIds.value.splice(docIndex, 1)
          }
        }
      } catch (error) {
        console.error('Error removing document:', error)
      }
    }

    const getFileSize = (base64String: any) => {
      let padding = 0
      if (base64String.endsWith('==')) padding = 2
      else if (base64String.endsWith('=')) padding = 1

      const base64Length = base64String.length
      const inBytes = base64Length * 0.75 - padding
      return formatBytes(inBytes)
    }

    const formatBytes = (bytes: any, decimals = 2) => {
      if (bytes === 0) return '0 Bytes'

      const k = 1024
      const dm = decimals < 0 ? 0 : decimals
      const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']

      const i = Math.floor(Math.log(bytes) / Math.log(k))

      return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i]
    }

    // Defensive computed for uploadedFiles
    const safeUploadedFiles = computed(() =>
      Array.isArray(uploadedFiles.value) ? uploadedFiles.value.filter((f) => f && typeof f === 'object') : []
    )

    const handleRemoveBackendDoc = async (payload: { index: number; file: DocumentsChallengeResponse }) => {
      const { index, file } = payload
      if (!file || !file.id) {
        console.warn('handleRemoveBackendDoc called without valid doc:', file)
        return
      }
      await removeDoc(index, file)
      // Remove from challengeRequest as well
      const idx = challengeRequest.value.documentsIds?.indexOf(file.id)
      if (idx > -1) {
        challengeRequest.value.documentsIds.splice(idx, 1)
      }
    }

    onMounted(async () => {
      await initializeChallengeData()
      sidebarStore.show()
    })

    // In setup, add a watcher to sync challengeRequest.value.youtubeLinks with youtubeLinks
    watch(youtubeLinks, (val) => {
      challengeRequest.value.youtubeLinks = val
    })

    // Handle field click navigation
    const handleFieldClick = (fieldName: string) => {
      nextTick(() => {
        const fieldSelectors = [
          `[data-field="${fieldName}"]`,
          `[name="${fieldName}"]`,
          `[id="${fieldName}"]`,
          `[data-testid="${fieldName}"]`,
          `input[name="${fieldName}"]`,
          `textarea[name="${fieldName}"]`,
          `select[name="${fieldName}"]`,
          `.v-autocomplete[data-field="${fieldName}"]`,
          `.v-text-field[data-field="${fieldName}"]`,
          `.v-textarea[data-field="${fieldName}"]`
        ]

        let targetElement: HTMLElement | null = null
        for (const selector of fieldSelectors) {
          targetElement = document.querySelector(selector) as HTMLElement
          if (targetElement) break
        }

        if (targetElement) {
          scrollAndFocusField(targetElement)
        } else {
          console.warn(`Field "${fieldName}" not found in the current tab`)
        }
      })
    }

    function scrollAndFocusField(targetElement: HTMLElement) {
      targetElement.scrollIntoView({
        behavior: 'smooth',
        block: 'center',
        inline: 'nearest'
      })
      setTimeout(() => {
        focusAndHighlightField(targetElement)
      }, 300)
    }

    function focusAndHighlightField(targetElement: HTMLElement) {
      const inputElement = targetElement?.querySelector('input, textarea, select') as HTMLElement
      if (inputElement) {
        inputElement.focus()
      } else {
        targetElement?.focus()
      }
      targetElement?.classList.add('field-highlight')
      setTimeout(() => {
        targetElement?.classList.remove('field-highlight')
      }, 2000)
    }

    return {
      // Skeleton loader state
      showSkeleton,

      // State
      currentTab,
      progress,
      isSubmitting,
      isLoading,
      validationErrors,
      completedTabs,
      pendingTabs,
      challengeRequest,
      selectedUsers,
      selectedValidatorUsers,
      selectedGuestUsers,
      selectedValidatorsItems,
      selectedTeamMembersItems,
      isAllSelected,
      isAllValidatorsSelected,
      isAllGuestUsersSelected,
      isTeamMembersLoading,
      isValidatorsLoading,
      teamMembersPagination,
      validatorsPagination,
      selectedDepartment,
      selectedSubDepartment,
      selectedSection,
      selectedDepartmentForSearch,
      selectedDepartmentForValidators,
      youtubeLinkInput,
      youtubeLinks,
      files,
      docsIds,
      totalSize,
      totalSizePercent,
      challengeDocsResponseList,
      isInvitationSectionCollapsed,
      inputText,
      newReminder,
      solutionsTexts,
      teamMembersLookupResponse,
      validatorsLookupResponse,
      moderatorsLookupResponse,
      isModeratorsLoading,
      moderatorsSearchTerm,
      hasMoreModerators,
      expertisesResponseListLookupResponse,
      strategicGoalsResponseListLookupResponse,
      strategicDirectionResponseListLookupResponse,
      futureStrategicGoalsResponseListLookupResponse,
      futureStrategicDirectionResponseListLookupResponse,
      departmentDtoResponseList,
      subDepartmentDtoResponseList,
      sectionsDtoResponseList,
      committeeListDropListResponse,
      topicClassificationListLookupResponse,
      levelOfDecisionListLookupResponse,
      importanceOfDecisionListLookupResponse,
      impactListLookupResponse,
      fieldListLookupResponse,
      selectedExpertLevel,
      selectedExpertise,
      selectedExpertLevelForValidators,
      selectedExpertiseForValidators,
      selectedCommittee,
      tabs,
      currentTabIndex,
      isFirstTab,
      isLastTab,
      currentTabConfig,
      nextTab,
      previousTab,
      canProceed,
      validateCurrentTab,
      validateAllTabs,
      goToNextTab,
      goToPreviousTab,
      goToTab,
      updateProgress,
      formatReminder,
      extractIdIfObject,
      formatSize,
      isFieldValid,
      challengeCreationLookup,
      committeeListEndPoint,
      fetchTeamMembers,
      fetchValidators,
      fetchModerators,
      seedModerators,
      retrieveAllSubDepartmentsByDepartmentId,
      findAllSectionBySubDepartmentId,
      onStrategicDirectionChange,
      submitForm,
      cancelForm,
      selectAll,
      selectAllValidators,
      selectAllGuestUsers,
      addSolutionText,
      removeSolutionText,
      addHashtag,
      removeHashtag,
      addReminder,
      clearReminders,
      onSelectedFiles,
      onRemoveTemplatingFile,
      uploadEvent,
      getFileTypeImage,
      isImage,
      isTheTypeIsImage,
      isDocument,
      removeDoc,
      getFileSize,
      formatBytes,
      toggleInvitationSection,
      validateMandatoryField,
      validateMandatoryArrayField,
      rules,
      guestUsers,
      safeUploadedFiles,
      filesModel,
      uploadedFiles,
      removeFile,
      clearAllFiles,
      getFileIcon,
      getFileColor,
      hashtagTexts,
      addYoutubeLink,
      removeYoutubeLink,
      handleRemoveBackendDoc,
      handleFieldClick
    }
  }
})
</script>

<template src="./ChallengeUpdate.html" lang="html"></template>
<style src="./ChallengeUpdate.scss" lang="scss"></style>
