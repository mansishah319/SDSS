<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'
import { update } from '@/utils/GenericApiHelper'
import { URIS } from '@/constants/DpConstants'
import { isAdminOrModerator } from '@/utils/roleUtils'
import { useSidebarStore } from '@/store/sidebar'
import { UserCreationRequest } from '@/dto/users/UserCreationRequest'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import UserForm from '@/components/common/UserForm.vue'
import ContentLoader from '@/components/loader/ContentLoader.vue'
import Roles from './js/UserRoles'
import UserExpertises from './js/UserExpertises'
import UserClientService from '@/services/user/UserClientService'

interface DropDownListRequest {
  id: number
  name: string
  localName: string
}

export default defineComponent({
  name: 'UpdateUser',
  components: {
    GenericDashboardHeaderView,
    ContentLoader,
    UserForm
  },
  props: {
    grpNumber: {
      type: String,
      default: ''
    }
  },
  setup(props) {
    const sidebarStore = useSidebarStore()
    const { t } = useI18n()
    const router = useRouter()

    const availableRoles = ref<DropDownListRequest[]>([])
    const availableExpertises = ref<DropDownListRequest[]>([])
    const initialData = ref({})
    const loading = ref(false)

    const fetchAvailableRoles = async () => {
      return Roles(availableRoles)
    }

    const fetchAvailableExpertises = async () => {
      return UserExpertises(availableExpertises)
    }

    const fetchUserData = async () => {
      if (!props.grpNumber) {
        toast.error(t('error.mandatory'))
        return
      }

      loading.value = true
      try {
        const response = await UserClientService.getByUserByGrp(props.grpNumber)
        if (response?.data) {
          const userData = response.data

          // Transform the data to match the form structure
          initialData.value = {
            grpNumber: userData.grpNumber || props.grpNumber,
            language: userData.language === 'English' ? 'EN' : 'AR',
            fullName: userData.fullName || '',
            shortDepartmentName: userData.shortDepartmentName || '',
            gradeName: userData.gradeName || '',
            jobName: userData.jobName || '',
            email: userData.email || '',
            alternativeEmail: userData.alternativeEmail || '',
            mobile: userData.mobile || '0501414755',
            alternativeMobile: userData.alternativeMobile || '',
            internalDocuments: userData.internalDocuments || false,
            chatContents: userData.chatContents || false,
            socialMediaFeeds: userData.socialMediaFeeds || false,
            decision: userData.decision || false,
            questionView: userData.questionView || false,
            scrollingText: userData.scrollingText || false,
            isDelphiModerator: userData.isDelphiModerator || false,
            roles: userData.roles || [],
            // Transform usersExpertisesResponses to the format expected by UserForm
            usersExpertises: userData.usersExpertisesResponses
              ? userData.usersExpertisesResponses.map((expertise: any) => ({
                areaOfExpertisesId: expertise.expertisesId.toString(),
                expertLevel: expertise.expertLevel,
                isPrimary: expertise.isPrimary,
                // Add the name for display purposes
                areaOfExpertisesName: expertise.expertisesName
              }))
              : []
          }
        }
      } catch (error: any) {
        console.error('Error fetching user data:', error)
        toast.error(error.message || t('Messages.error.data.fetchFailed'))
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      sidebarStore.show()
      fetchAvailableRoles()
      fetchAvailableExpertises()
      fetchUserData()
    })

    const handleSubmit = async (formData: UserCreationRequest) => {
      try {
        // Remove areaOfExpertisesName from usersExpertises before sending
        const cleanedFormData = {
          ...formData,
          usersExpertises: formData.usersExpertises?.map(({ areaOfExpertisesId, expertLevel, isPrimary }) => ({
            areaOfExpertisesId,
            expertLevel,
            isPrimary
          }))
        }
        const response = await update<any>(`${URIS.USER.BASE_USER_URI}`, cleanedFormData)
        if (response?.success) {
          console.log('User updated Successfully')
          toast.success(t('Messages.success.user.updated'))
          setTimeout(async () => {
            await router.push({ name: 'UsersList', params: {} })
          }, 1000)
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

    return {
      availableRoles,
      availableExpertises,
      initialData,
      loading,
      handleSubmit,
      handleCancel,
      handleDataFetched,
      isAdminOrModerator
    }
  }
})
</script>

<template>
  <div v-if="isAdminOrModerator()" class="update-user-container">
    <DubaiPoliceLoader :is-loading="loading" variant="badge" :message="$t('common.loader.userRecord')" />
    <GenericDashboardHeaderView
      :title-en="$t('user.update.title')"
      :title-ar="$t('user.update.title')"
      subtitle-en="Update the user record and navigate back to the users list from the header."
      subtitle-ar="حدّث سجل المستخدم وانتقل إلى قائمة المستخدمين من خلال الترويسة."
      icon="mdi-account-edit-outline"
      :show-back-button="true"
      back-route-name="UsersList"
    />
    <v-card class="custom-update-user-screen-card-style">
      <v-card-title class="custom-update-user-headline-title">
        <h2>{{ $t('user.update.title') }}</h2>
      </v-card-title>
      <v-card-text class="custom-update-user-body-card-text">
        <ContentLoader
          v-if="loading"
          variant="form"
          :heading="$t('common.loader.userProfileHeading')"
          :message="$t('common.loader.userUpdateForm')"
          :description="$t('common.loader.userUpdateFormDescription')"
          class="skeleton-loader"
        />

        <UserForm
          v-else
          mode="update"
          :initial-data="initialData"
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
.update-user-container {
  .custom-update-user-screen-card-style {
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

    .custom-update-user-headline-title {
      background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
      color: white;
      border-radius: 12px 12px 0 0;

      h2 {
        margin: 0;
        font-weight: 600;
      }
    }

    .custom-update-user-body-card-text {
      padding: 2rem;
    }
  }

  .skeleton-loader {
    min-height: 400px;
  }
}
</style>
