/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import UserClientService from '@/services/user/UserClientService'

export default async function userView(grpNumber) {
  try {
    return await UserClientService.getByUserByGrp(grpNumber)
  } catch (error) {
    console.log(error)
    if (error.response.status === 403) {
      // Handle permission error
      console.log(error)
    }
  }
}
