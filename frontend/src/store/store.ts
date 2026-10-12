/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

// store.ts
import { defineStore } from 'pinia'
import { ChallengeDashboardResponse } from '@/dto/challenge/ChallengeDashboardResponse.ts'
import { ref } from 'vue'

export const useStore = defineStore('myStore', () => {
  // State
  const challengeDashboardResponse = ref<ChallengeDashboardResponse | null>(null)

  // Actions
  const setChallengeDashboardResponse = (response: ChallengeDashboardResponse) => {
    challengeDashboardResponse.value = response
  }

  return {
    challengeDashboardResponse,
    setChallengeDashboardResponse
  }
})
