<template>
  <ChallengeUserSelectionTab
    title-key="challenge.info.teamMember"
    empty-message-key="Messages.common.empty.teamMembers"
    selected-label-key="common.selectedUsers"
    validation-field="selectedUsers"
    :users="teamMembers"
    :selected-users="selectedUsers"
    :selected-items="selectedItems"
    :selected-department="filterDepartment"
    :selected-expert-level="selectedExpertLevel"
    :selected-expertise="selectedExpertise"
    :expertises="expertises"
    :departments="departments"
    :validation-errors="validationErrors"
    :loading="loading"
    :current-page="currentPage"
    :page-size="pageSize"
    :total-items="totalItems"
    @update:selected-users="$emit('update:selectedUsers', $event)"
    @update:selected-department="$emit('update:filterDepartment', $event)"
    @update:selected-expert-level="$emit('update:selectedExpertLevel', $event)"
    @update:selected-expertise="$emit('update:selectedExpertise', $event)"
    @update:page="$emit('update:page', $event)"
    @update:page-size="$emit('update:pageSize', $event)"
    @search="$emit('fetch-team-members', $event)"
  />
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import ChallengeUserSelectionTab from '@/components/challenge/ChallengeUserSelectionTab.vue'
import { DepartmentResponse } from '@/dto/department/DepartmentResponse.ts'
import { DropdownResponse } from '@/dto/lookup/general/DropdownResponse.ts'
import { UserMainResponse } from '@/dto/users/UserMainResponse.ts'

export default defineComponent({
  name: 'TeamMembersTab',
  components: {
    ChallengeUserSelectionTab
  },
  props: {
    selectedUsers: {
      type: Array as PropType<number[]>,
      required: true
    },
    teamMembers: {
      type: Array as PropType<UserMainResponse[]>,
      required: true
    },
    selectedItems: {
      type: Array as PropType<UserMainResponse[]>,
      default: () => []
    },
    filterDepartment: {
      type: Object as PropType<DepartmentResponse | null>,
      default: null
    },
    selectedExpertLevel: {
      type: Object as PropType<DropdownResponse | null>,
      default: null
    },
    selectedExpertise: {
      type: Object as PropType<DropdownResponse | null>,
      default: null
    },
    expertises: {
      type: Array as PropType<DropdownResponse[]>,
      required: true
    },
    departments: {
      type: Array as PropType<DepartmentResponse[]>,
      required: true
    },
    validationErrors: {
      type: Array as PropType<Array<{ field: string; message: string }>>,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    currentPage: {
      type: Number,
      default: 0
    },
    pageSize: {
      type: Number,
      default: 10
    },
    totalItems: {
      type: Number,
      default: 0
    }
  },
  emits: [
    'update:selectedUsers',
    'update:filterDepartment',
    'update:selectedExpertLevel',
    'update:selectedExpertise',
    'update:page',
    'update:pageSize',
    'fetch-team-members'
  ]
})
</script>
