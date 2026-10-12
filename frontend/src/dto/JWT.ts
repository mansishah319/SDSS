/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn OCT, 2023
 */

class JWT {
  employeeName: string | null = null
  userName: string | null = null
  sub: string | null = null
  grpNumber: number | null = null
  gender: string | null = null
  empType: number | null = null
  departmentName: string | null = null
  departmentId: number | null = null
  sectorName: string | null = null
  sectorId: number | null = null
  realm: string | null = null
  iat: number | null = null
  iss: string | null = null
  exp: number | null = null
  language: string | null = null
  permissionList: [] = []
  roles: string[] = []
  isDelphiModerator: boolean | null = null
}

export default JWT
