import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { toast } from 'vue3-toastify'
import { ChallengesCreationRequest } from '@/dto/challenge/ChallengesCreationRequest.ts'
import { DropdownResponse } from '@/dto/lookup/general/DropdownResponse.ts'
import { DepartmentResponse } from '@/dto/department/DepartmentResponse.ts'
import { CommitteeResponse, getCommitteeId } from '@/dto/committee/CommitteeResponse.ts'
import { ChallengeLookupCreationScreenResponse } from '@/dto/lookup/challenge/ChallengeLookupCreationScreenResponse.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { ChallengesResponse } from '@/dto/challenge/ChallengesResponse.ts'
import { format } from 'date-fns'
import { createMultipart, get, updateMultipart } from '@/utils/GenericApiHelper'
import { URIS } from '@/constants/DpConstants.ts'
import { useRouter } from 'vue-router'
import moment from 'moment'
import UserClientService from '@/services/user/UserClientService'
import { UserMainResponse } from '@/dto/users/UserMainResponse.ts'
import { MainSystemRolesKeys } from '@/dto/enums/SystemRoles.ts'

export interface TabConfig {
  id: string
  label: string
  required: boolean
  validationFields: string[]
  icon?: string
}

export interface ValidationError {
  field: string
  message: string
  tabId: string
}

export interface FormState {
  currentTab: string
  progress: number
  isSubmitting: boolean
  isLoading: boolean
  validationErrors: ValidationError[]
  completedTabs: string[]
  pendingTabs: string[]
}

export interface UserSelectionState {
  selectedUsers: number[]
  selectedValidatorUsers: number[]
  selectedGuestUsers: string[]
  selectedValidatorsItems: UserMainResponse[]
  selectedTeamMembersItems: UserMainResponse[]
  isAllSelected: boolean
  isAllValidatorsSelected: boolean
  isAllGuestUsersSelected: boolean
}

export interface DepartmentSelectionState {
  selectedDepartment: DepartmentResponse | null
  selectedSubDepartment: DepartmentResponse | null
  selectedSection: DepartmentResponse | null
  selectedDepartmentForSearch: DepartmentResponse | null
  selectedDepartmentForValidators: DepartmentResponse | null
}

export interface FileState {
  files: any[]
  docsIds: number[]
  totalSize: number
  totalSizePercent: number
}

export interface DynamicContentState {
  inputText: string
  hashtags: string[]
  newReminder: string
  youtubeTextFields: Array<{ value: string }>
  solutionsTexts: Array<{ value: string }>
}

interface UserSearchPaginationState {
  page: number
  size: number
  totalElements: number
  sortField: string
  sortDirection: 'asc' | 'desc'
}

interface ChallengeFormDraft {
  formState: Pick<FormState, 'currentTab' | 'completedTabs'>
  challengeRequest: ChallengesCreationRequest
  selectedUsers: number[]
  selectedValidatorUsers: number[]
  selectedGuestUsers: string[]
  selectedValidatorsItems: UserMainResponse[]
  selectedTeamMembersItems: UserMainResponse[]
  selectedDepartment: DepartmentResponse | null
  selectedSubDepartment: DepartmentResponse | null
  selectedSection: DepartmentResponse | null
  selectedCommittee: CommitteeResponse | null
  inputText: string
  hashtagTexts: string[]
  newReminder: string
  youtubeLinkInput: string
  youtubeLinks: string[]
  solutionsTexts: Array<{ value: string }>
}

const CREATE_CHALLENGE_DRAFT_STORAGE_KEY = 'sdss.challenge.creation.draft'

const cloneSolutionsTexts = (items: Array<{ value: string }> | undefined | null): Array<{ value: string }> => {
  if (!Array.isArray(items)) {
    return []
  }

  return items.map((item) => ({ value: item?.value?.trim?.() ?? '' })).filter((item) => item.value)
}

const isBrowserEnvironment = () => typeof window !== 'undefined' && typeof window.localStorage !== 'undefined'

