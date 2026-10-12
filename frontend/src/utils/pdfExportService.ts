import { nextTick } from 'vue'
import pdfLeftLogoMarkup from '@/assets/images/pdf/dp-pdf-left-logo.svg?raw'
import pdfRightLogoMarkup from '@/assets/images/pdf/dp-pdf-right-logo.svg?raw'
import futureForesightLogoUrl from '@/assets/images/logo/future-foresight.png'
import {
  buildReportFooterCopyright,
  DEFAULT_REPORT_BRAND_SUBTITLE,
  DEFAULT_REPORT_BRAND_TITLE,
  DEFAULT_REPORT_OWNER
} from '@/utils/report/exportReportChrome'

export interface PdfFontFaceDefinition {
  family: string
  src: string
  format?: string
  weight?: string | number
  style?: string
  display?: string
}

export interface BuildPdfExportStylesOptions {
  scopeSelector: string
  fontFaces?: PdfFontFaceDefinition[]
  baseFontFamily?: string
  iconFontFamily?: string
  direction?: 'rtl' | 'ltr'
  unicodeBidi?: string
  textAlign?: 'left' | 'right' | 'center' | 'start' | 'end'
  includePageReset?: boolean
  autoHeightSelectors?: string[]
  hideSelectors?: string[]
  fullWidthSelectors?: string[]
  avoidBreakSelectors?: string[]
  iconSelectors?: string[]
  customCss?: string
}

export interface ExportPdfCloneContext {
  doc: Document
  sourceElement: HTMLElement
  clonedElement: HTMLElement
}

export interface ExportPdfPageChromeBranding {
  leftLogoUrl?: string
  rightLogoUrl?: string
  footerBadgeLabel?: string
  footerBrandTitle?: string
  footerBrandSubtitle?: string
  footerOwner?: string
  footerCopyright?: string
}

export interface ExportPdfPageChromeOptions {
  enabled?: boolean
  showHeader?: boolean
  showFooter?: boolean
  headerTitle?: string
  headerDescription?: string
  fontLoads?: string[]
  branding?: ExportPdfPageChromeBranding
}

export interface ExportElementToPdfOptions {
  target: string | HTMLElement
  fileName: string
  fontLoads?: string[]
  injectedStyle?: string
  canvasScale?: number
  useCORS?: boolean
  maxSinglePageHeightMm?: number
  beforeCapture?: () => Promise<void> | void
  onClone?: (context: ExportPdfCloneContext) => void
  pageChrome?: ExportPdfPageChromeOptions
  expandCollapsedCards?: boolean
}

export interface ApplyPdfWordSpacingOptions {
  selectors: string | string[]
  ignoredClosestSelectors?: string | string[]
  containerClassName?: string
  tokenClassName?: string
  preserveWhitespaceBetweenWords?: boolean
}

export const PDF_EXPORT_CLASS_NAMES = {
  hide: 'pdf-export-hide',
  avoidBreak: 'pdf-export-avoid-break',
  overflowVisible: 'pdf-export-overflow-visible',
  fullWidth: 'pdf-export-full-width',
  fieldValue: 'pdf-field-value',
  htmlContent: 'pdf-html-content',
  wordSpacingContainer: 'pdf-word-spacing-container',
  wordToken: 'pdf-word-token',
  inlineIconImage: 'pdf-inline-icon-image'
} as const

const DEFAULT_CANVAS_SCALE = 2.5
const DEFAULT_MAX_SINGLE_PAGE_HEIGHT_MM = 5000
const MAX_CAPTURE_CANVAS_HEIGHT_PX = 30000
const MAX_CAPTURE_CANVAS_AREA_PX = 60000000
const PDF_PAGE_SLICE_OVERLAP_PX = 8
const PDF_SIDE_MARGIN_MM = 8
const PDF_TOP_MARGIN_MM = 6
const PDF_BOTTOM_MARGIN_MM = 6
const PDF_HEADER_HEIGHT_MM = 18
const PDF_HEADER_GAP_MM = 5
const PDF_FOOTER_HEIGHT_MM = 22
const PDF_FOOTER_GAP_MM = 5

const createSvgDataUrl = (svgMarkup: string) =>
  `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svgMarkup)}`

const defaultLeftLogoUrl = createSvgDataUrl(pdfLeftLogoMarkup)
const defaultRightLogoUrl = createSvgDataUrl(pdfRightLogoMarkup)
const unique = (values: string[] = []) => Array.from(new Set(values.filter(Boolean)))

const buildRule = (selectors: string[] | undefined, declarations: string) => {
  const normalizedSelectors = unique(selectors)
  return normalizedSelectors.length ? `${normalizedSelectors.join(',\n')} {\n${declarations}\n}` : ''
}

