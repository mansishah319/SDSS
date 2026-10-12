<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { get } from '@/utils/GenericApiHelper'
import { Characters, URIS } from '@/constants/DpConstants'
import type { SuccessResponse } from '@/dto/generic/SuccessResponse'
import { resolveLocalizedValue } from '@/utils/localizationUtils'
import { getFileIconAndColor, getFileSize, isImageGeneric } from '@/utils/fileUtils'
import ContentLoader from '@/components/loader/ContentLoader.vue'
import ChallengeReportHeader from './report/ChallengeReportHeader.vue'
import ChallengeDetailsCard from './report/ChallengeDetailsCard.vue'
import ChallengeKpiCards from './report/ChallengeKpiCards.vue'
import ChallengeVotingChart from './report/ChallengeVotingChart.vue'
import ChallengeStageBreakdown from './report/ChallengeStageBreakdown.vue'
import ChallengeParticipantsCard from './report/ChallengeParticipantsCard.vue'
import ChallengeMediaCard from './report/ChallengeMediaCard.vue'
import { useChallengeReportPdf } from './report/useChallengeReportPdf'
import AppDialogShell from '@/components/common/AppDialogShell.vue'
import FooterComponent from '@/components/common/FooterComponent.vue'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import type {
  ChallengeAttachmentViewModel,
  ChallengeDetailsGroupViewModel,
  ChallengeHeaderViewModel,
  ChallengeHierarchyItemViewModel,
  ChallengeInvitationSummaryViewModel,
  ChallengeMediaCardLabels,
  ChallengeMediaVideoViewModel,
  ChallengeMetricCard,
  ChallengeOutcomeViewModel,
  ChallengeParticipantsCardLabels,
  ChallengeParticipantViewModel,
  ChallengeReportApiResponse,
  ChallengeReportSectionKey,
  ChallengeReportSectionOption,
  ChallengeReportSectionVisibility,
  ChallengeStageSolutionViewModel,
  ChallengeStageViewModel,
  ChallengeVotingChartModel,
  ChallengeVotingDatum
} from './report/challengeReport.types'

const invitationMetricKeys = ['invited', 'pending', 'accepted', 'rejected'] as const

type InvitationMetricKey = (typeof invitationMetricKeys)[number]

const route = useRoute()
const { locale } = useI18n()

const challenge = ref<ChallengeReportApiResponse | null>(null)
const isLoading = ref(false)
const loadError = ref<string | null>(null)
const invitationDialogOpen = ref(false)
const videoDialogOpen = ref(false)
const selectedInvitationMetric = ref<InvitationMetricKey>('invited')
const selectedVideo = ref<ChallengeMediaVideoViewModel | null>(null)
const reportSettingsOpen = ref(false)

const reportSectionStorageKey = 'sdss.challenge-report.sections.v1'
const defaultSectionVisibility: ChallengeReportSectionVisibility = {
  header: true,
  metrics: true,
  details: true,
  outcomes: true,
  fullSolutions: true,
  stages: true,
  voting: true,
  participants: true,
  media: true
}

const readSectionVisibility = (): ChallengeReportSectionVisibility => {
  try {
    const savedVisibility = JSON.parse(localStorage.getItem(reportSectionStorageKey) || '{}')
    return { ...defaultSectionVisibility, ...savedVisibility }
  } catch {
    return { ...defaultSectionVisibility }
  }
}

const sectionVisibility = ref<ChallengeReportSectionVisibility>(readSectionVisibility())

const challengeId = computed(() => String(route.params.challengeId ?? ''))
const isArabic = computed(() => locale.value.startsWith('ar'))

