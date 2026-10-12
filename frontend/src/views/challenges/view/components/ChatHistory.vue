<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->
<template>
  <v-card class="dp-card dp-card-height">
    <v-col cols="12">
      <h4 class="green-heading font-bukra-bold">
        <v-icon class="mdi mdi-chat-outline"></v-icon>
        {{ $t('challenge.viewInfo.chatHistory') }}
      </h4>
    </v-col>
    <v-divider></v-divider>
    <template v-if="messages && messages.length > 0">
      <div class="chat-history custom-scrollable-chat-history mt-5 d-flex flex-column gap-3">
        <div v-for="(chat, index) in messages" :key="index" class="message-item">
          <div class="message-avatar">
            <v-avatar size="40">
              <img :src="getAvatarSrc(chat.user?.base64Image)" alt="User avatar" />
            </v-avatar>
          </div>
          <div class="message-content">
            <div class="message-header">
              <div class="message-author">
                <v-icon>mdi-account</v-icon>
                {{ chat.user.fullName }}
              </div>
            </div>
            <v-list-item-subtitle class="message-text">
              <template v-if="chat.message">
                {{ chat.message }}
              </template>
              <template v-else>
                <template v-if="chat.file">
                  <template
                    v-if="
                      [
                        'jpeg',
                        'jpg',
                        'png',
                        'gif',
                        'bmp',
                        'tiff',
                        'tif',
                        'webp',
                        'svg',
                        'ico',
                        'heif',
                        'heic',
                        'raw',
                        'cr2',
                        'nef',
                        'arw',
                        'orf',
                        'rw2',
                      ].includes(chat.type)
                    "
                  >
                    <v-img :src="'data:image/' + chat.type + ';base64,' + chat.file" height="200" contain></v-img>
                  </template>
                  <template v-else>
                    <a :href="'data:application/octet-stream;base64,' + chat.file" :download="chat.fileName">
                      <span class="mdi mdi-file"></span>
                      {{ chat.fileName }}
                    </a>
                  </template>
                </template>
              </template>
            </v-list-item-subtitle>
          </div>
        </div>
      </div>
    </template>

    <!-- Empty state message if no documents available -->
    <template v-else>
      <EmptyDataTable :image-size="128" :message="$t('common.empty')"></EmptyDataTable>
    </template>
  </v-card>
</template>

<script>
import defaultAvatar from '@/assets/images/avatars/user.jpg'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'

export default {
  components: { EmptyDataTable },
  props: {
    messages: {
      type: Array,
      required: false,
      default: () => []
    }
  },
  methods: {
    getAvatarSrc(avatar) {
      if (avatar !== null && avatar !== undefined && avatar.trim() !== '') {
        return `data:image/png;base64,${avatar}`
      } else {
        return defaultAvatar
      }
    }
  }
}
</script>

<style scoped>
.message-item {
  display: flex;
  align-items: flex-start;
}

.message-avatar {
  margin-right: 10px;
  border-radius: 100px;
  border: 2px solid #e9e9e9;
}

.message-content {
  flex: 1;
}

.message-header {
  font-weight: bold;
}

.message-text {
  margin-top: 5px;
}

.custom-scrollable-chat-history {
  max-height: 265px;
  overflow-y: auto;
}
</style>
