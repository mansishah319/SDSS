<template>
  <FooterComponent :is-app="isApp" :justify-content="justifyContent" class="pdf-report-footer" />
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import FooterComponent from '@/components/common/FooterComponent.vue'

export default defineComponent({
  name: 'FooterPdfReportComponent',
  components: { FooterComponent },
  props: {
    isApp: {
      type: Boolean,
      default: false
    },
    justifyContent: {
      type: String,
      default: 'justify-content-end'
    }
  }
})
</script>

<style scoped lang="scss">
.pdf-report-footer {
  break-inside: avoid;
  page-break-inside: avoid;
}
</style>
