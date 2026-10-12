/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { Characters, URIS } from '@/constants/DpConstants'
import { get } from '@/utils/GenericApiHelper'

export default {
  retrieveChallengeById: async (challengeId) => {
    return await get(URIS.CHALLENGES.BASE + Characters.SLASH + challengeId)
  }
}
