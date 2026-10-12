<template>
  <v-text-field
    v-model="dateTimeFormatted"
    :label="label"
    variant="outlined"
    density="compact"
    :active="isMenuOpen"
    append-inner-icon="mdi-calendar-clock"
    readonly
    :clearable="clearable"
    :class="{ 'shake-error': hasError, 'field-error': hasError }"
    :rules="rules"
    :required="required"
    @click:clear="clearDateTime"
  >
    <v-menu
      v-model="isMenuOpen"
      offset-y
      min-width="350px"
      :close-on-content-click="false"
      :nudge-right="40"
      activator="parent"
    >
      <v-card class="datetime-picker-card">
        <v-toolbar :dir="locale === 'ar' ? 'rtl' : 'ltr'" color="primary" class="datetime-toolbar">
          <v-tabs v-model="tab" :dir="locale === 'ar' ? 'rtl' : 'ltr'">
            <v-tab value="date" class="datetime-tab">
              <v-icon class="me-2">mdi-calendar</v-icon>
              <span>{{ $t('common.date') }}</span>
            </v-tab>
            <v-tab value="time" class="datetime-tab">
              <v-icon class="me-2">mdi-clock</v-icon>
              <span>{{ $t('common.time') }}</span>
            </v-tab>
          </v-tabs>
        </v-toolbar>
        <v-card-text class="datetime-content">
          <v-window v-model="tab" :dir="locale === 'ar' ? 'rtl' : 'ltr'">
            <v-window-item value="date">
              <v-date-picker
                full-width
                hide-header
                color="primary"
                :locale="locale"
                :disabled="readonly"
                :min="minDate"
                :max="maxDate"
                @update:model-value="onDateSelect"
              ></v-date-picker>
            </v-window-item>
            <v-window-item value="time">
              <v-time-picker
                color="primary"
                hide-header
                format="24hr"
                :readonly="readonly"
                :min="minTime"
                :max="maxTime"
                @update:model-value="onTimeSelect"
              ></v-time-picker>
            </v-window-item>
          </v-window>
        </v-card-text>
        <v-card-actions class="datetime-actions">
          <v-spacer></v-spacer>
          <v-btn color="primary" variant="text" :disabled="!tempDate || !tempTime" @click="confirmSelection">
            {{ $t('common.confirm') }}
          </v-btn>
          <v-btn color="secondary" variant="text" @click="cancelSelection">
            {{ $t('common.cancel') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-menu>
  </v-text-field>
</template>

<script>
import { computed, defineComponent, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import moment from 'moment'
import { sdssDateFormat } from '@/utils/dateUtils'

export default defineComponent({
  name: 'DPDateTimePicker',
  props: {
    value: {
      type: String,
      default: ''
    },
    modelValue: {
      type: String,
      default: ''
    },
    readonly: {
      type: Boolean,
      default: false
    },
    persistent: {
      type: Boolean,
      default: false
    },
    dateFormat: {
      type: String,
      default: 'DD/MM/YYYY'
    },
    timeFormat: {
      type: String,
      default: 'HH:mm'
    },
    locale: {
      type: String,
      default: 'en'
    },
    label: {
      type: String,
      default: ''
    },
    clearable: {
      type: Boolean,
      default: true
    },
    required: {
      type: Boolean,
      default: false
    },
    rules: {
      type: Array,
      default: () => []
    },
    hasError: {
      type: Boolean,
      default: false
    },
    minDate: {
      type: String,
      default: null
    },
    maxDate: {
      type: String,
      default: null
    },
    minTime: {
      type: String,
      default: null
    },
    maxTime: {
      type: String,
      default: null
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const { locale: currentLocale } = useI18n()
    const isMenuOpen = ref(false)
    const date = ref(null)
    const time = ref(null)
    const tab = ref('date')
    const tempDate = ref(null)
    const tempTime = ref(null)

    // Set moment locale based on current app locale
    moment.locale(currentLocale.value)

    watch(
      () => props.modelValue,
      (newValue) => {
        if (newValue) {
          // Handle both ISO format and yyyy-MM-dd HH:mm:ss format
          let parsedDateTime
          if (newValue.includes('T')) {
            // ISO format
            parsedDateTime = moment(newValue)
          } else {
            // yyyy-MM-dd HH:mm:ss format
            parsedDateTime = moment(newValue, 'YYYY-MM-DD HH:mm:ss')
          }

          if (parsedDateTime.isValid()) {
            date.value = parsedDateTime.format(props.dateFormat)
            time.value = parsedDateTime.format(props.timeFormat)
            tempDate.value = parsedDateTime.format(props.dateFormat)
            tempTime.value = parsedDateTime.format(props.timeFormat)
          }
        } else {
          date.value = ''
          time.value = ''
          tempDate.value = ''
          tempTime.value = ''
        }
      },
      { immediate: true }
    )

    const dateTimeFormatted = computed(() => {
      if (date.value && time.value) {
        return `${date.value} ${time.value}`
      }
      return ''
    })

    const onDateSelect = (value) => {
      if (value) {
        const momentDate = moment(value)
        tempDate.value = momentDate.format(props.dateFormat)
        date.value = tempDate.value
        tab.value = 'time'
        if (tempTime.value) {
          const combinedDateTime = moment(
            `${tempDate.value} ${tempTime.value}`,
            `${props.dateFormat} ${props.timeFormat}`
          )
          if (combinedDateTime.isValid()) {
            emit('update:modelValue', sdssDateFormat(combinedDateTime.toDate()))
          }
        }
      }
    }

    const onTimeSelect = (value) => {
      if (value) {
        const momentTime = moment(value, props.timeFormat)
        tempTime.value = momentTime.format(props.timeFormat)
        time.value = tempTime.value
        if (tempDate.value) {
          const combinedDateTime = moment(
            `${tempDate.value} ${tempTime.value}`,
            `${props.dateFormat} ${props.timeFormat}`
          )
          if (combinedDateTime.isValid()) {
            emit('update:modelValue', sdssDateFormat(combinedDateTime.toDate()))
          }
        }
      }
      isMenuOpen.value = false
    }

    const confirmSelection = () => {
      if (tempDate.value && tempTime.value) {
        date.value = tempDate.value
        time.value = tempTime.value

        const combinedDateTime = moment(
          `${tempDate.value} ${tempTime.value}`,
          `${props.dateFormat} ${props.timeFormat}`
        )
        if (combinedDateTime.isValid()) {
          // Format as yyyy-MM-dd HH:mm:ss for backend compatibility
          emit('update:modelValue', sdssDateFormat(combinedDateTime.toDate()))
        }
      }
      isMenuOpen.value = false
    }

    const cancelSelection = () => {
      tempDate.value = date.value
      tempTime.value = time.value
      isMenuOpen.value = false
    }

    const clearDateTime = () => {
      date.value = null
      time.value = null
      tempDate.value = null
      tempTime.value = null
      emit('update:modelValue', '')
    }

    return {
      isMenuOpen,
      date,
      time,
      tempDate,
      tempTime,
      tab,
      dateTimeFormatted,
      onDateSelect,
      onTimeSelect,
      confirmSelection,
      cancelSelection,
      clearDateTime
    }
  }
})
</script>

<style scoped lang="scss">
.datetime-picker-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.datetime-toolbar {
  .datetime-tab {
    min-width: 120px;
    font-weight: 500;

    .v-icon {
      font-size: 18px;
    }
  }
}

.datetime-content {
  padding: 0;
  min-height: 320px;

  .v-window {
    height: 320px;
  }
}

.datetime-actions {
  padding: 12px 16px;
  border-top: 1px solid #e0e0e0;
  background: #fafafa;
}

// RTL support
[dir='rtl'] {
  .datetime-tab {
    .v-icon {
      margin-left: 8px;
      margin-right: 0;
    }
  }
}

// Responsive design
@media (max-width: 600px) {
  .datetime-picker-card {
    min-width: 300px;
  }

  .datetime-toolbar {
    .datetime-tab {
      min-width: 100px;
      font-size: 14px;

      .v-icon {
        font-size: 16px;
      }
    }
  }

  .datetime-content {
    min-height: 280px;

    .v-window {
      height: 280px;
    }
  }
}

// Error animation
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

/* Animations */
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
.v-text-field {
  transition: all 0.3s ease;
}
</style>
