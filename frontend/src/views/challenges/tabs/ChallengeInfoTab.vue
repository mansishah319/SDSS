<template>
  <v-fade-transition>
    <v-card
      :class="[
        'dp-card',
        'common-challenge-card-container',
        'challenge-info-card',
        'elevation-3',
        { 'tab-content-error': hasCurrentTabError },
      ]"
    >
      <h4 class="tab-title mb-4 sticky-section-header">{{ $t('challenge.view') }}</h4>

      <!-- Basic Information -->
      <h5 class="section-header mb-3">{{ $t('challenge.info.basicInfo') }}</h5>
      <v-row class="challenge-info-form-row" no-gutters>
        <v-col cols="12" md="6" sm="5">
          <v-text-field
            :model-value="challengeRequest.titleEn"
            dir="ltr"
            :clearable="true"
            density="compact"
            variant="outlined"
            :rules="[rules.required]"
            required
            data-field="titleEn"
            :class="[
              'challenge-direction-ltr',
              { 'shake-error': rules.required && !challengeRequest.titleEn, 'field-error': hasTitleEnError },
            ]"
            @update:model-value="updateField('titleEn', $event)"
          >
            <template #label>
              <CustomFormLabel
                :label-value="$t('challenge.info.titleEn')"
                :mandatory="true"
                tooltip="$t('challenge.info.requiredTooltip')"
              />
            </template>
          </v-text-field>
          <div v-if="titleEnError" class="error-message mt-1">
            <v-icon size="small" color="error" class="me-1">mdi-alert-circle</v-icon>
            <span class="error-text">{{ titleEnError }}</span>
          </div>
        </v-col>
        <v-col cols="12" md="5" sm="5">
          <v-text-field
            :model-value="challengeRequest.titleAr"
            dir="rtl"
            :rules="[rules.required]"
            :clearable="true"
            density="compact"
            variant="outlined"
            required
            data-field="titleAr"
            :class="[
              'challenge-direction-rtl',
              { 'shake-error': rules.required && !challengeRequest.titleAr, 'field-error': hasTitleArError },
            ]"
            @update:model-value="updateField('titleAr', $event)"
          >
            <template #label>
              <CustomFormLabel
                :label-value="$t('challenge.info.titleAr')"
                :mandatory="true"
                tooltip="$t('challenge.info.requiredTooltip')"
              />
            </template>
          </v-text-field>
          <div v-if="titleArError" class="error-message mt-1">
            <v-icon size="small" color="error" class="me-1">mdi-alert-circle</v-icon>
            <span class="error-text">{{ titleArError }}</span>
          </div>
        </v-col>
      </v-row>

      <v-divider class="my-4" />

      <!-- Descriptions -->
      <h5 class="section-header mb-3">{{ $t('challenge.info.descriptions') }}</h5>
      <v-row class="challenge-info-form-row" no-gutters>
        <v-col cols="12" md="5" sm="3">
          <v-textarea
            :model-value="challengeRequest.descriptionEn"
            dir="ltr"
            :clearable="true"
            :rules="[rules.required]"
            density="compact"
            variant="outlined"
            required
            rows="4"
            data-field="descriptionEn"
            :class="[
              'challenge-direction-ltr',
              {
                'shake-error': rules.required && !challengeRequest.descriptionEn,
                'field-error': hasDescriptionEnError,
              },
            ]"
            @update:model-value="updateField('descriptionEn', $event)"
          >
            <template #label>
              <CustomFormLabel
                :label-value="$t('challenge.info.descriptionEn')"
                :mandatory="true"
                tooltip="$t('challenge.info.requiredTooltip')"
              />
            </template>
          </v-textarea>
          <div v-if="descriptionEnError" class="error-message mt-1">
            <v-icon size="small" color="error" class="me-1">mdi-alert-circle</v-icon>
            <span class="error-text">{{ descriptionEnError }}</span>
          </div>
        </v-col>
        <v-col cols="12" md="6">
          <v-textarea
            :model-value="challengeRequest.descriptionAr"
            dir="rtl"
            :clearable="true"
            :rules="[rules.required]"
            density="compact"
            variant="outlined"
            required
            rows="4"
            data-field="descriptionAr"
            :class="[
              'challenge-direction-rtl',
              {
                'shake-error': rules.required && !challengeRequest.descriptionAr,
                'field-error': hasDescriptionArError,
              },
            ]"
            @update:model-value="updateField('descriptionAr', $event)"
          >
            <template #label>
              <CustomFormLabel
                :label-value="$t('challenge.info.descriptionAr')"
                :mandatory="true"
                tooltip="$t('challenge.info.requiredTooltip')"
              />
            </template>
          </v-textarea>
          <div v-if="descriptionArError" class="error-message mt-1">
            <v-icon size="small" color="error" class="me-1">mdi-alert-circle</v-icon>
            <span class="error-text">{{ descriptionArError }}</span>
          </div>
        </v-col>
      </v-row>

      <v-divider class="my-4" />

      <!-- Department Information -->
      <h5 class="section-header mb-3">{{ $t('challenge.info.departmentInfo') }}</h5>
      <v-row class="challenge-info-form-row" no-gutters>
        <v-col cols="12" md="4" sm="3">
          <v-autocomplete
            :items="departmentDtoResponseList"
            :model-value="selectedDepartment"
            return-object
            item-value="id"
            item-title="shortName"
            variant="outlined"
            density="compact"
            data-field="selectedDepartment"
            :rules="[rules.required]"
            required
            :class="{
              'shake-error': rules.required && !selectedDepartment,
              'field-error': hasDepartmentError,
            }"
            @update:model-value="
              (dept) => {
                $emit('update:selectedDepartment', dept)
                $emit('update:selectedSubDepartment', null)
                $emit('update:selectedSection', null)
                dept && $emit('retrieveAllSubDepartmentsByDepartmentId', dept.id)
              }
            "
          >
            <template #label>
              <CustomFormLabel
                :label-value="$t('challenge.viewInfo.department')"
                :mandatory="true"
                tooltip="$t('challenge.info.requiredTooltip')"
              />
            </template>
          </v-autocomplete>
          <div v-if="departmentError" class="error-message mt-1">
            <v-icon size="small" color="error" class="me-1">mdi-alert-circle</v-icon>
            <span class="error-text">{{ departmentError }}</span>
          </div>
        </v-col>
        <v-col cols="12" md="4" sm="3">
          <v-autocomplete
            :items="subDepartmentDtoResponseList"
            :model-value="selectedSubDepartment"
            return-object
            item-value="id"
            item-title="shortName"
            variant="outlined"
            density="compact"
            data-field="selectedSubDepartment"
            :disabled="!selectedDepartment"
            :clearable="true"
            @update:model-value="
              (subDept) => {
                $emit('update:selectedSubDepartment', subDept)
                $emit('update:selectedSection', null)
                subDept && $emit('findAllSectionBySubDepartmentId', subDept.id)
              }
            "
          >
            <template #label>
              <CustomFormLabel :label-value="$t('challenge.viewInfo.subDepartment')" :mandatory="false" />
            </template>
          </v-autocomplete>
        </v-col>
        <v-col cols="12" md="3" sm="3">
          <v-autocomplete
            :items="sectionsDtoResponseList"
            :model-value="selectedSection"
            return-object
            item-value="id"
            item-title="shortName"
            variant="outlined"
            density="compact"
            data-field="selectedSection"
            :disabled="!selectedSubDepartment"
            :clearable="true"
            @update:model-value="$emit('update:selectedSection', $event)"
          >
            <template #label>
              <CustomFormLabel :label-value="$t('challenge.viewInfo.section')" :mandatory="false" />
            </template>
          </v-autocomplete>
        </v-col>
        <v-col cols="12" md="11" sm="6">
          <v-autocomplete
            :items="committeeListDropListResponse"
            :model-value="selectedCommitteeObject"
            return-object
            item-value="teamID"
            item-title="teamTitle"
            variant="outlined"
            density="compact"
            :menu-props="{
              maxHeight: 300,
              contentClass: 'committee-dropdown-menu',
            }"
            @update:model-value="handleCommitteeUpdate"
          >
            <template #label>
              <CustomFormLabel :label-value="$t('challenge.info.committee')" :mandatory="false" />
            </template>
            <template #item="{ props, item }">
              <v-list-item v-bind="props" :title="item.raw.teamTitle" class="committee-dropdown-item">
                <template #title>
                  <div class="committee-item-title">
                    {{ item.raw.teamTitle }}
                  </div>
                </template>
              </v-list-item>
            </template>
          </v-autocomplete>
        </v-col>
      </v-row>

      <v-divider class="my-4" />

      <!-- Visibility and Dates -->
      <h5 class="section-header mb-3">{{ $t('challenge.info.visibilityAndDates') }}</h5>
      <v-row class="challenge-info-form-row" no-gutters>
        <v-col cols="12" md="2" sm="3">
          <v-autocomplete
            :model-value="challengeRequest.visibility"
            :clearable="true"
            :items="[
              { id: 'PUBLIC', name: $t('common.visibility.public') },
              { id: 'PRIVATE', name: $t('common.visibility.private') },
            ]"
            variant="outlined"
            item-value="id"
            item-title="name"
            density="compact"
            :rules="[rules.required]"
            required
            data-field="visibility"
            :class="{
              'shake-error': rules.required && !challengeRequest.visibility,
              'field-error': hasVisibilityError,
            }"
            @update:model-value="updateField('visibility', $event)"
          >
            <template #label>
              <CustomFormLabel
                :label-value="$t('challenge.info.visibility')"
                :mandatory="true"
                tooltip="$t('challenge.info.requiredTooltip')"
              />
            </template>
          </v-autocomplete>
          <div v-if="visibilityError" class="error-message mt-1">
            <v-icon size="small" color="error" class="me-1">mdi-alert-circle</v-icon>
            <span class="error-text">{{ visibilityError }}</span>
          </div>
        </v-col>
        <v-col cols="12" md="4" sm="3">
          <DPDateTimePicker
            :model-value="challengeRequest.startTime"
            :rules="[rules.required]"
            :label="translate('challenge.dashboard.startTime')"
            required
            :has-error="hasStartTimeError"
            :locale="locale"
            data-field="startTime"
            @update:model-value="updateField('startTime', $event)"
          >
            <template #label>
              <CustomFormLabel
                :label-value="$t('challenge.info.startTime')"
                :mandatory="true"
                tooltip="$t('challenge.info.requiredTooltip')"
              />
            </template>
          </DPDateTimePicker>
          <div v-if="startTimeError" class="error-message mt-1">
            <v-icon size="small" color="error" class="me-1">mdi-alert-circle</v-icon>
            <span class="error-text">{{ startTimeError }}</span>
          </div>
        </v-col>
        <v-col cols="12" md="4" sm="3">
          <DPDateTimePicker
            :model-value="challengeRequest.endTime"
            :rules="[rules.required]"
            :label="translate('challenge.dashboard.endTime')"
            required
            :has-error="hasEndTimeError"
            :locale="locale"
            data-field="endTime"
            @update:model-value="updateField('endTime', $event)"
          >
            <template #label>
              <CustomFormLabel
                :label-value="$t('challenge.info.endTime')"
                :mandatory="true"
                tooltip="$t('challenge.info.requiredTooltip')"
              />
            </template>
          </DPDateTimePicker>
          <div v-if="endTimeError" class="error-message mt-1">
            <v-icon size="small" color="error" class="me-1">mdi-alert-circle</v-icon>
            <span class="error-text">{{ endTimeError }}</span>
          </div>
        </v-col>
      </v-row>
      <v-divider class="my-4" />

      <!-- Strategic Information -->
      <h5 class="section-header mb-3">{{ $t('challenge.info.strategicInfo') }}</h5>
      <v-row class="challenge-info-form-row" no-gutters>
        <v-col cols="12" md="5" sm="4">
          <v-autocomplete
            :model-value="challengeRequest.strategicDirectionId"
            :items="strategicDirectionResponseListLookupResponse"
            item-value="id"
            item-title="name"
            variant="outlined"
            density="compact"
            @update:model-value="
              (directionId) => {
                updateField('strategicDirectionId', directionId)
                directionId && $emit('onStrategicDirectionChange', directionId)
              }
            "
          >
            <template #label>
              <CustomFormLabel :label-value="$t('challenge.info.strategicDirection')" :mandatory="false" />
            </template>
          </v-autocomplete>
        </v-col>

        <v-col cols="12" md="5" sm="4">
          <v-autocomplete
            :model-value="challengeRequest.strategicGoalsId"
            :items="strategicGoalsResponseListLookupResponse"
            item-value="id"
            item-title="name"
            variant="outlined"
            density="compact"
            @update:model-value="updateField('strategicGoalsId', $event)"
          >
            <template #label>
              <CustomFormLabel :label-value="$t('adminDashboard.strategicGoals')" :mandatory="false" />
            </template>
          </v-autocomplete>
        </v-col>

        <v-col cols="12" md="5" sm="4">
          <v-autocomplete
            :model-value="challengeRequest.futureStrategicDirectionId"
            :items="futureStrategicDirectionResponseListLookupResponse"
            item-value="id"
            item-title="name"
            variant="outlined"
            density="compact"
            @update:model-value="updateField('futureStrategicDirectionId', $event)"
          >
            <template #label>
              <CustomFormLabel :label-value="$t('challenge.info.futureStrategicDirection')" :mandatory="false" />
            </template>
          </v-autocomplete>
        </v-col>

        <v-col cols="12" md="5" sm="4">
          <v-autocomplete
            :model-value="challengeRequest.futureStrategicGoalsId"
            :items="futureStrategicGoalsResponseListLookupResponse"
            item-value="id"
            item-title="name"
            variant="outlined"
            density="compact"
            @update:model-value="updateField('futureStrategicGoalsId', $event)"
          >
            <template #label>
              <CustomFormLabel :label-value="$t('challenge.info.futureStrategicGoals')" :mandatory="false" />
            </template>
          </v-autocomplete>
        </v-col>
      </v-row>
      <v-divider class="my-4" />

      <!-- Moderators -->
      <h5 class="section-header mb-3">{{ $t('challenge.info.moderatorsAndCommittee') }}</h5>
      <v-row class="challenge-info-form-row" no-gutters>
        <v-col cols="12" md="3" sm="3">
          <v-autocomplete
            :model-value="challengeRequest.moderatorId"
            :items="moderatorsLookupResponse"
            item-value="grpNumber"
            item-title="fullName"
            variant="outlined"
            density="compact"
            no-filter
            :loading="isModeratorsLoading"
            :search="moderatorsSearchTerm"
            :menu-props="{ maxHeight: 320, contentClass: 'moderator-dropdown-menu' }"
            :rules="[rules.required]"
            required
            data-field="moderatorId"
            :class="{
              'shake-error': rules.required && !challengeRequest.moderatorId,
              'field-error': hasModeratorError,
            }"
            @update:model-value="updateField('moderatorId', $event)"
            @update:search="onModeratorsSearch"
            @update:menu="onModeratorsDropdownOpen"
            @scroll.passive="onModeratorsScroll"
          >
            <template #label>
              <CustomFormLabel
                :label-value="$t('challenge.info.moderator')"
                :mandatory="true"
                tooltip="$t('challenge.info.requiredTooltip')"
              />
            </template>
            <template #item="{ props: itemProps, item }">
              <v-list-item v-bind="itemProps" class="moderator-dropdown-item">
                <template #title>
                  <div class="moderator-item-title">{{ item.raw.fullName }}</div>
                </template>
                <template #subtitle>
                  <div class="moderator-item-subtitle">
                    {{ item.raw.grpNumber }}<span v-if="item.raw.departmentName"> | {{ item.raw.departmentName }}</span>
                  </div>
                </template>
              </v-list-item>
            </template>
            <template #append-item>
              <div v-if="isModeratorsLoading" class="moderator-dropdown-loader">
                <v-progress-circular indeterminate size="20" width="2" color="primary" />
              </div>
              <div
                v-else-if="hasMoreModerators"
                v-intersect="onModeratorsIntersect"
                class="moderator-dropdown-loader"
              />
            </template>
          </v-autocomplete>
          <div v-if="moderatorError" class="error-message mt-1">
            <v-icon size="small" color="error" class="me-1">mdi-alert-circle</v-icon>
            <span class="error-text">{{ moderatorError }}</span>
          </div>
        </v-col>
        <v-col cols="12" md="3" sm="3">
          <v-autocomplete
            :model-value="challengeRequest.assistantModeratorGrp"
            :items="moderatorsLookupResponse"
            item-value="grpNumber"
            item-title="fullName"
            variant="outlined"
            density="compact"
            no-filter
            :loading="isModeratorsLoading"
            :search="moderatorsSearchTerm"
            :menu-props="{ maxHeight: 320, contentClass: 'moderator-dropdown-menu' }"
            @update:model-value="updateField('assistantModeratorGrp', $event)"
            @update:search="onModeratorsSearch"
            @update:menu="onModeratorsDropdownOpen"
            @scroll.passive="onModeratorsScroll"
          >
            <template #label>
              <CustomFormLabel :label-value="$t('challenge.info.assistantModerator')" :mandatory="false" />
            </template>
            <template #item="{ props: itemProps, item }">
              <v-list-item v-bind="itemProps" class="moderator-dropdown-item">
                <template #title>
                  <div class="moderator-item-title">{{ item.raw.fullName }}</div>
                </template>
                <template #subtitle>
                  <div class="moderator-item-subtitle">
                    {{ item.raw.grpNumber }}<span v-if="item.raw.departmentName"> | {{ item.raw.departmentName }}</span>
                  </div>
                </template>
              </v-list-item>
            </template>
            <template #append-item>
              <div v-if="isModeratorsLoading" class="moderator-dropdown-loader">
                <v-progress-circular indeterminate size="20" width="2" color="primary" />
              </div>
              <div
                v-else-if="hasMoreModerators"
                v-intersect="onModeratorsIntersect"
                class="moderator-dropdown-loader"
              />
            </template>
          </v-autocomplete>
        </v-col>
      </v-row>
      <v-divider class="my-4" />

      <!-- Access Control -->
      <h5 class="section-header mb-3">{{ $t('challenge.info.accessControl') }}</h5>
      <v-row class="challenge-info-form-row" no-gutters>
        <v-col cols="12" md="5" sm="3">
          <v-radio-group
            :model-value="challengeRequest.allowImmediateAccess"
            :inline="true"
            data-field="allowImmediateAccess"
            :class="{
              'shake-error': rules.required && challengeRequest.allowImmediateAccess === null,
              'field-error': hasAllowImmediateAccessError,
            }"
            class="challenge-radio-group"
            @update:model-value="updateField('allowImmediateAccess', $event)"
          >
            <template #label>
              <CustomFormLabel
                :label-value="$t('challenge.info.allowImmediateAccess')"
                :mandatory="true"
                tooltip="$t('challenge.info.requiredTooltip')"
              />
            </template>
            <v-col cols="auto">
              <v-radio :label="$t('common.yes')" :value="true" />
            </v-col>
            <v-col cols="auto">
              <v-radio :label="$t('common.no')" :value="false" />
            </v-col>
          </v-radio-group>
          <div v-if="allowImmediateAccessError" class="error-message mt-1">
            <v-icon size="small" color="error" class="me-1">mdi-alert-circle</v-icon>
            <span class="error-text">{{ allowImmediateAccessError }}</span>
          </div>
        </v-col>
      </v-row>
      <v-divider class="my-4" />
    </v-card>
  </v-fade-transition>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref, watch } from 'vue'