export const buildPdfFontFaceStyles = (fontFaces: PdfFontFaceDefinition[] = []) =>
  fontFaces
    .filter((fontFace) => fontFace.family && fontFace.src)
    .map((fontFace) => {
      const format = fontFace.format ? ` format('${fontFace.format}')` : ''
      const display = fontFace.display ? `\n  font-display: ${fontFace.display};` : ''
      return `@font-face {
  font-family: '${fontFace.family}';
  src: url('${fontFace.src}')${format};
  font-weight: ${fontFace.weight ?? 400};
  font-style: ${fontFace.style ?? 'normal'};${display}
}`
    })
    .join('\n')

export const buildPdfExportStyles = ({
                                       scopeSelector,
                                       fontFaces = [],
                                       baseFontFamily = '\'DubaiRegular\', Arial, sans-serif',
                                       iconFontFamily,
                                       direction = 'rtl',
                                       unicodeBidi = 'normal',
                                       textAlign = direction === 'rtl' ? 'right' : 'left',
                                       includePageReset = true,
                                       autoHeightSelectors = [],
                                       hideSelectors = [],
                                       fullWidthSelectors = [],
                                       avoidBreakSelectors = [],
                                       iconSelectors = [],
                                       customCss = ''
                                     }: BuildPdfExportStylesOptions) => {
  const generatedSections = [
    buildRule(autoHeightSelectors, '  overflow: visible !important;\n  height: auto !important;\n  max-height: none !important;'),
    buildRule(hideSelectors, '  display: none !important;'),
    buildRule(fullWidthSelectors, '  width: 100% !important;\n  max-width: none !important;\n  min-width: 0 !important;'),
    buildRule(avoidBreakSelectors, '  break-inside: avoid-page;\n  page-break-inside: avoid;'),
    iconFontFamily
      ? buildRule(
        iconSelectors,
        `  font-family: '${iconFontFamily}' !important;\n  font-weight: normal !important;\n  font-style: normal !important;`
      )
      : ''
  ]
    .filter(Boolean)
    .join('\n\n')

  return `
@charset "UTF-8";
${buildPdfFontFaceStyles(fontFaces)}
html, body, ${scopeSelector}, ${scopeSelector} * {
  direction: ${direction} !important;
  unicode-bidi: ${unicodeBidi} !important;
  letter-spacing: 0 !important;
  word-spacing: normal !important;
  text-align: ${textAlign} !important;
  text-rendering: geometricPrecision !important;
  font-kerning: normal !important;
  -webkit-font-smoothing: antialiased !important;
  white-space: normal;
  font-family: ${baseFontFamily} !important;
}
${includePageReset ? 'html, body { margin: 0 !important; padding: 0 !important; background: #fff !important; overflow: visible !important; }' : ''}
${generatedSections}
${scopeSelector} .${PDF_EXPORT_CLASS_NAMES.hide} { display: none !important; }
${scopeSelector} .${PDF_EXPORT_CLASS_NAMES.overflowVisible},
${scopeSelector} .${PDF_EXPORT_CLASS_NAMES.overflowVisible} * {
  overflow: visible !important;
  height: auto !important;
  max-height: none !important;
}
${scopeSelector} .${PDF_EXPORT_CLASS_NAMES.avoidBreak} {
  break-inside: avoid-page;
  page-break-inside: avoid;
}
${scopeSelector} .${PDF_EXPORT_CLASS_NAMES.fullWidth} {
  width: 100% !important;
  max-width: none !important;
  min-width: 0 !important;
}
${scopeSelector} .${PDF_EXPORT_CLASS_NAMES.inlineIconImage} {
  object-fit: contain !important;
}
${scopeSelector} .${PDF_EXPORT_CLASS_NAMES.fieldValue} {
  min-height: 36px;
  display: flex;
  align-items: center;
  padding: 6px 10px;
  border: 1px solid rgba(148, 163, 184, 0.55);
  border-radius: 10px;
  background: #fff;
  color: #111827;
  line-height: 1.55;
  overflow-wrap: anywhere;
  white-space: pre-wrap;
}
${scopeSelector} .${PDF_EXPORT_CLASS_NAMES.htmlContent},
${scopeSelector} .${PDF_EXPORT_CLASS_NAMES.htmlContent} * {
  max-width: 100% !important;
  overflow: visible !important;
  visibility: visible !important;
}
${scopeSelector} .${PDF_EXPORT_CLASS_NAMES.wordSpacingContainer} {
  display: inline !important;
  direction: ${direction} !important;
  unicode-bidi: plaintext !important;
  word-spacing: 0.14em !important;
}
${scopeSelector} .${PDF_EXPORT_CLASS_NAMES.wordToken} {
  display: inline !important;
  white-space: nowrap !important;
}
${customCss}
`.trim()
}

