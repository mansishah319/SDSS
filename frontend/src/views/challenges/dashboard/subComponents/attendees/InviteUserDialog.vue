<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue'
import { create, httpGenericRequest } from '@/utils/GenericApiHelper.ts'
import { PaginationResponse } from '@/dto/generic/PaginationResponse.ts'
import { UserSearchData } from '@/dto/users/UserSearchResponse.ts'
import { URIS } from '@/constants/DpConstants.ts'
import { useRouter } from 'vue-router'

let allUsersCache: any[] = []

export default defineComponent({
  props: {
    showInvitePopup: {
      type: Boolean,
      required: true
    }
  },
  emits: ['update:showInvitePopup'],
  setup(props, { emit }) {
    const route = useRouter()
    const searchQuery = ref('')
    const loading = ref(false)
    const userSuggestions = ref<any[]>([])
    const selectedUsers = ref<any[]>([])
    const params = route.currentRoute.value.params
    const challengeId = params.challengeId

    const fetchAllUsers = async () => {
      try {
        const response = await httpGenericRequest<PaginationResponse<UserSearchData>>(
          URIS.METHODS.GET,
          URIS.USER.USER_SEARCH_URI,
          null,
          null
        )
        allUsersCache = response.data
      } catch (error) {
        console.error('Error fetching all users:', error)
      }
    }

    const fetchUserSuggestions = () => {
      const query = searchQuery.value.toLowerCase()
      userSuggestions.value = allUsersCache.filter(
        (user) =>
          user?.fullName?.toLowerCase().includes(query) ||
          user?.email.toLowerCase().includes(query) ||
          user?.alternativeEmail?.toLowerCase().includes(query) ||
          user.grpNumber.toLowerCase().includes(query)
      )
    }

    const selectUser = (user: any) => {
      const isAlreadySelected = selectedUsers.value.some((u) => u.grpNumber === user.grpNumber)

      if (!isAlreadySelected) {
        selectedUsers.value.push(user)
      }

      userSuggestions.value = []
      searchQuery.value = ''
    }

    const removeUser = (user: any) => {
      // Debugging line to check values
      console.log('Removing user:', user)

      selectedUsers.value = selectedUsers.value.filter((u) => u.grpNumber !== user.grpNumber)
    }

    const sendInvitations = async () => {
      console.log(selectedUsers.value)
      console.log(challengeId)
      const grpNumberList: number[] = selectedUsers.value.map((user) => user.grpNumber as number) as number[]
      console.log('Sending invitations to:', grpNumberList)
      const data = {
        userGrpList: grpNumberList,
        challengeId: challengeId
      }
      const response = await create(URIS.USER_INVITATION.BASE + URIS.USER_INVITATION.INVITE_LIST, data)
      console.log(response.data)
      closeDialog()
    }

    const closeDialog = () => {
      emit('update:showInvitePopup', false)
    }

    onMounted(() => {
      fetchAllUsers()
    })

    return {
      searchQuery,
      loading,
      userSuggestions,
      selectedUsers,
      fetchUserSuggestions,
      selectUser,
      removeUser,
      sendInvitations,
      closeDialog
    }
  }
})
</script>

<template src="./InviteUserDialog.html"></template>

<style src="./InviteUserDialog.scss" scoped></style>
