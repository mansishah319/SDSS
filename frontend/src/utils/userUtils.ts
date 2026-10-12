import { Fields } from '@/constants/DpConstants'

export function getUserPhotoUrl(
  user: { userPhoto?: string; userPhotoType?: string; avatar?: string } | null | undefined
): string {
  if (!user) return ''
  const photo = user.userPhoto
  if (!photo) return user.avatar ?? ''
  const cleanPhoto = photo.trim().replace(/(^"|"$)/g, '')
  if (cleanPhoto.startsWith('data:image')) return cleanPhoto
  let type = user.userPhotoType ?? 'jpeg'
  if (cleanPhoto.startsWith('iVBORw0KGgo')) type = 'png'
  else if (cleanPhoto.startsWith('R0lGOD')) type = 'gif'
  else if (cleanPhoto.startsWith('UklGR')) type = 'webp'
  return `data:image/${type};base64,${cleanPhoto}`
}

export function getEmployeeFromStorage(): any {
  try {
    const storedEmployee = localStorage.getItem(Fields.LOGGED_IN_EMP)
    return storedEmployee ? JSON.parse(storedEmployee) : null
  } catch (error) {
    console.error('Error parsing employee from localStorage:', error)
    return null
  }
}
