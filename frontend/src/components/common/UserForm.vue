<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { computed, defineComponent, nextTick, onMounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { toast } from 'vue3-toastify'
import { get } from '@/utils/GenericApiHelper'
import { URIS } from '@/constants/DpConstants'
import { EmployeeGrpResponse } from '@/dto/core/EmployeeGrpResponse'
import { UserCreationRequest } from '@/dto/users/UserCreationRequest'
import { sanitizeObject, textSafetyRule } from '@/utils/inputSanitizationUtils'
import { resolveLocalizedFieldValue } from '@/utils/localizationUtils'

interface DropDownListRequest {
  id: number
  name: string
  localName: string
  nameAr?: string
  nameEn?: string
  ar?: string
  en?: string
}

interface UsersExpertises {
  areaOfExpertisesId: string
  expertLevel: string
  isPrimary: boolean
  areaOfExpertisesName?: string
}

interface ValidationError {
  field: string
  message: string
}

interface RequiredFieldChip {
  id: string
  label: string
  icon: string
  field: string
  isValid: boolean
}

export default defineComponent({
  name: 'UserForm',
  props: {
    mode: {
      type: String,
      required: true,
      validator: (value: string) => ['create', 'update'].includes(value)
    },
    initialData: {
      type: Object,
      default: () => ({})
    },
    availableRoles: {
      type: Array as () => DropDownListRequest[],
      default: () => []
    },
    availableExpertises: {
      type: Array as () => DropDownListRequest[],
      default: () => []
    }
  },
  emits: ['submit', 'cancel', 'data-fetched'],
  setup(props, { emit }) {
    const { t, locale } = useI18n()

    // State
    const loading = ref(false)
    const isGrpSearch = ref(props.mode === 'create')
    const isGrpNumberReadOnly = ref(props.mode === 'update')
    const showFormFields = ref(props.mode === 'update')
    const validationErrors = ref<ValidationError[]>([])
    const selectedRoles = ref<number[]>([])
    const selectedUsersExpertisesList = ref<UsersExpertises[]>([])
    const viewUsersExpertisesList = ref<UsersExpertises[]>([])
    const isAnyPrimaryExpertiseSelected = ref<boolean>(false)
    const selectAll = ref(false)
    const itemsPerPage = ref(5)
    const currentPage = ref(1)

    // Form data
    const formData = ref({
      grpNumber: props.initialData.grpNumber || '',
      language: props.initialData.language || '',
      fullName: props.initialData.fullName || '',
      shortDepartmentName: props.initialData.shortDepartmentName || '',
      gradeName: props.initialData.gradeName || '',
      jobName: props.initialData.jobName || '',
      email: props.initialData.email || '',
      alternativeEmail: props.initialData.alternativeEmail || '',
      mobile: props.initialData.mobile || '0501414755', // Default mobile fallback
      alternativeMobile: props.initialData.alternativeMobile || '',
      internalDocuments: props.initialData.internalDocuments || false,
      chatContents: props.initialData.chatContents || false,
      socialMediaFeeds: props.initialData.socialMediaFeeds || false,
      decision: props.initialData.decision || false,
      questionView: props.initialData.questionView || false,
      scrollingText: props.initialData.scrollingText || false,
      isDelphiModerator: props.initialData.isDelphiModerator || false,
      roles: selectedRoles,
      usersExpertises: selectedUsersExpertisesList
    })

    const usersExpertisesData = ref({
      areaOfExpertisesId: '',
      expertLevel: '',
      isPrimary: false
    })

    // Computed properties
    const selectedRolesAsStrings = computed(() => selectedRoles.value.map(String))

    const localizedAvailableRoles = computed(() => {
      return props.availableRoles.map((role) => ({
        id: role.id,
        name: resolveLocalizedFieldValue({
          item: role,
          locale: locale.value,
          fieldName: 'name',
          fallback: String(role.id)
        })
      }))
    })

    const localizedAvailableExpertises = computed(() => {
      return props.availableExpertises.map((ex) => ({
        id: ex.id,
        name: resolveLocalizedFieldValue({
          item: ex,
          locale: locale.value,
          fieldName: 'name',
          fallback: String(ex.id)
        })
      }))
    })

    const expertiseLevels = ref([
      { id: 'expert', value: t('common.expertLevels.expert') },
      { id: 'intermediate', value: t('common.expertLevels.intermediate') },
      { id: 'basic', value: t('common.expertLevels.basic') }
    ])

    const isRtl = computed(() => locale.value === 'ar')

    const languageList = ref([
      { id: 'EN', value: t('common.language.en') },
      { id: 'AR', value: t('common.language.ar') }
    ])

    // Required fields chips
    const requiredFields = computed<RequiredFieldChip[]>(() => {
      const fields = [
        {
          id: 'fullName',
          label: t('user.add.fullName'),
          icon: 'mdi-account',
          field: 'fullName',
          isValid: !!formData.value.fullName?.trim()
        },
        {
          id: 'email',
          label: t('user.search.email'),
          icon: 'mdi-email',
          field: 'email',
          isValid: !!formData.value.email?.trim()
        },
        {
          id: 'mobile',
          label: t('user.search.mobile'),
          icon: 'mdi-cellphone',
          field: 'mobile',
          isValid: !!formData.value.mobile?.trim()
        },
        {
          id: 'language',
          label: t('user.add.language'),
          icon: 'mdi-web',
          field: 'language',
          isValid: !!formData.value.language?.trim()
        },
        {
          id: 'roles',
          label: t('user.search.list.role'),
          icon: 'mdi-security',
          field: 'roles',
          isValid: selectedRoles.value.length > 0
        }
      ]

      // Add expertise validation only if expertises are selected
      if (selectedUsersExpertisesList.value.length > 0) {
        fields.push({
          id: 'expertises',
          label: t('user.add.expertisesMainTitle'),
          icon: 'mdi-head-snowflake-outline',
          field: 'expertises',
          isValid: selectedUsersExpertisesList.value.some((expertise) => expertise.isPrimary)
        })
      }

      return fields
    })

    const expertisesHeaders = [
      {
        title: t('user.add.expertisesTitle'),
        align: 'start',
        key: 'areaOfExpertisesId',
        width: '20%'
      },
      {
        title: t('user.add.expertLevel'),
        align: 'start',
        key: 'expertLevel',
        width: '15%'
      },
      {
        title: t('user.add.defaultExpertises'),
        align: 'start',
        key: 'isPrimaryLocal',
        width: '15%'
      },
      {
        title: t('common.actions'),
        align: 'end',
        key: 'actions'
      }
    ]

    // Methods
    const fetchUserData = async () => {
      if (!formData.value.grpNumber?.trim()) {
        toast.error(t('error.mandatory'))
        return
      }

      loading.value = true
      try {
        const response: any = await get(
          `${URIS.CORE.BASE + URIS.CORE.EMPLOYEE_INFO + '?grpNumber='}${formData.value.grpNumber}`
        )

        if (response && response.data) {
          const data = response.data as EmployeeGrpResponse

          // Populate form data
          formData.value.fullName = data.fullName || ''
          formData.value.email = data.emailAddress || ''
          formData.value.gradeName = data.gradeName || ''
          formData.value.jobName = data.jobName || ''
          formData.value.shortDepartmentName = data.departmentName || ''
          formData.value.mobile = data.empMobile || '0501414755' // Default fallback

          isGrpNumberReadOnly.value = true
          isGrpSearch.value = false
          showFormFields.value = true

          // Emit data fetched event
          emit('data-fetched', data)

          toast.success(t('Messages.success.data.fetched'))
        }
      } catch (error: any) {
        toast.error(error.message || t('Messages.error.data.fetchFailed'))
      } finally {
        loading.value = false
      }
    }

    const cancelSearch = () => {
      formData.value.grpNumber = ''
      isGrpNumberReadOnly.value = false
      isGrpSearch.value = true
      showFormFields.value = false
      // Reset form data
      Object.keys(formData.value).forEach((key) => {
        if (key !== 'grpNumber') {
          if (typeof formData.value[key] === 'boolean') {
            formData.value[key] = false
          } else if (Array.isArray(formData.value[key])) {
            formData.value[key] = []
          } else {
            formData.value[key] = ''
          }
        }
      })
      formData.value.mobile = '0501414755' // Reset to default
    }

    const scrollToField = async (fieldId: string) => {
      await nextTick()
      const element = document.getElementById(fieldId)
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'center' })
        element.focus()
      }
    }

    const validateForm = (): ValidationError[] => {
      const errors: ValidationError[] = []

      if (!formData.value.grpNumber?.trim()) {
        errors.push({ field: 'grpNumber', message: t('error.mandatory') })
      }

      if (!selectedRoles.value.length) {
        errors.push({ field: 'roles', message: t('error.mandatory') })
      }

      if (!formData.value.email?.trim()) {
        errors.push({ field: 'email', message: t('error.mandatory') })
      }

      if (!formData.value.language?.trim()) {
        errors.push({ field: 'language', message: t('error.mandatory') })
      }

      if (!formData.value.mobile?.trim()) {
        errors.push({ field: 'mobile', message: t('error.mandatory') })
      }

      // Text safety validation for all string inputs
      const textFields = [
        { field: 'grpNumber', value: formData.value.grpNumber },
        { field: 'fullName', value: formData.value.fullName },
        { field: 'shortDepartmentName', value: formData.value.shortDepartmentName },
        { field: 'gradeName', value: formData.value.gradeName },
        { field: 'jobName', value: formData.value.jobName },
        { field: 'email', value: formData.value.email },
        { field: 'alternativeEmail', value: formData.value.alternativeEmail },
        { field: 'mobile', value: formData.value.mobile },
        { field: 'alternativeMobile', value: formData.value.alternativeMobile }
      ]

      textFields.forEach(({ field, value }) => {
        if (value && typeof value === 'string') {
          const safetyRule = textSafetyRule()
          const validationResult = safetyRule(value)
          if (validationResult !== true) {
            errors.push({ field, message: validationResult as string })
          }
        }
      })

      // Validate expertise selection: if expertises are selected, at least one must be primary
      if (selectedUsersExpertisesList.value.length > 0) {
        const hasPrimaryExpertise = selectedUsersExpertisesList.value.some((expertise) => expertise.isPrimary)
        if (!hasPrimaryExpertise) {
          errors.push({ field: 'expertises', message: t('user.add.primaryExpertiseRequired') })
        }
      }

      return errors
    }

    const handleSubmit = () => {
      validationErrors.value = validateForm()

      if (validationErrors.value.length > 0) {
        // Don't show toast for validation errors - they're displayed in the alert component
        return
      }

      // Sanitize the form data before submission to prevent injection attacks
      const rawSubmissionData: UserCreationRequest = {
        grpNumber: formData.value.grpNumber,
        language: formData.value.language,
        email: formData.value.email,
        alternativeEmail: formData.value.alternativeEmail,
        mobile: formData.value.mobile,
        alternativeMobile: formData.value.alternativeMobile,
        internalDocuments: formData.value.internalDocuments,
        chatContents: formData.value.chatContents,
        socialMediaFeeds: formData.value.socialMediaFeeds,
        decision: formData.value.decision,
        questionView: formData.value.questionView,
        scrollingText: formData.value.scrollingText,
        isDelphiModerator: formData.value.isDelphiModerator,
        roles: selectedRoles.value,
        usersExpertises: selectedUsersExpertisesList.value
      }

      // Apply sanitization to all text fields in the submission data
      const sanitizedSubmissionData = sanitizeObject(rawSubmissionData)

      emit('submit', sanitizedSubmissionData)
    }

    const handleCancel = () => {
      emit('cancel')
    }

    // Expertise management
    const addExpertise = (event: any) => {
      event.preventDefault()
      if (usersExpertisesData.value.areaOfExpertisesId && usersExpertisesData.value.expertLevel) {
        if (
          selectedUsersExpertisesList.value.some(
            (expertise) => expertise.areaOfExpertisesId === usersExpertisesData.value.areaOfExpertisesId
          )
        ) {
          toast.warning(t('user.add.expertiseAlreadyExists'))
        } else {
          selectedUsersExpertisesList.value.push({
            ...usersExpertisesData.value
          })

          usersExpertisesData.value.areaOfExpertisesId = localizedAvailableExpertises.value
            .filter(
              (localizedAvailableExpertise) =>
                localizedAvailableExpertise.id === parseInt(usersExpertisesData.value.areaOfExpertisesId)
            )
            .map((expertises) => expertises.name)[0]

          viewUsersExpertisesList.value.push({
            ...usersExpertisesData.value
          })

          // Clear the input fields
          usersExpertisesData.value = {
            areaOfExpertisesId: '',
            expertLevel: '',
            isPrimary: false
          }

          // Update primary expertise status after adding
          isAnyPrimaryExpertiseSelected.value = selectedUsersExpertisesList.value.some(
            (expertise) => expertise.isPrimary
          )

          if (viewUsersExpertisesList.value.length % itemsPerPage.value === 1 && currentPage.value > 1) {
            currentPage.value--
          }

          toast.success(t('user.add.expertiseAdded'))
        }
      } else {
        toast.error(t('user.add.fillRequiredExpertiseFields'))
      }
    }

    const removeExpertise = (item: any) => {
      // Find the index of the item to remove from both lists
      const selectedIndex = selectedUsersExpertisesList.value.findIndex((expertise) => {
        // In update mode, we need to match by expertise name or ID
        if (props.mode === 'update') {
          // If the item has areaOfExpertisesName, use it for comparison
          if (item.areaOfExpertisesName) {
            return expertise.areaOfExpertisesName === item.areaOfExpertisesName
          }
          // Otherwise, try to match by the original ID
          return expertise.areaOfExpertisesId === item.areaOfExpertisesId
        }
        // In create mode, match by areaOfExpertisesId
        return expertise.areaOfExpertisesId === item.areaOfExpertisesId
      })

      const viewIndex = viewUsersExpertisesList.value.findIndex(
        (expertise) => expertise.areaOfExpertisesId === item.areaOfExpertisesId
      )

      // Remove from both lists if found
      if (selectedIndex !== -1) {
        const newSelectedExpertises = [...selectedUsersExpertisesList.value]
        newSelectedExpertises.splice(selectedIndex, 1)
        selectedUsersExpertisesList.value = newSelectedExpertises
      }

      if (viewIndex !== -1) {
        const newViewExpertises = [...viewUsersExpertisesList.value]
        newViewExpertises.splice(viewIndex, 1)
        viewUsersExpertisesList.value = newViewExpertises
      }

      // Update primary expertise status after removal
      isAnyPrimaryExpertiseSelected.value = selectedUsersExpertisesList.value.some((expertise) => expertise.isPrimary)

      toast.success(t('user.add.expertiseRemoved'))
    }

    const moveRolesToSelected = () => {
      // This will be handled by the parent component
    }

    // Permissions management
    const toggleAllPermissions = () => {
      const data = formData.value
      data.internalDocuments = selectAll.value
      data.chatContents = selectAll.value
      data.socialMediaFeeds = selectAll.value
      data.decision = selectAll.value
      data.questionView = selectAll.value
      data.scrollingText = selectAll.value
    }

    const updateSelectAll = () => {
      const data = formData.value
      selectAll.value =
        data.internalDocuments &&
        data.chatContents &&
        data.socialMediaFeeds &&
        data.decision &&
        data.questionView &&
        data.scrollingText
    }

    // Watchers
    watch(
      formData,
      (newValue) => {
        selectAll.value =
          newValue.internalDocuments &&
          newValue.chatContents &&
          newValue.socialMediaFeeds &&
          newValue.decision &&
          newValue.questionView &&
          newValue.scrollingText
      },
      { deep: true }
    )

    // Watch for changes in expertise list to update validation
    watch(
      selectedUsersExpertisesList,
      (newExpertises) => {
        isAnyPrimaryExpertiseSelected.value = newExpertises.some((expertise) => expertise.isPrimary)
      },
      { deep: true }
    )

    // Computed for pagination
    const slicedData = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage.value
      const end = start + itemsPerPage.value
      return viewUsersExpertisesList.value.slice(start, end)
    })

    const localizedSlicedData = computed(() => {
      return slicedData.value.map((item) => ({
        ...item,
        expertLevel: expertiseLevels.value.find((level) => level.id === item.expertLevel)?.value || item.expertLevel,
        isPrimaryLocal: item.isPrimary ? t('common.yes') : t('common.no')
      }))
    })

    // Initialize form if in update mode
    onMounted(() => {
      if (props.mode === 'update' && props.initialData) {
        // Helper function to get expertise display name
        const getExpertiseDisplayName = (expertise: any, availableExpertises: any[]) => {
          if (expertise.areaOfExpertisesName) {
            return expertise.areaOfExpertisesName
          }

          const foundExpertise = availableExpertises.find((ex: any) => ex.id === parseInt(expertise.areaOfExpertisesId))

          if (!foundExpertise) {
            return expertise.areaOfExpertisesId
          }

          return resolveLocalizedFieldValue({
            item: foundExpertise,
            locale: locale.value,
            fieldName: 'name',
            fallback: String(expertise.areaOfExpertisesId)
          })
        }

        // Populate form with initial data
        Object.keys(props.initialData).forEach((key) => {
          if (key in formData.value) {
            formData.value[key] = props.initialData[key]
          }
        })

        // Set roles if available
        if (props.initialData.roles) {
          selectedRoles.value = props.initialData.roles.map((role: any) => role.id)
        }

        // Process expertises if available
        if (props.initialData.usersExpertises) {
          const transformedExpertises = props.initialData.usersExpertises.map((expertise: any) => ({
            ...expertise,
            areaOfExpertisesId: getExpertiseDisplayName(expertise, props.availableExpertises)
          }))

          selectedUsersExpertisesList.value = [...props.initialData.usersExpertises]
          viewUsersExpertisesList.value = [...transformedExpertises]
        }
      }
    })

    return {
      // State
      loading,
      isGrpSearch,
      isGrpNumberReadOnly,
      showFormFields,
      validationErrors,
      selectedRoles,
      selectedUsersExpertisesList,
      viewUsersExpertisesList,
      isAnyPrimaryExpertiseSelected,
      selectAll,
      itemsPerPage,
      currentPage,
      formData,
      usersExpertisesData,

      // Computed
      selectedRolesAsStrings,
      localizedAvailableRoles,
      localizedAvailableExpertises,
      expertiseLevels,
      isRtl,
      languageList,
      requiredFields,
      expertisesHeaders,
      slicedData,
      localizedSlicedData,

      // Methods
      fetchUserData,
      cancelSearch,
      scrollToField,
      validateForm,
      handleSubmit,
      handleCancel,
      addExpertise,
      removeExpertise,
      moveRolesToSelected,
      toggleAllPermissions,
      updateSelectAll
    }
  }
})
</script>

