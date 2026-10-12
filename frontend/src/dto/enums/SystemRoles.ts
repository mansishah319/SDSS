/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export enum MainSystemRolesKeys {
  _GUEST = 'GUEST',
  _ADMIN = 'ADMIN',
  _MEMBER = 'MEMBER',
  _VALIDATOR = 'VALIDATOR',
  _COMMANDER = 'COMMANDER',
  _MODERATOR = 'MODERATOR',
}

export type SystemRole = {
  en: string
  ar: string
  key: string
  tokenRole: string
}

export const SystemRoles: Record<string, SystemRole> = {
  GUEST: {
    en: 'Guest',
    ar: 'ضيف',
    key: 'ROLE_GUEST',
    tokenRole: 'PORTALGUEST',
  },
  ADMIN: {
    en: 'Admin',
    ar: 'أدمن',
    key: 'ROLE_ADMIN',
    tokenRole: 'PORTALADMIN',
  },
  MEMBER: {
    en: 'Member',
    ar: 'عضو',
    key: 'ROLE_MEMBER',
    tokenRole: 'PORTALROLEMEMBER',
  },
  VALIDATOR: {
    en: 'Validator',
    ar: 'ُمدقق',
    key: 'ROLE_VALIDATOR',
    tokenRole: 'PORTALROLEVALIDATOR',
  },
  COMMANDER: {
    en: 'Commander',
    ar: 'مدير',
    key: 'ROLE_COMMANDER',
    tokenRole: 'PORTALROLECOMMANDER',
  },
  MODERATOR: {
    en: 'Moderator',
    ar: 'منسق',
    key: 'ROLE_MODERATOR',
    tokenRole: 'PORTALROLEMODERATOR',
  },
}

export function fromTokenRole(tokenRole: string): SystemRole {
  const role = Object.values(SystemRoles).find(
    (r) => r.tokenRole.toLowerCase() === tokenRole.toLowerCase() || r.key.toLowerCase() === tokenRole.toLowerCase(),
  )

  if (!role) {
    throw new Error(`Unknown token role: ${tokenRole}`)
  }

  return role
}

/**
 * Converts IDM roles, backend role keys, and already-normalized role names to
 * the role values used by SDSS authorization checks.
 */
export function normalizeTokenRoles(roles: unknown): MainSystemRolesKeys[] {
  if (!Array.isArray(roles)) return []

  const normalizedRoles = roles.flatMap((role) => {
    if (typeof role !== 'string') return []

    const tokenRole = role.trim().toLowerCase()
    const systemRoleEntry = Object.entries(SystemRoles).find(([name, systemRole]) =>
      [name, systemRole.key, systemRole.tokenRole].some((value) => value.toLowerCase() === tokenRole),
    )

    if (!systemRoleEntry) return []

    return [systemRoleEntry[0] as MainSystemRolesKeys]
  })

  return [...new Set(normalizedRoles)]
}
