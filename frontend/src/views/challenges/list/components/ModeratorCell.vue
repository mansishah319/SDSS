<template>
  <div class="moderator-cell">
    <v-chip
      v-for="user in users"
      :key="`${user.role}-${user.grpNumber}`"
      :color="user.color"
      variant="tonal"
      size="small"
      class="moderator-chip"
    >
      <v-icon start size="14">{{ user.icon }}</v-icon>
      <span class="moderator-chip__label">{{ user.label }}</span>
    </v-chip>

    <span v-if="users.length === 0" class="moderator-cell__empty">-</span>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import { useI18n } from 'vue-i18n'
import type { ChallengesResponse } from '@/dto/challenge/ChallengesResponse'
import type { UserDropDownListResponse } from '@/dto/challenge/UserDropDownListResponse'

type ChipUser = {
  role: string
  label: string
  grpNumber: number | string
  icon: string
  color: string
}

export default defineComponent({
  name: 'ModeratorCell',
  props: {
    item: {
      type: Object as () => ChallengesResponse,
      required: true
    }
  },
  setup(props) {
    const { t } = useI18n()

    const buildChipLabel = (user?: UserDropDownListResponse | null) => {
      if (!user) {
        return ''
      }

      const fullName = user.fullName?.trim()
      const firstName = user.firstname?.trim()
      const lastName = user.lastname?.trim()
      const fallbackName = [firstName, lastName].filter(Boolean).join(' ').trim()
      const displayName = fullName || fallbackName

      if (displayName && user.grpNumber) {
        return `${displayName} (${user.grpNumber})`
      }

      return displayName || String(user.grpNumber || '')
    }

    const users = computed<ChipUser[]>(() => {
      const result: ChipUser[] = []

      if (props.item?.moderator) {
        result.push({
          role: 'moderator',
          label: buildChipLabel(props.item.moderator),
          grpNumber: props.item.moderator.grpNumber,
          icon: 'mdi-account-tie',
          color: 'primary'
        })
      }

      if (props.item?.assistantModerator) {
        result.push({
          role: 'assistant-moderator',
          label: buildChipLabel(props.item.assistantModerator),
          grpNumber: props.item.assistantModerator.grpNumber,
          icon: 'mdi-account-supervisor-circle',
          color: 'secondary'
        })
      }

      return result.filter((user) => user.label)
    })

    return {
      t,
      users
    }
  }
})
</script>

<style scoped lang="scss">
.moderator-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
  align-items: center;
  min-height: 2rem;
}

.moderator-chip {
  max-width: 100%;
}

.moderator-chip__label {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.moderator-cell__empty {
  color: rgba(0, 0, 0, 0.54);
  font-size: 0.875rem;
}
</style>
