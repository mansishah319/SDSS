<template>
  <div class="profile-fullscreen-card" :class="isAr ? 'rtl' : 'ltr'">
    <v-card class="profile-card glass-card fullscreen-card" elevation="16">
      <div class="profile-page-header">
        <GenericDashboardHeaderView
          class="profile-shared-header"
          title-en="Profile Settings"
          title-ar="إعدادات الملف الشخصي"
          subtitle-en="Update your alternate contact information while keeping core profile data unchanged."
          subtitle-ar="قم بتحديث بيانات التواصل البديلة مع الحفاظ على البيانات الأساسية للملف الشخصي."
          icon="mdi-account-cog-outline"
          :show-back-button="true"
          back-fallback-route-name="home"
        />
      </div>

      <div class="profile-avatar-wrapper-full">
        <v-avatar size="78" class="profile-avatar-glass">
          <img
            v-if="profileImage"
            :src="profileImage"
            :alt="user.fullName || 'User'"
            style="width: 100%; height: 100%; object-fit: cover"
          />
          <v-icon v-else size="90" color="#b0b0b8">mdi-account-circle</v-icon>
        </v-avatar>
      </div>
      <v-divider></v-divider>
      <v-card-text>
        <div class="section-header animated-header">
          <v-icon class="custom-profile-settings-icon" color="primary">mdi-account-details-outline</v-icon>
          {{ t('profile.personalInfo') }}
        </div>
        <v-row>
          <v-col v-for="(field, idx) in viewOnlyFields" :key="idx" cols="12" sm="6">
            <div class="profile-label custom-profile-settings-preview-only-fields">
              <v-icon size="18" class="custom-profile-settings-icon mr-1" color="primary">
                {{ field.icon }}
              </v-icon>
              {{ field.label }}
            </div>
            <div class="profile-value custom-profile-settings-preview-only-fields">
              {{ user[field.key] || '-' }}
            </div>
          </v-col>
        </v-row>
        <div class="section-header animated-header mt-3">
          <v-icon class="custom-profile-settings-icon" color="primary">mdi-email-edit-outline</v-icon>
          {{ t('profile.contactInfo') }}
        </div>
        <v-row class="custom-profile-settings-update-info-row">
          <v-col cols="12" sm="6">
            <v-text-field
              v-model="form.alternativeMobile"
              :label="t('user.add.alternativeMobile')"
              :rules="mobileRules"
              prepend-inner-icon="mdi-cellphone"
              outlined
              dense
              color="primary"
              class="profile-edit-field animated-field"
              :required="false"
              helper="Optional"
              @focus="animateField($event)"
            />
          </v-col>
          <v-col cols="12" sm="6">
            <v-text-field
              v-model="form.alternativeEmail"
              :label="t('user.add.alternativeEmail')"
              :rules="emailRules"
              prepend-inner-icon="mdi-email-outline"
              outlined
              dense
              color="primary"
              class="profile-edit-field animated-field"
              :required="false"
              helper="Optional"
              @focus="animateField($event)"
            />
          </v-col>
        </v-row>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-actions class="profile-actions">
        <v-btn color="grey lighten-1" class="cancel-btn animated-btn" elevation="0" @click="resetForm">
          <v-icon left>mdi-close</v-icon>
          <span class="common-custom-button-text-style"> {{ t('common.cancel') }} </span>
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn color="primary" :loading="loading" elevation="2" class="save-btn animated-btn" @click="saveProfile">
          <v-icon left>mdi-content-save</v-icon>
          <span class="common-custom-button-text-style"> {{ t('common.update') }} </span>
        </v-btn>
      </v-card-actions>
      <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="2500" top>
        <template v-if="snackbar.color === 'success'">
          <v-icon left color="success">mdi-check-circle</v-icon>
        </template>
        {{ snackbar.text }}
      </v-snackbar>
    </v-card>
  </div>
</template>

<script lang="ts">
import { computed, defineAsyncComponent, onMounted, reactive, ref } from 'vue'
import { useSidebarStore } from '@/store/sidebar'
import { getEmployeeFromStorage, getUserPhotoUrl } from '@/utils/userUtils'
import UserClientView from '@/views/Users/view/js/UserView'
import { Fields, URIS } from '@/constants/DpConstants'
import { useI18n } from 'vue-i18n'
import { update } from '@/utils/GenericApiHelper'
import { SuccessResponse } from '@/dto/generic/SuccessResponse'
import { UserMainResponse } from '@/dto/users/UserMainResponse'
import { toast } from 'vue3-toastify'
import { useRouter } from 'vue-router'

const GenericDashboardHeaderView = defineAsyncComponent(
  () => import('@/components/generic/header/GenericDashboardHeaderView.vue')
)

