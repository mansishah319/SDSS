/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export type SupportedLocale = 'ar' | 'en' | string

const toSafeString = (value: unknown): string => {
  if (typeof value !== 'string') {
    return ''
  }

  return value.trim()
}

const getFirstNonEmptyValue = (values: unknown[]): string => {
  for (const value of values) {
    const normalized = toSafeString(value)
    if (normalized) {
      return normalized
    }
  }

  return ''
}

export const isArabicText = (value: unknown): boolean => /[\u0600-\u06FF]/.test(toSafeString(value))

export const resolveLocalizedValue = (params: {
  locale: SupportedLocale
  ar?: unknown
  en?: unknown
  local?: unknown
  fallback?: string
}): string => {
  const { locale, ar, en, local, fallback = '-' } = params

  const prioritizedByLocale = locale === 'ar' ? [local, ar, en] : [local, en, ar]
  return getFirstNonEmptyValue(prioritizedByLocale) || fallback
}

export const resolveLocalizedFieldValue = (params: {
  item: Record<string, any> | null | undefined
  locale: SupportedLocale
  fieldName?: string
  fallback?: string
}): string => {
  const { item, locale, fieldName = 'name', fallback = '-' } = params

  if (!item) {
    return fallback
  }

  const fieldNameAr = `${fieldName}Ar`
  const fieldNameEn = `${fieldName}En`

  return resolveLocalizedValue({
    locale,
    local: item.localName,
    ar: item[fieldNameAr] ?? item.ar,
    en: item[fieldNameEn] ?? item.en,
    fallback
  })
}
