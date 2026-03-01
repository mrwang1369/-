// utils/validator.js - 数据验证工具

/**
 * 数据验证工具类
 * 各种数据格式验证
 */
const Validator = {
  /**
   * 验证手机号
   */
  isPhone(phone) {
    return /^1[3-9]\d{9}$/.test(phone)
  },

  /**
   * 验证邮箱
   */
  isEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
  },

  /**
   * 验证身份证号
   */
  isIdCard(idCard) {
    return /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)
  },

  /**
   * 验证URL
   */
  isUrl(url) {
    return /^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$/.test(url)
  },

  /**
   * 验证数字
   */
  isNumber(value) {
    return !isNaN(parseFloat(value)) && isFinite(value)
  },

  /**
   * 验证整数
   */
  isInteger(value) {
    return Number.isInteger(Number(value))
  },

  /**
   * 验证正数
   */
  isPositive(value) {
    return this.isNumber(value) && Number(value) > 0
  },

  /**
   * 验证非负数
   */
  isNonNegative(value) {
    return this.isNumber(value) && Number(value) >= 0
  },

  /**
   * 验证日期
   */
  isDate(value) {
    return !isNaN(new Date(value).getTime())
  },

  /**
   * 验证过去日期
   */
  isPastDate(value) {
    return this.isDate(value) && new Date(value).getTime() < Date.now()
  },

  /**
   * 验证未来日期
   */
  isFutureDate(value) {
    return this.isDate(value) && new Date(value).getTime() > Date.now()
  },

  /**
   * 验证字符串长度
   */
  isLength(value, min, max) {
    if (typeof value !== 'string') return false
    const len = value.length
    return len >= min && len <= max
  },

  /**
   * 验证是否为空
   */
  isEmpty(value) {
    if (value === null || value === undefined) return true
    if (typeof value === 'string') return value.trim() === ''
    if (Array.isArray(value)) return value.length === 0
    if (typeof value === 'object') return Object.keys(value).length === 0
    return false
  },

  /**
   * 验证必填字段
   */
  required(value, message = '此项不能为空') {
    if (this.isEmpty(value)) {
      return {
        valid: false,
        message: message
      }
    }
    return { valid: true }
  },

  /**
   * 验证手机号
   */
  phone(value, message = '手机号格式不正确') {
    if (!this.isPhone(value)) {
      return {
        valid: false,
        message: message
      }
    }
    return { valid: true }
  },

  /**
   * 验证密码
   */
  password(value, min = 6, max = 20, message = '密码长度应在6-20位之间') {
    if (!this.isLength(value, min, max)) {
      return {
        valid: false,
        message: message
      }
    }
    return { valid: true }
  },

  /**
   * 批量验证
   */
  validate(data, rules) {
    const errors = []

    for (const field in rules) {
      const value = data[field]
      const fieldRules = rules[field]

      for (const rule of fieldRules) {
        if (typeof rule === 'function') {
          const result = rule(value, data)
          if (!result.valid) {
            errors.push({
              field: field,
              message: result.message
            })
            break
          }
        } else if (rule.type) {
          // 使用内置验证器
          const validator = this[rule.type]
          if (validator) {
            const result = validator.call(this, value, rule.message)
            if (!result.valid) {
              errors.push({
                field: field,
                message: result.message
              })
              break
            }
          }
        }
      }
    }

    return {
      valid: errors.length === 0,
      errors: errors
    }
  }
}

module.exports = Validator
