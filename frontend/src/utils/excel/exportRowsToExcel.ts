import type { Worksheet } from 'exceljs'
import excelJsBundleUrl from 'exceljs/dist/exceljs.min.js?url'
import pdfLeftLogoUrl from '@/assets/images/pdf/dp-pdf-left-logo.svg?url'
import pdfRightLogoUrl from '@/assets/images/pdf/dp-pdf-right-logo.svg?url'
import { loadBrowserGlobal } from '@/utils/browser/loadBrowserGlobal'
import { DEFAULT_REPORT_OWNER, resolveReportExportChrome } from '@/utils/report/exportReportChrome'

export interface ExcelExportColumn<T> {
  key: string;
  header: string;
  width?: number;
  value?: (row: T, index: number) => unknown;
}

export interface ExcelExportMetadataItem {
  label: string;
  value: string;
}

export interface ExcelLegendItem {
  label: string;
  description?: string;
  fillColor: string;
  borderColor?: string;
  textColor?: string;
}

export interface ExcelMetricBarItem {
  label: string;
  value: number;
  color: string;
  valueLabel?: string;
  helper?: string;
  trackColor?: string;
}

export interface ExcelRowStyle {
  fillColor?: string;
  borderColor?: string;
  textColor?: string;
  strikeThrough?: boolean;
  rowHeight?: number;
}

export interface ExcelCellStyle {
  fillColor?: string;
  borderColor?: string;
  textColor?: string;
  bold?: boolean;
  italic?: boolean;
  horizontal?: 'left' | 'center' | 'right';
  wrapText?: boolean;
}

export interface ExcelCellStyleContext<T> {
  row: T;
  rowIndex: number;
  column: ExcelExportColumn<T>;
  columnIndex: number;
  rawValue: unknown;
  normalizedValue: string;
  rowStyle?: ExcelRowStyle | null;
}

export interface ExportRowsToExcelOptions<T> {
  fileName: string;
  sheetName: string;
  title: string;
  locale?: string;
  subtitle?: string;
  headerTitle?: string;
  headerDescription?: string;
  columns: ExcelExportColumn<T>[];
  rows: T[];
  metadata?: ExcelExportMetadataItem[];
  footerNote?: string;
  rtl?: boolean;
  emptyValue?: string;
  legendTitle?: string;
  legendItems?: ExcelLegendItem[];
  metricBarsTitle?: string;
  metricBars?: ExcelMetricBarItem[];
  getRowStyle?: (row: T, index: number) => ExcelRowStyle | null | undefined;
  getCellStyle?: (
    context: ExcelCellStyleContext<T>
  ) => ExcelCellStyle | null | undefined;
  autoFitColumns?: boolean;
  autoFitColumnSampleSize?: number;
  autoFitColumnPadding?: number;
  minAutoFitWidth?: number;
  maxAutoFitWidth?: number;
  freezeHeaderRow?: boolean;
}

const EXCEL_MIME_TYPE =
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
const DEFAULT_EMPTY_VALUE = '-'
const COLOR_BRAND_DARK = 'FF006C44'
const COLOR_BRAND = 'FF008755'
const COLOR_BRAND_SOFT = 'FFF4FBF8'
const COLOR_SURFACE = 'FFFFFFFF'
const COLOR_BORDER = 'FFD0D5DD'
const COLOR_TEXT_PRIMARY = 'FF101828'
const COLOR_TEXT_SECONDARY = 'FF667085'
const COLOR_TEXT_MUTED = 'FF475467'
const COLOR_TABLE_ALT = 'FFF7FAF9'
const EXCEL_HEADER_TITLE_ROW_HEIGHT = 42
const EXCEL_HEADER_SUBTITLE_ROW_HEIGHT = 30
const EXCEL_LOGO_COLUMN_WIDTH = 30
const EXCEL_LOGO_MAX_WIDTH = 176
const EXCEL_LOGO_MAX_HEIGHT = 74
const EXCEL_LOGO_MIN_PADDING = 8
const DEFAULT_AUTOFIT_SAMPLE_SIZE = 100
const LARGE_EXPORT_ROW_THRESHOLD = 750
const LARGE_EXPORT_CELL_THRESHOLD = 12000

interface ExcelLogoRenderOptions {
  displayWidth: number;
  displayHeight: number;
  renderScale?: number;
  paddingX?: number;
  paddingY?: number;
  focusScale?: number;
}

interface ExcelLogoPlacement extends ExcelLogoRenderOptions {
  imageUrl: string;
  anchorColumn: number;
  anchorRow: number;
}

interface ExcelLayoutConfig {
  isRtl: boolean;
  locale: string;
  readingOrder: 'rtl' | 'ltr';
  textAlign: 'left' | 'right';
  metadataValueAlign: 'left' | 'right';
  downloadDateLabel: string;
  legendTitle: string;
  metricBarsTitle: string;
  yesLabel: string;
  noLabel: string;
}

const imageDataUrlCache = new Map<string, Promise<string>>()
let excelJsModulePromise: Promise<typeof import('exceljs')> | null = null

const loadExcelJsModule = async (): Promise<typeof import('exceljs')> => {
  if (!excelJsModulePromise) {
    excelJsModulePromise = loadBrowserGlobal<typeof import('exceljs')>(
      excelJsBundleUrl,
      'ExcelJS'
    )
  }

  return excelJsModulePromise
}

