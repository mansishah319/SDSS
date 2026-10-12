export interface CardConfig {
  columns: CardColumnConfig[]
}

export interface CardColumnConfig {
  title: string
  value: string | number | undefined
  cols?: number
  showNullValues?: boolean
  style?: {
    backgroundColor?: string
    color?: string
  }
  fullWidth?: boolean
}
