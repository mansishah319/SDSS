/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import RolesClientService from '@/services/roles/RolesClientService'

export default function userSearch(availableRoles) {
  return RolesClientService.getAll()
    .then((response) => {
      availableRoles.value = response
    })
    .catch((error) => {
      console.error('Error fetching available roles', error)
    })
}
