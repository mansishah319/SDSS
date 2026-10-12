/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

// api-utils.js
import axios from 'axios'
import authHeader, { authHeaderMultipart, generalAuthHeader } from '@/utils/auth-header'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'
import { assertSafeForDownload } from '@/utils/fileSafety'

export const search = async (url, requestBody, pageNumber, pageSize, sortField, sortDirection) => {
  try {
    // debugger
    const response = await axios.get(url, {
      params: {
        page: pageNumber,
        size: pageSize,
        sort: `${sortField},${sortDirection}`,

        grpNumber: requestBody?.grpNumber,
        department: requestBody?.department,
        fullName: requestBody?.fullName, // (not in Java class but kept if used in backend)
        expertLevel: requestBody?.expertLevel,
        email: requestBody?.email,
        mobile: requestBody?.mobile,
        role: requestBody?.role,
        status: requestBody?.status,
        searchAny: requestBody?.searchAny,

        // ✅ Added missing optional fields
        language: requestBody?.language,
        type: requestBody?.type,
        sector: requestBody?.sector,
        areaOfExpertises: requestBody?.areaOfExpertises
      },
      headers: authHeader()
    })
    return response.data
  } catch (error) {
    throw new Error('Error fetching data: ' + error)
  }
}
export const updateLanguage = async (url, language) => {
  try {
    const response = await axios.get(url, {
      params: {
        language: language
      },
      headers: authHeader()
    })

    return response.data
  } catch (error) {
    throw new Error('Error update user data: ' + error)
  }
}

export const retrieveDefaultUserLanguage = async (url) => {
  try {
    return await axios.get(url, {
      headers: authHeader()
    })
  } catch (error) {
    console.error('Error retrieving user language:', error)
    if (error.response) {
      console.error('Error Response:', error.response.data)
    }
    throw new Error('Error retrieving user language code issue : ' + error.message)
  }
}

/**
 * Generic file download function that handles different file types
 * @param {Object} file - File object containing base64 data and metadata
 * @param {string} file.file - Base64 encoded file content
 * @param {string} file.fileName - Name of the file
 * @param {string} file.type - MIME type of the file
 */
export const downloadFile = (file) => {
  if (!file?.file || !file?.fileName) {
    console.error('Invalid file data for download')
    return
  }

  try {
    // Decode base64 content for scanning
    let content = null
    try {
      content = atob(file.file)
    } catch (e) {
      console.warn('Unable to decode base64 content for scanning')
    }

    const safety = assertSafeForDownload({
      fileName: file.fileName,
      mimeType: file.type,
      content: content
    })
    if (!safety.safe) {
      console.warn(`Download blocked: ${safety.reason}`)
      toast.error('Blocked unsafe file download')
      return
    }

    const link = document.createElement('a')
    link.href = `data:${file.type};base64,${file.file}`
    link.download = file.fileName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } catch (error) {
    console.error('Error downloading file:', error)
  }
}

/**
 * Specialized function for downloading Excel files
 * @param {Object} file - File object containing base64 data and metadata
 */
