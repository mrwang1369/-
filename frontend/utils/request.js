// utils/request.js - 网络请求封装
const app = getApp()

/**
 * 网络请求封装
 * 统一处理JWT认证、错误处理、加载状态
 */
class Request {
  constructor() {
    this.baseUrl = app.globalData.baseUrl
  }

  /**
   * 基础请求方法
   * @param {string} url 请求地址
   * @param {object} data 请求数据
   * @param {string} method 请求方法
   * @param {boolean} needAuth 是否需要认证
   * @param {boolean} showLoading 是否显示加载提示
   */
  request(url, data = {}, method = 'GET', needAuth = true, showLoading = true) {
    return new Promise((resolve, reject) => {
      // 显示加载提示
      if (showLoading) {
        wx.showLoading({
          title: '加载中...',
          mask: true
        })
      }

      // 构建请求头
      const header = {
        'Content-Type': 'application/json'
      }

      // 添加JWT token
      if (needAuth) {
        const token = app.globalData.token
        if (!token) {
          wx.hideLoading()
          wx.showToast({
            title: '请先登录',
            icon: 'none'
          })
          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/login/login'
            })
          }, 1500)
          reject(new Error('未登录'))
          return
        }
        header['Authorization'] = `Bearer ${token}`
      }

      // 发起请求
      wx.request({
        url: this.baseUrl + url,
        data: data,
        method: method,
        header: header,
        success: (res) => {
          wx.hideLoading()

          // 处理响应
          if (res.statusCode === 200) {
            const response = res.data

            // 检查业务状态码
            if (response.code === 200) {
              // 成功响应
              resolve(response.data)
            } else if (response.code === 401) {
              // Token过期或无效
              this.handleUnauthorized()
              reject(new Error(response.message || '登录已过期'))
            } else {
              // 业务错误
              wx.showToast({
                title: response.message || '请求失败',
                icon: 'none'
              })
              reject(new Error(response.message || '请求失败'))
            }
          } else if (res.statusCode === 401) {
            // HTTP 401未授权
            this.handleUnauthorized()
            reject(new Error('登录已过期'))
          } else {
            // 其他HTTP错误
            wx.showToast({
              title: '网络请求失败',
              icon: 'none'
            })
            reject(new Error('网络请求失败'))
          }
        },
        fail: (err) => {
          wx.hideLoading()
          wx.showToast({
            title: '网络连接失败',
            icon: 'none'
          })
          reject(err)
        }
      })
    })
  }

  /**
   * 处理未授权情况
   */
  handleUnauthorized() {
    // 清除本地存储
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    app.globalData.token = null
    app.globalData.userInfo = null

    // 提示用户
    wx.showToast({
      title: '登录已过期，请重新登录',
      icon: 'none',
      duration: 2000
    })

    // 延迟跳转到登录页
    setTimeout(() => {
      wx.reLaunch({
        url: '/pages/login/login'
      })
    }, 2000)
  }

  /**
   * GET请求
   */
  get(url, data = {}, needAuth = true, showLoading = true) {
    return this.request(url, data, 'GET', needAuth, showLoading)
  }

  /**
   * POST请求
   */
  post(url, data = {}, needAuth = true, showLoading = true) {
    return this.request(url, data, 'POST', needAuth, showLoading)
  }

  /**
   * PUT请求
   */
  put(url, data = {}, needAuth = true, showLoading = true) {
    return this.request(url, data, 'PUT', needAuth, showLoading)
  }

  /**
   * DELETE请求
   */
  delete(url, data = {}, needAuth = true, showLoading = true) {
    return this.request(url, data, 'DELETE', needAuth, showLoading)
  }

  /**
   * PATCH请求
   */
  patch(url, data = {}, needAuth = true, showLoading = true) {
    return this.request(url, data, 'PATCH', needAuth, showLoading)
  }

  /**
   * 文件上传
   */
  uploadFile(url, filePath, formData = {}) {
    return new Promise((resolve, reject) => {
      wx.showLoading({
        title: '上传中...',
        mask: true
      })

      const token = app.globalData.token
      const header = {}

      if (token) {
        header['Authorization'] = `Bearer ${token}`
      }

      wx.uploadFile({
        url: this.baseUrl + url,
        filePath: filePath,
        name: 'file',
        formData: formData,
        header: header,
        success: (res) => {
          wx.hideLoading()

          if (res.statusCode === 200) {
            const response = JSON.parse(res.data)
            if (response.code === 200) {
              resolve(response.data)
            } else {
              wx.showToast({
                title: response.message || '上传失败',
                icon: 'none'
              })
              reject(new Error(response.message || '上传失败'))
            }
          } else {
            wx.showToast({
              title: '上传失败',
              icon: 'none'
            })
            reject(new Error('上传失败'))
          }
        },
        fail: (err) => {
          wx.hideLoading()
          wx.showToast({
            title: '上传失败',
            icon: 'none'
          })
          reject(err)
        }
      })
    })
  }
}

// 创建单例实例
const request = new Request()

module.exports = request
