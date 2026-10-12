<!--
  ~ * ***************************************************************
  ~ * Copyright ©1956-2024 DubaiPolice. All rights reserved
  ~ * ——————————————————————————————————
  ~ * NOTICE: All information contained herein is a property of DubaiPolice.
  ~ * ***************************************************************
  -->

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { isAdmin, isAdminOrModerator, isDelphiModerator } from '@/utils/roleUtils'
import { useSidebarStore } from '@/store/sidebar'
import managementIcon from '@/assets/images/logo/management.png'
import controllingIcon from '@/assets/images/logo/target.png'
import surveyIcon from '@/assets/images/logo/online--survey.png'

const router = useRouter()
const { t } = useI18n()
const sidebarStore = useSidebarStore()

const adminModules = ref([
  {
    id: 1,
    title: t('admin.modules.users'),
    iconImage: managementIcon,
    route: '/users',
    color: '#4CAF50',
    description: t('admin.moduleDescriptions.users'),
    isAuthorized: isAdmin()
  },
  {
    id: 2,
    title: t('admin.modules.challenges'),
    iconImage: controllingIcon,
    route: '/challenge/list',
    color: '#2196F3',
    description: t('admin.moduleDescriptions.challenges'),
    isAuthorized: isAdminOrModerator()
  },
  {
    id: 3,
    title: t('admin.modules.surveys'),
    iconImage: surveyIcon,
    route: '/surveys',
    color: '#FF9800',
    description: t('admin.moduleDescriptions.surveys'),
    isAuthorized: isDelphiModerator()
  }
])

const isPageLoaded = ref(false)

onMounted(() => {
  sidebarStore.show()
  setTimeout(() => {
    isPageLoaded.value = true
  }, 100)
})

const navigateToModule = (route: string) => {
  router.push(route)
}
</script>