const sanitizeWorkbookName = (value: string) =>
  value.replaceAll(/[\\/?*:[\]]/g, ' ').trim() || 'Sheet1'
const normalizeExcelFileName = (value: string) => {
  const normalizedValue = value.trim().replace(/\.(xlsx|xls)$/i, '')
  return `${normalizedValue || 'report'}.xlsx`
}

const sanitizeForExcelFormula = (value: string) => {
  const trimmed = value.trim()

  if (!trimmed) {
    return trimmed
  }

  return /^[=+\-@]/.test(trimmed) ? `'${trimmed}` : trimmed
}

const getDownloadDate = (locale = 'ar-AE') =>
  new Intl.DateTimeFormat(locale, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date())

const resolveExcelLayoutConfig = (
  options: Pick<
    ExportRowsToExcelOptions<unknown>,
    'rtl' | 'locale' | 'legendTitle' | 'metricBarsTitle'
  >
): ExcelLayoutConfig => {
  const locale = options.locale?.trim() || 'ar'
  const isRtl = options.rtl ?? locale.toLowerCase().startsWith('ar')

  return {
    isRtl,
    locale,
    readingOrder: isRtl ? 'rtl' : 'ltr',
    textAlign: isRtl ? 'right' : 'left',
    metadataValueAlign: isRtl ? 'right' : 'left',
    downloadDateLabel: isRtl ? 'تاريخ التصدير' : 'Export date',
    legendTitle: options.legendTitle ?? (isRtl ? 'دلالات الجدول' : 'Insights'),
    metricBarsTitle:
      options.metricBarsTitle ?? (isRtl ? 'مؤشرات النسب' : 'Analytics Overview'),
    yesLabel: isRtl ? 'نعم' : 'Yes',
    noLabel: isRtl ? 'لا' : 'No'
  }
}

const normalizeCellValue = (
  value: unknown,
  emptyValue: string,
  layout: ExcelLayoutConfig
) => {
  if (value === null || value === undefined || value === '') {
    return emptyValue
  }

  if (typeof value === 'boolean') {
    return value ? layout.yesLabel : layout.noLabel
  }

  if (
    typeof value === 'string' ||
    typeof value === 'number' ||
    typeof value === 'bigint'
  ) {
    return sanitizeForExcelFormula(String(value))
  }

  if (value instanceof Date) {
    return sanitizeForExcelFormula(value.toISOString())
  }

  try {
    return sanitizeForExcelFormula(JSON.stringify(value))
  } catch {
    return emptyValue
  }
}

const getLongestCellLineLength = (value: string) =>
  value
    .split(/\r?\n/)
    .reduce((maxLength, line) => Math.max(maxLength, line.trim().length), 0)

const estimateColumnWidth = <T>(
  column: ExcelExportColumn<T>,
  rows: T[],
  emptyValue: string,
  layout: ExcelLayoutConfig,
  options?: {
    autoFitColumns?: boolean;
    autoFitColumnSampleSize?: number;
    autoFitColumnPadding?: number;
    minAutoFitWidth?: number;
    maxAutoFitWidth?: number;
  }
) => {
  if (column.width && !options?.autoFitColumns) {
    return column.width
  }

  const sampleSize = options?.autoFitColumns
    ? Math.max(
      Math.min(
        options.autoFitColumnSampleSize ?? DEFAULT_AUTOFIT_SAMPLE_SIZE,
        rows.length,
        DEFAULT_AUTOFIT_SAMPLE_SIZE
      ),
      1
    )
    : 50

  const sampleValues = rows.slice(0, sampleSize).map((row, index) => {
    const rawValue = column.value
      ? column.value(row, index)
      : (row as Record<string, unknown>)[column.key]
    return String(normalizeCellValue(rawValue, emptyValue, layout))
  })

  const maxLength = Math.max(
    getLongestCellLineLength(column.header),
    ...sampleValues.map((value) => getLongestCellLineLength(value))
  )
  const padding = options?.autoFitColumns
    ? (options.autoFitColumnPadding ?? 5)
    : 4
  const minWidth = Math.max(column.width ?? options?.minAutoFitWidth ?? 14, 8)
  const maxWidth = options?.maxAutoFitWidth ?? 42

  return Math.min(Math.max(maxLength + padding, minWidth), maxWidth)
}

const normalizeExcelColor = (
  value: string | undefined | null,
  fallback: string
) => {
  const normalizedValue = value?.trim()

  if (!normalizedValue) {
    return fallback
  }

  const cleanedValue = normalizedValue.replace('#', '').toUpperCase()

  if (/^[A-F0-9]{8}$/.test(cleanedValue)) {
    return cleanedValue
  }

  if (/^[A-F0-9]{6}$/.test(cleanedValue)) {
    return `FF${cleanedValue}`
  }

  return fallback
}

const getColumnWidthUnits = (worksheet: Worksheet, columnNumber: number) =>
  Math.max(Number(worksheet.getColumn(columnNumber).width ?? 14), 8)

const getRangeWidthUnits = (
  worksheet: Worksheet,
  startColumn: number,
  endColumn: number
) => {
  let totalWidth = 0

  for (
    let columnNumber = startColumn;
    columnNumber <= endColumn;
    columnNumber += 1
  ) {
    totalWidth += getColumnWidthUnits(worksheet, columnNumber)
  }

  return totalWidth
}

