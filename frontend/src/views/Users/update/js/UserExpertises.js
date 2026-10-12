/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import ExpertisesClientService from '@/services/expertises/ExpertisesClientService'

export default function userSearch(availableExpertises) {
  return ExpertisesClientService.getAll()
    .then((response) => {
      availableExpertises.value = response
    })
    .catch((error) => {
      console.error('Error fetching available roles', error)
    })
}
