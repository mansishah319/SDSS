<template>
  <v-btn
    :prepend-icon="backIcon"
    :append-icon="computedIcon"
    :color="color"
    :variant="variant"
    class="custom-back-button-style"
    @click="handleBack"
  >
    {{ $t(title) }}
  </v-btn>
</template>

<script lang="ts">
import { useRouter } from 'vue-router'
import { computed } from 'vue'
import { useLeftArrowIcon } from '@/utils/rtlUtils'

export default {
  name: 'BackButton',
  props: {
    redirectPath: {
      type: String,
      default: 'home'
    },
    icon: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: 'common.back'
    },
    color: {
      type: String,
      default: 'primary'
    },
    variant: {
      type: String,
      default: 'flat'
    }
  },
  setup(props) {
    const router = useRouter()
    const leftArrowIcon = useLeftArrowIcon()

    const handleBack = () => {
      router.push({ name: props.redirectPath })
    }

    const computedIcon = computed(() => {
      if (props.icon) return props.icon
      return 'mdi mdi-home-circle'
    })

    const backIcon = computed(() => {
      return leftArrowIcon.value
    })

    return { handleBack, computedIcon, backIcon }
  }
}
</script>

<style lang="scss">
@use '@/assets/css/variables' as *;

.custom-back-button-style {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  font-size: 16px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  background-color: #0da06e !important;

  .v-btn__content {
    font-family: BukraRegular, serif !important;
  }

  &.bg-primary {
    background-color: var(--primary-color) !important;
  }
}

.custom-back-button-style:hover {
  background-color: #0056b3;
}

.custom-back-button-style:active {
  background-color: #004085;
}

.custom-back-button-style:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(38, 143, 255, 0.5);
}
</style>
