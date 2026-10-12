<template>
  <v-container :style="containerStyles" fill-height>
    <v-row justify="center" align="center" class="text-center h-100">
      <v-col cols="12" md="8">
        <!-- Coming Soon GIF -->
        <v-img :src="previewImageUrl" alt="Coming Soon" max-width="300" contain class="mx-auto coming-soon-image" />

        <!-- Title -->
        <v-card-title class="text-h4 font-weight-bold mt-4 custom-coming-soon-main-title-style">
          {{ $t('common.comingSoon') }}
        </v-card-title>

        <!-- Subtitle -->
        <v-card-subtitle class="text-subtitle-1 grey--text">
          {{ $t('common.comingSoonText') }}
        </v-card-subtitle>

        <v-col cols="12"></v-col>
        <CustomGoBackButton
          v-show="!hideBackButton"
          v-if="!hideBackButton"
          :is-arabic="isLanguageAr()"
        ></CustomGoBackButton>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { defineComponent, watchEffect } from 'vue'
import CustomGoBackButton from '@/components/custom/CustomGoBackButton.vue'
import { Fields } from '@/constants/DpConstants.ts'
import { useI18n } from 'vue-i18n'

export default defineComponent({
  name: 'CustomComingSoon',
  components: { CustomGoBackButton },
  props: {
    height: {
      type: String,
      default: '80vh' // Default value if no height is provided
    },
    backgroundColor: {
      type: String,
      default: '#f5f5f5' // Default background color if no background is provided
    },
    hideBackButton: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const { locale } = useI18n()
    const employeeLang: string = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'
    const previewImageUrl = new URL('@/assets/images/custom/coming-soon.gif', import.meta.url).href

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    const isLanguageAr = (): boolean => {
      return employeeLang === 'ar'
    }

    const handleButtonClick = () => {
      alert('You will be notified about updates!')
    }

    // Apply styles directly instead of using dynamic class binding
    const containerStyles = {
      backgroundColor: props.backgroundColor,
      height: props.height
    }

    return {
      isLanguageAr,
      previewImageUrl,
      handleButtonClick,
      containerStyles
    }
  }
})
</script>

<style scoped lang="scss">
.coming-soon-container {
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.mx-auto {
  margin-left: auto !important;
  margin-right: auto !important;
}

.coming-soon-image {
  animation: fadeIn 2s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.text-center {
  text-align: center;
}
</style>