import { CommitteeResponse } from '@/dto/committee/CommitteeResponse'
import { ChallengesCreationRequest } from '@/dto/challenge/ChallengesCreationRequest'
import CustomFormLabel from '@/components/custom/CustomFormLabel.vue'
import DPDateTimePicker from '@/components/date/DPDateTimePicker.vue'
import { useI18n } from 'vue-i18n'
import moment from 'moment'
import { translate } from '@/utils/TranslatorHelper'
import { UserMainResponse } from '@/dto/users/UserMainResponse'

export default defineComponent({
  name: 'ChallengeInfoTab',
  components: {
    CustomFormLabel,
    DPDateTimePicker
  },
  props: {
    challengeRequest: { type: Object as PropType<ChallengesCreationRequest>, required: true },
    selectedDepartment: { type: Object, default: null },
    selectedSubDepartment: { type: Object, default: null },
    selectedSection: { type: Object, default: null },
    departmentDtoResponseList: { type: Array, required: true },
    subDepartmentDtoResponseList: { type: Array, required: true },
    sectionsDtoResponseList: { type: Array, required: true },
    moderatorsLookupResponse: { type: Array as PropType<UserMainResponse[]>, required: true },
    isModeratorsLoading: { type: Boolean, default: false },
    moderatorsSearchTerm: { type: String, default: '' },
    hasMoreModerators: { type: Boolean, default: false },
    committeeListDropListResponse: { type: Array as PropType<CommitteeResponse[]>, required: true },
    strategicDirectionResponseListLookupResponse: { type: Array, required: true },
    strategicGoalsResponseListLookupResponse: { type: Array, required: true },
    futureStrategicDirectionResponseListLookupResponse: { type: Array, required: true },
    futureStrategicGoalsResponseListLookupResponse: { type: Array, required: true },
    rules: { type: Object, required: true },
    selectedCommittee: { type: [Object, Number], default: null },
    validationErrors: {
      type: Array as PropType<Array<{ field: string; message: string }>>,
      default: () => []
    }
  },
  emits: [
    'update:challengeRequest',
    'update:selectedDepartment',
    'update:selectedSubDepartment',
    'update:selectedSection',
    'update:selectedCommittee',
    'update:moderatorsSearchTerm',
    'retrieveAllSubDepartmentsByDepartmentId',
    'findAllSectionBySubDepartmentId',
    'onStrategicDirectionChange',
    'fetch-moderators'
  ],
  setup(props, { emit }) {
    const { locale, t } = useI18n()
    const localModeratorsSearch = ref(props.moderatorsSearchTerm)
    const moderatorsSearchTimeout = ref<number | null>(null)

    // Handle committee selection - convert number to object if needed
    const selectedCommitteeObject = computed(() => {
      if (!props.selectedCommittee) return null

      // If it's already an object, return it
      if (typeof props.selectedCommittee === 'object') {
        return props.selectedCommittee
      }

      // If it's a number, find the corresponding committee object
      return (
        props.committeeListDropListResponse.find((committee) => committee.teamID === props.selectedCommittee) || null
      )
    })

    const updateField = (field: keyof ChallengesCreationRequest, value: any) => {
      emit('update:challengeRequest', { ...props.challengeRequest, [field]: value })
    }

    const validateStartTime = () => {
      const startTime = props.challengeRequest.startTime
      if (!startTime) return null

      const startMoment = moment(startTime, 'YYYY-MM-DD HH:mm:ss')
      const currentMoment = moment()

      if (startMoment.isSameOrBefore(currentMoment)) {
        return t('challenge.error.startTimeMustBeAfterCurrent')
      }
      return null
    }

    const validateEndTime = () => {
      const endTime = props.challengeRequest.endTime
      if (!endTime) return null

      const endMoment = moment(endTime, 'YYYY-MM-DD HH:mm:ss')
      const currentMoment = moment()

      if (endMoment.isSameOrBefore(currentMoment)) {
        return t('challenge.error.endTimeMustBeAfterCurrent')
      }

      const startTime = props.challengeRequest.startTime
      if (startTime) {
        const startMoment = moment(startTime, 'YYYY-MM-DD HH:mm:ss')
        if (endMoment.isSameOrBefore(startMoment)) {
          return t('challenge.error.endTimeMustBeAfterStartTime')
        }
      }
      return null
    }

    const startTimeError = computed(() => {
      // Check for parent validation errors first
      const parentError = props.validationErrors.find((error) => error.field === 'startTime')
      if (parentError) return parentError.message

      // Then check local validation
      return validateStartTime()
    })

    const endTimeError = computed(() => {
      // Check for parent validation errors first
      const parentError = props.validationErrors.find((error) => error.field === 'endTime')
      if (parentError) return parentError.message

      // Then check local validation
      return validateEndTime()
    })

    const hasStartTimeError = computed(() => {
      return startTimeError.value !== null || (props.rules.required && !props.challengeRequest.startTime)
    })

    const hasEndTimeError = computed(() => {
      return endTimeError.value !== null || (props.rules.required && !props.challengeRequest.endTime)
    })

    const hasTitleEnError = computed(() => {
      return titleEnError.value !== null || (props.rules.required && !props.challengeRequest.titleEn?.trim())
    })

    const hasTitleArError = computed(() => {
      return titleArError.value !== null || (props.rules.required && !props.challengeRequest.titleAr?.trim())
    })

    const titleEnError = computed(() => {
      // Check for parent validation errors first
      const parentError = props.validationErrors.find((error) => error.field === 'titleEn')
      if (parentError) return parentError.message

      // Then check local validation
      return null
    })

    const titleArError = computed(() => {
      // Check for parent validation errors first
      const parentError = props.validationErrors.find((error) => error.field === 'titleAr')
      if (parentError) return parentError.message

      // Then check local validation
      return null
    })

    const hasVisibilityError = computed(() => {
      return visibilityError.value !== null || (props.rules.required && !props.challengeRequest.visibility)
    })

    const visibilityError = computed(() => {
      // Check for parent validation errors first
      const parentError = props.validationErrors.find((error) => error.field === 'visibility')
      if (parentError) return parentError.message

      // Then check local validation
      return null
    })

    const hasDescriptionEnError = computed(() => {
      return (
        descriptionEnError.value !== null || (props.rules.required && !props.challengeRequest.descriptionEn?.trim())
      )
    })

    const descriptionEnError = computed(() => {
      // Check for parent validation errors first
      const parentError = props.validationErrors.find((error) => error.field === 'descriptionEn')
      if (parentError) return parentError.message

      // Then check local validation
      return null
    })

    const hasDescriptionArError = computed(() => {
      return (
        descriptionArError.value !== null || (props.rules.required && !props.challengeRequest.descriptionAr?.trim())
      )
    })

    const descriptionArError = computed(() => {
      // Check for parent validation errors first
      const parentError = props.validationErrors.find((error) => error.field === 'descriptionAr')
      if (parentError) return parentError.message

      // Then check local validation
      return null
    })

    const hasModeratorError = computed(() => {
      return moderatorError.value !== null || (props.rules.required && !props.challengeRequest.moderatorId)
    })

    const moderatorError = computed(() => {
      // Check for parent validation errors first
      const parentError = props.validationErrors.find((error) => error.field === 'moderatorId')
      if (parentError) return parentError.message

      // Then check local validation
      return null
    })

    const hasAllowImmediateAccessError = computed(() => {
      return (
        allowImmediateAccessError.value !== null ||
        (props.rules.required && props.challengeRequest.allowImmediateAccess === null)
      )
    })

    const allowImmediateAccessError = computed(() => {
      // Check for parent validation errors first
      const parentError = props.validationErrors.find((error) => error.field === 'allowImmediateAccess')
      if (parentError) return parentError.message

      // Then check local validation
      return null
    })

    const hasDepartmentError = computed(() => {
      return departmentError.value !== null || (props.rules.required && !props.selectedDepartment)
    })

    const departmentError = computed(() => {
      // Check for parent validation errors first
      const parentError = props.validationErrors.find((error) => error.field === 'selectedDepartment')
      if (parentError) return parentError.message

      // Then check local validation
      return null
    })

    // Instead of tabId, check if any error.field matches a field in this tab
    const tabValidationFields = [
      'titleEn',
      'titleAr',
      'descriptionEn',
      'descriptionAr',
      'startTime',
      'endTime',
      'visibility',
      'moderatorId',
      'allowImmediateAccess'
    ]
    const hasCurrentTabError = computed(() => {
      return props.validationErrors.some((error) => tabValidationFields.includes(error.field))
    })

    const handleCommitteeUpdate = (committee: CommitteeResponse) => {
      emit('update:selectedCommittee', committee)
    }

    const triggerModeratorsFetch = (reset = false) => {
      emit('fetch-moderators', {
        reset,
        searchAny: localModeratorsSearch.value
      })
    }

    const onModeratorsSearch = (value: string) => {
      localModeratorsSearch.value = value
      emit('update:moderatorsSearchTerm', value)

      if (moderatorsSearchTimeout.value) {
        window.clearTimeout(moderatorsSearchTimeout.value)
      }

      moderatorsSearchTimeout.value = window.setTimeout(() => {
        triggerModeratorsFetch(true)
      }, 300)
    }

    const onModeratorsDropdownOpen = (isOpen: boolean) => {
      if (isOpen && props.moderatorsLookupResponse.length === 0) {
        triggerModeratorsFetch(true)
      }
    }

    const onModeratorsScroll = (event: Event) => {
      const target = event.target as HTMLElement
      if (!target || props.isModeratorsLoading || !props.hasMoreModerators) {
        return
      }

      if (target.scrollTop + target.clientHeight >= target.scrollHeight - 16) {
        triggerModeratorsFetch(false)
      }
    }

    const onModeratorsIntersect = (isIntersecting: boolean) => {
      if (isIntersecting && !props.isModeratorsLoading && props.hasMoreModerators) {
        triggerModeratorsFetch(false)
      }
    }

    watch(
      () => props.moderatorsSearchTerm,
      (value) => {
        localModeratorsSearch.value = value
      }
    )

    return {
      updateField,
      locale,
      selectedCommitteeObject,
      hasStartTimeError,
      hasEndTimeError,
      startTimeError,
      endTimeError,
      hasTitleEnError,
      hasTitleArError,
      titleEnError,
      titleArError,
      hasVisibilityError,
      visibilityError,
      hasDescriptionEnError,
      descriptionEnError,
      hasDescriptionArError,
      descriptionArError,
      hasModeratorError,
      moderatorError,
      hasAllowImmediateAccessError,
      allowImmediateAccessError,
      hasDepartmentError,
      departmentError,
      hasCurrentTabError,
      handleCommitteeUpdate,
      onModeratorsSearch,
      onModeratorsDropdownOpen,
      onModeratorsScroll,
      onModeratorsIntersect
    }
  },
  methods: { translate }
})
</script>

