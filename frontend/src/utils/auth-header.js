/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { Fields, SecurityConstants } from '@/constants/DpConstants'

const getCurrentLanguage = () => localStorage.getItem(Fields.EMPLOYEE_LANG) || localStorage.getItem(Fields.EMPLOYEE_LANG_LOCAL_STORAGE) || 'en'

function getAuthHeader(contentType) {
  const token = localStorage.getItem(SecurityConstants.TOKEN)
  const headers = {
    'Accept-Language': getCurrentLanguage()
  }
  if (token) {
    headers.Authorization = 'Bearer ' + token
    if (contentType) {
      headers['Content-Type'] = contentType
    }
    return headers
  } else {
    return headers
  }
}

export default function authHeader() {
  return getAuthHeader('application/json')
}

export function authHeaderMultipart() {
  return getAuthHeader('multipart/form-data')
}

export function generalAuthHeader() {
  return getAuthHeader()
}

export function getSurveyAuthHeader(language) {
  const token = localStorage.getItem(SecurityConstants.Survey_TOKEN)
  if (token) {
    return {
      SurveyAuthorization: token,
      'Accept-Language': language
    }
  } else {
    return {}
  }
}

export function getPublicApiHeader(language) {
  return {
    'Accept-Language': language
  }
}

export function getLoginSurveyAuthHeader(language) {
  const token = localStorage.getItem(SecurityConstants.TOKEN)
  if (token) {
    return {
      Authorization: token,
      'Accept-Language': language
    }
  } else {
    return {
      'Accept-Language': language
    }
  }
}
