/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { ChallengesCreationRequest } from '@/dto/challenge/ChallengesCreationRequest.ts'
import { ChallengesResponse } from '@/dto/challenge/ChallengesResponse.ts'
import { ChallengeReminders } from '@/dto/reminder/ChallengeReminders.ts'
import { SolutionFullResponse } from '@/dto/challenge/SolutionFullResponse.ts'
import { DocumentsChallengeResponse } from '@/dto/challenge/DocumentsChallengeResponse.ts'
import { DepartmentResponse } from '@/dto/department/DepartmentResponse.ts'
import { CommitteeResponse } from '@/dto/committee/CommitteeResponse.ts'

export const fillChallengeRequest = (viewResponse: ChallengesResponse): ChallengesCreationRequest => {
  return {
    // User mappings
    invitedUsersIds:
      viewResponse.userInvitationCountedResponse?.invitedInternalUserResponse?.map((user: any) => user.id) || [],
    validatorsIds: viewResponse.validatorList?.map((validator: any) => validator.id) || [],
    moderatorId: viewResponse.moderator?.grpNumber || null,
    assistantModeratorGrp: viewResponse?.assistantModerator?.grpNumber || null,

    // Time mappings
    startTime: viewResponse.startTime || '',
    endTime: viewResponse.endTime || '',

    // Basic challenge info
    visibility: viewResponse.visibility || '',
    hashtagTexts: viewResponse.hashtagTexts || [],
    allowImmediateAccess: viewResponse.allowImmediateAccess || false,

    // Strategic mappings
    strategicGoalsId: viewResponse?.strategicGoals?.id || null,
    strategicDirectionId: viewResponse?.strategicDirection?.id || null,
    futureStrategicGoalsId: viewResponse?.futureStrategicGoals?.id || null,
    futureStrategicDirectionId: viewResponse?.futureStrategicDirection?.id || null,

    // Media and content
    youtubeLinks: viewResponse.youtubeUrls || [],
    titleAr: viewResponse.titleAr || '',
    titleEn: viewResponse.titleEn || '',
    descriptionAr: viewResponse.descriptionAr || '',
    descriptionEn: viewResponse.descriptionEn || '',

    // Lists (will be populated separately)
    challengeRemindersDateTimeList: [],
    solutionsTexts: viewResponse.solutionsTexts || [],
    documentsIds: viewResponse.challengeDocsResponseList?.map((doc: any) => doc.id) || [],

    // Department mappings
    departmentId: viewResponse.departmentId?.toString() || '',
    departmentName: viewResponse.departmentName || '',
    subDepartmentId: viewResponse.subDepartmentId?.toString() || '',
    subDepartmentName: viewResponse.subDepartmentName || '',
    sectionId: viewResponse.sectionId?.toString() || '',
    sectionName: viewResponse.sectionName || '',

    // Settings mappings
    urgent: false, // Default value can be enhanced if needed
    topicClassification: viewResponse?.topicClassificationResponse?.id || null,
    levelOfDecision: viewResponse?.levelOfDecisionResponse?.id || null,
    importanceOfDecision: viewResponse?.importanceOfDecisionResponse?.id || null,
    impact: viewResponse?.impactResponse?.id || null,
    field: viewResponse?.fieldResponse?.id || null,
    committeeId: viewResponse?.committeeId || null // Map committeeId from response
  }
}

/**
 * Maps challenge reminders to formatted date strings
 */
export const mapChallengeReminders = (
  challengeReminders: ChallengeReminders[],
  formatReminder: (_reminderTime: string) => string
): string[] => {
  const reminders: string[] = []
  if (challengeReminders && challengeReminders.length > 0) {
    challengeReminders.forEach((reminder) => {
      reminders.push(formatReminder(reminder.reminderTime))
    })
  }
  return reminders
}

/**
 * Maps solutions to form-compatible format
 */
export const mapSolutionsTexts = (solutionFullResponseList: SolutionFullResponse[]): { value: string }[] => {
  return solutionFullResponseList.map((solution: SolutionFullResponse) => ({
    value: solution.solutionText
  }))
}

/**
 * Maps document IDs and document list
 */
export const mapDocuments = (challengeDocsResponseList: DocumentsChallengeResponse[]) => {
  return {
    docsIds: challengeDocsResponseList.map((doc: DocumentsChallengeResponse) => doc.id),
    challengeDocsResponseList: challengeDocsResponseList
  }
}

/**
 * Maps user selections for team members and validators
 */
export const mapUserSelections = (viewResponseData: ChallengesResponse) => {
  return {
    selectedUsers: viewResponseData.userInvitationCountedResponse.invitedInternalUserResponse.map((user: any) =>
      Number(user.grpNumber)
    ),
    selectedValidatorUsers: viewResponseData.validatorList.map((validator: any) => Number(validator.grpNumber))
  }
}

/**
 * Maps department selections
 */