<style scoped lang="scss">
.challenge-info-card {
  border-radius: 18px;
  box-shadow: 0 2px 16px 0 rgba(0, 0, 0, 0.08);
  padding: 2rem 1.5rem 1.5rem 1.5rem;
  background: #fff;
  margin-bottom: 2rem;
  transition: box-shadow 0.3s;
}

.challenge-info-form-row {
  row-gap: 1.5rem;
  column-gap: 0.8rem;
}

.sticky-section-header {
  position: sticky;
  top: 0;
  background: #fff;
  z-index: 10;
  padding-top: 1rem;
  padding-bottom: 1rem;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
}

.moderator-dropdown-item {
  padding-block: 6px;
}

.moderator-item-title {
  color: #1f2937;
  font-weight: 600;
}

.moderator-item-subtitle {
  color: #64748b;
  font-size: 12px;
}

.moderator-dropdown-loader {
  display: flex;
  justify-content: center;
  padding: 12px;
}

// Animation for error shake
@keyframes shake {
  0% {
    transform: translateX(0);
  }
  20% {
    transform: translateX(-8px);
  }
  40% {
    transform: translateX(8px);
  }
  60% {
    transform: translateX(-8px);
  }
  80% {
    transform: translateX(8px);
  }
  100% {
    transform: translateX(0);
  }
}