<template>
  <div :class="['user-form-container', { 'user-form-container--rtl': isRtl, 'user-form-container--ltr': !isRtl }]">
    <!-- Header with Required Fields Chips -->
    <div v-if="showFormFields" class="required-fields-section">
      <h3 class="required-fields-title">{{ $t('user.add.requiredFields') }}</h3>
      <div class="required-fields-chips">
        <v-chip
          v-for="field in requiredFields"
          :key="field.id"
          :color="field.isValid ? 'success' : 'warning'"
          variant="elevated"
          size="small"
          class="field-chip"
          @click="scrollToField(field.field)"
        >
          <v-icon start :icon="field.icon" size="16"></v-icon>
          {{ field.label }}
          <v-icon end :icon="field.isValid ? 'mdi-check' : 'mdi-alert'" size="16"></v-icon>
        </v-chip>
      </div>
    </div>

    <!-- GRP Number Search Section -->
    <v-card class="search-card mb-4">
      <v-card-title class="search-title common-search-title-creation-and-update-user">
        <v-icon start>mdi-identifier</v-icon>
        {{ $t('user.search.list.grpNumber') }}
      </v-card-title>
      <div class="grp-search-hint mb-2">
        {{ $t('user.search.grpNumberSearchHint') }}
      </div>
      <v-card-text>
        <v-row>
          <v-col :cols="!isGrpSearch ? 10 : 12">
            <v-text-field
              v-model="formData.grpNumber"
              :placeholder="$t('user.search.list.grpNumber')"
              prepend-icon="mdi-identifier"
              :append-inner-icon="isGrpSearch ? 'mdi-magnify' : 'mdi-magnify-remove-outline'"
              :disabled="!isGrpSearch"
              density="default"
              variant="outlined"
              :readonly="isGrpNumberReadOnly"
              :error="validationErrors.some((e) => e.field === 'grpNumber')"
              @keyup.enter="fetchUserData"
            >
              <template #label>
                <div>
                  {{ $t('user.search.list.grpNumber') }}
                  <span class="required-asterisk">*</span>
                </div>
              </template>
            </v-text-field>
          </v-col>

          <v-col
            v-if="!isGrpSearch && mode === 'create'"
            cols="auto"
            class="grp-close-column d-flex align-center justify-center pa-0"
          >
            <v-tooltip :text="$t('user.creation.resetAndSearch')" location="top">
              <template #activator="{ props }">
                <v-btn
                  density="compact"
                  icon
                  color="error"
                  class="grp-close-btn"
                  size="50"
                  v-bind="props"
                  @click="cancelSearch"
                >
                  <v-icon>mdi-close</v-icon>
                </v-btn>
              </template>
            </v-tooltip>
          </v-col>
        </v-row>

        <v-btn
          v-if="isGrpSearch"
          color="primary"
          variant="elevated"
          :loading="loading"
          class="search-btn"
          @click="fetchUserData"
        >
          <v-icon start>mdi-magnify</v-icon>
          {{ $t('common.search') }}
        </v-btn>
      </v-card-text>
    </v-card>

    <!-- Main Form Fields (Hidden initially, shown after successful search) -->
    <v-expand-transition>
      <div v-show="showFormFields" class="form-fields-section">
        <!-- Validation Errors Summary -->
        <v-alert v-if="validationErrors.length > 0" type="error" variant="tonal" class="mb-4" closable>
          <template #title>
            <div class="d-flex align-center">
              <v-icon start>mdi-alert-circle</v-icon>
              {{ $t('common.validationErrors') }} ({{ validationErrors.length }})
            </div>
          </template>
          <div class="d-flex flex-wrap gap-2">
            <v-chip v-for="error in validationErrors" :key="error.field" color="error" variant="elevated" size="small">
              {{ error.message }}
            </v-chip>
          </div>
        </v-alert>

        <!-- Basic Information Section -->
        <v-card class="form-section-card mb-4">
          <v-card-title class="section-title">
            <v-icon start>mdi-account-details</v-icon>
            {{ $t('user.add.basicInformation') }}
          </v-card-title>
          <v-card-text>
            <v-row>
              <!-- Full Name -->
              <v-col cols="12" md="6">
                <v-text-field
                  id="fullName"
                  v-model="formData.fullName"
                  :label="$t('user.add.fullName')"
                  :readonly="isGrpNumberReadOnly"
                  prepend-icon="mdi-account"
                  density="default"
                  variant="outlined"
                  :error="validationErrors.some((e) => e.field === 'fullName')"
                ></v-text-field>
              </v-col>

              <!-- Department -->
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.shortDepartmentName"
                  :label="$t('user.add.department')"
                  :readonly="isGrpNumberReadOnly"
                  prepend-icon="mdi-bank"
                  density="default"
                  variant="outlined"
                ></v-text-field>
              </v-col>

              <!-- Grade Name -->
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.gradeName"
                  :label="$t('user.add.gradeName')"
                  prepend-icon="mdi-account-school-outline"
                  density="default"
                  variant="outlined"
                  :readonly="isGrpNumberReadOnly"
                ></v-text-field>
              </v-col>

              <!-- Job Name -->
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.jobName"
                  :label="$t('user.add.jobName')"
                  prepend-icon="mdi-briefcase-outline"
                  density="default"
                  variant="outlined"
                  :readonly="isGrpNumberReadOnly"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>

        <!-- Contact Information Section -->
        <v-card class="form-section-card mb-4">
          <v-card-title class="section-title">
            <v-icon start>mdi-phone-message</v-icon>
            {{ $t('user.add.contactInformation') }}
          </v-card-title>
          <v-card-text>
            <v-row>
              <!-- Email -->
              <v-col cols="12" md="6">
                <v-text-field
                  id="email"
                  v-model="formData.email"
                  :label="$t('user.search.email')"
                  :readonly="isGrpNumberReadOnly"
                  prepend-icon="mdi-email"
                  density="default"
                  variant="outlined"
                  :error="validationErrors.some((e) => e.field === 'email')"
                >
                  <template #label>
                    <div>
                      {{ $t('user.search.email') }}
                      <span class="required-asterisk">*</span>
                    </div>
                  </template>
                </v-text-field>
              </v-col>

              <!-- Alternative Email -->
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.alternativeEmail"
                  :label="$t('user.add.alternativeEmail')"
                  prepend-icon="mdi-email-plus-outline"
                  density="default"
                  variant="outlined"
                  clearable
                ></v-text-field>
              </v-col>

              <!-- Mobile -->
              <v-col cols="12" md="6">
                <v-text-field
                  id="mobile"
                  v-model="formData.mobile"
                  :label="$t('user.search.mobile')"
                  :readonly="isGrpNumberReadOnly"
                  prepend-icon="mdi-cellphone"
                  density="default"
                  variant="outlined"
                  :error="validationErrors.some((e) => e.field === 'mobile')"
                >
                  <template #label>
                    <div>
                      {{ $t('user.search.mobile') }}
                      <span class="required-asterisk">*</span>
                    </div>
                  </template>
                </v-text-field>
              </v-col>

              <!-- Alternative Mobile -->
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.alternativeMobile"
                  :label="$t('user.add.alternativeMobile')"
                  prepend-icon="mdi-phone-plus"
                  density="default"
                  variant="outlined"
                  clearable
                ></v-text-field>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>

        <!-- Settings Section -->
        <v-card class="form-section-card mb-4">
          <v-card-title class="section-title">
            <v-icon start>mdi-cog</v-icon>
            {{ $t('user.add.settings') }}
          </v-card-title>
          <v-card-text>
            <v-row>
              <!-- Language Selection -->
              <v-col cols="12" md="6">
                <v-autocomplete
                  id="language"
                  v-model="formData.language"
                  :items="languageList"
                  prepend-icon="mdi-web"
                  item-value="id"
                  item-title="value"
                  variant="outlined"
                  density="default"
                  :error="validationErrors.some((e) => e.field === 'language')"
                >
                  <template #label>
                    <div>
                      {{ $t('user.add.language') }}
                      <span class="required-asterisk">*</span>
                    </div>
                  </template>
                </v-autocomplete>
              </v-col>

              <!-- Delphi Moderator -->
              <v-col cols="12" md="6">
                <v-checkbox
                  v-model="formData.isDelphiModerator"
                  :label="$t('user.add.isDelphiModerator')"
                  prepend-icon="mdi-shield-crown"
                  hide-details
                ></v-checkbox>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>

        <!-- Roles Section -->
        <v-card class="form-section-card mb-4">
          <v-card-title class="section-title">
            <v-icon start>mdi-security</v-icon>
            {{ $t('user.search.list.role') }}
          </v-card-title>
          <v-card-text>
            <v-select
              id="roles"
              v-model="selectedRoles"
              :items="localizedAvailableRoles"
              item-value="id"
              item-title="name"
              :label="$t('user.search.list.role')"
              prepend-icon="mdi-security"
              variant="outlined"
              density="default"
              multiple
              chips
              closable-chips
              :error="validationErrors.some((e) => e.field === 'roles')"
            >
              <template #label>
                <div>
                  {{ $t('user.search.list.role') }}
                  <span class="required-asterisk">*</span>
                </div>
              </template>
            </v-select>
          </v-card-text>
        </v-card>

        <!-- Expertises Section -->
        <v-card class="form-section-card mb-4">
          <v-card-title class="section-title">
            <v-icon start>mdi-head-snowflake-outline</v-icon>
            {{ $t('user.add.expertisesMainTitle') }}
          </v-card-title>
          <v-card-text>
            <form @submit="addExpertise">
              <v-row>
                <v-col cols="12" sm="6" md="3">
                  <v-select
                    v-model="usersExpertisesData.areaOfExpertisesId"
                    :items="localizedAvailableExpertises"
                    item-value="id"
                    item-title="name"
                    :label="$t('user.add.expertisesTitle')"
                    prepend-icon="mdi-head-snowflake-outline"
                    variant="outlined"
                    clearable
                    dense
                  ></v-select>
                </v-col>
                <v-col cols="12" sm="6" md="3">
                  <v-autocomplete
                    v-model="usersExpertisesData.expertLevel"
                    :items="expertiseLevels"
                    prepend-icon="mdi-head-snowflake"
                    clearable
                    :label="$t('user.add.expertLevel')"
                    item-title="value"
                    item-value="id"
                    variant="outlined"
                    density="default"
                  ></v-autocomplete>
                </v-col>
                <v-col cols="12" sm="6" md="2">
                  <v-checkbox
                    v-model="usersExpertisesData.isPrimary"
                    :disabled="isAnyPrimaryExpertiseSelected"
                    prepend-icon="mdi-key-star"
                    value="true"
                    :label="$t('common.default')"
                    type="checkbox"
                  ></v-checkbox>
                </v-col>
                <v-col cols="12" sm="6" md="2" class="d-flex align-center">
                  <v-btn type="submit" color="primary" class="me-4">
                    {{ $t('common.submit') }}
                  </v-btn>
                </v-col>
              </v-row>
            </form>
          </v-card-text>
        </v-card>

        <!-- Expertises Table -->
        <v-card v-if="viewUsersExpertisesList.length > 0" class="form-section-card mb-4">
          <v-card-text>
            <v-data-table-server
              v-model:items-per-page="itemsPerPage"
              :headers="expertisesHeaders"
              :items="localizedSlicedData"
              :items-length="viewUsersExpertisesList.length"
              class="elevation-1"
              item-value="name"
            >
              <template #item.actions="{ item }">
                <v-btn density="default" color="red" variant="tonal" @click="removeExpertise(item)">
                  <i class="mdi mdi-trash-can"></i>
                </v-btn>
              </template>
            </v-data-table-server>

            <v-pagination
              v-model="currentPage"
              :length="Math.ceil(viewUsersExpertisesList.length / itemsPerPage)"
              class="mt-4"
            ></v-pagination>
          </v-card-text>
        </v-card>

        <!-- Permissions Section -->
        <v-card class="form-section-card mb-4">
          <v-card-title class="section-title">
            <v-icon start>mdi-shield-account</v-icon>
            {{ $t('user.add.permissionDetails') }}
          </v-card-title>
          <v-card-text>
            <v-container fluid>
              <v-row>
                <v-checkbox
                  v-model="selectAll"
                  :label="$t('user.add.selectAll')"
                  hide-details
                  @change="toggleAllPermissions"
                ></v-checkbox>
              </v-row>
              <v-row>
                <v-col cols="12" sm="6" md="4">
                  <v-checkbox
                    v-model="formData.chatContents"
                    :label="$t('user.add.chatContents')"
                    hide-details
                    @change="updateSelectAll"
                  ></v-checkbox>
                  <v-checkbox
                    v-model="formData.decision"
                    :label="$t('user.add.decision')"
                    hide-details
                    @change="updateSelectAll"
                  ></v-checkbox>
                </v-col>
                <v-col cols="12" sm="6" md="4">
                  <v-checkbox
                    v-model="formData.internalDocuments"
                    :label="$t('user.add.internalDocuments')"
                    hide-details
                    @change="updateSelectAll"
                  ></v-checkbox>
                  <v-checkbox
                    v-model="formData.questionView"
                    :label="$t('user.add.questionView')"
                    hide-details
                    @change="updateSelectAll"
                  ></v-checkbox>
                </v-col>
                <v-col cols="12" sm="6" md="4">
                  <v-checkbox
                    v-model="formData.scrollingText"
                    :label="$t('user.add.scrollingText')"
                    hide-details
                    @change="updateSelectAll"
                  ></v-checkbox>
                  <v-checkbox
                    v-model="formData.socialMediaFeeds"
                    :label="$t('user.add.socialMediaFeeds')"
                    hide-details
                    @change="updateSelectAll"
                  ></v-checkbox>
                </v-col>
              </v-row>
            </v-container>
          </v-card-text>
        </v-card>

        <!-- Action Buttons -->
        <v-row class="action-buttons">
          <v-col cols="12" sm="2"></v-col>
          <v-col cols="12" sm="3">
            <v-btn color="primary" prepend-icon="mdi-check" large width="100%" @click="handleSubmit">
              {{ mode === 'create' ? $t('user.add.Submit') : $t('common.update') }}
            </v-btn>
          </v-col>
          <v-col cols="12" sm="2"></v-col>
          <v-col cols="12" sm="3">
            <v-btn
              color="error"
              :prepend-icon="mode === 'create' ? 'mdi-close' : undefined"
              width="100%"
              large
              @click="handleCancel"
            >
              {{ $t('common.cancel') }}
            </v-btn>
          </v-col>
        </v-row>
      </div>
    </v-expand-transition>
  </div>
