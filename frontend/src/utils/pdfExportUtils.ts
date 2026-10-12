import {
  appendPdfCharsetMeta,
  applyPdfWordSpacing,
  normalizeExportedMdiIcons,
  preserveRenderedCanvases,
  setPdfDocumentLanguage
} from '@/utils/pdfExportService'

type PdfFontResourceUrls = {
  bukraRegularFontUrl: string
  bukraBoldFontUrl: string
  dubaiRegularFontUrl: string
}

type PdfCloneHandlerArgs = {
  doc: Document
  sourceElement: HTMLElement
  clonedElement: HTMLElement
}

type CreateCommonPdfCloneHandlerOptions = {
  isArabic: boolean | (() => boolean)
  wordSpacingSelectors: string[]
  ignoredClosestSelectors?: string[]
  afterClone?: (args: PdfCloneHandlerArgs) => void
}

const PDF_EXPANDABLE_CONTAINER_SELECTORS = [
  '.v-expansion-panel-text',
  '.v-expansion-panel-text__wrapper',
  '.v-expansion-panel-text__wrap',
  '.v-expansion-panels',
  '.v-expansion-panel',
  '.v-window',
  '.v-window__container',
  '.v-window-item',
  '.v-tabs-window-item',
  '.v-carousel-item'
]

export const expandPdfContentInClone = (doc: Document, rootElement: HTMLElement) => {
  const styleElement = doc.createElement('style')
  styleElement.dataset.pdfExpandContent = 'true'
  styleElement.textContent = `
details {
  display: block !important;
}

${PDF_EXPANDABLE_CONTAINER_SELECTORS.join(',\n')} {
  display: block !important;
  opacity: 1 !important;
  visibility: visible !important;
  height: auto !important;
  min-height: 0 !important;
  max-height: none !important;
  overflow: visible !important;
  transform: none !important;
}
`.trim()
  doc.head.appendChild(styleElement)

  const view = doc.defaultView

  rootElement.querySelectorAll<HTMLDetailsElement>('details').forEach((element) => {
    element.open = true
  })

  rootElement.querySelectorAll<HTMLElement>(PDF_EXPANDABLE_CONTAINER_SELECTORS.join(',')).forEach((element) => {
    element.style.display = 'block'
    element.style.opacity = '1'
    element.style.visibility = 'visible'
    element.style.height = 'auto'
    element.style.minHeight = '0'
    element.style.maxHeight = 'none'
    element.style.overflow = 'visible'
    element.style.transform = 'none'
  })

  if (!view) {
    return
  }

  rootElement.querySelectorAll<HTMLElement>('*').forEach((element) => {
    const computedStyle = view.getComputedStyle(element)
    const hasScrollableOverflow =
      ['auto', 'scroll', 'hidden', 'clip'].includes(computedStyle.overflowY) ||
      ['auto', 'scroll', 'hidden', 'clip'].includes(computedStyle.overflowX)
    const hasConstrainedHeight = computedStyle.maxHeight !== 'none'
    const hasHiddenOverflowContent =
      element.scrollHeight > element.clientHeight + 2 || element.scrollWidth > element.clientWidth + 2
    const lineClampValue = computedStyle.getPropertyValue('-webkit-line-clamp').trim()
    const isLineClamped =
      computedStyle.display === '-webkit-box' ||
      (lineClampValue !== '' && lineClampValue !== 'none' && lineClampValue !== 'unset')

    if (isLineClamped) {
      element.style.display = 'block'
      element.style.overflow = 'visible'
      element.style.textOverflow = 'clip'
      element.style.whiteSpace = 'normal'
      element.style.wordBreak = 'break-word'
      element.style.setProperty('-webkit-line-clamp', 'unset')
      element.style.setProperty('-webkit-box-orient', 'initial')
    }

    if (!hasHiddenOverflowContent && !hasScrollableOverflow && !hasConstrainedHeight) {
      return
    }

    element.style.overflow = 'visible'
    element.style.overflowX = 'visible'
    element.style.overflowY = 'visible'
    element.style.height = 'auto'
    element.style.maxHeight = 'none'
  })
}

