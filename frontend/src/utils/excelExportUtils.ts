import xlsxBundleUrl from 'xlsx-js-style/dist/xlsx.bundle.js?url'
import { loadBrowserGlobal } from '@/utils/browser/loadBrowserGlobal'

export interface ExcelExportOptions {
  title: string
  data: Record<string, any>[]
  headers: Record<string, string>
  isArabic: boolean
  fileName?: string
  sheetName?: string
  metadata?: ExcelSheetMetadataItem[]
  columnWidths?: number[]
  statusColumns?: string[]
}

export interface ExcelSheetMetadataItem {
  label: string
  value: string | number | boolean | null | undefined
}

export interface ExcelWorkbookSheetOptions {
  sheetName: string
  title: string
  data: Record<string, any>[]
  headers: Record<string, string>
  metadata?: ExcelSheetMetadataItem[]
  columnWidths?: number[]
  statusColumns?: string[]
}

export interface ExcelWorkbookExportOptions {
  fileName: string
  isArabic: boolean
  sheets: ExcelWorkbookSheetOptions[]
}

export interface StatusColorMap {
  [key: string]: string
}

type ExportCellStyle = {
  fill?: {
    patternType: string
    fgColor: { rgb: string }
  }
  font?: {
    bold?: boolean
    color?: { rgb: string }
    sz?: number
  }
  alignment?: {
    horizontal?: 'left' | 'right' | 'center'
    vertical?: 'top' | 'center' | 'bottom'
    wrapText?: boolean
  }
  border?: {
    top?: { style: string; color: { rgb: string } }
    bottom?: { style: string; color: { rgb: string } }
    left?: { style: string; color: { rgb: string } }
    right?: { style: string; color: { rgb: string } }
  }
}

interface SheetBuildContext {
  headerKeys: string[]
  headerLabels: string[]
  headerRowIndex: number
  dataStartRowIndex: number
}

type XlsxModule = typeof import('xlsx-js-style')
type ExcelWorksheet = Record<string, any> & {
  ['!cols']?: Array<{ wch: number }>
  ['!merges']?: Array<{
    s: { r: number; c: number }
    e: { r: number; c: number }
  }>
  ['!freeze']?: { xSplit: number; ySplit: number }
}

let xlsxModulePromise: Promise<XlsxModule> | null = null

const loadXlsxModule = async (): Promise<XlsxModule> => {
  if (!xlsxModulePromise) {
    xlsxModulePromise = loadBrowserGlobal<XlsxModule>(xlsxBundleUrl, 'XLSX')
  }

  return xlsxModulePromise
}

const COLOR_BRAND = '006C44'
const COLOR_BRAND_ALT = '0B8A58'
const COLOR_SURFACE = 'F8FAFC'
const COLOR_SURFACE_ALT = 'F3F6F8'
const COLOR_BORDER = 'D0D5DD'
const COLOR_TEXT = '101828'
const defaultStatusColorMap: StatusColorMap = {
  Completed: '28A745',
  Open: '007BFF',
  Closed: 'DC3545',
  Voting: 'FFC107',
  Revised: '17A2B8',
  Paused: '6C757D',
  'Under Validator Review': 'FD7E14',
  'Under Admin Review': '6F42C1',
  New: '20C997',
  Urgent: 'E74C3C',
  Answered: '12B76A',
  Pending: 'F79009',
  'Not Answered': 'F79009',
  Active: '12B76A',
  Inactive: '98A2B3',
  مكتمل: '28A745',
  مفتوح: '007BFF',
  مغلق: 'DC3545',
  التصويت: 'FFC107',
  'تم مراجعته': '17A2B8',
  'الإيقاف المؤقت': '6C757D',
  'تحت مراجعة المدقق': 'FD7E14',
  'تحت مراجعة الأدمن': '6F42C1',
  جديد: '20C997',
  ضروري: 'E74C3C',
  'تمت الإجابة': '12B76A',
  'لم تتم الإجابة': 'F79009',
  نشط: '12B76A',
  'غير نشط': '98A2B3'
}

export const getStatusBackgroundColor = (status: string, customColorMap?: StatusColorMap): string => {
  const colorMap = customColorMap ?? defaultStatusColorMap
  return colorMap[status] ?? COLOR_SURFACE
}

