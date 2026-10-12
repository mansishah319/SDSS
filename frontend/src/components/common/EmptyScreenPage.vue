<template lang="html">
  <EmptyDataTable :image-size="imageSize" :message="$t(message)"></EmptyDataTable>
  <v-col cols="12" class="d-flex justify-center">
    <div class="mt-3">
      <BackButton
        :redirect-path="redirectTo"
        :title="backButtonTitle"
        :icon="backButtonIcon"
        :color="backButtonColor"
      />
    </div>
  </v-col>
  <!-- Footer -->
  <FooterComponent :justify-content="'justify-content-start'"></FooterComponent>
</template>
<script lang="ts">
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import FooterComponent from '@/components/common/FooterComponent.vue'
import BackButton from '@/components/common/BackButton.vue'

export default {
  name: 'EmptyScreenPage',
  components: { BackButton, FooterComponent, EmptyDataTable },
  props: {
    imageSize: {
      type: Number,
      default: 128
    },
    message: {
      type: String,
      default: 'common.empty'
    },
    redirectTo: {
      type: String,
      default: 'home'
    },
    backButtonTitle: {
      type: String,
      default: 'common.back'
    },
    backButtonIcon: {
      type: String,
      default: 'back'
    },
    backButtonColor: {
      type: String,
      default: '#000000'
    }
  }
}
</script>
