<template>
  <div class="challenge-progress-container">
    <!-- Progress Bar -->
    <div class="progress-bar-container mb-4">
      <div class="progress-bar">
        <div
          class="progress-fill"
          :style="{ width: `${progress}%` }"
          :class="{
            'progress-complete': progress === 100,
            'progress-error': validationErrors.length > 0,
          }"
          :aria-valuenow="progress"
          aria-valuemin="0"
          aria-valuemax="100"
          :aria-label="t('common.progress', { percent: progress })"
        ></div>
      </div>
      <div class="progress-text">
        <span class="progress-percentage">{{ progress }}%</span>
        <span class="progress-label">{{ t('common.complete') }}</span>
      </div>
    </div>

    <!-- Tab Indicators -->
    <div class="tab-indicators" role="tablist">
      <div
        v-for="(tab, index) in tabs"
        :key="tab.id"
        class="tab-indicator"
        :class="{
          active: currentTab === tab.id,
          completed: getTabStatus(tab) === 'completed',
          error: getTabStatus(tab) === 'error',
          initial: getTabStatus(tab) === 'initial',
          pending: getTabStatus(tab) === 'pending',
          accessible: isTabAccessible(tab, index),
          disabled: !isTabAccessible(tab, index),
          required: tab.required,
        }"
        :aria-disabled="!isTabAccessible(tab, index)"
        role="tab"
        :aria-selected="currentTab === tab.id"
        :tabindex="isTabAccessible(tab, index) ? 0 : -1"
        :aria-label="getTabAriaLabel(tab, index)"
        @click="handleTabClick(tab, index)"
        @keydown.enter="handleTabClick(tab, index)"
        @keydown.space.prevent="handleTabClick(tab, index)"
      >
        <div class="tab-number" :class="{ error: hasTabError(tab) }">
          <v-icon v-if="hasTabError(tab)" size="small" color="error" class="tab-icon"> mdi-alert-circle</v-icon>
          <v-icon v-else-if="getTabNumberContent(tab) === '✓'" size="small" color="success" class="tab-icon">
            mdi-check
          </v-icon>
          <v-icon v-else-if="getTabNumberContent(tab) === '*'" size="small" color="error" class="tab-icon">
            mdi-asterisk
          </v-icon>
          <v-icon v-else-if="getTabNumberContent(tab) === '⏳'" size="small" color="warning" class="tab-icon">
            mdi-clock
          </v-icon>
          <span v-else class="tab-number-text">{{ index + 1 }}</span>
        </div>
        <div class="tab-content">
          <div class="tab-label">
            <span class="tab-label-text">{{ t(tab.label) }}</span>
            <span v-if="tab.required" class="required-indicator">*</span>
          </div>
          <div class="tab-status">
            <v-icon :icon="getTabIcon(tab)" :color="getTabIconColor(tab)" size="small" class="status-icon" />
          </div>
        </div>
        <div v-if="tab.icon" class="tab-icon-container">
          <v-icon size="small" color="grey" class="tab-type-icon">
            {{ tab.icon }}
          </v-icon>
        </div>
      </div>
    </div>

    <!-- Navigation Buttons -->
    <div class="navigation-buttons mt-4">
      <v-btn
        v-if="!isFirstTab"
        variant="outlined"
        color="primary"
        :disabled="isSubmitting"
        class="me-2"
        :loading="isSubmitting"
        :aria-label="t('common.previous')"
        @click="$emit('previous')"
      >
        <v-icon start>{{ leftArrowIcon }}</v-icon>
        {{ t('common.previous') }}
      </v-btn>

      <v-btn
        v-if="!isLastTab"
        variant="elevated"
        color="primary"
        :disabled="!canProceed || isSubmitting"
        class="ms-auto"
        :loading="isSubmitting"
        :aria-label="t('common.next')"
        @click="$emit('next')"
      >
        {{ t('common.next') }}
        <v-icon end>{{ rightArrowIcon }}</v-icon>
      </v-btn>

      <v-btn
        v-if="isLastTab"
        variant="elevated"
        color="success"
        :disabled="!canProceed || isSubmitting"
        :loading="isSubmitting"
        class="ms-auto"
        :aria-label="t('common.submit')"
        @click="$emit('submit')"
      >
        <v-icon start>mdi-check</v-icon>
        {{ t('common.submit') }}
      </v-btn>
    </div>

    <!-- Error Summary -->
    <div v-if="validationErrors.length > 0" class="error-summary mt-4">
      <v-alert type="error" variant="tonal" class="mb-0 error-alert">
        <template #title>
          <div class="error-title">
            <span class="error-title-text">{{ t('common.validationErrors') }}</span>
            <span class="error-count">({{ validationErrors.length }})</span>
          </div>
        </template>
        <div class="error-chips-container">
          <v-chip
            v-for="error in validationErrors"
            :key="error.field"
            color="error"
            variant="elevated"
            size="small"
            class="error-chip me-2 mb-2"
            :title="error.message"
            clickable
            @click="handleFieldClick(error)"
          >
            <v-icon start size="small" color="error">mdi-alert-circle</v-icon>
            <span class="error-field">{{ getFieldDisplayName(error.field) }}</span>
            <v-icon end size="small" color="error" class="navigate-icon">{{ rightArrowIcon }}</v-icon>
            <v-tooltip activator="parent" location="top">
              <span>{{ error.message }}</span>
            </v-tooltip>
          </v-chip>
        </div>
      </v-alert>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { useI18n } from 'vue-i18n'
