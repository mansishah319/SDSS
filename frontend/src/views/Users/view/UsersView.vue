<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template src="./UsersView.html" lang="html"></template>

<script lang="ts">
import { computed, defineComponent, onBeforeMount, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import UserClientService from '@/services/user/UserClientService'
import { UserMainResponse } from '@/dto/users/UserMainResponse'
import UserClientView from '@/views/Users/view/js/UserView'
import { useRoute } from 'vue-router'
import defaultAvatar from '@/assets/images/avatars/user.jpg'

export default defineComponent({
  name: 'UsersView',
  props: {
    grpNumber: {
      type: String,
      required: true
    },
    showCloseButton: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  emits: ['close'],
  setup(props) {
    const { t, locale } = useI18n()
    const route = useRoute()
    const params = route.params
    const grpNumber = ref<number>(Number(props.grpNumber) || Number(params.grpNumber))
    const imageUrl = ref<string | null>(null)
    const currentUser = ref<UserMainResponse>({} as UserMainResponse)

    const avatarColStyle = computed(() => {
      return locale.value === 'ar' ? { marginLeft: '32px', marginRight: '0' } : { marginLeft: '0', marginRight: '32px' }
    })

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
      {
        title: t('user.add.expertisesTitle'),
        align: 'start',
        key: 'expertisesName',
        width: '30%'
      },
      {
        title: t('user.add.expertLevel'),
        align: 'start',
        key: 'expertLevel',
        width: '30%'
      },
      {
        title: t('common.primary'),
        align: 'center',
        key: 'isPrimary',
        width: '20%'
      }
    ]

    onBeforeMount(async () => {
      await UserClientView(grpNumber.value).then((response) => {
        currentUser.value = response.data
      })
      if (grpNumber.value) imageUrl.value = await UserClientService.getUserProfileImageByGrpNumber(grpNumber.value)
    })

    return {
      imageUrl,
      defaultAvatar,
      currentUser,
      avatarColStyle,
      enhancedUserFields,
      expertisesHeaders
    }
  }
})
</script>

<style scoped lang="scss">
.users-view-container {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.07);
  padding: 0 0 32px 0;
  width: fit-content;
  min-height: 80vh;
  height: 80vh;
  margin: 0;
  display: flex;
  flex-direction: column;
}

.user-header-row {
  border-bottom: 1px solid #e0e0e0;
  padding: 24px 32px 0 32px;
  position: sticky;
  top: 0;
  z-index: 10;
  background: #fff;
}

.user-header-title {
  font-size: 2rem;
  font-weight: 700;
  color: #1976d2;
  display: flex;
  align-items: center;
  gap: 12px;
}

.close-btn {
  margin-left: 12px;
}

.user-main-row {
  flex: 1 1 auto;
  overflow-y: auto;
  padding: 32px 32px 0 32px;
  gap: 0;
}

.user-avatar-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 220px;
  max-width: 320px;
  margin-bottom: 24px;
  height: 100%;
}

.user-avatar-card {
  width: 100%;
  height: 100%;
  min-height: 320px;
  padding: 0 0 16px 0;
  border-radius: 16px;
  background: linear-gradient(135deg, #f8fafc 60%, #e3f2fd 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 32px rgba(25, 118, 210, 0.13);
  transition: box-shadow 0.3s,
  transform 0.3s;
  animation: fadeInCard 0.7s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-avatar-card:hover {
  box-shadow: 0 8px 32px rgba(25, 118, 210, 0.13);
  transform: translateY(-2px) scale(1.03);
}

.avatar-wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  flex: 1;
  margin-top: 24px;
  margin-bottom: 8px;
}

.avatar-img-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 12px auto;
  animation: fadeInAvatar 1s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-in-avatar {
  animation: fadeInAvatar 1s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes fadeInAvatar {
  from {
    opacity: 0;
    transform: scale(0.85);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.fade-in-card {
  animation: fadeInCard 0.7s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes fadeInCard {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-info-col {
  padding-left: 48px;
  padding-right: 0;
  transition: padding 0.3s;
}

.user-info-card {
  background: #f9fafd;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(25, 118, 210, 0.04);
  margin-bottom: 24px;
}

.user-name-row {
  font-size: 1.5rem;
  font-weight: 700;
  color: #222;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.user-subtitle {
  font-size: 1.1rem;
  color: #1976d2;
  font-weight: 500;
  opacity: 0.85;
}

.field-label {
  font-size: 1.01rem;
  color: #888;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.field-value {
  font-size: 1.13rem;
  font-weight: 600;
  color: #222;
  max-width: 100%;
  overflow: hidden;
  background: #fff;
  border-radius: 8px;
  padding: 7px 14px;
  box-shadow: 0 1px 4px rgba(25, 118, 210, 0.03);
  transition: background 0.2s;
}

.field-value:hover {
  background: #f0f6ff;
}

.field-value .truncate {
  color: #1976d2;
  font-weight: 600;
}

.field-value .truncate[title] {
  cursor: pointer;
}

.status-chip {
  font-size: 0.95rem;
  font-weight: 500;
}

.status-chip-active {
  background-color: #43a047 !important;
  color: #fff !important;
  font-weight: 700;
  border: 1.5px solid #388e3c;
  box-shadow: 0 2px 8px rgba(67, 160, 71, 0.08);
}

.status-chip-inactive {
  background-color: #ececec !important;
  color: #888 !important;
  font-weight: 500;
  border: 1.5px solid #bdbdbd;
  box-shadow: none;
}

.status-chip-deleted {
  background-color: #e53935 !important;
  color: #fff !important;
  font-weight: 700;
  border: 1.5px solid #b71c1c;
  box-shadow: 0 2px 8px rgba(229, 57, 53, 0.08);
}

.user-role {
  font-size: 1.1rem;
  color: #1976d2;
  font-weight: 600;
}

.user-info-grid {
  margin-bottom: 0;
}

@media (max-width: 900px) {
  .user-main-row {
    flex-direction: column;
    padding: 16px 8px 0 8px;
    gap: 0;
  }
  .user-avatar-col {
    max-width: 100%;
    min-width: 0;
    margin-bottom: 24px;
    margin-right: 0 !important;
    margin-left: 0 !important;
  }
  .user-info-col {
    padding-left: 0;
  }
}

@media (max-width: 600px) {
  .users-view-container {
    padding: 0 0 16px 0;
  }
  .user-header-row {
    padding: 16px 8px 0 8px;
  }
  .user-main-row {
    padding: 8px 0 0 0;
  }
  .user-avatar-card {
    min-height: 180px;
  }
  .expertises-table-card {
    margin-top: 12px;
  }
  .user-info-col {
    padding-left: 0;
  }
}
</style>