const labels = computed(() => {
  if (isArabic.value) {
    return {
      report: 'تقرير التحدي',
      overview: 'نظرة عامة',
      overviewSubtitle: 'جميع بيانات التحدي منظمة',
      hierarchyTitle: 'التسلسل التنظيمي الكامل',
      sector: 'القطاع',
      department: 'الإدارة العامة',
      subDepartment: 'الإدارة الفرعية',
      section: 'القسم',
      basicDetails: 'بيانات التحدي',
      basicDetailsSubtitle: 'الحالة والصلاحيات والفترة الزمنية',
      classificationDetails: 'التصنيف',
      classificationDetailsSubtitle: 'تصنيف الموضوع ومحددات القرار والتأثير',
      strategicDetails: 'الترابط الاستراتيجي والاستشرافي',
      strategicDetailsSubtitle: 'الاتجاهات والأهداف الحالية والمستقبلية',
      moderationDetails: 'الإشراف والإدارة',
      moderationDetailsSubtitle: 'المشرفون واللجنة والبيانات التعريفية',
      auditDetails: 'سجل التحديث',
      auditDetailsSubtitle: 'بيانات الإنشاء وآخر تعديل',
      highlightedOutcomes: 'المخرجات النهائية',
      highlightedOutcomesSubtitle: 'الحلول المرشحة للفوز والنتائج الأبرز',
      emptyOutcomes: 'لا توجد حلول نهائية معروضة حالياً.',
      stage: 'الجولة',
      currentStage: 'الجولة الحالية',
      totalSolutions: 'إجمالي الحلول',
      totalVotes: 'إجمالي الأصوات',
      invitedUsers: 'المستخدمون المدعوون',
      pendingInvites: 'الدعوات المعلقة',
      acceptedInvites: 'الدعوات المقبولة',
      rejectedInvites: 'الدعوات المرفوضة',
      currentQueueLevel: 'عدد الجولات',
      finalChallenge: 'تحدٍ نهائي',
      finalistBadge: 'مرشح نهائي',
      notAvailable: 'غير متوفر',
      topic: 'تصنيف الموضوع',
      field: 'مجال الموضوع',
      impact: 'تأثير الموضوع',
      decisionLevel: 'مستوى القرار',
      decisionImportance: 'أهمية القرار',
      strategicDirection: 'التوجه الاستراتيجي',
      strategicGoals: 'الهدف الاستراتيجي',
      futureStrategicDirection: 'الأولوية المستقبلية',
      futureStrategicGoals: 'التحدي المستقبلي',
      moderator: 'المشرف',
      assistantModerator: 'المشرف المساعد',
      moderatorGroupNumber: 'رقم مجموعة المشرف',
      committee: 'اللجنة',
      challengeIdLabel: 'رقم التحدي',
      immediateAccess: 'الوصول الفوري',
      finalStatus: 'تحدٍ نهائي',
      votingTime: 'مدة التصويت',
      yes: 'نعم',
      no: 'لا',
      finalists: 'الحلول النهائية',
      fullSolutions: 'سجل الحلول الكامل',
      fullSolutionsSubtitle: 'عرض داخلي تفصيلي للحلول متاح فقط للمشرف والمشرف المساعد',
      fullSolutionsEmpty: 'لا توجد حلول كاملة متاحة للعرض حالياً.',
      rank: 'الترتيب',
      points: 'النقاط',
      adminApproved: 'اعتماد الإدارة',
      approved: 'معتمد',
      notApproved: 'غير معتمد',
      moderatorEmail: 'البريد الإلكتروني للمشرف',
      createdAt: 'تاريخ الإنشاء',
      updatedAt: 'آخر تحديث',
      startTime: 'بداية التحدي',
      endTime: 'نهاية التحدي',
      surveyStatus: 'حالة الاستبيان',
      visibility: 'الخصوصية',
      status: 'الحالة',
      createdBy: 'أنشئ بواسطة',
      updatedBy: 'آخر تعديل بواسطة',
      lifecycleCreated: 'تم إنشاء التحدي',
      lifecycleStarts: 'بداية المشاركة',
      lifecycleCurrent: 'الوضع الحالي',
      lifecycleEnds: 'نهاية التحدي',
      lifecycleUpdated: 'آخر تحديث',
      winningOutcome: 'الحل الأعلى تصويتاً',
      tieBetween: 'تعادل في الصدارة بين',
      stageSolutionsWord: 'حلول',
      challengeNotFound: 'تعذر تحميل بيانات التحدي.',
      retry: 'إعادة المحاولة',
      votingTitle: 'نتائج التصويت العامة',
      votingEmpty: 'لا توجد بيانات تصويت متاحة.',
      participantsTitle: 'المشاركون والدعوات',
      participantsSubtitle: 'حالة الدعوات وتواجد المستخدمين',
      mediaTitle: 'الوسائط والمرفقات',
      mediaSubtitle: 'الهاشتاجات والملفات المرفقة وروابط الفيديو',
      pendingState: 'معلق',
      acceptedState: 'مقبول',
      rejectedState: 'مرفوض',
      invitedState: 'مدعو',
      online: 'متصل',
      offline: 'غير متصل',
      noParticipants: 'لا توجد بيانات مشاركين.',
      noVideos: 'لا توجد روابط فيديو.',
      noHashtags: 'لا توجد وسوم.',
      noAttachments: 'لا توجد مرفقات.',
      videos: 'الفيديوهات',
      hashtags: 'الهاشتاج',
      attachments: 'المرفقات',
      openVideo: 'فتح الفيديو',
      reportLoading: 'جاري تحميل تقرير التحدي',
      reportPreparing: 'جاري تجهيز تقرير التحدي',
      openExternally: 'فتح في يوتيوب',
      videoDialogSubtitle: 'مشاهدة الفيديو داخل بوابة النظام بوضع خصوصية محسّن.',
      privacyEnhanced: 'وضع خصوصية محسّن',
      nowPlaying: 'يتم الآن تشغيل',
      externalVideoHint: 'يمكنك أيضاً فتح الفيديو مباشرة في يوتيوب إذا رغبت.',
      download: 'تنزيل',
      close: 'إغلاق',
      email: 'البريد الإلكتروني',
      grpNumber: 'الرقم العام',
      invitationDetails: 'تفاصيل الدعوات',
      usersWord: 'مستخدم',
      votes: 'الأصوات',
      queueLevel: 'مستوى الجولة',
      dateRange: 'الفترة الزمنية',
      downloadPdf: 'تنزيل PDF',
      customizeReport: 'تخصيص التقرير',
      customizeReportSubtitle: 'اختر الأقسام الظاهرة في الشاشة وملف PDF',
      visibleSections: 'الأقسام الظاهرة',
      showAllSections: 'إظهار الكل',
      resetSections: 'استعادة العرض الافتراضي',
      done: 'تم',
      sectionHeader: 'ملخص التحدي',
      sectionMetrics: 'مؤشرات الأداء',
      sectionDetails: 'تفاصيل التحدي',
      sectionOutcomes: 'الحلول النهائية',
      sectionFullSolutions: 'سجل الحلول الكامل',
      sectionStages: 'الجولات والحلول',
      sectionVoting: 'نتائج التصويت',
      sectionParticipants: 'المشاركون والدعوات',
      sectionMedia: 'الوسائط والمرفقات',
      overallRating: 'تقييم الأثر',
      participantRating: 'تقييم الأثر - المشاركين',
      validatorRating: 'تقييم الأثر - المدقق',
      commanderRating: 'تقييم الأثر - منسق الجلسة',
      ratings: 'تقييمات',
      noRatings: 'لا توجد تقييمات بعد'
    }
  }

  return {
    report: 'Challenge Report',
    overview: 'Overview',
    overviewSubtitle: 'All challenge information',
    hierarchyTitle: 'Full organizational hierarchy',
    sector: 'Sector',
    department: 'Department',
    subDepartment: 'Subdepartment',
    section: 'Section',
    basicDetails: 'Challenge details',
    basicDetailsSubtitle: 'Status, access, and timeline information',
    classificationDetails: 'Classification',
    classificationDetailsSubtitle: 'Topic classification and decision context',
    strategicDetails: 'Strategic & foresight alignment',
    strategicDetailsSubtitle: 'Current and future directions and goals',
    moderationDetails: 'Moderation & governance',
    moderationDetailsSubtitle: 'Moderators, committee, and identifiers',
    auditDetails: 'Change history',
    auditDetailsSubtitle: 'Creation and latest update information',
    highlightedOutcomes: 'Highlighted outcomes',
    highlightedOutcomesSubtitle: 'Finalists, likely winners, and executive takeaways',
    emptyOutcomes: 'No final solutions are available yet.',
    stage: 'Round',
    currentStage: 'Current Round',
    totalSolutions: 'Total solutions',
    totalVotes: 'Total votes',
    invitedUsers: 'Invited users',
    pendingInvites: 'Pending invites',
    acceptedInvites: 'Accepted invites',
    rejectedInvites: 'Rejected invites',
    currentQueueLevel: 'Queue level',
    finalChallenge: 'Final challenge',
    finalistBadge: 'Finalist',
    notAvailable: 'Not available',
    topic: 'Topic classification',
    field: 'Topic field',
    impact: 'Topic impact',
    decisionLevel: 'Decision level',
    decisionImportance: 'Decision importance',
    strategicDirection: 'Strategic direction',
    strategicGoals: 'Strategic goal',
    futureStrategicDirection: 'Future priority',
    futureStrategicGoals: 'Future challenge',
    moderator: 'Moderator',
    assistantModerator: 'Assistant moderator',
    moderatorGroupNumber: 'Moderator group number',
    committee: 'Committee',
    challengeIdLabel: 'Challenge ID',
    immediateAccess: 'Immediate access',
    finalStatus: 'Final challenge',
    votingTime: 'Voting duration',
    yes: 'Yes',
    no: 'No',
    finalists: 'Finalists',
    fullSolutions: 'Full solution register',
    fullSolutionsSubtitle: 'Internal detailed view of submitted solutions available only to the moderator and assistant moderator',
    fullSolutionsEmpty: 'No full solutions are available to display.',
    rank: 'Rank',
    points: 'Points',
    adminApproved: 'Admin approval',
    approved: 'Approved',
    notApproved: 'Not approved',
    moderatorEmail: 'Moderator email',
    createdAt: 'Created at',
    updatedAt: 'Updated at',
    startTime: 'Start time',
    endTime: 'End time',
    surveyStatus: 'Survey status',
    visibility: 'Visibility',
    status: 'Status',
    createdBy: 'Created by',
    updatedBy: 'Updated by',
    lifecycleCreated: 'Challenge created',
    lifecycleStarts: 'Participation opens',
    lifecycleCurrent: 'Current status',
    lifecycleEnds: 'Challenge closes',
    lifecycleUpdated: 'Last updated',
    winningOutcome: 'Top voted solution',
    tieBetween: 'Tie for the lead between',
    stageSolutionsWord: 'solutions',
    challengeNotFound: 'Unable to load challenge data.',
    retry: 'Retry',
    votingTitle: 'Overall voting results',
    votingEmpty: 'No voting data is available.',
    participantsTitle: 'Participants & invitations',
    participantsSubtitle: 'Invitation state and user presence',
    mediaTitle: 'Media & attachments',
    mediaSubtitle: 'Video links, hashtags, and supporting files',
    pendingState: 'Pending',
    acceptedState: 'Accepted',
    rejectedState: 'Rejected',
    invitedState: 'Invited',
    online: 'Online',
    offline: 'Offline',
    noParticipants: 'No participants were returned for this challenge.',
    noVideos: 'No video links were added.',
    noHashtags: 'No hashtags were added.',
    noAttachments: 'No attachments were added.',
    videos: 'Videos',
    hashtags: 'Hashtags',
    attachments: 'Attachments',
    openVideo: 'Open video',
    reportLoading: 'Loading challenge report',
    reportPreparing: 'Preparing challenge report',
    openExternally: 'Open on YouTube',
    videoDialogSubtitle: 'Watch the video inside the portal with privacy-enhanced playback.',
    privacyEnhanced: 'Privacy-enhanced mode',
    nowPlaying: 'Now playing',
    externalVideoHint: 'You can also open this video directly on YouTube if needed.',
    download: 'Download',
    close: 'Close',
    email: 'Email',
    grpNumber: 'GRP number',
    invitationDetails: 'Invitation details',
    usersWord: 'users',
    votes: 'Votes',
    queueLevel: 'Queue level',
    dateRange: 'Date range',
    downloadPdf: 'Download PDF',
    customizeReport: 'Customize report',
    customizeReportSubtitle: 'Choose the sections shown on screen and in the PDF',
    visibleSections: 'Visible sections',
    showAllSections: 'Show all',
    resetSections: 'Restore default view',
    done: 'Done',
    sectionHeader: 'Challenge summary',
    sectionMetrics: 'Performance indicators',
    sectionDetails: 'Challenge details',
    sectionOutcomes: 'Final solutions',
    sectionFullSolutions: 'Full solution register',
    sectionStages: 'Stages and solutions',
    sectionVoting: 'Voting results',
    sectionParticipants: 'Participants and invitations',
    sectionMedia: 'Media and attachments',
    overallRating: 'Impact assessment',
    participantRating: 'Impact assessment - Participant',
    validatorRating: 'Impact assessment - Validator',
    commanderRating: 'Impact assessment - Moderator',
    ratings: 'ratings',
    noRatings: 'No ratings yet'
  }
})

