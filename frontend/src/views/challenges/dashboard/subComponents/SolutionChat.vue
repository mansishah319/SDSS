<script lang="ts">
import { nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { createWithActiveSocket, get, uploadFile } from '@/utils/GenericApiHelper'
import { useRouter } from 'vue-router'
import { WebsocketClient } from '@/utils/WebsocketClient'
import { Characters, Fields, URIS } from '@/constants/DpConstants'
import { ChatHistory } from '@/dto/challenge/ChatHistory'
import { SuccessResponse } from '@/dto/generic/SuccessResponse'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import Employee from '@/dto/Employee'
import SpeakerBubble from '@/components/chat/SpeakerBubble.vue'
import SpeakerBubbleOther from '@/components/chat/SpeakerBubbleOther.vue'
import { getEmployeeData } from '@/utils/roleUtils'
import { downloadExcelFile, downloadFile } from '@/utils/api-utils'
import { getFileIconAndColor, getFileSize, isExcelFile, isImageGeneric, isPdfGeneric } from '@/utils/fileUtils'

export default {
  components: { SpeakerBubble, SpeakerBubbleOther },
  props: {
    isFinalDecision: {
      type: Boolean,
      required: true
    },
    disableAutoScroll: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const { t } = useI18n()
    const isLoading = ref(false)
    const newMessage = ref('')
    const messages = ref<any[]>([])
    const uploadedFiles = ref<File[]>([])
    const challengeId = ref<string | null>(null)
    let subscription: any = null
    const chatHistory = ref<any[]>([])

    const showAttachmentPreview = ref(false)
    const previewFileName = ref('')
    const previewFileSize = ref<number>(0)
    const previewImageUrl = ref('')
    const isPreviewImage = ref(false)
    const currentUserLang = window.localStorage.getItem(Fields.EMPLOYEE_LANG)

    const previewFile = ref<{
      file?: string
      type?: string
      fileName?: string
    } | null>(null)

    const showPreview = ref(false)

    // To get a selected employee
    const selectedEmployee = ref<Employee | null>(null)

    // Add ref for the message container
    const discussionMessages = ref<any>(null)
    const route = useRouter()
    const stompsocket = WebsocketClient.getInstance().getStompClient() // Declare stompsocket here
    let pendingScrollFrame: number | null = null

    const getDiscussionMessagesElement = (): HTMLElement | null => {
      const element = discussionMessages.value
      if (!element) {
        return null
      }
      return (element.$el as HTMLElement) || (element as HTMLElement)
    }

    // Keep chat scrolling inside its own container so the page position does not jump.
    const scrollToBottom = (behavior: ScrollBehavior = 'smooth') => {
      if (props.disableAutoScroll) {
        return
      }
      nextTick(() => {
        const element = getDiscussionMessagesElement()
        if (!element) {
          return
        }

        if (pendingScrollFrame !== null) {
          cancelAnimationFrame(pendingScrollFrame)
        }

        pendingScrollFrame = requestAnimationFrame(() => {
          pendingScrollFrame = null
          element.scrollTop = element.scrollHeight
          if (behavior === 'smooth') {
            element.scrollTo({
              top: element.scrollHeight,
              behavior
            })
          }
        })
      })
    }

    const handleSocketConnected = () => {
      subscribeToMessages()
    }

    const createChatMessage = async () => {
      if (!newMessage.value.trim()) {
        toast.error(t('error.chatMessageBlank'))
        return
      }
      try {
        const response = await createWithActiveSocket<any>(
          URIS.CHAT_MESSAGES.BASE +
          Characters.QUESTION_MARK +
          'message=' +
          newMessage.value +
          '&challengeId=' +
          challengeId.value,
          null
        )

        if (response.data.success) {
          newMessage.value = '' // Clear input field
        }
      } catch (error) {
        console.error('Error submitting answer:', error)
      }
    }

    const sendFilesAsMessage = async () => {
      try {
        if (!challengeId.value || uploadedFiles.value.length === 0) {
          return
        }

        const formData = new FormData()
        formData.append('challengeId', challengeId.value)

        const additionalData = { challengeId: challengeId.value }
        const response = await uploadFile<SuccessResponse<any>>(
          URIS.CHAT_MESSAGES.BASE + URIS.CHAT_MESSAGES.UPLOAD_FILE,
          uploadedFiles.value[0],
          additionalData
        )

        if (response?.success) {
          newMessage.value = ''
          uploadedFiles.value = []
          closePreview()
        }
      } catch (error) {
        console.error('Error submitting answer:', error)
      }
    }

    const fileInput = ref<HTMLInputElement | null>(null)

    const triggerFileInput = () => {
      if (fileInput.value) {
        fileInput.value.click()
      }
    }

    const handleFileInputChange = (event: Event) => {
      const target = event.target as HTMLInputElement
      const files = target.files

      if (files && files.length > 0) {
        const fileArray = Array.from(files)
        if (fileArray.length <= 1 && uploadedFiles.value.length <= 1) {
          uploadedFiles.value.push(...fileArray)
          createPreviewData(fileArray)
          showAttachmentPreview.value = true
        } else {
          toast.error(t('error.challengeChatAllowedFiles'))
        }
      }

      // Reset the file input value
      if (target) {
        target.value = ''
      }
    }

    const handleFileUpload = (file: []) => {
      if (file.length <= 1 && uploadedFiles.value.length <= 1) {
        uploadedFiles.value.push(...file)
      } else {
        toast.error(t('error.challengeChatAllowedFiles'))
        return
      }
      if (!file) {
        return
      }
      createPreviewData(file)
      showAttachmentPreview.value = true
      // Reset the file input value after handling the file upload
    }

    const createPreviewData = (file: any) => {
      file = file[0]
      const supportedImageTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/svg+xml']
      if (supportedImageTypes.includes(file.type)) {
        const reader = new FileReader()

        reader.onload = (e: any) => {
          previewImageUrl.value = e.target.result
        }
        isPreviewImage.value = true
        reader.readAsDataURL(file)
      }
      previewFileSize.value = file.size / 1000000
      previewFileName.value = file.name
    }

    const removeFile = (index: number) => {
      uploadedFiles.value.splice(index, 1)
    }

    const loadChatHistory = async (challengeId: string) => {
      try {
        isLoading.value = true
        const response = await get<SuccessResponse<ChatHistory>>(
          URIS.CHAT_MESSAGES.BASE +
          URIS.CHAT_MESSAGES.GET_BY_CHALLENGE_ID +
          Characters.QUESTION_MARK +
          'challengeId=' +
          challengeId
        )

        if (response?.success) {
          chatHistory.value = response.data as unknown as any[]
          if (Array.isArray(response.data)) {
            messages.value = [...response.data]
          }
        } else {
          console.error('API request was not successful for loadChatHistory:', response?.message)
          // Handle error scenario
        }
      } catch (error) {
        console.error('Error loading chat history data:', error)
        // Handle error scenario
      } finally {
        isLoading.value = false
      }
    }

    // Watch for new messages and auto-scroll
    watch(
      () => messages.value.length,
      () => {
        scrollToBottom('auto')
      }
    )

    onMounted(async () => {
      const params = route.currentRoute.value.params
      challengeId.value = params.challengeId as string

      const employeeData = localStorage.getItem(Fields.LOGGED_IN_EMP)
      selectedEmployee.value = employeeData ? JSON.parse(employeeData) : null

      if (stompsocket?.connected) {
        subscribeToMessages()
      }

      // Set the callback function to be triggered on WebSocket connection
      WebsocketClient.getInstance().setOnConnectCallback(handleSocketConnected)

      if (challengeId.value) {
        await loadChatHistory(challengeId.value)
      }
    })

    const handleOutsideClick = () => {
      closePreview()
    }
    const closePreview = () => {
      uploadedFiles.value = []
      isPreviewImage.value = false
      showAttachmentPreview.value = false
    }

    onUnmounted(() => {
      if (pendingScrollFrame !== null) {
        cancelAnimationFrame(pendingScrollFrame)
        pendingScrollFrame = null
      }
      if (subscription) {
        subscription.unsubscribe()
        subscription = null
      }
      WebsocketClient.getInstance().removeOnConnectCallback(handleSocketConnected)
    })

    const subscribeToMessages = () => {
      if (stompsocket && !subscription) {
        subscription = stompsocket.subscribe('/topic/chat', (message: any) => {
          const messageObject = JSON.parse(message.body)
          messages.value.push(messageObject.body.data)
          newMessage.value = ''
        })
      }
    }

    const isImage = (fileType: string) => {
      const imageTypes = [
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
        'rw2'
      ]
      return imageTypes.includes(fileType)
    }

    const isCurrentUser = (message: any) => {
      try {
        if (!selectedEmployee.value) return false
        const currentEmployee = getEmployeeData()
        if (!currentEmployee) {
          return false
        }
        return Number(currentEmployee?.grpNumber) === Number(message.createdBy)
      } catch (error) {
        console.error('Error parsing employee data:', error)
        return false
      }
    }

    const isTheCurrentComponentSpeakerBubbleOther = (message: any) => {
      const componentType = isCurrentUser(message) ? 'SpeakerBubble' : 'SpeakerBubbleOther'
      return componentType === 'SpeakerBubbleOther'
    }

    // Helper: get time string in correct locale
    function getTimeString(date: Date, lang: string) {
      const timeOptions: Intl.DateTimeFormatOptions = {
        hour: '2-digit',
        minute: '2-digit',
        hour12: lang !== 'ar'
      }
      return date.toLocaleTimeString(lang === 'ar' ? 'ar-SA' : 'en-US', timeOptions)
    }

    // Helper: get date string in correct locale
    function getDateString(date: Date, lang: string) {
      const dateOptions: Intl.DateTimeFormatOptions = {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: lang !== 'ar'
      }
      return date.toLocaleDateString(lang === 'ar' ? 'ar-SA' : 'en-US', dateOptions)
    }

    // Helper: get relative time string
    function getRelativeTime(
      diffInSeconds: number,
      diffInMinutes: number,
      diffInHours: number,
      diffInDays: number,
      lang: string
    ) {
      const timeCases = [
        {
          check: () => diffInSeconds < 60,
          get: () => (lang === 'ar' ? 'الآن' : 'Just now')
        },
        {
          check: () => diffInMinutes < 60,
          get: () => {
            const minutesText = lang === 'ar' ? 'دقيقة' : 'm'
            return lang === 'ar' ? `منذ ${diffInMinutes} ${minutesText}` : `${diffInMinutes}${minutesText} ago`
          }
        },
        {
          check: () => diffInHours < 24,
          get: () => {
            const hoursText = lang === 'ar' ? 'ساعة' : 'h'
            return lang === 'ar' ? `منذ ${diffInHours} ${hoursText}` : `${diffInHours}${hoursText} ago`
          }
        },
        {
          check: () => diffInDays < 7,
          get: () => {
            const daysText = lang === 'ar' ? 'يوم' : 'd'
            return lang === 'ar' ? `منذ ${diffInDays} ${daysText}` : `${diffInDays}${daysText} ago`
          }
        }
      ]
      for (const timeCase of timeCases) {
        if (timeCase.check()) return timeCase.get()
      }
      return null
    }

    // Refactored formatMessageTime
    const formatMessageTime = (timestamp: string) => {
      if (!timestamp) return ''
      try {
        const messageDate = new Date(timestamp)
        const now = new Date()
        if (isNaN(messageDate.getTime())) {
          console.warn('Invalid timestamp:', timestamp)
          return ''
        }
        const diffInMs = now.getTime() - messageDate.getTime()
        const diffInSeconds = Math.floor(diffInMs / 1000)
        const diffInMinutes = Math.floor(diffInSeconds / 60)
        const diffInHours = Math.floor(diffInMinutes / 60)
        const diffInDays = Math.floor(diffInHours / 24)
        const lang = currentUserLang || 'en'
        const timeString = getTimeString(messageDate, lang)
        if (diffInMs < 0) return timeString
        const isToday = messageDate.toDateString() === now.toDateString()
        const isYesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000).toDateString() === messageDate.toDateString()
        const relative = getRelativeTime(diffInSeconds, diffInMinutes, diffInHours, diffInDays, lang)
        if (relative) return relative
        if (isToday) return timeString
        if (isYesterday) return lang === 'ar' ? `أمس ${timeString}` : `Yesterday ${timeString}`
        return getDateString(messageDate, lang)
      } catch (error) {
        console.error('Error formatting message time:', error, timestamp)
        return ''
      }
    }

    // Open image preview
    const openImagePreview = (message: any) => {
      if (message.file && isImage(message.type)) {
        const imageUrl = 'data:image/' + message.type + ';base64,' + message.file
        // You can implement a full-screen image preview here
        window.open(imageUrl, '_blank')
      }
    }

    const openFilePreview = (message: any) => {
      if (!message || !message.file) {
        console.error('Invalid message for preview:', message)
        return
      }

      previewFile.value = message
      showPreview.value = true

      // For PDFs, ensure the data URL is properly formatted
      if (isPdfGeneric(message.type)) {
        nextTick(() => {
          previewFile.value = { ...previewFile.value! } // Non-null assertion
        })
      }
    }

    return {
      newMessage,
      uploadedFiles,
      messages,
      challengeId,
      createChatMessage,
      handleFileUpload,
      removeFile,
      showAttachmentPreview,
      previewFileName,
      previewFileSize,
      previewImageUrl,
      isPreviewImage,
      closePreview,
      sendFilesAsMessage,
      selectedEmployee,
      isImage,
      isCurrentUser,
      handleOutsideClick,
      currentUserLang,
      isTheCurrentComponentSpeakerBubbleOther,
      formatMessageTime,
      openImagePreview,
      fileInput,
      triggerFileInput,
      handleFileInputChange,
      discussionMessages,
      previewFile,
      showPreview,
      openFilePreview,
      isLoading
    }
  },
  methods: {
    isPdfGeneric,
    isImageGeneric,
    isExcelFile,

    getFileIcon(type) {
      return getFileIconAndColor(type).icon
    },

    getFileIconColor(type) {
      return getFileIconAndColor(type).color
    },

    handleFileDownload(message) {
      if (isExcelFile(message.type)) {
        downloadExcelFile(message)
      } else {
        downloadFile(message)
      }
    },

    getFileSizeFormatted(file) {
      return `${getFileSize(file.file)} MB`
    },

    getImageDataUrl(message: any) {
      const mimeMap: Record<string, string> = {
        jpg: 'jpeg',
        jpeg: 'jpeg',
        png: 'png',
        gif: 'gif',
        svg: 'svg+xml',
        webp: 'webp',
        bmp: 'bmp',
        tiff: 'tiff'
      }
      const ext = message.type.toLowerCase()
      const mimeType = mimeMap[ext] || ext
      return `data:image/${mimeType};base64,${message.file}`
    },
    getPdfDataUrl(message: any) {
      return `data:application/pdf;base64,${message.file}`
    },
    downloadFile(file: any) {
      const blob = this.base64ToBlob(file.file, file.type)
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = file.fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    },
    base64ToBlob(base64: string, type: string) {
      const byteString = atob(base64)
      const arrayBuffer = new ArrayBuffer(byteString.length)
      const uintArray = new Uint8Array(arrayBuffer)

      for (let i = 0; i < byteString.length; i++) {
        uintArray[i] = byteString.charCodeAt(i)
      }

      return new Blob([arrayBuffer], { type: this.getMimeType(type) })
    },

    getMimeType(type: string) {
      const typeMap: Record<string, string> = {
        jpg: 'image/jpeg',
        jpeg: 'image/jpeg',
        png: 'image/png',
        gif: 'image/gif',
        pdf: 'application/pdf',
        svg: 'image/svg+xml',
        doc: 'application/msword',
        docx: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        xls: 'application/vnd.ms-excel',
        xlsx: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        txt: 'text/plain'
      }
      return typeMap[type.toLowerCase()] || 'application/octet-stream'
    }
  }
}
</script>

<template src="./SolutionChat.html"></template>
<style src="./SolutionChat.scss" lang="scss"></style>
