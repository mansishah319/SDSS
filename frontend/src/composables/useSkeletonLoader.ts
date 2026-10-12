import { computed, ref } from 'vue'

export interface SkeletonLoaderState {
  isInitialLoading: boolean
  isLoadingChallenge: boolean
  isLoadingData: boolean
  isLoadingLookup: boolean
  isLoadingForm: boolean
}

export function useSkeletonLoader() {
  // Individual loading states
  const isInitialLoading = ref(true)
  const isLoadingChallenge = ref(false)
  const isLoadingData = ref(false)
  const isLoadingLookup = ref(false)
  const isLoadingForm = ref(false)

  // Computed property to check if any loading is happening
  const isAnyLoading = computed(() => {
    return (
      isInitialLoading.value ||
      isLoadingChallenge.value ||
      isLoadingData.value ||
      isLoadingLookup.value ||
      isLoadingForm.value
    )
  })

  // Computed property for showing skeleton loader
  const showSkeleton = computed(() => {
    return isAnyLoading.value
  })

  // Methods to control loading states
  const startInitialLoading = () => {
    isInitialLoading.value = true
  }

  const stopInitialLoading = () => {
    isInitialLoading.value = false
  }

  const startChallengeLoading = () => {
    isLoadingChallenge.value = true
  }

  const stopChallengeLoading = () => {
    isLoadingChallenge.value = false
  }

  const startDataLoading = () => {
    isLoadingData.value = true
  }

  const stopDataLoading = () => {
    isLoadingData.value = false
  }

  const startLookupLoading = () => {
    isLoadingLookup.value = true
  }

  const stopLookupLoading = () => {
    isLoadingLookup.value = false
  }

  const startFormLoading = () => {
    isLoadingForm.value = true
  }

  const stopFormLoading = () => {
    isLoadingForm.value = false
  }

  // Method to start all loading states
  const startAllLoading = () => {
    startInitialLoading()
    startChallengeLoading()
    startDataLoading()
    startLookupLoading()
    startFormLoading()
  }

  // Method to stop all loading states
  const stopAllLoading = () => {
    stopInitialLoading()
    stopChallengeLoading()
    stopDataLoading()
    stopLookupLoading()
    stopFormLoading()
  }

  // Method to reset all loading states
  const resetLoadingStates = () => {
    isInitialLoading.value = false
    isLoadingChallenge.value = false
    isLoadingData.value = false
    isLoadingLookup.value = false
    isLoadingForm.value = false
  }

  return {
    // State
    isInitialLoading,
    isLoadingChallenge,
    isLoadingData,
    isLoadingLookup,
    isLoadingForm,

    // Computed
    isAnyLoading,
    showSkeleton,

    // Methods
    startInitialLoading,
    stopInitialLoading,
    startChallengeLoading,
    stopChallengeLoading,
    startDataLoading,
    stopDataLoading,
    startLookupLoading,
    stopLookupLoading,
    startFormLoading,
    stopFormLoading,
    startAllLoading,
    stopAllLoading,
    resetLoadingStates
  }
}
