<template>
  <ChallengeUserSelectionTab
    title-key="challenge.info.validators"
    empty-message-key="Messages.common.empty.validators"
    selected-label-key="common.selectedValidators"
    validation-field="selectedValidatorUsers"
    :users="validators"
    :selected-users="selectedValidatorUsers"
    :selected-items="selectedValidatorsItems"
    :selected-department="selectedDepartment"
    :selected-expert-level="selectedExpertLevel"
    :selected-expertise="selectedExpertise"
    :expertises="expertises"
    :departments="departments"
    :validation-errors="validationErrors"
    :loading="loading"
    :current-page="currentPage"
    :page-size="pageSize"
    :total-items="totalItems"
    @update:selected-users="$emit('update:selectedValidatorUsers', $event)"
    @update:selected-department="$emit('update:selectedDepartment', $event)"
    @update:selected-expert-level="$emit('update:selectedExpertLevel', $event)"
    @update:selected-expertise="$emit('update:selectedExpertise', $event)"
    @update:page="$emit('update:page', $event)"
    @update:page-size="$emit('update:pageSize', $event)"
    @search="$emit('fetch-validators', $event)"
  />
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import ChallengeUserSelectionTab from '@/components/challenge/ChallengeUserSelectionTab.vue'
import { DepartmentResponse } from '@/dto/department/DepartmentResponse.ts'
import { DropdownResponse } from '@/dto/lookup/general/DropdownResponse.ts'
import { UserMainResponse } from '@/dto/users/UserMainResponse.ts'

export default defineComponent({
  name: 'ValidatorsTab',
  components: {
    ChallengeUserSelectionTab
  },
  props: {
    selectedValidatorUsers: {
      type: Array as PropType<number[]>,
      required: true
    },
    validators: {
      type: Array as PropType<UserMainResponse[]>,
      required: true
    },
    selectedDepartment: {
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
    selectedValidatorsItems: {
      type: Array as PropType<UserMainResponse[]>,
      default: () => []
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
    'update:selectedValidatorUsers',
    'update:selectedDepartment',
    'update:selectedExpertLevel',
    'update:selectedExpertise',
    'update:page',
    'update:pageSize',
    'fetch-validators'
  ]
})
</script>
