/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

// ChallengeMapper

import { Pageable } from '@/dto/pagination/Pageable.ts'

const mapDataBasedOnLange = (sortingKey: string, isLanguageEn: boolean) => {
  if (sortingKey === 'localTitle') {
    sortingKey = isLanguageEn ? 'titleEn' : 'titleAr'
  } else if (sortingKey === 'localDescription') {
    sortingKey = isLanguageEn ? 'descriptionEn' : 'descriptionAr'
  } else {
    sortingKey = 'updatedAt'
  }
  return sortingKey
}

export const processMapPageableListInfo = (pageable: Pageable, challengeSearchRequest: any, isLanguageEn: boolean) => {
  if (pageable?.page && pageable?.sortBy?.length && pageable?.itemsPerPage) {
    let sortingKey = pageable.sortBy[0]?.key
    let sortingOrder = pageable.sortBy[0]?.order
    sortingOrder = sortingOrder === undefined ? 'desc' : pageable.sortBy[0]?.order
    sortingKey = mapDataBasedOnLange(sortingKey, isLanguageEn)
    challengeSearchRequest.page = pageable.page
    challengeSearchRequest.size = pageable.itemsPerPage
    challengeSearchRequest.sort = `${sortingKey},${sortingOrder}`
  }
}
