/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export default class UserPageable {
  constructor(currentPage, pageSize) {
    this.currentPage = currentPage
    this.pageSize = pageSize
    this.sortBy = ''
    this.sortDesc = false
    this.data = {}
  }

  static bar() {
    // ...
  }
}
