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

import { createI18n } from 'vue-i18n'
import enData from '@/locales/en.json'
import arData from '@/locales/ar.json'
import { ar, en } from 'vuetify/locale'
import { Fields } from '@/constants/DpConstants'

const savedLanguage = (window.localStorage.getItem(Fields.EMPLOYEE_LANG) ??
  window.localStorage.getItem(Fields.EMPLOYEE_LANG_LOCAL_STORAGE) ??
  'en') as 'ar' | 'en'

const messages = {
  en: {
    ...enData,
    $vuetify: en,
  },
  ar: { ...arData, $vuetify: ar },
}

const i18n = createI18n({
  legacy: false,
  locale: savedLanguage,
  fallbackLocale: {
    ar: ['en'],
    en: ['en'],
    default: ['en'],
  },
  messages,
})
export default i18n
