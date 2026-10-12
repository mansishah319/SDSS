import i18n from '@/plugins/i18n'
import { Fields } from '@/constants/DpConstants'
import type Employee from '@/dto/Employee'

export type AppLanguage = 'ar' | 'en'

const normalizeLanguage = (language?: string | null): AppLanguage | null => {
  if (!language) return null
  const normalized = language.toLowerCase()
  if (normalized.startsWith('ar')) return 'ar'
  if (normalized.startsWith('en')) return 'en'
  return null
}

export const getStoredLanguage = (): AppLanguage => {
  return (
    normalizeLanguage(localStorage.getItem(Fields.EMPLOYEE_LANG)) ||
    normalizeLanguage(localStorage.getItem(Fields.EMPLOYEE_LANG_LOCAL_STORAGE)) ||
    'en'
  )
}

export const applyLanguage = (language?: string | null): AppLanguage => {
  const normalized = normalizeLanguage(language) || getStoredLanguage()
  localStorage.setItem(Fields.EMPLOYEE_LANG, normalized)
  localStorage.setItem(Fields.EMPLOYEE_LANG_LOCAL_STORAGE, normalized)
  localStorage.setItem('selectedLanguage', normalized)
  i18n.global.locale.value = normalized
  document.documentElement.setAttribute('lang', normalized)
  document.documentElement.setAttribute('dir', normalized === 'ar' ? 'rtl' : 'ltr')
  document.body.classList.remove('rtl', 'ltr')
  document.body.classList.add(normalized === 'ar' ? 'rtl' : 'ltr')
  return normalized
}

export const applyEmployeeLanguage = (employee?: Employee | null): AppLanguage => {
  return applyLanguage(employee?.language)
}