const reportSectionOptions = computed<ChallengeReportSectionOption[]>(() => [
  { key: 'header', label: labels.value.sectionHeader, icon: 'mdi-card-account-details-outline' },
  { key: 'metrics', label: labels.value.sectionMetrics, icon: 'mdi-view-dashboard-outline' },
  { key: 'details', label: labels.value.sectionDetails, icon: 'mdi-clipboard-text-outline' },
  { key: 'outcomes', label: labels.value.sectionOutcomes, icon: 'mdi-trophy-outline' },
  { key: 'fullSolutions', label: labels.value.sectionFullSolutions, icon: 'mdi-lightbulb-group-outline' },
  { key: 'stages', label: labels.value.sectionStages, icon: 'mdi-layers-triple-outline' },
  { key: 'voting', label: labels.value.sectionVoting, icon: 'mdi-chart-bar-stacked' },
  { key: 'participants', label: labels.value.sectionParticipants, icon: 'mdi-account-group-outline' },
  { key: 'media', label: labels.value.sectionMedia, icon: 'mdi-folder-multimedia-outline' }
])

const visibleSectionCount = computed(() => Object.values(sectionVisibility.value).filter(Boolean).length)

const setSectionVisibility = (key: ChallengeReportSectionKey, visible: boolean | null) => {
  sectionVisibility.value = { ...sectionVisibility.value, [key]: Boolean(visible) }
}

const showAllReportSections = () => {
  sectionVisibility.value = { ...defaultSectionVisibility }
}

watch(
  sectionVisibility,
  (visibility) => localStorage.setItem(reportSectionStorageKey, JSON.stringify(visibility)),
  { deep: true }
)

const displayValue = (value?: string | number | null) => {
  if (value === null || value === undefined || value === '') {
    return labels.value.notAvailable
  }

  return String(value)
}

const localizeText = (ar?: string | null, en?: string | null, fallback?: string | null) => {
  return (
    resolveLocalizedValue({
      locale: locale.value,
      ar: ar || undefined,
      en: en || undefined,
      fallback: fallback || undefined
    }) || labels.value.notAvailable
  )
}

