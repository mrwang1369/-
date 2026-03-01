// utils/date.js - 日期时间工具

/**
 * 日期时间工具类
 * 格式化、计算、比较等日期操作
 */
const DateUtil = {
  /**
   * 格式化日期
   * @param {Date|string|number} date 日期
   * @param {string} format 格式模板
   */
  format(date, format = 'YYYY-MM-DD HH:mm:ss') {
    if (!date) return ''

    const d = new Date(date)
    if (isNaN(d.getTime())) return ''

    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    const seconds = String(d.getSeconds()).padStart(2, '0')

    return format
      .replace('YYYY', year)
      .replace('MM', month)
      .replace('DD', day)
      .replace('HH', hours)
      .replace('mm', minutes)
      .replace('ss', seconds)
  },

  /**
   * 格式化短日期
   */
  formatDate(date) {
    return this.format(date, 'YYYY-MM-DD')
  },

  /**
   * 格式化时间
   */
  formatTime(date) {
    return this.format(date, 'HH:mm:ss')
  },

  /**
   * 格式化为相对时间
   * @param {Date|string|number} date 日期
   */
  formatRelative(date) {
    if (!date) return ''

    const d = new Date(date)
    const now = new Date()
    const diff = now.getTime() - d.getTime()
    const seconds = Math.floor(diff / 1000)
    const minutes = Math.floor(seconds / 60)
    const hours = Math.floor(minutes / 60)
    const days = Math.floor(hours / 24)

    if (seconds < 60) {
      return '刚刚'
    } else if (minutes < 60) {
      return `${minutes}分钟前`
    } else if (hours < 24) {
      return `${hours}小时前`
    } else if (days < 7) {
      return `${days}天前`
    } else {
      return this.formatDate(date)
    }
  },

  /**
   * 计算年龄
   * @param {Date|string|number} birthDate 出生日期
   */
  calculateAge(birthDate) {
    if (!birthDate) return ''

    const birth = new Date(birthDate)
    const now = new Date()
    let age = now.getFullYear() - birth.getFullYear()
    const monthDiff = now.getMonth() - birth.getMonth()

    if (monthDiff < 0 || (monthDiff === 0 && now.getDate() < birth.getDate())) {
      age--
    }

    if (age === 0) {
      // 未满1岁，显示月龄
      let months = now.getMonth() - birth.getMonth()
      if (now.getDate() < birth.getDate()) {
        months--
      }
      if (months < 0) {
        months += 12
      }
      return `${months}个月`
    }

    return `${age}岁`
  },

  /**
   * 计算天数差
   * @param {Date|string|number} date1 日期1
   * @param {Date|string|number} date2 日期2
   */
  daysBetween(date1, date2) {
    const d1 = new Date(date1)
    const d2 = new Date(date2)
    const diff = d2.getTime() - d1.getTime()
    return Math.floor(diff / (1000 * 60 * 60 * 24))
  },

  /**
   * 判断是否为今天
   */
  isToday(date) {
    const d = new Date(date)
    const today = new Date()
    return d.getDate() === today.getDate() &&
           d.getMonth() === today.getMonth() &&
           d.getFullYear() === today.getFullYear()
  },

  /**
   * 判断是否为明天
   */
  isTomorrow(date) {
    const d = new Date(date)
    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1)
    return d.getDate() === tomorrow.getDate() &&
           d.getMonth() === tomorrow.getMonth() &&
           d.getFullYear() === tomorrow.getFullYear()
  },

  /**
   * 判断是否为过去
   */
  isPast(date) {
    const d = new Date(date)
    return d.getTime() < new Date().getTime()
  },

  /**
   * 判断是否为未来
   */
  isFuture(date) {
    const d = new Date(date)
    return d.getTime() > new Date().getTime()
  },

  /**
   * 添加天数
   */
  addDays(date, days) {
    const d = new Date(date)
    d.setDate(d.getDate() + days)
    return d
  },

  /**
   * 添加月数
   */
  addMonths(date, months) {
    const d = new Date(date)
    d.setMonth(d.getMonth() + months)
    return d
  },

  /**
   * 获取日期范围
   */
  getDateRange(startDate, endDate) {
    const dates = []
    const start = new Date(startDate)
    const end = new Date(endDate)

    while (start <= end) {
      dates.push(this.formatDate(start))
      start.setDate(start.getDate() + 1)
    }

    return dates
  }
}

module.exports = DateUtil
