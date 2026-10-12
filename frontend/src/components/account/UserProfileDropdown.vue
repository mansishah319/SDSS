<!--
  * ***************************************************************
  * Copyright ©1956-2024 DubaiPolice. All rights reserved
  * ——————————————————————————————————
  * NOTICE: All information contained herein is a property of DubaiPolice.
  * ***************************************************************
-->

<template v-if="isHasAnyRole()">
  <div class="user-profile-dropdown-wrapper" :dir="computedDir">
    <v-menu
      v-model="isMenuOpen"
      location="bottom end"
      transition="scale-transition"
      :close-on-content-click="true"
      offset-y
      min-width="260"
    >
      <template #activator="{ props }">
        <v-btn icon rounded elevation="0" class="user-profile-trigger" v-bind="props">
          <div class="avatar-online-wrapper">
            <v-avatar class="custom-avatar-style" size="48">
              <template v-if="showFallbackAvatar">
                <v-icon size="40" color="#006c44">mdi-account-circle</v-icon>
              </template>
              <template v-else>
                <img :src="profileImage" :alt="currentEmployee?.name || 'User'" @error="onAvatarError" />
              </template>
            </v-avatar>
            <span v-if="isOnline" class="online-indicator online-indicator--trigger" :title="t('common.online')"></span>
          </div>
        </v-btn>
      </template>
      <transition name="dropdown-glass-fade">
        <div v-if="isMenuOpen" class="modern-glass-menu">
          <div class="modern-menu-user" @click="handleProfileCurrentUserView">
            <div class="avatar-online-wrapper">
              <v-avatar size="44" class="modern-menu-avatar">
                <template v-if="showFallbackAvatar">
                  <v-icon size="40" color="#b0b0b8">mdi-account-circle</v-icon>
                </template>
                <template v-else>
                  <img :src="profileImage" :alt="currentEmployee?.name || 'User'" @error="onAvatarError" />
                </template>
              </v-avatar>
              <span v-if="isOnline" class="online-indicator online-indicator--menu" :title="t('common.online')"></span>
            </div>
            <div class="modern-menu-userinfo">
              <div class="modern-menu-username">
                {{ currentEmployee?.name || 'User' }}
                <v-icon v-if="isOnline" size="12" color="#22c55e">mdi-circle</v-icon>
                <span v-if="currentEmployee?.isPro" class="modern-menu-badge">PRO</span>
              </div>
              <div class="modern-menu-email">{{ currentEmployee?.email }}</div>
            </div>
          </div>
          <ul class="modern-menu-list">
            <li class="modern-menu-item" @click="handleProfileSettings">
              <v-icon size="22" class="modern-menu-icon">mdi-cog-outline</v-icon>
              <span>
                {{ t('common.profileSettings') }}
              </span>
            </li>
          </ul>
          <div class="modern-menu-divider"></div>
          <ul class="modern-menu-list">
            <li class="modern-menu-item signout" @click="handleLogoutProcess">
              <v-icon size="22" class="modern-menu-icon">mdi-logout</v-icon>
              <span>
                {{ t('common.signOut') }}
              </span>
            </li>
          </ul>
        </div>
      </transition>
    </v-menu>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import Employee from '@/dto/Employee'
import { useI18n } from 'vue-i18n'
import { getEmployeeFromStorage, getUserPhotoUrl } from '@/utils/userUtils'
import { Fields, SecurityConstants, SuccessMessages, URIS } from '@/constants/DpConstants'
import { isHasAnyRole } from '@/utils/roleUtils'
import { WebsocketClient } from '@/utils/WebsocketClient'
import { createWithActiveSocket } from '@/utils/GenericApiHelper'
import Wso2Service from '@/idm/Wso2Service'

