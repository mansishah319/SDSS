import { computed, type ComputedRef, nextTick, ref } from 'vue'
import { buildPdfExportStyles, exportElementToPdf } from '@/utils/pdfExportService'
import {
  buildPdfFileName,
  createCommonPdfCloneHandler,
  createPdfFontResources,
  sanitizePdfFileSegment,
  sanitizeUnsupportedColorFunctionsInClone
} from '@/utils/pdfExportUtils'

type UseSurveyDashboardPdfOptions = {
  isArabic: ComputedRef<boolean>
  reportLabel: ComputedRef<string>
  localizedTitle: ComputedRef<string>
}

const pdfScopeSelector = '#survey-dashboard-export-root'
const bukraRegularFontUrl = new URL('../../../assets/fonts/Bukra/TTF/29ltbukraregular.ttf', import.meta.url).href
const bukraBoldFontUrl = new URL('../../../assets/fonts/Bukra/TTF/29ltbukrabold.ttf', import.meta.url).href
const dubaiRegularFontUrl = new URL('../../../assets/fonts/Dubai/DubaiRegular.ttf', import.meta.url).href

const colorPattern = /rgba?\(([^)]+)\)/i

const captureVideoFrameAsDataUrl = (videoElement: HTMLVideoElement) => {
  if (!videoElement.videoWidth || !videoElement.videoHeight) {
    return undefined
  }

  const canvas = document.createElement('canvas')
  canvas.width = videoElement.videoWidth
  canvas.height = videoElement.videoHeight

  const context = canvas.getContext('2d')
  if (!context) {
    return undefined
  }

  context.drawImage(videoElement, 0, 0, canvas.width, canvas.height)
  return canvas.toDataURL('image/png')
}

const captureVideoFrameToCanvas = (videoElement: HTMLVideoElement) => {
  if (!videoElement.videoWidth || !videoElement.videoHeight) {
    return undefined
  }

  const canvas = document.createElement('canvas')
  canvas.width = videoElement.videoWidth
  canvas.height = videoElement.videoHeight

  const context = canvas.getContext('2d')
  if (!context) {
    return undefined
  }

  context.drawImage(videoElement, 0, 0, canvas.width, canvas.height)
  return canvas
}

const loadVideoPosterFromSource = (src: string) =>
  new Promise<string | undefined>((resolve) => {
    const tempVideo = document.createElement('video')

    const cleanup = () => {
      tempVideo.pause()
      tempVideo.removeAttribute('src')
      tempVideo.load()
    }

    const handleFailure = () => {
      cleanup()
      resolve(undefined)
    }

    tempVideo.muted = true
    tempVideo.playsInline = true
    tempVideo.preload = 'auto'

    tempVideo.addEventListener(
      'loadeddata',
      () => {
        requestAnimationFrame(() => {
          try {
            const poster = captureVideoFrameAsDataUrl(tempVideo)
            cleanup()
            resolve(poster)
          } catch {
            handleFailure()
          }
        })
      },
      { once: true }
    )

    tempVideo.addEventListener('error', handleFailure, { once: true })
    tempVideo.src = src
    tempVideo.load()
  })

const loadVideoFrameCanvasFromSource = (src: string) =>
  new Promise<HTMLCanvasElement | undefined>((resolve) => {
    const tempVideo = document.createElement('video')

    const cleanup = () => {
      tempVideo.pause()
      tempVideo.removeAttribute('src')
      tempVideo.load()
    }

    const handleFailure = () => {
      cleanup()
      resolve(undefined)
    }

    tempVideo.muted = true
    tempVideo.playsInline = true
    tempVideo.preload = 'auto'

    tempVideo.addEventListener(
      'loadeddata',
      () => {
        requestAnimationFrame(() => {
          try {
            const canvas = captureVideoFrameToCanvas(tempVideo)
            cleanup()
            resolve(canvas)
          } catch {
            handleFailure()
          }
        })
      },
      { once: true }
    )

    tempVideo.addEventListener('error', handleFailure, { once: true })
    tempVideo.src = src
    tempVideo.load()
  })

