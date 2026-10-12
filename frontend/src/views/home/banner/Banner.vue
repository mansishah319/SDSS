<script lang="ts">
import { computed, PropType, ref, StyleValue } from 'vue'
import { useI18n } from 'vue-i18n'
import { translate } from '@/utils/TranslatorHelper'

type BannerHomeType = 'main' | 'sdss' | 'survey'

const bannerLogoMap: Record<BannerHomeType, string> = {
  main: new URL('@/assets/images/logo/sdss-logo.png', import.meta.url).href,
  sdss: new URL('@/assets/images/logo/decision-support-system.png', import.meta.url).href,
  survey: new URL('@/assets/images/logo/online-survey.png', import.meta.url).href
}

export default {
  name: 'BannerView',
  props: {
    bannerImage: {
      type: String,
      required: true
    },
    bannerImageStyle: {
      type: [Object, Array, String] as PropType<StyleValue>,
      default: () => ({})
    },
    bannerImageClass: {
      type: String,
      default: ''
    },
    homeType: {
      type: String as PropType<BannerHomeType>,
      default: 'main'
    },
    homeMainTitleEn: {
      type: String,
      default: translate('common.masterHomeMainTitleEn')
    },
    homeMainTitleAr: {
      type: String,
      default: translate('common.masterHomeMainTitleAr')
    },
    showHideButton: {
      type: Boolean,
      default: true
    },
    autoHide: {
      type: Boolean,
      default: false
    },
    autoHideDelay: {
      type: Number,
      default: 5000
    }
  },
  setup(props) {
    const { locale, t } = useI18n()
    const showBannerBlock = ref<boolean>(true)
    const isVisible = ref<boolean>(true)

    // Computed properties for responsive behavior
    const isMobile = computed(() => {
      return window.innerWidth <= 768
    })

    const isTablet = computed(() => {
      return window.innerWidth > 768 && window.innerWidth <= 1200
    })

    const bannerLogo = computed(() => {
      return bannerLogoMap[props.homeType]
    })

    const bannerLogoAlt = computed(() => {
      switch (props.homeType) {
        case 'sdss':
          return 'sdss-banner-logo'
        case 'survey':
          return 'survey-banner-logo'
        default:
          return 'main-banner-logo'
      }
    })

    // Auto-hide functionality
    if (props.autoHide) {
      setTimeout(() => {
        showBannerBlock.value = false
      }, props.autoHideDelay)
    }

    const toggleBanner = () => {
      showBannerBlock.value = !showBannerBlock.value
      isVisible.value = showBannerBlock.value
    }

    const showBanner = () => {
      showBannerBlock.value = true
      isVisible.value = true
    }

    const hideBanner = () => {
      showBannerBlock.value = false
      isVisible.value = false
    }

    return {
      showBannerBlock,
      isVisible,
      isMobile,
      isTablet,
      bannerLogo,
      bannerLogoAlt,
      locale,
      t,
      toggleBanner,
      showBanner,
      hideBanner
    }
  }
}
</script>

<template>
  <header
    v-if="isVisible"
    class="application-banner-div"
    :class="{
      'hide-banner': !showBannerBlock,
      'banner-show': showBannerBlock,
      'mobile-layout': isMobile,
      'tablet-layout': isTablet,
    }"
    :aria-label="t('common.banner')"
  >
    <img class="bg-img" :src="bannerImage" alt="banner-img" :style="bannerImageStyle" :class="bannerImageClass" />
    <div class="banner-bottom-bg" />

    <div class="application-banner-content">
      <div class="left">
        <p class="banner-txt">
          {{ t('common.platform') }}
          <span class="line-style">
            <span />
            <span />
            <span />
          </span>
        </p>
        <h3 class="app-name-ar">
          {{ homeMainTitleAr }}
        </h3>
        <p class="app-name-en">
          {{ homeMainTitleEn }}
        </p>
      </div>

      <div class="center">
        <div class="application-logo-div">
          <img :src="bannerLogo" :alt="bannerLogoAlt" loading="lazy" />
        </div>
      </div>

      <div v-if="showHideButton" class="right">
        <button
          :id="showBannerBlock ? 'hide-banner' : 'show-banner'"
          class="hide-btn"
          type="button"
          :aria-label="showBannerBlock ? t('common.hide') : t('common.show')"
          :aria-expanded="showBannerBlock"
          @click="toggleBanner"
        >
          {{ showBannerBlock ? t('common.hide') : t('common.show') }}
        </button>
      </div>
    </div>
  </header>

  <header
    v-else
    class="application-banner-div collapsed-banner"
    :class="{ 'mobile-layout': isMobile, 'tablet-layout': isTablet }"
    :aria-label="t('common.banner')"
  >
    <div class="application-banner-content">
      <div class="left">
        <p class="banner-txt">
          {{ t('common.platform') }}
          <span class="line-style">
            <span />
            <span />
            <span />
          </span>
        </p>
        <h3 class="app-name-ar">
          {{ homeMainTitleAr }}
        </h3>
        <p class="app-name-en">
          {{ homeMainTitleEn }}
        </p>
      </div>

      <div class="center">
        <div class="application-logo-div">
          <img :src="bannerLogo" :alt="bannerLogoAlt" loading="lazy" />
        </div>
      </div>

      <div v-if="showHideButton" class="right">
        <button
          id="show-banner"
          class="show-btn"
          type="button"
          :aria-label="t('common.show')"
          aria-expanded="false"
          @click="showBanner"
        >
          {{ t('common.show') }}
        </button>
      </div>
    </div>
  </header>
</template>

<style src="./Banner.scss" lang="scss" scoped></style>