export default defineComponent({
  name: 'UserProfileDropdown',
  props: {
    employee: {
      type: Object as () => Employee,
      required: false,
      default: null
    },
    rtl: {
      type: Boolean,
      default: false
    }
  },
  emits: ['helpCenter', 'lightMode', 'upgradePlan'],
  setup(props) {
    const { t } = useI18n()
    const router = useRouter()
    const computedDir = computed(() => (props.rtl ? 'rtl' : 'ltr'))
    const isOnline = ref(false)
    const isMenuOpen = ref(false)
    let onlineStatusInterval: ReturnType<typeof setInterval> | null = null

    const storedEmployee = getEmployeeFromStorage()
    const currentEmployee = computed(() => storedEmployee || props.employee)

    const profileImage = computed(() => {
      return getUserPhotoUrl(currentEmployee.value)
    })

    const syncOnlineStatus = () => {
      const stompClient = WebsocketClient.getInstance().getStompClient()
      isOnline.value = Boolean(stompClient?.connected)
    }

    function onAvatarError() {
      // If userPhoto fails, try to use avatar as a fallback
      if (currentEmployee.value?.avatar) {
        // This will trigger a re-render with the avatar
      }
    }

    const handleProfileSettings = () => {
      router.push({ name: 'ProfileSettings' })
    }

    const handleLogoutProcess = async () => {
      try {
        await createWithActiveSocket(`${URIS.WEB_SOCKET.USER.BASE}${URIS.WEB_SOCKET.USER.OFFLINE}`, null)
      } catch {
        // Continue with local logout cleanup.
      }

      try {
        await WebsocketClient.getInstance().disconnect()
      } catch {
        // Continue with local logout cleanup.
      }

      localStorage.removeItem(Fields.THEME)
      localStorage.removeItem(Fields.EMPLOYEE)
      localStorage.removeItem(Fields.LOGGED_IN_EMP)
      localStorage.removeItem(Fields.EMPLOYEE_LANG)
      localStorage.removeItem(SuccessMessages.GRP_NO)
      localStorage.removeItem(SecurityConstants.TOKEN)
      localStorage.removeItem(SecurityConstants.Survey_TOKEN)
      localStorage.removeItem(Fields.CURRENT_USER_DASHBOARD_TAB)
      localStorage.removeItem(Fields.EMPLOYEE_LANG_LOCAL_STORAGE)
      localStorage.removeItem(Fields.CURRENT_USER_SIDE_BAR_VISIBILITY)

      if (!Wso2Service.isLocalEnvironment()) {
        await Wso2Service.logout()
        return
      }

      window.location.reload()
    }

    const handleProfileCurrentUserView = () => {
      router.push({ name: 'MyProfileView' })
    }

    // Watch for prop changes (e.g., user logs in/out)
    watch(
      () => props.employee,
      () => {
        // Re-evaluate when employee prop changes
      },
      { deep: true }
    )

    onMounted(() => {
      syncOnlineStatus()
      onlineStatusInterval = setInterval(syncOnlineStatus, 1500)
    })

    onBeforeUnmount(() => {
      if (onlineStatusInterval) {
        clearInterval(onlineStatusInterval)
      }
    })

    return {
      computedDir,
      profileImage,
      showFallbackAvatar: false,
      onAvatarError,
      handleProfileSettings,
      handleProfileCurrentUserView,
      currentEmployee,
      handleLogoutProcess,
      isHasAnyRole,
      isOnline,
      isMenuOpen,
      t
    }
  }
})
</script>

<style scoped lang="scss">
.user-profile-dropdown-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.user-profile-trigger {
  cursor: pointer;
  display: flex;
  align-items: center;
  border-radius: 50%;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
  }
}

.custom-avatar-style {
  background-color: #f8fafc;
}

.avatar-online-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.online-indicator {
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #22c55e;
  border: 2px solid #ffffff;
  box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.2);
  z-index: 3;
}

.online-indicator--trigger {
  right: 0;
  bottom: 2px;
}

.online-indicator--menu {
  right: -2px;
  bottom: 1px;
}

.modern-glass-menu {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.85) 0%, rgba(230, 255, 245, 0.85) 100%);
  backdrop-filter: blur(16px);
  border-radius: 22px;
  box-shadow: 0 12px 48px 0 rgba(0, 0, 0, 0.18),
  0 2px 8px 0 rgba(0, 0, 0, 0.1);
  min-width: 280px;
  padding: 0;
  overflow: hidden;
  color: #222;
  border: 1.5px solid rgba(180, 220, 200, 0.35);
  animation: glass-pop 0.5s cubic-bezier(0.23, 1, 0.32, 1);
}