const estimateWrappedLineCount = (
  value: string,
  widthUnits: number,
  wrapText = true
) => {
  const normalizedValue = value.replaceAll('\t', ' ').trim()

  if (!normalizedValue) {
    return 1
  }

  if (!wrapText) {
    return 1
  }

  const maxCharsPerLine = Math.max(Math.floor(widthUnits * 1.2), 8)

  return normalizedValue
    .split(/\r?\n/)
    .filter((line) => line.length > 0)
    .reduce(
      (totalLines, line) =>
        totalLines + Math.max(1, Math.ceil(line.length / maxCharsPerLine)),
      0
    )
}

const estimateRowHeight = (
  cells: Array<{ value: string; widthUnits: number; wrapText?: boolean }>,
  minimumHeight = 22
) => {
  const maxLines = cells.reduce(
    (currentMax, cell) =>
      Math.max(
        currentMax,
        estimateWrappedLineCount(
          cell.value,
          cell.widthUnits,
          cell.wrapText ?? true
        )
      ),
    1
  )

  return Math.max(minimumHeight, 18 + (maxLines - 1) * 13)
}

const columnWidthToPixels = (width: number) =>
  Math.max(Math.round(width * 7 + 5), 72)
const resolveLogoDisplayWidth = (columnWidth: number) =>
  Math.max(
    Math.min(columnWidth - EXCEL_LOGO_MIN_PADDING * 2, EXCEL_LOGO_MAX_WIDTH),
    132
  )
const resolveLogoOffset = (columnWidth: number, displayWidth: number) =>
  Math.max((columnWidth - displayWidth) / 2, EXCEL_LOGO_MIN_PADDING)

const downloadBlob = (blob: Blob, fileName: string) => {
  const downloadUrl = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = downloadUrl
  link.download = fileName
  document.body.appendChild(link)
  link.click()
  link.remove()
  URL.revokeObjectURL(downloadUrl)
}

const buildBorder = (argb = COLOR_BORDER) => ({
  top: { style: 'thin' as const, color: { argb } },
  left: { style: 'thin' as const, color: { argb } },
  bottom: { style: 'thin' as const, color: { argb } },
  right: { style: 'thin' as const, color: { argb } }
})

const applyCellStyle = (
  worksheet: Worksheet,
  rowNumber: number,
  columnNumber: number,
  style: {
    fill?: Record<string, unknown>;
    font?: Record<string, unknown>;
    alignment?: Record<string, unknown>;
    border?: Record<string, unknown>;
  }
) => {
  const cell = worksheet.getCell(rowNumber, columnNumber)

  if (style.fill) {
    cell.fill = style.fill as never
  }

  if (style.font) {
    cell.font = style.font as never
  }

  if (style.alignment) {
    cell.alignment = style.alignment as never
  }

  if (style.border) {
    cell.border = style.border as never
  }
}

const applyRangeStyle = (
  worksheet: Worksheet,
  startRow: number,
  startColumn: number,
  endRow: number,
  endColumn: number,
  style: Parameters<typeof applyCellStyle>[3]
) => {
  for (let rowNumber = startRow; rowNumber <= endRow; rowNumber += 1) {
    for (
      let columnNumber = startColumn;
      columnNumber <= endColumn;
      columnNumber += 1
    ) {
      applyCellStyle(worksheet, rowNumber, columnNumber, style)
    }
  }
}

const addMergedValue = (
  worksheet: Worksheet,
  rowNumber: number,
  startColumn: number,
  endColumn: number,
  value: string,
  style: Parameters<typeof applyCellStyle>[3]
) => {
  worksheet.mergeCells(rowNumber, startColumn, rowNumber, endColumn)
  worksheet.getCell(rowNumber, startColumn).value = value
  applyRangeStyle(
    worksheet,
    rowNumber,
    startColumn,
    rowNumber,
    endColumn,
    style
  )
}

const renderSectionHeading = (
  worksheet: Worksheet,
  rowNumber: number,
  layoutColumnCount: number,
  title: string,
  layout: ExcelLayoutConfig
) => {
  addMergedValue(worksheet, rowNumber, 1, layoutColumnCount, title, {
    fill: {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: COLOR_BRAND_SOFT }
    },
    font: {
      name: 'Dubai',
      bold: true,
      size: 12,
      color: { argb: COLOR_BRAND_DARK }
    },
    alignment: {
      horizontal: layout.textAlign,
      vertical: 'middle',
      readingOrder: layout.readingOrder
    },
    border: buildBorder(COLOR_BRAND)
  })
  worksheet.getRow(rowNumber).height = estimateRowHeight(
    [
      {
        value: title,
        widthUnits: getRangeWidthUnits(worksheet, 1, layoutColumnCount)
      }
    ],
    22
  )

  return rowNumber + 1
}

