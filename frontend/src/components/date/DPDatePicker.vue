<template>
  <v-text-field
    v-model="dateFormatted"
    :label="label"
    variant="outlined"
    density="compact"
    :active="isMenuOpen"
    append-inner-icon="mdi-calendar"
    readonly
    :clearable="clearable"
    @click:clear="clearDate"
  >
    <v-menu v-model="isMenuOpen" offset-y :close-on-content-click="false" activator="parent">
      <v-date-picker
        full-width
        hide-header
        color="primary"
        :locale="locale"
        :disabled="readonly"
        @update:model-value="onDateSelect"
      ></v-date-picker>
    </v-menu>
  </v-text-field>
</template>

<script>
import { computed, defineComponent, ref, watch } from 'vue'
import moment from 'moment'

export default defineComponent({
  name: 'DPDatePicker',
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
    locale: {
      type: String,
      default: 'en-US'
    },
    label: {
      type: String,
      default: ''
    },
    clearable: {
      type: Boolean,
      default: true
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const isMenuOpen = ref(false)
    const date = ref(props.modelValue ? moment(props.modelValue).format(props.dateFormat) : null)

    watch(
      () => props.modelValue,
      (newValue) => {
        if (newValue) {
          const parsedDate = moment(newValue, props.dateFormat, true)
          date.value = parsedDate.isValid() ? parsedDate.toDate() : null
        } else {
          date.value = ''
        }
      },
      { immediate: true }
    )

    const dateFormatted = computed(() => {
      if (date.value) {
        return moment(date.value).format(props.dateFormat)
      } else {
        return ''
      }
    })

    const onDateSelect = (value) => {
      if (value) {
        date.value = value
        const formattedDate = moment(value).format(props.dateFormat)
        emit('update:modelValue', formattedDate)
      }

      isMenuOpen.value = false
    }

    const clearDate = () => {
      date.value = null
      emit('update:modelValue', '')
    }

    return {
      isMenuOpen,
      onDateSelect,
      dateFormatted,
      clearDate // Add this to handle clearing the date
    }
  }
})
</script>
