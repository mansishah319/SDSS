import type {
  ChallengesResponse,
  SolutionsPerStageResponse,
  VotingResultResponse
} from '@/dto/challenge/ChallengesResponse'
import type { DocumentsChallengeResponse } from '@/dto/challenge/DocumentsChallengeResponse'

export interface ChallengeVideoResponse {
  id?: number
  link: string
}

export interface ChallengeDocumentViewModel extends DocumentsChallengeResponse {
  name?: string
}

export interface ChallengeReportApiResponse extends Omit<ChallengesResponse, 'assistantModerator'> {
  surveyStatus?: string
  lastQueueLevel?: number
  countOfSolutions?: number
  videosResponseList?: ChallengeVideoResponse[]
  challengeDocsResponseList: ChallengeDocumentViewModel[]
  solutionsPerStageResponseList: SolutionsPerStageResponse[]
  votingResultResponse: VotingResultResponse
  userInvitationCountedResponse: ChallengesResponse['userInvitationCountedResponse']
  moderator: ChallengesResponse['moderator']
  assistantModerator?: ChallengesResponse['assistantModerator']
}

export interface ChallengeHeaderViewModel {
  eyebrow: string
  title: string
  secondaryTitle?: string
  description: string
  sector: string
  department: string
  status: string
  statusTone: string
  visibility: string
  surveyStatus: string
  isFinal: boolean
  immediateAccess: boolean
  currentStage: number
  totalStages: number
  progressPercent: number
}

export interface ChallengeHierarchyItemViewModel {
  key: string
  label: string
  value: string
  icon: string
}

export interface ChallengeDetailsGroupViewModel {
  key: string
  title: string
  subtitle: string
  icon: string
  items: ChallengeMetadataItemViewModel[]
}

export interface ChallengeMetricCard {
  key: string
  label: string
  value: string | number
  caption?: string
  icon: string
  tone: 'primary' | 'success' | 'warning' | 'error' | 'info' | 'neutral'
  actionable?: boolean
}

export interface ChallengeVotingDatum {
  id: string
  label: string
  votes: number
  percentage: number
  isWinner: boolean
}

export interface ChallengeVotingChartModel {
  title: string
  totalVotes: number
  items: ChallengeVotingDatum[]
  leadingLabel?: string
  tieLabel?: string
  emptyLabel: string
}

export interface ChallengeStageSolutionViewModel {
  id: number
  text: string
  voteCount: number
  voteShare: number
  isFinal: boolean
  isWinner: boolean
  queueLevel: number
}

export interface ChallengeStageViewModel {
  id: string
  stageNumber: number
  title: string
  subtitle: string
  totalVotes: number
  solutionCount: number
  winnerLabel?: string
  tieLabel?: string
  isCurrent: boolean
  solutions: ChallengeStageSolutionViewModel[]
}

export interface ChallengeInvitationSummaryViewModel {
  invited: number
  pending: number
  accepted: number
  rejected: number
  online: number
}

export interface ChallengeParticipantViewModel {
  id: number
  name: string
  grpNumber: number | string
  email: string
  online: boolean
  state: 'pending' | 'accepted' | 'rejected' | 'invited'
}

export interface ChallengeMediaVideoViewModel {
  id: string
  link: string
  thumbnailUrl?: string
  playerUrl?: string
  label: string
}

export interface ChallengeAttachmentViewModel {
  id: number
  name: string
  extension: string
  icon: string
  color: string
  sizeLabel?: string
  previewUrl?: string
  downloadable: boolean
  content?: string
}

export interface ChallengeMetadataItemViewModel {
  key: string
  label: string
  value: string
  icon: string
}

export interface ChallengeTimelineItemViewModel {
  key: string
  title: string
  subtitle: string
  icon: string
  color: string
}

export interface ChallengeOutcomeViewModel {
  id: number
  title: string
  badge: string
  votes: number
  note?: string
  averageRating: number
  ratingCount: number
  participantAverageRating: number
  participantRatingCount: number
  validatorAverageRating: number
  validatorRatingCount: number
  commanderAverageRating: number
  commanderRatingCount: number
}

export type ChallengeReportSectionKey =
  | 'header'
  | 'metrics'
  | 'details'
  | 'outcomes'
  | 'fullSolutions'
  | 'stages'
  | 'voting'
  | 'participants'
  | 'media'

export type ChallengeReportSectionVisibility = Record<ChallengeReportSectionKey, boolean>

export interface ChallengeReportSectionOption {
  key: ChallengeReportSectionKey
  label: string
  icon: string
}

export interface ChallengeParticipantsCardLabels {
  title: string
  subtitle: string
  invited: string
  pending: string
  accepted: string
  rejected: string
  online: string
  offline: string
  empty: string
  pendingState: string
  acceptedState: string
  rejectedState: string
  invitedState: string
}

export interface ChallengeMediaCardLabels {
  title: string
  subtitle: string
  videos: string
  hashtags: string
  attachments: string
  openVideo: string
  openExternally: string
  download: string
  noVideos: string
  noHashtags: string
  noAttachments: string
}
