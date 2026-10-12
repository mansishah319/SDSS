/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

// utils/fileUtils.ts

// Define the file type as a string
// export function isPdfGeneric(type: string): boolean {
//   return type.toLowerCase() === 'pdf'
// }

export function isWordGeneric(type: string): boolean {
  const lowerType = type.toLowerCase()
  return lowerType === 'doc' || lowerType === 'docx'
}

export function isExcelGeneric(type: string): boolean {
  const lowerType = type.toLowerCase()
  return lowerType === 'xls' || lowerType === 'xlsx'
}

export function isTheTypeIsImageGeneric(type: string): boolean {
  return ['jpeg', 'jpg', 'png', 'gif'].includes(type.toLowerCase())
}

export function isHTML(type: string): boolean {
  return type === 'html' || type === 'htm'
}

export function getFileSizeGeneric(content: string): string {
  // Check if padding is needed
  const hasPadding = content.endsWith('=')
  const padding = hasPadding ? 1 : 0

  // Calculate size in bytes
  const sizeInBytes = (content.length * 3) / 4 - padding
  const sizeInKB = sizeInBytes / 1024

  return sizeInKB.toFixed(2) + ' KB'
}

export const isPdfGeneric = (type: string | undefined | null): boolean => {
  if (!type) return false
  return type.toLowerCase().includes('pdf') || type.toLowerCase().includes('application/pdf')
}

export const isImageGeneric = (type: string | undefined | null): boolean => {
  if (!type) return false

  const imageTypes = [
    'jpg',
    'jpeg',
    'png',
    'gif',
    'webp',
    'svg',
    'bmp',
    'tiff',
    'image/jpeg',
    'image/png',
    'image/gif',
    'image/webp',
    'image/svg+xml',
    'image/bmp',
    'image/tiff'
  ]

  return imageTypes.some((imgType) => type.toLowerCase().includes(imgType))
}

export const isExcelFile = (type) => {
  const excelTypes = [
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'xls',
    'xlsx'
  ]
  return excelTypes.some((excelType) => type?.toLowerCase().includes(excelType))
}

export const isWordFile = (type) => {
  const wordTypes = [
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'doc',
    'docx'
  ]
  return wordTypes.some((wordType) => type?.toLowerCase().includes(wordType))
}

export const getFileIconAndColor = (type) => {
  if (isPdfGeneric(type)) {
    return { icon: 'mdi-file-pdf-box', color: 'error' }
  }
  if (isExcelFile(type)) {
    return { icon: 'mdi-file-excel', color: 'success' }
  }
  if (isWordFile(type)) {
    return { icon: 'mdi-file-word', color: 'primary' }
  }
  if (type?.includes('text/')) {
    return { icon: 'mdi-file-document-outline', color: 'grey-darken-1' }
  }
  return { icon: 'mdi-file-outline', color: 'grey' }
}

export const getFileSize = (base64String: string): string => {
  let padding = 0
  if (base64String.endsWith('==')) {
    padding = 2
  } else if (base64String.endsWith('=')) {
    padding = 1
  }

  const size = base64String.length * 0.75 - padding
  return (size / 1024 / 1024).toFixed(2) // Size in MB
}
