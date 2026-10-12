<template>
  <v-card class="dp-card common-challenge-card-container">
    <h4 class="tab-title mb-4">{{ $t('challenge.guestInvitation') }}</h4>
    <CustomComingSoon
      :hide-back-button="true"
      class="custom-coming-soon-screen-guest-users"
      height="35vh"
    ></CustomComingSoon>
  </v-card>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import CustomComingSoon from '@/components/custom/CustomComingSoon.vue'

export default defineComponent({
  name: 'GuestInvitationTab',
  components: { CustomComingSoon },
  props: {
    selectedGuestUsers: {
      type: Array as () => string[],
      required: true
    },
    isAllGuestUsersSelected: {
      type: Boolean,
      required: true
    }
  },
  emits: ['update:selectedGuestUsers', 'select-all-guest-users', 'update:otherselectedGuestUsers'],
  setup(props, { emit }) {
    const selectAllGuestUsers = () => {
      if (props.isAllGuestUsersSelected) {
        emit('update:selectedGuestUsers', [])
      } else {
        // const allEmails = props.guestUsers.map((user) => user.email)
        emit('update:otherselectedGuestUsers', [])
      }
    }

    const toggleUser = (email: string) => {
      const newSelected = [...props.selectedGuestUsers]
      const index = newSelected.indexOf(email)
      if (index > -1) {
        newSelected.splice(index, 1)
      } else {
        newSelected.push(email)
      }
      emit('update:selectedGuestUsers', newSelected)
    }

    return {
      selectAllGuestUsers,
      toggleUser
    }
  }
})
</script>

<style scoped lang="scss">
.small-width {
  width: 50px;
}

.common-table-container {
  height: 600px;
}

.custom-coming-soon-screen-guest-users {
  @media (min-width: 1920px) {
    .v-container {
      max-width: 60%;
    }
  }
}
</style>
