<!-- DocumentList.vue -->
<template>
  <v-card class="dp-card document-list-dp-card-height pa-4">
    <div class="document-list-header">
      <h4 class="green-heading font-bukra-bold">
        {{ $t('challenge.viewInfo.documentList') }}
      </h4>
      <v-chip size="small" color="success" variant="tonal">{{ documentList?.length || 0 }}</v-chip>
    </div>
    <v-divider></v-divider>

    <div v-if="documentList && documentList.length > 0" class="document-list-grid">
      <article v-for="(document, index) in documentList" :key="index" class="document-item">
        <div class="document-item-icon">
          <v-icon size="24">mdi-file-document-outline</v-icon>
        </div>

        <div class="document-item-body">
          <span class="document-item-label">{{ $t('challenge.viewInfo.documentName') }}</span>
          <strong class="document-name">{{ document.name }}</strong>
        </div>

        <div class="document-item-action">
          <v-btn icon color="primary" variant="tonal" @click="downloadDocument(document.content, document.name)">
            <v-icon>mdi-download</v-icon>
          </v-btn>
        </div>
      </article>
    </div>

    <template v-if="!(documentList && documentList.length > 0)">
      <EmptyDataTable :image-size="128" :message="$t('Messages.common.empty.documents')"></EmptyDataTable>
    </template>
  </v-card>
</template>

<script>
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'

export default {
  components: { EmptyDataTable },
  props: {
    documentList: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    downloadDocument(base64Content, fileName) {
      const byteCharacters = atob(base64Content)
      const byteNumbers = Array.from(byteCharacters, (char) => char.charCodeAt(0))
      const byteArray = new Uint8Array(byteNumbers)
      const blob = new Blob([byteArray], {
        type: 'application/octet-stream'
      })

      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = fileName
      link.click()
      URL.revokeObjectURL(link.href)
    }
  }
}
</script>

<style scoped>
.dp-card {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 18px;
  overflow: hidden;
}

.document-list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.green-heading {
  color: #0d6b4d;
  font-weight: bold;
  margin: 0;
}

.document-list-grid {
  display: grid;
  gap: 14px;
  margin-top: 18px;
  max-height: 320px;
  overflow-y: auto;
  padding-right: 2px;
}

.document-item {
  display: grid;
  grid-template-columns: 44px minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
  padding: 14px 16px;
  border: 1px solid rgba(13, 107, 77, 0.1);
  border-radius: 18px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fcfa 100%);
}

.document-item-icon {
  width: 44px;
  height: 44px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: rgba(13, 107, 77, 0.1);
  color: #0d6b4d;
}

.document-item-body {
  min-width: 0;
  display: grid;
  gap: 4px;
}

.document-item-label {
  font-size: 0.74rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #6c7c75;
}

.document-name {
  font-size: 0.95rem;
  color: #1d342c;
  line-height: 1.6;
  overflow-wrap: anywhere;
  word-break: break-word;
}

.document-list-dp-card-height {
  min-height: 400px;
}

@media (max-width: 600px) {
  .document-item {
    grid-template-columns: 1fr;
    align-items: flex-start;
  }

  .document-item-action {
    width: 100%;
  }
}
</style>
