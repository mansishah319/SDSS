<!--
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
-->

<template>
  <v-sheet class="users-view-container" :dir="dir">
    <div class="profile-page-header">
      <GenericDashboardHeaderView
        class="profile-shared-header"
        title-en="My Profile"
        title-ar="ملفي الشخصي"
        subtitle-en="Review your account details, status, and expertise profile in one place."
        subtitle-ar="استعرض بيانات حسابك وحالتك ومستوى الخبرات في مكان واحد."
        icon="mdi-account-badge-outline"
        :show-back-button="true"
        back-fallback-route-name="home"
      />
    </div>

    <v-row class="user-main-row" align="stretch" no-gutters>
      <v-col cols="12" md="3" class="user-avatar-col d-flex align-center justify-center" :style="avatarColStyle">
        <v-card class="user-avatar-card d-flex align-center justify-center" elevation="1">
          <div class="avatar-wrapper d-flex align-center justify-center flex-column" style="flex: 1">
            <v-avatar size="108" class="avatar-img-wrapper mx-auto mb-2">
              <v-img :src="imageUrl || defaultAvatar" cover class="fade-in-avatar" />
            </v-avatar>
            <v-chip
              :color="
                currentUser?.status === 'ACTIVE' || currentUser.status === 'نشط'
                  ? 'success'
                  : currentUser?.status === 'DELETED' || currentUser.status === 'محذوف'
                    ? 'error'
                    : 'grey'
              "
              :class="[
                'status-chip',
                currentUser?.status === 'ACTIVE' || currentUser.status === 'نشط'
                  ? 'status-chip-active'
                  : currentUser?.status === 'DELETED' || currentUser.status === 'محذوف'
                    ? 'status-chip-deleted'
                    : 'status-chip-inactive',
              ]"
              size="small"
              label
            >
              <v-icon start size="16">
                {{
                  currentUser?.status === 'ACTIVE' || currentUser.status === 'نشط'
                    ? 'mdi-check-circle'
                    : currentUser?.status === 'DELETED' || currentUser.status === 'محذوف'
                      ? 'mdi-delete-circle'
                      : 'mdi-cancel'
                }}
              </v-icon>
              {{
                t(
                  currentUser?.status === 'ACTIVE' || currentUser.status === 'نشط'
                    ? 'common.status.active'
                    : currentUser?.status === 'DELETED' || currentUser.status === 'محذوف'
                      ? 'common.status.deleted'
                      : 'common.status.inactive'
                )
              }}
            </v-chip>
            <span class="user-role mt-2">{{ currentUser?.mainExpertLevel }}</span>
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" md="9" class="user-info-col">
        <v-card class="user-info-card pa-4">
          <div class="user-name-row mb-2">
            <v-icon color="primary" size="22" class="me-2">mdi-account</v-icon>
            <span class="user-name">{{ currentUser?.fullName || '-' }}</span>
            <span v-if="currentUser?.mainExpertLevel" class="user-subtitle ms-2">
              {{ currentUser?.mainExpertLevel }}
            </span>
          </div>

          <v-row class="user-info-grid mt-1">
            <v-col v-for="field in enhancedUserFields" :key="field.key" cols="12" sm="6" class="mb-2">
              <div class="field-label d-flex align-center mb-1">
                <v-icon
                  v-if="field.icon"
                  :icon="field.icon"
                  size="16"
                  class="me-1"
                  :class="{ 'ms-1': isAr }"
                  color="primary"
                />
                <span>{{ t(field.label) }}</span>
              </div>
              <div class="field-value" :title="currentUser?.[field.key]">
                <v-tooltip
                  v-if="currentUser?.[field.key] && currentUser?.[field.key].length > 20"
                  :text="currentUser?.[field.key]"
                >
                  <template #activator="{ props }">
                    <span class="truncate" v-bind="props">{{ currentUser?.[field.key] || '-' }}</span>
                  </template>
                </v-tooltip>
                <span v-else class="truncate">{{ currentUser?.[field.key] || '-' }}</span>
              </div>
            </v-col>
          </v-row>
        </v-card>

        <v-card
          v-if="currentUser && currentUser.usersExpertisesResponses && currentUser.usersExpertisesResponses.length > 0"
          class="expertises-table-card mt-2"
          elevation="1"
        >
          <v-card-title>
            <v-icon start>mdi-head-snowflake-outline</v-icon>
            {{ t('user.add.expertisesMainTitle') }}
          </v-card-title>
          <v-data-table
            :headers="expertisesHeaders"
            :items="expertiseItems"
            class="expertises-table"
            density="compact"
            hide-default-footer
            :mobile-breakpoint="0"
          />
        </v-card>
      </v-col>
    </v-row>
  </v-sheet>
