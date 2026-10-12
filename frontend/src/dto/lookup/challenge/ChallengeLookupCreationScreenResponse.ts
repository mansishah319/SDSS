import { DropdownResponse } from '@/dto/lookup/general/DropdownResponse.ts'
import { DepartmentResponse } from '@/dto/department/DepartmentResponse.ts'
import { UserMainResponse } from '@/dto/users/UserMainResponse.ts'

export interface ChallengeLookupCreationScreenResponse {
  moderators: UserMainResponse[]
  validators: UserMainResponse[]
  teamMembers: UserMainResponse[]
  expertisesResponseList: DropdownResponse[]
  strategicGoalsResponseList: DropdownResponse[]
  strategicDirectionResponseList: DropdownResponse[]

  futureStrategicGoalsResponseList: DropdownResponse[]
  futureStrategicDirectionResponseList: DropdownResponse[]

  topicClassificationList: DropdownResponse[]
  levelOfDecisionList: DropdownResponse[]
  importanceOfDecisionList: DropdownResponse[]
  impactList: DropdownResponse[]
  fieldList: DropdownResponse[]

  departmentResponseList: DepartmentResponse[]
  sectorsList: DepartmentResponse[]
}
