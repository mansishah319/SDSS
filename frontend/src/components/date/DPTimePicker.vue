<template>
  <v-text-field
    v-model="timeFormatted"
    :label="label"
    filled
    :active="isMenuOpen"
    append-inner-icon="mdi-calendar"
    readonly
  >
    <v-menu
      v-model="isMenuOpen"
      offset-y
      min-width="320px"
      :close-on-content-click="false"
      :nudge-right="40"
      activator="parent"
    >
      <v-time-picker
        color="white"
        hide-header
        format="24hr"
        :readonly="readonly"
        @update:model-value="onTimeSelect"
      ></v-time-picker>
    </v-menu>
  </v-text-field>
</template>

<script>
import { defineComponent, ref, watch } from 'vue'
import moment from 'moment'

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
    timeFormat: {
      type: String,
      default: 'HH:mm'
    },
    locale: {
      type: String,
      default: 'en-US'
    },
    label: {
      type: String,
      default: ''
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const isMenuOpen = ref(false)
    const time = ref(null)
    const timeFormatted = ref('')

    const removeSeconds = (time) => {
      if (time.includes(':')) {
        const parts = time.split(':')
        if (parts.length === 3) {
          return `${parts[0]}:${parts[1]}`
        } else if (parts.length === 2) {
          return time
        }
      }
      return time
    }

    watch(
      () => props.modelValue,
      (newValue) => {
        if (newValue) {
          const parsedTime = moment(removeSeconds(newValue), props.timeFormat, true)
          if (parsedTime.isValid()) {
            timeFormatted.value = parsedTime.format(props.timeFormat)
          } else {
            timeFormatted.value = ''
          }
        } else {
          timeFormatted.value = ''
        }
      },
      { immediate: true }
    )

    const onTimeSelect = (value) => {
      if (value) {
        const momentTime = moment(value, props.timeFormat)
        time.value = momentTime.format(props.timeFormat)
        timeFormatted.value = time.value
        emit('update:modelValue', timeFormatted.value)
      }

      isMenuOpen.value = false
    }

    return {
      isMenuOpen,
      timeFormatted,
      onTimeSelect
    }
  }
})
</script>