export const sanitizePdfFileSegment = (value: string, fallback: string) => {
  return (
    value
      .trim()
      .replaceAll(/[<>:"/\\|?*\u0000-\u001F]+/g, '-')
      .replaceAll(/\s+/g, '-')
      .replaceAll(/-+/g, '-')
      .replaceAll(/^-|-$/g, '') || fallback
  )
}

export const createPdfTimestampSegment = (date = new Date()) => {
  const year = String(date.getFullYear())
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')

  return `${year}${month}${day}-${hours}${minutes}${seconds}`
}

export const buildPdfFileName = (segments: Array<string | null | undefined>, fallback: string) => {
  const safeSegments = segments
    .map((segment) => segment?.trim())
    .filter((segment): segment is string => Boolean(segment))
  const baseName = safeSegments.length ? safeSegments.join('-') : fallback

  return `${baseName}-${createPdfTimestampSegment()}.pdf`
}

export const createPdfFontResources = ({
                                         bukraRegularFontUrl,
                                         bukraBoldFontUrl,
                                         dubaiRegularFontUrl
                                       }: PdfFontResourceUrls) => ({
  fontFaces: [
    { family: 'DubaiRegular', src: dubaiRegularFontUrl, format: 'truetype' },
    { family: 'bukraregular', src: bukraRegularFontUrl, format: 'truetype' },
    { family: 'bukrabold', src: bukraBoldFontUrl, format: 'truetype', weight: 700 }
  ],
  fontLoads: ['16px \'DubaiRegular\'', '16px \'bukraregular\'', '700 16px \'bukrabold\'']
})

export const createCommonPdfCloneHandler = ({
                                              isArabic,
                                              wordSpacingSelectors,
                                              ignoredClosestSelectors = ['.apexcharts-text'],
                                              afterClone
                                            }: CreateCommonPdfCloneHandlerOptions) => {
  return ({ doc, sourceElement, clonedElement }: PdfCloneHandlerArgs) => {
    const isArabicLanguage = typeof isArabic === 'function' ? isArabic() : isArabic

    appendPdfCharsetMeta(doc)
    setPdfDocumentLanguage(doc, isArabicLanguage ? 'ar' : 'en', isArabicLanguage ? 'rtl' : 'ltr')
    afterClone?.({ doc, sourceElement, clonedElement })
    expandPdfContentInClone(doc, clonedElement)
    preserveRenderedCanvases(sourceElement, clonedElement)
    normalizeExportedMdiIcons(sourceElement, clonedElement)

    // The package-level Arabic word tokenization can reverse word order in html2canvas output.
    // Keep it for non-Arabic exports only until a safe RTL-aware variant is needed.
    if (!isArabicLanguage) {
      applyPdfWordSpacing(doc, clonedElement, {
        selectors: wordSpacingSelectors,
        ignoredClosestSelectors
      })
    }
  }
}

const hasUnsupportedColorFunction = (value?: string | null) => {
  const normalizedValue = value?.toLowerCase() || ''
  return normalizedValue.includes('color(') || normalizedValue.includes('color-mix(')
}

export const sanitizeUnsupportedColorFunctionsInClone = (doc: Document, rootElement: HTMLElement) => {
  const view = doc.defaultView
  if (!view) {
    return
  }

  const elements = [rootElement, ...Array.from(rootElement.querySelectorAll<HTMLElement>('*'))]
  const pseudoRules: string[] = []

  elements.forEach((element, index) => {
    const computedStyle = view.getComputedStyle(element)

    if (hasUnsupportedColorFunction(computedStyle.backgroundImage)) {
      element.style.backgroundImage = 'none'
    }

    if (hasUnsupportedColorFunction(computedStyle.background)) {
      element.style.backgroundImage = 'none'
    }

    if (hasUnsupportedColorFunction(computedStyle.borderImageSource)) {
      element.style.borderImage = 'none'
    }

    ;(['before', 'after'] as const).forEach((pseudoName) => {
      const pseudoSelector = pseudoName === 'before' ? '::before' : '::after'
      const pseudoStyle = view.getComputedStyle(element, pseudoSelector)
      if (
        !hasUnsupportedColorFunction(pseudoStyle.backgroundImage) &&
        !hasUnsupportedColorFunction(pseudoStyle.background)
      ) {
        return
      }

      const attributeName = `data-pdf-strip-${pseudoName}`
      element.setAttribute(attributeName, String(index))
      pseudoRules.push(
        `[${attributeName}="${index}"]${pseudoSelector}{content:none !important;background:none !important;background-image:none !important;border-image:none !important;box-shadow:none !important;}`
      )
    })
  })

  if (!pseudoRules.length) {
    return
  }

  const styleElement = doc.createElement('style')
  styleElement.dataset.pdfUnsupportedColorFix = 'true'
  styleElement.textContent = pseudoRules.join('')
  doc.head.appendChild(styleElement)
}
