<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { computed, defineComponent, nextTick, onMounted, watch } from 'vue'
import { useSidebarStore } from '@/store/sidebar'
import { useChallengeForm } from '@/composables/useChallengeForm'
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
import TeamMembersTab from '../tabs/TeamMembersTab.vue'
import ValidatorsTab from '../tabs/ValidatorsTab.vue'
import GuestInvitationTab from '../tabs/GuestInvitationTab.vue'
import ChallengeInfoTab from '../tabs/ChallengeInfoTab.vue'
import SolutionsTab from '../tabs/SolutionsTab.vue'
import ChallengeSettingsTab from '../tabs/ChallengeSettingsTab.vue'
import ResourcesTab from '../tabs/ResourcesTab.vue'

export default defineComponent({
  name: 'ChallengeCreation',
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
    const sidebarStore = useSidebarStore()

    // Use the skeleton loader composable
    const { showSkeleton, startAllLoading, stopAllLoading } = useSkeletonLoader()

    // Use the enhanced challenge form composable
    const {
      // Form state
      currentTab,
      progress,
      isSubmitting,
      isLoading,
      validationErrors,
      completedTabs,
      pendingTabs,

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
      addSolutionFromInput,
      handleSolutionKeydown,
      removeSolutionText,
      getValidSolutions,
      addYoutubeLink,
      removeYoutubeLink,
      addHashtag,
      removeHashtag,
      hashtagTexts,
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

      // Enhanced Vuetify file handling
      filesModel,
      uploadedFiles,
      removeFile,
      clearAllFiles,
      getFileIcon,
      getFileColor
    } = useChallengeForm(false)

    // Defensive computed for uploadedFiles
    const safeUploadedFiles = computed(() =>
      Array.isArray(uploadedFiles.value)
        ? uploadedFiles.value.filter((f) => f && typeof f === 'object' && typeof f.type === 'string')
        : []
    )

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

    // Initialization function
    const initializeChallengeData = async () => {
      try {
        startAllLoading()

        // Step 1: Load lookup data first
        await challengeCreationLookup()
        await committeeListEndPoint()
        await fetchModerators({ reset: true, searchAny: '' })
      } catch (error) {
        console.error('Error initializing challenge data:', error)
        // You might want to show a user-friendly error message here
      } finally {
        stopAllLoading()
      }
    }

    onMounted(async () => {
      await initializeChallengeData()
      sidebarStore.show()
    })

    // Add a watcher to sync challengeRequest.youtubeLinks with youtubeLinks
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
      files,
      docsIds,
      totalSize,
      totalSizePercent,
      isInvitationSectionCollapsed,
      inputText,
      newReminder,
      solutionsTexts,
      youtubeLinkInput,
      youtubeLinks,
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
      retrieveAllSubDepartmentsByDepartmentId,
      findAllSectionBySubDepartmentId,
      onStrategicDirectionChange,
      submitForm,
      cancelForm,
      selectAll,
      selectAllValidators,
      selectAllGuestUsers,
      addSolutionText,
      addSolutionFromInput,
      handleSolutionKeydown,
      removeSolutionText,
      getValidSolutions,
      addYoutubeLink,
      removeYoutubeLink,
      addHashtag,
      removeHashtag,
      hashtagTexts,
      addReminder,
      clearReminders,
      onSelectedFiles,
      onRemoveTemplatingFile,
      uploadEvent,
      getFileTypeImage,
      isImage,
      isTheTypeIsImage,
      isDocument,
      toggleInvitationSection,
      validateMandatoryField,
      validateMandatoryArrayField,
      rules,
      guestUsers,
      filesModel,
      safeUploadedFiles,
      removeFile,
      clearAllFiles,
      getFileIcon,
      getFileColor,
      handleFieldClick
    }
  }
})
</script>

<template src="./ChallengeCreation.html" lang="html"></template>
<style src="./ChallengeCreation.scss" lang="scss"></style>
