<script lang="ts">
import { computed, defineComponent, onMounted, ref } from 'vue'
import { ChallengeDashboardSearchType } from '@/enums/SearchTypes'
import { get } from '@/utils/GenericApiHelper.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { Characters, URIS } from '@/constants/DpConstants.ts'
import { isExcelGeneric, isHTML, isPdfGeneric, isTheTypeIsImageGeneric, isWordGeneric } from '@/utils/fileUtils.ts'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import { VideosDashboardResponse } from '@/dto/video/VideoResponse.ts'
import { useI18n } from 'vue-i18n'
import { resolveLocalizedValue } from '@/utils/localizationUtils'

interface Document {
  id: number
  name: string
  path: string
  type: string
  documentFile: string
}

interface Folder {
  challengeId: string | null
  documentsResponseList: Document[]
}

export default defineComponent({
  name: 'SearchComponent',
  components: { EmptyDataTable },
  props: {
    initialSearchType: {
      type: String,
      default: ChallengeDashboardSearchType.INTERNAL
    },
    initialSearchQuery: {
      type: String,
      default: ''
    },
    isCurrentUserChallengeModerator: {
      type: Boolean,
      default: false
    },
    challengeId: {
      type: String,
      default: ''
    },
    isFinalDecision: {
      type: Boolean,
      required: true
    }
  },
  setup(props) {
    const { locale } = useI18n()
    const searchType = ref(props.initialSearchType)
    const searchQuery = ref(props.initialSearchQuery)
    const folders = ref<Folder[]>([])
    const videoFolders = ref<VideosDashboardResponse[]>([])
    const selectedVideoId = ref<string | null>(null)
    const selectedVideoTitle = ref<string>('')
    const dialog = ref(false)

    // Fetch data when a component mounts
    onMounted(async () => {
      await fetchDocuments()
      await fetchYouTubeVideos()
    })

    const fetchDocuments = async () => {
      try {
        let response: SuccessResponse<Folder[]>
        console.log(props.isCurrentUserChallengeModerator)
        if (props.isCurrentUserChallengeModerator) {
          response = await get<SuccessResponse<Folder[]>>(URIS.DOCUMENTS.BASE)
        } else {
          response = await get<SuccessResponse<Folder[]>>(
            URIS.DOCUMENTS.BASE + Characters.QUESTION_MARK + 'challengeId=' + props.challengeId
          )
        }
        if (response.success) {
          // Transform response to a list of folders
          const docs = response.data
          folders.value = docs.map((folder: any) => ({
            challengeId: folder.challengeId,
            documentsResponseList: folder.documentsResponseList
          }))
        } else {
          folders.value = []
        }
      } catch (error) {
        console.error('Error fetching internal documents:', error)
        folders.value = []
      }
    }

    const fetchYouTubeVideos = async () => {
      try {
        let response: SuccessResponse<VideosDashboardResponse[]>
        console.log(props.isCurrentUserChallengeModerator)
        if (props.isCurrentUserChallengeModerator) {
          response = (await get<SuccessResponse<VideosDashboardResponse[]>>(
            URIS.YOUTUBE_VIDEOS.BASE
          )) as SuccessResponse<VideosDashboardResponse[]>
        } else {
          response = (await get<SuccessResponse<VideosDashboardResponse[]>>(
            URIS.YOUTUBE_VIDEOS.BASE + Characters.QUESTION_MARK + 'challengeId=' + props.challengeId
          )) as SuccessResponse<VideosDashboardResponse[]>
        }
        if (response.success) {
          // Transform response to a list of folders
          const videos = response.data
          videoFolders.value = videos.map((folder: any) => ({
            challengeId: folder.challengeId,
            videosResponseList: folder.videosResponseList
          }))
        } else {
          videoFolders.value = []
        }
      } catch (error) {
        console.error('Error fetching YouTube videos:', error)
        videoFolders.value = []
      }
    }

    const setSearchType = (type: ChallengeDashboardSearchType) => {
      searchType.value = type
      performSearch() // Re-run search when changing a type
    }

    const performSearch = () => {
      if (searchType.value === ChallengeDashboardSearchType.YOUTUBE) {
        searchInYoutubeLinks()
      } else {
        searchInternal()
      }
    }

    const getYouTubeThumbnailUrl = (link: string) => {
      const videoId = link.split('v=')[1]
      return `https://img.youtube.com/vi/${videoId}/hqdefault.jpg`
    }

    const openDialog = (link: string) => {
      selectedVideoId.value = link.split('v=')[1]
      dialog.value = true
    }

    const searchInternal = () => {
      // Ensure a search is performed after data is fetched
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filteredFolders.value = folders.value.map((folder) => ({
          ...folder,
          documentsResponseList: folder.documentsResponseList.filter((doc) => doc.name.toLowerCase().includes(query))
        }))
      } else {
        filteredFolders.value = folders.value
      }
    }

    const searchInYoutubeLinks = () => {
      // Ensure a search is performed after data is fetched
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filteredVideoFolders.value = videoFolders.value.map((folder) => ({
          ...folder,
          videosResponseList: folder.videosResponseList.filter((video: any) =>
            video.link.toLowerCase().includes(query)
          )
        }))
      } else {
        filteredVideoFolders.value = videoFolders.value
      }
    }

    const getImageUrl = (documentFile: string) => {
      // Logic to generate the image URL from documentFile if needed
      return `data:image/png;base64,${documentFile}`
    }

    const downloadFile = (fileName: string, base64Data: string, fileType: string) => {
      // Decode the Base64 string into a binary string
      const byteCharacters = atob(base64Data)

      // Create an array of 8-bit unsigned integers
      const byteNumbers = new Array(byteCharacters.length)
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i)
      }

      const byteArray = new Uint8Array(byteNumbers)

      // Create a Blob object from the byteArray
      const blob = new Blob([byteArray], {
        type: `application/${fileType}`
      })

      // Create a temporary URL for the Blob
      const blobUrl = URL.createObjectURL(blob)

      // Create a temporary link element
      const link = document.createElement('a')
      link.href = blobUrl
      link.download = fileName

      // Programmatically click the link to trigger the download
      link.click()

      // Clean up the temporary URL
      URL.revokeObjectURL(blobUrl)
    }

    const getFileIcon = (fileType: string) => {
      switch (fileType) {
        case 'pdf':
          return 'mdi-file-pdf'
        case 'doc':
        case 'docx':
          return 'mdi-file-word'
        case 'xlsx':
          return 'mdi-file-excel'
        case 'jpg':
        case 'png':
          return 'mdi-file-image'
        default:
          return 'mdi-file'
      }
    }

    const getChallengeSearchText = (challenge: any) => {
      const localizedTitle = resolveLocalizedValue({
        locale: locale.value,
        ar: challenge?.titleAr,
        en: challenge?.titleEn,
        fallback: ''
      })

      return [localizedTitle, challenge?.titleAr, challenge?.titleEn]
        .filter((value): value is string => typeof value === 'string' && value.trim().length > 0)
        .join(' ')
        .toLowerCase()
    }

    const filteredFolders = computed(() => {
      if (!searchQuery.value) return folders.value
      return folders.value.map((folder) => ({
        ...folder,
        documentsResponseList: folder.documentsResponseList.filter((doc) =>
          doc.name.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }))
    })

    const filteredVideoFolders = computed(() => {
      if (!searchQuery.value) return videoFolders.value
      const normalizedQuery = searchQuery.value.toLowerCase()

      return videoFolders.value.map((folder) => ({
        ...folder,
        videosResponseList: folder.videosResponseList.filter((video: any) =>
          getChallengeSearchText(video.challenges).includes(normalizedQuery)
        )
      }))
    })

    return {
      searchType,
      searchQuery,
      folders,
      setSearchType,
      performSearch,
      getImageUrl,
      downloadFile,
      getFileIcon,
      filteredFolders,
      isTheTypeIsImageGeneric,
      isHTML,
      isPdfGeneric,
      isWordGeneric,
      isExcelGeneric,
      getYouTubeThumbnailUrl,
      videoFolders,
      filteredVideoFolders,
      selectedVideoId,
      selectedVideoTitle,
      openDialog,
      dialog
    }
  },
  computed: {
    ChallengeDashboardSearchType() {
      return ChallengeDashboardSearchType
    }
  }
})
</script>

<template src="./SearchCard.html"></template>

<style src="./SearchCard.scss" scoped></style>
