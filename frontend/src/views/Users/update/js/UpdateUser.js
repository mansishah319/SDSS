/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import UserClientService from '@/services/user/UserClientService'

export default function UpdateUser(formData) {
  const formDataInstance = new FormData()

  console.log(formData.value)
  console.log(JSON.stringify(formData.value))
  formDataInstance.append('request', JSON.stringify(formData.value))
  console.log(formDataInstance)
  return UserClientService.updateUser(formData.value)
    .then((response) => {
      console.log('user creation Response is --->' + response)
    })
    .catch((error) => {
      throw new error()
    })
}
