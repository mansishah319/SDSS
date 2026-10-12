// Input Sanitization Utilities
// Prevents script/command injection in text fields

import { translate } from '@/utils/TranslatorHelper'

/**
 * Patterns to detect potentially dangerous content
 */
const DANGEROUS_PATTERNS = [
  // Script tags
  /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,

  // JavaScript event handlers
  /on\w+\s*=\s*["'][^"']*["']/gi,

  // JavaScript URLs
  /javascript\s*:/gi,

  // SQL injection patterns
  /(\b(ALTER|CREATE|DELETE|DROP|EXEC(UTE)?|INSERT|MERGE|SELECT|UPDATE|UNION|INTO|FROM|WHERE|JOIN)\b)/gi,

  // Command injection patterns
  /(\b(bash|sh|cmd|powershell|eval|exec|system|passthru|shell_exec|proc_open|popen)\b)/gi,

  // Common shell commands (split to reduce complexity)
  /(\b(ls|dir|cat|type|mkdir|rmdir|rm|del)\b)/gi,
  /(\b(copy|move|mv|cp|chmod|chown|wget|curl)\b)/gi,
  /(\b(ping|netstat|ps|kill|killall|sudo|su|whoami)\b)/gi,

  // HTML tags that can be dangerous (split to reduce complexity)
  /<\s*(iframe|object|embed|link|meta|base)\b[^>]*>/gi,
  /<\s*(form|input|button|select|textarea)\b[^>]*>/gi,
  /<\s*(script|style)\b[^>]*>/gi,

  // Data URLs that might contain scripts
  /data\s*:\s*[^,]*script/gi,

  // Expressions that look like code execution
  /\$\{[^}]*}/g, // Template literals
  /\$\([^)]*\)/g, // Command substitution
  /`[^`]*`/g // Backticks (command substitution in some shells)
]

/**
 * Additional patterns for stricter validation
 */
const STRICT_PATTERNS = [
  // Any HTML tags
  /<[^>]*>/g,

  // Special characters that might be used in injection
  /[&<>"'`]/g,

  // Semicolons that might separate commands
  /;\s*\w+/g,

  // Pipe characters for command chaining
  /\|\s*\w+/g,

  // Ampersand for command chaining
  /&\s*\w+/g
]

/**
 * Sanitizes input text by removing or encoding dangerous patterns
 * @param input - The text input to sanitize
 * @param strict - Whether to apply strict sanitization (removes more content)
 * @returns Sanitized text
 */
export function sanitizeTextInput(input: any, strict = false): string {
  if (!input || typeof input !== 'string') {
    return ''
  }

  let sanitized = input.trim()

  // Apply basic dangerous pattern removal
  DANGEROUS_PATTERNS.forEach((pattern) => {
    sanitized = sanitized.replace(pattern, '')
  })

  // Apply strict patterns if requested
  if (strict) {
    STRICT_PATTERNS.forEach((pattern) => {
      sanitized = sanitized.replace(pattern, '')
    })
  }

  // Additional safety: encode remaining special characters
  sanitized = sanitized
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#x27;')
    .replace(/`/g, '&#x60;')

  return sanitized.trim()
}

/**
 * Validates if text input contains potentially dangerous content
 * @param input - The text input to validate
 * @param strict - Whether to apply strict validation
 * @returns Object with a validation result and details
 */
export function validateTextSafety(
  input: any,
  strict = false
): {
  isValid: boolean
  errors: string[]
  sanitized: string
} {
  if (!input || typeof input !== 'string') {
    return {
      isValid: true,
      errors: [],
      sanitized: ''
    }
  }

  const errors: string[] = []
  const patterns = strict ? [...DANGEROUS_PATTERNS, ...STRICT_PATTERNS] : DANGEROUS_PATTERNS

  // Check for dangerous patterns
  patterns.forEach((pattern, index) => {
    if (pattern.test(input)) {
      if (index < DANGEROUS_PATTERNS.length) {
        errors.push(translate('validation.dangerousContent'))
      } else {
        errors.push(translate('validation.htmlNotAllowed'))
      }
    }
  })

  return {
    isValid: errors.length === 0,
    errors: Array.from(new Set(errors)), // Remove duplicates
    sanitized: sanitizeTextInput(input, strict)
  }
}

/**
 * Vue composable for text input validation
 * @param strict - Whether to apply strict validation
 * @returns Validation functions for use in Vue components
 */
export function useTextValidation(strict = false) {
  const validateInput = (value: any) => {
    const result = validateTextSafety(value, strict)
    return result.isValid ? true : result.errors[0]
  }

  const sanitizeInput = (value: any) => {
    return sanitizeTextInput(value, strict)
  }

  return {
    validateInput,
    sanitizeInput
  }
}

/**
 * Validation rule for v-text-field and v-textarea components
 * @param strict - Whether to apply strict validation
 * @returns Validation rule function
 */
export function textSafetyRule(strict = false) {
  return (value: any) => {
    if (!value) return true
    const result = validateTextSafety(value, strict)
    return result.isValid || result.errors[0]
  }
}

/**
 * Helper function to sanitize array items
 * @param item - Array item to sanitize
 * @param strict - Whether to apply strict sanitization
 * @returns Sanitized item
 */
function sanitizeArrayItem(item: any, strict: boolean): any {
  if (typeof item === 'string') {
    return sanitizeTextInput(item, strict)
  }
  if (item && typeof item === 'object') {
    return sanitizeObject(item, strict)
  }
  return item
}

/**
 * Sanitizes an entire object's string properties
 * @param obj - Object to sanitize
 * @param strict - Whether to apply strict sanitization
 * @returns New object with sanitized string properties
 */
export function sanitizeObject<T extends Record<string, any>>(obj: T, strict = false): T {
  const sanitized = { ...obj }

  Object.keys(sanitized).forEach((key: any) => {
    const value = sanitized[key as keyof T]
    if (typeof value === 'string') {
      ;(sanitized as any)[key] = sanitizeTextInput(value, strict)
    } else if (value && typeof value === 'object' && !Array.isArray(value)) {
      ;(sanitized as any)[key] = sanitizeObject(value, strict)
    } else if (Array.isArray(value)) {
      ;(sanitized as any)[key] = value.map((item: any) => sanitizeArrayItem(item, strict))
    }
  })

  return sanitized
}