const renderLegendSection = (
  worksheet: Worksheet,
  rowNumber: number,
  layoutColumnCount: number,
  title: string,
  items: ExcelLegendItem[],
  layout: ExcelLayoutConfig
) => {
  let currentRow = renderSectionHeading(
    worksheet,
    rowNumber,
    layoutColumnCount,
    title,
    layout
  )

  items.forEach((item) => {
    const fillColor = normalizeExcelColor(item.fillColor, COLOR_BRAND_SOFT)
    const borderColor = normalizeExcelColor(item.borderColor, fillColor)
    const textColor = normalizeExcelColor(item.textColor, COLOR_TEXT_PRIMARY)

    worksheet.getCell(currentRow, 1).value = item.label
    applyCellStyle(worksheet, currentRow, 1, {
      fill: { type: 'pattern', pattern: 'solid', fgColor: { argb: fillColor } },
      font: { name: 'Dubai', bold: true, size: 11, color: { argb: textColor } },
      alignment: {
        horizontal: 'center',
        vertical: 'middle',
        readingOrder: layout.readingOrder,
        wrapText: true
      },
      border: buildBorder(borderColor)
    })

    addMergedValue(
      worksheet,
      currentRow,
      2,
      layoutColumnCount,
      item.description ?? '',
      {
        fill: {
          type: 'pattern',
          pattern: 'solid',
          fgColor: { argb: COLOR_SURFACE }
        },
        font: { name: 'Dubai', size: 10, color: { argb: COLOR_TEXT_MUTED } },
        alignment: {
          horizontal: layout.textAlign,
          vertical: 'middle',
          readingOrder: layout.readingOrder,
          wrapText: true
        },
        border: buildBorder()
      }
    )

    worksheet.getRow(currentRow).height = estimateRowHeight(
      [
        {
          value: item.label,
          widthUnits: getColumnWidthUnits(worksheet, 1)
        },
        {
          value: item.description ?? '',
          widthUnits: getRangeWidthUnits(worksheet, 2, layoutColumnCount)
        }
      ],
      item.description ? 24 : 20
    )
    currentRow += 1
  })

  return currentRow
}

const renderMetricBarsSection = (
  worksheet: Worksheet,
  rowNumber: number,
  layoutColumnCount: number,
  title: string,
  items: ExcelMetricBarItem[],
  layout: ExcelLayoutConfig
) => {
  let currentRow = renderSectionHeading(
    worksheet,
    rowNumber,
    layoutColumnCount,
    title,
    layout
  )
  const labelColumn = 1
  const valueColumn = layoutColumnCount
  const trackStartColumn = Math.min(2, valueColumn)
  const trackEndColumn = Math.max(trackStartColumn, valueColumn - 1)
  const trackCellCount = Math.max(trackEndColumn - trackStartColumn + 1, 1)

  items.forEach((item) => {
    const normalizedValue = Math.max(0, Math.min(Number(item.value || 0), 100))
    const barColor = normalizeExcelColor(item.color, COLOR_BRAND)
    const trackColor = normalizeExcelColor(item.trackColor, COLOR_TABLE_ALT)
    const filledCells =
      normalizedValue > 0
        ? Math.max(1, Math.round((normalizedValue / 100) * trackCellCount))
        : 0

    worksheet.getCell(currentRow, labelColumn).value = item.label
    applyCellStyle(worksheet, currentRow, labelColumn, {
      fill: {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: COLOR_SURFACE }
      },
      font: {
        name: 'Dubai',
        bold: true,
        size: 11,
        color: { argb: COLOR_TEXT_PRIMARY }
      },
      alignment: {
        horizontal: layout.textAlign,
        vertical: 'middle',
        readingOrder: layout.readingOrder
      },
      border: buildBorder()
    })

    for (
      let columnNumber = trackStartColumn;
      columnNumber <= trackEndColumn;
      columnNumber += 1
    ) {
      const isFilled = columnNumber - trackStartColumn < filledCells
      applyCellStyle(worksheet, currentRow, columnNumber, {
        fill: {
          type: 'pattern',
          pattern: 'solid',
          fgColor: { argb: isFilled ? barColor : trackColor }
        },
        border: buildBorder(isFilled ? barColor : COLOR_BORDER)
      })
    }

    worksheet.getCell(currentRow, valueColumn).value =
      item.valueLabel ?? `${normalizedValue.toFixed(1)}%`
    applyCellStyle(worksheet, currentRow, valueColumn, {
      fill: {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: COLOR_SURFACE }
      },
      font: {
        name: 'Dubai',
        bold: true,
        size: 11,
        color: { argb: COLOR_TEXT_PRIMARY }
      },
      alignment: {
        horizontal: 'center',
        vertical: 'middle',
        readingOrder: layout.readingOrder
      },
      border: buildBorder()
    })
    worksheet.getRow(currentRow).height = estimateRowHeight(
      [
        {
          value: item.label,
          widthUnits: getColumnWidthUnits(worksheet, labelColumn)
        },
        {
          value: item.valueLabel ?? `${normalizedValue.toFixed(1)}%`,
          widthUnits: getColumnWidthUnits(worksheet, valueColumn),
          wrapText: false
        }
      ],
      18
    )
    currentRow += 1

    if (item.helper) {
      addMergedValue(worksheet, currentRow, 2, layoutColumnCount, item.helper, {
        fill: {
          type: 'pattern',
          pattern: 'solid',
          fgColor: { argb: COLOR_SURFACE }
        },
        font: {
          name: 'Dubai',
          size: 10,
          color: { argb: COLOR_TEXT_SECONDARY }
        },
        alignment: {
          horizontal: layout.textAlign,
          vertical: 'middle',
          readingOrder: layout.readingOrder,
          wrapText: true
        },
        border: buildBorder()
      })
      applyCellStyle(worksheet, currentRow, 1, {
        fill: {
          type: 'pattern',
          pattern: 'solid',
          fgColor: { argb: COLOR_SURFACE }
        },
        border: buildBorder()
      })
      worksheet.getRow(currentRow).height = estimateRowHeight(
        [
          {
            value: item.helper,
            widthUnits: getRangeWidthUnits(worksheet, 2, layoutColumnCount)
          }
        ],
        18
      )
      currentRow += 1
    }
  })

  return currentRow
}

