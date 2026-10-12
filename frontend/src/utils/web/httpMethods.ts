/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

// httpMethods.ts
type HttpMethod = 'get' | 'post' | 'put' | 'delete'
export const METHODS: { [key: string]: HttpMethod } = {
  GET: 'get',
  POST: 'post',
  DELETE: 'delete',
  UPDATE: 'put'
}