const preloadSurveyVideoPosters = async (rootElement: HTMLElement) => {
  const videoElements = Array.from(rootElement.querySelectorAll<HTMLVideoElement>('video'))

  await Promise.all(
    videoElements.map(async (videoElement) => {
      const existingPoster = captureVideoFrameAsDataUrl(videoElement)
      if (existingPoster) {
        videoElement.dataset.pdfPoster = existingPoster
        return
      }

      const sourceUrl =
        videoElement.currentSrc || videoElement.querySelector('source')?.src || videoElement.getAttribute('src') || ''

      if (!sourceUrl) {
        return
      }

      const poster = await loadVideoPosterFromSource(sourceUrl)
      if (poster) {
        videoElement.dataset.pdfPoster = poster
      }
    })
  )
}

const replaceSurveyVideosForPdf = (doc: Document, sourceElement: HTMLElement, clonedElement: HTMLElement) => {
  const sourceVideos = Array.from(sourceElement.querySelectorAll<HTMLVideoElement>('video'))
  const clonedVideos = Array.from(clonedElement.querySelectorAll<HTMLVideoElement>('video'))

  clonedVideos.forEach((clonedVideo, index) => {
    const sourceVideo = sourceVideos[index]
    const posterSrc = sourceVideo?.dataset.pdfPoster

    if (!posterSrc) {
      return
    }

    const previewImage = doc.createElement('img')
    previewImage.className = 'survey-pdf-video-preview'
    previewImage.src = posterSrc
    previewImage.alt = sourceVideo.getAttribute('aria-label') || `Preview ${index + 1}`

    const inlineWidth = sourceVideo.style.width
    if (inlineWidth) {
      previewImage.style.width = inlineWidth
    }

    clonedVideo.replaceWith(previewImage)
  })
}

const replaceSourceVideosWithPdfCanvases = async (rootElement: HTMLElement) => {
  const videoElements = Array.from(rootElement.querySelectorAll<HTMLVideoElement>('video'))
  const replacements = await Promise.all(
    videoElements.map(async (videoElement) => {
      let previewCanvas = captureVideoFrameToCanvas(videoElement)

      if (!previewCanvas) {
        const sourceUrl =
          videoElement.currentSrc || videoElement.querySelector('source')?.src || videoElement.getAttribute('src') || ''

        if (sourceUrl) {
          previewCanvas = await loadVideoFrameCanvasFromSource(sourceUrl)
        }
      }

      if (!previewCanvas) {
        return undefined
      }

      previewCanvas.className = 'survey-pdf-video-preview'
      previewCanvas.style.display = 'block'
      previewCanvas.style.margin = '0 auto'
      previewCanvas.style.maxWidth = '100%'
      previewCanvas.style.height = 'auto'

      const inlineWidth = videoElement.style.width || videoElement.getAttribute('width')
      if (inlineWidth) {
        previewCanvas.style.width = /^\d+$/.test(inlineWidth) ? `${inlineWidth}px` : inlineWidth
      }

      videoElement.replaceWith(previewCanvas)

      return {
        previewCanvas,
        videoElement
      }
    })
  )

  return () => {
    replacements.forEach((entry) => {
      entry?.previewCanvas.replaceWith(entry.videoElement)
    })
  }
}

const normalizeColorToRgbTuple = (value: string) => {
  const normalizedValue = value.trim()
  if (!normalizedValue) {
    return undefined
  }

  if (normalizedValue.startsWith('#')) {
    const hex = normalizedValue.slice(1)
    const expandedHex =
      hex.length === 3
        ? hex
          .split('')
          .map((segment) => `${segment}${segment}`)
          .join('')
        : hex.slice(0, 6)

    if (expandedHex.length !== 6) {
      return undefined
    }

    const red = parseInt(expandedHex.slice(0, 2), 16)
    const green = parseInt(expandedHex.slice(2, 4), 16)
    const blue = parseInt(expandedHex.slice(4, 6), 16)

    return `${red}, ${green}, ${blue}`
  }

  const rgbMatch = normalizedValue.match(colorPattern)
  if (!rgbMatch?.[1]) {
    return undefined
  }

  const [red, green, blue] = rgbMatch[1].split(',').map((segment) => segment.trim())
  if (!red || !green || !blue) {
    return undefined
  }

  return `${red}, ${green}, ${blue}`
}