const cropCanvasToVisibleBounds = (canvas: HTMLCanvasElement) => {
  const context = canvas.getContext('2d')
  if (!context) {
    throw new TypeError('EXCEL_IMAGE_CANVAS_ERROR')
  }

  const { width, height } = canvas
  const imageData = context.getImageData(0, 0, width, height)
  const { data } = imageData

  let minX = width
  let minY = height
  let maxX = -1
  let maxY = -1

  for (let y = 0; y < height; y += 1) {
    for (let x = 0; x < width; x += 1) {
      const alphaIndex = (y * width + x) * 4 + 3
      if (data[alphaIndex] <= 8) {
        continue
      }

      minX = Math.min(minX, x)
      minY = Math.min(minY, y)
      maxX = Math.max(maxX, x)
      maxY = Math.max(maxY, y)
    }
  }

  if (maxX < 0 || maxY < 0) {
    return canvas
  }

  const croppedCanvas = document.createElement('canvas')
  croppedCanvas.width = maxX - minX + 1
  croppedCanvas.height = maxY - minY + 1
  const croppedContext = croppedCanvas.getContext('2d')

  if (!croppedContext) {
    throw new TypeError('EXCEL_IMAGE_CANVAS_ERROR')
  }

  croppedContext.drawImage(
    canvas,
    minX,
    minY,
    croppedCanvas.width,
    croppedCanvas.height,
    0,
    0,
    croppedCanvas.width,
    croppedCanvas.height
  )

  return croppedCanvas
}

const loadImageAsPngDataUrl = async (
  imageUrl: string,
  options: ExcelLogoRenderOptions
) => {
  const cacheKey = JSON.stringify({ imageUrl, options })
  const cachedImage = imageDataUrlCache.get(cacheKey)
  if (cachedImage !== undefined) {
    return cachedImage
  }

  const imagePromise = (async () => {
    const response = await fetch(imageUrl)
    if (!response.ok) {
      throw new Error(`EXCEL_IMAGE_LOAD_FAILED:${response.status}`)
    }

    const blob = await response.blob()
    const objectUrl = URL.createObjectURL(blob)

    try {
      return await new Promise<string>((resolve, reject) => {
        const image = new Image()
        image.onload = () => {
          const naturalWidth = Math.max(
            image.naturalWidth || image.width || 240,
            120
          )
          const naturalHeight = Math.max(
            image.naturalHeight || image.height || 80,
            60
          )
          const oversampleScale = Math.max(options.renderScale ?? 2, 3)
          const sourceCanvas = document.createElement('canvas')
          sourceCanvas.width = Math.round(naturalWidth * oversampleScale)
          sourceCanvas.height = Math.round(naturalHeight * oversampleScale)

          const context = sourceCanvas.getContext('2d')
          if (!context) {
            reject(new TypeError('EXCEL_IMAGE_CANVAS_ERROR'))
            return
          }

          context.clearRect(0, 0, sourceCanvas.width, sourceCanvas.height)
          context.imageSmoothingEnabled = true
          context.imageSmoothingQuality = 'high'
          context.drawImage(
            image,
            0,
            0,
            sourceCanvas.width,
            sourceCanvas.height
          )

          const croppedCanvas = cropCanvasToVisibleBounds(sourceCanvas)
          const renderScale = options.renderScale ?? 2
          const paddingX = options.paddingX ?? 16
          const paddingY = options.paddingY ?? 10
          const targetCanvas = document.createElement('canvas')
          targetCanvas.width = Math.round(options.displayWidth * renderScale)
          targetCanvas.height = Math.round(options.displayHeight * renderScale)

          const targetContext = targetCanvas.getContext('2d')
          if (!targetContext) {
            reject(new TypeError('EXCEL_IMAGE_CANVAS_ERROR'))
            return
          }

          const availableWidth =
            targetCanvas.width - paddingX * renderScale * 2
          const availableHeight =
            targetCanvas.height - paddingY * renderScale * 2
          const baseScale = Math.min(
            availableWidth / croppedCanvas.width,
            availableHeight / croppedCanvas.height
          )
          const scale = baseScale * (options.focusScale ?? 1)
          const drawWidth = croppedCanvas.width * scale
          const drawHeight = croppedCanvas.height * scale
          const offsetX = (targetCanvas.width - drawWidth) / 2
          const offsetY = (targetCanvas.height - drawHeight) / 2

          targetContext.clearRect(
            0,
            0,
            targetCanvas.width,
            targetCanvas.height
          )
          targetContext.fillStyle = '#FFFFFF'
          targetContext.fillRect(0, 0, targetCanvas.width, targetCanvas.height)
          targetContext.imageSmoothingEnabled = true
          targetContext.imageSmoothingQuality = 'high'
          targetContext.drawImage(
            croppedCanvas,
            offsetX,
            offsetY,
            drawWidth,
            drawHeight
          )

          resolve(targetCanvas.toDataURL('image/png'))
        }

        image.onerror = () => reject(new Error('EXCEL_IMAGE_DECODE_FAILED'))
        image.src = objectUrl
      })
    } finally {
      URL.revokeObjectURL(objectUrl)
    }
  })()

  imageDataUrlCache.set(cacheKey, imagePromise)

  try {
    return await imagePromise
  } catch (error) {
    imageDataUrlCache.delete(cacheKey)
    throw error
  }
}

