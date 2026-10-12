<template>
  <v-container fluid class="custom-survey-dashboard-container">
    <v-card class="survey-dashboard-survey-card">
      <v-card-title class="survey-dashboard-card-title animate__animated animate__fadeInUp font-dubai-bold">
        {{ $t('futurePoll.main-title') }}
      </v-card-title>
    </v-card>
    <v-row>
      <CustomComingSoon :height="'50vh'"></CustomComingSoon>
    </v-row>
  </v-container>
  <!-- Footer -->
  <FooterComponent :justify-content="'justify-content-start'"></FooterComponent>
</template>

<script lang="ts">
import CustomComingSoon from '@/components/custom/CustomComingSoon.vue'
import { defineComponent, onMounted } from 'vue'
import FooterComponent from '@/components/common/FooterComponent.vue'
import { useSidebarStore } from '@/store/sidebar.ts'

export default defineComponent({
  name: 'FuturePollScreen',
  components: { FooterComponent, CustomComingSoon },
  setup() {
    const sidebarStore = useSidebarStore()

    onMounted(() => {
      sidebarStore.disable()
    })

    return {}
  }
})
</script>

<style scoped lang="scss">
.custom-survey-dashboard-container {
  overflow-y: auto;
  overflow-x: hidden;
}

.survey-dashboard-survey-card {
  margin-top: 2rem;
  border-radius: 12px; /* Rounded corners */
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); /* Subtle shadow */
}

.survey-dashboard-card-title {
  font-size: 2rem; /* Larger font size */
  font-weight: bold;
  color: #333; /* Dark text for better visibility */
  text-align: center;
  padding: 1rem;
  background-color: #f4f5f7; /* Subtle background */
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
}

/* Responsive adjustments */
@media (max-width: 500px) {
  .survey-dashboard-survey-card {
    margin-top: 1rem;
  }

  .survey-dashboard-card-title {
    font-size: 1.4rem; /* Smaller title on mobile */
  }
}
</style>