const normalizeSelectors = (selectors?: string | string[]) => {
  if (!selectors) {
    return []
  }

  const normalized = Array.isArray(selectors) ? selectors : [selectors]

  return normalized.filter(Boolean)
}

export const applyPdfWordSpacing = (
  doc: Document,
  root: ParentNode,
  options: ApplyPdfWordSpacingOptions
) => {
  const selectors = normalizeSelectors(options.selectors)
  if (!selectors.length) return

  const ignoredSelectors = normalizeSelectors(options.ignoredClosestSelectors)
  const candidates = root.querySelectorAll<HTMLElement>(selectors.join(', '))

  candidates.forEach((element) => {
    const onlyTextNodes = Array.from(element.childNodes).every((node) => node.nodeType === Node.TEXT_NODE)
    const ignored = ignoredSelectors.some((selector) => Boolean(element.closest(selector)))
    const text = element.textContent?.replaceAll('\u00A0', ' ').replaceAll(/\s+/g, ' ').trim() || ''
    if (!onlyTextNodes || ignored || !/[\u0600-\u06FF]/.test(text) || !text.includes(' ')) return

    const container = doc.createElement('span')
    container.className = options.containerClassName || PDF_EXPORT_CLASS_NAMES.wordSpacingContainer
    text.split(' ').filter(Boolean).forEach((word, index, words) => {
      const token = doc.createElement('span')
      token.className = options.tokenClassName || PDF_EXPORT_CLASS_NAMES.wordToken
      token.textContent = word
      container.appendChild(token)
      if ((options.preserveWhitespaceBetweenWords ?? true) && index < words.length - 1) {
        container.appendChild(doc.createTextNode(' '))
      }
    })
    element.replaceChildren(container)
  })
}

export const appendPdfCharsetMeta = (doc: Document, charset = 'UTF-8') => {
  const existingCharset = doc.head.querySelector('meta[charset]')
  if (existingCharset) {
    existingCharset.setAttribute('charset', charset)
    return
  }
  const metaCharset = doc.createElement('meta')
  metaCharset.setAttribute('charset', charset)
  doc.head.prepend(metaCharset)
}

export const setPdfDocumentLanguage = (doc: Document, lang = 'ar', dir?: 'rtl' | 'ltr') => {
  doc.documentElement.setAttribute('lang', lang)
  if (dir) doc.documentElement.setAttribute('dir', dir)
}

export const preserveRenderedCanvases = (sourceRoot: ParentNode, clonedRoot: ParentNode) => {
  const sources = Array.from(sourceRoot.querySelectorAll<HTMLCanvasElement>('canvas'))
  Array.from(clonedRoot.querySelectorAll<HTMLCanvasElement>('canvas')).forEach((clone, index) => {
    const source = sources[index]
    const context = clone.getContext('2d')
    if (!source || !context) return
    clone.width = source.width
    clone.height = source.height
    clone.style.width = source.style.width || `${source.clientWidth}px`
    clone.style.height = source.style.height || `${source.clientHeight}px`
    try {
      context.drawImage(source, 0, 0)
    } catch {
      // A tainted or transient canvas should not abort the report export.
    }
  })
}