.modern-menu-user {
  display: flex;
  align-items: center;
  padding: 26px 28px 14px 28px;
  border-bottom: 1.5px solid rgba(180, 220, 200, 0.25);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.7) 0%, rgba(230, 255, 245, 0.7) 100%);
  cursor: pointer;
  transition: background 0.22s cubic-bezier(0.23, 1, 0.32, 1),
  transform 0.18s;
  border-radius: 12px 12px 0 0;

  &:hover {
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.85) 0%, rgba(230, 255, 245, 0.85) 100%);
    transform: scale(1.02);

    .modern-menu-username {
      color: #008c5a;
    }

    .modern-menu-avatar {
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
      transform: scale(1.05);
    }
  }
}

.modern-menu-avatar {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border: 3px solid #fff;
  background: #f8fafc;
  transition: box-shadow 0.22s cubic-bezier(0.23, 1, 0.32, 1),
  transform 0.18s;
}

.modern-menu-userinfo {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.modern-menu-username {
  font-size: 18px;
  font-weight: 700;
  color: #222;
  display: flex;
  align-items: center;
  gap: 8px;
}

.modern-menu-badge {
  background: linear-gradient(90deg, #4f8cff 0%, #00e6d0 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border-radius: 8px;
  padding: 2px 10px;
  margin-left: 6px;
  letter-spacing: 0.5px;
  box-shadow: 0 0 8px 2px rgba(79, 140, 255, 0.18);
  border: 1.5px solid #fff;
}

.modern-menu-email {
  font-size: 14px;
  color: #4a4a4a;
  opacity: 0.7;
  margin-top: 2px;
}

.modern-menu-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.modern-menu-item {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 18px 32px;
  font-size: 17px;
  color: #222;
  cursor: pointer;
  border-radius: 12px;
  font-family: BukraRegular, sans-serif;
  font-weight: 500;
  transition: background 0.22s cubic-bezier(0.23, 1, 0.32, 1),
  color 0.18s,
  transform 0.18s;
  will-change: background, color, transform;

  &:hover,
  &.active {
    background: linear-gradient(90deg, rgba(0, 236, 188, 0.13) 0%, rgba(79, 140, 255, 0.13) 100%);
    color: #008c5a;
    transform: scale(1.04);

    .modern-menu-icon {
      color: #008c5a;
    }
  }

  .modern-menu-icon {
    color: #7a7a7a;
    transition: color 0.18s;
  }

  &.signout {
    color: black;

    .modern-menu-icon {
      color: black;
    }

    &:hover {
      background: linear-gradient(90deg, rgb(163, 28, 28) 0%, rgba(172, 172, 172, 0.08) 100%);
      color: #fff;

      .modern-menu-icon {
        color: #fff;
      }
    }
  }
}

.modern-menu-divider {
  // height: 1.5px
  background: rgba(180, 220, 200, 0.18);
  //margin: 16px 0
}

// Glassy pop transition
.dropdown-glass-fade-enter-active {
  transition: opacity 0.33s cubic-bezier(0.23, 1, 0.32, 1),
  transform 0.33s cubic-bezier(0.23, 1, 0.32, 1);
}

.dropdown-glass-fade-leave-active {
  transition: opacity 0.22s cubic-bezier(0.23, 1, 0.32, 1),
  transform 0.22s cubic-bezier(0.23, 1, 0.32, 1);
}

.dropdown-glass-fade-enter-from,
.dropdown-glass-fade-leave-to {
  opacity: 0;
  transform: scale(0.96) translateY(-12px);
}

.rtl {
  .modern-menu-avatar {
    margin-left: 16px;
  }

  .online-indicator--trigger {
    right: auto;
    left: 0;
  }

  .online-indicator--menu {
    right: auto;
    left: -2px;
  }
}

.ltr {
  .modern-menu-avatar {
    margin-right: 16px;
  }
}

@keyframes glass-pop {
  0% {
    opacity: 0;
    transform: scale(0.92) translateY(-16px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>
