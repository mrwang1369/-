// utils/common.js - 通用工具函数

/**
 * 通用工具函数集合
 */
const Common = {
  /**
   * 防抖函数
   * @param {Function} func 要防抖的函数
   * @param {number} wait 等待时间(ms)
   */
  debounce(func, wait = 300) {
    let timeout
    return function(...args) {
      clearTimeout(timeout)
      timeout = setTimeout(() => func.apply(this, args), wait)
    }
  },

  /**
   * 节流函数
   * @param {Function} func 要节流的函数
   * @param {number} wait 等待时间(ms)
   */
  throttle(func, wait = 300) {
    let previous = 0
    return function(...args) {
      const now = Date.now()
      if (now - previous > wait) {
        func.apply(this, args)
        previous = now
      }
    }
  },

  /**
   * 深拷贝
   */
  deepClone(obj) {
    if (obj === null || typeof obj !== 'object') {
      return obj
    }

    if (obj instanceof Date) {
      return new Date(obj.getTime())
    }

    if (Array.isArray(obj)) {
      return obj.map(item => this.deepClone(item))
    }

    const cloned = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        cloned[key] = this.deepClone(obj[key])
      }
    }

    return cloned
  },

  /**
   * 数组去重
   */
  unique(arr, key = null) {
    if (key) {
      const map = new Map()
      return arr.filter(item => !map.has(item[key]) && map.set(item[key], 1))
    }
    return [...new Set(arr)]
  },

  /**
   * 数组排序
   */
  sortBy(arr, key, order = 'asc') {
    return arr.sort((a, b) => {
      const valA = a[key]
      const valB = b[key]

      if (valA < valB) return order === 'asc' ? -1 : 1
      if (valA > valB) return order === 'asc' ? 1 : -1
      return 0
    })
  },

  /**
   * 数组分组
   */
  groupBy(arr, key) {
    return arr.reduce((groups, item) => {
      const groupKey = item[key]
      if (!groups[groupKey]) {
        groups[groupKey] = []
      }
      groups[groupKey].push(item)
      return groups
    }, {})
  },

  /**
   * 对象转URL参数
   */
  toQueryString(obj) {
    return Object.keys(obj)
      .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(obj[key])}`)
      .join('&')
  },

  /**
   * URL参数转对象
   */
  parseQueryString(str) {
    const params = {}
    const pairs = str.split('&')

    for (const pair of pairs) {
      const [key, value] = pair.split('=')
      params[decodeURIComponent(key)] = decodeURIComponent(value || '')
    }

    return params
  },

  /**
   * 生成随机字符串
   */
  randomString(length = 8) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    let result = ''
    for (let i = 0; i < length; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length))
    }
    return result
  },

  /**
   * 生成UUID
   */
  uuid() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      const r = Math.random() * 16 | 0
      const v = c === 'x' ? r : (r & 0x3 | 0x8)
      return v.toString(16)
    })
  },

  /**
   * 格式化文件大小
   */
  formatFileSize(bytes) {
    if (bytes === 0) return '0 B'

    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))

    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
  },

  /**
   * 格式化数字
   */
  formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
  },

  /**
   * 截断文本
   */
  truncateText(text, maxLength = 50, suffix = '...') {
    if (text.length <= maxLength) return text
    return text.substring(0, maxLength) + suffix
  },

  /**
   * 显示Toast提示
   */
  toast(title, icon = 'none', duration = 2000) {
    wx.showToast({
      title: title,
      icon: icon,
      duration: duration
    })
  },

  /**
   * 显示确认对话框
   */
  confirm(content, title = '提示') {
    return new Promise((resolve) => {
      wx.showModal({
        title: title,
        content: content,
        success: (res) => {
          resolve(res.confirm)
        },
        fail: () => {
          resolve(false)
        }
      })
    })
  },

  /**
   * 显示加载提示
   */
  showLoading(title = '加载中...') {
    wx.showLoading({
      title: title,
      mask: true
    })
  },

  /**
   * 隐藏加载提示
   */
  hideLoading() {
    wx.hideLoading()
  },

  /**
   * 选择图片
   */
  chooseImage(count = 1, sizeType = ['original', 'compressed'], sourceType = ['album', 'camera']) {
    return new Promise((resolve, reject) => {
      wx.chooseImage({
        count: count,
        sizeType: sizeType,
        sourceType: sourceType,
        success: resolve,
        fail: reject
      })
    })
  },

  /**
   * 上传图片
   */
  uploadImage(filePath, moduleType = 'pet_avatar') {
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: `${app.globalData.baseUrl}/files/upload`,
        filePath: filePath,
        name: 'file',
        formData: {
          moduleType: moduleType
        },
        header: {
          'Authorization': `Bearer ${app.globalData.token}`
        },
        success: (res) => {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data.data)
          } else {
            reject(new Error(data.message || '上传失败'))
          }
        },
        fail: reject
      })
    })
  }
}

module.exports = Common
