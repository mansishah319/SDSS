import { Fields, SecurityConstants } from '@/constants/DpConstants'

export const clearAuthSession = () => {
  localStorage.removeItem(Fields.EMPLOYEE)
  localStorage.removeItem(Fields.LOGGED_IN_EMP)
  localStorage.removeItem(SecurityConstants.TOKEN)
}

export const redirectToLogin = () => {
  clearAuthSession()
  if (window.location.pathname !== '/login') {
    window.location.replace('/login')
  }
}