const decodePseudoContent = (content: string) =>
  content && content !== 'none' && content !== 'normal'
    ? content
      .replaceAll(/^['"]|['"]$/g, '')
      .replaceAll(/\\([0-9a-fA-F]{1,6})\s?/g, (_, hex: string) => String.fromCodePoint(Number.parseInt(hex, 16)))
    : ''

export const normalizeExportedMdiIcons = (sourceRoot: ParentNode, clonedRoot: ParentNode) => {
  const sources = Array.from(sourceRoot.querySelectorAll<HTMLElement>('.v-icon'))
  Array.from(clonedRoot.querySelectorAll<HTMLElement>('.v-icon')).forEach((clone, index) => {
    const source = sources[index]
    if (!source) return

    const glyph = decodePseudoContent(globalThis.getComputedStyle(source, '::before').content)
    if (!glyph) return

    const style = globalThis.getComputedStyle(source)
    const rect = source.getBoundingClientRect()
    const fontSize = Math.max(14, Number.parseFloat(style.fontSize || '16'))
    const width = Math.max(fontSize, rect.width || 0)
    const height = Math.max(fontSize, rect.height || 0)
    const scale = 3
    const canvas = document.createElement('canvas')
    canvas.width = Math.ceil(width * scale)
    canvas.height = Math.ceil(height * scale)
    const context = canvas.getContext('2d')
    if (!context) return

    context.fillStyle = style.color || '#11523c'
    context.font = `normal ${fontSize * scale}px "Material Design Icons"`
    context.textAlign = 'center'
    context.textBaseline = 'middle'
    context.fillText(glyph, canvas.width / 2, canvas.height / 2)

    const image = document.createElement('img')
    image.className = PDF_EXPORT_CLASS_NAMES.inlineIconImage
    image.src = canvas.toDataURL('image/png')
    image.alt = ''
    image.ariaHidden = 'true'
    image.style.width = `${width}px`
    image.style.height = `${height}px`
    image.style.display = 'inline-block'
    image.style.verticalAlign = 'middle'
    clone.replaceWith(image)
  })
}

export const extractPrintableFieldValue = (element: Element) => {
  const input = element.querySelector<HTMLInputElement>('input')
  const textarea = element.querySelector<HTMLTextAreaElement>('textarea')
  const selection = element.querySelector<HTMLElement>(
    '.v-select__selection-text, .v-field__input .v-chip__content, .v-field__input'
  )
  const checkbox = element.querySelector<HTMLInputElement>('input[type="checkbox"]')
  if (checkbox) return checkbox.checked ? 'نعم' : 'لا'
  return input?.value.trim() || textarea?.value.trim() || selection?.textContent?.trim() || '-'
}

export const replaceElementsWithPrintableValues = (
  sourceRoot: ParentNode,
  clonedRoot: ParentNode,
  selector: string,
  valueResolver: (element: Element) => string = extractPrintableFieldValue,
  className = PDF_EXPORT_CLASS_NAMES.fieldValue
) => {
  const sources = sourceRoot.querySelectorAll(selector)
  clonedRoot.querySelectorAll(selector).forEach((clone, index) => {
    const source = sources[index]
    if (!source) return
    const printableField = document.createElement('div')
    printableField.className = className
    printableField.textContent = valueResolver(source)
    clone.replaceWith(printableField)
  })
}

export const expandCollapsedPdfCards = ({ clonedElement }: ExportPdfCloneContext) => {
  clonedElement.querySelectorAll<HTMLDetailsElement>('details').forEach((element) => {
    element.open = true
  })
  clonedElement
    .querySelectorAll<HTMLElement>(
      '.v-expansion-panel-text, .v-expansion-panel-text__wrapper, .v-expansion-panel-text__wrap'
    )
    .forEach((element) => {
      element.hidden = false
      Object.assign(element.style, {
        display: 'block',
        height: 'auto',
        maxHeight: 'none',
        opacity: '1',
        overflow: 'visible',
        visibility: 'visible'
      })
    })
}

const resolveTarget = (target: string | HTMLElement) =>
  typeof target === 'string' ? document.querySelector<HTMLElement>(target) : target

const waitForFonts = async (fontLoads: string[] = []) => {
  if (!document.fonts) return
  await document.fonts.ready
  await Promise.all(fontLoads.map((font) => document.fonts.load(font)))
}

const createPageChrome = (
  title: string,
  description: string,
  branding: ExportPdfPageChromeBranding,
  type: 'header' | 'footer'
) => {
  const root = document.createElement('div')
  root.dir = 'rtl'
  root.style.cssText =
    'box-sizing:border-box;width:100%;height:100%;display:flex;align-items:center;justify-content:space-between;font-family:DubaiRegular,Arial,sans-serif;'

  if (type === 'header') {
    root.style.padding = '8px 14px'
    root.style.borderBottom = '2px solid #0d6b4d'

    const brandCluster = document.createElement('div')
    brandCluster.style.cssText =
      'display:flex;align-items:center;justify-content:flex-start;gap:12px;flex:0 0 auto;direction:rtl;'

    const rightLogo = document.createElement('img')
    rightLogo.src = branding.rightLogoUrl || defaultRightLogoUrl
    rightLogo.style.cssText = 'width:112px;max-height:68px;object-fit:contain;'

    const brandSeparator = document.createElement('span')
    brandSeparator.setAttribute('aria-hidden', 'true')
    brandSeparator.style.cssText =
      'display:block;width:2px;height:48px;border-radius:999px;background:linear-gradient(180deg,#45c979,#0d6b4d);opacity:.72;'

    const futureForesightLogo = document.createElement('img')
    futureForesightLogo.src = futureForesightLogoUrl
    futureForesightLogo.alt = 'Future Foresight'
    futureForesightLogo.style.cssText = 'width:116px;max-height:58px;object-fit:contain;'

    brandCluster.append(rightLogo, brandSeparator, futureForesightLogo)

    const copyShell = document.createElement('div')
    copyShell.style.cssText =
      'flex:1;min-width:0;display:flex;align-items:center;justify-content:center;padding:0 22px;'

    const copy = document.createElement('div')
    copy.style.cssText =
      'width:min(100%,560px);min-width:260px;box-sizing:border-box;display:flex;flex-direction:column;align-items:center;justify-content:flex-start;gap:8px;padding:10px 26px 9px;border:1px solid #d5e9e1;border-radius:14px;background:#ffffff;box-shadow:0 4px 12px rgba(13,82,62,.07);text-align:center;color:#143b30;'

    const titleText = document.createElement('strong')
    titleText.textContent = title
    const getTitleFontSize = (title: string): number => {
      if (title.length > 80) return 17
      if (title.length > 52) return 19
      if (title.length > 30) return 21
      return 24
    }

    const titleFontSize = getTitleFontSize(title)
    titleText.style.cssText = `
  display:block;
  max-width:100%;
  margin:0;
  font-family:DubaiBold,DubaiRegular,Arial,sans-serif;
  font-size:${titleFontSize}px;
  font-weight:700;
  line-height:1.22;
  color:#0d5f47;
  overflow-wrap:anywhere;
  unicode-bidi:plaintext;
`

    // Update the description pill (around line 287)
    const descriptionPill = document.createElement('div')
    descriptionPill.style.cssText = `
  display: block;
  max-width: 100%;
  min-height: 24px;
  box-sizing: border-box;
  padding: 4px 0 5px; /* Remove horizontal padding */
  font-family: DubaiRegular,Arial,sans-serif;
  font-size: 12px;
  font-weight: 400;
  line-height: 1.55;
  text-align: center;
  overflow: visible;
  width: 100%;
  color: #506b62;
`

// Update the description text (around line 294)
    const descriptionText = document.createElement('span')
    descriptionText.textContent = description
    descriptionText.dir = 'auto'
    descriptionText.style.cssText = `
  display: block;
  max-width: 100%;
  overflow: visible;
  white-space: normal;
  overflow-wrap: anywhere;
  unicode-bidi: isolate;
  padding: 0 8px;
  word-break: break-word;
  color: #506b62; /* Keep text color */
`

    descriptionPill.appendChild(descriptionText)
    copy.appendChild(titleText)
    if (description.trim()) {
      copy.appendChild(descriptionPill)
    }
    copyShell.appendChild(copy)

    const leftLogo = document.createElement('img')
    leftLogo.src = branding.leftLogoUrl || defaultLeftLogoUrl
    leftLogo.style.cssText = 'max-width:118px;max-height:72px;object-fit:contain;'

    root.append(brandCluster, copyShell, leftLogo)
    return root
  }

  root.style.padding = '16px 34px'
  root.style.borderRadius = '16px'
  root.style.background = 'linear-gradient(135deg,#064f3f 0%,#087457 54%,#0a8a62 100%)'
  root.style.boxShadow = '0 8px 22px rgba(4,72,55,.18)'
  root.style.color = '#fff'
  root.style.overflow = 'hidden'

  const brand = document.createElement('div')
  brand.style.cssText =
    'display:flex;align-items:center;justify-content:flex-start;flex:1.35 1 0;min-width:0;direction:rtl;text-align:right;'

  const brandCopy = document.createElement('div')
  brandCopy.style.cssText =
    'display:flex;flex-direction:column;justify-content:center;align-items:flex-start;min-width:0;text-align:right;direction:rtl;'

  const brandTitle = document.createElement('strong')
  brandTitle.textContent = branding.footerBrandTitle || DEFAULT_REPORT_BRAND_TITLE
  brandTitle.style.cssText =
    'font-family:DubaiBold,DubaiRegular,Arial,sans-serif;font-size:21px;font-weight:700;line-height:1.35;color:#fff;white-space:nowrap;'

  const brandSubtitle = document.createElement('span')
  brandSubtitle.textContent = branding.footerBrandSubtitle || DEFAULT_REPORT_BRAND_SUBTITLE
  brandSubtitle.style.cssText =
    'margin-top:7px;font-family:Arial,sans-serif;font-size:12px;font-weight:400;line-height:1.45;letter-spacing:.15px;color:rgba(255,255,255,.78);white-space:nowrap;direction:ltr;'

  brandCopy.append(brandTitle, brandSubtitle)
  brand.appendChild(brandCopy)

  const createSeparator = () => {
    const separator = document.createElement('span')
    separator.setAttribute('aria-hidden', 'true')
    separator.style.cssText =
      'display:block;width:1px;height:54px;flex:0 0 1px;margin:0 24px;background:rgba(255,255,255,.22);'
    return separator
  }

  const meta = document.createElement('div')
  meta.style.cssText =
    'display:flex;flex:1 1 0;min-width:0;flex-direction:column;align-items:flex-end;justify-content:center;gap:7px;text-align:right;direction:rtl;color:rgba(255,255,255,.9);'

  const copyright = document.createElement('span')
  copyright.textContent = branding.footerCopyright || buildReportFooterCopyright()
  copyright.style.cssText =
    'font-family:DubaiBold,DubaiRegular,Arial,sans-serif;font-size:13px;font-weight:700;color:#fff;'
  const owner = document.createElement('span')
  owner.textContent = branding.footerOwner || DEFAULT_REPORT_OWNER
  owner.style.cssText = 'font-size:11px;line-height:1.45;color:rgba(255,255,255,.76);'
  meta.append(copyright, owner)

  const navigation = document.createElement('div')
  navigation.style.cssText =
    'margin-top:7px;font-family:Arial,sans-serif;font-size:12px;font-weight:400;line-height:1.45;color:rgba(255,255,255,.78);white-space:nowrap;direction:ltr;'
  // 'display:flex;flex:.72 1 0;min-width:0;flex-direction:column;align-items:flex-end;justify-content:center;text-align:right;direction:rtl;color:#fff;'

  // const foresightTitle = document.createElement('strong')
  // foresightTitle.textContent = 'استشراف المستقبل'
  // foresightTitle.style.cssText =
  //   'font-family:DubaiBold,DubaiRegular,Arial,sans-serif;font-size:17px;font-weight:700;line-height:1.35;color:#fff;white-space:nowrap;'
  //
  // const foresightSubtitle = document.createElement('span')
  // foresightSubtitle.textContent = 'Future Foresight'
  // foresightSubtitle.style.cssText =
  //   'margin-top:7px;font-family:Arial,sans-serif;font-size:12px;font-weight:400;line-height:1.45;color:rgba(255,255,255,.78);white-space:nowrap;direction:ltr;'

  // navigation.append(foresightTitle, foresightSubtitle)
  root.append(brand, createSeparator(), meta)
  return root
}

const renderChrome = async (
  html2canvas: typeof import('html2canvas').default,
  node: HTMLElement,
  widthPx: number,
  heightPx: number,
  useCORS: boolean
) => {
  const host = document.createElement('div')
  host.style.cssText = `position:fixed;left:-100000px;top:0;width:${widthPx}px;height:${heightPx}px;background:#fff;`
  node.style.width = `${widthPx}px`
  node.style.height = `${heightPx}px`
  host.appendChild(node)
  document.body.appendChild(host)
  try {
    await new Promise<void>((resolve) => requestAnimationFrame(() => resolve()))
    const canvas = await html2canvas(node, {
      scale: 2,
      useCORS,
      backgroundColor: null,
      width: widthPx,
      height: heightPx
    })
    return canvas.toDataURL('image/png')
  } finally {
    host.remove()
  }
}

export const exportElementToPdf = async (options: ExportElementToPdfOptions) => {
  const sourceElement = resolveTarget(options.target)
  if (!sourceElement) throw new TypeError('PDF_TARGET_NOT_FOUND')

  await options.beforeCapture?.()
  await waitForFonts(unique([...(options.fontLoads || []), ...(options.pageChrome?.fontLoads || [])]))
  const [{ default: html2canvas }, { jsPDF }] = await Promise.all([import('html2canvas'), import('jspdf')])

  const exportHost = document.createElement('div')
  exportHost.style.cssText = `position:fixed;left:-100000px;top:0;width:${Math.max(sourceElement.scrollWidth, sourceElement.offsetWidth)}px;background:#fff;overflow:visible;z-index:-1;`
  const clonedElement = sourceElement.cloneNode(true) as HTMLElement
  clonedElement.dataset.pdfExportRoot = 'true'
  clonedElement.style.cssText += `;width:${Math.max(sourceElement.scrollWidth, sourceElement.offsetWidth)}px;max-width:none;margin:0;background:#fff;overflow:visible;`
  exportHost.appendChild(clonedElement)
  document.body.appendChild(exportHost)

  const sourceElementId = sourceElement.getAttribute('id')
  if (sourceElementId && clonedElement.id === sourceElementId) {
    sourceElement.removeAttribute('id')
  }

  const measurementStyle = document.createElement('style')
  measurementStyle.dataset.pdfMeasurementStyles = 'true'
  measurementStyle.textContent = (options.injectedStyle || '')
    .replaceAll(/html,\s*body\s*\{[^}]*}/g, '')
    .replaceAll(/html,\s*body,\s*/g, '')
  document.head.appendChild(measurementStyle)

  try {
    if (options.expandCollapsedCards !== false) {
      expandCollapsedPdfCards({ doc: document, sourceElement, clonedElement })
    }
    await nextTick()
    await new Promise<void>((resolve) => requestAnimationFrame(() => resolve()))

    const captureWidth = Math.max(1, clonedElement.scrollWidth)
    const captureHeight = Math.max(1, clonedElement.scrollHeight)
    const requestedScale = options.canvasScale || DEFAULT_CANVAS_SCALE
    const heightLimitedScale = MAX_CAPTURE_CANVAS_HEIGHT_PX / captureHeight
    const areaLimitedScale = Math.sqrt(MAX_CAPTURE_CANVAS_AREA_PX / (captureWidth * captureHeight))
    const captureScale = Math.max(0.25, Math.min(requestedScale, heightLimitedScale, areaLimitedScale))

    const canvas = await html2canvas(clonedElement, {
      scale: captureScale,
      useCORS: options.useCORS ?? true,
      backgroundColor: '#fff',
      width: captureWidth,
      height: captureHeight,
      windowWidth: captureWidth,
      windowHeight: captureHeight,
      scrollX: 0,
      scrollY: 0,
      onclone: (doc) => {
        if (options.injectedStyle?.trim()) {
          const style = doc.createElement('style')
          style.textContent = options.injectedStyle
          doc.head.appendChild(style)
        }
        const activeClone = doc.body.querySelector<HTMLElement>('[data-pdf-export-root]') || doc.body.firstElementChild
        if (!(activeClone instanceof HTMLElement)) return
        const context = { doc, sourceElement, clonedElement: activeClone }
        if (options.expandCollapsedCards !== false) expandCollapsedPdfCards(context)
        options.onClone?.(context)
      }
    })

    const pageChrome = options.pageChrome
    const chromeEnabled = pageChrome?.enabled ?? true
    const showHeader = chromeEnabled && (pageChrome?.showHeader ?? true)
    const showFooter = chromeEnabled && (pageChrome?.showFooter ?? true)
    const orientation = canvas.width >= canvas.height ? 'landscape' : 'portrait'
    const basePageWidth = orientation === 'landscape' ? 297 : 210
    const singlePageContentHeight = (canvas.height * basePageWidth) / canvas.width
    const chromeHeight =
      PDF_TOP_MARGIN_MM +
      (showHeader ? PDF_HEADER_HEIGHT_MM + PDF_HEADER_GAP_MM : 0) +
      (showFooter ? PDF_FOOTER_HEIGHT_MM + PDF_FOOTER_GAP_MM : 0) +
      PDF_BOTTOM_MARGIN_MM
    const branding = pageChrome?.branding || {}
    const createHeaderDataUrl = (pageWidth: number) =>
      showHeader
        ? renderChrome(
          html2canvas,
          createPageChrome(
            pageChrome?.headerTitle || 'التقارير',
            pageChrome?.headerDescription ||
            `تم تحميل هذا التقرير يوم ${new Date().toLocaleDateString('ar-AE')}`,
            branding,
            'header'
          ),
          Math.round((pageWidth - PDF_SIDE_MARGIN_MM * 2) * 6),
          Math.round(PDF_HEADER_HEIGHT_MM * 6),
          options.useCORS ?? true
        )
        : Promise.resolve('')
    const createFooterDataUrl = (pageWidth: number) =>
      showFooter
        ? renderChrome(
          html2canvas,
          createPageChrome('', '', branding, 'footer'),
          Math.round((pageWidth - PDF_SIDE_MARGIN_MM * 2) * 6),
          Math.round(PDF_FOOTER_HEIGHT_MM * 6),
          options.useCORS ?? true
        )
        : Promise.resolve('')
    const maxSinglePageHeight = options.maxSinglePageHeightMm || DEFAULT_MAX_SINGLE_PAGE_HEIGHT_MM

    if (singlePageContentHeight <= maxSinglePageHeight) {
      const totalPageHeight = singlePageContentHeight + chromeHeight
      const singlePageOrientation = totalPageHeight >= basePageWidth ? 'portrait' : 'landscape'
      const singlePagePdf = new jsPDF({
        orientation: singlePageOrientation,
        unit: 'mm',
        format: [basePageWidth, totalPageHeight]
      })
      const pageWidth = singlePagePdf.internal.pageSize.getWidth()
      const pageHeight = singlePagePdf.internal.pageSize.getHeight()
      const contentTop =
        PDF_TOP_MARGIN_MM + (showHeader ? PDF_HEADER_HEIGHT_MM + PDF_HEADER_GAP_MM : 0)
      const [headerDataUrl, footerDataUrl] = await Promise.all([
        createHeaderDataUrl(pageWidth),
        createFooterDataUrl(pageWidth)
      ])

      singlePagePdf.addImage(
        canvas.toDataURL('image/png'),
        'PNG',
        0,
        contentTop,
        pageWidth,
        (canvas.height * pageWidth) / canvas.width,
        undefined,
        'FAST'
      )
      if (showHeader) {
        singlePagePdf.addImage(
          headerDataUrl,
          'PNG',
          PDF_SIDE_MARGIN_MM,
          PDF_TOP_MARGIN_MM,
          pageWidth - PDF_SIDE_MARGIN_MM * 2,
          PDF_HEADER_HEIGHT_MM,
          undefined,
          'FAST'
        )
      }
      if (showFooter) {
        singlePagePdf.addImage(
          footerDataUrl,
          'PNG',
          PDF_SIDE_MARGIN_MM,
          pageHeight - PDF_BOTTOM_MARGIN_MM - PDF_FOOTER_HEIGHT_MM,
          pageWidth - PDF_SIDE_MARGIN_MM * 2,
          PDF_FOOTER_HEIGHT_MM,
          undefined,
          'FAST'
        )
      }
      singlePagePdf.save(options.fileName)
      return
    }

    const pdf = new jsPDF({ orientation, unit: 'mm', format: 'a4' })
    const pageWidth = pdf.internal.pageSize.getWidth()
    const pageHeight = pdf.internal.pageSize.getHeight()
    const contentTop =
      PDF_TOP_MARGIN_MM + (showHeader ? PDF_HEADER_HEIGHT_MM + PDF_HEADER_GAP_MM : 0)
    const contentBottom =
      PDF_BOTTOM_MARGIN_MM + (showFooter ? PDF_FOOTER_HEIGHT_MM + PDF_FOOTER_GAP_MM : 0)
    const contentHeight = pageHeight - contentTop - contentBottom
    const pageHeightInCanvasPx = Math.max(1, Math.round((canvas.width * contentHeight) / pageWidth))
    const [headerDataUrl, footerDataUrl] = await Promise.all([
      createHeaderDataUrl(pageWidth),
      createFooterDataUrl(pageWidth)
    ])

    let renderedPages = 0
    for (let offsetY = 0; offsetY < canvas.height; offsetY += pageHeightInCanvasPx) {
      const sourceOffsetY = renderedPages ? Math.max(0, offsetY - PDF_PAGE_SLICE_OVERLAP_PX) : offsetY
      const overlap = renderedPages ? PDF_PAGE_SLICE_OVERLAP_PX : 0
      const sliceHeight = Math.min(pageHeightInCanvasPx + overlap, canvas.height - sourceOffsetY)
      const pageCanvas = document.createElement('canvas')
      pageCanvas.width = canvas.width
      pageCanvas.height = sliceHeight
      const context = pageCanvas.getContext('2d')
      if (!context) throw new TypeError('PDF_PAGE_CONTEXT_ERROR')
      context.fillStyle = '#fff'
      context.fillRect(0, 0, pageCanvas.width, pageCanvas.height)
      context.drawImage(
        canvas,
        0,
        sourceOffsetY,
        canvas.width,
        sliceHeight,
        0,
        0,
        pageCanvas.width,
        pageCanvas.height
      )
      if (renderedPages) pdf.addPage()
      const renderedHeight = Math.min(contentHeight, (sliceHeight * pageWidth) / canvas.width)
      pdf.addImage(pageCanvas.toDataURL('image/png'), 'PNG', 0, contentTop, pageWidth, renderedHeight, undefined, 'FAST')
      if (showHeader) {
        pdf.addImage(
          headerDataUrl,
          'PNG',
          PDF_SIDE_MARGIN_MM,
          PDF_TOP_MARGIN_MM,
          pageWidth - PDF_SIDE_MARGIN_MM * 2,
          PDF_HEADER_HEIGHT_MM,
          undefined,
          'FAST'
        )
      }
      if (showFooter) {
        pdf.addImage(
          footerDataUrl,
          'PNG',
          PDF_SIDE_MARGIN_MM,
          pageHeight - PDF_BOTTOM_MARGIN_MM - PDF_FOOTER_HEIGHT_MM,
          pageWidth - PDF_SIDE_MARGIN_MM * 2,
          PDF_FOOTER_HEIGHT_MM,
          undefined,
          'FAST'
        )
      }
      renderedPages += 1
    }

    pdf.save(options.fileName)
  } finally {
    measurementStyle.remove()
    exportHost.remove()
    if (sourceElementId) {
      sourceElement.setAttribute('id', sourceElementId)
    }
  }
}
