import axios, { AxiosError, AxiosInstance } from 'axios'
import { errorPublisher, errorPublisherMsg } from '@/exception/ExceptionHandler'
import type { ErrorResponse } from '@/dto/generic/ErrorResponse'
import { useErrorStore } from '@/store/errorStore'
import { Paths } from '@/Paths'
import { translate } from '@/utils/TranslatorHelper'
import { redirectToLogin } from '@/utils/authSession'

const axiosInstance: AxiosInstance = axios.create({
  baseURL: Paths.BASE_URL,
  timeout: 120000,
  withCredentials: true, // Required for CSRF token to be sent with requests
  xsrfCookieName: 'XSRF-TOKEN', // The name of the cookie to use for the CSRF token
  xsrfHeaderName: 'X-XSRF-TOKEN' // The name of the http header that carries the CSRF token
})

// Helper function to handle redirects after showing an error
// const redirectWithDelay = (url: string, delay = 1000) => {
//   setTimeout(() => {
//     window.location.href = url
//   }, delay)
// }

// Set up interceptor for handling responses and errors globally
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const errorStore = useErrorStore()

    // Check for error response and handle specific status codes
    if (error.response) {
      if (error.response.status === 401 || error.response.status === 403) {
        redirectToLogin()
      }
      const errorData: ErrorResponse = (error.response.data as ErrorResponse) || {
        message: translate('Messages.error.general.serviceDown'),
        errorCode: '500'
      }

      // Only publish error if not already displayed (prevent duplicates)
      if (!errorStore.isErrorDisplayed) {
        errorPublisher(errorData.message, errorData.errorCode)
        errorStore.setError(errorData.message)
      }

      // Show error for a short time before redirecting
      // if (error.response.status === 401) {
      //   redirectWithDelay('/login')
      // } else if (error.response.status === 403) {
      //   redirectWithDelay('/access-denied')
      // } else if (error.response.status === 404) {
      //   redirectWithDelay('/404')
      // } else if (error.response.status >= 500) {
      //   redirectWithDelay('/error')
      // }
    } else {
      const networkErrorMsg = translate('Messages.error.general.networkError')

      // Only publish error if not already displayed (prevent duplicates)
      if (!errorStore.isErrorDisplayed) {
        errorPublisherMsg(networkErrorMsg)
        errorStore.setError(networkErrorMsg)
      }
    }

    return Promise.reject(error)
  }
)

export default axiosInstance
