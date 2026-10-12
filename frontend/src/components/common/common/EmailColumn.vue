<template>
  <div class="email-cell">
    <div class="email-value">{{ item.email }}</div>
    <div v-if="item.emailVerified" class="email-verified">
      <v-icon size="16" color="success">mdi-check-circle</v-icon>
      <span class="verified-text">Verified</span>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'EmailColumn',
  props: {
    item: {
      type: Object,
      required: true
    },
    value: {
      type: String,
      default: null
    }
  }
})
</script>

<style scoped>
.email-cell {
  .email-value {
    font-weight: 500;
    color: var(--v-theme-text-primary);
    font-size: 0.875rem;
  }

  .email-verified {
    display: flex;
    align-items: center;
    gap: 4px;
    margin-top: 4px;

    .verified-text {
      font-size: 0.75rem;
      color: var(--v-theme-success);
      font-weight: 500;
    }
  }
}
</style>