import { TabConfig, ValidationError } from '@/composables/useChallengeForm'
import { useLeftArrowIcon, useRightArrowIcon } from '@/utils/rtlUtils'

export default defineComponent({
  name: 'ChallengeProgressIndicator',
  props: {
    tabs: {
      type: Array as () => TabConfig[],
      required: true
    },
    currentTab: {
      type: String,
      required: true
    },
    progress: {
      type: Number,
      required: true
    },
    isSubmitting: {
      type: Boolean,
      default: false
    },
    canProceed: {
      type: Boolean,
      default: false
    },
    isFirstTab: {
      type: Boolean,
      required: true
    },
    isLastTab: {
      type: Boolean,
      required: true
    },
    validationErrors: {
      type: Array as () => ValidationError[],
      default: () => []
    },
    completedTabs: {
      type: Array as () => string[],
      default: () => []
    },
    pendingTabs: {
      type: Array as () => string[],
      default: () => []
    }
  },
  emits: ['next', 'previous', 'submit', 'tab-click', 'field-click'],
  setup(props, { emit }) {
    // Initialize i18n at the top level
    const { t } = useI18n()

    // RTL-aware arrow direction using utility functions
    const leftArrowIcon = useLeftArrowIcon()
    const rightArrowIcon = useRightArrowIcon()

    // Computed properties for tab state
    const isTabCompleted = (tab: TabConfig): boolean => {
      // A tab is completed if it's in the completedTabs array (meaning the user successfully navigated past it)
      return props.completedTabs.includes(tab.id)
    }

    const isTabPending = (tab: TabConfig): boolean => {
      // A tab is pending if it has mandatory data filled but is not completed
      return props.pendingTabs.includes(tab.id)
    }

    const isTabAccessible = (_tab: TabConfig, index: number): boolean => {
      const currentIndex = props.tabs.findIndex((t) => t.id === props.currentTab)

      // Always allow access to the current tab and previous tabs
      if (index <= currentIndex) return true

      // For future tabs, check if all previous required tabs are completed
      for (let i = 0; i < index; i++) {
        const previousTab = props.tabs[i]
        if (previousTab.required) {
          const isCompleted = props.completedTabs.includes(previousTab.id)
          const hasErrors = props.validationErrors.some((error) => error.tabId === previousTab.id)
          if (!isCompleted && hasErrors) return false
        }
      }

      return true
    }

    const hasTabError = (tab: TabConfig): boolean => {
      return props.validationErrors.some((error) => error.tabId === tab.id)
    }

    const getTabStatus = (tab: TabConfig): 'completed' | 'error' | 'pending' | 'initial' => {
      if (isTabCompleted(tab)) return 'completed'
      if (hasTabError(tab)) return 'error'
      if (isTabPending(tab)) return 'pending'
      if (tab.required) return 'initial' // Mandatory tab that hasn't been filled yet
      return 'pending' // Non-mandatory tab
    }

    const getTabIcon = (tab: TabConfig): string => {
      const status = getTabStatus(tab)
      switch (status) {
        case 'completed':
          return 'mdi-check-circle'
        case 'error':
          return 'mdi-alert-circle'
        case 'pending':
          return 'mdi-clock-outline'
        case 'initial':
          return 'mdi-asterisk'
        default:
          return 'mdi-circle-outline'
      }
    }

    const getTabIconColor = (tab: TabConfig): string => {
      const status = getTabStatus(tab)
      switch (status) {
        case 'completed':
          return 'success'
        case 'error':
          return 'error'
        case 'pending':
          return 'warning'
        case 'initial':
          return 'error'
        default:
          return 'grey'
      }
    }

    const getTabNumberContent = (tab: TabConfig): string | null => {
      const status = getTabStatus(tab)
      switch (status) {
        case 'completed':
          return '✓'
        case 'error':
          return '!'
        case 'pending':
          return '⏳'
        case 'initial':
          return '*'
        default:
          return null // Show number
      }
    }

    const getTabAriaLabel = (tab: TabConfig, index: number): string => {
      const baseLabel = t(tab.label)
      const status = getTabStatus(tab)
      const accessible = isTabAccessible(tab, index) ? 'accessible' : 'not accessible'
      return `${baseLabel}, ${status}, ${accessible}`
    }

    const handleTabClick = (tab: TabConfig, index: number) => {
      if (isTabAccessible(tab, index)) {
        emit('tab-click', tab.id)
      }
    }

    const handleFieldClick = (error: ValidationError) => {
      // First, navigate to the tab that contains this field if not already there
      if (error.tabId && error.tabId !== props.currentTab) {
        emit('tab-click', error.tabId)
      }

      // Then emit the field-click event for the specific field
      emit('field-click', error.field)
    }

    const getFieldDisplayName = (field: string): string => {
      // Map field keys to their proper display names
      const fieldNameMap: Record<string, string> = {
        // Challenge Info fields
        titleEn: t('challenge.info.titleEn'),
        titleAr: t('challenge.info.titleAr'),
        descriptionEn: t('challenge.info.descriptionEn'),
        descriptionAr: t('challenge.info.descriptionAr'),
        startTime: t('challenge.info.startTime'),
        endTime: t('challenge.info.endTime'),
        visibility: t('challenge.info.visibility'),
        moderatorId: t('challenge.info.moderator'),
        allowImmediateAccess: t('challenge.info.allowImmediateAccess'),

        // Team and Validator fields
        selectedUsers: t('challenge.info.teamMember'),
        selectedValidatorUsers: t('challenge.info.validators'),
        selectedDepartment: t('challenge.viewInfo.department'),

        // Common field names
        field: t('challenge.info.field'),
        impact: t('challenge.info.impact'),
        sector: t('challenge.info.sector'),
        department: t('challenge.info.department'),
        committee: t('challenge.info.committee'),
        moderator: t('challenge.info.moderator'),
        assistantModerator: t('challenge.info.assistantModerator'),
        topicClassification: t('challenge.info.topicClassification'),
        levelOfDecision: t('challenge.info.levelOfDecision'),
        impactOfDecision: t('challenge.info.impactOfDecision'),
        strategicDirection: t('challenge.info.strategicDirection'),
        strategicGoal: t('challenge.info.strategicGoal'),
        futureStrategicDirection: t('challenge.info.futureStrategicDirection'),
        futureStrategicGoal: t('challenge.info.futureStrategicGoal'),
        solutionsRateList: t('challenge.info.solutionsRateList'),
        challengeSettings: t('challenge.info.challengeSettings'),
        resources: t('challenge.info.resources'),
        docsResources: t('challenge.info.docsResources'),
        youtubeResources: t('challenge.info.youtubeResources'),
        reminder: t('challenge.info.reminder'),
        hashTag: t('challenge.info.hashTag'),
        isUrgent: t('challenge.info.isUrgent')
      }

      // Return a mapped name or fallback to a field key with proper formatting
      return fieldNameMap[field] || field.replace(/([A-Z])/g, ' $1').replace(/^./, (str) => str.toUpperCase())
    }

    return {
      t,
      isTabCompleted,
      isTabPending,
      isTabAccessible,
      hasTabError,
      getTabStatus,
      getTabIcon,
      getTabIconColor,
      getTabNumberContent,
      getTabAriaLabel,
      handleTabClick,
      handleFieldClick,
      getFieldDisplayName,
      leftArrowIcon,
      rightArrowIcon
    }
  }
})
</script>