.shake-error {
  animation: shake 0.4s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}

// Responsive tweaks
@media (max-width: 960px) {
  .challenge-info-card {
    padding: 1rem 0.5rem;
  }
  .sticky-section-header {
    font-size: 1.1rem;
    padding-top: 0.5rem;
    padding-bottom: 0.5rem;
  }
}

@media (max-width: 600px) {
  .challenge-info-card {
    padding: 0.5rem 0.2rem;
  }
  .challenge-info-form-row {
    row-gap: 1rem;
    column-gap: 0.2rem;
  }
}

.error-message {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #d32f2f;
  margin-top: 4px;
  animation: fadeIn 0.3s ease-in-out;
  padding: 4px 8px;
  border-radius: 4px;
  background-color: rgba(211, 47, 47, 0.05);
  border-left: 3px solid #d32f2f;
}

.error-text {
  font-weight: 500;
}

.field-error {
  animation: pulseError 0.6s ease-in-out;
}

.field-error .v-field__outline {
  border-color: #d32f2f !important;
  transition: border-color 0.3s ease;
}

.field-error .v-field__outline__start,
.field-error .v-field__outline__end,
.field-error .v-field__outline__notch {
  border-color: #d32f2f !important;
  transition: border-color 0.3s ease;
}

.field-error .v-field__outline__notch::before {
  border-color: #d32f2f !important;
  transition: border-color 0.3s ease;
}

