/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import UserClientService from '@/services/user/UserClientService'

export default function UserRegistration(formData, imagePreview) {
  const formDataInstance = new FormData()

  if (imagePreview.value) {
    const file = imagePreview.value
    console.log('imageFile is -> ' + file.name)
    formDataInstance.append('profile', file, file.name)
  }

  formDataInstance.append('request', JSON.stringify(formData.value))
  return UserClientService.userCreation(formDataInstance)
}