const addWorksheetLogos = async (
  worksheet: Worksheet,
  totalColumns: number
) => {
  const firstColumnWidth = columnWidthToPixels(
    worksheet.getColumn(1).width ?? EXCEL_LOGO_COLUMN_WIDTH
  )
  const lastColumnWidth = columnWidthToPixels(
    worksheet.getColumn(totalColumns).width ?? EXCEL_LOGO_COLUMN_WIDTH
  )
  const rightLogoWidth = resolveLogoDisplayWidth(firstColumnWidth)
  const leftLogoWidth = resolveLogoDisplayWidth(lastColumnWidth)
  const logoPlacements: ExcelLogoPlacement[] = [
    {
      imageUrl: pdfRightLogoUrl,
      anchorColumn:
        resolveLogoOffset(firstColumnWidth, rightLogoWidth) / firstColumnWidth,
      anchorRow: 0.08,
      displayWidth: rightLogoWidth,
      displayHeight: EXCEL_LOGO_MAX_HEIGHT,
      renderScale: 3.5,
      paddingX: 8,
      paddingY: 6,
      focusScale: 1.04
    },
    {
      imageUrl: pdfLeftLogoUrl,
      anchorColumn:
        totalColumns -
        1 +
        resolveLogoOffset(lastColumnWidth, leftLogoWidth) / lastColumnWidth,
      anchorRow: 0.08,
      displayWidth: leftLogoWidth,
      displayHeight: EXCEL_LOGO_MAX_HEIGHT,
      renderScale: 4,
      paddingX: 4,
      paddingY: 5,
      focusScale: 1.08
    }
  ]

  const renderedLogos = await Promise.allSettled(
    logoPlacements.map((placement) =>
      loadImageAsPngDataUrl(placement.imageUrl, placement)
    )
  )

  renderedLogos.forEach((renderedLogo, index) => {
    if (renderedLogo.status !== 'fulfilled') {
      return
    }

    const placement = logoPlacements[index]
    const imageId = worksheet.workbook.addImage({
      base64: renderedLogo.value,
      extension: 'png'
    })

    worksheet.addImage(imageId, {
      tl: { col: placement.anchorColumn, row: placement.anchorRow },
      ext: {
        width: placement.displayWidth,
        height: placement.displayHeight
      },
      editAs: 'oneCell'
    })
  })
}