const buildBorder = (rgb: string): NonNullable<ExportCellStyle['border']> => ({
  top: { style: 'thin', color: { rgb } },
  bottom: { style: 'thin', color: { rgb } },
  left: { style: 'thin', color: { rgb } },
  right: { style: 'thin', color: { rgb } }
})

const applyCellStyle = (
  xlsx: XlsxModule,
  worksheet: ExcelWorksheet,
  rowIndex: number,
  columnIndex: number,
  style: ExportCellStyle
) => {
  const cellAddress = xlsx.utils.encode_cell({ r: rowIndex, c: columnIndex })
  const cell = worksheet[cellAddress]

  if (cell) {
    cell.s = style
  }
}

const sanitizeSheetName = (value: string) => {
  const sanitized = (value || 'Sheet')
    .replace(/[\\/*?:[\]]/g, ' ')
    .trim()
    .slice(0, 31)

  return sanitized || 'Sheet'
}

const sanitizeFileSegment = (value: string) =>
  (value || 'report')
    .replace(/[<>:"/\\|?*\u0000-\u001F]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()

const normalizeFileName = (value: string) => {
  const sanitized = sanitizeFileSegment(value)
  return sanitized.endsWith('.xlsx') ? sanitized : `${sanitized}.xlsx`
}

const formatTimestamp = (date: Date) => {
  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  const ss = String(date.getSeconds()).padStart(2, '0')

  return `${yyyy}-${mm}-${dd}_${hh}-${min}-${ss}`
}

export const buildTimestampedExcelFileName = (baseName: string, date = new Date()) => {
  const normalizedBase = sanitizeFileSegment(baseName) || 'report'
  return normalizeFileName(`${normalizedBase}_${formatTimestamp(date)}`)
}

const normalizeCellValue = (value: unknown): string | number | boolean => {
  if (value === null || value === undefined || value === '') {
    return '-'
  }

  if (typeof value === 'number' || typeof value === 'boolean') {
    return value
  }

  if (value instanceof Date) {
    return value.toISOString()
  }

  return String(value)
}

const inferColumnWidths = (
  headerLabels: string[],
  rows: Record<string, any>[],
  headerKeys: string[],
  explicitWidths?: number[]
) => {
  if (explicitWidths?.length) {
    return explicitWidths.map((width) => ({ wch: width }))
  }

  return headerKeys.map((key, index) => {
    const headerLength = headerLabels[index]?.length ?? 10
    const dataLength = rows.reduce((max, row) => {
      const value = normalizeCellValue(row[key])
      return Math.max(max, String(value).length)
    }, 0)

    return { wch: Math.min(Math.max(headerLength, dataLength, 14), 42) }
  })
}

const createBaseRowStyle = (isArabic: boolean, fillColor: string): ExportCellStyle => ({
  fill: {
    patternType: 'solid',
    fgColor: { rgb: fillColor }
  },
  font: {
    color: { rgb: COLOR_TEXT },
    sz: 10
  },
  alignment: {
    horizontal: isArabic ? 'right' : 'left',
    vertical: 'center',
    wrapText: true
  },
  border: buildBorder(COLOR_BORDER)
})

const buildSheet = (
  xlsx: XlsxModule,
  worksheet: ExcelWorksheet,
  sheet: ExcelWorkbookSheetOptions
): SheetBuildContext => {
  const headerKeys = Object.keys(sheet.headers)
  const headerLabels = Object.values(sheet.headers)
  const metadata = sheet.metadata ?? []

  const rows: any[][] = [[sheet.title], []]

  metadata.forEach((item) => {
    rows.push([item.label, normalizeCellValue(item.value)])
  })

  if (metadata.length) {
    rows.push([])
  }

  const headerRowIndex = rows.length
  rows.push(headerLabels)

  sheet.data.forEach((row) => {
    rows.push(headerKeys.map((key) => normalizeCellValue(row[key])))
  })

  const builtSheet = xlsx.utils.aoa_to_sheet(rows)

  Object.assign(worksheet, builtSheet)
  worksheet['!cols'] = inferColumnWidths(headerLabels, sheet.data, headerKeys, sheet.columnWidths)
  worksheet['!merges'] = [
    {
      s: { r: 0, c: 0 },
      e: { r: 0, c: Math.max(headerLabels.length - 1, 1) }
    }
  ]
  worksheet['!freeze'] = { xSplit: 0, ySplit: headerRowIndex + 1 }

  return {
    headerKeys,
    headerLabels,
    headerRowIndex,
    dataStartRowIndex: headerRowIndex + 1
  }
}

const styleSheet = (
  xlsx: XlsxModule,
  worksheet: ExcelWorksheet,
  sheet: ExcelWorkbookSheetOptions,
  isArabic: boolean,
  context: SheetBuildContext
) => {
  const titleStyle: ExportCellStyle = {
    fill: {
      patternType: 'solid',
      fgColor: { rgb: COLOR_BRAND }
    },
    font: {
      bold: true,
      color: { rgb: 'FFFFFF' },
      sz: 14
    },
    alignment: {
      horizontal: isArabic ? 'right' : 'left',
      vertical: 'center',
      wrapText: true
    },
    border: buildBorder(COLOR_BRAND)
  }

  applyCellStyle(xlsx, worksheet, 0, 0, titleStyle)

  const metadataStyleLabel: ExportCellStyle = {
    fill: {
      patternType: 'solid',
      fgColor: { rgb: COLOR_SURFACE }
    },
    font: {
      bold: true,
      color: { rgb: COLOR_TEXT },
      sz: 10
    },
    alignment: {
      horizontal: isArabic ? 'right' : 'left',
      vertical: 'center',
      wrapText: true
    },
    border: buildBorder(COLOR_BORDER)
  }

  const metadataStyleValue: ExportCellStyle = {
    fill: {
      patternType: 'solid',
      fgColor: { rgb: 'FFFFFF' }
    },
    font: {
      color: { rgb: COLOR_TEXT },
      sz: 10
    },
    alignment: {
      horizontal: isArabic ? 'right' : 'left',
      vertical: 'center',
      wrapText: true
    },
    border: buildBorder(COLOR_BORDER)
  }

  let currentMetadataRow = 2
  ;(sheet.metadata ?? []).forEach(() => {
    applyCellStyle(xlsx, worksheet, currentMetadataRow, 0, metadataStyleLabel)
    applyCellStyle(xlsx, worksheet, currentMetadataRow, 1, metadataStyleValue)
    currentMetadataRow += 1
  })

  const headerStyle: ExportCellStyle = {
    fill: {
      patternType: 'solid',
      fgColor: { rgb: COLOR_BRAND_ALT }
    },
    font: {
      bold: true,
      color: { rgb: 'FFFFFF' },
      sz: 11
    },
    alignment: {
      horizontal: isArabic ? 'right' : 'left',
      vertical: 'center',
      wrapText: true
    },
    border: buildBorder(COLOR_BORDER)
  }

  context.headerLabels.forEach((_, columnIndex) => {
    applyCellStyle(xlsx, worksheet, context.headerRowIndex, columnIndex, headerStyle)
  })

  sheet.data.forEach((row, rowIndex) => {
    const excelRowIndex = context.dataStartRowIndex + rowIndex
    const baseFill = rowIndex % 2 === 0 ? 'FFFFFF' : COLOR_SURFACE_ALT

    context.headerKeys.forEach((key, columnIndex) => {
      const baseStyle = createBaseRowStyle(isArabic, baseFill)
      const normalizedKey = key.toLowerCase()
      const statusColumns = (sheet.statusColumns ?? []).map((column) => column.toLowerCase())
      const isStatusColumn =
        statusColumns.includes(normalizedKey) ||
        normalizedKey.includes('status') ||
        normalizedKey.includes('answered')

      if (isStatusColumn) {
        const statusColor = getStatusBackgroundColor(String(row[key] ?? ''))
        baseStyle.fill = {
          patternType: 'solid',
          fgColor: { rgb: statusColor }
        }
        baseStyle.font = {
          color: { rgb: statusColor === COLOR_SURFACE ? COLOR_TEXT : 'FFFFFF' },
          sz: 10,
          bold: true
        }
        baseStyle.alignment = {
          horizontal: 'center',
          vertical: 'center',
          wrapText: true
        }
      }

      applyCellStyle(xlsx, worksheet, excelRowIndex, columnIndex, baseStyle)
    })
  })
}

export const exportWorkbookToExcel = async (options: ExcelWorkbookExportOptions): Promise<void> => {
  const xlsx = await loadXlsxModule()
  const workbook = xlsx.utils.book_new()
  workbook.Workbook = {
    ...(workbook.Workbook || {}),
    Views: [{ RTL: options.isArabic }]
  }

  options.sheets.forEach((sheet) => {
    const worksheet: ExcelWorksheet = {}
    const context = buildSheet(xlsx, worksheet, sheet)
    styleSheet(xlsx, worksheet, sheet, options.isArabic, context)
    xlsx.utils.book_append_sheet(workbook, worksheet, sanitizeSheetName(sheet.sheetName))
  })

  xlsx.writeFile(workbook, normalizeFileName(options.fileName))
}

export const exportToExcel = async (options: ExcelExportOptions): Promise<void> => {
  const { title, data, headers, isArabic, fileName, sheetName, metadata, columnWidths, statusColumns } = options

  await exportWorkbookToExcel({
    isArabic,
    fileName: fileName ?? buildTimestampedExcelFileName(title),
    sheets: [
      {
        sheetName: sheetName ?? (isArabic ? 'البيانات' : 'Data'),
        title,
        data,
        headers,
        metadata,
        columnWidths,
        statusColumns
      }
    ]
  })
}

export const exportChallengesToExcel = async (
  challenges: any[],
  isArabic: boolean,
  formatDate: (dateString: string) => string
): Promise<void> => {
  const headers = isArabic
    ? {
      serialNumber: 'الرقم التسلسلي',
      id: 'الرقم التعريفي',
      titleEn: 'العنوان (إنجليزي)',
      titleAr: 'العنوان (عربي)',
      descriptionEn: 'الوصف (إنجليزي)',
      descriptionAr: 'الوصف (عربي)',
      moderator: 'المشرف',
      department: 'الإدارة',
      status: 'الحالة',
      visibility: 'الرؤية',
      startTime: 'وقت البداية',
      endTime: 'وقت النهاية',
      createdAt: 'تاريخ الإنشاء',
      updatedAt: 'تاريخ التحديث'
    }
    : {
      serialNumber: 'Sr No',
      id: 'ID',
      titleEn: 'Title (English)',
      titleAr: 'Title (Arabic)',
      descriptionEn: 'Description (English)',
      descriptionAr: 'Description (Arabic)',
      moderator: 'Moderator',
      department: 'Department',
      status: 'Status',
      visibility: 'Visibility',
      startTime: 'Start Time',
      endTime: 'End Time',
      createdAt: 'Created At',
      updatedAt: 'Updated At'
    }

  const exportData = challenges.map((challenge: any, index: number) => ({
    serialNumber: index + 1,
    id: challenge.id,
    titleEn: challenge.titleEn ?? '-',
    titleAr: challenge.titleAr ?? '-',
    descriptionEn: challenge.descriptionEn ?? '-',
    descriptionAr: challenge.descriptionAr ?? '-',
    moderator: challenge.moderatorGrpNumber ?? '-',
    department: challenge.departmentName ?? '-',
    status: challenge.status ?? '-',
    visibility: challenge.visibility ?? '-',
    startTime: challenge.startTime ? formatDate(challenge.startTime) : '-',
    endTime: challenge.endTime ? formatDate(challenge.endTime) : '-',
    createdAt: challenge.createdAt ? formatDate(challenge.createdAt) : '-',
    updatedAt: challenge.lastModifiedAt ? formatDate(challenge.lastModifiedAt) : '-'
  }))

  await exportToExcel({
    title: isArabic ? 'تقرير التحديات' : 'Challenges Report',
    data: exportData,
    headers,
    isArabic,
    sheetName: isArabic ? 'التحديات' : 'Challenges',
    statusColumns: ['status'],
    fileName: buildTimestampedExcelFileName(isArabic ? 'تقرير التحديات' : 'Challenges Report')
  })
}