export const mapDepartmentSelections = (
  viewResponseData: ChallengesResponse,
  departmentDtoResponseList: DepartmentResponse[],
  subDepartmentDtoResponseList: DepartmentResponse[],
  sectionsDtoResponseList: DepartmentResponse[]
) => {
  const selectedDepartment = departmentDtoResponseList.find((department) => {
    return Number(department.id) == Number(viewResponseData.departmentId)
  }) as DepartmentResponse

  const selectedSubDepartment =
    viewResponseData.subDepartmentId && subDepartmentDtoResponseList.length > 0
      ? (subDepartmentDtoResponseList.find((subDept) => {
        return Number(subDept.id) == Number(viewResponseData.subDepartmentId)
      }) as DepartmentResponse)
      : null

  const selectedSection =
    viewResponseData.sectionId && sectionsDtoResponseList.length > 0
      ? (sectionsDtoResponseList.find((section) => {
        return Number(section.id) == Number(viewResponseData.sectionId)
      }) as DepartmentResponse)
      : null

  return {
    selectedDepartment,
    selectedSubDepartment,
    selectedSection,
    selectedDepartmentForSearch: null,
    selectedDepartmentForValidators: null
  }
}

/**
 * Maps all form state from API response
 */
export const mapFormState = (
  viewResponseData: ChallengesResponse,
  departmentDtoResponseList: DepartmentResponse[],
  subDepartmentDtoResponseList: DepartmentResponse[],
  sectionsDtoResponseList: DepartmentResponse[],
  formatReminder: (_reminderTime: string) => string,
  committeeListDropListResponse: CommitteeResponse[]
) => {
  // Map main challenge request
  const challengeRequest = fillChallengeRequest(viewResponseData)

  // Map reminders
  challengeRequest.challengeRemindersDateTimeList = mapChallengeReminders(
    viewResponseData.challengeReminders,
    formatReminder
  )

  // Map solutions
  const solutionsTexts = viewResponseData.solutionsTexts?.map((text: string) => ({ value: text })) || []

  // Map documents
  const { docsIds, challengeDocsResponseList } = mapDocuments(viewResponseData.challengeDocsResponseList)

  // Map user selections
  const { selectedUsers, selectedValidatorUsers } = mapUserSelections(viewResponseData)

  const selectedCommittee = committeeListDropListResponse.find((committee) => {
    return Number(committee.teamID) == Number(viewResponseData.committeeId)
  })

  // Map department selections
  const departmentSelections = mapDepartmentSelections(
    viewResponseData,
    departmentDtoResponseList,
    subDepartmentDtoResponseList,
    sectionsDtoResponseList
  )

  // Map content arrays
  const hashtagTexts = Array.isArray(viewResponseData.hashtagTexts) ? [...viewResponseData.hashtagTexts] : []
  const youtubeLinks = Array.isArray(viewResponseData.youtubeUrls) ? [...viewResponseData.youtubeUrls] : []

  return {
    challengeRequest,
    solutionsTexts,
    docsIds,
    challengeDocsResponseList,
    selectedUsers,
    selectedValidatorUsers,
    hashtagTexts,
    youtubeLinks,
    selectedCommittee,
    ...departmentSelections
  }
}

/**
 * Creates a comprehensive mapping summary for debugging purposes
 */
export const createMappingSummary = (viewResponse: ChallengesResponse) => {
  return {
    // Basic Info
    id: viewResponse.id,
    status: viewResponse.status,
    localTitle: viewResponse.localTitle,
    titleAr: viewResponse.titleAr,
    titleEn: viewResponse.titleEn,

    // Department Info
    department: {
      id: viewResponse.departmentId,
      name: viewResponse.departmentName
    },
    subDepartment: {
      id: viewResponse.subDepartmentId,
      name: viewResponse.subDepartmentName
    },
    section: {
      id: viewResponse.sectionId,
      name: viewResponse.sectionName
    },

    // Users
    moderator: {
      grpNumber: viewResponse.moderator?.grpNumber,
      fullName: viewResponse.moderator?.fullName
    },
    assistantModerator: {
      grpNumber: viewResponse.assistantModerator?.grpNumber,
      fullName: viewResponse.assistantModerator?.fullName
    },
    invitedUsers: viewResponse.userInvitationCountedResponse?.invitedInternalUserResponse?.length || 0,
    validators: viewResponse.validatorList?.length || 0,

    // Committee (not available in API response yet)
    committee: {
      id: viewResponse.committeeId || null,
      name: viewResponse.committeeId ? `Committee ID: ${viewResponse.committeeId}` : 'No committee assigned'
    },

    // Settings
    settings: {
      topicClassification: viewResponse.topicClassificationResponse?.name,
      levelOfDecision: viewResponse.levelOfDecisionResponse?.name,
      importanceOfDecision: viewResponse.importanceOfDecisionResponse?.name,
      impact: viewResponse.impactResponse?.name,
      field: viewResponse.fieldResponse?.name
    },

    // Strategic
    strategic: {
      direction: viewResponse.strategicDirection?.name,
      goals: viewResponse.strategicGoals?.name,
      futureDirection: viewResponse.futureStrategicDirection?.name,
      futureGoals: viewResponse.futureStrategicGoals?.name
    },

    // Content
    content: {
      documents: viewResponse.challengeDocsResponseList?.length || 0,
      solutions: viewResponse.solutionsTexts?.length || 0,
      youtubeLinks: viewResponse.youtubeUrls?.length || 0,
      hashtags: viewResponse.hashtagTexts?.length || 0,
      reminders: viewResponse.challengeReminders?.length || 0
    },

    // Timings
    timings: {
      startTime: viewResponse.startTime,
      endTime: viewResponse.endTime,
      visibility: viewResponse.visibility,
      allowImmediateAccess: viewResponse.allowImmediateAccess
    }
  }
}