.field-error .v-field__outline__notch::after {
  border-color: #d32f2f !important;
  transition: border-color 0.3s ease;
}

/* Radio group error styling */
.challenge-radio-group.field-error {
  border: 1px solid #d32f2f;
  border-radius: 8px;
  padding: 8px;
  background-color: rgba(211, 47, 47, 0.02);
  transition: all 0.3s ease;
}

/* Textarea error styling */
.field-error .v-textarea .v-field__outline {
  border-color: #d32f2f !important;
  transition: border-color 0.3s ease;
}

/* Autocomplete error styling */
.field-error .v-autocomplete .v-field__outline {
  border-color: #d32f2f !important;
  transition: border-color 0.3s ease;
}

/* Animations */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulseError {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
  100% {
    transform: scale(1);
  }
}

/* Hover effects for error fields */
.field-error:hover {
  box-shadow: 0 0 0 1px rgba(211, 47, 47, 0.1);
  transition: box-shadow 0.3s ease;
}

/* Smooth transitions for all form elements */
.v-text-field,
.v-textarea,
.v-autocomplete,
.v-radio-group {
  transition: all 0.3s ease;
}

.challenge-direction-ltr :deep(input),
.challenge-direction-ltr :deep(textarea) {
  direction: ltr;
  text-align: left;
}