const applySurveyPdfColorVariables = (rootElement: HTMLElement) => {
  const accentSelectors = [
    '.survey-dashboard-kpi-card',
    '.survey-dashboard-panel',
    '.survey-user-dashboard__spotlight-icon',
    '.survey-user-dashboard__activity-icon'
  ]

  const toneSelectors = ['.survey-dashboard-list']

  accentSelectors.forEach((selector) => {
    rootElement.querySelectorAll<HTMLElement>(selector).forEach((element) => {
      const accentColor = getComputedStyle(element).getPropertyValue('--accent')
      const accentRgb = normalizeColorToRgbTuple(accentColor)
      if (accentRgb) {
        element.style.setProperty('--pdf-accent-rgb', accentRgb)
      }
    })
  })

  toneSelectors.forEach((selector) => {
    rootElement.querySelectorAll<HTMLElement>(selector).forEach((element) => {
      const toneColor = getComputedStyle(element).getPropertyValue('--tone')
      const toneRgb = normalizeColorToRgbTuple(toneColor)
      if (toneRgb) {
        element.style.setProperty('--pdf-tone-rgb', toneRgb)
      }
    })
  })
}

const applySurveyPdfInlineFallbacks = (rootElement: HTMLElement) => {
  rootElement.querySelectorAll<HTMLElement>('.survey-dashboard-panel').forEach((element) => {
    const accentRgb = element.style.getPropertyValue('--pdf-accent-rgb') || '21, 112, 239'
    element.style.background = '#ffffff'
    element.style.borderColor = `rgba(${accentRgb}, 0.12)`
  })

  rootElement.querySelectorAll<HTMLElement>('.survey-dashboard-panel__icon').forEach((element) => {
    const accentRgb =
      element.style.getPropertyValue('--pdf-accent-rgb') ||
      element.parentElement?.style.getPropertyValue('--pdf-accent-rgb') ||
      '21, 112, 239'
    element.style.background = `rgba(${accentRgb}, 0.12)`
    element.style.color = `rgb(${accentRgb})`
  })

  rootElement.querySelectorAll<HTMLElement>('.survey-dashboard-kpi-card').forEach((element) => {
    const accentRgb = element.style.getPropertyValue('--pdf-accent-rgb') || '21, 112, 239'
    element.style.background = 'linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(246, 250, 251, 0.98) 100%)'
    element.style.borderColor = `rgba(${accentRgb}, 0.16)`
    element.style.boxShadow = '0 18px 48px rgba(16, 24, 40, 0.08)'
  })

  rootElement.querySelectorAll<HTMLElement>('.survey-dashboard-kpi-card__icon').forEach((element) => {
    const accentRgb =
      element.closest<HTMLElement>('.survey-dashboard-kpi-card')?.style.getPropertyValue('--pdf-accent-rgb') ||
      '21, 112, 239'
    element.style.background = `rgb(${accentRgb})`
    element.style.color = '#ffffff'
  })

  rootElement.querySelectorAll<HTMLElement>('.survey-dashboard-kpi-card__badge').forEach((element) => {
    const accentRgb =
      element.closest<HTMLElement>('.survey-dashboard-kpi-card')?.style.getPropertyValue('--pdf-accent-rgb') ||
      '21, 112, 239'
    element.style.background = `rgba(${accentRgb}, 0.1)`
    element.style.color = `rgb(${accentRgb})`
  })

  rootElement.querySelectorAll<HTMLElement>('.survey-dashboard-kpi-card__fill').forEach((element) => {
    const accentRgb =
      element.closest<HTMLElement>('.survey-dashboard-kpi-card')?.style.getPropertyValue('--pdf-accent-rgb') ||
      '21, 112, 239'
    element.style.background = `rgb(${accentRgb})`
  })

  rootElement.querySelectorAll<HTMLElement>('.survey-dashboard-list').forEach((element) => {
    const toneRgb = element.style.getPropertyValue('--pdf-tone-rgb') || '21, 112, 239'
    element.style.background = 'linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.98) 100%)'
    element.style.borderColor = `rgba(${toneRgb}, 0.14)`
  })

  rootElement.querySelectorAll<HTMLElement>('.survey-dashboard-list__count').forEach((element) => {
    const toneRgb =
      element.closest<HTMLElement>('.survey-dashboard-list')?.style.getPropertyValue('--pdf-tone-rgb') || '21, 112, 239'
    element.style.background = `rgba(${toneRgb}, 0.12)`
    element.style.color = `rgb(${toneRgb})`
  })

  rootElement
    .querySelectorAll<HTMLElement>('.survey-user-dashboard__spotlight-icon, .survey-user-dashboard__activity-icon')
    .forEach((element) => {
      const accentRgb =
        element.style.getPropertyValue('--pdf-accent-rgb') ||
        element.parentElement?.style.getPropertyValue('--pdf-accent-rgb') ||
        '21, 112, 239'
      element.style.background = `rgba(${accentRgb}, 0.12)`
      element.style.color = `rgb(${accentRgb})`
    })
}

