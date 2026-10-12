const DateUtils = {
  getDateWithoutTime: (date: Date) => {
    return new Date(date.getFullYear(), date.getMonth(), date.getDate())
  },
  months: [
    { text: 'يناير', value: 1 },
    { text: 'فبراير', value: 2 },
    { text: 'مارس', value: 3 },
    { text: 'إبريل', value: 4 },
    { text: 'مايو', value: 5 },
    { text: 'يونيو', value: 6 },
    { text: 'يوليو', value: 7 },
    { text: 'أغسطس', value: 8 },
    { text: 'سبتمبر', value: 9 },
    { text: 'أكتوبر', value: 10 },
    { text: 'نوفمبر', value: 11 },
    { text: 'ديسمبر', value: 12 }
  ],
  getMonthName: (monthValue: number) => {
    const month = DateUtils.months.find((month) => month.value === monthValue)
    return month ? month.text : ''
  },

  formatDateTime(date: string, time: string): string {
    if (!DateUtils.isValidDate(date) || !DateUtils.isValidTime(time)) {
      return ''
    }

    const accDate = new Date(date)
    const accTime = new Date(`2000-01-01T${time}`)

    // Format date
    const formattedDate = `${accDate.getDate().toString().padStart(2, '0')}-${(accDate.getMonth() + 1)
      .toString()
      .padStart(2, '0')}-${accDate.getFullYear()}`

    const formattedTime = accTime.toLocaleTimeString(undefined, {
      hour: '2-digit',
      minute: '2-digit'
    })

    return `${formattedDate} - ${formattedTime}`
  },

  isValidDate(date: string): boolean {
    const parsedDate = new Date(date)
    return !isNaN(parsedDate.getTime())
  },

  isValidTime(time: string): boolean {
    const pattern = /^([01]?\d|2[0-3]):[0-5]\d$/
    return pattern.test(time)
  },

  calculateAge(birthDate: string): number {
    const birth = new Date(birthDate)
    const today = new Date()

    let age = today.getFullYear() - birth.getFullYear()
    const monthDifference = today.getMonth() - birth.getMonth()

    // Check if the birthday has occurred this year
    if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birth.getDate())) {
      age--
    }

    return age
  }
}

export default DateUtils
