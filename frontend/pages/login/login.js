// pages/login/login.js
const app = getApp()
const Auth = require('../../utils/auth.js')
const Validator = require('../../utils/validator.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    formData: {
      phone: '',
      password: ''
    },
    showPassword: false,
    loading: false
  },

  onLoad(options) {
    // 检查是否已登录
    if (Auth.isLogin()) {
      wx.switchTab({
        url: '/pages/index/index'
      })
    }
  },

  /**
   * 手机号输入
   */
  onPhoneInput(e) {
    this.setData({
      'formData.phone': e.detail.value
    })
  },

  /**
   * 密码输入
   */
  onPasswordInput(e) {
    this.setData({
      'formData.password': e.detail.value
    })
  },

  /**
   * 切换密码显示/隐藏
   */
  togglePassword() {
    this.setData({
      showPassword: !this.data.showPassword
    })
  },

  /**
   * 手机号登录
   */
  async handleLogin() {
    const { phone, password } = this.data.formData

    // 表单验证
    if (!phone) {
      Common.toast('请输入手机号')
      return
    }

    if (!Validator.isPhone(phone)) {
      Common.toast('手机号格式不正确')
      return
    }

    if (!password) {
      Common.toast('请输入密码')
      return
    }

    if (password.length < 6) {
      Common.toast('密码长度不能少于6位')
      return
    }

    // 显示加载状态
    this.setData({ loading: true })

    try {
      // 调用登录接口
      const result = await Auth.login(phone, password)

      console.log('登录结果:', result)

      if (result && result.success) {
        Common.toast('登录成功', 'success')

        // 延迟跳转，让用户看到成功提示
        setTimeout(() => {
          wx.switchTab({
            url: '/pages/index/index',
            success: () => {
              console.log('跳转成功')
            },
            fail: (err) => {
              console.error('跳转失败:', err)
            }
          })
        }, 1000)
      } else {
        Common.toast(result.message || '登录失败')
      }
    } catch (error) {
      console.error('登录失败:', error)
      Common.toast('登录失败，请稍后重试')
    } finally {
      this.setData({ loading: false })
    }
  },

  /**
   * 微信登录
   */
  async handleWxLogin(e) {
    if (e.detail.userInfo) {
      // 用户授权成功
      this.setData({ loading: true })

      try {
        // 获取微信登录code
        const loginRes = await wx.login()

        if (loginRes.code) {
          // 调用微信登录接口
          const result = await Auth.wxLogin(loginRes.code)

          if (result.success) {
            Common.toast('登录成功', 'success')

            // 延迟跳转
            setTimeout(() => {
              wx.switchTab({
                url: '/pages/index/index'
              })
            }, 1000)
          } else {
            Common.toast(result.message || '微信登录失败')
          }
        } else {
          Common.toast('获取微信登录凭证失败')
        }
      } catch (error) {
        console.error('微信登录失败:', error)
        Common.toast('微信登录失败，请稍后重试')
      } finally {
        this.setData({ loading: false })
      }
    }
  },

  /**
   * 跳转注册页
   */
  goToRegister() {
    wx.navigateTo({
      url: '/pages/register/register'
    })
  }
})
