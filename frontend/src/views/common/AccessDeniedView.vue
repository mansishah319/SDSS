<template>
  <AccessDeniedCommonPage
    :image-size="imageSize"
    :back-redirect-path="backRedirectPath"
    :back-icon="backIcon"
    :back-title="backTitle"
    :back-color="backColor"
  ></AccessDeniedCommonPage>
</template>

<script lang="ts">
import AccessDeniedCommonPage from '@/components/common/AccessDeniedCommonPage.vue'

export default {
  name: 'AccessDeniedPage',
  components: { AccessDeniedCommonPage },
  props: {
    imageSize: {
      type: [Number, String],
      default: 500
    },
    backRedirectPath: {
      type: String,
      default: 'home'
    },
    backIcon: {
      type: String,
      default: ''
    },
    backTitle: {
      type: String,
      default: 'common.back'
    },
    backColor: {
      type: String,
      default: 'primary'
    }
  },
  setup() {
    return {}
  }
}
</script>
