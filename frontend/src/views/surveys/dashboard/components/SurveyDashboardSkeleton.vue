<template>
  <ContentLoader
    variant="analytics"
    :heading="$t('common.loader.surveyDashboardHeading')"
    :message="$t('common.loader.surveyAnalytics')"
    :description="$t('common.loader.surveyAnalyticsDescription')"
  />
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import ContentLoader from '@/components/loader/ContentLoader.vue'

export default defineComponent({
  name: 'SurveyDashboardSkeleton',
  components: {
    ContentLoader
  }
})
</script>

<style scoped lang="scss"></style>
