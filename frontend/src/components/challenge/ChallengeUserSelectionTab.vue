<template>
  <v-card class="dp-card challenge-user-tab">
    <div class="challenge-user-tab__header">
      <div>
        <p class="challenge-user-tab__eyebrow">{{ $t('common.searchResults') }}</p>
        <h5 class="challenge-user-tab__title">{{ $t(titleKey) }}</h5>
      </div>
      <div class="challenge-user-tab__summary">
        <v-chip color="primary" variant="tonal" size="small" class="dp-chip">
          {{ users.length }} {{ $t('common.totalResults') }}
        </v-chip>
        <v-chip color="success" variant="tonal" size="small" class="dp-chip">
          {{ selectedUsers.length }} {{ $t(selectedLabelKey) }}
        </v-chip>
      </div>
    </div>

    <div class="challenge-user-tab__filters">
      <div class="challenge-user-tab__filters-header">
        <div class="challenge-user-tab__filters-copy">
          <span class="challenge-user-tab__filters-title">{{ $t('common.filters') }}</span>
          <span class="challenge-user-tab__filters-subtitle">{{ activeFiltersCount }} {{ $t('common.selected') }}</span>
        </div>
        <v-btn
          variant="text"
          color="secondary"
          prepend-icon="mdi-filter-off-outline"
          class="challenge-user-tab__reset-btn"
          :disabled="activeFiltersCount === 0"
          @click="resetFilters"
        >
          {{ $t('common.clearAll') }}
        </v-btn>
      </div>
      <div class="d-flex pa-0 ma-0">
        <v-col cols="12" md="4">
          <v-autocomplete
            :model-value="localDepartment"
            :items="departments"
            :label="$t('challenge.info.department')"
            return-object
            item-value="id"
            item-title="shortName"
            variant="outlined"
            density="comfortable"
            clearable
            hide-details
            bg-color="white"
            @update:model-value="onDepartmentChange"
          />
        </v-col>
        <v-col cols="12" md="4">
          <v-autocomplete
            :model-value="localExpertLevel"
            :items="expertLevels"
            :label="$t('user.add.expertLevel')"
            return-object
            item-value="id"
            item-title="name"
            variant="outlined"
            density="comfortable"
            clearable
            hide-details
            bg-color="white"
            @update:model-value="onExpertLevelChange"
          />
        </v-col>
        <v-col cols="12" md="4">
          <v-autocomplete
            :model-value="localExpertise"
            :items="expertises"
            :label="$t('user.add.expertisesTitle')"
            return-object
            item-value="id"
            item-title="name"
            variant="outlined"
            density="comfortable"
            clearable
            hide-details
            bg-color="white"
            @update:model-value="onExpertiseChange"
          />
        </v-col>
      </div>
    </div>

    <div class="challenge-user-tab__body">
      <div class="challenge-user-tab__toolbar">
        <div class="challenge-user-tab__toolbar-copy">
          <span class="challenge-user-tab__toolbar-title">{{ $t(titleKey) }}</span>
        </div>
        <v-btn variant="text" color="primary" prepend-icon="mdi-refresh" :loading="loading" @click="emitSearch()">
          {{ $t('common.refresh') }}
        </v-btn>
      </div>

      <div
        class="challenge-user-tab__table-shell"
        :class="{ 'challenge-user-tab__table-shell--error': hasSelectionError }"
        :data-field="validationField"
      >
        <div v-if="loading" class="challenge-user-tab__loading">
          <v-progress-circular indeterminate color="primary" size="28" />
        </div>

        <div v-else-if="users.length === 0" class="challenge-user-tab__empty">
          <EmptyDataTable :image-size="128" :message="$t(emptyMessageKey)" text-color="#666" />
        </div>

        <v-table v-else class="common-table-border">
          <thead class="common-sticky-header">
          <tr>
            <th class="small-width" scope="col">
              <input type="checkbox" :checked="isAllSelected" @change="toggleAllUsers" />
            </th>
            <th class="text-start" scope="col">{{ $t('common.variety.grpNumber') }}</th>
            <th class="text-start" scope="col">{{ $t('common.variety.name') }}</th>
            <th class="text-start" scope="col">{{ $t('common.variety.email') }}</th>
            <th class="text-start" scope="col">{{ $t('challenge.info.department') }}</th>
            <th class="text-start" scope="col">{{ $t('common.variety.expertLevel') }}</th>
            <th class="text-start" scope="col">{{ $t('common.variety.expertisesTitle') }}</th>
          </tr>
          </thead>
          <tbody class="common-scrollable-tbody">
          <tr v-for="user in users" :key="user.grpNumber">
            <td class="small-width">
              <input
                type="checkbox"
                :value="Number(user.grpNumber)"
                :checked="selectedUsers.includes(Number(user.grpNumber))"
                @change="toggleUser(Number(user.grpNumber))"
              />
            </td>
            <td>{{ user.grpNumber || '-' }}</td>
            <td>
              <div class="challenge-user-tab__identity">
                <span class="challenge-user-tab__name">{{ user.fullName || '-' }}</span>
                <span class="challenge-user-tab__meta">{{ user.title || user.gradeName || '-' }}</span>
              </div>
            </td>
            <td>{{ user.email || '-' }}</td>
            <td>{{ getDepartmentName(user) }}</td>
            <td>
              <v-chip size="small" variant="outlined" color="primary">
                {{ user.expertLevel || '-' }}
              </v-chip>
            </td>
            <td>{{ user.areaOfExpertise || '-' }}</td>
          </tr>
          </tbody>
        </v-table>

        <div v-if="users.length > 0" class="challenge-user-tab__footer">
          <div class="challenge-user-tab__footer-summary">
            {{ paginationSummary }}
          </div>
          <div class="challenge-user-tab__footer-actions">
            <v-select
              :model-value="pageSize"
              :items="pageSizeOptions"
              item-title="label"
              item-value="value"
              :label="$t('common.items_per_page')"
              variant="outlined"
              density="compact"
              hide-details
              class="challenge-user-tab__page-size"
              @update:model-value="onPageSizeChange"
            />
            <v-pagination
              v-if="pageSize !== -1 && pageCount > 1"
              :model-value="currentPage + 1"
              :length="pageCount"
              :total-visible="5"
              density="comfortable"
              @update:model-value="onPageChange"
            />
          </div>
        </div>
      </div>
    </div>

    <div v-if="selectedItems.length > 0" class="challenge-user-tab__selection">
      <h6 class="challenge-user-tab__selection-title">{{ $t(selectedLabelKey) }}</h6>
      <div class="challenge-user-tab__chips">
        <div v-for="user in selectedItems" :key="user.grpNumber" class="challenge-user-tab__chip">
          <span class="challenge-user-tab__chip-name">{{ user.fullName }}</span>
          <span class="challenge-user-tab__chip-code">{{ user.grpNumber }}</span>
        </div>
      </div>
    </div>
  </v-card>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import { DepartmentResponse } from '@/dto/department/DepartmentResponse.ts'
