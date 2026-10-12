<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->
<template>
  <div class="user-list-card">
    <div class="user-list-header">
      <h4 class="green-heading font-bukra-bold">{{ title }}</h4>
      <v-chip size="small" color="success" variant="tonal">{{ users?.length || 0 }}</v-chip>
    </div>
    <v-divider></v-divider>

    <div v-if="users && users.length > 0" class="user-list-grid">
      <article v-for="(user, index) in users" :key="index" class="user-item" :class="{ rtl: isRtl }">
        <div class="user-item-index font-bukra-bold">{{ index + 1 }}</div>

        <div class="user-item-body">
          <div class="user-item-row user-item-name-row">
            <span class="user-item-label">{{ $t('common.variety.name') }}</span>
            <strong class="user-item-value user-item-name">{{ user.fullName || $t('notAvailable') }}</strong>
          </div>

          <div class="user-item-grid">
            <div v-if="showGrpNumber" class="user-item-row">
              <span class="user-item-label">{{ $t('common.variety.grpNumber') }}</span>
              <span class="user-item-value">{{ user.grpNumber || $t('notAvailable') }}</span>
            </div>

            <div class="user-item-row user-item-email-row">
              <span class="user-item-label">{{ $t('common.variety.email') }}</span>
              <span class="user-item-value user-item-email">{{ user.email || $t('notAvailable') }}</span>
            </div>
          </div>
        </div>
      </article>
    </div>

    <template v-else>
      <EmptyDataTable :image-size="128" :message="$t('Messages.common.empty.validator')"></EmptyDataTable>
    </template>
  </div>
</template>

<script>
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'

export default {
  components: { EmptyDataTable },
  props: {
    title: { type: String, required: true },
    users: { type: Array, required: true },
    showGrpNumber: { type: Boolean, default: true }
  },
  data() {
    return {
      isRtl: false // Set this dynamically based on a language direction
    }
  },
  mounted() {
    // Detect language direction and set isRtl accordingly
    this.isRtl = document.documentElement.dir === 'rtl'
  }
}
</script>

<style scoped>
.user-list-card {
  padding: 16px;
}

.user-list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.green-heading {
  color: #0d6b4d;
  margin: 0;
}

.user-list-grid {
  display: grid;
  gap: 14px;
  margin-top: 18px;
}

.user-item {
  display: grid;
  grid-template-columns: 44px minmax(0, 1fr);
  gap: 14px;
  align-items: flex-start;
  padding: 16px;
  border: 1px solid rgba(13, 107, 77, 0.1);
  border-radius: 18px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fcfa 100%);
}

.user-item.rtl {
  direction: rtl;
}

.user-item-index {
  width: 44px;
  height: 44px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: rgba(13, 107, 77, 0.1);
  color: #0d6b4d;
}

.user-item-body {
  min-width: 0;
}

.user-item-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.user-item-row {
  display: grid;
  gap: 4px;
  min-width: 0;
}

.user-item-label {
  font-size: 0.74rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #6c7c75;
}

.user-item-value {
  color: #1d342c;
  line-height: 1.6;
  overflow-wrap: anywhere;
  word-break: break-word;
}

.user-item-name {
  font-size: 1rem;
}

.user-item-email {
  font-size: 0.95rem;
}

@media (max-width: 700px) {
  .user-item {
    grid-template-columns: 1fr;
  }

  .user-item-grid {
    grid-template-columns: 1fr;
  }
}
</style>