export const downloadExcelFile = (file) => {
  if (!file?.file || !file?.fileName) {
    console.error('Invalid Excel file data')
    return
  }

  try {
    // Basic safety check for Excel files
    const safety = assertSafeForDownload({
      fileName: file.fileName,
      mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    if (!safety.safe) {
      console.warn(`Download blocked: ${safety.reason}`)
      toast.error('Blocked unsafe file download')
      return
    }

    const link = document.createElement('a')
    // Force Excel MIME type
    const mimeType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    link.href = `data:${mimeType};base64,${file.file}`
    // Ensure .xlsx extension
    link.download = file.fileName.endsWith('.xlsx') ? file.fileName : `${file.fileName}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } catch (error) {
    console.error('Error downloading Excel file:', error)
  }
}

/**
 * Preview file in a new tab
 * @param {Object} file - File object containing base64 data and metadata
 */
export const previewFile = (file) => {
  if (!file?.file || !file?.type) {
    console.error('Invalid file data for preview')
    return
  }

  try {
    const dataUrl = `data:${file.type};base64,${file.file}`
    window.open(dataUrl, '_blank')
  } catch (error) {
    console.error('Error previewing file:', error)
  }
}

export const getAll = async (url) => {
  try {
    const response = await axios.get(url, {
      headers: authHeader()
    })
    return response.data.data
  } catch (error) {
    throw new Error('Error fetching data: ' + error)
  }
}

export const getAllWithPagination = async (url, pageNumber, pageSize, sortField, sortDirection) => {
  try {
    const response = await axios.get(url, {
      params: {
        page: pageNumber,
        size: pageSize,
        sort: `${sortField},${sortDirection}`
      },
      headers: authHeader()
    })

    return response.data
  } catch (error) {
    throw new Error('Error fetching data: ' + error)
  }
}

export const create = async (url, formData) =>
  await axios
    .post(url, formData, {
      headers: authHeaderMultipart()
    })
    .then((response) => {
      return response
    })
    .catch((error) => {
      console.error('API Error:', error)
      toast.error(error.response.data.message)
      throw new error()
    })

export const getByPathVariableId = async (url) => {
  try {
    const response = await axios.get(url, {
      headers: authHeader()
    })
    return response.data
  } catch (error) {
    throw new Error('Error fetching data: ' + error)
  }
}

export const getImageByPathVariableId = async (apiUrl) => {
  try {
    const response = await axios.get(apiUrl, {
      responseType: 'arraybuffer',
      headers: generalAuthHeader()
    })
    if (response.status === 200) {
      const blob = new Blob([response.data], { type: 'image/png' })
      return URL.createObjectURL(blob)
    } else {
      return null
    }
  } catch (error) {
    console.error('Error fetching the image:', error)
    return null
  }
}

export const update = async (url, requestBody) =>
  await axios
    .put(url, requestBody, {
      headers: authHeader()
    })
    .then((response) => {
      console.log('API Response:', response.data)
      return response.data
    })
    .catch((error) => {
      toast.error(error.response.data.message)
      throw new error()
    })

export const remove = async (url) =>
  await axios
    .delete(url, {
      headers: authHeader()
    })
    .then((response) => {
      console.log('API Response:', response)
      return response.data
    })
    .catch((error) => {
      console.error('API Error:', error)
    })

// utils/api-utils.ts

export const downloadChatFile = (file) => {
  const mimeType = getMimeType(file.type)

  // Safety check before creating blob
  const safety = assertSafeForDownload({
    fileName: file.fileName,
    mimeType: mimeType
  })
  if (!safety.safe) {
    console.warn(`Download blocked: ${safety.reason}`)
    toast.error('Blocked unsafe file download')
    return
  }

  const blob = base64ToBlob(file.file, mimeType)
  const url = URL.createObjectURL(blob)

  const a = document.createElement('a')
  a.href = url
  a.download = file.fileName
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)

  // Clean up
  setTimeout(() => URL.revokeObjectURL(url), 100)
}

const base64ToBlob = (base64, mimeType) => {
  const byteCharacters = atob(base64)
  const byteArrays = []

  for (let offset = 0; offset < byteCharacters.length; offset += 512) {
    const slice = byteCharacters.slice(offset, offset + 512)
    const byteNumbers = new Array(slice.length)

    for (let i = 0; i < slice.length; i++) {
      byteNumbers[i] = slice.charCodeAt(i)
    }

    const byteArray = new Uint8Array(byteNumbers)
    byteArrays.push(byteArray)
  }

  return new Blob(byteArrays, { type: mimeType })
}

const getMimeType = (type) => {
  const typeMap = {
    jpg: 'image/jpeg',
    jpeg: 'image/jpeg',
    png: 'image/png',
    gif: 'image/gif',
    pdf: 'application/pdf',
    svg: 'image/svg+xml',
    doc: 'application/msword',
    docx: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    xls: 'application/vnd.ms-excel',
    xlsx: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    txt: 'text/plain'
  }

  // Remove any extra characters and get a base type
  const baseType = type.split(';')[0].trim().toLowerCase()
  return typeMap[baseType] || 'application/octet-stream'
}
