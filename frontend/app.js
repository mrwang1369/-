// app.js
App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://localhost:8080/api'
  },

  onLaunch() {
    // 检查登录状态
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')

    if (token) {
      this.globalData.token = token
    }

    if (userInfo) {
      this.globalData.userInfo = userInfo
    }

    // 检查登录状态是否有效
    if (token) {
      this.checkLoginStatus()
    }
  },

  // 检查登录状态
  checkLoginStatus() {
    const token = this.globalData.token
    if (!token) {
      return false
    }

    try {
      // 解析JWT token获取过期时间
      const payload = JSON.parse(atob(token.split('.')[1]))
      const exp = payload.exp
      const now = Math.floor(Date.now() / 1000)

      if (exp < now) {
        // token已过期
        this.logout()
        return false
      }

      return true
    } catch (error) {
      console.error('Token验证失败:', error)
      return false
    }
  },

  // 登录成功处理
  loginSuccess(token, userInfo) {
    this.globalData.token = token
    this.globalData.userInfo = userInfo

    // 持久化存储
    wx.setStorageSync('token', token)
    wx.setStorageSync('userInfo', userInfo)
  },

  // 退出登录
  logout() {
    this.globalData.token = null
    this.globalData.userInfo = null

    // 清除存储
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')

    // 跳转到登录页
    wx.reLaunch({
      url: '/pages/login/login'
    })
  },

  // 检查是否登录
  isLogin() {
    return !!this.globalData.token && this.checkLoginStatus()
  }
})
