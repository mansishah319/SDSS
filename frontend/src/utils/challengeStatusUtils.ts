import { ChallengeStatus, toChallengeStatus } from '@/dto/challenge/ChallengeStatus'

export interface ChallengeStatusAppearance {
  key: ChallengeStatus | 'UNKNOWN'
  color: string
  backgroundColor: string
  borderColor: string
  textColor: string
  icon: string
}

const challengeStatusAliases: Record<string, ChallengeStatus> = {
  OPEN: ChallengeStatus.OPEN,
  OPENED: ChallengeStatus.OPEN,
  NEW: ChallengeStatus.NEW,
  VOTING: ChallengeStatus.VOTING,
  PAUSED: ChallengeStatus.PAUSED,
  COMPLETED: ChallengeStatus.COMPLETED,
  CLOSED: ChallengeStatus.CLOSED,
  REVISED: ChallengeStatus.REVISED,
  URGENT: ChallengeStatus.URGENT,
  UNDER_VALIDATOR_REVIEW: ChallengeStatus.UNDER_VALIDATOR_REVIEW,
  UNDER_ADMIN_REVIEW: ChallengeStatus.UNDER_ADMIN_REVIEW,
  'UNDER VALIDATOR REVIEW': ChallengeStatus.UNDER_VALIDATOR_REVIEW,
  'UNDER ADMIN REVIEW': ChallengeStatus.UNDER_ADMIN_REVIEW,
  مفتوح: ChallengeStatus.OPEN,
  جديد: ChallengeStatus.NEW,
  التصويت: ChallengeStatus.VOTING,
  'الإيقاف المؤقت': ChallengeStatus.PAUSED,
  مكتمل: ChallengeStatus.COMPLETED,
  مغلق: ChallengeStatus.CLOSED,
  'تم مراجعته': ChallengeStatus.REVISED,
  ضروري: ChallengeStatus.URGENT,
  'تحت مراجعة المدقق': ChallengeStatus.UNDER_VALIDATOR_REVIEW,
  'تحت مراجعة الأدمن': ChallengeStatus.UNDER_ADMIN_REVIEW
}

const challengeStatusAppearanceMap: Record<ChallengeStatus, ChallengeStatusAppearance> = {
  [ChallengeStatus.OPEN]: {
    key: ChallengeStatus.OPEN,
    color: '#0f6cbd',
    backgroundColor: '#eaf4ff',
    borderColor: '#b7d4f0',
    textColor: '#0b4f8c',
    icon: 'mdi-door-open'
  },
  [ChallengeStatus.NEW]: {
    key: ChallengeStatus.NEW,
    color: '#087443',
    backgroundColor: '#e9fbf3',
    borderColor: '#b9e7cb',
    textColor: '#065f38',
    icon: 'mdi-star-four-points'
  },
  [ChallengeStatus.VOTING]: {
    key: ChallengeStatus.VOTING,
    color: '#b35f00',
    backgroundColor: '#fff4e5',
    borderColor: '#f0c894',
    textColor: '#8a4a00',
    icon: 'mdi-vote'
  },
  [ChallengeStatus.PAUSED]: {
    key: ChallengeStatus.PAUSED,
    color: '#667085',
    backgroundColor: '#f2f4f7',
    borderColor: '#d0d5dd',
    textColor: '#475467',
    icon: 'mdi-pause-circle'
  },
  [ChallengeStatus.UNDER_VALIDATOR_REVIEW]: {
    key: ChallengeStatus.UNDER_VALIDATOR_REVIEW,
    color: '#b54708',
    backgroundColor: '#fff1e8',
    borderColor: '#f7c7a5',
    textColor: '#93370d',
    icon: 'mdi-account-search'
  },
  [ChallengeStatus.UNDER_ADMIN_REVIEW]: {
    key: ChallengeStatus.UNDER_ADMIN_REVIEW,
    color: '#7a3ef0',
    backgroundColor: '#f4efff',
    borderColor: '#d9c9fb',
    textColor: '#5b21b6',
    icon: 'mdi-shield-account'
  },
  [ChallengeStatus.COMPLETED]: {
    key: ChallengeStatus.COMPLETED,
    color: '#1e7a46',
    backgroundColor: '#ecfdf3',
    borderColor: '#b7ebc8',
    textColor: '#166534',
    icon: 'mdi-check-decagram'
  },
  [ChallengeStatus.CLOSED]: {
    key: ChallengeStatus.CLOSED,
    color: '#b42318',
    backgroundColor: '#fef3f2',
    borderColor: '#f3c0be',
    textColor: '#912018',
    icon: 'mdi-close-circle'
  },
  [ChallengeStatus.REVISED]: {
    key: ChallengeStatus.REVISED,
    color: '#155eef',
    backgroundColor: '#eef4ff',
    borderColor: '#c7d7fe',
    textColor: '#1849a9',
    icon: 'mdi-file-document-edit-outline'
  },
  [ChallengeStatus.URGENT]: {
    key: ChallengeStatus.URGENT,
    color: '#d92d20',
    backgroundColor: '#fff1f3',
    borderColor: '#f7b1ac',
    textColor: '#b42318',
    icon: 'mdi-alert'
  }
}

const defaultChallengeStatusAppearance: ChallengeStatusAppearance = {
  key: 'UNKNOWN',
  color: '#667085',
  backgroundColor: '#f2f4f7',
  borderColor: '#d0d5dd',
  textColor: '#344054',
  icon: 'mdi-help-circle-outline'
}

export const normalizeChallengeStatus = (status?: string | ChallengeStatus | null): ChallengeStatus | undefined => {
  if (!status) {
    return undefined
  }

  const trimmedStatus = String(status).trim()
  if (!trimmedStatus) {
    return undefined
  }

  const directMatch = toChallengeStatus(trimmedStatus)
  if (directMatch) {
    return directMatch
  }

  const normalizedKey = trimmedStatus.replace(/[\s-]+/g, '_').toUpperCase()
  const normalizedMatch = toChallengeStatus(normalizedKey)
  if (normalizedMatch) {
    return normalizedMatch
  }

  return challengeStatusAliases[trimmedStatus] || challengeStatusAliases[trimmedStatus.toUpperCase()]
}

export const formatChallengeStatusLabel = (status?: string | ChallengeStatus | null): string => {
  if (!status) {
    return ''
  }

  const normalizedStatus = normalizeChallengeStatus(status)
  if (!normalizedStatus) {
    return String(status).trim()
  }

  return normalizedStatus
    .toLowerCase()
    .split('_')
    .map((segment) => segment.charAt(0).toUpperCase() + segment.slice(1))
    .join(' ')
}

export const getChallengeStatusAppearance = (status?: string | ChallengeStatus | null): ChallengeStatusAppearance => {
  const normalizedStatus = normalizeChallengeStatus(status)
  if (!normalizedStatus) {
    return defaultChallengeStatusAppearance
  }

  return challengeStatusAppearanceMap[normalizedStatus] || defaultChallengeStatusAppearance
}
