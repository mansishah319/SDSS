import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useErrorStore = defineStore('errorStore', () => {
  // Define the reactive state with types
  const errorMessage = ref<string | null>(null)
  const isErrorDisplayed = ref<boolean>(false)
  const lastErrorTime = ref<number>(0)
  const lastErrorMessage = ref<string | null>(null)

  // Action to set the error message with duplicate prevention
  function setError(message: string): void {
    const now = Date.now()
    const isDuplicate = lastErrorMessage.value === message && now - lastErrorTime.value < 2000 // 2 second cooldown

    if (!isErrorDisplayed.value && !isDuplicate) {
      errorMessage.value = message
      isErrorDisplayed.value = true
      lastErrorMessage.value = message
      lastErrorTime.value = now

      // Automatically clear the error after a timeout (optional)
      setTimeout(() => clearError(), 5000)
    }
  }

  // Action to clear the error
  function clearError(): void {
    errorMessage.value = null
    isErrorDisplayed.value = false
  }

  // Action to force set error (bypass duplicate check)
  function forceSetError(message: string): void {
    errorMessage.value = message
    isErrorDisplayed.value = true
    lastErrorMessage.value = message
    lastErrorTime.value = Date.now()

    // Automatically clear the error after a timeout (optional)
    setTimeout(() => clearError(), 5000)
  }

  return {
    errorMessage,
    isErrorDisplayed,
    setError,
    clearError,
    forceSetError
  }
})
