export interface ResolveReportExportChromeOptions {
  headerTitle?: string
  headerDescription?: string
  footerNote?: string
}

export interface ResolvedReportExportChrome {
  headerTitle: string
  headerDescription: string
  footerBrandTitle: string
  footerBrandSubtitle: string
  footerOwner: string
  footerCopyright: string
  footerNote: string
}

export const DEFAULT_REPORT_HEADER_TITLE = 'التقارير'
export const DEFAULT_REPORT_BRAND_TITLE = 'نظام دعم اتخاذ القرار الذكي'
export const DEFAULT_REPORT_BRAND_SUBTITLE = 'Smart Decision Support System'
export const DEFAULT_REPORT_OWNER = 'مركز إستشراف المستقبل | شرطة دبي'
export const DEFAULT_REPORT_COPYRIGHT_YEAR = String(new Date().getFullYear())
export const DEFAULT_REPORT_FOOTER_NOTE = 'تم إنشاء هذا الملف من نظام دعم اتخاذ القرار الذكي - شرطة دبي'

const getTrimmedValue = (value?: string | null) => value?.trim() || null

export const formatReportExportDate = (date: Date = new Date()) =>
  new Intl.DateTimeFormat('ar-AE', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).format(date)

export const buildDefaultReportHeaderDescription = (date: Date = new Date()) =>
  `تم تحميل هذا التقرير يوم ${formatReportExportDate(date)}`

export const buildReportFooterCopyright = () =>
  `© ${DEFAULT_REPORT_COPYRIGHT_YEAR} جميع الحقوق محفوظة`

export const resolveReportExportChrome = (
  options: ResolveReportExportChromeOptions = {}
): ResolvedReportExportChrome => ({
  headerTitle: getTrimmedValue(options.headerTitle) || DEFAULT_REPORT_HEADER_TITLE,
  headerDescription: getTrimmedValue(options.headerDescription) || buildDefaultReportHeaderDescription(),
  footerBrandTitle: DEFAULT_REPORT_BRAND_TITLE,
  footerBrandSubtitle: DEFAULT_REPORT_BRAND_SUBTITLE,
  footerOwner: DEFAULT_REPORT_OWNER,
  footerCopyright: buildReportFooterCopyright(),
  footerNote: getTrimmedValue(options.footerNote) || DEFAULT_REPORT_FOOTER_NOTE
})