<style lang="scss" scoped>
.challenge-progress-container {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;

  .progress-bar-container {
    .progress-bar {
      width: 100%;
      height: 18px;
      background: #e0e0e0;
      border-radius: 12px;
      overflow: hidden;
      position: relative;

      .progress-fill {
        height: 100%;
        background: linear-gradient(90deg, #4caf50, #8bc34a);
        border-radius: 12px;
        transition: width 0.3s ease,
        background 0.3s ease;

        &.progress-complete {
          background: linear-gradient(90deg, #4caf50, #2e7d32);
        }

        &.progress-error {
          background: linear-gradient(90deg, #f44336, #ff5722);
        }
      }
    }

    .progress-text {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 8px;

      .progress-percentage {
        font-weight: 600;
        color: #333;
        font-size: 14px;
      }

      .progress-label {
        color: #666;
        font-size: 12px;
      }
    }
  }

  .tab-indicators {
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
    margin-top: 24px;

    .tab-indicator {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 16px;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s ease;
      border: 2px solid transparent;
      min-width: 200px;
      background: #f8f9fa;

      &:hover {
        background: #e9ecef;
        transform: translateY(-1px);
      }

      &.active {
        border-color: #2196f3;
        background: #e3f2fd;
        box-shadow: 0 2px 8px rgba(33, 150, 243, 0.2);
      }

      &.completed {
        border-color: #4caf50;
        background: #e8f5e8;
      }

      &.error {
        border-color: #f44336;
        background: #ffebee;
        box-shadow: 0 0 0 2px #f4433633;
        animation: shake 0.3s cubic-bezier(0.36, 0.07, 0.19, 0.97);
      }

      @keyframes shake {
        0% {
          transform: translateX(0);
        }
        20% {
          transform: translateX(-6px);
        }
        40% {
          transform: translateX(6px);
        }
        60% {
          transform: translateX(-6px);
        }
        80% {
          transform: translateX(6px);
        }
        100% {
          transform: translateX(0);
        }
      }

      &.disabled {
        opacity: 0.5;
        cursor: not-allowed;
        background: #f5f5f5;

        &:hover {
          transform: none;
          background: #f5f5f5;
        }
      }

      &.required .tab-label::after {
        content: '';
        color: transparent;
        font-weight: bold;
      }

      &.pending {
        border-color: #ff9800;
        background: #fff3e0;
      }

      &.initial {
        border-color: #f44336;
        background: #ffebee;
      }

      .tab-number {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: #e0e0e0;
        color: #666;
        font-weight: 600;
        font-size: 14px;

        &.error {
          background: #ffebee;
          color: #f44336;
        }

        .tab-icon {
          font-size: 16px;
        }
      }

      .tab-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 4px;
        justify-content: center;

        .tab-label {
          align-items: flex-start;
          font-weight: 500;
          color: #333;
          display: inline-flex;
          font-size: 14px;
          gap: 6px;
          line-height: 1.2;

          .tab-label-text {
            flex: 1 1 auto;
            min-width: 0;
          }

          .required-indicator {
            color: #d32f2f;
            flex: 0 0 auto;
            font-weight: 700;
            line-height: 1;
            margin-top: 1px;
          }
        }

        .tab-status {
          display: flex;
          align-items: center;
          gap: 4px;
          min-height: 18px;

          .status-icon {
            font-size: 12px;
          }
        }
      }

      .tab-icon-container {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 24px;
        height: 24px;
        border-radius: 4px;
        background: #f0f0f0;

        .tab-type-icon {
          font-size: 14px;
        }
      }
    }
  }

  .navigation-buttons {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;

    .v-btn {
      align-items: center;
      display: inline-flex;
      min-width: 120px;
      font-weight: 500;

      :deep(.v-btn__content) {
        align-items: center;
        display: inline-flex;
        gap: 8px;
      }
    }
  }

  .error-summary {
    .error-alert {
      border-left: 4px solid #d32f2f;

      .error-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 600;
        color: #d32f2f;

        .error-title-text {
          font-size: 16px;
          font-weight: 700;
        }

        .error-count {
          background: #d32f2f;
          color: white;
          padding: 2px 8px;
          border-radius: 12px;
          font-size: 12px;
          font-weight: 600;
          margin-left: 4px;
        }
      }
    }

    .error-chips-container {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      align-items: center;

      .error-chip {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        border-radius: 20px;
        background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
        color: #d32f2f;
        font-size: 13px;
        font-weight: 500;
        border: 1px solid #ffcdd2;
        transition: all 0.2s ease;
        cursor: pointer;
        box-shadow: 0 2px 4px rgba(244, 67, 54, 0.1);
        position: relative;
        overflow: hidden;

        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
          transition: left 0.5s ease;
        }

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 12px rgba(244, 67, 54, 0.25);
          background: linear-gradient(135deg, #ffcdd2 0%, #ef9a9a 100%);
          border-color: #ef9a9a;

          &::before {
            left: 100%;
          }

          .navigate-icon {
            transform: translateX(2px);
          }
        }

        &:active {
          transform: translateY(0);
          box-shadow: 0 2px 4px rgba(244, 67, 54, 0.2);
        }

        &:focus {
          outline: 2px solid #d32f2f;
          outline-offset: 2px;
        }

        &:focus-visible {
          outline: 2px solid #d32f2f;
          outline-offset: 2px;
        }

        .error-field {
          color: #d32f2f;
          font-weight: 600;
          text-transform: capitalize;
          white-space: nowrap;
        }

        .navigate-icon {
          transition: transform 0.2s ease;
          font-size: 12px;
        }

        .v-icon {
          font-size: 14px;
        }
      }
    }
  }
}

