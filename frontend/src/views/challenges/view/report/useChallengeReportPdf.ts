import { computed, type ComputedRef, nextTick, ref, type Ref } from 'vue'
import { buildPdfExportStyles, exportElementToPdf } from '@/utils/pdfExportService'
import type { ChallengeReportApiResponse } from './challengeReport.types'
import {
  buildPdfFileName,
  createCommonPdfCloneHandler,
  createPdfFontResources,
  sanitizePdfFileSegment
} from '@/utils/pdfExportUtils'

type UseChallengeReportPdfOptions = {
  challenge: Ref<ChallengeReportApiResponse | null>
  isArabic: ComputedRef<boolean>
  localizedTitle: ComputedRef<string>
  reportLabel: ComputedRef<string>
}

const pdfScopeSelector = '#challenge-report-export-root'
const bukraRegularFontUrl = new URL('../../../../assets/fonts/Bukra/TTF/29ltbukraregular.ttf', import.meta.url).href
const bukraBoldFontUrl = new URL('../../../../assets/fonts/Bukra/TTF/29ltbukrabold.ttf', import.meta.url).href
const dubaiRegularFontUrl = new URL('../../../../assets/fonts/Dubai/DubaiRegular.ttf', import.meta.url).href

export const useChallengeReportPdf = ({
                                        challenge,
                                        isArabic,
                                        localizedTitle,
                                        reportLabel
                                      }: UseChallengeReportPdfOptions) => {
  const isPdfExporting = ref(false)
  const reportExportRoot = ref<HTMLElement | null>(null)
  const expandedStagePanels = ref<string[]>([])
  const pdfFontResources = createPdfFontResources({
    bukraRegularFontUrl,
    bukraBoldFontUrl,
    dubaiRegularFontUrl
  })
  const rafAnimationFrame = () => new Promise(requestAnimationFrame)

  const handlePdfClone = createCommonPdfCloneHandler({
    isArabic: () => isArabic.value,
    wordSpacingSelectors: [
      '.challenge-report-header__title',
      '.challenge-report-header__secondary',
      '.challenge-report-header__description',
      '.challenge-report-header__organization-copy strong',
      '.challenge-stage-card__solution-text',
      '.moderator-solution-card__title',
      '.challenge-voting-card__legend-label',
      '.metadata-tile__value',
      '.challenge-detail-item__value',
      '.challenge-hierarchy__content strong',
      '.challenge-participants-card__name',
      '.challenge-media-card__attachment-name'
    ]
  })

  const pdfFileName = computed(() => {
    const reportName = sanitizePdfFileSegment(reportLabel.value, 'challenge-report')
    const challengeName = sanitizePdfFileSegment(localizedTitle.value, 'challenge-report')

    return buildPdfFileName([reportName, challengeName], 'challenge-report')
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
        `${pdfScopeSelector} .v-card-text`,
        `${pdfScopeSelector} .v-row`,
        `${pdfScopeSelector} .v-col`,
        `${pdfScopeSelector} .challenge-stage-card`,
        `${pdfScopeSelector} .challenge-stage-card__body`,
        `${pdfScopeSelector} .challenge-stage-card__panels`,
        `${pdfScopeSelector} .challenge-stage-card__pdf-panels`,
        `${pdfScopeSelector} .challenge-stage-card__pdf-panel`,
        `${pdfScopeSelector} .challenge-stage-card__solutions-grid`,
        `${pdfScopeSelector} .v-expansion-panel-text`,
        `${pdfScopeSelector} .v-expansion-panel-text__wrapper`,
        `${pdfScopeSelector} .v-expansion-panel-text__wrap`,
        `${pdfScopeSelector} .apexcharts-canvas`,
        `${pdfScopeSelector} .apexcharts-svg`,
        `${pdfScopeSelector} svg`
      ],
      avoidBreakSelectors: [
        `${pdfScopeSelector} .challenge-report-header`,
        `${pdfScopeSelector} .challenge-details-card__hierarchy`,
        `${pdfScopeSelector} .challenge-details-card__group`,
        `${pdfScopeSelector} .challenge-voting-card`,
        `${pdfScopeSelector} .challenge-participants-card`,
        `${pdfScopeSelector} .challenge-media-card`,
        `${pdfScopeSelector} .challenge-stage-card__solution`,
        `${pdfScopeSelector} .moderator-solution-card`,
        `${pdfScopeSelector} .outcome-card`,
        `${pdfScopeSelector} .metadata-tile`
      ],
      hideSelectors: [
        `${pdfScopeSelector} .challenge-media-card .v-btn`,
        `${pdfScopeSelector} .v-tooltip`,
        `${pdfScopeSelector} .v-overlay-container`
      ],
      customCss: `
${pdfScopeSelector} {
  background: #ffffff !important;
  display: block !important;
  width: 100% !important;
  height: auto !important;
  min-height: 0 !important;
  max-height: none !important;
  overflow: visible !important;
  padding-bottom: 88px !important;
}

${pdfScopeSelector},
${pdfScopeSelector} > *,
${pdfScopeSelector} .challenge-report-screen__kpis,
${pdfScopeSelector} .challenge-report-screen__grid,
${pdfScopeSelector} .challenge-report-screen__grid-column,
${pdfScopeSelector} .v-card,
${pdfScopeSelector} .v-card-text,
${pdfScopeSelector} .v-row,
${pdfScopeSelector} .v-col {
  height: auto !important;
  min-height: 0 !important;
  max-height: none !important;
  overflow: visible !important;
}

${pdfScopeSelector} .challenge-report-screen__grid {
  display: flex !important;
  flex-wrap: nowrap !important;
  align-items: flex-start !important;
  margin: 20px 0 0 !important;
}

${pdfScopeSelector} .challenge-kpi-grid > .v-col {
  width: 25% !important;
  max-width: 25% !important;
  flex: 0 0 25% !important;
}

${pdfScopeSelector} .challenge-report-screen__grid-column {
  display: block !important;
  width: 66.666667% !important;
  max-width: 66.666667% !important;
  flex: 0 0 66.666667% !important;
  padding: 0 6px !important;
}

${pdfScopeSelector} .challenge-report-screen__sidebar {
  display: block !important;
  width: 33.333333% !important;
  max-width: 33.333333% !important;
  flex: 0 0 33.333333% !important;
  margin-top: 0 !important;
  padding: 0 6px !important;
}

${pdfScopeSelector} .challenge-stage-card,
${pdfScopeSelector} .challenge-stage-card__solution,
${pdfScopeSelector} .report-section-card,
${pdfScopeSelector} .challenge-voting-card,
${pdfScopeSelector} .challenge-participants-card,
${pdfScopeSelector} .challenge-media-card {
  overflow: visible !important;
}

${pdfScopeSelector} .report-section-card,
${pdfScopeSelector} .challenge-moderator-solutions,
${pdfScopeSelector} .moderator-solutions-grid,
${pdfScopeSelector} .challenge-stage-card,
${pdfScopeSelector} .challenge-stage-card__body,
${pdfScopeSelector} .challenge-stage-card__panels,
${pdfScopeSelector} .challenge-stage-card__pdf-panels,
${pdfScopeSelector} .challenge-stage-card__pdf-panel,
${pdfScopeSelector} .challenge-stage-card__solutions-grid {
  break-inside: auto !important;
  page-break-inside: auto !important;
}

${pdfScopeSelector} .report-section-card__heading,
${pdfScopeSelector} .moderator-solution-card,
${pdfScopeSelector} .outcome-card,
${pdfScopeSelector} .metadata-tile,
${pdfScopeSelector} .challenge-stage-card__solution {
  break-inside: avoid !important;
  page-break-inside: avoid !important;
}

${pdfScopeSelector} .challenge-details-card__body {
  padding: 22px !important;
}

${pdfScopeSelector} .challenge-hierarchy {
  display: grid !important;
  grid-template-columns: repeat(4, minmax(0, 1fr)) !important;
  gap: 10px !important;
}

${pdfScopeSelector} .challenge-details-card__grid {
  display: grid !important;
  grid-template-columns: repeat(3, minmax(0, 1fr)) !important;
  gap: 10px !important;
}

${pdfScopeSelector} .challenge-hierarchy__arrow {
  display: none !important;
}

${pdfScopeSelector} .v-chip {
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  width: auto !important;
  max-width: none !important;
  min-height: 30px !important;
  border-radius: 999px !important;
  vertical-align: middle !important;
}

${pdfScopeSelector} .v-chip__content {
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  white-space: nowrap !important;
  line-height: 1.2 !important;
  overflow: visible !important;
}

${pdfScopeSelector} .challenge-report-header__chips,
${pdfScopeSelector} .challenge-stage-card__panel-chips,
${pdfScopeSelector} .challenge-stage-card__solution-chips,
${pdfScopeSelector} .challenge-media-card__hashtags,
${pdfScopeSelector} .invitation-dialog__chips,
${pdfScopeSelector} .outcome-card__head {
  display: flex !important;
  flex-wrap: wrap !important;
  align-items: center !important;
  gap: 8px !important;
}

${pdfScopeSelector} .challenge-stage-card__panel-title-row,
${pdfScopeSelector} .challenge-stage-card__solution-head,
${pdfScopeSelector} .moderator-solution-card__head,
${pdfScopeSelector} .invitation-dialog__head {
  display: flex !important;
  align-items: flex-start !important;
  flex-wrap: wrap !important;
  justify-content: space-between !important;
  gap: 12px !important;
}

${pdfScopeSelector} .challenge-stage-card {
  padding-bottom: 32px !important;
}

${pdfScopeSelector} .challenge-stage-card__panels {
  display: flex !important;
  gap: 14px !important;
}

${pdfScopeSelector} .challenge-stage-card__pdf-panels {
  display: grid !important;
  gap: 14px !important;
  margin-top: 18px !important;
}

${pdfScopeSelector} .challenge-stage-card__pdf-panel {
  display: grid !important;
  gap: 14px !important;
  border: 1px solid rgba(13, 82, 62, 0.08) !important;
  border-radius: 22px !important;
  background: #ffffff !important;
  padding: 16px !important;
  overflow: visible !important;
}

${pdfScopeSelector} .challenge-stage-card__panel {
  overflow: visible !important;
  margin-bottom: 8px !important;
}

${pdfScopeSelector} .moderator-solutions-grid {
  display: grid !important;
  grid-template-columns: repeat(2, minmax(0, 1fr)) !important;
  gap: 12px !important;
}

${pdfScopeSelector} .moderator-solution-card__stats {
  display: grid !important;
  grid-template-columns: repeat(4, minmax(0, 1fr)) !important;
  gap: 10px !important;
}

${pdfScopeSelector} .challenge-stage-card__panel .v-expansion-panel-text,
${pdfScopeSelector} .challenge-stage-card__panel .v-expansion-panel-text__wrapper,
${pdfScopeSelector} .challenge-stage-card__panel .v-expansion-panel-text__wrap {
  display: block !important;
  opacity: 1 !important;
  visibility: visible !important;
  height: auto !important;
  max-height: none !important;
  overflow: visible !important;
  transform: none !important;
}

@media (max-width: 960px) {
  ${pdfScopeSelector} .moderator-solutions-grid {
    grid-template-columns: 1fr !important;
  }
}
`
    })
  })

  const waitForPdfRenderStability = async () => {
    await nextTick()
    await nextTick()
    await rafAnimationFrame()
    await rafAnimationFrame()
    await new Promise((resolve) => setTimeout(resolve, 250))
  }

  const handlePdfDownload = async () => {
    if (!reportExportRoot.value || isPdfExporting.value || !challenge.value) {
      return
    }

    isPdfExporting.value = true
    const exportRootElement = reportExportRoot.value

    try {
      await waitForPdfRenderStability()

      await exportElementToPdf({
        target: exportRootElement,
        fileName: pdfFileName.value,
        fontLoads: pdfFontResources.fontLoads,
        injectedStyle: pdfInjectedStyle.value,
        canvasScale: 2,
        beforeCapture: async () => {
          await waitForPdfRenderStability()
        },
        onClone: handlePdfClone,
        pageChrome: {
          headerTitle: reportLabel.value,
          fontLoads: pdfFontResources.fontLoads
        }
      })
    } catch (error) {
      console.error('Error exporting challenge report PDF', error)
    } finally {
      isPdfExporting.value = false
    }
  }

  return {
    expandedStagePanels,
    handlePdfDownload,
    isPdfExporting,
    reportExportRoot
  }
}
