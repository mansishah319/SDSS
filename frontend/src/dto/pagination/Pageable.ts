/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export interface Pageable {
  page: number
  itemsPerPage: number
  sortBy: SortBy[]
}

export interface SortBy {
  key: string // or the specific type of `key` if it's not a string
  order: string // or the specific type of `order` if it's not a string
}
