// utils/auth.js - 认证工具

/**
 * 认证工具类
 * 处理登录、注册、Token等认证相关操作
 */
const app = getApp()
const request = require('./request.js')
const Storage = require('./storage.js')

const Auth = {
  /**
   * 用户登录
   * @param {string} phone 手机号
   * @param {string} password 密码
   */
  async login(phone, password) {
    try {
      const res = await request.post('/auth/login', {
        loginType: 'phone',
        phone: phone,
        password: password
      }, false)

      console.log('登录响应:', res)

      if (res && res.accessToken) {
        // 保存token和用户信息
        const token = res.accessToken
        const userInfo = res.userInfo

        app.globalData.token = token
        app.globalData.userInfo = userInfo

        Storage.set('token', token)
        Storage.set('userInfo', userInfo)

        console.log('Token已保存:', token)
        console.log('用户信息已保存:', userInfo)

        return {
          success: true,
          data: res
        }
      }

      return {
        success: false,
        message: res?.message || '登录失败'
      }
    } catch (error) {
      console.error('登录异常:', error)
      return {
        success: false,
        message: error.message || '登录失败'
      }
    }
  },

  /**
   * 微信登录
   * @param {string} code 微信登录凭证
   */
  async wxLogin(code) {
    try {
      const res = await request.post('/auth/wxlogin', {
        loginType: 'wx',
        wxCode: code
      }, false)

      if (res && res.accessToken) {
        // 保存token和用户信息
        const token = res.accessToken
        const userInfo = res.userInfo

        app.globalData.token = token
        app.globalData.userInfo = userInfo

        Storage.set('token', token)
        Storage.set('userInfo', userInfo)

        return {
          success: true,
          data: res
        }
      }

      return {
        success: false,
        message: res?.message || '微信登录失败'
      }
    } catch (error) {
      return {
        success: false,
        message: error.message || '微信登录失败'
      }
    }
  },

  /**
   * 用户注册
   * @param {string} phone 手机号
   * @param {string} password 密码
   * @param {string} nickname 昵称
   */
  async register(phone, password, nickname) {
    try {
      const result = await request.post('/auth/register', {
        phone: phone,
        password: password,
        confirmPassword: password,
        nickname: nickname
      }, false)

      return {
        success: true,
        data: result
      }
    } catch (error) {
      return {
        success: false,
        message: error.message || '注册失败'
      }
    }
  },

  /**
   * 退出登录
   */
  logout() {
    // 清除本地数据
    app.globalData.token = null
    app.globalData.userInfo = null

    Storage.remove('token')
    Storage.remove('userInfo')

    // 跳转到登录页
    wx.reLaunch({
      url: '/pages/login/login'
    })
  },

  /**
   * 获取用户信息
   */
  async getUserInfo() {
    try {
      const result = await request.get('/auth/profile')

      if (result) {
        app.globalData.userInfo = result
        Storage.set('userInfo', result)

        return {
          success: true,
          data: result
        }
      }

      return {
        success: false,
        message: '获取用户信息失败'
      }
    } catch (error) {
      return {
        success: false,
        message: error.message || '获取用户信息失败'
      }
    }
  },

  /**
   * 刷新Token
   */
  async refreshToken() {
    try {
      const result = await request.post('/auth/refresh', {}, true, false)

      if (result && result.token) {
        app.globalData.token = result.token
        Storage.set('token', result.token)

        return {
          success: true,
          data: result
        }
      }

      return {
        success: false,
        message: 'Token刷新失败'
      }
    } catch (error) {
      // Token刷新失败，需要重新登录
      this.logout()
      return {
        success: false,
        message: '登录已过期'
      }
    }
  },

  /**
   * 检查是否登录
   */
  isLogin() {
    return app.isLogin()
  },

  /**
   * 获取当前用户ID
   */
  getCurrentUserId() {
    const userInfo = app.globalData.userInfo
    return userInfo ? userInfo.userId : null
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUserInfo() {
    return app.globalData.userInfo
  }
}

module.exports = Auth
