/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export default class DataTableResponse {
  constructor(total, currentPage, pageSize) {
    this.total = total
    this.currentPage = currentPage
    this.pageSize = pageSize
    this.data = []
  }

  static bar() {
    // ...
  }
}