export default {
  name: 'ProfileSettings',
  components: {
    GenericDashboardHeaderView
  },
  setup() {
    const { t } = useI18n()
    const router = useRouter()
    const sidebarStore = useSidebarStore()
    const storedEmployee = getEmployeeFromStorage()
    const currentEmployee = computed(() => storedEmployee)
    const isAr = computed(() => window.localStorage.getItem('selectedLanguage') === 'ar')

    const user = reactive({
      fullName: '',
      grpNumber: '',
      nationality: '',
      mobile: '',
      email: '',
      gradeName: '',
      departmentName: '',
      alternativeMobile: '',
      alternativeEmail: '',
      userPhoto: '',
      userPhotoType: '',
      avatar: ''
    })

    const viewOnlyFields = [
      { key: 'fullName', label: t('profile.fullName'), icon: 'mdi-account' },
      { key: 'grpNumber', label: t('profile.grpNumber'), icon: 'mdi-numeric' },
      { key: 'nationality', label: t('profile.nationality'), icon: 'mdi-flag' },
      { key: 'mobile', label: t('profile.mobile'), icon: 'mdi-cellphone' },
      { key: 'email', label: t('profile.email'), icon: 'mdi-email-outline' },
      { key: 'gradeName', label: t('profile.grade'), icon: 'mdi-school-outline' },
      { key: 'departmentName', label: t('profile.department'), icon: 'mdi-office-building-outline' }
    ]

    const form = reactive({
      alternativeMobile: null as string | null,
      alternativeEmail: null as string | null
    })

    const loading = ref(false)
    const snackbar = reactive({ show: false, text: '', color: 'success' })

    const mobileRules = [(v: string) => !v || /^\+?\d{7,15}$/.test(v) || t('profile.invalidMobile')]
    const emailRules = [(v: string) => !v || /.+@.+\..+/.test(v) || t('profile.invalidEmail')]

    const isDirty = computed(
      () => form.alternativeMobile !== user.alternativeMobile || form.alternativeEmail !== user.alternativeEmail
    )

    function animateField(e: FocusEvent) {
      const el = e.target as HTMLElement
      if (el) {
        el.classList.add('field-glow')
        setTimeout(() => el.classList.remove('field-glow'), 600)
      }
    }

    function resetForm() {
      form.alternativeMobile = user.alternativeMobile
      form.alternativeEmail = user.alternativeEmail
      router.push({ name: 'home' })
    }

    onMounted(async () => {
      sidebarStore.disable()
      let storedEmployee: any
      try {
        const empStr = localStorage.getItem(Fields.LOGGED_IN_EMP)
        storedEmployee = empStr ? JSON.parse(empStr) : null
      } catch (e) {
        console.error('Error retrieving employee from localStorage:', e)
        storedEmployee = null
      }
      const grpNumber = storedEmployee?.grpNumber || ''
      if (grpNumber) {
        try {
          const response = await UserClientView(grpNumber)
          for (const key in response.data) {
            user[key] = response.data[key]
          }
        } catch (e) {
          console.error('Error fetching user data:', e)
        }
      }
      form.alternativeMobile = user.alternativeMobile
      form.alternativeEmail = user.alternativeEmail
    })

    const profileImage = computed(() => getUserPhotoUrl(currentEmployee.value))

    async function saveProfile() {
      const mobileValid = !form.alternativeMobile || /^\+?\d{7,15}$/.test(form.alternativeMobile)
      const emailValid = !form.alternativeEmail || /.+@.+\..+/.test(form.alternativeEmail)

      if (!mobileValid || !emailValid) {
        snackbar.text = !mobileValid ? t('profile.invalidMobile') : t('profile.invalidEmail')
        snackbar.color = 'error'
        snackbar.show = true
        return
      }
      loading.value = true
      try {
        await update<SuccessResponse<UserMainResponse>>(URIS.USER.UPDATE_MY_PROFILE, form).then(() => {
          snackbar.color = 'success'
          snackbar.text = t('profile.updateSuccess')
          snackbar.show = true
          toast.success(t('Messages.common.updated'))
          router.push({ name: 'home' })
        })
      } catch (e) {
        console.error('Error updating profile:', e)
        snackbar.text = t('profile.updateFail')
        snackbar.color = 'error'
        snackbar.show = true
      } finally {
        loading.value = false
      }
    }

    return {
      user,
      viewOnlyFields,
      form,
      loading,
      snackbar,
      mobileRules,
      emailRules,
      isDirty,
      animateField,
      saveProfile,
      resetForm,
      profileImage,
      t,
      isAr
    }
  }
}
</script>