import { DropdownResponse } from '@/dto/lookup/general/DropdownResponse.ts'
import { UserMainResponse } from '@/dto/users/UserMainResponse.ts'

interface ExpertLevelOption {
  id: string
  name: string
}

interface SearchPayload {
  department: DepartmentResponse | null
  expertLevel: DropdownResponse | null
  expertise: DropdownResponse | null
  page?: number
  size?: number
  sortField?: string
  sortDirection?: 'asc' | 'desc'
}

export default defineComponent({
  name: 'ChallengeUserSelectionTab',
  components: {
    EmptyDataTable
  },
  props: {
    titleKey: {
      type: String,
      required: true
    },
    emptyMessageKey: {
      type: String,
      required: true
    },
    selectedLabelKey: {
      type: String,
      required: true
    },
    validationField: {
      type: String,
      required: true
    },
    users: {
      type: Array as PropType<UserMainResponse[]>,
      required: true
    },
    selectedUsers: {
      type: Array as PropType<number[]>,
      required: true
    },
    selectedItems: {
      type: Array as PropType<UserMainResponse[]>,
      default: () => []
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
    'update:selectedDepartment',
    'update:selectedExpertLevel',
    'update:selectedExpertise',
    'update:page',
    'update:pageSize',
    'search'
  ],
  setup(props, { emit }) {
    const { t } = useI18n()
    const pageSizeOptions = computed(() =>
      [10, 25, 50, 100, 200, -1].map((value) => ({
        label: value === -1 ? t('common.all') : String(value),
        value
      }))
    )

    const expertLevels = computed<ExpertLevelOption[]>(() => [
      { id: 'EXPERT', name: t('common.expertLevels.expert') },
      { id: 'INTERMEDIATE', name: t('common.expertLevels.intermediate') },
      { id: 'BASIC', name: t('common.expertLevels.basic') },
      { id: 'NONE', name: t('common.expertLevels.none') }
    ])

    const localDepartment = ref<DepartmentResponse | null>(props.selectedDepartment)
    const localExpertLevel = ref<DropdownResponse | null>(props.selectedExpertLevel)
    const localExpertise = ref<DropdownResponse | null>(props.selectedExpertise)
    const searchDebounce = ref<number | null>(null)

    const hasSelectionError = computed(() => {
      const parentError = props.validationErrors.find((error) => error.field === props.validationField)
      return Boolean(parentError) || props.selectedUsers.length === 0
    })

    const activeFiltersCount = computed(() => {
      return [localDepartment.value, localExpertLevel.value, localExpertise.value].filter(Boolean).length
    })

    const isAllSelected = computed(() => {
      if (props.users.length === 0) return false
      return props.users.every((user) => props.selectedUsers.includes(Number(user.grpNumber)))
    })

    const pageCount = computed(() => {
      if (props.pageSize === -1) {
        return 1
      }

      const count = Math.ceil(props.totalItems / props.pageSize)
      return count > 0 ? count : 1
    })

    const paginationSummary = computed(() => {
      if (props.totalItems === 0) {
        return t('common.noData')
      }

      const startItem = props.pageSize === -1 ? 1 : props.currentPage * props.pageSize + 1
      const endItem =
        props.pageSize === -1 ? props.totalItems : Math.min(startItem + props.users.length - 1, props.totalItems)
      return `${startItem}-${endItem} ${t('pagination.of')} ${props.totalItems}`
    })

    const emitSearch = () => {
      emit('search', {
        department: localDepartment.value,
        expertLevel: localExpertLevel.value,
        expertise: localExpertise.value,
        page: props.currentPage,
        size: props.pageSize,
        sortField: 'fullName',
        sortDirection: 'asc'
      } as SearchPayload)
    }

    const scheduleSearch = () => {
      if (searchDebounce.value) {
        clearTimeout(searchDebounce.value)
      }
      searchDebounce.value = setTimeout(() => {
        emitSearch()
      }, 250)
    }

    const onDepartmentChange = (value: DepartmentResponse | null) => {
      localDepartment.value = value
      emit('update:selectedDepartment', value)
      emit('update:page', 0)
      scheduleSearch()
    }

    const onExpertLevelChange = (value: DropdownResponse | null) => {
      localExpertLevel.value = value
      emit('update:selectedExpertLevel', value)
      emit('update:page', 0)
      scheduleSearch()
    }

    const onExpertiseChange = (value: DropdownResponse | null) => {
      localExpertise.value = value
      emit('update:selectedExpertise', value)
      emit('update:page', 0)
      scheduleSearch()
    }

    const resetFilters = () => {
      localDepartment.value = null
      localExpertLevel.value = null
      localExpertise.value = null
      emit('update:selectedDepartment', null)
      emit('update:selectedExpertLevel', null)
      emit('update:selectedExpertise', null)
      emit('update:page', 0)
      emit('search', {
        department: null,
        expertLevel: null,
        expertise: null,
        page: 0,
        size: props.pageSize,
        sortField: 'fullName',
        sortDirection: 'asc'
      } as SearchPayload)
    }

    const onPageChange = (page: number) => {
      const nextPage = Math.max(page - 1, 0)
      emit('update:page', nextPage)
      emit('search', {
        department: localDepartment.value,
        expertLevel: localExpertLevel.value,
        expertise: localExpertise.value,
        page: nextPage,
        size: props.pageSize,
        sortField: 'fullName',
        sortDirection: 'asc'
      } as SearchPayload)
    }

    const onPageSizeChange = (size: number) => {
      emit('update:pageSize', size)
      emit('update:page', 0)
      emit('search', {
        department: localDepartment.value,
        expertLevel: localExpertLevel.value,
        expertise: localExpertise.value,
        page: 0,
        size,
        sortField: 'fullName',
        sortDirection: 'asc'
      } as SearchPayload)
    }

    const toggleAllUsers = () => {
      const visibleUserIds = props.users.map((user) => Number(user.grpNumber))
      if (isAllSelected.value) {
        emit(
          'update:selectedUsers',
          props.selectedUsers.filter((userId) => !visibleUserIds.includes(userId))
        )
        return
      }

      emit('update:selectedUsers', [...new Set([...props.selectedUsers, ...visibleUserIds])])
    }

    const toggleUser = (grpNumber: number) => {
      const nextSelection = [...props.selectedUsers]
      const index = nextSelection.indexOf(grpNumber)
      if (index >= 0) {
        nextSelection.splice(index, 1)
      } else {
        nextSelection.push(grpNumber)
      }
      emit('update:selectedUsers', nextSelection)
    }

    const getDepartmentName = (user: UserMainResponse) => {
      return user.departmentName || user.shortDepartmentName || user.parentDepartmentName || '-'
    }

    watch(
      () => props.selectedDepartment,
      (value) => {
        localDepartment.value = value
      }
    )

    watch(
      () => props.selectedExpertLevel,
      (value) => {
        localExpertLevel.value = value
      }
    )

    watch(
      () => props.selectedExpertise,
      (value) => {
        localExpertise.value = value
      }
    )

    watch(
      () => [props.selectedDepartment, props.selectedExpertLevel, props.selectedExpertise],
      () => {
        scheduleSearch()
      },
      { immediate: true }
    )

    return {
      expertLevels,
      localDepartment,
      localExpertLevel,
      localExpertise,
      activeFiltersCount,
      hasSelectionError,
      isAllSelected,
      pageCount,
      pageSizeOptions,
      paginationSummary,
      emitSearch,
      onDepartmentChange,
      onExpertLevelChange,
      onExpertiseChange,
      onPageChange,
      onPageSizeChange,
      resetFilters,
      toggleAllUsers,
      toggleUser,
      getDepartmentName
    }
  }
})
</script>

<style scoped lang="scss">
.challenge-user-tab {
  background: #fff;
  border: 1px solid #dbe5f0;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.08);
  padding: 24px;
}

.challenge-user-tab__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.challenge-user-tab__eyebrow {
  color: #667085;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  margin-bottom: 6px;
  text-transform: uppercase;
}

.challenge-user-tab__title {
  color: #1e293b;
  font-size: 18px;
  font-weight: 700;
  margin: 0;
}

.challenge-user-tab__summary {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.challenge-user-tab :deep(.v-chip) {
  border-radius: 999px;
  font-weight: 500;
}

.challenge-user-tab__filters {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid #dde5ee;
  border-radius: 20px;
  margin-bottom: 20px;
  padding: 24px 20px;
}

.challenge-user-tab__filters-header {
  align-items: center;
  display: flex;
  gap: 16px;
  justify-content: space-between;
  margin-bottom: 12px;
}

.challenge-user-tab__filters-copy {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.challenge-user-tab__filters-title {
  color: #0f172a;
  font-size: 14px;
  font-weight: 700;
}

.challenge-user-tab__filters-subtitle {
  color: #64748b;
  font-size: 12px;
}

.challenge-user-tab__reset-btn {
  min-width: fit-content;
}

.challenge-user-tab__body {
  background: #fff;
  border: 1px solid #dde5ee;
  border-radius: 20px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8);
  overflow: hidden;
}

.challenge-user-tab__toolbar {
  align-items: center;
  background: linear-gradient(90deg, rgba(232, 240, 248, 0.95), rgba(255, 255, 255, 0.98));
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 18px 16px;
  border-bottom: 1px solid #e5edf5;
}

.challenge-user-tab__toolbar-title {
  color: #1e293b;
  display: block;
  font-size: 15px;
  font-weight: 700;
}

.challenge-user-tab__toolbar-subtitle {
  color: #63758a;
  font-size: 13px;
}

.challenge-user-tab__table-shell {
  min-height: 220px;
  position: relative;
}

.challenge-user-tab__footer {
  align-items: center;
  border-top: 1px solid #e6edf4;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: space-between;
  padding: 14px 18px;
}

.challenge-user-tab__footer-summary {
  color: #475569;
  font-size: 13px;
  font-weight: 600;
}

.challenge-user-tab__footer-actions {
  align-items: center;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.challenge-user-tab__page-size {
  width: 150px;
}

.challenge-user-tab__table-shell--error {
  box-shadow: inset 0 0 0 1px #ef4444;
}

.challenge-user-tab__loading,
.challenge-user-tab__empty {
  align-items: center;
  display: flex;
  justify-content: center;
  min-height: 220px;
  padding: 24px;
}

.challenge-user-tab__identity {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.challenge-user-tab__name {
  color: #1f2937;
  font-weight: 600;
}

.challenge-user-tab__meta {
  color: #718096;
  font-size: 12px;
}

.challenge-user-tab__selection {
  margin-top: 20px;
}

.challenge-user-tab__selection-title {
  color: #1e293b;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 12px;
}

.challenge-user-tab__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.challenge-user-tab__chip {
  align-items: center;
  background: #eefaf3;
  border: 1px solid #b7e4c7;
  border-radius: 999px;
  display: inline-flex;
  gap: 10px;
  padding: 8px 12px;
}

.challenge-user-tab__chip-name {
  color: #0f9f5b;
  font-weight: 600;
}

.challenge-user-tab__chip-code {
  background: #fff;
  border-radius: 999px;
  color: #5b6778;
  font-size: 12px;
  padding: 2px 8px;
}

.small-width {
  width: 50px;
}

.challenge-user-tab :deep(.v-field) {
  border-radius: 6px;
  box-shadow: none;
}

.challenge-user-tab :deep(.v-field__outline) {
  --v-field-border-opacity: 1;
  color: #c7d1dc;
}

.challenge-user-tab :deep(.v-btn) {
  letter-spacing: 0.08em;
  font-weight: 700;
}

.challenge-user-tab :deep(.v-btn__content) {
  align-items: center;
  color: #07a35d;
  display: inline-flex;
  gap: 8px;
}

.challenge-user-tab :deep(.common-table-border) {
  border: none !important;
}

.challenge-user-tab :deep(thead tr) {
  background: #edf2f7;
}

.challenge-user-tab :deep(thead th) {
  color: #f8fafc;
  background: #006c44;
  border-bottom: 1px solid #d6dee8;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.challenge-user-tab :deep(tbody tr) {
  background: #fff;
}

.challenge-user-tab :deep(tbody tr:hover) {
  background: #f9fbfd;
}

.challenge-user-tab :deep(tbody td) {
  border-bottom: 1px solid #e6edf4;
  color: #1f2937;
}

.challenge-user-tab :deep(input[type='checkbox']) {
  accent-color: #0aa35c;
}

@media (max-width: 960px) {
  .challenge-user-tab {
    padding: 16px;
  }

  .challenge-user-tab__header,
  .challenge-user-tab__toolbar,
  .challenge-user-tab__filters-header {
    flex-direction: column;
    align-items: stretch;
  }

  .challenge-user-tab__footer,
  .challenge-user-tab__footer-actions {
    align-items: stretch;
    flex-direction: column;
  }

  .challenge-user-tab__page-size {
    width: 100%;
  }
}
</style>
