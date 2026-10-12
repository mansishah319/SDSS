/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import ExpertisesClientService from '@/services/expertises/ExpertisesClientService'

export default async function userSearch(availableExpertises) {
  try {
    availableExpertises.value = await ExpertisesClientService.getAll()
  } catch (error) {
    console.error('Error fetching available roles', error)
  }
}