.challenge-direction-rtl :deep(input),
.challenge-direction-rtl :deep(textarea) {
  direction: rtl;
  text-align: right;
}

/* Committee dropdown styling */
:deep(.committee-dropdown-menu) {
  max-height: 300px !important;
  overflow: hidden;
  width: auto !important;
  min-width: 100% !important;

  .v-list {
    max-height: 300px;
    overflow-y: auto;
    width: 100%;
  }
}

.committee-dropdown-item {
  min-height: 48px;
  padding: 8px 16px;
  width: 100%;

  .committee-item-title {
    font-size: 14px;
    line-height: 1.4;
    word-wrap: break-word;
    white-space: normal;
    overflow-wrap: break-word;
    hyphens: auto;
    max-width: 100%;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    padding-right: 8px;
  }
}

/* Ensure dropdown matches autocomplete width */
:deep(.v-autocomplete) {
  .v-field__input {
    min-height: 40px;
  }

  .v-menu__content {
    max-height: 300px !important;
    overflow: hidden;
    width: auto !important;
    min-width: 100% !important;
  }

  /* Make sure the menu content matches the field width */
  .v-menu__content .v-list {
    width: 100%;
    min-width: 100%;
  }
}

.tab-content-error {
  border: 2px solid #f44336 !important;
  background: #ffebee !important;
  box-shadow: 0 0 0 2px #f4433633;
}
</style>
