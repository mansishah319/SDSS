/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { toast } from 'vue3-toastify'

// Track displayed toasts to prevent duplicates
const displayedToasts = new Set<string>()
const toastCooldown = 2000 // 2 seconds

export const errorPublisher = (errorMessage: string, errorCode: string) => {
  console.log('Error message is --> ', errorMessage, ' With error code : ', errorCode)

  // Check if this exact error was recently shown
  const errorKey = `${errorMessage}-${errorCode}`
  if (displayedToasts.has(errorKey)) {
    return // Skip duplicate
  }

  // Add to displayed set and set timeout to remove it
  displayedToasts.add(errorKey)
  setTimeout(() => displayedToasts.delete(errorKey), toastCooldown)

  toast.error(errorMessage)
}

export const errorPublisherMsg = (errorMessage: string) => {
  // Check if this exact error was recently shown
  if (displayedToasts.has(errorMessage)) {
    return // Skip duplicate
  }

  // Add to displayed set and set timeout to remove it
  displayedToasts.add(errorMessage)
  setTimeout(() => displayedToasts.delete(errorMessage), toastCooldown)

  toast.error(errorMessage)
}