const formatDateTime = (value?: string | null) => {
  if (!value) {
    return labels.value.notAvailable
  }

  const parsedDate = new Date(value)
  if (Number.isNaN(parsedDate.getTime())) {
    return value
  }

  return new Intl.DateTimeFormat(isArabic.value ? 'ar-AE' : 'en-GB', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(parsedDate)
}

const normalizeOnlineStatus = (value: unknown) => {
  if (typeof value === 'boolean') {
    return value
  }

  if (typeof value === 'string') {
    const normalized = value.toLowerCase()
    return normalized === 'true' || normalized === 'online' || normalized === '1'
  }

  if (typeof value === 'number') {
    return value === 1
  }

  return false
}

const extractYoutubeId = (link: string) => {
  try {
    const url = new URL(link)
    if (url.hostname.includes('youtu.be')) {
      return url.pathname.replace('/', '') || undefined
    }

    return url.searchParams.get('v') || undefined
  } catch {
    return undefined
  }
}

const buildYoutubePlayerUrl = (link: string) => {
  const youtubeId = extractYoutubeId(link)
  if (!youtubeId) {
    return undefined
  }

  const query = new URLSearchParams({
    autoplay: '1',
    rel: '0',
    modestbranding: '1',
    playsinline: '1',
    iv_load_policy: '3'
  })

  return `https://www.youtube-nocookie.com/embed/${youtubeId}?${query.toString()}`
}

const resolveStatusTone = (status?: string | null): ChallengeMetricCard['tone'] => {
  const value = status?.toLowerCase() || ''

  if (!value) return 'neutral'
  if (value.includes('مراجعة') || value.includes('review') || value.includes('pending')) return 'warning'
  if (value.includes('نهائي') || value.includes('final') || value.includes('مكتمل')) return 'success'
  if (value.includes('رفض') || value.includes('rejected') || value.includes('error')) return 'error'
  if (value.includes('start') || value.includes('active') || value.includes('جاري')) return 'info'
  return 'primary'
}

const uniqueById = <T extends { id?: number | string }>(items: T[]) => {
  const seen = new Set<string>()
  return items.filter((item) => {
    const key = String(item.id ?? JSON.stringify(item))
    if (seen.has(key)) return false
    seen.add(key)
    return true
  })
}

const isInvitationMetricKey = (key: string): key is InvitationMetricKey => {
  return invitationMetricKeys.includes(key as InvitationMetricKey)
}

const handleMetricSelect = (key: string) => {
  if (!isInvitationMetricKey(key)) {
    return
  }

  selectedInvitationMetric.value = key
  invitationDialogOpen.value = true
}

const loadChallenge = async () => {
  if (!challengeId.value) {
    challenge.value = null
    return
  }

  isLoading.value = true
  loadError.value = null

  try {
    const response = await get<SuccessResponse<ChallengeReportApiResponse>>(
      `${URIS.CHALLENGES.BASE}${Characters.SLASH}${challengeId.value}`
    )

    if (!response?.data) {
      challenge.value = null
      loadError.value = labels.value.challengeNotFound
      return
    }

    challenge.value = response.data as ChallengeReportApiResponse
  } catch (error) {
    console.error('Error loading challenge report', error)
    challenge.value = null
    loadError.value = labels.value.challengeNotFound
  } finally {
    isLoading.value = false
  }
}

watch(challengeId, loadChallenge, { immediate: true })

const localizedTitle = computed(() =>
  challenge.value
    ? localizeText(challenge.value.titleAr, challenge.value.titleEn, challenge.value.localTitle)
    : labels.value.notAvailable
)

const localizedDescription = computed(() =>
  challenge.value
    ? localizeText(challenge.value.descriptionAr, challenge.value.descriptionEn, challenge.value.localDescription)
    : labels.value.notAvailable
)

const resolveNamedValue = (
  response?: { localName?: string | null; name?: string | null } | null,
  fallback?: string | null
) => displayValue(response?.localName || response?.name || fallback)

const formatBoolean = (value?: boolean | number | null) => (Boolean(value) ? labels.value.yes : labels.value.no)

const allSolutions = computed(() => {
  const directSolutions = challenge.value?.solutionFullResponseList || []
  const stagedSolutions = (challenge.value?.solutionsPerStageResponseList || []).flatMap(
    (stage) => stage.solutionResponseList || []
  )
  return uniqueById([...directSolutions, ...stagedSolutions])
})

const canViewFullSolutions = computed(
  () => Boolean(challenge.value?.currentUserIsTheChallengeModeratorOrAssistantModerator)
)

const fullSolutionRecords = computed(() => {
  return uniqueById(challenge.value?.solutionFullResponseList || []).sort((left, right) => {
    const leftRank = left.rank ?? Number.MAX_SAFE_INTEGER
    const rightRank = right.rank ?? Number.MAX_SAFE_INTEGER

    if (leftRank !== rightRank) {
      return leftRank - rightRank
    }

    return (right.voteCount ?? 0) - (left.voteCount ?? 0)
  })
})

const solutionLookup = computed(() => {
  const map = new Map<string, string>()
  allSolutions.value.forEach((solution) => map.set(String(solution.id), solution.solutionText))
  return map
})

const overallVotingItems = computed<ChallengeVotingDatum[]>(() => {
  const voting = challenge.value?.votingResultResponse
  if (!voting?.solutionTextList?.length) return []

  const maxVotes = Math.max(...(voting.countOfVotes || [0]), 0)

  return voting.solutionTextList.map((solutionKey, index) => ({
    id: `${solutionKey}-${index}`,
    label: solutionLookup.value.get(String(solutionKey)) || `#${solutionKey}`,
    votes: voting.countOfVotes?.[index] ?? 0,
    percentage: voting.percentageOfVotes?.[index] ?? 0,
    isWinner: maxVotes > 0 && (voting.countOfVotes?.[index] ?? 0) === maxVotes
  }))
})

const votingChartModel = computed<ChallengeVotingChartModel>(() => {
  const winners = overallVotingItems.value.filter((item) => item.isWinner && item.votes > 0)

  return {
    title: labels.value.votingTitle,
    totalVotes: challenge.value?.votingResultResponse?.totalVotes ?? 0,
    items: overallVotingItems.value,
    leadingLabel: winners.length === 1 ? `${labels.value.winningOutcome}: ${winners[0].label}` : undefined,
    tieLabel:
      winners.length > 1 ? `${labels.value.tieBetween} ${winners.map((item) => item.label).join('، ')}` : undefined,
    emptyLabel: labels.value.votingEmpty
  }
})

const currentStage = computed(() => challenge.value?.queLevel ?? 0)
const totalStages = computed(() => {
  const reported = challenge.value?.lastQueueLevel ?? 0
  const inferred = Math.max(
    currentStage.value,
    ...(challenge.value?.solutionsPerStageResponseList || []).map((stage) => stage.stageNumber || 0),
    0
  )
  return reported || inferred || 1
})

const headerModel = computed<ChallengeHeaderViewModel>(() => ({
  eyebrow: labels.value.report,
  title: localizedTitle.value,
  secondaryTitle: challenge.value?.localTitle || localizedTitle.value,
  description: localizedDescription.value,
  sector: displayValue(challenge.value?.sectorName),
  department: displayValue(challenge.value?.departmentName),
  status: displayValue(challenge.value?.status),
  statusTone: resolveStatusTone(challenge.value?.status),
  visibility: displayValue(challenge.value?.visibility),
  surveyStatus: displayValue(challenge.value?.surveyStatus),
  isFinal: Boolean(challenge.value?.isFinal),
  immediateAccess: Boolean(challenge.value?.allowImmediateAccess),
  currentStage: currentStage.value,
  totalStages: totalStages.value,
  progressPercent: Math.min(100, Math.max(0, (currentStage.value / Math.max(totalStages.value, 1)) * 100))
}))

const summaryMetrics = computed<ChallengeMetricCard[]>(() => {
  const invitation = challenge.value?.userInvitationCountedResponse

  return [
    {
      key: 'solutions',
      label: labels.value.totalSolutions,
      value: challenge.value?.countOfSolutions ?? allSolutions.value.length,
      caption: `${totalStages.value} ${labels.value.stageSolutionsWord}`,
      icon: 'mdi-lightbulb-on-outline',
      tone: 'primary'
    },
    {
      key: 'finalists',
      label: labels.value.finalists,
      value: allSolutions.value.filter((solution) => solution.isFinal).length,
      caption: featuredOutcomes.value[0]?.title || labels.value.emptyOutcomes,
      icon: 'mdi-trophy-outline',
      tone: 'success'
    },
    {
      key: 'queue',
      label: labels.value.currentQueueLevel,
      value: `${currentStage.value}/${totalStages.value}`,
      caption: displayValue(challenge.value?.surveyStatus),
      icon: 'mdi-layers-triple-outline',
      tone: 'info'
    },
    {
      key: 'votes',
      label: labels.value.totalVotes,
      value: challenge.value?.votingResultResponse?.totalVotes ?? 0,
      caption: votingChartModel.value.tieLabel || votingChartModel.value.leadingLabel,
      icon: 'mdi-chart-bar-stacked',
      tone: 'success'
    },
    {
      key: 'invited',
      label: labels.value.invitedUsers,
      value: invitation?.countOfInvitationUsers ?? 0,
      icon: 'mdi-account-group-outline',
      tone: 'neutral',
      actionable: true
    },
    {
      key: 'pending',
      label: labels.value.pendingInvites,
      value: invitation?.countOfInvitationPending ?? 0,
      icon: 'mdi-timer-sand',
      tone: 'warning',
      actionable: true
    },
    {
      key: 'accepted',
      label: labels.value.acceptedInvites,
      value: invitation?.countOfInvitationAccepted ?? 0,
      icon: 'mdi-account-check-outline',
      tone: 'success',
      actionable: true
    },
    {
      key: 'rejected',
      label: labels.value.rejectedInvites,
      value: invitation?.countOfInvitationRejected ?? 0,
      icon: 'mdi-account-cancel-outline',
      tone: 'error',
      actionable: true
    }
  ]
})

const hierarchyItems = computed<ChallengeHierarchyItemViewModel[]>(() => [
  {
    key: 'sector',
    label: labels.value.sector,
    value: displayValue(challenge.value?.sectorName),
    icon: 'mdi-domain'
  },
  {
    key: 'department',
    label: labels.value.department,
    value: displayValue(challenge.value?.departmentName),
    icon: 'mdi-office-building-outline'
  },
  {
    key: 'subDepartment',
    label: labels.value.subDepartment,
    value: displayValue(challenge.value?.subDepartmentName),
    icon: 'mdi-source-branch'
  },
  {
    key: 'section',
    label: labels.value.section,
    value: displayValue(challenge.value?.sectionName),
    icon: 'mdi-office-building-marker-outline'
  }
])

const detailGroups = computed<ChallengeDetailsGroupViewModel[]>(() => [
  {
    key: 'basic',
    title: labels.value.basicDetails,
    subtitle: labels.value.basicDetailsSubtitle,
    icon: 'mdi-clipboard-text-outline',
    items: [
      {
        key: 'status',
        label: labels.value.status,
        value: displayValue(challenge.value?.status),
        icon: 'mdi-progress-clock'
      },
      {
        key: 'surveyStatus',
        label: labels.value.surveyStatus,
        value: displayValue(challenge.value?.surveyStatus),
        icon: 'mdi-poll'
      },
      {
        key: 'visibility',
        label: labels.value.visibility,
        value: displayValue(challenge.value?.visibility),
        icon: 'mdi-eye-outline'
      },
      {
        key: 'immediateAccess',
        label: labels.value.immediateAccess,
        value: formatBoolean(challenge.value?.allowImmediateAccess),
        icon: 'mdi-door-open'
      },
      {
        key: 'finalStatus',
        label: labels.value.finalStatus,
        value: formatBoolean(challenge.value?.isFinal),
        icon: 'mdi-flag-checkered'
      },
      {
        key: 'startTime',
        label: labels.value.startTime,
        value: formatDateTime(challenge.value?.startTime),
        icon: 'mdi-calendar-start'
      },
      {
        key: 'endTime',
        label: labels.value.endTime,
        value: formatDateTime(challenge.value?.endTime),
        icon: 'mdi-calendar-end'
      },
      {
        key: 'votingTime',
        label: labels.value.votingTime,
        value: displayValue(challenge.value?.votingTime),
        icon: 'mdi-timer-outline'
      }
    ]
  },
  {
    key: 'classification',
    title: labels.value.classificationDetails,
    subtitle: labels.value.classificationDetailsSubtitle,
    icon: 'mdi-shape-outline',
    items: [
      {
        key: 'topic',
        label: labels.value.topic,
        value: resolveNamedValue(
          challenge.value?.topicClassificationResponse,
          challenge.value?.topicClassification
        ),
        icon: 'mdi-tag-outline'
      },
      {
        key: 'field',
        label: labels.value.field,
        value: resolveNamedValue(challenge.value?.fieldResponse, challenge.value?.field),
        icon: 'mdi-view-grid-outline'
      },
      {
        key: 'impact',
        label: labels.value.impact,
        value: resolveNamedValue(challenge.value?.impactResponse, challenge.value?.impact),
        icon: 'mdi-chart-timeline-variant-shimmer'
      },
      {
        key: 'decisionLevel',
        label: labels.value.decisionLevel,
        value: resolveNamedValue(challenge.value?.levelOfDecisionResponse, challenge.value?.levelOfDecision),
        icon: 'mdi-stairs-up'
      },
      {
        key: 'decisionImportance',
        label: labels.value.decisionImportance,
        value: resolveNamedValue(
          challenge.value?.importanceOfDecisionResponse,
          challenge.value?.importanceOfDecision
        ),
        icon: 'mdi-alert-decagram-outline'
      }
    ]
  },
  {
    key: 'strategic',
    title: labels.value.strategicDetails,
    subtitle: labels.value.strategicDetailsSubtitle,
    icon: 'mdi-compass-outline',
    items: [
      {
        key: 'strategicDirection',
        label: labels.value.strategicDirection,
        value: resolveNamedValue(challenge.value?.strategicDirection),
        icon: 'mdi-compass-outline'
      },
      {
        key: 'strategicGoals',
        label: labels.value.strategicGoals,
        value: resolveNamedValue(challenge.value?.strategicGoals),
        icon: 'mdi-target'
      },
      {
        key: 'futureStrategicDirection',
        label: labels.value.futureStrategicDirection,
        value: resolveNamedValue(challenge.value?.futureStrategicDirection),
        icon: 'mdi-telescope'
      },
      {
        key: 'futureStrategicGoals',
        label: labels.value.futureStrategicGoals,
        value: resolveNamedValue(challenge.value?.futureStrategicGoals),
        icon: 'mdi-chart-timeline-variant'
      }
    ]
  },
  {
    key: 'moderation',
    title: labels.value.moderationDetails,
    subtitle: labels.value.moderationDetailsSubtitle,
    icon: 'mdi-account-tie-outline',
    items: [
      {
        key: 'moderator',
        label: labels.value.moderator,
        value: displayValue(challenge.value?.moderator?.fullName),
        icon: 'mdi-account-tie-outline'
      },
      {
        key: 'moderatorEmail',
        label: labels.value.moderatorEmail,
        value: displayValue(challenge.value?.moderator?.email),
        icon: 'mdi-email-outline'
      },
      {
        key: 'assistantModerator',
        label: labels.value.assistantModerator,
        value: displayValue(challenge.value?.assistantModerator?.fullName),
        icon: 'mdi-account-star-outline'
      },
      {
        key: 'assistantModeratorEmail',
        label: labels.value.email,
        value: displayValue(challenge.value?.assistantModerator?.email),
        icon: 'mdi-email-outline'
      },
      {
        key: 'moderatorGroupNumber',
        label: labels.value.moderatorGroupNumber,
        value: displayValue(challenge.value?.moderatorGrpNumber),
        icon: 'mdi-identifier'
      },
      {
        key: 'committee',
        label: labels.value.committee,
        value: displayValue(challenge.value?.committeeId),
        icon: 'mdi-account-multiple-check-outline'
      },
      {
        key: 'challengeId',
        label: labels.value.challengeIdLabel,
        value: displayValue(challenge.value?.id),
        icon: 'mdi-pound'
      }
    ]
  },
  {
    key: 'audit',
    title: labels.value.auditDetails,
    subtitle: labels.value.auditDetailsSubtitle,
    icon: 'mdi-history',
    items: [
      {
        key: 'createdAt',
        label: labels.value.createdAt,
        value: formatDateTime(challenge.value?.createdAt),
        icon: 'mdi-calendar-plus'
      },
      {
        key: 'createdBy',
        label: labels.value.createdBy,
        value: displayValue(challenge.value?.createdBy),
        icon: 'mdi-account-plus-outline'
      },
      {
        key: 'updatedAt',
        label: labels.value.updatedAt,
        value: formatDateTime(challenge.value?.updatedAt),
        icon: 'mdi-calendar-refresh'
      },
      {
        key: 'updatedBy',
        label: labels.value.updatedBy,
        value: displayValue(challenge.value?.updatedBy),
        icon: 'mdi-account-edit-outline'
      }
    ]
  }
])

const featuredOutcomes = computed<ChallengeOutcomeViewModel[]>(() => {
  return uniqueById(
    allSolutions.value.filter((solution) => solution.isFinal).sort((left, right) => right.voteCount - left.voteCount)
  )
    .slice(0, 4)
    .map((solution, index) => ({
      id: solution.id,
      title: solution.solutionText,
      badge: index === 0 ? labels.value.winningOutcome : labels.value.finalistBadge,
      votes: solution.voteCount ?? 0,
      note: `${labels.value.queueLevel}: ${solution.queLevel}`,
      averageRating: solution.averageRating ?? 0,
      ratingCount: solution.ratingCount ?? 0,
      participantAverageRating: solution.participantAverageRating ?? 0,
      participantRatingCount: solution.participantRatingCount ?? 0,
      validatorAverageRating: solution.validatorAverageRating ?? 0,
      validatorRatingCount: solution.validatorRatingCount ?? 0,
      commanderAverageRating: solution.commanderAverageRating ?? 0,
      commanderRatingCount: solution.commanderRatingCount ?? 0
    }))
})

const stageModels = computed<ChallengeStageViewModel[]>(() => {
  return (challenge.value?.solutionsPerStageResponseList || []).map((stage) => {
    const stageVotes = stage.votingResultPerStageResponse?.votingResultResponse?.totalVotes ?? 0
    const solutions = stage.solutionResponseList || []
    const maxVotes = Math.max(...solutions.map((solution) => solution.voteCount ?? 0), 0)
    const winners = solutions.filter((solution) => maxVotes > 0 && (solution.voteCount ?? 0) === maxVotes)

    const mappedSolutions: ChallengeStageSolutionViewModel[] = solutions.map((solution) => ({
      id: solution.id,
      text: solution.solutionText,
      voteCount: solution.voteCount ?? 0,
      voteShare: stageVotes ? ((solution.voteCount ?? 0) / stageVotes) * 100 : 0,
      isFinal: Boolean(solution.isFinal),
      isWinner: winners.some((winner) => winner.id === solution.id),
      queueLevel: solution.queLevel
    }))

    return {
      id: `stage-${stage.stageNumber}`,
      stageNumber: stage.stageNumber || 0,
      title: `${labels.value.stage} ${stage.stageNumber || 0}`,
      subtitle: `${solutions.length} ${labels.value.stageSolutionsWord} • ${stageVotes} ${labels.value.votes}`,
      totalVotes: stageVotes,
      solutionCount: solutions.length,
      winnerLabel: winners.length === 1 ? winners[0].solutionText : undefined,
      tieLabel:
        winners.length > 1
          ? `${labels.value.tieBetween} ${winners.map((winner) => winner.solutionText).join('، ')}`
          : undefined,
      isCurrent: (stage.stageNumber || 0) === currentStage.value,
      solutions: mappedSolutions
    }
  })
})

const challengeReportPdf = useChallengeReportPdf({
  challenge,
  isArabic,
  localizedTitle,
  reportLabel: computed(() => labels.value.report)
})

const { expandedStagePanels, handlePdfDownload, isPdfExporting } = challengeReportPdf

const participantSummary = computed<ChallengeInvitationSummaryViewModel>(() => ({
  invited: challenge.value?.userInvitationCountedResponse?.countOfInvitationUsers ?? 0,
  pending: challenge.value?.userInvitationCountedResponse?.countOfInvitationPending ?? 0,
  accepted: challenge.value?.userInvitationCountedResponse?.countOfInvitationAccepted ?? 0,
  rejected: challenge.value?.userInvitationCountedResponse?.countOfInvitationRejected ?? 0,
  online: (challenge.value?.userInvitationCountedResponse?.invitedInternalUserResponse || []).filter((user) =>
    normalizeOnlineStatus(user.onlineStatus)
  ).length
}))

const participantStates = computed(() => {
  const stateMap = new Map<number, ChallengeParticipantViewModel['state']>()
  ;(challenge.value?.userInvitationCountedResponse?.pendingUsersResponse || []).forEach((user) =>
    stateMap.set(user.id, 'pending')
  )
  ;(challenge.value?.userInvitationCountedResponse?.acceptedUsersResponse || []).forEach((user) =>
    stateMap.set(user.id, 'accepted')
  )
  ;(challenge.value?.userInvitationCountedResponse?.rejectedUsersResponse || []).forEach((user) =>
    stateMap.set(user.id, 'rejected')
  )
  return stateMap
})

const participantRows = computed<ChallengeParticipantViewModel[]>(() => {
  return (challenge.value?.userInvitationCountedResponse?.invitedInternalUserResponse || []).map((user) => ({
    id: user.id,
    name: user.fullName || labels.value.notAvailable,
    grpNumber: user.grpNumber ?? labels.value.notAvailable,
    email: user.email || labels.value.notAvailable,
    online: normalizeOnlineStatus(user.onlineStatus),
    state: participantStates.value.get(user.id) || 'invited'
  }))
})

const invitationDialogTitle = computed(() => {
  switch (selectedInvitationMetric.value) {
    case 'pending':
      return labels.value.pendingInvites
    case 'accepted':
      return labels.value.acceptedInvites
    case 'rejected':
      return labels.value.rejectedInvites
    default:
      return labels.value.invitedUsers
  }
})

const invitationDialogUsers = computed<ChallengeParticipantViewModel[]>(() => {
  if (selectedInvitationMetric.value === 'invited') {
    return participantRows.value
  }

  return participantRows.value.filter((participant) => participant.state === selectedInvitationMetric.value)
})

const participantLabels = computed<ChallengeParticipantsCardLabels>(() => ({
  title: labels.value.participantsTitle,
  subtitle: labels.value.participantsSubtitle,
  invited: labels.value.invitedUsers,
  pending: labels.value.pendingInvites,
  accepted: labels.value.acceptedInvites,
  rejected: labels.value.rejectedInvites,
  online: labels.value.online,
  offline: labels.value.offline,
  empty: labels.value.noParticipants,
  pendingState: labels.value.pendingState,
  acceptedState: labels.value.acceptedState,
  rejectedState: labels.value.rejectedState,
  invitedState: labels.value.invitedState
}))

const mediaVideos = computed<ChallengeMediaVideoViewModel[]>(() => {
  const rawLinks = new Set<string>()
  ;(challenge.value?.youtubeUrls || []).forEach((link) => rawLinks.add(link))
  ;(challenge.value?.videosResponseList || []).forEach((video) => rawLinks.add(video.link))

  return Array.from(rawLinks).map((link, index) => {
    const youtubeId = extractYoutubeId(link)
    return {
      id: `video-${index}`,
      link,
      thumbnailUrl: youtubeId ? `https://i.ytimg.com/vi/${youtubeId}/hqdefault.jpg` : undefined,
      playerUrl: buildYoutubePlayerUrl(link),
      label: `Video ${index + 1}`
    }
  })
})

const handleOpenVideo = (video: ChallengeMediaVideoViewModel) => {
  selectedVideo.value = video
  videoDialogOpen.value = true
}

const closeVideoDialog = () => {
  videoDialogOpen.value = false
}

const mediaAttachments = computed<ChallengeAttachmentViewModel[]>(() => {
  return (challenge.value?.challengeDocsResponseList || []).map((documentItem, index) => {
    const extension = documentItem.type || 'file'
    const { icon, color } = getFileIconAndColor(extension)
    const name = documentItem.name || `attachment-${documentItem.id || index + 1}.${extension}`
    const content = documentItem.content || undefined

    return {
      id: documentItem.id || index + 1,
      name,
      extension,
      icon,
      color,
      sizeLabel: content ? `${getFileSize(content)} MB` : undefined,
      previewUrl: content && isImageGeneric(extension) ? `data:image/${extension};base64,${content}` : undefined,
      downloadable: Boolean(content),
      content
    }
  })
})

const mediaLabels = computed<ChallengeMediaCardLabels>(() => ({
  title: labels.value.mediaTitle,
  subtitle: labels.value.mediaSubtitle,
  videos: labels.value.videos,
  hashtags: labels.value.hashtags,
  attachments: labels.value.attachments,
  openVideo: labels.value.openVideo,
  openExternally: labels.value.openExternally,
  download: labels.value.download,
  noVideos: labels.value.noVideos,
  noHashtags: labels.value.noHashtags,
  noAttachments: labels.value.noAttachments
}))

watch(videoDialogOpen, (isOpen) => {
  if (!isOpen) {
    selectedVideo.value = null
  }
})
</script>

<template>
  <v-container fluid class="challenge-report-screen px-0">
    <GenericDashboardHeaderView
      :title-en="labels.report"
      :title-ar="labels.report"
      :subtitle-en="localizedTitle"
      :subtitle-ar="localizedTitle"
      icon="mdi-file-chart-outline"
      :show-back-button="true"
      :show-pdf-button="Boolean(challenge)"
      :pdf-label-en="labels.downloadPdf"
      :pdf-label-ar="labels.downloadPdf"
      :pdf-loading="isPdfExporting"
      back-fallback-route-name="MyChallenges"
      :total-value="challenge?.countOfSolutions ?? allSolutions.length"
      :total-label-en="labels.totalSolutions"
      :total-label-ar="labels.totalSolutions"
      @download-pdf="handlePdfDownload"
    />

    <div v-if="isLoading" class="challenge-report-screen__loading">
      <ContentLoader variant="details" :message="labels.reportLoading" />
    </div>

    <v-alert v-else-if="loadError" type="error" variant="tonal" rounded="xl" class="challenge-report-screen__alert">
      <div class="challenge-report-screen__alert-body">
        <span>{{ loadError }}</span>
        <v-btn color="error" variant="outlined" @click="loadChallenge">{{ labels.retry }}</v-btn>
      </div>
    </v-alert>

    <template v-else-if="challenge">
      <div class="challenge-report-screen__content-shell">
        <div class="challenge-report-controls">
          <div class="challenge-report-controls__summary">
            <v-icon size="20">mdi-tune-variant</v-icon>
            <span>{{ labels.visibleSections }}: {{ visibleSectionCount }}/{{ reportSectionOptions.length }}</span>
          </div>
          <v-btn color="primary" variant="outlined" prepend-icon="mdi-tune-variant" @click="reportSettingsOpen = true">
            {{ labels.customizeReport }}
          </v-btn>
        </div>

        <div
          id="challenge-report-export-root"
          class="challenge-report-screen__export-root pdf-stage-breakdown-wrapper"
          :ref="(element) => (challengeReportPdf.reportExportRoot.value = element as HTMLElement | null)"
        >
          <ChallengeReportHeader v-if="sectionVisibility.header" :header="headerModel" />

          <div v-if="sectionVisibility.metrics" class="challenge-report-screen__kpis">
            <ChallengeKpiCards :items="summaryMetrics" @select="handleMetricSelect" />
          </div>

          <v-row class="challenge-report-screen__grid" dense>
            <v-col cols="12" lg="8" class="challenge-report-screen__grid-column">
              <ChallengeDetailsCard
                v-if="sectionVisibility.details"
                :title="labels.overview"
                :subtitle="labels.overviewSubtitle"
                :hierarchy-title="labels.hierarchyTitle"
                :hierarchy="hierarchyItems"
                :groups="detailGroups"
              />

              <v-card v-if="sectionVisibility.outcomes" class="report-section-card" rounded="xl" elevation="0">
                <v-card-text class="report-section-card__body">
                  <div class="report-section-card__heading">
                    <div>
                      <p class="report-section-card__eyebrow">{{ labels.highlightedOutcomesSubtitle }}</p>
                      <h3 class="report-section-card__title font-bukra-bold">{{ labels.highlightedOutcomes }}</h3>
                    </div>
                  </div>

                  <div v-if="featuredOutcomes.length" class="outcomes-grid">
                    <article v-for="outcome in featuredOutcomes" :key="outcome.id" class="outcome-card">
                      <div class="outcome-card__head">
                        <v-chip color="success" size="small" variant="tonal">{{ outcome.badge }}</v-chip>
                        <span class="outcome-card__votes">{{ outcome.votes }} {{ labels.votes }}</span>
                      </div>
                      <strong class="outcome-card__title font-dubai-bold">{{ outcome.title }}</strong>
                      <span v-if="outcome.note" class="outcome-card__note">{{ outcome.note }}</span>

                      <div class="outcome-rating">
                        <div class="outcome-rating__summary">
                          <div>
                            <span class="outcome-rating__label">{{ labels.overallRating }}</span>
                            <strong class="outcome-rating__value font-bukra-bold">
                              {{ outcome.ratingCount ? outcome.averageRating.toFixed(1) : '—' }}
                            </strong>
                          </div>
                          <div class="outcome-rating__stars" :aria-label="labels.overallRating">
                            <span
                              v-for="star in 5"
                              :key="star"
                              :class="{ 'is-active': star <= Math.round(outcome.averageRating) }"
                            >★</span>
                          </div>
                          <span class="outcome-rating__count">
                            {{ outcome.ratingCount ? `${outcome.ratingCount} ${labels.ratings}` : labels.noRatings }}
                          </span>
                        </div>

                        <div class="outcome-rating__breakdown">
                          <div class="outcome-rating__row">
                            <span>{{ labels.validatorRating }}</span>
                            <strong>{{ outcome.validatorAverageRating.toFixed(1) }}/5</strong>
                            <small>{{ outcome.validatorRatingCount }}</small>
                          </div>
                          <div class="outcome-rating__row">
                            <span>{{ labels.commanderRating }}</span>
                            <strong>{{ outcome.commanderAverageRating.toFixed(1) }}/5</strong>
                            <small>{{ outcome.commanderRatingCount }}</small>
                          </div>
                        </div>
                      </div>
                    </article>
                  </div>
                  <div v-else class="report-empty-state">{{ labels.emptyOutcomes }}</div>
                </v-card-text>
              </v-card>

              <v-card
                v-if="canViewFullSolutions && sectionVisibility.fullSolutions"
                class="report-section-card challenge-moderator-solutions"
                rounded="xl"
                elevation="0"
              >
                <v-card-text class="report-section-card__body">
                  <div class="report-section-card__heading">
                    <div>
                      <p class="report-section-card__eyebrow">{{ labels.fullSolutionsSubtitle }}</p>
                      <h3 class="report-section-card__title font-bukra-bold">{{ labels.fullSolutions }}</h3>
                    </div>
                  </div>

                  <div v-if="fullSolutionRecords.length" class="moderator-solutions-grid">
                    <article
                      v-for="solution in fullSolutionRecords"
                      :key="solution.id"
                      class="moderator-solution-card"
                    >
                      <div class="moderator-solution-card__head">
                        <div>
                          <span class="moderator-solution-card__id">#{{ solution.id }}</span>
                          <strong class="moderator-solution-card__title font-dubai-bold">
                            {{ solution.solutionText }}
                          </strong>
                        </div>

                        <div class="moderator-solution-card__chips">
                          <v-chip v-if="solution.isFinal" size="small" color="primary" variant="tonal">
                            {{ labels.finalChallenge }}
                          </v-chip>
                          <v-chip
                            size="small"
                            :color="solution.isAdminAgree ? 'success' : 'warning'"
                            variant="tonal"
                          >
                            {{ labels.adminApproved }}:
                            {{ solution.isAdminAgree ? labels.approved : labels.notApproved }}
                          </v-chip>
                        </div>
                      </div>

                      <div class="moderator-solution-card__stats">
                        <div class="moderator-solution-card__stat">
                          <span>{{ labels.rank }}</span>
                          <strong>{{ solution.rank ?? labels.notAvailable }}</strong>
                        </div>
                        <div class="moderator-solution-card__stat">
                          <span>{{ labels.points }}</span>
                          <strong>{{ solution.point ?? 0 }}</strong>
                        </div>
                        <div class="moderator-solution-card__stat">
                          <span>{{ labels.votes }}</span>
                          <strong>{{ solution.voteCount ?? 0 }}</strong>
                        </div>
                        <div class="moderator-solution-card__stat">
                          <span>{{ labels.queueLevel }}</span>
                          <strong>{{ solution.queLevel ?? labels.notAvailable }}</strong>
                        </div>
                      </div>
                    </article>
                  </div>
                  <div v-else class="report-empty-state">{{ labels.fullSolutionsEmpty }}</div>
                </v-card-text>
              </v-card>

              <ChallengeStageBreakdown
                v-if="sectionVisibility.stages"
                class="challenge-stage-breakdown-component"
                v-model="expandedStagePanels"
                :stages="stageModels"
              />
            </v-col>

            <v-col cols="12" lg="4" class="challenge-report-screen__sidebar">
              <ChallengeVotingChart v-if="sectionVisibility.voting" :model="votingChartModel" />
              <ChallengeParticipantsCard
                v-if="sectionVisibility.participants"
                :summary="participantSummary"
                :participants="participantRows"
                :labels="participantLabels"
              />
              <ChallengeMediaCard
                v-if="sectionVisibility.media"
                :videos="mediaVideos"
                :hashtags="challenge.hashtagTexts || []"
                :attachments="mediaAttachments"
                :labels="mediaLabels"
                @open-video="handleOpenVideo"
              />
            </v-col>
          </v-row>
        </div>

        <ContentLoader
          v-if="isPdfExporting"
          class="challenge-report-screen__pdf-loader"
          variant="badge"
          overlay
          compact-badge
          :message="labels.reportPreparing"
        />
      </div>

      <AppDialogShell
        v-model="reportSettingsOpen"
        :title="labels.customizeReport"
        :subtitle="labels.customizeReportSubtitle"
        icon="mdi-tune-variant"
        tone="success"
        :max-width="760"
      >
        <template #meta>
          <v-chip color="white" variant="flat" size="small">
            {{ visibleSectionCount }}/{{ reportSectionOptions.length }}
          </v-chip>
        </template>

        <div class="report-visibility-settings">
          <article v-for="section in reportSectionOptions" :key="section.key" class="report-visibility-option">
            <div class="report-visibility-option__identity">
              <span class="report-visibility-option__icon"><v-icon>{{ section.icon }}</v-icon></span>
              <strong class="font-dubai-bold">{{ section.label }}</strong>
            </div>
            <v-switch
              :model-value="sectionVisibility[section.key]"
              :disabled="visibleSectionCount === 1 && sectionVisibility[section.key]"
              color="primary"
              density="compact"
              hide-details
              @update:model-value="setSectionVisibility(section.key, $event)"
            />
          </article>
        </div>

        <template #actions>
          <div class="report-visibility-actions">
            <v-btn variant="text" color="primary" prepend-icon="mdi-refresh" @click="showAllReportSections">
              {{ labels.resetSections }}
            </v-btn>
            <v-btn color="primary" variant="flat" @click="reportSettingsOpen = false">{{ labels.done }}</v-btn>
          </div>
        </template>
      </AppDialogShell>

      <AppDialogShell
        v-model="invitationDialogOpen"
        :title="invitationDialogTitle"
        :subtitle="labels.invitationDetails"
        icon="mdi-account-group-outline"
        tone="success"
        :max-width="860"
      >
        <template #meta>
          <v-chip color="white" variant="flat" size="small">
            {{ invitationDialogUsers.length }} {{ labels.usersWord }}
          </v-chip>
        </template>

        <div class="invitation-dialog">
          <div v-if="invitationDialogUsers.length" class="invitation-dialog__list">
            <article v-for="participant in invitationDialogUsers" :key="participant.id" class="invitation-dialog__item">
              <div class="invitation-dialog__avatar">{{ participant.name.charAt(0) }}</div>

              <div class="invitation-dialog__body">
                <div class="invitation-dialog__head">
                  <strong class="font-dubai-bold">{{ participant.name }}</strong>
                  <div class="invitation-dialog__chips">
                    <v-chip size="small" variant="tonal" :color="participant.online ? 'success' : 'default'">
                      {{ participant.online ? labels.online : labels.offline }}
                    </v-chip>
                    <v-chip
                      size="small"
                      variant="tonal"
                      :color="
                        participant.state === 'accepted'
                          ? 'success'
                          : participant.state === 'pending'
                            ? 'warning'
                            : participant.state === 'rejected'
                              ? 'error'
                              : 'primary'
                      "
                    >
                      {{
                        participant.state === 'accepted'
                          ? labels.acceptedState
                          : participant.state === 'pending'
                            ? labels.pendingState
                            : participant.state === 'rejected'
                              ? labels.rejectedState
                              : labels.invitedState
                      }}
                    </v-chip>
                  </div>
                </div>

                <div class="invitation-dialog__meta">
                  <span>{{ labels.grpNumber }}: {{ participant.grpNumber }}</span>
                  <span>{{ labels.email }}: {{ participant.email }}</span>
                </div>
              </div>
            </article>
          </div>

          <div v-else class="report-empty-state">{{ labels.noParticipants }}</div>
        </div>

        <template #actions>
          <div class="invitation-dialog__actions">
            <v-btn color="primary" variant="flat" @click="invitationDialogOpen = false">{{ labels.close }}</v-btn>
          </div>
        </template>
      </AppDialogShell>

      <AppDialogShell
        v-model="videoDialogOpen"
        :title="selectedVideo?.label || labels.openVideo"
        :subtitle="labels.videoDialogSubtitle"
        icon="mdi-play-box-multiple-outline"
        tone="neutral"
        :max-width="1080"
        content-class="challenge-video-dialog__content"
        @close="closeVideoDialog"
      >
        <template #meta>
          <v-chip color="white" variant="flat" size="small">YouTube</v-chip>
          <v-chip color="white" variant="outlined" size="small">{{ labels.privacyEnhanced }}</v-chip>
        </template>

        <div class="challenge-video-dialog">
          <div class="challenge-video-dialog__stage">
            <div class="challenge-video-dialog__player-shell">
              <iframe
                v-if="videoDialogOpen && selectedVideo?.playerUrl"
                class="challenge-video-dialog__iframe"
                :src="selectedVideo.playerUrl"
                :title="selectedVideo.label"
                allow="
                  accelerometer;
                  autoplay;
                  clipboard-write;
                  encrypted-media;
                  gyroscope;
                  picture-in-picture;
                  web-share;
                "
                allowfullscreen
                referrerpolicy="strict-origin-when-cross-origin"
              />

              <div v-else class="challenge-video-dialog__empty">
                <v-icon size="34">mdi-video-off-outline</v-icon>
                <span>{{ labels.noVideos }}</span>
              </div>
            </div>

            <div class="challenge-video-dialog__summary">
              <p class="challenge-video-dialog__eyebrow">{{ labels.nowPlaying }}</p>
              <h3 class="challenge-video-dialog__title font-bukra-bold">{{ selectedVideo?.label }}</h3>
              <p class="challenge-video-dialog__description">{{ labels.externalVideoHint }}</p>
            </div>
          </div>

          <div class="challenge-video-dialog__footer-wrap">
            <FooterComponent :justify-content="'justify-content-start'" class="challenge-video-dialog__footer" />
          </div>
        </div>

        <template #actions>
          <div class="challenge-video-dialog__actions">
            <v-btn color="primary" variant="text" :href="selectedVideo?.link" target="_blank" rel="noopener noreferrer">
              {{ labels.openExternally }}
            </v-btn>
            <v-btn color="primary" variant="flat" @click="closeVideoDialog">{{ labels.close }}</v-btn>
          </div>
        </template>
      </AppDialogShell>
    </template>
  </v-container>
</template>

<style scoped lang="scss">
.challenge-report-screen {
  color: #16362d;
}

.challenge-report-screen__loading,
.challenge-report-screen__alert {
  margin-bottom: 20px;
}

.challenge-report-screen__content-shell,
.challenge-report-screen__export-root {
  position: relative;
}

.challenge-report-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
  padding: 12px 16px;
  border: 1px solid rgba(13, 82, 62, 0.1);
  border-radius: 18px;
  background: #fff;
}