<template>
  <div class="admin-welcome-container" :class="{ 'page-loaded': isPageLoaded }">
    <!-- Welcome Header -->
    <div class="welcome-header">
      <v-icon icon="mdi-shield-crown" size="48" class="welcome-icon" color="primary"></v-icon>
      <h1 class="text-h3 welcome-title">{{ t('admin.welcome.title') }}</h1>
      <p class="text-body-1 welcome-subtitle">{{ t('admin.welcome.subtitle') }}</p>
    </div>

    <!-- Admin Modules Grid -->
    <div class="admin-modules-grid">
      <v-hover v-for="(module, idx) in adminModules" :key="module.id" v-slot="{ isHovering, props }">
        <v-card
          v-if="module.isAuthorized"
          v-show="module.isAuthorized"
          v-bind="props"
          :class="['module-card', { 'is-hovering': isHovering }]"
          elevation="4"
          :style="{ '--index': idx }"
          @click="navigateToModule(module.route)"
        >
          <div class="module-content" :style="{ '--module-color': module.color }">
            <div class="icon-container" :class="{ bounce: isHovering }">
              <img
                v-if="module.iconImage"
                :src="module.iconImage"
                :alt="`${module.title} icon`"
                loading="lazy"
                class="module-image-icon"
              />
              <v-icon v-else :icon="module.icon" size="36"></v-icon>
            </div>
            <h3 class="text-h6 module-title">{{ module.title }}</h3>
            <p class="text-body-2 module-description">{{ module.description }}</p>
          </div>
          <div class="hover-overlay">
            <v-btn variant="text" color="white" class="explore-btn" :ripple="false">
              {{ t('admin.modules.explore') }}
              <v-icon end icon="mdi-arrow-right" class="explore-icon"></v-icon>
            </v-btn>
          </div>
        </v-card>
      </v-hover>
    </div>

    <!--     Quick Stats Section -->
    <div v-if="false" v-show="false" class="quick-stats" :hidden="true">
      <v-card class="stats-card" elevation="2">
        <v-card-text>
          <div class="stats-content">
            <div class="stat-item">
              <v-icon icon="mdi-account-multiple" color="primary" size="32"></v-icon>
              <div class="stat-details">
                <span class="stat-value">2,547</span>
                <span class="stat-label">{{ t('admin.stats.totalUsers') }}</span>
              </div>
            </div>
            <v-divider vertical></v-divider>
            <div class="stat-item">
              <v-icon icon="mdi-trophy" color="success" size="32"></v-icon>
              <div class="stat-details">
                <span class="stat-value">156</span>
                <span class="stat-label">{{ t('admin.stats.activeChallenge') }}</span>
              </div>
            </div>
            <v-divider vertical></v-divider>
            <div class="stat-item">
              <v-icon icon="mdi-poll" color="info" size="32"></v-icon>
              <div class="stat-details">
                <span class="stat-value">89%</span>
                <span class="stat-label">{{ t('admin.stats.satisfaction') }}</span>
              </div>
            </div>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.admin-welcome-container {
  padding: 32px;
  //min-height: 100vh
  background-size: 400% 400%;
  animation: gradientBG 12s ease infinite;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);

  &.page-loaded {
    opacity: 1;
    transform: translateY(0);
  }

  .welcome-header {
    text-align: center;
    margin-bottom: 48px;
    animation: fadeInDown 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;

    .welcome-icon {
      margin-bottom: 16px;
      animation: rotateIn 1s cubic-bezier(0.4, 0, 0.2, 1);
    }

    .welcome-title {
      color: var(--v-primary-base);
      margin-bottom: 16px;
      font-weight: 600;
      background: linear-gradient(135deg, #006c44 0%, #004d32 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      font-family: bukrabold, serif;
    }

    .welcome-subtitle {
      color: #6c757d;
      max-width: 600px;
      margin: 0 auto;
      font-family: bukrabold, serif;
    }
  }

  .admin-modules-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 24px;
    margin-bottom: 48px;

    .module-card {
      border-radius: 16px;
      overflow: hidden;
      cursor: pointer;
      position: relative;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      animation: fadeInUp 0.6s cubic-bezier(0.4, 0, 0.2, 1) both;
      animation-delay: calc(var(--index) * 0.12s);

      &.is-hovering {
        transform: translateY(-8px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);

        .hover-overlay {
          opacity: 1;
        }

        .module-content {
          transform: scale(1.01);
        }
      }

      .module-content {
        padding: 24px;
        background: white;
        transition: transform 0.3s ease;
        position: relative;
        z-index: 1;

        &::before {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 4px;
          background: var(--module-color);
        }

        .icon-container {
          width: 64px;
          height: 64px;
          border-radius: 16px;
          background: linear-gradient(135deg, var(--module-color) 0%, rgba(var(--module-color), 0.8) 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 16px;

          .v-icon {
            color: white;
          }

          .module-image-icon {
            width: 70px;
            height: 70px;
            object-fit: contain;
          }
        }

        .animated-icon.bounce {
          animation: bounce 0.7s;
        }

        .module-title {
          margin-bottom: 8px;
          color: #000;
          font-family: bukrabold, serif;
        }

        .module-description {
          color: #6c757d;
          line-height: 1.5;
          font-family: bukraregular, serif;
        }
      }

      .hover-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg, var(--module-color) 0%, rgba(var(--module-color), 0.9) 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: all 0.3s ease;
        z-index: 2;

        .explore-btn {
          font-weight: 600;
          letter-spacing: 0.5px;
          transition: transform 0.2s,
          background 0.2s;

          .explore-icon {
            transition: transform 0.3s ease;
          }
        }

        .module-card.is-hovering .explore-btn {
          transform: scale(1.08);
          background: rgba(255, 255, 255, 0.15);
        }
      }
    }
  }

  .quick-stats {
    animation: fadeInUp 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
    animation-delay: 0.6s;

    .stats-card {
      border-radius: 16px;
      background: white;

      .stats-content {
        display: flex;
        justify-content: space-around;
        padding: 16px;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 16px;

          .stat-details {
            display: flex;
            flex-direction: column;

            .stat-value {
              font-size: 1.5rem;
              font-weight: 600;
              color: #2c3e50;
            }

            .stat-label {
              font-size: 0.875rem;
              color: #6c757d;
            }
          }
        }
      }
    }
  }
}

// Animations
@keyframes gradientBG {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
  50% {
    transform: translateY(-20px);
  }
  70% {
    transform: translateY(-10px);
  }
}

@keyframes rotateIn {
  from {
    opacity: 0;
    transform: rotate(-180deg);
  }
  to {
    opacity: 1;
    transform: rotate(0);
  }
}

// Responsive Design
@media (max-width: 960px) {
  .admin-welcome-container {
    padding: 24px;

    .welcome-header {
      margin-bottom: 32px;

      .welcome-title {
        font-size: 2rem;
      }
    }

    .quick-stats {
      .stats-content {
        flex-direction: column;
        gap: 24px;

        .v-divider {
          display: none;
        }

        .stat-item {
          justify-content: center;
        }
      }
    }
  }
}

@media (max-width: 600px) {
  .admin-welcome-container {
    padding: 16px;

    .welcome-header {
      .welcome-title {
        font-size: 1.75rem;
      }
    }

    .admin-modules-grid {
      grid-template-columns: 1fr;
      gap: 16px;
    }
  }
}

// RTL Support
.rtl {
  .explore-icon {
    transform: scaleX(-1);
  }

  .module-card.is-hovering .explore-icon {
    transform: translateX(-4px) scaleX(-1);
  }
}
</style>
