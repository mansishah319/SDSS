/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

/**
 * Utility functions for RTL (Right-to-Left) language support
 */

/**
 * Get the current locale from i18n
 * @returns The current locale string
 */
export const useLocale = () => {
  const { locale } = useI18n()
  return locale
}

/**
 * Check if the current language is RTL (Arabic)
 * @returns Computed boolean indicating if current language is RTL
 */
export const useIsRTL = () => {
  const locale = useLocale()
  return computed(() => locale.value === 'ar')
}

/**
 * Get RTL-aware left arrow icon
 * @returns Computed string for left arrow icon (switches direction for RTL)
 */
export const useLeftArrowIcon = () => {
  const isRTL = useIsRTL()
  return computed(() => (isRTL.value ? 'mdi-arrow-right' : 'mdi-arrow-left'))
}

/**
 * Get RTL-aware right arrow icon
 * @returns Computed string for right arrow icon (switches direction for RTL)
 */
export const useRightArrowIcon = () => {
  const isRTL = useIsRTL()
  return computed(() => (isRTL.value ? 'mdi-arrow-left' : 'mdi-arrow-right'))
}

/**
 * Get RTL-aware arrow icon based on direction
 * @param direction - The intended direction ('left' or 'right')
 * @returns Computed string for the appropriate arrow icon
 */
export const useArrowIcon = (direction: 'left' | 'right') => {
  const isRTL = useIsRTL()
  return computed(() => {
    if (direction === 'left') {
      return isRTL.value ? 'mdi-arrow-right' : 'mdi-arrow-left'
    } else {
      return isRTL.value ? 'mdi-arrow-left' : 'mdi-arrow-right'
    }
  })
}

/**
 * Get RTL-aware margin classes
 * @returns Object with RTL-aware margin classes
 */
export const useRTLMarginClasses = () => {
  const isRTL = useIsRTL()
  return {
    marginStart: computed(() => (isRTL.value ? 'ms-auto' : 'me-2')),
    marginEnd: computed(() => (isRTL.value ? 'me-2' : 'ms-auto'))
  }
}

/**
 * Get RTL-aware flex direction
 * @returns Computed flex direction class
 */
export const useRTLFlexDirection = () => {
  const isRTL = useIsRTL()
  return computed(() => (isRTL.value ? 'flex-row-reverse' : 'flex-row'))
}
