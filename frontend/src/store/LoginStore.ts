/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { ref } from 'vue'
import { defineStore } from 'pinia'
import Login from '@/dto/Login'

export const useLoginStore = defineStore('login', () => {
  const selectedLogin = ref<Login>(new Login())

  function updateLogin(login: Login) {
    selectedLogin.value = login
  }

  return { selectedLogin, updateLogin }
})