export const useSurveyDashboardPdf = ({ isArabic, reportLabel, localizedTitle }: UseSurveyDashboardPdfOptions) => {
  const isPdfExporting = ref(false)
  const reportExportRoot = ref<HTMLElement | null>(null)
  const pdfFontResources = createPdfFontResources({
    bukraRegularFontUrl,
    bukraBoldFontUrl,
    dubaiRegularFontUrl
  })

  const handlePdfClone = createCommonPdfCloneHandler({
    isArabic: () => isArabic.value,
    wordSpacingSelectors: [
      '.generic-dashboard-header__title',
      '.generic-dashboard-header__subtitle',
      '.survey-user-dashboard__hero-copy h1',
      '.survey-user-dashboard__hero-copy p',
      '.survey-dashboard-panel__title',
      '.survey-dashboard-panel__subtitle',
      '.survey-dashboard-kpi-card__title',
      '.survey-dashboard-kpi-card__value',
      '.survey-dashboard-list__title',
      '.survey-dashboard-list__item-copy h4',
      '.survey-dashboard-list__item-copy p',
      '.survey-user-dashboard__spotlight-copy strong',
      '.survey-user-dashboard__activity-copy strong'
    ],
    afterClone: ({ doc, sourceElement, clonedElement }) => {
      replaceSurveyVideosForPdf(doc, sourceElement, clonedElement)
      applySurveyPdfColorVariables(clonedElement)
      applySurveyPdfInlineFallbacks(clonedElement)
      sanitizeUnsupportedColorFunctionsInClone(doc, clonedElement)
    }
  })

  const pdfFileName = computed(() => {
    const reportName = sanitizePdfFileSegment(reportLabel.value, 'survey-dashboard')
    const title = sanitizePdfFileSegment(localizedTitle.value, 'survey-dashboard')

    return buildPdfFileName([reportName, title], 'survey-dashboard')
  })

  const pdfInjectedStyle = computed(() => {
    return buildPdfExportStyles({
      scopeSelector: pdfScopeSelector,
      fontFaces: pdfFontResources.fontFaces,
      baseFontFamily: isArabic.value ? '\'DubaiRegular\', sans-serif' : '\'bukraregular\', \'DubaiRegular\', sans-serif',
      direction: isArabic.value ? 'rtl' : 'ltr',
      textAlign: isArabic.value ? 'right' : 'left',
      unicodeBidi: 'plaintext',
      autoHeightSelectors: [
        `${pdfScopeSelector}`,
        `${pdfScopeSelector} .survey-user-dashboard__shell`,
        `${pdfScopeSelector} .survey-user-dashboard__chart-shell`,
        `${pdfScopeSelector} .survey-user-dashboard__chart-summary`,
        `${pdfScopeSelector} .survey-dashboard-list__items`,
        `${pdfScopeSelector} .apexcharts-canvas`,
        `${pdfScopeSelector} .apexcharts-svg`,
        `${pdfScopeSelector} svg`
      ],
      avoidBreakSelectors: [
        `${pdfScopeSelector} .generic-dashboard-header`,
        `${pdfScopeSelector} .survey-user-dashboard__hero-card`,
        `${pdfScopeSelector} .survey-dashboard-panel`,
        `${pdfScopeSelector} .survey-dashboard-kpi-card`,
        `${pdfScopeSelector} .survey-dashboard-list`,
        `${pdfScopeSelector} .survey-user-dashboard__spotlight-item`,
        `${pdfScopeSelector} .survey-user-dashboard__activity-item`,
        `${pdfScopeSelector} .survey-user-dashboard__moderator-stat`,
        `${pdfScopeSelector} .survey-user-dashboard__profile-item`
      ],
      hideSelectors: [
        `${pdfScopeSelector} .generic-dashboard-header__actions`,
        `${pdfScopeSelector} .survey-user-dashboard__hero-bar`,
        `${pdfScopeSelector} .survey-user-dashboard__footer-action`,
        `${pdfScopeSelector} .survey-user-dashboard__pdf-loader`,
        `${pdfScopeSelector} .survey-user-dashboard__inline-empty video`,
        `${pdfScopeSelector} .survey-dashboard-list__empty video`,
        `${pdfScopeSelector} .v-overlay-container`,
        `${pdfScopeSelector} .v-tooltip`
      ],
      customCss: `
${pdfScopeSelector} {
  background: #ffffff !important;
  color: #101828 !important;
  padding-bottom: 42px !important;
}

${pdfScopeSelector} .survey-user-dashboard__shell {
  gap: 20px !important;
}

${pdfScopeSelector} .generic-dashboard-header,
${pdfScopeSelector} .generic-dashboard-header__content,
${pdfScopeSelector} .generic-dashboard-header__kicker,
${pdfScopeSelector} .generic-dashboard-header__title,
${pdfScopeSelector} .generic-dashboard-header__subtitle {
  direction: ${isArabic.value ? 'rtl' : 'ltr'} !important;
  text-align: ${isArabic.value ? 'right' : 'left'} !important;
  unicode-bidi: isolate !important;
}

${pdfScopeSelector} .survey-user-dashboard__hero-grid,
${pdfScopeSelector} .survey-user-dashboard__analytics,
${pdfScopeSelector} .survey-user-dashboard__lists,
${pdfScopeSelector} .survey-user-dashboard__kpis,
${pdfScopeSelector} .survey-user-dashboard__profile-grid,
${pdfScopeSelector} .survey-user-dashboard__moderator-grid,
${pdfScopeSelector} .survey-user-dashboard__chart-summary,
${pdfScopeSelector} .survey-user-dashboard__activity-list {
  display: grid !important;
}

${pdfScopeSelector} .survey-user-dashboard__analytics > :last-child {
  grid-column: span 2 !important;
}

${pdfScopeSelector} .survey-dashboard-list,
${pdfScopeSelector} .survey-dashboard-panel,
${pdfScopeSelector} .survey-dashboard-kpi-card,
${pdfScopeSelector} .survey-user-dashboard__hero-card,
${pdfScopeSelector} .generic-dashboard-header {
  overflow: visible !important;
}

${pdfScopeSelector} .survey-dashboard-list__items {
  max-height: none !important;
  overflow: visible !important;
  padding: 0 !important;
}

${pdfScopeSelector} .survey-dashboard-list__item-top {
  display: grid !important;
  grid-template-columns: 1fr !important;
  align-items: start !important;
  gap: 10px !important;
}

${pdfScopeSelector} .survey-dashboard-list__item-copy {
  min-width: auto !important;
}

${pdfScopeSelector} .survey-dashboard-list__item-copy h4,
${pdfScopeSelector} .survey-dashboard-list__item-copy p {
  display: block !important;
  overflow: visible !important;
  text-overflow: clip !important;
  white-space: normal !important;
  word-break: break-word !important;
  -webkit-line-clamp: unset !important;
  -webkit-box-orient: initial !important;
}

${pdfScopeSelector} .survey-dashboard-list__item-copy p {
  max-height: none !important;
}

${pdfScopeSelector} .survey-dashboard-list__item .dp-chip,
${pdfScopeSelector} .survey-dashboard-list__item .v-chip {
  align-self: start !important;
  justify-self: start !important;
}

${pdfScopeSelector} .survey-pdf-video-preview {
  display: block !important;
  width: 200px !important;
  max-width: 100% !important;
  height: auto !important;
  object-fit: contain !important;
  margin: 0 auto !important;
}

${pdfScopeSelector} .survey-dashboard-panel__icon,
${pdfScopeSelector} .survey-user-dashboard__spotlight-icon,
${pdfScopeSelector} .survey-user-dashboard__activity-icon {
  background: rgba(var(--pdf-accent-rgb, 21, 112, 239), 0.12) !important;
  color: rgb(var(--pdf-accent-rgb, 21, 112, 239)) !important;
}

${pdfScopeSelector} .survey-dashboard-kpi-card {
  border-color: rgba(var(--pdf-accent-rgb, 21, 112, 239), 0.16) !important;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(246, 250, 251, 0.98) 100%),
    linear-gradient(135deg, rgba(var(--pdf-accent-rgb, 21, 112, 239), 0.08), transparent) !important;
}

${pdfScopeSelector} .survey-dashboard-kpi-card::after {
  content: none !important;
  display: none !important;
  background: none !important;
}

${pdfScopeSelector} .survey-dashboard-kpi-card__icon {
  background: linear-gradient(
    135deg,
    rgb(var(--pdf-accent-rgb, 21, 112, 239)),
    rgba(var(--pdf-accent-rgb, 21, 112, 239), 0.72)
  ) !important;
}

${pdfScopeSelector} .survey-dashboard-kpi-card__badge {
  background: rgba(var(--pdf-accent-rgb, 21, 112, 239), 0.1) !important;
  color: rgb(var(--pdf-accent-rgb, 21, 112, 239)) !important;
}

${pdfScopeSelector} .survey-dashboard-kpi-card__fill {
  background: linear-gradient(
    90deg,
    rgb(var(--pdf-accent-rgb, 21, 112, 239)),
    rgba(var(--pdf-accent-rgb, 21, 112, 239), 0.65)
  ) !important;
}

${pdfScopeSelector} .survey-dashboard-list {
  border-color: rgba(var(--pdf-tone-rgb, 21, 112, 239), 0.14) !important;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.98) 100%),
    linear-gradient(135deg, rgba(var(--pdf-tone-rgb, 21, 112, 239), 0.08), transparent) !important;
}

${pdfScopeSelector} .survey-dashboard-list__count {
  background: rgba(var(--pdf-tone-rgb, 21, 112, 239), 0.12) !important;
  color: rgb(var(--pdf-tone-rgb, 21, 112, 239)) !important;
}

${pdfScopeSelector} .survey-user-dashboard__chart-shell,
${pdfScopeSelector} .apexcharts-canvas,
${pdfScopeSelector} .apexcharts-svg {
  overflow: visible !important;
}

${pdfScopeSelector} .v-chip,
${pdfScopeSelector} .survey-user-dashboard__badge,
${pdfScopeSelector} .survey-dashboard-kpi-card__badge,
${pdfScopeSelector} .generic-dashboard-header__total-pill {
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  width: auto !important;
  max-width: none !important;
}

${pdfScopeSelector} .v-chip__content {
  white-space: nowrap !important;
}
`
    })
  })

  const handlePdfDownload = async () => {
    if (!reportExportRoot.value || isPdfExporting.value) {
      return
    }

    isPdfExporting.value = true
    let restoreSourceVideos = () => {
    }

    try {
      restoreSourceVideos = await replaceSourceVideosWithPdfCanvases(reportExportRoot.value)
      await nextTick()
      await preloadSurveyVideoPosters(reportExportRoot.value)

      await exportElementToPdf({
        target: reportExportRoot.value,
        fileName: pdfFileName.value,
        fontLoads: pdfFontResources.fontLoads,
        injectedStyle: pdfInjectedStyle.value,
        canvasScale: 2,
        beforeCapture: async () => {
          await nextTick()
        },
        onClone: handlePdfClone,
        pageChrome: {
          headerTitle: reportLabel.value,
          headerDescription: localizedTitle.value,
          fontLoads: pdfFontResources.fontLoads
        }
      })
    } catch (error) {
      console.error('Error exporting survey dashboard PDF', error)
    } finally {
      restoreSourceVideos()
      isPdfExporting.value = false
    }
  }

  return {
    handlePdfDownload,
    isPdfExporting,
    reportExportRoot
  }
}
