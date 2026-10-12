<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'
import { create } from '@/utils/GenericApiHelper'
import { URIS } from '@/constants/DpConstants'
import { isAdminOrModerator } from '@/utils/roleUtils'
import { useSidebarStore } from '@/store/sidebar'
import { UserCreationRequest } from '@/dto/users/UserCreationRequest'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import UserForm from '@/components/common/UserForm.vue'
import Roles from './js/UserRoles'
import UserExpertises from './js/UserExpertises'

interface DropDownListRequest {
  id: number
  name: string
  localName: string
}

export default defineComponent({
  name: 'UsersCreation',
  components: {
    GenericDashboardHeaderView,
    UserForm
  },
  setup() {
    const sidebarStore = useSidebarStore()
    const { t, locale } = useI18n()
    const router = useRouter()

    const availableRoles = ref<DropDownListRequest[]>([])
    const availableExpertises = ref<DropDownListRequest[]>([])

    const fetchAvailableRoles = async () => {
      return Roles(availableRoles)
    }

    const fetchAvailableExpertises = async () => {
      return UserExpertises(availableExpertises)
    }

    onMounted(() => {
      sidebarStore.show()
      fetchAvailableRoles()
      fetchAvailableExpertises()
    })

    const handleSubmit = async (formData: UserCreationRequest) => {
      try {
        const cleanedFormData = {
          ...formData,
          usersExpertises: formData.usersExpertises?.map(({ areaOfExpertisesId, expertLevel, isPrimary }) => ({
            areaOfExpertisesId,
            expertLevel,
            isPrimary
          }))
        }
        const response = await create<any>(URIS.USER.BASE_USER_URI, cleanedFormData)
        if (response?.success) {
          toast.success(t('Messages.success.user.created'))
          setTimeout(async () => {
            await router.push({ name: 'UsersList', params: {} })
          }, 1000)
        } else {
          toast.error(response.message)
        }
      } catch (error: any) {
        toast.error(error.message)
      }
    }

    const handleCancel = () => {
      router.push({ name: 'UsersList' })
    }

    const handleDataFetched = (data: any) => {
      console.log('User data fetched:', data)
    }

    const isRtl = computed(() => locale.value === 'ar')

    return {
      availableRoles,
      availableExpertises,
      handleSubmit,
      handleCancel,
      handleDataFetched,
      isAdminOrModerator,
      isRtl
    }
  }
})
</script>

<template>
  <div v-if="isAdminOrModerator()" :class="['users-creation-container', { 'users-creation-container--rtl': isRtl }]">
    <GenericDashboardHeaderView
      :title-en="$t('user.add.title')"
      :title-ar="$t('user.add.title')"
      subtitle-en="Create a user profile and return directly to the users list when needed."
      subtitle-ar="أنشئ ملف مستخدم وارجع مباشرة إلى قائمة المستخدمين عند الحاجة."
      icon="mdi-account-plus-outline"
      :show-back-button="true"
      back-route-name="UsersList"
    />
    <v-card class="custom-user-creation-screen-card-style">
      <v-card-title class="custom-user-creation-headline-title">
        <h2>{{ $t('user.add.title') }}</h2>
      </v-card-title>
      <v-card-text class="custom-user-creation-body-card-text">
        <UserForm
          mode="create"
          :available-roles="availableRoles"
          :available-expertises="availableExpertises"
          @submit="handleSubmit"
          @cancel="handleCancel"
          @data-fetched="handleDataFetched"
        />
      </v-card-text>
    </v-card>
  </div>
</template>

<style scoped lang="scss">
.users-creation-container {
  // padding: clamp(0.75rem, 2vw, 1.5rem);

  .custom-user-creation-screen-card-style {
    overflow: hidden;
    border-radius: 24px;
    border: 1px solid rgba(0, 108, 68, 0.12);
    background: radial-gradient(circle at top right, rgba(0, 108, 68, 0.08), transparent 32%),
    linear-gradient(180deg, #ffffff 0%, #f7faf8 100%);
    box-shadow: 0 20px 45px rgba(15, 23, 42, 0.08);

    .custom-user-creation-headline-title {
      padding: 10px 20px;
      background: linear-gradient(135deg, #006c44 0%, #0a8f5a 100%);
      color: white;

      h2 {
        margin: 0;
        font-weight: 600;
        font-size: clamp(1.2rem, 2vw, 1.6rem);
      }
    }

    .custom-user-creation-body-card-text {
      padding: clamp(1rem, 2.6vw, 2rem);
    }
  }
}

.users-creation-container--rtl {
  .custom-user-creation-screen-card-style {
    .custom-user-creation-headline-title,
    .custom-user-creation-body-card-text {
      text-align: right;
    }
  }
}

@media (max-width: 960px) {
  .users-creation-container {
    padding: 0.75rem;
  }
}

@media (max-width: 600px) {
  .users-creation-container {
    padding: 0;

    .custom-user-creation-screen-card-style {
      border-radius: 18px;

      .custom-user-creation-headline-title {
        padding: 1rem 1rem 0.9rem;
      }

      .custom-user-creation-body-card-text {
        padding: 0.9rem;
      }
    }
  }
}
</style>
