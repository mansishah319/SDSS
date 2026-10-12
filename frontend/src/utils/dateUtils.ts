/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

// src/utils/dateUtils.ts
import { format } from 'date-fns'

export const sdssDateFormat = (datetime: string): string => {
  return format(new Date(datetime), 'yyyy-MM-dd HH:mm:ss')
}

// Define your function to calculate and format the time difference
export const getTimeDifferenceFormatted = (start: string, end: string): string => {
  const startTime = new Date(start).getTime()
  const endTime = new Date(end).getTime()
  const diffInMs = endTime - startTime

  // Calculate days, hours, minutes, and seconds
  const days = Math.floor(diffInMs / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diffInMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diffInMs % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diffInMs % (1000 * 60)) / 1000)

  // Format the result
  const dayPart = days > 0 ? `${days}d ` : '' // Include days only if greater than 0
  return `${dayPart}${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(
    2,
    '0'
  )}`
}