<style scoped lang="scss">
.profile-fullscreen-card {
  --profile-emerald: #0b9f6a;
  --profile-teal: #0d7d8f;
  --profile-surface: rgba(255, 255, 255, 0.86);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 0;
  margin: 0;
  background: radial-gradient(circle at 15% 10%, rgba(11, 159, 106, 0.2), transparent 38%),
  radial-gradient(circle at 85% 15%, rgba(13, 125, 143, 0.16), transparent 42%),
  linear-gradient(155deg, #effaf5 0%, #f8fcff 52%, #f6fbfd 100%);
}

.profile-card.fullscreen-card {
  width: 100%;
  min-height: auto;
  max-height: none;
  border-radius: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: flex-start;
  border: 1px solid rgba(16, 57, 58, 0.09);
  box-shadow: 0 16px 44px rgba(4, 33, 44, 0.14);
  overflow: hidden;
}

.profile-page-header {
  padding: 8px 12px 0;
}

.profile-shared-header {
  margin-bottom: 0;
}

.profile-page-header :deep(.generic-dashboard-header) {
  margin-bottom: 4px;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(6, 55, 60, 0.12);
}

.glass-card {
  background: linear-gradient(165deg, var(--profile-surface) 0%, rgba(247, 255, 252, 0.95) 100%);
  backdrop-filter: blur(12px);
  animation: profile-pop 0.45s cubic-bezier(0.2, 0.85, 0.32, 1);
}

.profile-avatar-wrapper-full {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  margin: 4px 0 2px;
  z-index: 2;
  position: relative;
}

.profile-avatar-wrapper-full::before {
  content: '';
  position: absolute;
  width: min(540px, 82%);
  height: 1px;
  bottom: -8px;
  background: linear-gradient(90deg, transparent, rgba(11, 159, 106, 0.28), transparent);
}

.profile-avatar-glass {
  box-shadow: 0 10px 30px rgba(5, 88, 65, 0.18);
  border: 4px solid #fff;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(231, 255, 246, 0.95) 100%);
  transition: box-shadow 0.3s,
  transform 0.3s;
}

.profile-avatar-glass:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 32px rgba(5, 88, 65, 0.22);
}

.profile-title {
  font-size: clamp(1.05rem, 1.2vw, 1.32rem);
  font-weight: 700;
  letter-spacing: 0.3px;
  color: #045f52;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 6px 16px 8px;
}

:deep(.v-divider) {
  border-color: rgba(14, 113, 100, 0.13);
}

:deep(.v-card-text) {
  overflow: hidden;
  padding: 6px 14px 4px;
}

.section-header {
  font-size: 0.98rem;
  font-weight: 700;
  color: #0d7d8f;
  margin: 4px 0 6px;
  display: flex;
  align-items: center;
  letter-spacing: 0.15px;
  opacity: 0.95;
}

.animated-header {
  animation: header-pop 0.6s ease;
}

.profile-label {
  font-size: 0.82rem;
  color: #5f6f75;
  font-weight: 600;
  margin-bottom: 2px;
  display: flex;
  align-items: center;
}

.profile-value {
  font-size: 0.9rem;
  font-weight: 600;
  color: #10282a;
  background: linear-gradient(180deg, #ffffff 0%, #f4fbf8 100%);
  border: 1px solid rgba(13, 125, 143, 0.12);
  border-radius: 12px;
  padding: 6px 8px;
  margin-bottom: 6px;
  transition: transform 0.2s,
  box-shadow 0.2s;
}

.profile-value:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(13, 125, 143, 0.11);
}

.profile-edit-field {
  margin-bottom: 0;
}

.animated-field:focus-within {
  box-shadow: 0 0 0 3px rgba(11, 159, 106, 0.2);
  border-radius: 10px;
}

.field-glow {
  box-shadow: 0 0 0 4px rgba(11, 159, 106, 0.2) !important;
  border-radius: 10px;
}

.profile-actions {
  padding: 8px 12px;
  display: flex;
  align-items: center;
  border-top: 1px solid rgba(14, 113, 100, 0.1);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(6px);
}

.save-btn,
.cancel-btn {
  min-width: 104px;
  font-weight: 700;
  font-size: 0.88rem;
  border-radius: 10px;
  margin-inline-end: 8px;
  transition: box-shadow 0.2s,
  transform 0.18s;
}

.save-btn:last-child,
.cancel-btn:last-child {
  margin-inline-end: 0;
}

.save-btn {
  box-shadow: 0 10px 18px rgba(11, 159, 106, 0.23);
}

.animated-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 22px rgba(11, 159, 106, 0.24);
}

@keyframes profile-pop {
  from {
    opacity: 0;
    transform: translateY(14px) scale(0.985);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes header-pop {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.ltr {
  .custom-profile-settings-preview-only-fields {
    margin-left: 2px;
  }

  .custom-profile-settings-icon {
    margin-right: 4px;
  }
}

.rtl {
  .custom-profile-settings-preview-only-fields {
    margin-right: 2px;
  }

  .custom-profile-settings-icon {
    margin-left: 4px;
  }
}

@media (max-width: 900px) {
  .profile-fullscreen-card {
    padding: 0;
  }

  .profile-card.fullscreen-card {
    min-height: auto;
    max-height: none;
    border-radius: 0;
  }

  :deep(.v-card-text) {
    overflow: visible;
    padding: 6px 10px 4px;
  }

  .profile-actions {
    padding: 6px 8px;
  }

  .profile-page-header {
    padding: 8px 10px 0;
  }

  .profile-page-header :deep(.generic-dashboard-header) {
    border-radius: 16px;
    margin-bottom: 4px;
  }
}

@media (max-width: 700px) {
  .profile-avatar-wrapper-full {
    margin: 14px 0 10px;
  }

  .profile-title {
    font-size: 1.06rem;
    padding: 8px 10px 12px;
  }

  .profile-value {
    font-size: 0.95rem;
  }

  .save-btn,
  .cancel-btn {
    min-width: 96px;
    font-size: 0.86rem;
    margin-inline-end: 6px;
  }
}
</style>