</template>

<style scoped lang="scss">
.user-form-container {
  --user-form-inline-space: clamp(1rem, 2vw, 1.5rem);
  --user-form-block-space: clamp(1rem, 2.2vw, 1.75rem);
  --user-form-surface: rgba(255, 255, 255, 0.96);

  display: grid;
  gap: 1.25rem;

  .required-fields-section {
    margin-bottom: 2rem;
    padding: var(--user-form-block-space) var(--user-form-inline-space);
    background: linear-gradient(135deg, #f8fbff 0%, #dfeaf7 100%);
    border-radius: 20px;
    border: 1px solid #d7e3f0;
    box-shadow: 0 12px 30px rgba(15, 23, 42, 0.05);

    .required-fields-title {
      font-size: 1.1rem;
      font-weight: 600;
      margin-bottom: 1rem;
      color: #2c3e50;
    }

    .required-fields-chips {
      display: flex;
      flex-wrap: wrap;
      gap: 0.5rem;

      .field-chip {
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }
      }
    }
  }

  .search-card {
    overflow: hidden;
    border: 1px solid #dcecf8;
    border-radius: 20px;
    background: linear-gradient(180deg, var(--user-form-surface) 0%, #f8fcff 100%);
    box-shadow: 0 14px 30px rgba(21, 101, 192, 0.08);

    :deep(.v-card-text) {
      padding: var(--user-form-block-space) var(--user-form-inline-space) calc(var(--user-form-block-space) + 0.25rem);
    }

    .search-title {
      padding: 1rem var(--user-form-inline-space);
      background: linear-gradient(135deg, #e8f4fe 0%, #c9e4ff 100%);
      color: #1565c0;
      font-weight: 600;
    }

    .search-btn {
      margin-top: 1rem;
      min-height: 46px;
      padding-inline: 1.5rem;
    }
  }

  .form-section-card {
    overflow: hidden;
    border-radius: 20px;
    border: 1px solid rgba(222, 226, 230, 0.85);
    background: linear-gradient(180deg, var(--user-form-surface) 0%, #fcfcfd 100%);
    box-shadow: 0 12px 28px rgba(15, 23, 42, 0.06);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 18px 32px rgba(15, 23, 42, 0.1);
    }

    :deep(.v-card-text) {
      padding: var(--user-form-block-space) var(--user-form-inline-space);
    }

    .section-title {
      padding: 1rem var(--user-form-inline-space);
      background: linear-gradient(135deg, #f8fafc 0%, #eef3f8 100%);
      color: #495057;
      font-weight: 600;
      border-bottom: 1px solid #dee2e6;
    }
  }

  .required-asterisk {
    color: #dc3545;
    font-weight: bold;
  }

  .action-buttons {
    margin-top: 2rem;
    padding-top: 2rem;
    border-top: 2px solid #e9ecef;
  }

  .grp-close-column {
    inline-size: 80px;
    block-size: 80px;
  }

  // Responsive design
  @media (max-width: 768px) {
    --user-form-inline-space: 1rem;
    --user-form-block-space: 1rem;

    .required-fields-chips {
      justify-content: center;
    }

    .search-card .search-title {
      font-size: 1rem;
    }

    .form-section-card .section-title {
      font-size: 1rem;
    }

    .action-buttons {
      margin-top: 1.5rem;
      padding-top: 1.5rem;
    }
  }

  @media (max-width: 576px) {
    gap: 1rem;

    .required-fields-chips {
      flex-direction: column;
      align-items: center;
    }

    .grp-close-column {
      inline-size: 100%;
      block-size: auto;
      justify-content: flex-start !important;
      padding-inline: 0.25rem !important;
      padding-bottom: 0.5rem !important;
    }

    .action-buttons {
      .v-col {
        margin-bottom: 1rem;
      }
    }
  }
}

.user-form-container--rtl {
  .required-fields-section,
  .search-card,
  .form-section-card,
  .grp-search-hint,
  .action-buttons {
    text-align: right;
  }

  .required-fields-chips {
    justify-content: flex-end;
  }

  .grp-close-column {
    justify-content: flex-start !important;
  }
}

.user-form-container--ltr {
  .grp-close-column {
    justify-content: center;
  }
}

.common-search-title-creation-and-update-user {
  margin-bottom: 0;
}

.grp-close-btn {
  margin-top: 0 !important;
  margin-bottom: 0 !important;
  margin-inline-start: -0.5rem;
}

.grp-search-hint {
  margin-inline: var(--user-form-inline-space);
  margin-top: 0.9rem;
  color: #5f6b7a;
  font-size: 0.98rem;
  line-height: 1.6;
}

@media (max-width: 576px) {
  .grp-close-btn {
    margin-inline-start: 0;
  }

  .grp-search-hint {
    margin-top: 0.75rem;
    margin-inline: 1rem;
    font-size: 0.92rem;
  }
}
</style>
