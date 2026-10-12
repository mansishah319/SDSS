/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import CommitteeClientService from '@/services/committee/CommitteeClientService'

export default function userSearch(availableRoles) {
  return CommitteeClientService.getAll()
    .then((response) => {
      availableRoles.value = response
    })
    .catch((error) => {
      console.error('Error fetching available roles', error)
    })
}