export const useChallengeForm = (isUpdateMode = false, challengeId?: string) => {
  const { t } = useI18n()
  const router = useRouter()

  // Tab configuration with enhanced validation and progress weights
  const tabs: TabConfig[] = [
    {
      id: 'one',
      label: 'challenge.info.teamMember',
      required: true,
      validationFields: ['selectedUsers'],
      icon: 'mdi-account-group'
    },
    {
      id: 'two',
      label: 'challenge.info.validators',
      required: true,
      validationFields: ['selectedValidatorUsers'],
      icon: 'mdi-shield-check'
    },
    // {
    //   id: 'three',
    //   label: 'challenge.guestInvitation',
    //   required: false,
    //   validationFields: [],
    //   icon: 'mdi-account-plus',
    // },
    {
      id: 'four',
      label: 'challenge.view',
      required: true,
      validationFields: [
        'titleEn',
        'titleAr',
        'descriptionEn',
        'descriptionAr',
        'startTime',
        'endTime',
        'visibility',
        'moderatorId',
        'allowImmediateAccess',
        'selectedDepartment'
      ],
      icon: 'mdi-eye'
    },
    {
      id: 'five',
      label: 'challenge.info.solutionsRateList',
      required: false,
      validationFields: [],
      icon: 'mdi-format-list-bulleted'
    },
    {
      id: 'six',
      label: 'challenge.info.challengeSettings',
      required: false,
      validationFields: [],
      icon: 'mdi-cog'
    },
    {
      id: 'seven',
      label: 'challenge.info.resources',
      required: false,
      validationFields: [],
      icon: 'mdi-folder-multiple'
    }
  ]

  // Progress weights for each tab (percentage contribution to total progress)
  const tabProgressWeights = {
    one: 15, // Team Members - 15%
    two: 15, // Validators - 15%
    three: 10, // Guest Invitation - 10%
    four: 25, // Challenge Info - 25% (most important)
    five: 15, // Solutions - 15%
    six: 10, // Challenge Settings - 10%
    seven: 10 // Resources - 10%
  }

  // Form state
  const formState = ref<FormState>({
    currentTab: 'one',
    progress: 0,
    isSubmitting: false,
    isLoading: false,
    validationErrors: [],
    completedTabs: [],
    pendingTabs: []
  })

  // Form data with proper typing
  const challengeRequest = ref<ChallengesCreationRequest>({
    invitedUsersIds: [],
    validatorsIds: [],
    moderatorId: null,
    assistantModeratorGrp: null,
    departmentId: '',
    departmentName: '',
    subDepartmentId: '',
    subDepartmentName: '',
    sectionId: '',
    sectionName: '',
    startTime: '',
    endTime: '',
    visibility: '',
    hashtagTexts: [],
    allowImmediateAccess: false,
    strategicGoalsId: null,
    strategicDirectionId: null,
    futureStrategicGoalsId: null,
    futureStrategicDirectionId: null,
    youtubeLinks: [],
    titleAr: '',
    titleEn: '',
    descriptionAr: '',
    descriptionEn: '',
    challengeRemindersDateTimeList: [],
    solutionsTexts: [],
    documentsIds: [],
    topicClassification: null,
    levelOfDecision: null,
    importanceOfDecision: null,
    impact: null,
    field: null,
    urgent: false,
    committeeId: null
  })

  // Individual refs for user selection - these can be returned directly
  const selectedUsers = ref<number[]>([])
  const selectedValidatorUsers = ref<number[]>([])
  const selectedGuestUsers = ref<string[]>([])
  const selectedValidatorsItems = ref<UserMainResponse[]>([])
  const selectedTeamMembersItems = ref<UserMainResponse[]>([])
  const isAllSelected = ref<boolean>(false)
  const isAllValidatorsSelected = ref<boolean>(false)
  const isAllGuestUsersSelected = ref<boolean>(false)
  const isTeamMembersLoading = ref<boolean>(false)
  const isValidatorsLoading = ref<boolean>(false)
  const isModeratorsLoading = ref<boolean>(false)
  const moderatorsSearchTerm = ref<string>('')
  const moderatorsPageNumber = ref<number>(0)
  const moderatorsPageSize = 20
  const hasMoreModerators = ref<boolean>(true)
  const teamMembersPagination = ref<UserSearchPaginationState>({
    page: 0,
    size: 10,
    totalElements: 0,
    sortField: 'fullName',
    sortDirection: 'asc'
  })
  const validatorsPagination = ref<UserSearchPaginationState>({
    page: 0,
    size: 10,
    totalElements: 0,
    sortField: 'fullName',
    sortDirection: 'asc'
  })

  // Individual refs for department selection
  const selectedDepartment = ref<DepartmentResponse | null>(null)
  const selectedSubDepartment = ref<DepartmentResponse | null>(null)
  const selectedSection = ref<DepartmentResponse | null>(null)
  const selectedDepartmentForSearch = ref<DepartmentResponse | null>(null)
  const selectedDepartmentForValidators = ref<DepartmentResponse | null>(null)

  // Individual refs for file state
  const files = ref<any[]>([])
  const docsIds = ref<number[]>([])
  const totalSize = ref<number>(0)
  const totalSizePercent = ref<number>(0)

  // Individual refs for dynamic content
  const inputText = ref<string>('')
  const hashtagTexts = ref<string[]>([])
  const newReminder = ref<string>('')
  const youtubeLinkInput = ref<string>('')
  const youtubeLinks = ref<string[]>([])
  const solutionsTexts = ref<Array<{ value: string }>>([])

  // Individual refs for expert selection
  const selectedExpertLevel = ref<DropdownResponse | null>(null)
  const selectedExpertise = ref<DropdownResponse | null>(null)
  const selectedExpertLevelForValidators = ref<DropdownResponse | null>(null)
  const selectedExpertiseForValidators = ref<DropdownResponse | null>(null)

  // Committee selection
  const selectedCommittee = ref<CommitteeResponse | null>(null)

  // Individual refs for UI state
  const isInvitationSectionCollapsed = ref<boolean>(false)
  const guestUsers = ref<UserMainResponse[]>([])

  // Lookup data
  const lookupData = ref({
    teamMembersLookupResponse: [] as UserMainResponse[],
    validatorsLookupResponse: [] as UserMainResponse[],
    moderatorsLookupResponse: [] as UserMainResponse[],
    expertisesResponseListLookupResponse: [] as DropdownResponse[],
    strategicGoalsResponseListLookupResponse: [] as DropdownResponse[],
    strategicDirectionResponseListLookupResponse: [] as DropdownResponse[],
    futureStrategicGoalsResponseListLookupResponse: [] as DropdownResponse[],
    futureStrategicDirectionResponseListLookupResponse: [] as DropdownResponse[],
    departmentDtoResponseList: [] as DepartmentResponse[],
    subDepartmentDtoResponseList: [] as DepartmentResponse[],
    sectionsDtoResponseList: [] as DepartmentResponse[],
    committeeListDropListResponse: [] as CommitteeResponse[],
    topicClassificationListLookupResponse: [] as DropdownResponse[],
    levelOfDecisionListLookupResponse: [] as DropdownResponse[],
    importanceOfDecisionListLookupResponse: [] as DropdownResponse[],
    impactListLookupResponse: [] as DropdownResponse[],
    fieldListLookupResponse: [] as DropdownResponse[]
  })

  // Computed properties for enhanced navigation
  const currentTabIndex = computed(() => tabs.findIndex((tab) => tab.id === formState.value.currentTab))
  const isFirstTab = computed(() => currentTabIndex.value === 0)
  const isLastTab = computed(() => currentTabIndex.value === tabs.length - 1)
  const currentTabConfig = computed(() => tabs[currentTabIndex.value])
  const nextTab = computed(() => tabs[currentTabIndex.value + 1])
  const previousTab = computed(() => tabs[currentTabIndex.value - 1])

  // Helper for time validation
  function validateStartTime(startTimeStr: string): string | null {
    if (!startTimeStr) return t('error.mandatory')
    const startTime = moment(startTimeStr, 'YYYY-MM-DD HH:mm:ss')
    const currentTime = moment()
    if (startTime.isSameOrBefore(currentTime)) {
      return t('challenge.error.startTimeMustBeAfterCurrent')
    }
    return null
  }

  function validateEndTime(endTimeStr: string, startTimeStr?: string): string | null {
    if (!endTimeStr) return t('error.mandatory')
    const endTime = moment(endTimeStr, 'YYYY-MM-DD HH:mm:ss')
    const currentTime = moment()
    if (endTime.isSameOrBefore(currentTime)) {
      return t('challenge.error.endTimeMustBeAfterCurrent')
    }
    if (startTimeStr) {
      const startTime = moment(startTimeStr, 'YYYY-MM-DD HH:mm:ss')
      if (endTime.isSameOrBefore(startTime)) {
        return t('challenge.error.endTimeMustBeAfterStartTime')
      }
    }
    return null
  }

  const simpleFieldChecks: Record<string, () => string | null> = {
    selectedUsers: () => (selectedUsers.value.length === 0 ? t('error.mandatory') : null),
    selectedValidatorUsers: () => (selectedValidatorUsers.value.length === 0 ? t('error.mandatory') : null),
    titleEn: () => (!challengeRequest.value.titleEn?.trim() ? t('error.mandatory') : null),
    titleAr: () => (!challengeRequest.value.titleAr?.trim() ? t('error.mandatory') : null),
    descriptionEn: () => (!challengeRequest.value.descriptionEn?.trim() ? t('error.mandatory') : null),
    descriptionAr: () => (!challengeRequest.value.descriptionAr?.trim() ? t('error.mandatory') : null),
    visibility: () => (!challengeRequest.value.visibility ? t('error.mandatory') : null),
    moderatorId: () => (!challengeRequest.value.moderatorId ? t('error.mandatory') : null),
    allowImmediateAccess: () => (challengeRequest.value.allowImmediateAccess === null ? t('error.mandatory') : null),
    selectedDepartment: () => (!selectedDepartment.value ? t('error.mandatory') : null)
  }

  const validateField = (field: string): string | null => {
    if (field === 'startTime') {
      return validateStartTime(challengeRequest.value.startTime)
    }
    if (field === 'endTime') {
      return validateEndTime(challengeRequest.value.endTime, challengeRequest.value.startTime)
    }
    if (simpleFieldChecks[field]) {
      return simpleFieldChecks[field]()
    }
    return null
  }

  const validateCurrentTab = (): boolean => {
    const currentTab = currentTabConfig.value
    if (!currentTab.required) return true

    const errors: ValidationError[] = []
    currentTab.validationFields.forEach((field) => {
      const error = validateField(field)
      if (error) {
        errors.push({
          field,
          message: error,
          tabId: currentTab.id
        })
      }
    })

    formState.value.validationErrors = errors
    return errors.length === 0
  }

  const validateAllTabs = (): ValidationError[] => {
    const allErrors: ValidationError[] = []

    tabs.forEach((tab) => {
      if (tab.required) {
        tab.validationFields.forEach((field) => {
          const error = validateField(field)
          if (error) {
            allErrors.push({
              field,
              message: error,
              tabId: tab.id
            })
          }
        })
      }
    })

    formState.value.validationErrors = allErrors
    return allErrors
  }

  // Enhanced navigation with validation
  const goToNextTab = (): boolean => {
    // In update mode, block navigation if any required tab up to the current one has errors
    if (isUpdateMode) {
      const currentIdx = currentTabIndex.value
      for (let i = 0; i <= currentIdx; i++) {
        const tab = tabs[i]
        if (tab.required && tab.validationFields.some((field) => validateField(field) !== null)) {
          toast.error(t('error.pleaseCompleteAllTabsBeforeProceeding'))
          return false
        }
      }
    }
    if (!validateCurrentTab()) {
      toast.error(t('error.pleaseCompleteCurrentTab'))
      return false
    }

    // Mark the current tab as completed before moving to the next
    const currentTabId = formState.value.currentTab
    if (!formState.value.completedTabs.includes(currentTabId)) {
      formState.value.completedTabs.push(currentTabId)
    }

    if (isLastTab.value) return false

    const nextTabIndex = currentTabIndex.value + 1
    formState.value.currentTab = tabs[nextTabIndex].id
    return true
  }

  const goToPreviousTab = (): boolean => {
    if (isFirstTab.value) return false

    const previousTabIndex = currentTabIndex.value - 1
    formState.value.currentTab = tabs[previousTabIndex].id
    return true
  }

  const goToTab = (tabId: string): boolean => {
    const targetTabIndex = tabs.findIndex((tab) => tab.id === tabId)
    if (targetTabIndex === -1) return false

    // Check if we can access this tab (all previous tabs must be completed)
    for (let i = 0; i < targetTabIndex; i++) {
      const tab = tabs[i]
      if (tab.required) {
        // Check if the tab is completed or has no validation errors
        const isCompleted = formState.value.completedTabs.includes(tab.id)
        const hasErrors = tab.validationFields.some((field) => validateField(field) !== null)
        if (!isCompleted && hasErrors) {
          toast.error(t('error.pleaseCompletePreviousTabs'))
          return false
        }
      }
    }

    // Mark the current tab as completed if we're moving forward
    const currentTabIndex = tabs.findIndex((tab) => tab.id === formState.value.currentTab)
    if (targetTabIndex > currentTabIndex) {
      const currentTabId = formState.value.currentTab
      if (!formState.value.completedTabs.includes(currentTabId)) {
        formState.value.completedTabs.push(currentTabId)
      }
    }

    formState.value.currentTab = tabId
    return true
  }

  // Enhanced progress calculation based on completed stages
  const progress = computed(() => {
    let totalProgress = 0
    const progressDetails: Record<string, { weight: number; status: string; contribution: number }> = {}

    tabs.forEach((tab) => {
      const tabWeight = tabProgressWeights[tab.id as keyof typeof tabProgressWeights] || 0
      let contribution = 0
      let status = 'empty'

      if (isTabCompleted(tab.id)) {
        // Tab is fully completed - add full weight
        contribution = tabWeight
        status = 'completed'
      } else if (isTabPending(tab.id)) {
        // Tab has data but not completed - add partial weight (50% of tab weight)
        contribution = tabWeight * 0.5
        status = 'pending'
      } else if (tab.required && isTabDataFilled(tab.id)) {
        // Required tab has data filled - add partial weight (75% of tab weight)
        contribution = tabWeight * 0.75
        status = 'filled'
      }
      // Optional tabs with no data contribute 0% to progress

      totalProgress += contribution
      progressDetails[tab.id] = { weight: tabWeight, status, contribution }
    })

    const finalProgress = Math.round(totalProgress)

    // Debug logging (only in development)
    if (process.env.NODE_ENV === 'development') {
      console.log('Progress Calculation:', {
        totalProgress: finalProgress,
        details: progressDetails,
        completedTabs: formState.value.completedTabs,
        pendingTabs: formState.value.pendingTabs
      })
    }

    return finalProgress
  })

  // Debug a function to get progress details (for development/testing)
  const getProgressDetails = () => {
    const details: Record<
      string,
      {
        weight: number
        status: string
        contribution: number
        isCompleted: boolean
        isPending: boolean
        isDataFilled: boolean
      }
    > = {}

    tabs.forEach((tab) => {
      const tabWeight = tabProgressWeights[tab.id as keyof typeof tabProgressWeights] || 0
      const isCompleted = isTabCompleted(tab.id)
      const isPending = isTabPending(tab.id)
      const isDataFilled = isTabDataFilled(tab.id)

      let contribution = 0
      let status = 'empty'

      if (isCompleted) {
        contribution = tabWeight
        status = 'completed'
      } else if (isPending) {
        contribution = tabWeight * 0.5
        status = 'pending'
      } else if (tab.required && isDataFilled) {
        contribution = tabWeight * 0.75
        status = 'filled'
      }

      details[tab.id] = {
        weight: tabWeight,
        status,
        contribution,
        isCompleted,
        isPending,
        isDataFilled
      }
    })

    return details
  }

  // Function to check if a tab has mandatory data filled
  const isTabDataFilled = (tabId: string): boolean => {
    const tab = tabs.find((t) => t.id === tabId)
    if (!tab) return false

    // For non-required tabs, consider them filled if they have any data
    if (!tab.required) {
      switch (tabId) {
        case 'three': // Guest Invitation
          return selectedGuestUsers.value.length > 0
        case 'five': // Solutions
          return getValidSolutions().length > 0
        case 'six': // Challenge Settings
          return Boolean(
            challengeRequest.value.topicClassification ??
            challengeRequest.value.levelOfDecision ??
            challengeRequest.value.importanceOfDecision ??
            challengeRequest.value.impact ??
            challengeRequest.value.field
          )
        case 'seven': // Resources
          return (
            uploadedFiles.value.length > 0 ||
            (docsIds.value?.length ?? 0) > 0 ||
            youtubeLinks.value.length > 0 ||
            hashtagTexts.value.length > 0 ||
            (challengeRequest.value.challengeRemindersDateTimeList?.length ?? 0) > 0
          )
        default:
          return false
      }
    }

    // For required tabs, check if all validation fields are filled
    const hasErrors = tab.validationFields?.some((field) => validateField(field) !== null) ?? false
    return !hasErrors && (tab.validationFields?.length ?? 0) > 0
  }

  const buildDraftSnapshot = (): ChallengeFormDraft => ({
    formState: {
      currentTab: formState.value.currentTab,
      completedTabs: [...formState.value.completedTabs]
    },
    challengeRequest: {
      ...challengeRequest.value,
      invitedUsersIds: [...selectedUsers.value],
      validatorsIds: [...selectedValidatorUsers.value],
      hashtagTexts: [...hashtagTexts.value],
      youtubeLinks: [...youtubeLinks.value],
      challengeRemindersDateTimeList: [...challengeRequest.value.challengeRemindersDateTimeList],
      solutionsTexts: getValidSolutions()
    },
    selectedUsers: [...selectedUsers.value],
    selectedValidatorUsers: [...selectedValidatorUsers.value],
    selectedGuestUsers: [...selectedGuestUsers.value],
    selectedValidatorsItems: [...selectedValidatorsItems.value],
    selectedTeamMembersItems: [...selectedTeamMembersItems.value],
    selectedDepartment: selectedDepartment.value,
    selectedSubDepartment: selectedSubDepartment.value,
    selectedSection: selectedSection.value,
    selectedCommittee: selectedCommittee.value,
    inputText: inputText.value,
    hashtagTexts: [...hashtagTexts.value],
    newReminder: newReminder.value,
    youtubeLinkInput: youtubeLinkInput.value,
    youtubeLinks: [...youtubeLinks.value],
    solutionsTexts: cloneSolutionsTexts(solutionsTexts.value)
  })

  const persistChallengeDraft = () => {
    if (isUpdateMode || !isBrowserEnvironment()) {
      return
    }

    window.localStorage.setItem(CREATE_CHALLENGE_DRAFT_STORAGE_KEY, JSON.stringify(buildDraftSnapshot()))
  }

  const restoreChallengeDraft = () => {
    if (isUpdateMode || !isBrowserEnvironment()) {
      return
    }

    const rawDraft = window.localStorage.getItem(CREATE_CHALLENGE_DRAFT_STORAGE_KEY)
    if (!rawDraft) {
      return
    }

    try {
      const draft = JSON.parse(rawDraft) as Partial<ChallengeFormDraft>
      if (!draft || !draft.challengeRequest) {
        return
      }

      challengeRequest.value = {
        ...challengeRequest.value,
        ...draft.challengeRequest,
        hashtagTexts: Array.isArray(draft.challengeRequest.hashtagTexts)
          ? [...draft.challengeRequest.hashtagTexts]
          : [],
        youtubeLinks: Array.isArray(draft.challengeRequest.youtubeLinks)
          ? [...draft.challengeRequest.youtubeLinks]
          : [],
        challengeRemindersDateTimeList: Array.isArray(draft.challengeRequest.challengeRemindersDateTimeList)
          ? [...draft.challengeRequest.challengeRemindersDateTimeList]
          : [],
        solutionsTexts: Array.isArray(draft.challengeRequest.solutionsTexts)
          ? [...draft.challengeRequest.solutionsTexts]
          : []
      }

      formState.value.currentTab = draft.formState?.currentTab || formState.value.currentTab
      formState.value.completedTabs = Array.isArray(draft.formState?.completedTabs)
        ? [...draft.formState.completedTabs]
        : []

      selectedUsers.value = Array.isArray(draft.selectedUsers) ? [...draft.selectedUsers] : []
      selectedValidatorUsers.value = Array.isArray(draft.selectedValidatorUsers)
        ? [...draft.selectedValidatorUsers]
        : []
      selectedGuestUsers.value = Array.isArray(draft.selectedGuestUsers) ? [...draft.selectedGuestUsers] : []
      selectedValidatorsItems.value = Array.isArray(draft.selectedValidatorsItems)
        ? [...draft.selectedValidatorsItems]
        : []
      selectedTeamMembersItems.value = Array.isArray(draft.selectedTeamMembersItems)
        ? [...draft.selectedTeamMembersItems]
        : []

      selectedDepartment.value = draft.selectedDepartment ?? null
      selectedSubDepartment.value = draft.selectedSubDepartment ?? null
      selectedSection.value = draft.selectedSection ?? null
      selectedDepartmentForSearch.value = null
      selectedDepartmentForValidators.value = null
      selectedExpertLevel.value = null
      selectedExpertise.value = null
      selectedExpertLevelForValidators.value = null
      selectedExpertiseForValidators.value = null
      selectedCommittee.value = draft.selectedCommittee ?? null

      inputText.value = draft.inputText ?? ''
      hashtagTexts.value = Array.isArray(draft.hashtagTexts) ? [...draft.hashtagTexts] : []
      newReminder.value = draft.newReminder ?? ''
      youtubeLinkInput.value = draft.youtubeLinkInput ?? ''
      youtubeLinks.value = Array.isArray(draft.youtubeLinks) ? [...draft.youtubeLinks] : []
      solutionsTexts.value = cloneSolutionsTexts(draft.solutionsTexts)
    } catch (error) {
      console.error('Error restoring challenge draft:', error)
      window.localStorage.removeItem(CREATE_CHALLENGE_DRAFT_STORAGE_KEY)
    }
  }

  const clearChallengeDraft = () => {
    if (isUpdateMode || !isBrowserEnvironment()) {
      return
    }

    window.localStorage.removeItem(CREATE_CHALLENGE_DRAFT_STORAGE_KEY)
  }

  // Function to update pending tabs based on current data
  const updatePendingTabs = () => {
    const newPendingTabs: string[] = []

    tabs.forEach((tab) => {
      // If the tab is not completed but has mandatory data filled, mark as pending
      if (!formState.value.completedTabs.includes(tab.id) && isTabDataFilled(tab.id)) {
        newPendingTabs.push(tab.id)
      }
    })

    formState.value.pendingTabs = newPendingTabs
  }

  // Function to check if a tab is completed
  const isTabCompleted = (tabId: string): boolean => {
    // A tab is completed if it's in the completedTabs array
    return formState.value.completedTabs.includes(tabId)
  }

  // Function to check if a tab is pending
  const isTabPending = (tabId: string): boolean => {
    // A tab is pending if it has mandatory data filled but is not completed
    return formState.value.pendingTabs.includes(tabId)
  }

  // Function to get tab status
  const getTabStatus = (tabId: string): 'completed' | 'error' | 'pending' | 'initial' => {
    const tab = tabs.find((t) => t.id === tabId)
    if (!tab) return 'pending'

    if (isTabCompleted(tabId)) return 'completed'

    const hasErrors = formState.value.validationErrors.some((error) => error.tabId === tabId)
    if (hasErrors) return 'error'

    if (isTabPending(tabId)) return 'pending'

    if (tab.required) return 'initial'
    return 'pending'
  }

  // Enhanced field validation helper
  const isFieldValid = (field: string): boolean => {
    return validateField(field) === null
  }

  // Computed property for navigation control
  const canProceed = computed(() => {
    return validateCurrentTab()
  })

  // Utility functions
  const formatReminder = (dateTime: string): string => {
    return format(new Date(dateTime), 'yyyy-MM-dd\'T\'HH:mm')
  }

  const extractIdIfObject = (obj: any): any => {
    return typeof obj === 'object' && obj !== null ? obj.id : obj
  }

  const formatSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes'
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  // API operations
  const challengeCreationLookup = async () => {
    try {
      formState.value.isLoading = true
      await get<SuccessResponse<ChallengeLookupCreationScreenResponse>>(
        URIS.LOOKUP.BASE + URIS.LOOKUP.CHALLENGES.CREATION
      ).then((response: SuccessResponse<ChallengeLookupCreationScreenResponse>) => {
        if (response?.success && response.data) {
          const data = response.data
          lookupData.value.moderatorsLookupResponse = data.moderators || []
          lookupData.value.expertisesResponseListLookupResponse = data.expertisesResponseList || []
          lookupData.value.strategicGoalsResponseListLookupResponse = data.strategicGoalsResponseList || []
          lookupData.value.strategicDirectionResponseListLookupResponse = data.strategicDirectionResponseList || []
          lookupData.value.futureStrategicGoalsResponseListLookupResponse = data.futureStrategicGoalsResponseList || []
          lookupData.value.futureStrategicDirectionResponseListLookupResponse =
            data.futureStrategicDirectionResponseList || []
          lookupData.value.departmentDtoResponseList = data.departmentResponseList || []
          lookupData.value.topicClassificationListLookupResponse = data.topicClassificationList || []
          lookupData.value.levelOfDecisionListLookupResponse = data.levelOfDecisionList || []
          lookupData.value.importanceOfDecisionListLookupResponse = data.importanceOfDecisionList || []
          lookupData.value.impactListLookupResponse = data.impactList || []
          lookupData.value.fieldListLookupResponse = data.fieldList || []
        }
      })
    } catch (error) {
      console.error('Error fetching challenge creation lookup:', error)
      toast.error(t('error.failedToLoadData'))
    } finally {
      formState.value.isLoading = false
    }
  }

  const committeeListEndPoint = async () => {
    try {
      await get<SuccessResponse<CommitteeResponse[]>>(URIS.COMMITTEE.BASE + URIS.COMMITTEE.DROPDOWN_LIST).then(
        (response) => {
          if (response?.success && response.data) {
            lookupData.value.committeeListDropListResponse = response.data
          }
        }
      )
    } catch (error) {
      console.error('Error fetching committee list:', error)
    }
  }

  const resolveExpertLevelSearchValue = (expertLevel: DropdownResponse | null): string | undefined => {
    if (!expertLevel) return undefined
    return expertLevel.id != null ? String(expertLevel.id) : undefined
  }

  const normalizeSearchedUsers = (users: UserMainResponse[] = []): UserMainResponse[] => {
    return users
      .filter((user) => user?.grpNumber)
      .map((user) => ({
        ...user,
        shortDepartmentName: user.shortDepartmentName || user.shortName
      }))
  }

  const mergeUsersByGrpNumber = (existingUsers: UserMainResponse[], incomingUsers: UserMainResponse[]) => {
    return Array.from(
      new Map(
        [...existingUsers, ...incomingUsers]
          .filter((user) => user?.grpNumber)
          .map((user) => [Number(user.grpNumber), user])
      ).values()
    )
  }

  const seedModerators = (moderators: UserMainResponse[] = []) => {
    lookupData.value.moderatorsLookupResponse = mergeUsersByGrpNumber(
      lookupData.value.moderatorsLookupResponse,
      normalizeSearchedUsers(moderators)
    )
  }

  const fetchTeamMembers = async (filters?: {
    department?: DepartmentResponse | null
    expertLevel?: DropdownResponse | null
    expertise?: DropdownResponse | null
    page?: number
    size?: number
    sortField?: string
    sortDirection?: 'asc' | 'desc'
  }) => {
    try {
      isTeamMembersLoading.value = true
      const page = filters?.page ?? teamMembersPagination.value.page
      const size = filters?.size ?? teamMembersPagination.value.size
      const sortField = filters?.sortField ?? teamMembersPagination.value.sortField
      const sortDirection = filters?.sortDirection ?? teamMembersPagination.value.sortDirection
      const response = await UserClientService.search(
        {
          department: filters?.department?.shortName || selectedDepartmentForSearch.value?.shortName,
          expertLevel: resolveExpertLevelSearchValue(filters?.expertLevel || selectedExpertLevel.value),
          areaOfExpertises: filters?.expertise?.name || selectedExpertise.value?.name,
          status: '1'
        },
        page,
        size,
        sortField,
        sortDirection
      )

      lookupData.value.teamMembersLookupResponse = normalizeSearchedUsers(response?.data || [])
      teamMembersPagination.value = {
        page,
        size,
        sortField,
        sortDirection,
        totalElements: Number(response?.totalElements ?? response?.data?.length ?? 0)
      }
    } catch (error) {
      console.error('Error fetching team members:', error)
    } finally {
      isTeamMembersLoading.value = false
    }
  }

  const fetchValidators = async (filters?: {
    department?: DepartmentResponse | null
    expertLevel?: DropdownResponse | null
    expertise?: DropdownResponse | null
    page?: number
    size?: number
    sortField?: string
    sortDirection?: 'asc' | 'desc'
  }) => {
    try {
      isValidatorsLoading.value = true
      const page = filters?.page ?? validatorsPagination.value.page
      const size = filters?.size ?? validatorsPagination.value.size
      const sortField = filters?.sortField ?? validatorsPagination.value.sortField
      const sortDirection = filters?.sortDirection ?? validatorsPagination.value.sortDirection
      const response = await UserClientService.search(
        {
          department: filters?.department?.shortName || selectedDepartmentForValidators.value?.shortName,
          expertLevel: resolveExpertLevelSearchValue(filters?.expertLevel || selectedExpertLevelForValidators.value),
          areaOfExpertises: filters?.expertise?.name || selectedExpertiseForValidators.value?.name,
          role: MainSystemRolesKeys._VALIDATOR,
          status: '1'
        },
        page,
        size,
        sortField,
        sortDirection
      )

      lookupData.value.validatorsLookupResponse = normalizeSearchedUsers(response?.data || [])
      validatorsPagination.value = {
        page,
        size,
        sortField,
        sortDirection,
        totalElements: Number(response?.totalElements ?? response?.data?.length ?? 0)
      }
    } catch (error) {
      console.error('Error fetching validators:', error)
    } finally {
      isValidatorsLoading.value = false
    }
  }

  const fetchModerators = async (options?: { reset?: boolean; searchAny?: string }) => {
    const shouldReset = options?.reset ?? false
    const searchAny = options?.searchAny ?? moderatorsSearchTerm.value

    if (isModeratorsLoading.value || (!hasMoreModerators.value && !shouldReset)) {
      return
    }

    try {
      isModeratorsLoading.value = true

      if (shouldReset) {
        moderatorsPageNumber.value = 0
        hasMoreModerators.value = true
      }

      const response = await UserClientService.search(
        {
          role: MainSystemRolesKeys._MODERATOR,
          status: '1',
          searchAny: searchAny || undefined
        },
        moderatorsPageNumber.value,
        moderatorsPageSize,
        'fullName',
        'asc'
      )

      const normalizedModerators = normalizeSearchedUsers(response?.data || [])
      lookupData.value.moderatorsLookupResponse = shouldReset
        ? normalizedModerators
        : mergeUsersByGrpNumber(lookupData.value.moderatorsLookupResponse, normalizedModerators)

      const totalElements = Number(response?.totalElements || 0)
      const loadedItems = lookupData.value.moderatorsLookupResponse.length
      hasMoreModerators.value = loadedItems < totalElements

      if (normalizedModerators.length > 0) {
        moderatorsPageNumber.value += 1
      } else {
        hasMoreModerators.value = false
      }
    } catch (error) {
      console.error('Error fetching moderators:', error)
    } finally {
      isModeratorsLoading.value = false
    }
  }

  const retrieveAllSubDepartmentsByDepartmentId = async (departmentId: number) => {
    try {
      const response = await get<SuccessResponse<DepartmentResponse[]>>(
        URIS.CORE.BASE + URIS.CORE.DEPARTMENT_SUB_DEPARTMENT + '?departmentId=' + departmentId
      )

      if (response?.success && response.data) {
        lookupData.value.subDepartmentDtoResponseList = response.data
      }
    } catch (error) {
      console.error('Error fetching sub departments:', error)
    }
  }

  const findAllSectionBySubDepartmentId = async (subDepartmentId: number) => {
    try {
      const response = await get<SuccessResponse<DepartmentResponse[]>>(
        URIS.CORE.BASE + URIS.CORE.DEPARTMENT_SECTIONS + '?subDepartmentId=' + subDepartmentId
      )

      if (response?.success && response.data) {
        lookupData.value.sectionsDtoResponseList = response.data
      }
    } catch (error) {
      console.error('Error fetching sections:', error)
    }
  }

  const onStrategicDirectionChange = async (newDirectionId: number) => {
    try {
      const response = await get<SuccessResponse<DropdownResponse[]>>(
        URIS.STRATEGIC_DIRECTION.BASE + URIS.STRATEGIC_DIRECTION.ALL_GOALS + '?strategicDirectionId=' + newDirectionId
      )

      if (response?.success && response.data) {
        lookupData.value.strategicGoalsResponseListLookupResponse = response.data
      }
    } catch (error) {
      console.error('Error fetching strategic goals:', error)
    }
  }

  const resolveSubmissionErrorMessage = (error: any, response?: SuccessResponse<ChallengesResponse> | null): string => {
    const responseMessage = response?.message || error?.response?.data?.message

    if (typeof responseMessage === 'string' && responseMessage.trim()) {
      const translated = t(responseMessage)
      return translated === responseMessage ? responseMessage : translated
    }

    if (error?.message?.startsWith('Unsafe file blocked')) {
      return t('error.uploadFailed')
    }

    return t('error.submissionFailed')
  }

  // Form submission
  const submitForm = async (): Promise<boolean> => {
    try {
      formState.value.isSubmitting = true

      const request = {
        ...challengeRequest.value,
        invitedUsersIds: selectedUsers.value,
        validatorsIds: selectedValidatorUsers.value,
        documentsIds: docsIds.value,
        solutionsTexts: getValidSolutions(),
        hashtagTexts: hashtagTexts.value,
        challengeRemindersDateTimeList: challengeRequest.value.challengeRemindersDateTimeList,
        youtubeLinks: youtubeLinks.value,
        committeeId: getCommitteeId(selectedCommittee.value)
      }

      let response: SuccessResponse<ChallengesResponse> | null
      const filesToUpload = uploadedFiles.value.map((f) => f.file)

      if (isUpdateMode && challengeId) {
        response = await updateMultipart<SuccessResponse<ChallengesResponse>>(
          URIS.CHALLENGES.BASE + '?challengeId=' + challengeId,
          'request',
          request,
          filesToUpload
        )
      } else {
        response = await createMultipart<SuccessResponse<ChallengesResponse>>(
          URIS.CHALLENGES.BASE,
          'request',
          request,
          filesToUpload
        )
      }

      if (response?.success) {
        const successMessage = isUpdateMode
          ? t('Messages.success.challenge.updated')
          : t('Messages.success.challenge.created')
        toast.success(successMessage)

        // Mark all tabs as completed and set progress to 100%
        formState.value.completedTabs = tabs.map((tab) => tab.id)
        formState.value.progress = 100
        updatePendingTabs()

        setTimeout(async () => {
          clearChallengeDraft()
          await router.push({ name: 'ChallengeList', params: {} })
        }, 1000)
        return true
      } else {
        toast.error(resolveSubmissionErrorMessage(null, response))
        return false
      }
    } catch (error) {
      console.error('Error submitting form:', error)
      toast.error(resolveSubmissionErrorMessage(error))
      return false
    } finally {
      formState.value.isSubmitting = false
    }
  }

  const cancelForm = () => {
    clearChallengeDraft()
    router.push({ name: 'ChallengeList', params: {} })
  }

  // User selection methods
  const selectAll = (): void => {
    const allUserIds = lookupData.value.teamMembersLookupResponse.map((user) => Number(user.grpNumber))

    // Check if all users are already selected
    const allSelected = allUserIds.length > 0 && allUserIds.every((id) => selectedUsers.value.includes(id))

    if (allSelected) {
      // If all are selected, deselect them all
      selectedUsers.value = selectedUsers.value.filter((id) => !allUserIds.includes(id))
      isAllSelected.value = false
    } else {
      // If not all are selected, select them all
      selectedUsers.value = [...new Set([...selectedUsers.value, ...allUserIds])]
      isAllSelected.value = true
    }
  }

  const selectAllValidators = (): void => {
    const allValidatorIds = lookupData.value.validatorsLookupResponse.map((user) => Number(user.grpNumber))

    // Check if all validators are already selected
    const allSelected =
      allValidatorIds.length > 0 && allValidatorIds.every((id) => selectedValidatorUsers.value.includes(id))

    if (allSelected) {
      // If all are selected, deselect them all
      selectedValidatorUsers.value = selectedValidatorUsers.value.filter((id) => !allValidatorIds.includes(id))
      isAllValidatorsSelected.value = false
    } else {
      // If not all are selected, select them all
      selectedValidatorUsers.value = [...new Set([...selectedValidatorUsers.value, ...allValidatorIds])]
      isAllValidatorsSelected.value = true
    }
  }

  const selectAllGuestUsers = (): void => {
    const allGuestIds = guestUsers.value
      .map((user) => user.grpNumber)
      .filter((grpNumber): grpNumber is string => Boolean(grpNumber))

    // Check if all guest users are already selected
    const allSelected = allGuestIds.length > 0 && allGuestIds.every((id) => selectedGuestUsers.value.includes(id))

    if (allSelected) {
      // If all are selected, deselect them all
      selectedGuestUsers.value = selectedGuestUsers.value.filter((id) => !allGuestIds.includes(id))
      isAllGuestUsersSelected.value = false
    } else {
      // If not all are selected, select them all
      selectedGuestUsers.value = [...new Set([...selectedGuestUsers.value, ...allGuestIds])]
      isAllGuestUsersSelected.value = true
    }
  }

  const selectUser = (user: UserMainResponse): void => {
    if (!user.grpNumber) {
      return
    }

    if (!selectedGuestUsers.value.includes(user.grpNumber)) {
      selectedGuestUsers.value.push(user.grpNumber)
    }
  }

  const syncSelectedItems = (
    selectedIds: number[],
    currentResults: UserMainResponse[],
    currentSelectedItems: UserMainResponse[]
  ): UserMainResponse[] => {
    const mergedUsers = [...currentSelectedItems, ...currentResults]
    const uniqueUsers = Array.from(
      new Map(mergedUsers.filter((user) => user?.grpNumber).map((user) => [Number(user.grpNumber), user])).values()
    )

    return uniqueUsers.filter((user) => selectedIds.includes(Number(user.grpNumber)))
  }

  // Watch for changes to update select all states
  watch(
    [selectedUsers, () => lookupData.value.teamMembersLookupResponse],
    () => {
      const allUserIds = lookupData.value.teamMembersLookupResponse.map((user) => Number(user.grpNumber))
      isAllSelected.value = allUserIds.length > 0 && allUserIds.every((id) => selectedUsers.value.includes(id))

      selectedTeamMembersItems.value = syncSelectedItems(
        selectedUsers.value,
        lookupData.value.teamMembersLookupResponse,
        selectedTeamMembersItems.value
      )
    },
    { deep: true }
  )

  watch(
    [selectedValidatorUsers, () => lookupData.value.validatorsLookupResponse],
    () => {
      const allValidatorIds = lookupData.value.validatorsLookupResponse.map((user) => Number(user.grpNumber))
      isAllValidatorsSelected.value =
        allValidatorIds.length > 0 && allValidatorIds.every((id) => selectedValidatorUsers.value.includes(id))

      selectedValidatorsItems.value = syncSelectedItems(
        selectedValidatorUsers.value,
        lookupData.value.validatorsLookupResponse,
        selectedValidatorsItems.value
      )
    },
    { deep: true }
  )

  watch(
    [selectedGuestUsers, guestUsers],
    () => {
      const allGuestIds = guestUsers.value
        .map((user) => user.grpNumber)
        .filter((grpNumber): grpNumber is string => Boolean(grpNumber))
      isAllGuestUsersSelected.value =
        allGuestIds.length > 0 && allGuestIds.every((id) => selectedGuestUsers.value.includes(id))
    },
    { deep: true }
  )

  // Dynamic content methods
  const addSolutionText = (solution: string) => {
    const normalizedSolution = solution?.trim()
    if (normalizedSolution && !solutionsTexts.value.some((item) => item.value === normalizedSolution)) {
      solutionsTexts.value.push({ value: normalizedSolution })
    }
  }

  const addSolutionFromInput = () => {
    return
  }

  const handleSolutionKeydown = (event: KeyboardEvent) => {
    if (event.key === 'Enter') {
      event.preventDefault()
      addSolutionFromInput()
    }
  }

  const removeSolutionText = (index: number, _solution: string) => {
    solutionsTexts.value.splice(index, 1)
  }

  const clearEmptySolutions = () => {
    solutionsTexts.value = cloneSolutionsTexts(solutionsTexts.value)
  }

  const getValidSolutions = () => {
    return cloneSolutionsTexts(solutionsTexts.value).map((field) => field.value)
  }

  const addYoutubeLink = (url?: string) => {
    const link = (url ?? youtubeLinkInput.value).trim()
    const youtubeRegex = /^(https?:\/\/)?(www\.)?(youtube\.com|youtu\.be)\/.+$/

    if (link && youtubeRegex.test(link)) {
      if (!youtubeLinks.value.includes(link)) {
        youtubeLinks.value.push(link)
        youtubeLinkInput.value = ''
      } else {
        toast.info(t('challenge.info.youtubeLinkAlreadyAdded'))
      }
    } else if (link) {
      toast.error(t('challenge.info.invalidYoutubeUrl'))
    }
  }

  const removeYoutubeLink = (index: number) => {
    youtubeLinks.value.splice(index, 1)
  }

  const addHashtag = (): void => {
    const normalizedHashtag = inputText.value.replace(/^#+/, '').trim()
    if (normalizedHashtag && !hashtagTexts.value.includes(normalizedHashtag)) {
      hashtagTexts.value.push(normalizedHashtag)
      inputText.value = ''
    }
  }

  const removeHashtag = (index: number) => {
    hashtagTexts.value.splice(index, 1)
  }

  const addReminder = () => {
    if (newReminder.value && !challengeRequest.value.challengeRemindersDateTimeList.includes(newReminder.value)) {
      challengeRequest.value.challengeRemindersDateTimeList.push(newReminder.value)
      newReminder.value = ''
    }
  }

  const clearReminders = () => {
    challengeRequest.value.challengeRemindersDateTimeList = []
  }

  // File handling methods
  const onSelectedFiles = (event: any) => {
    files.value = event.files
  }

  const onRemoveTemplatingFile = (file: any, removeFileCallback: any, index: any) => {
    removeFileCallback(index)
    totalSize.value -= file.size ?? 0
  }

  const onClearTemplatedUpload = (clear: any) => {
    clear()
    totalSize.value = 0
    totalSizePercent.value = 0
  }

  const uploadEvent = (callback: any) => {
    totalSizePercent.value = totalSize.value / 10
    callback()
  }

  // Enhanced Vuetify file handling methods
  const filesModel = ref<File[]>([])
  const uploadedFiles = ref<
    Array<{
      name: string
      size: number
      type: string
      url?: string
      preview?: string
      id?: number
      loading?: boolean
      error?: string
      file: File
    }>
  >([])

  watch(
    filesModel,
    (newFiles) => {
      if (!newFiles) {
        return
      }

      newFiles.forEach((file) => {
        const isDuplicate = uploadedFiles.value.some(
          (existingFile) => existingFile.name === file.name && existingFile.size === file.size
        )

        if (isDuplicate) return

        const fileObj: any = {
          name: file.name,
          size: file.size,
          type: file.type,
          loading: false,
          error: undefined,
          file: file
        }

        if (file.type.startsWith('image/')) {
          const reader = new FileReader()
          reader.onload = (e) => {
            fileObj.preview = e.target?.result as string
          }
          reader.readAsDataURL(file)
        }

        uploadedFiles.value.push(fileObj)
        totalSize.value += file.size
      })
    },
    { deep: true }
  )

  const removeFile = (index: number) => {
    const fileToRemove = uploadedFiles.value[index]
    totalSize.value -= fileToRemove.size
    uploadedFiles.value.splice(index, 1)

    const indexInModel = filesModel.value.findIndex((f) => f.name === fileToRemove.name && f.size === fileToRemove.size)
    if (indexInModel > -1) {
      filesModel.value.splice(indexInModel, 1)
    }
  }

  const clearAllFiles = () => {
    filesModel.value = []
    uploadedFiles.value = []
    totalSize.value = 0
    totalSizePercent.value = 0
  }

  const getFileIcon = (fileType: string) => {
    if (fileType.startsWith('image/')) return 'mdi-image'
    if (fileType.includes('pdf')) return 'mdi-file-pdf-box'
    if (fileType.includes('word') || fileType.includes('doc')) return 'mdi-file-word-box'
    if (fileType.includes('excel') || fileType.includes('xls')) return 'mdi-file-excel-box'
    if (fileType.includes('powerpoint') || fileType.includes('ppt')) return 'mdi-file-powerpoint-box'
    return 'mdi-file'
  }

  const getFileColor = (fileType: string) => {
    if (fileType.startsWith('image/')) return 'success'
    if (fileType.includes('pdf')) return 'error'
    if (fileType.includes('word') || fileType.includes('doc')) return 'primary'
    if (fileType.includes('excel') || fileType.includes('xls')) return 'success'
    if (fileType.includes('powerpoint') || fileType.includes('ppt')) return 'warning'
    return 'grey'
  }

  // UI helpers
  const toggleInvitationSection = () => {
    isInvitationSectionCollapsed.value = !isInvitationSectionCollapsed.value
  }

  const validateMandatoryField = (fieldValue: string | null): boolean | string => {
    return fieldValue ? true : t('error.mandatory')
  }

  const validateMandatoryArrayField = (fieldValue: string[] | null): boolean | string => {
    return fieldValue && fieldValue.length > 0 ? true : t('error.mandatory')
  }

  // Rules for validation
  const rules = {
    required: (value: any) => !!value || t('error.mandatory'),
    email: (value: string) => {
      const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      return pattern.test(value) || t('error.invalidEmail')
    }
  }

  restoreChallengeDraft()

  watch(
    [solutionsTexts, hashtagTexts, youtubeLinks],
    () => {
      challengeRequest.value.solutionsTexts = getValidSolutions()
      challengeRequest.value.hashtagTexts = [...hashtagTexts.value]
      challengeRequest.value.youtubeLinks = [...youtubeLinks.value]
    },
    { deep: true, immediate: true }
  )

  // Watch for changes to update progress and pending tabs
  watch(
    [
      selectedUsers,
      selectedValidatorUsers,
      selectedGuestUsers,
      () => challengeRequest.value.titleEn,
      () => challengeRequest.value.titleAr,
      () => challengeRequest.value.descriptionEn,
      () => challengeRequest.value.descriptionAr,
      () => challengeRequest.value.startTime,
      () => challengeRequest.value.endTime,
      () => challengeRequest.value.visibility,
      () => challengeRequest.value.moderatorId,
      () => challengeRequest.value.allowImmediateAccess,
      () => solutionsTexts.value,
      () => challengeRequest.value.topicClassification,
      () => challengeRequest.value.levelOfDecision,
      () => challengeRequest.value.importanceOfDecision,
      () => challengeRequest.value.impact,
      () => challengeRequest.value.field,
      () => challengeRequest.value.documentsIds,
      () => challengeRequest.value.youtubeLinks,
      () => challengeRequest.value.hashtagTexts,
      () => challengeRequest.value.challengeRemindersDateTimeList
    ],
    () => {
      updatePendingTabs()
    },
    { deep: true }
  )

  // Watch for department selection changes to update challenge request
  watch(
    [selectedDepartment, selectedSubDepartment, selectedSection],
    () => {
      if (selectedDepartment.value) {
        challengeRequest.value.departmentId = selectedDepartment.value.id?.toString() || ''
        challengeRequest.value.departmentName = selectedDepartment.value.shortName ?? ''
      }
      if (selectedSubDepartment.value) {
        challengeRequest.value.subDepartmentId = selectedSubDepartment.value.id?.toString() || ''
        challengeRequest.value.subDepartmentName = selectedSubDepartment.value.shortName ?? ''
      }
      if (selectedSection.value) {
        challengeRequest.value.sectionId = selectedSection.value.id?.toString() || ''
        challengeRequest.value.sectionName = selectedSection.value.shortName ?? ''
      }
    },
    { deep: true }
  )

  watch(
    [
      () => formState.value.currentTab,
      () => formState.value.completedTabs,
      challengeRequest,
      selectedUsers,
      selectedValidatorUsers,
      selectedGuestUsers,
      selectedValidatorsItems,
      selectedTeamMembersItems,
      selectedDepartment,
      selectedSubDepartment,
      selectedSection,
      selectedDepartmentForSearch,
      selectedDepartmentForValidators,
      selectedExpertLevel,
      selectedExpertise,
      selectedExpertLevelForValidators,
      selectedExpertiseForValidators,
      selectedCommittee,
      inputText,
      hashtagTexts,
      newReminder,
      youtubeLinkInput,
      youtubeLinks,
      solutionsTexts
    ],
    () => {
      persistChallengeDraft()
    },
    { deep: true }
  )

  // Function to manually update progress (can be called from components)
  const updateProgress = () => {
    // This function triggers the computed progress to recalculate
    // The actual calculation is done in the progress-computed property
    updatePendingTabs()
  }

  return {
    // Form state
    currentTab: computed(() => formState.value.currentTab),
    progress: progress,
    isSubmitting: computed(() => formState.value.isSubmitting),
    isLoading: computed(() => formState.value.isLoading),
    validationErrors: computed(() => formState.value.validationErrors),
    completedTabs: computed(() => formState.value.completedTabs),
    pendingTabs: computed(() => formState.value.pendingTabs),

    // Form data
    challengeRequest,

    // User selection - return actual refs for v-model binding
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
    isModeratorsLoading,
    moderatorsSearchTerm,
    hasMoreModerators,
    teamMembersPagination,
    validatorsPagination,

    // Department selection - return actual refs for v-model binding
    selectedDepartment,
    selectedSubDepartment,
    selectedSection,
    selectedDepartmentForSearch,
    selectedDepartmentForValidators,

    // File state - return actual refs for v-model binding
    files,
    docsIds,
    totalSize,
    totalSizePercent,

    // Dynamic content - return actual refs for v-model binding
    inputText,
    hashtagTexts,
    newReminder,
    youtubeLinkInput,
    youtubeLinks,
    solutionsTexts,

    // Lookup data
    teamMembersLookupResponse: computed(() => lookupData.value.teamMembersLookupResponse),
    validatorsLookupResponse: computed(() => lookupData.value.validatorsLookupResponse),
    moderatorsLookupResponse: computed(() => lookupData.value.moderatorsLookupResponse),
    expertisesResponseListLookupResponse: computed(() => lookupData.value.expertisesResponseListLookupResponse),
    strategicGoalsResponseListLookupResponse: computed(() => lookupData.value.strategicGoalsResponseListLookupResponse),
    strategicDirectionResponseListLookupResponse: computed(
      () => lookupData.value.strategicDirectionResponseListLookupResponse
    ),
    futureStrategicGoalsResponseListLookupResponse: computed(
      () => lookupData.value.futureStrategicGoalsResponseListLookupResponse
    ),
    futureStrategicDirectionResponseListLookupResponse: computed(
      () => lookupData.value.futureStrategicDirectionResponseListLookupResponse
    ),
    departmentDtoResponseList: computed(() => lookupData.value.departmentDtoResponseList),
    subDepartmentDtoResponseList: computed(() => lookupData.value.subDepartmentDtoResponseList),
    sectionsDtoResponseList: computed(() => lookupData.value.sectionsDtoResponseList),
    committeeListDropListResponse: computed(() => lookupData.value.committeeListDropListResponse),
    topicClassificationListLookupResponse: computed(() => lookupData.value.topicClassificationListLookupResponse),
    levelOfDecisionListLookupResponse: computed(() => lookupData.value.levelOfDecisionListLookupResponse),
    importanceOfDecisionListLookupResponse: computed(() => lookupData.value.importanceOfDecisionListLookupResponse),
    impactListLookupResponse: computed(() => lookupData.value.impactListLookupResponse),
    fieldListLookupResponse: computed(() => lookupData.value.fieldListLookupResponse),

    // Expert selection - return actual refs for v-model binding
    selectedExpertLevel,
    selectedExpertise,
    selectedExpertLevelForValidators,
    selectedExpertiseForValidators,

    // Committee selection
    selectedCommittee,

    // UI state - return actual refs for v-model binding
    isInvitationSectionCollapsed,
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
    selectUser,

    // Dynamic content
    addSolutionText,
    addSolutionFromInput,
    handleSolutionKeydown,
    removeSolutionText,
    clearEmptySolutions,
    getValidSolutions,
    addYoutubeLink,
    removeYoutubeLink,
    addHashtag,
    removeHashtag,
    addReminder,
    clearReminders,

    // File handling
    onSelectedFiles,
    onRemoveTemplatingFile,
    onClearTemplatedUpload,
    uploadEvent,

    // Enhanced Vuetify file handling
    filesModel,
    uploadedFiles,
    removeFile,
    clearAllFiles,
    getFileIcon,
    getFileColor,

    // UI helpers
    toggleInvitationSection,
    validateMandatoryField,
    validateMandatoryArrayField,

    // Rules for validation
    rules,

    // New methods
    getTabStatus,
    isTabCompleted,
    isTabPending,
    isTabDataFilled,
    updatePendingTabs,
    validateField,
    formState,
    updateProgress,
    getProgressDetails
  }
}
