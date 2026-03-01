// utils/storage.js - 本地存储工具

/**
 * 本地存储工具类
 * 统一管理本地数据存储
 */
const Storage = {
  /**
   * 设置数据
   * @param {string} key 键名
   * @param {any} value 值
   */
  set(key, value) {
    try {
      wx.setStorageSync(key, value)
      return true
    } catch (error) {
      console.error('存储失败:', error)
      return false
    }
  },

  /**
   * 获取数据
   * @param {string} key 键名
   * @param {any} defaultValue 默认值
   */
  get(key, defaultValue = null) {
    try {
      return wx.getStorageSync(key) || defaultValue
    } catch (error) {
      console.error('读取失败:', error)
      return defaultValue
    }
  },

  /**
   * 删除数据
   * @param {string} key 键名
   */
  remove(key) {
    try {
      wx.removeStorageSync(key)
      return true
    } catch (error) {
      console.error('删除失败:', error)
      return false
    }
  },

  /**
   * 清空所有数据
   */
  clear() {
    try {
      wx.clearStorageSync()
      return true
    } catch (error) {
      console.error('清空失败:', error)
      return false
    }
  },

  /**
   * 获取存储信息
   */
  getInfo() {
    try {
      return wx.getStorageInfoSync()
    } catch (error) {
      console.error('获取存储信息失败:', error)
      return null
    }
  }
}

module.exports = Storage