</template>

<script lang="ts">
import { computed, defineAsyncComponent, defineComponent, onBeforeMount, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { get } from '@/utils/GenericApiHelper'
import { URIS } from '@/constants/DpConstants'
import defaultAvatar from '@/assets/images/avatars/user.jpg'
import { getEmployeeFromStorage, getUserPhotoUrl } from '@/utils/userUtils'

const GenericDashboardHeaderView = defineAsyncComponent(
  () => import('@/components/generic/header/GenericDashboardHeaderView.vue')
)

export default defineComponent({
  name: 'MyProfileView',
  components: {
    GenericDashboardHeaderView
  },
  setup() {
    const { t, locale } = useI18n()
    const currentUser = ref<any>({})
    const imageUrl = ref<string | null>(null)

    const avatarColStyle = computed(() =>
      locale.value === 'ar' ? { marginLeft: '12px', marginRight: '0' } : { marginLeft: '0', marginRight: '12px' }
    )
    const dir = computed(() => (locale.value === 'ar' ? 'rtl' : 'ltr'))
    const isAr = computed(() => locale.value === 'ar')

    const enhancedUserFields = [
      { key: 'grpNumber', label: 'user.search.list.grpNumber', icon: 'mdi-identifier' },
      { key: 'email', label: 'user.search.email', icon: 'mdi-email' },
      { key: 'mobile', label: 'user.search.list.mobile', icon: 'mdi-cellphone' },
      { key: 'gradeName', label: 'user.grade', icon: 'mdi-account-school-outline' },
      { key: 'sectorName', label: 'user.sector', icon: 'mdi-domain' },
      { key: 'departmentName', label: 'user.search.list.departmentName', icon: 'mdi-bank' },
      { key: 'departmentType', label: 'user.departmentType', icon: 'mdi-office-building' },
      { key: 'sectorShortName', label: 'user.sectorShortName', icon: 'mdi-domain' },
      { key: 'createdAt', label: 'user.createdAt', icon: 'mdi-calendar' }
    ]

    const expertisesHeaders = [
      { title: t('user.add.expertisesTitle'), align: 'start' as const, key: 'expertisesName', width: '30%' },
      { title: t('user.add.expertLevel'), align: 'start' as const, key: 'expertLevel', width: '30%' },
      { title: t('common.primary'), align: 'center' as const, key: 'isPrimary', width: '20%' }
    ]

    const expertiseItems = computed(() => currentUser.value?.usersExpertisesResponses ?? [])

    onBeforeMount(async () => {
      const response = await get<any>(URIS.USER.MY_PROFILE)
      currentUser.value = (response as any)?.data ?? response
      const employee = getEmployeeFromStorage()
      imageUrl.value = getUserPhotoUrl(employee)
    })

    return {
      t,
      currentUser,
      imageUrl,
      defaultAvatar,
      avatarColStyle,
      enhancedUserFields,
      expertisesHeaders,
      dir,
      isAr,
      expertiseItems
    }
  }
})
</script>

<style scoped lang="scss">
.users-view-container {
  --profile-primary: #0f6a88;
  --profile-surface: #fbfdff;
  background: radial-gradient(circle at 12% 16%, rgba(15, 106, 136, 0.08), transparent 36%),
  radial-gradient(circle at 88% 0%, rgba(24, 156, 128, 0.06), transparent 40%), var(--profile-surface);
  border: 1px solid rgba(13, 82, 97, 0.1);
  box-shadow: 0 10px 26px rgba(13, 47, 56, 0.1);
  width: 100%;
  margin: 0;
  display: flex;
  flex-direction: column;
  padding: 0 0 8px;
}

.profile-page-header {
  padding: 8px 10px 0;
}

.profile-page-header :deep(.generic-dashboard-header) {
  margin-bottom: 4px;
  border-radius: 14px;
  box-shadow: 0 8px 20px rgba(13, 47, 56, 0.1);
}

.user-main-row {
  padding: 6px 10px 4px;
  gap: 6px;
}

.user-avatar-col {
  min-width: 210px;
  max-width: 280px;
  margin-bottom: 4px;
}

.user-avatar-card {
  width: 100%;
  padding: 8px;
  border-radius: 14px;
  border: 1px solid rgba(14, 95, 117, 0.12);
  background: linear-gradient(165deg, #f7fcfe 0%, #eff9ff 52%, #f6fdfa 100%);
  box-shadow: 0 8px 20px rgba(15, 78, 94, 0.12);
}

.avatar-wrapper {
  margin: 0;
}

.avatar-img-wrapper {
  margin: 0 auto 6px;
  border: 2px solid rgba(15, 106, 136, 0.2);
  box-shadow: 0 6px 14px rgba(15, 106, 136, 0.14);
}

.user-info-col {
  padding-left: 8px;
  padding-right: 0;
}

.user-info-card {
  background: linear-gradient(170deg, #ffffff 0%, #f9fdff 100%);
  border: 1px solid rgba(13, 82, 97, 0.1);
  border-radius: 14px;
  box-shadow: 0 6px 16px rgba(10, 56, 63, 0.07);
  margin-bottom: 6px;
}

.user-name-row {
  font-size: clamp(0.95rem, 1.05vw, 1.08rem);
  gap: 6px;
}

.user-subtitle {
  font-size: 0.9rem;
  color: var(--profile-primary);
}

.field-label {
  font-size: 0.74rem;
  margin-bottom: 2px;
}

.field-value {
  font-size: 0.84rem;
  border-radius: 10px;
  padding: 5px 8px;
  box-shadow: 0 3px 8px rgba(19, 79, 91, 0.05);
}

.field-value .truncate {
  color: #0f6a88;
  font-weight: 600;
}

.status-chip {
  font-size: 0.8rem;
  font-weight: 700;
}

.status-chip-active {
  background-color: #43a047 !important;
  color: #fff !important;
  border: 1px solid #388e3c;
}

.status-chip-inactive {
  background-color: #ececec !important;
  color: #888 !important;
  border: 1px solid #bdbdbd;
}

.status-chip-deleted {
  background-color: #e53935 !important;
  color: #fff !important;
  border: 1px solid #b71c1c;
}

.user-role {
  font-size: 0.78rem;
  color: #116f87;
  background: rgba(17, 111, 135, 0.09);
  border: 1px solid rgba(17, 111, 135, 0.16);
  border-radius: 999px;
  padding: 3px 9px;
}

.expertises-table-card {
  border-radius: 12px;
  border: 1px solid rgba(13, 82, 97, 0.1);
  box-shadow: 0 5px 12px rgba(10, 56, 63, 0.06);
  overflow: hidden;
}

.expertises-table-card :deep(.v-card-title) {
  background: linear-gradient(90deg, rgba(15, 106, 136, 0.1), rgba(20, 166, 122, 0.08));
  color: #11434f;
  font-weight: 700;
  border-bottom: 1px solid rgba(13, 82, 97, 0.1);
}

.expertises-table :deep(thead th) {
  font-weight: 700;
  color: #0f5567;
}

.expertises-table :deep(tbody tr:nth-child(odd)) {
  background: rgba(15, 106, 136, 0.03);
}

@media (max-width: 900px) {
  .user-main-row {
    padding: 8px 8px 0;
  }

  .user-avatar-col {
    max-width: 100%;
    min-width: 0;
    margin-right: 0 !important;
    margin-left: 0 !important;
  }

  .user-info-col {
    padding-left: 0;
    padding-top: 4px;
  }
}

@media (max-width: 600px) {
  .profile-page-header {
    padding: 6px 6px 0;
  }

  .profile-page-header :deep(.generic-dashboard-header) {
    border-radius: 12px;
  }

  .user-main-row {
    padding: 4px 6px;
  }
}
</style>
