import { createMultipart, get, httpGenericRequest, updateMultipart, uploadList } from '@/utils/GenericApiHelper'
import { Characters, URIS } from '@/constants/DpConstants.ts'
import { ChallengesCreationRequest } from '@/dto/challenge/ChallengesCreationRequest.ts'
import { ChallengesResponse } from '@/dto/challenge/ChallengesResponse.ts'
import { ChallengeLookupCreationScreenResponse } from '@/dto/lookup/challenge/ChallengeLookupCreationScreenResponse.ts'
import { UserDropDownListResponse } from '@/dto/lookup/user/UserDropDownListResponse.ts'
import { DocumentsCreationResponse } from '@/dto/documents/DocumentsCreationResponse.ts'
import { DepartmentResponse } from '@/dto/department/DepartmentResponse.ts'
import { PaginationResponse } from '@/dto/generic/PaginationResponse.ts'
import { UserSearchData } from '@/dto/users/UserSearchResponse.ts'
import type { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'

export class ChallengeFormService {
  // Challenge CRUD operations
  static async createChallenge(
    request: ChallengesCreationRequest,
    files: File[] = []
  ): Promise<SuccessResponse<ChallengesResponse> | null> {
    return await createMultipart<SuccessResponse<ChallengesResponse>>(URIS.CHALLENGES.BASE, 'request', request, files)
  }

  static async updateChallenge(
    challengeId: string,
    request: ChallengesCreationRequest,
    files: File[] = []
  ): Promise<SuccessResponse<ChallengesResponse> | null> {
    return await updateMultipart<SuccessResponse<ChallengesResponse>>(
      URIS.CHALLENGES.BASE + '?challengeId=' + challengeId,
      'request',
      request,
      files
    )
  }

  static async getChallenge(challengeId: string): Promise<SuccessResponse<ChallengesResponse> | null> {
    return await get<SuccessResponse<ChallengesResponse>>(URIS.CHALLENGES.BASE + Characters.SLASH + challengeId)
  }

  // Lookup data
  static async getChallengeLookupData(): Promise<ChallengeLookupCreationScreenResponse | null> {
    const response = await get<SuccessResponse<ChallengeLookupCreationScreenResponse>>(
      URIS.CHALLENGES.BASE + URIS.LOOKUP.CHALLENGES.CREATION
    )
    return response?.data || null
  }

  // User search
  static async searchUsers(params: {
    expertLevel?: string
    expertisesName?: string
    department?: string
    page?: number
    size?: number
  }): Promise<UserDropDownListResponse[]> {
    const response = await httpGenericRequest<PaginationResponse<UserSearchData>>(
      URIS.METHODS.GET,
      URIS.USER.USER_SEARCH_URI,
      null,
      params
    )
    return response?.data || []
  }

  // Department operations
  static async getAllDepartments(): Promise<DepartmentResponse[]> {
    const response = await get<SuccessResponse<DepartmentResponse[]>>(URIS.CORE.BASE + URIS.CORE.DEPARTMENT_ALL)
    return response?.data || []
  }

  static async getSubDepartmentsByDepartmentId(departmentId: number): Promise<DepartmentResponse[]> {
    const response = await get<SuccessResponse<DepartmentResponse[]>>(
      URIS.CORE.BASE + URIS.CORE.DEPARTMENT_SUB_DEPARTMENT + '?departmentId=' + departmentId
    )
    return response?.data || []
  }

  static async getSectionsBySubDepartmentId(subDepartmentId: number): Promise<DepartmentResponse[]> {
    const response = await get<SuccessResponse<DepartmentResponse[]>>(
      URIS.CORE.BASE + URIS.CORE.DEPARTMENT_SECTIONS + '?subDepartmentId=' + subDepartmentId
    )
    return response?.data || []
  }

  // Document operations
  static async uploadDocuments(files: []): Promise<DocumentsCreationResponse[]> {
    const response = await uploadList<SuccessResponse<any>>(
      files,
      URIS.DOCUMENTS.BASE + URIS.DOCUMENTS.UPLOAD_LIST_WITHOUT_CHALLENGE_ID
    )
    return response?.data?.data ?? []
  }

  // Utility methods
  static prepareChallengeRequest(
    challengeRequest: ChallengesCreationRequest,
    selectedUsers: number[],
    selectedValidatorUsers: number[],
    docsIds: number[],
    selectedDepartment?: DepartmentResponse,
    selectedSubDepartment?: DepartmentResponse,
    selectedSection?: DepartmentResponse
  ): ChallengesCreationRequest {
    const request = { ...challengeRequest }

    // Set user selections
    request.invitedUsersIds = selectedUsers
    request.validatorsIds = selectedValidatorUsers
    request.documentsIds = docsIds

    // Format dates
    request.startTime = this.formatReminder(request.startTime)
    request.endTime = this.formatReminder(request.endTime)

    // Set department information
    if (selectedDepartment) {
      request.departmentId = String(selectedDepartment.id)
      request.departmentName = String(selectedDepartment.shortName)
    }

    if (selectedSubDepartment) {
      request.subDepartmentId = String(selectedSubDepartment.id)
      request.subDepartmentName = String(selectedSubDepartment.shortName)
    }

    if (selectedSection) {
      request.sectionId = String(selectedSection.id)
      request.sectionName = String(selectedSection.shortName)
    }

    // Extract IDs from objects
    request.strategicGoalsId = this.extractIdIfObject(request.strategicGoalsId)
    request.strategicDirectionId = this.extractIdIfObject(request.strategicDirectionId)
    request.futureStrategicDirectionId = this.extractIdIfObject(request.futureStrategicDirectionId)
    request.field = this.extractIdIfObject(request.field)

    return request
  }

  static formatReminder(dateTime: string): string {
    if (!dateTime) return ''
    try {
      const date = new Date(dateTime)
      return date.toISOString().slice(0, 16) // Format as "YYYY-MM-DDTHH:mm"
    } catch {
      return dateTime
    }
  }

  static extractIdIfObject(obj: any): any {
    if (typeof obj === 'object' && obj !== null && Object.hasOwn(obj, 'id')) {
      return obj.id
    }
    return obj
  }

  static validateChallengeRequest(challengeRequest: ChallengesCreationRequest): string[] {
    const errors: string[] = []

    if (
      !challengeRequest.startTime ||
      !challengeRequest.endTime ||
      !challengeRequest.titleAr ||
      !challengeRequest.titleEn ||
      !challengeRequest.descriptionAr ||
      !challengeRequest.descriptionEn ||
      !challengeRequest.moderatorId ||
      !challengeRequest.visibility ||
      challengeRequest.allowImmediateAccess === null ||
      challengeRequest.allowImmediateAccess === undefined
    ) {
      errors.push('error.mandatoryFields')
    }

    return errors
  }
}
