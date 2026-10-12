<template>
  <v-footer :app="isApp" :class="['custom-main-footer-style', justifyContent]">
    <v-container fluid class="custom-footer-container">
      <FooterBrandContent />
    </v-container>
  </v-footer>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import FooterBrandContent from '@/components/common/FooterBrandContent.vue'

export default defineComponent({
  name: 'FooterComponent',
  components: { FooterBrandContent },
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
.custom-main-footer-style {
  width: 100%;
  padding: 12px;
  background: transparent;
}

.custom-footer-container {
  width: 100%;
  max-width: 1600px;
  padding: 0;
}
</style>
