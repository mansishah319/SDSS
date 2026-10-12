/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import axios, { AxiosResponse } from 'axios'
import authHeader, {
  authHeaderMultipart,
  getLoginSurveyAuthHeader,
  getPublicApiHeader,
  getSurveyAuthHeader
} from '@/utils/auth-header'
import { Paths } from '@/Paths'
import { errorPublisher, errorPublisherMsg } from '@/exception/ExceptionHandler'
import type { ErrorResponse } from '@/dto/generic/ErrorResponse'
import { METHODS } from '@/utils/web/httpMethods.ts'
import { useErrorStore } from '@/store/errorStore'
import axiosInstance from '@/plugins/axiosInstance'
import { translate } from './TranslatorHelper'
import { assertSafeForDownload, assertSafeForUpload } from '@/utils/fileSafety'
import { redirectToLogin } from '@/utils/authSession'

// Define an interface for the optional parameters if needed
interface Params {
  [key: string]: any // This allows any type of parameter
}

export const create = async <T>(endpoint: string, data: any): Promise<T | null> => {
  try {
    const response: AxiosResponse<T> = await axios.post<T>(Paths.BASE_URL + endpoint, data, {
      headers: {
        ...authHeader(),
        'Content-Type': 'application/json'
      }
    })
    return response.data
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

const buildMultipartFormData = async (
  requestPartName: string,
  request: unknown,
  files: File[] = []
): Promise<FormData> => {
  const formData = new FormData()
  formData.append(requestPartName, new Blob([JSON.stringify(request)], { type: 'application/json' }))

  for (const file of files) {
    const safety = await assertSafeForUpload(file)
    if (!safety.safe) {
      throw new Error(`Unsafe file blocked: ${safety.reason}`)
    }
    formData.append('files', file)
  }

  return formData
}

const sendMultipart = async <T>(
  method: 'post' | 'put',
  endpoint: string,
  requestPartName: string,
  request: unknown,
  files: File[] = []
): Promise<T | null> => {
  try {
    const formData = await buildMultipartFormData(requestPartName, request, files)
    const response: AxiosResponse<T> = await axios[method]<T>(Paths.BASE_URL + endpoint, formData, {
      headers: {
        ...authHeaderMultipart()
      }
    })
    return response.data
  } catch (err: any) {
    handleErrorResponse(err)
    if (!err.response) {
      throw err
    }
    return err.response?.data
  }
}

export const createMultipart = async <T>(
  endpoint: string,
  requestPartName: string,
  request: unknown,
  files: File[] = []
): Promise<T | null> => sendMultipart<T>('post', endpoint, requestPartName, request, files)

export const updateMultipart = async <T>(
  endpoint: string,
  requestPartName: string,
  request: unknown,
  files: File[] = []
): Promise<T | null> => sendMultipart<T>('put', endpoint, requestPartName, request, files)

export const publicPost = async <T>(endpoint: string, data: any, language?: string): Promise<T | null> => {
  try {
    const response: AxiosResponse<T> = await axiosInstance.post<T>(Paths.BASE_URL + endpoint, data, {
      headers: {
        ...getPublicApiHeader(language),
        'Content-Type': 'application/json'
      }
    })
    return response.data
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

export const createWithActiveSocket = async <T>(endpoint: string, data: any): Promise<T | null> => {
  try {
    const response: AxiosResponse<T> = await axios.post<T>(Paths.SERVICE_WEBSOCKET_URL + endpoint, data, {
      headers: {
        ...authHeader(),
        'Content-Type': 'application/json'
      }
    })
    return response.data
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

export const update = async <T>(endpoint: string, data: any): Promise<T | null> => {
  try {
    const response: AxiosResponse<T> = await axios.put<T>(Paths.BASE_URL + endpoint, data, {
      headers: {
        ...authHeader(),
        'Content-Type': 'application/json'
      }
    })
    return response.data
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

export const enhancedUpdate = async <T>(endpoint: string, data: any): Promise<AxiosResponse<T> | null> => {
  try {
    return await axios.put<T>(Paths.BASE_URL + endpoint, data, {
      headers: {
        ...authHeader(),
        'Content-Type': 'application/json'
      }
    })
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

export const get = async <T>(endpoint: string): Promise<T | null> => {
  try {
    const response: AxiosResponse<T> = await axiosInstance.get<T>(Paths.BASE_URL + endpoint, {
      headers: authHeader()
    })
    return response.data
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

export const download = async <T>(endpoint: string, fileName: string): Promise<AxiosResponse<T>> => {
  return axiosInstance
    .get<T>(Paths.BASE_URL + endpoint, {
      responseType: 'blob',
      headers: authHeader()
    })
    .then(async (response) => {
      if (response.data) {
        // Assert that response.data is a Blob
        const blob = response.data as unknown as Blob

        // Basic safety check (block HTML/JS and known dangerous types)
        const safety = assertSafeForDownload({ fileName, mimeType: (blob as any).type })
        if (!safety.safe) {
          console.warn(`Download blocked: ${safety.reason}`)
          throw new Error('Unsafe file detected. Download blocked.')
        }

        // For text-based files, scan content for phishing patterns
        if (
          blob.type.startsWith('text/') ||
          fileName.endsWith('.html') ||
          fileName.endsWith('.htm') ||
          fileName.endsWith('.js') ||
          fileName.endsWith('.xml') ||
          fileName.endsWith('.svg')
        ) {
          try {
            const text = await blob.text()
            const contentSafety = assertSafeForDownload({ fileName, mimeType: blob.type, content: text })
            if (!contentSafety.safe) {
              console.warn(`Download blocked by content scan: ${contentSafety.reason}`)
              throw new Error(`Unsafe file content detected: ${contentSafety.reason}`)
            }
          } catch (error) {
            console.warn('Content scan failed, blocking download for safety')
            throw new Error('Unable to verify file safety. Download blocked.')
          }
        }

        // Create a URL for the downloaded blob
        const blobUrl = URL.createObjectURL(blob)

        // Create an anchor element to trigger the download
        const link = document.createElement('a')
        link.href = blobUrl
        link.setAttribute('download', fileName) // Specify the file name

        // Append a link to the body and click it programmatically
        document.body.appendChild(link)
        link.click()

        // Clean up the URL and element
        document.body.removeChild(link)
        URL.revokeObjectURL(blobUrl)

        console.log('File downloaded successfully!')
      }

      return response // Return the Axios response
    })
}

export const createPublicSurvey = async <T>(
  endpoint: string,
  data: any,
  language: string
): Promise<AxiosResponse<T> | null> => {
  try {
    return await axiosInstance.post<T>(Paths.BASE_URL + endpoint, data, {
      headers: { ...getSurveyAuthHeader(language) }
    })
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response
  }
}

export const getPublicSurveyAuth = async <T>(endpoint: string, language: string): Promise<AxiosResponse<T> | null> => {
  try {
    return await axiosInstance.get<T>(Paths.BASE_URL + endpoint, {
      headers: getSurveyAuthHeader(language)
    })
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response
  }
}

export const getPublicLogin = async <T>(endpoint: string, language: string): Promise<AxiosResponse<T> | null> => {
  try {
    return await axiosInstance.get<T>(Paths.BASE_URL + endpoint, {
      headers: getLoginSurveyAuthHeader(language)
    })
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response
  }
}

export const getWithPrams = async <T>(endpoint: string, params?: Params): Promise<T | null> => {
  try {
    const response: AxiosResponse<T> = await axios.get<T>(Paths.BASE_URL + endpoint, {
      headers: authHeader(),
      params // Pass the optional params here
    })
    return response.data
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

export const uploadList = async <T>(files: [], endpoint: string): Promise<AxiosResponse<T> | null> => {
  try {
    const formData = new FormData()

    // Check each file for safety
    for (const file of files) {
      if (file && typeof file === 'object' && 'name' in file && 'type' in file) {
        const safety = await assertSafeForUpload(file as File)
        if (!safety.safe) {
          throw new Error(`Unsafe file blocked: ${safety.reason}`)
        }
      }
      formData.append(`files`, file)
    }

    return await axios.post(Paths.BASE_URL + endpoint, formData, {
      headers: {
        ...authHeaderMultipart()
      }
    })
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

export const searchRequest = async <T>(
  method: 'get' | 'post' | 'put',
  endpoint: string,
  data?: any,
  params?: any,
  isMultipart = false
): Promise<T | null> => {
  try {
    let response: AxiosResponse<T> | null = null

    const config: any = {
      headers: isMultipart ? { ...authHeaderMultipart() } : { ...authHeader() }
    }

    if (method === 'get') {
      config.params = params
      response = await axios[method](Paths.BASE_URL + endpoint, config)
    } else if ((method === 'put' || method === 'post') && data !== null) {
      response = await axios[method](Paths.BASE_URL + endpoint, data, config)
    } else {
      errorPublisherMsg(`Unsupported HTTP method: ${method}`)
    }

    return response ? response.data : null
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

export const deleteRequest = async <T>(endpoint: string): Promise<T | null> => {
  try {
    const response: AxiosResponse<T> = await axios.delete<T>(Paths.BASE_URL + endpoint, {
      headers: authHeader()
    })
    return response.data
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

export const del = async <T>(endpoint: string, params?: any): Promise<T | null> => {
  try {
    const response: AxiosResponse<T> = await axios.delete<T>(Paths.BASE_URL + endpoint, {
      headers: authHeader(),
      params: params
    })
    return response.data
  } catch (err: any) {
    const errorData: ErrorResponse = err.response?.data ?? {
      message: translate('Messages.error.general.serviceDown'),
      errorCode: '500'
    }
    console.error(errorData.errorCode)
    errorPublisher(errorData.message, errorData.errorCode)
    return err.response?.data
  }
}

export const httpGenericRequest = async <T>(
  method: keyof typeof METHODS,
  endpoint: string,
  data?: any,
  params?: any,
  isMultipart = false
): Promise<T | null> => {
  try {
    let response: AxiosResponse<T> | null = null
    const config: any = {
      headers: isMultipart ? { ...authHeaderMultipart() } : { ...authHeader() }
    }

    if (method === 'get') {
      config.params = params
      response = await axios[method](Paths.BASE_URL + endpoint, config)
    } else if ((method === 'put' || method === 'post') && data !== null) {
      response = await axios[method](Paths.BASE_URL + endpoint, data, config)
    } else if (method === 'delete') {
      response = await axios[method](Paths.BASE_URL + endpoint, config)
    } else {
      errorPublisherMsg(`Unsupported HTTP method: ${method}`)
    }

    return response ? response.data : null
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

export const httpGenericWebSocketRequest = async <T>(
  method: 'get' | 'post' | 'put' | 'delete',
  endpoint: string,
  data?: any,
  params?: any,
  isMultipart = false
): Promise<T | null> => {
  try {
    let response: AxiosResponse<T> | null = null
    const config: any = {
      headers: isMultipart ? { ...authHeaderMultipart() } : { ...authHeader() }
    }

    if (method === 'get') {
      config.params = params
      response = await axios[method](Paths.SERVICE_WEBSOCKET_URL + endpoint, config)
    } else if ((method === 'put' || method === 'post') && data !== null) {
      response = await axios[method](Paths.SERVICE_WEBSOCKET_URL + endpoint, data, config)
    } else if (method === 'delete') {
      response = await axios[method](Paths.SERVICE_WEBSOCKET_URL + endpoint, config)
    } else {
      errorPublisherMsg(`Unsupported HTTP method: ${method}`)
    }

    return response ? response.data : null
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

/**
 * Generic function to handle file upload
 * @param endpoint - The API endpoint to upload the file
 * @param file - The file to be uploaded
 * @param additionalData - Any additional data to be sent with the file
 * @returns The API response or null in case of error
 */
export const uploadFile = async <T>(
  endpoint: string,
  file: File,
  additionalData?: { [key: string]: any }
): Promise<T | null> => {
  try {
    const safety = await assertSafeForUpload(file)
    if (!safety.safe) {
      throw new Error(`Unsafe file blocked: ${safety.reason}`)
    }
    const formData = new FormData()
    formData.append('file', file)

    if (additionalData) {
      Object.keys(additionalData).forEach((key) => {
        formData.append(key, additionalData[key])
      })
    }

    const response: AxiosResponse<T> = await axios.post<T>(Paths.SERVICE_WEBSOCKET_URL + endpoint, formData, {
      headers: {
        ...authHeaderMultipart()
      }
    })
    return response.data
  } catch (err: any) {
    handleErrorResponse(err)
    return err.response?.data
  }
}

const handleErrorResponse = (err: any) => {
  const errorStore = useErrorStore()

  const errorData: ErrorResponse = err.response?.data ?? {
    message: translate('Messages.error.general.serviceDown'),
    errorCode: 'UNKNOWN_ERROR'
  }

  // Determine the appropriate error message based on error code
  let errorMessage = errorData.message

  switch (errorData.errorCode) {
    case '401': // Unauthorized
      errorMessage = translate('Messages.error.unauthorized')
      break
    case '403': // Forbidden
      errorMessage = translate('Messages.error.forbidden')
      break
    case '404': // Not Found
      errorMessage = translate('Messages.error.notFound')
      break
    case '500': // Internal Server Error
      errorMessage = translate('Messages.error.internalServerError')
      break
    case '502': // Bad Gateway
      errorMessage = translate('Messages.error.badGateway')
      break
    case '503': // Service Unavailable
      errorMessage = translate('Messages.error.serviceUnavailable')
      break
    case '504': // Gateway Timeout
      errorMessage = translate('Messages.error.gatewayTimeout')
      break
    default:
      errorMessage = errorData.message || translate('Messages.error.general.serviceDown')
      break
  }

  if (err.response?.status === 401 || err.response?.status === 403) {
    redirectToLogin()
  }

  // Set error only once with the determined message
  errorStore.setError(errorMessage)

  // Optionally log the error to an external service (e.g., Sentry, LogRocket)
  console.error(`Error Code: ${errorData.errorCode}, Message: ${errorMessage}`)
}
