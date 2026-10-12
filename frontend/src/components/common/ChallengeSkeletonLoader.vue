<template>
  <ContentLoader
    variant="form"
    :heading="$t('common.loader.challengeWorkspaceHeading')"
    :message="$t('common.loader.challengeSetup')"
    :description="$t('common.loader.challengeSetupDescription')"
  />
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import ContentLoader from '@/components/loader/ContentLoader.vue'

export default defineComponent({
  name: 'ChallengeSkeletonLoader',
  components: {
    ContentLoader
  },
  props: {
    // Optional props for customization
    showHeader: {
      type: Boolean,
      default: true
    },
    showProgress: {
      type: Boolean,
      default: true
    },
    fieldCount: {
      type: Number,
      default: 5
    }
  }
})
</script>

<style lang="scss" scoped></style>