export const exportRowsToExcel = async <T>(
  options: ExportRowsToExcelOptions<T>
) => {
  const ExcelJS = await loadExcelJsModule()
  const emptyValue = options.emptyValue ?? DEFAULT_EMPTY_VALUE
  const layout = resolveExcelLayoutConfig(
    options as ExportRowsToExcelOptions<unknown>
  )
  const chromeContent = resolveReportExportChrome({
    headerTitle: options.headerTitle ?? options.title,
    headerDescription: options.headerDescription ?? options.subtitle,
    footerNote: options.footerNote
  })
  const metadata = [
    ...(options.metadata ?? []),
    { label: layout.downloadDateLabel, value: getDownloadDate(layout.locale) }
  ]
  const normalizedSheetName = sanitizeWorkbookName(options.sheetName)
  const normalizedFileName = normalizeExcelFileName(options.fileName)
  const layoutColumnCount = Math.max(
    options.columns.length,
    options.metricBars?.length ? 6 : 3
  )
  const workbook = new ExcelJS.Workbook()
  const worksheet = workbook.addWorksheet(normalizedSheetName, {
    views: [{ rightToLeft: layout.isRtl, showGridLines: false }]
  })
  const useLightweightBodyLayout =
    options.rows.length > LARGE_EXPORT_ROW_THRESHOLD ||
    options.rows.length * options.columns.length > LARGE_EXPORT_CELL_THRESHOLD

  workbook.creator = DEFAULT_REPORT_OWNER
  workbook.lastModifiedBy = DEFAULT_REPORT_OWNER
  workbook.created = new Date()
  workbook.modified = new Date()
  workbook.company = 'Dubai Police'
  workbook.title = chromeContent.headerTitle
  workbook.subject = chromeContent.headerDescription
  workbook.description = chromeContent.footerNote

  worksheet.pageSetup = {
    orientation: layoutColumnCount > 6 ? 'landscape' : 'portrait',
    fitToPage: true,
    fitToWidth: 1,
    fitToHeight: 0,
    margins: {
      left: 0.35,
      right: 0.35,
      top: 0.55,
      bottom: 0.55,
      header: 0.3,
      footer: 0.3
    }
  }

  Array.from({ length: layoutColumnCount }).forEach((_, index) => {
    let width =
      index < options.columns.length
        ? estimateColumnWidth(
          options.columns[index],
          options.rows,
          emptyValue,
          layout,
          {
            autoFitColumns: options.autoFitColumns,
            autoFitColumnSampleSize: options.autoFitColumnSampleSize,
            autoFitColumnPadding: options.autoFitColumnPadding,
            minAutoFitWidth: options.minAutoFitWidth,
            maxAutoFitWidth: options.maxAutoFitWidth
          }
        )
        : 16

    if (index === 0 || index === layoutColumnCount - 1) {
      width = Math.max(width, EXCEL_LOGO_COLUMN_WIDTH)
    }

    worksheet.getColumn(index + 1).width = width
  })

  worksheet.mergeCells(1, 1, 2, 1)
  worksheet.mergeCells(1, layoutColumnCount, 2, layoutColumnCount)
  addMergedValue(
    worksheet,
    1,
    2,
    layoutColumnCount - 1,
    chromeContent.headerTitle,
    {
      fill: {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: COLOR_BRAND_SOFT }
      },
      font: {
        name: 'Dubai',
        bold: true,
        size: 16,
        color: { argb: COLOR_BRAND_DARK }
      },
      alignment: {
        horizontal: 'center',
        vertical: 'middle',
        readingOrder: layout.readingOrder
      },
      border: buildBorder(COLOR_BRAND)
    }
  )
  addMergedValue(
    worksheet,
    2,
    2,
    layoutColumnCount - 1,
    chromeContent.headerDescription,
    {
      fill: {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: COLOR_BRAND_SOFT }
      },
      font: { name: 'Dubai', size: 10, color: { argb: COLOR_TEXT_SECONDARY } },
      alignment: {
        horizontal: 'center',
        vertical: 'middle',
        readingOrder: layout.readingOrder
      },
      border: buildBorder(COLOR_BRAND)
    }
  )
  applyRangeStyle(worksheet, 1, 1, 2, 1, {
    fill: {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: COLOR_SURFACE }
    },
    border: buildBorder(COLOR_BRAND)
  })
  applyRangeStyle(worksheet, 1, layoutColumnCount, 2, layoutColumnCount, {
    fill: {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: COLOR_SURFACE }
    },
    border: buildBorder(COLOR_BRAND)
  })

  worksheet.getRow(1).height = EXCEL_HEADER_TITLE_ROW_HEIGHT
  worksheet.getRow(2).height = EXCEL_HEADER_SUBTITLE_ROW_HEIGHT
  worksheet.getRow(3).height = 10

  let currentRow = 4
  metadata.forEach((item) => {
    worksheet.getCell(currentRow, 1).value = item.label
    applyCellStyle(worksheet, currentRow, 1, {
      fill: {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: COLOR_BRAND_DARK }
      },
      font: {
        name: 'Dubai',
        bold: true,
        size: 11,
        color: { argb: COLOR_SURFACE }
      },
      alignment: {
        horizontal: 'center',
        vertical: 'middle',
        readingOrder: layout.readingOrder
      },
      border: buildBorder(COLOR_BRAND_DARK)
    })

    addMergedValue(worksheet, currentRow, 2, layoutColumnCount, item.value, {
      fill: {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: COLOR_SURFACE }
      },
      font: { name: 'Dubai', size: 11, color: { argb: COLOR_TEXT_MUTED } },
      alignment: {
        horizontal: layout.metadataValueAlign,
        vertical: 'middle',
        readingOrder: layout.readingOrder
      },
      border: buildBorder()
    })

    worksheet.getRow(currentRow).height = estimateRowHeight(
      [
        {
          value: item.label,
          widthUnits: getColumnWidthUnits(worksheet, 1)
        },
        {
          value: item.value,
          widthUnits: getRangeWidthUnits(worksheet, 2, layoutColumnCount)
        }
      ],
      20
    )
    currentRow += 1
  })

  currentRow += 1
  if ((options.legendItems?.length ?? 0) > 0) {
    currentRow = renderLegendSection(
      worksheet,
      currentRow,
      layoutColumnCount,
      layout.legendTitle,
      options.legendItems ?? [],
      layout
    )
    currentRow += 1
  }

  if ((options.metricBars?.length ?? 0) > 0) {
    currentRow = renderMetricBarsSection(
      worksheet,
      currentRow,
      layoutColumnCount,
      layout.metricBarsTitle,
      options.metricBars ?? [],
      layout
    )
    currentRow += 1
  }

  const headerRowNumber = currentRow

  options.columns.forEach((column, index) => {
    const cell = worksheet.getCell(headerRowNumber, index + 1)
    cell.value = column.header
    cell.fill = {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: COLOR_BRAND_DARK }
    } as never
    cell.font = {
      name: 'Dubai',
      bold: true,
      size: 11,
      color: { argb: COLOR_SURFACE }
    } as never
    cell.alignment = {
      horizontal: 'center',
      vertical: 'middle',
      readingOrder: layout.readingOrder,
      wrapText: true
    } as never
    cell.border = buildBorder(COLOR_BRAND_DARK) as never
  })
  worksheet.getRow(headerRowNumber).height = estimateRowHeight(
    options.columns.map((column, index) => ({
      value: column.header,
      widthUnits: getColumnWidthUnits(worksheet, index + 1)
    })),
    22
  )

  options.rows.forEach((row, rowIndex) => {
    const sheetRowNumber = headerRowNumber + rowIndex + 1
    const isEvenRow = rowIndex % 2 === 1
    const rowStyle = options.getRowStyle?.(row, rowIndex) ?? null
    const defaultFillColor = normalizeExcelColor(
      rowStyle?.fillColor,
      isEvenRow ? COLOR_TABLE_ALT : COLOR_SURFACE
    )
    const defaultBorderColor = normalizeExcelColor(
      rowStyle?.borderColor,
      COLOR_BORDER
    )
    const defaultTextColor = normalizeExcelColor(
      rowStyle?.textColor,
      COLOR_TEXT_PRIMARY
    )

    const rowHeightCells: Array<{
      value: string;
      widthUnits: number;
      wrapText?: boolean;
    }> = []

    options.columns.forEach((column, columnIndex) => {
      const rawValue = column.value
        ? column.value(row, rowIndex)
        : (row as Record<string, unknown>)[column.key]
      const normalizedValue = normalizeCellValue(rawValue, emptyValue, layout)
      const cellStyle =
        options.getCellStyle?.({
          row,
          rowIndex,
          column,
          columnIndex,
          rawValue,
          normalizedValue: String(normalizedValue),
          rowStyle
        }) ?? null
      const cellFillColor = normalizeExcelColor(
        cellStyle?.fillColor,
        defaultFillColor
      )
      const cellBorderColor = normalizeExcelColor(
        cellStyle?.borderColor,
        defaultBorderColor
      )
      const cellTextColor = normalizeExcelColor(
        cellStyle?.textColor,
        defaultTextColor
      )
      const cell = worksheet.getCell(sheetRowNumber, columnIndex + 1)
      cell.value = normalizedValue
      cell.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: cellFillColor }
      } as never
      cell.font = {
        name: 'Dubai',
        size: 11,
        color: { argb: cellTextColor },
        bold: Boolean(cellStyle?.bold),
        italic: Boolean(cellStyle?.italic),
        strike: Boolean(rowStyle?.strikeThrough)
      } as never
      cell.alignment = {
        horizontal: cellStyle?.horizontal ?? layout.textAlign,
        vertical: 'middle',
        readingOrder: layout.readingOrder,
        wrapText: useLightweightBodyLayout ? false : (cellStyle?.wrapText ?? true)
      } as never
      cell.border = buildBorder(cellBorderColor) as never

      if (!useLightweightBodyLayout) {
        rowHeightCells.push({
          value: String(normalizedValue),
          widthUnits: getColumnWidthUnits(worksheet, columnIndex + 1),
          wrapText: cellStyle?.wrapText ?? true
        })
      }
    })

    worksheet.getRow(sheetRowNumber).height = useLightweightBodyLayout
      ? Math.max(rowStyle?.rowHeight ?? 22, 22)
      : estimateRowHeight(
        rowHeightCells,
        Math.max(rowStyle?.rowHeight ?? 22, 22)
      )
  })

  if (options.rows.length > 0) {
    worksheet.autoFilter = {
      from: { row: headerRowNumber, column: 1 },
      to: { row: headerRowNumber, column: options.columns.length }
    }
  }

  currentRow = headerRowNumber + options.rows.length + 2
  addMergedValue(
    worksheet,
    currentRow,
    1,
    layoutColumnCount,
    `${chromeContent.footerBrandTitle} | ${chromeContent.footerBrandSubtitle}`,
    {
      fill: {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: COLOR_BRAND_DARK }
      },
      font: {
        name: 'Dubai',
        bold: true,
        size: 12,
        color: { argb: COLOR_SURFACE }
      },
      alignment: {
        horizontal: 'center',
        vertical: 'middle',
        readingOrder: layout.readingOrder
      },
      border: buildBorder(COLOR_BRAND_DARK)
    }
  )
  worksheet.getRow(currentRow).height = 22

  currentRow += 1
  addMergedValue(
    worksheet,
    currentRow,
    1,
    layoutColumnCount,
    `${chromeContent.footerCopyright} | ${chromeContent.footerOwner}`,
    {
      fill: {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: COLOR_BRAND }
      },
      font: { name: 'Dubai', size: 10, color: { argb: COLOR_SURFACE } },
      alignment: {
        horizontal: 'center',
        vertical: 'middle',
        readingOrder: layout.readingOrder
      },
      border: buildBorder(COLOR_BRAND)
    }
  )
  worksheet.getRow(currentRow).height = 20

  currentRow += 1
  addMergedValue(
    worksheet,
    currentRow,
    1,
    layoutColumnCount,
    chromeContent.footerNote,
    {
      fill: {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: COLOR_BRAND_SOFT }
      },
      font: {
        name: 'Dubai',
        italic: true,
        size: 10,
        color: { argb: COLOR_TEXT_MUTED }
      },
      alignment: {
        horizontal: 'center',
        vertical: 'middle',
        readingOrder: layout.readingOrder
      },
      border: buildBorder(COLOR_BRAND)
    }
  )
  worksheet.getRow(currentRow).height = 18

  worksheet.views = options.freezeHeaderRow
    ? [
      {
        rightToLeft: layout.isRtl,
        showGridLines: false,
        state: 'frozen',
        ySplit: headerRowNumber
      }
    ]
    : [
      {
        rightToLeft: layout.isRtl,
        showGridLines: false
      }
    ]

  await addWorksheetLogos(worksheet, layoutColumnCount)

  const buffer = await workbook.xlsx.writeBuffer()
  downloadBlob(
    new Blob([buffer], { type: EXCEL_MIME_TYPE }),
    normalizedFileName
  )
}
