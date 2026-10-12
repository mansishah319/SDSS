import { defineStore } from 'pinia'
import { SecurityConstants } from '@/constants/DpConstants'

export const useSurveyTokenStore = defineStore('surveyToken', {
  state: () => ({
    surveyToken: localStorage.getItem(SecurityConstants.Survey_TOKEN) ?? ''
  }),
  actions: {
    updateSurveyToken(newToken: string) {
      localStorage.setItem(SecurityConstants.Survey_TOKEN, newToken)
      this.surveyToken = newToken
    },
    clearSurveyToken() {
      this.surveyToken = ''
      localStorage.removeItem(SecurityConstants.Survey_TOKEN)
    }
  }
})
