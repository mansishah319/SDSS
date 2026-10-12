import { defineStore } from 'pinia'
import { SecurityConstants } from '@/constants/DpConstants.ts'

export const useSdssInternalUserCheckStore = defineStore('sdssInternalUserCheck', {
  state: () => ({
    isInternalUser: localStorage.getItem(SecurityConstants.TOKEN) !== null
  }),
  actions: {
    setInternalUser(isInternalUser: boolean) {
      this.isInternalUser = isInternalUser
    }
  }
})
