/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

const DISALLOWED_MIME_TYPES = new Set<string>([
  'text/html',
  'application/xhtml+xml',
  'application/javascript',
  'text/javascript',
  'application/x-sh',
  'application/x-msdownload',
  'application/x-msdos-program',
  'application/x-bat',
  'application/x-elf',
  'application/x-msi',
  'application/x-dosexec',
  'application/x-executable',
  'text/plain' // Block plain text files that might contain HTML
])

const DISALLOWED_EXTENSIONS = new Set<string>([
  'html',
  'htm',
  'js',
  'mjs',
  'exe',
  'bat',
  'sh',
  'msi',
  'cmd',
  'com',
  'scr',
  'zipjs',
  'txt', // Block text files that might contain HTML
  'xml', // Block XML files that might contain HTML
  'svg' // Block SVG files that might contain JavaScript
])

const ALLOWED_EXTENSIONS = new Set<string>([
  'pdf',
  'png',
  'jpg',
  'jpeg',
  'gif',
  'doc',
  'docx',
  'xls',
  'xlsx',
  'csv',
  'json',
  'zip',
  'rar',
  '7z',
  'mp4',
  'mp3',
  'wav',
  'avi',
  'mov'
])

export function getExtension(fileName?: string | null): string | null {
  if (!fileName) return null
  const idx = fileName.lastIndexOf('.')
  if (idx === -1) return null
  return fileName.substring(idx + 1).toLowerCase()
}

export function isMimeTypeDisallowed(mimeType?: string | null): boolean {
  if (!mimeType) return false
  const normalized = mimeType.split(';')[0].trim().toLowerCase()
  return DISALLOWED_MIME_TYPES.has(normalized)
}

export function isExtensionDisallowed(ext?: string | null): boolean {
  if (!ext) return false
  return DISALLOWED_EXTENSIONS.has(ext.toLowerCase())
}

export function isExtensionExplicitlyAllowed(ext?: string | null): boolean {
  if (!ext) return false
  return ALLOWED_EXTENSIONS.has(ext.toLowerCase())
}

export function assertSafeForDownload(params: { fileName?: string; mimeType?: string; content?: string }): {
  safe: boolean
  reason?: string
} {
  const ext = getExtension(params.fileName ?? null)

  if (isMimeTypeDisallowed(params.mimeType)) {
    return { safe: false, reason: `Blocked by MIME type ${params.mimeType}` }
  }

  if (isExtensionDisallowed(ext)) {
    return { safe: false, reason: `Blocked by extension .${ext}` }
  }

  // If extension is known and not in allowed list but MIME is also unknown, be conservative
  if (ext && !isExtensionExplicitlyAllowed(ext) && !params.mimeType) {
    return { safe: false, reason: 'Unknown file type without MIME' }
  }

  // Scan content if provided
  if (params.content) {
    const hasHtmlContent = HTML_PATTERNS.some((pattern) => pattern.test(params.content!))
    if (hasHtmlContent) {
      return { safe: false, reason: 'File contains HTML content' }
    }

    const hasPhishingContent = PHISHING_PATTERNS.some((pattern) => pattern.test(params.content!))
    if (hasPhishingContent) {
      return { safe: false, reason: 'File contains potential phishing content' }
    }

    const redirectPatterns = [
      /window\.location\s*=/gi,
      /location\.href\s*=/gi,
      /document\.location\s*=/gi,
      /http-equiv\s*=\s*["']refresh["']/gi,
      /javascript:/gi
    ]

    const hasRedirects = redirectPatterns.some((pattern) => pattern.test(params.content!))
    if (hasRedirects) {
      return { safe: false, reason: 'File contains suspicious redirect patterns' }
    }
  }

  return { safe: true }
}

// Phishing detection patterns
const PHISHING_PATTERNS = [
  /<script[^>]*>[\s\S]*?window\.location[\s\S]*?<\/script>/gi,
  /<script[^>]*>[\s\S]*?location\.href[\s\S]*?<\/script>/gi,
  /<script[^>]*>[\s\S]*?document\.location[\s\S]*?<\/script>/gi,
  /<meta[^>]*http-equiv=["']refresh["'][^>]*>/gi,
  /<iframe[^>]*src[\s\S]*?<\/iframe>/gi,
  /<object[^>]*data[\s\S]*?<\/object>/gi,
  /<embed[^>]*src[\s\S]*?<\/embed>/gi,
  /javascript:/gi,
  /onclick\s*=/gi,
  /onload\s*=/gi,
  /onerror\s*=/gi,
  /onmouseover\s*=/gi,
  /<a[^>]*href\s*=\s*["']javascript:/gi,
  /<form[^>]*action\s*=\s*["']javascript:/gi
]

// HTML content detection patterns
const HTML_PATTERNS = [
  /<html[\s\S]*?>/gi,
  /<head[\s\S]*?>/gi,
  /<body[\s\S]*?>/gi,
  /<script[\s\S]*?>/gi,
  /<iframe[\s\S]*?>/gi,
  /<object[\s\S]*?>/gi,
  /<embed[\s\S]*?>/gi,
  /<meta[\s\S]*?>/gi,
  /<link[\s\S]*?>/gi,
  /<style[\s\S]*?>/gi
]

export function scanFileContent(file: File): Promise<{ safe: boolean; reason?: string }> {
  return new Promise((resolve) => {
    const reader = new FileReader()

    reader.onload = (e) => {
      const content = e.target?.result as string

      // Check for HTML content
      const hasHtmlContent = HTML_PATTERNS.some((pattern) => pattern.test(content))
      if (hasHtmlContent) {
        resolve({ safe: false, reason: 'File contains HTML content' })
        return
      }

      // Check for phishing patterns
      const hasPhishingContent = PHISHING_PATTERNS.some((pattern) => pattern.test(content))
      if (hasPhishingContent) {
        resolve({ safe: false, reason: 'File contains potential phishing content' })
        return
      }

      // Check for suspicious redirects
      const redirectPatterns = [
        /window\.location\s*=/gi,
        /location\.href\s*=/gi,
        /document\.location\s*=/gi,
        /http-equiv\s*=\s*["']refresh["']/gi,
        /javascript:/gi
      ]

      const hasRedirects = redirectPatterns.some((pattern) => pattern.test(content))
      if (hasRedirects) {
        resolve({ safe: false, reason: 'File contains suspicious redirect patterns' })
        return
      }

      resolve({ safe: true })
    }

    reader.onerror = () => {
      resolve({ safe: false, reason: 'Unable to scan file content' })
    }

    // Only scan text-based files
    if (
      file.type.startsWith('text/') ||
      file.name.endsWith('.html') ||
      file.name.endsWith('.htm') ||
      file.name.endsWith('.js') ||
      file.name.endsWith('.xml') ||
      file.name.endsWith('.svg')
    ) {
      reader.readAsText(file)
    } else {
      resolve({ safe: true })
    }
  })
}

export function assertSafeForUpload(file: File): Promise<{ safe: boolean; reason?: string }> {
  return new Promise(async (resolve) => {
    const ext = getExtension(file?.name)
    const type = file?.type

    // First check MIME type and extension
    if (isMimeTypeDisallowed(type)) {
      resolve({ safe: false, reason: `Blocked by MIME type ${type}` })
      return
    }

    if (isExtensionDisallowed(ext)) {
      resolve({ safe: false, reason: `Blocked by extension .${ext}` })
      return
    }

    // Then scan content for phishing patterns
    const contentScan = await scanFileContent(file)
    resolve(contentScan)
  })
}