@media (min-width: 1024px) {
  .challenge-progress-container {
    .tab-indicators {
      flex-wrap: nowrap;
      gap: 10px;
      overflow-x: hidden;

      .tab-indicator {
        flex: 1 1 0;
        gap: 8px;
        min-width: 0;
        padding: 10px 12px;

        .tab-number {
          flex: 0 0 28px;
          height: 28px;
          width: 28px;

          .tab-icon {
            font-size: 14px;
          }
        }

        .tab-content {
          gap: 2px;
          min-width: 0;

          .tab-label {
            font-size: 12px;
            gap: 4px;
            line-height: 1.3;
            overflow: visible;
            text-overflow: unset;
            white-space: normal;
            word-break: break-word;
          }

          .tab-status {
            min-height: 14px;

            .status-icon {
              font-size: 11px;
            }
          }
        }

        .tab-icon-container {
          flex: 0 0 20px;
          height: 20px;
          width: 20px;

          .tab-type-icon {
            font-size: 12px;
          }
        }
      }
    }
  }
}

// Responsive design
@media (max-width: 768px) {
  .challenge-progress-container {
    padding: 16px;

    .tab-indicators {
      flex-direction: column;
      gap: 8px;

      .tab-indicator {
        min-width: auto;
        width: 100%;
      }
    }

    .navigation-buttons {
      flex-direction: column;
      gap: 8px;

      .v-btn {
        width: 100%;
      }
    }

    .error-summary {
      .error-alert {
        .error-title {
          flex-direction: column;
          align-items: flex-start;
          gap: 4px;

          .error-count {
            align-self: flex-start;
          }
        }
      }

      .error-chips-container {
        gap: 6px;

        .error-chip {
          padding: 8px 12px;
          font-size: 12px;
          min-width: 120px;
          justify-content: center;

          .error-field {
            font-size: 12px;
          }

          .navigate-icon {
            font-size: 10px;
          }
        }
      }
    }
  }
}

// Dark theme support
@media (prefers-color-scheme: dark) {
  .challenge-progress-container {
    background: #1e1e1e;
    color: #fff;

    .progress-bar {
      background: #333;
    }

    .tab-indicator {
      background: #2d2d2d;
      color: #fff;

      &:hover {
        background: #3d3d3d;
      }

      &.active {
        background: #1a237e;
      }

      &.completed {
        background: #1b5e20;
      }

      &.error {
        background: #b71c1c;
      }

      .tab-number {
        background: #444;
        color: #ccc;
      }

      .tab-content .tab-label {
        color: #fff;
      }
    }
  }
}

// High contrast mode support
@media (prefers-contrast: high) {
  .challenge-progress-container {
    border: 2px solid #000;

    .tab-indicator {
      border-width: 3px;
    }
  }
}

// Reduced motion support
@media (prefers-reduced-motion: reduce) {
  .challenge-progress-container {
    .progress-fill,
    .tab-indicator {
      transition: none;
    }
  }
}
</style>
