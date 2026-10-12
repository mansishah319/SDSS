/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { defineStore } from 'pinia'
import Employee from '@/dto/Employee'

export interface EmployeeState {
  selectedEmployee: Employee
}

export const useEmployeeStore = defineStore('employee', {
  state: (): EmployeeState => ({
    selectedEmployee: new Employee()
  }),
  actions: {
    updateEmployee(employee: Employee) {
      this.selectedEmployee = employee
    }
  }
})

export default useEmployeeStore