.challenge-report-controls__summary {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #526b63;
  font-size: 0.86rem;
}

.challenge-report-screen__pdf-loader {
  z-index: 4;
  inset: 72px auto auto 50% !important;
  width: min(460px, calc(100% - 32px));
  transform: translateX(-50%);
  min-height: 190px;
  max-height: 220px;
  overflow: hidden;
  border: 1px solid rgba(13, 82, 62, 0.14);
  box-shadow: 0 18px 46px rgba(13, 82, 62, 0.14);
}

.challenge-report-screen__alert-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.challenge-report-screen__kpis,
.challenge-report-screen__grid {
  margin-top: 20px;
}

.report-section-card {
  border: 1px solid rgba(13, 82, 62, 0.1);
  background: linear-gradient(180deg, #ffffff 0%, #f8fcfa 100%);
  margin-bottom: 20px;
}

.report-section-card__body {
  padding: 22px;
}

.report-section-card__heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.report-section-card__eyebrow,
.metadata-tile__label,
.timeline-shell__item-subtitle,
.outcome-card__note,
.outcome-card__votes {
  color: #73837d;
}

.report-section-card__eyebrow {
  margin: 0;
  font-size: 0.8rem;
}

.report-section-card__title,
.timeline-shell__title {
  margin: 8px 0 0;
  color: #16362d;
}

.metadata-grid,
.outcomes-grid,
.moderator-solutions-grid {
  display: grid;
  gap: 12px;
}

.metadata-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.metadata-tile,
.outcome-card,
.timeline-shell {
  border: 1px solid rgba(13, 82, 62, 0.08);
  border-radius: 22px;
  background: #fff;
}

.metadata-tile {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 16px;
}

.metadata-tile__icon {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: rgba(13, 107, 77, 0.08);
  color: #0d6b4d;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.metadata-tile__body {
  min-width: 0;
}

.metadata-tile__label,
.timeline-shell__item-subtitle,
.outcome-card__note,
.outcome-card__votes {
  display: block;
  font-size: 0.82rem;
}

.metadata-tile__value,
.timeline-shell__item-title,
.outcome-card__title {
  display: block;
  margin-top: 6px;
  overflow-wrap: anywhere;
}

.timeline-shell {
  padding: 18px;
  height: 100%;
}

.timeline-shell__title {
  margin: 0 0 14px;
}

.timeline-shell__item-title {
  color: #16362d;
  font-weight: 700;
}

.outcomes-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.moderator-solutions-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.outcome-card {
  padding: 16px;
}

.challenge-moderator-solutions {
  border-color: rgba(13, 82, 62, 0.14);
  background: radial-gradient(circle at top right, rgba(18, 159, 99, 0.08), transparent 36%),
  linear-gradient(180deg, #ffffff 0%, #f6fcf9 100%);
}

.moderator-solution-card {
  display: grid;
  gap: 16px;
  padding: 18px;
  border: 1px solid rgba(13, 82, 62, 0.08);
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff 0%, #fbfefd 100%);
}

.moderator-solution-card__head,
.moderator-solution-card__chips,
.moderator-solution-card__stats {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.moderator-solution-card__head {
  align-items: flex-start;
  justify-content: space-between;
}

.moderator-solution-card__id {
  display: inline-block;
  margin-bottom: 8px;
  color: #0d6b4d;
  font-size: 0.82rem;
  font-weight: 700;
}

.moderator-solution-card__title {
  display: block;
  color: #17362d;
  line-height: 1.7;
}

.moderator-solution-card__stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.moderator-solution-card__stat {
  padding: 12px 14px;
  border-radius: 16px;
  background: #f5faf7;
}

.moderator-solution-card__stat span {
  display: block;
  color: #73837d;
  font-size: 0.78rem;
}

.moderator-solution-card__stat strong {
  display: block;
  margin-top: 6px;
  color: #16362d;
  font-size: 1rem;
}

.outcome-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.outcome-card__title {
  margin-top: 14px;
  color: #17362d;
}

.outcome-rating {
  display: grid;
  gap: 12px;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid rgba(13, 82, 62, 0.08);
}

.outcome-rating__summary {
  display: grid;
  grid-template-columns: auto minmax(110px, 1fr) auto;
  align-items: center;
  gap: 12px;
}

.outcome-rating__label,
.outcome-rating__count,
.outcome-rating__row span,
.outcome-rating__row small {
  color: #73837d;
  font-size: 0.76rem;
}

.outcome-rating__label,
.outcome-rating__value {
  display: block;
}

.outcome-rating__value {
  margin-top: 3px;
  color: #0d6b4d;
  font-size: 1.15rem;
}

.outcome-rating__stars {
  display: flex;
  gap: 3px;
  color: #d5ddd9;
  font-size: 1.15rem;
}

.outcome-rating__stars .is-active {
  color: #f4b740;
}

.outcome-rating__breakdown {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
}

.outcome-rating__row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  align-items: center;
  gap: 8px;
  padding: 9px 11px;
  border-radius: 12px;
  background: #f5faf7;
}

.outcome-rating__row strong {
  color: #17362d;
}

.report-visibility-settings {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.report-visibility-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  border: 1px solid rgba(13, 82, 62, 0.08);
  border-radius: 16px;
  background: #fbfdfc;
}

.report-visibility-option__identity,
.report-visibility-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.report-visibility-option__icon {
  width: 38px;
  height: 38px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-radius: 12px;
  background: rgba(13, 107, 77, 0.08);
  color: #0d6b4d;
}

.report-visibility-actions {
  width: 100%;
  justify-content: space-between;
}

.report-empty-state {
  padding: 20px;
  text-align: center;
  border-radius: 20px;
  background: #f8fbfa;
  color: #73837d;
}

.challenge-report-screen__sidebar {
  display: grid;
  gap: 20px;
  align-content: start;
}

.invitation-dialog {
  display: grid;
  gap: 12px;
}

.invitation-dialog__list {
  display: grid;
  gap: 12px;
}

.invitation-dialog__item {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  padding: 16px;
  border: 1px solid rgba(13, 82, 62, 0.08);
  border-radius: 20px;
  background: #fdfefd;
}

.invitation-dialog__avatar {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: rgba(13, 107, 77, 0.12);
  color: #0d6b4d;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
  text-transform: uppercase;
}

.invitation-dialog__body {
  min-width: 0;
  flex: 1;
}

.invitation-dialog__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.invitation-dialog__chips,
.invitation-dialog__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.invitation-dialog__meta {
  margin-top: 10px;
  color: #6a7a73;
  font-size: 0.84rem;
}

.invitation-dialog__actions {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.challenge-video-dialog {
  display: grid;
  gap: 18px;
}

.challenge-video-dialog__stage {
  display: grid;
  gap: 16px;
}

.challenge-video-dialog__player-shell {
  position: relative;
  overflow: hidden;
  border-radius: 24px;
  border: 1px solid rgba(13, 82, 62, 0.08);
  background: radial-gradient(circle at top left, rgba(255, 255, 255, 0.12), transparent 30%),
  linear-gradient(145deg, #0c2531 0%, #112d3b 44%, #091821 100%);
  aspect-ratio: 16 / 9;
  box-shadow: 0 24px 48px rgba(7, 24, 20, 0.18);
}

.challenge-video-dialog__iframe,
.challenge-video-dialog__empty {
  width: 100%;
  height: 100%;
  border: 0;
}

.challenge-video-dialog__empty {
  display: grid;
  place-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.9);
}

.challenge-video-dialog__summary {
  padding: 18px 20px;
  border-radius: 22px;
  border: 1px solid rgba(13, 82, 62, 0.08);
  background: linear-gradient(180deg, rgba(248, 252, 250, 0.96) 0%, #ffffff 100%);
}

.challenge-video-dialog__eyebrow,
.challenge-video-dialog__description {
  margin: 0;
  color: #6a7a73;
}

.challenge-video-dialog__eyebrow {
  font-size: 0.78rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.challenge-video-dialog__title {
  margin: 10px 0 8px;
  color: #16362d;
}

.challenge-video-dialog__footer-wrap {
  overflow: hidden;
  border-radius: 18px;
}

.challenge-video-dialog__footer :deep(.custom-main-footer-style) {
  border-radius: 18px;
}

.challenge-video-dialog__actions {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 960px) {
  .metadata-grid,
  .outcomes-grid,
  .moderator-solutions-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .challenge-report-controls,
  .report-visibility-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .report-visibility-settings,
  .outcome-rating__breakdown {
    grid-template-columns: 1fr;
  }

  .outcome-rating__summary {
    grid-template-columns: 1fr;
  }

  .challenge-report-screen__alert-body {
    flex-direction: column;
    align-items: flex-start;
  }

  .invitation-dialog__item,
  .invitation-dialog__head,
  .moderator-solution-card__head,
  .invitation-dialog__actions,
  .challenge-video-dialog__actions {
    flex-direction: column;
    align-items: flex-start;
  }

  .report-section-card__body {
    padding: 18px;
  }

  .challenge-video-dialog__actions :deep(.v-btn) {
    width: 100%;
  }

  .moderator-solution-card__stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
