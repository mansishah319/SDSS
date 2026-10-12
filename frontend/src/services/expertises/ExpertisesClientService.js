/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { Paths } from '@/Paths'
import { getAll } from '@/utils/api-utils'
import { URIS } from '@/constants/DpConstants'

export default {
  getAll: async () => {
    return await getAll(Paths.BASE_URL + URIS.EXPERTISES.EXPERTISES_ALL_URI)
  }
}
