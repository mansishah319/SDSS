<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { computed, defineComponent, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { IMessage, StompSubscription } from '@stomp/stompjs'
import { get, httpGenericWebSocketRequest } from '@/utils/GenericApiHelper.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { Characters, URIS } from '@/constants/DpConstants.ts'
import { ChallengeAttendeesUsersResponse } from '@/dto/challenge/ChallengeAttendeesUsersResponse.ts'
import { WebsocketClient } from '@/utils/WebsocketClient.ts'
import InviteUserDialog from '@/views/challenges/dashboard/subComponents/attendees/InviteUserDialog.vue'

type AttendeesSocketPayload = {
  challengeId?: number
  attendeesResponse?: ChallengeAttendeesUsersResponse
  onlineCount?: number
  offlineCount?: number
  allAttendeesUsers?: ChallengeAttendeesUsersResponse['allAttendeesUsers']
  invitedAdminUsers?: ChallengeAttendeesUsersResponse['invitedAdminUsers']
  invitedValidatorUsers?: ChallengeAttendeesUsersResponse['invitedValidatorUsers']
}

export default defineComponent({
  name: 'AttendeesUsers',
  components: { InviteUserDialog },
  props: {
    isTheChallengeModerator: {
      type: Boolean,
      required: true
    }
  },
  setup(props) {
    const route = useRouter()
    const showInvitePopup = ref(false)
    const userAttendeesResponse = ref<ChallengeAttendeesUsersResponse>({
      onlineCount: 0,
      offlineCount: 0,
      invitedAdminUsers: [],
      invitedValidatorUsers: [],
      allAttendeesUsers: []
    })
    const dataLoaded = ref(false)
    const attendeesSubscription = ref<StompSubscription | null>(null)
    const countOnlineAttendees = ref(0)
    const countOfflineAttendees = ref(0)

    const isModerator = computed(() => props.isTheChallengeModerator)
    const normalizedChallengeId = Number(route.currentRoute.value.params.challengeId)

    const applyAttendeesResponse = (payload?: AttendeesSocketPayload | null) => {
      if (!payload) {
        return
      }

      if (payload.challengeId && payload.challengeId !== normalizedChallengeId) {
        return
      }

      const attendeesResponse = payload.attendeesResponse ?? payload
      if (!attendeesResponse) {
        return
      }

      userAttendeesResponse.value = attendeesResponse as ChallengeAttendeesUsersResponse
      const allAttendeesUsers = userAttendeesResponse.value?.allAttendeesUsers || []
      let onlineCount = 0
      let offlineCount = 0

      for (const attendee of allAttendeesUsers) {
        if (attendee.onlineStatus) {
          onlineCount += 1
        } else {
          offlineCount += 1
        }
      }

      countOnlineAttendees.value = onlineCount || userAttendeesResponse.value?.onlineCount || 0
      countOfflineAttendees.value = offlineCount || userAttendeesResponse.value?.offlineCount || 0
      dataLoaded.value = true
    }

    const loadAttendeesData = async (funChallengeId: any) => {
      if (!funChallengeId) {
        console.error('Challenge ID is not available yet.')
        return // Exit early if challengeId is not available
      }
      try {
        const websocketClient = WebsocketClient.getInstance()
        websocketClient.sendUserStatus('online', funChallengeId)

        const res = await httpGenericWebSocketRequest<SuccessResponse<ChallengeAttendeesUsersResponse>>(
          'get',
          URIS.WEB_SOCKET.CHALLENGES.BASE +
          URIS.WEB_SOCKET.CHALLENGES.USER_ATTENDEES +
          Characters.QUESTION_MARK +
          'challengeId=' +
          funChallengeId
        )

        if (res?.success) {
          applyAttendeesResponse(res.data)
          return res.data as ChallengeAttendeesUsersResponse
        } else {
          console.log(res?.message)
        }
      } catch (error) {
        console.error('Error loading attendees data:', error)
        try {
          const res = await get<SuccessResponse<ChallengeAttendeesUsersResponse>>(
            URIS.CHALLENGES.BASE +
            URIS.CHALLENGES.USER_ATTENDEES +
            Characters.QUESTION_MARK +
            'challengeId=' +
            funChallengeId
          )
          if (res?.success) {
            applyAttendeesResponse(res.data as ChallengeAttendeesUsersResponse)
            return res.data as ChallengeAttendeesUsersResponse
          } else {
            console.log(res?.message)
          }
        } catch (error: any) {
          console.log(error?.message)
        }
      }
    }

    const allAttendeesUsers = computed(() => userAttendeesResponse.value?.allAttendeesUsers || [])

    const handleVisibilityChange = () => {
      const websocketClient = WebsocketClient.getInstance()
      if (document.visibilityState === 'hidden') {
        websocketClient.sendUserStatus('offline', normalizedChallengeId)
      } else {
        websocketClient.sendUserStatus('online', normalizedChallengeId)
      }
      loadAttendeesData(normalizedChallengeId)
    }

    const handleBeforeUnload = () => {
      const websocketClient = WebsocketClient.getInstance()
      websocketClient.sendUserStatus('offline', normalizedChallengeId)
    }

    const websocketClient = WebsocketClient.getInstance()
    const subscribeToAttendees = () => {
      attendeesSubscription.value?.unsubscribe()
      attendeesSubscription.value =
        websocketClient.getStompClient()?.subscribe('/topic/public', (message: IMessage) => {
          const payload = JSON.parse(message.body) as AttendeesSocketPayload
          applyAttendeesResponse(payload)
        }) || null
    }

    const handleSocketConnected = () => {
      subscribeToAttendees()
      publishOnlineStatus()
      loadAttendeesData(normalizedChallengeId)
    }

    const publishOfflineStatus = () => {
      if (!normalizedChallengeId) {
        return
      }

      websocketClient.sendUserStatus('offline', normalizedChallengeId)
    }

    const publishOnlineStatus = () => {
      if (!normalizedChallengeId) {
        return
      }

      websocketClient.sendUserStatus('online', normalizedChallengeId)
    }

    onMounted(async () => {
      websocketClient.setOnConnectCallback(handleSocketConnected)

      if (websocketClient.isConnected()) {
        subscribeToAttendees()
        publishOnlineStatus()
      }

      document.addEventListener('visibilitychange', handleVisibilityChange)
      window.addEventListener('beforeunload', handleBeforeUnload)
      window.addEventListener('pagehide', handleBeforeUnload)

      await loadAttendeesData(normalizedChallengeId)
    })

    onBeforeUnmount(() => {
      publishOfflineStatus()
      websocketClient.removeOnConnectCallback(handleSocketConnected)
      attendeesSubscription.value?.unsubscribe()
      document.removeEventListener('visibilitychange', handleVisibilityChange)
      window.removeEventListener('beforeunload', handleBeforeUnload)
      window.removeEventListener('pagehide', handleBeforeUnload)
    })

    return {
      allAttendeesUsers,
      countOnlineAttendees,
      countOfflineAttendees,
      isModerator,
      showInvitePopup
    }
  }
})
</script>

<template src="./Attendees.html"></template>

<style src="./Attendees.scss" scoped></style>
