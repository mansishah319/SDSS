/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import LookupClientService from '@/services/lookup/LookupClientService'

export default function userListLookup() {
  return LookupClientService.getAllUserListLookup()
    .then((response) => {
      console.log(response)
    })
    .catch((error) => {
      console.log(error)
      if (error.response.status === 403) {
        // Handle permission error
        console.log(error)
      }
    })
}
