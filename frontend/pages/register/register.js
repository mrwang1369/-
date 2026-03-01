// pages/register/register.js
const Auth = require('../../utils/auth.js')
const Validator = require('../../utils/validator.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    formData: {
      phone: '',
      nickname: '',
      password: '',
      confirmPassword: ''
    },
    showPassword: false,
    showConfirmPassword: false,
    agreed: false,
    loading: false
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
   * 昵称输入
   */
  onNicknameInput(e) {
    this.setData({
      'formData.nickname': e.detail.value
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
   * 确认密码输入
   */
  onConfirmPasswordInput(e) {
    this.setData({
      'formData.confirmPassword': e.detail.value
    })
  },

  /**
   * 切换密码显示
   */
  togglePassword() {
    this.setData({
      showPassword: !this.data.showPassword
    })
  },

  /**
   * 切换确认密码显示
   */
  toggleConfirmPassword() {
    this.setData({
      showConfirmPassword: !this.data.showConfirmPassword
    })
  },

  /**
   * 切换协议同意状态
   */
  toggleAgreement() {
    this.setData({
      agreed: !this.data.agreed
    })
  },

  /**
   * 显示用户协议
   */
  showAgreement() {
    wx.showModal({
      title: '用户协议',
      content: '这里是用户协议内容...',
      showCancel: false
    })
  },

  /**
   * 显示隐私政策
   */
  showPrivacy() {
    wx.showModal({
      title: '隐私政策',
      content: '这里是隐私政策内容...',
      showCancel: false
    })
  },

  /**
   * 注册
   */
  async handleRegister() {
    const { phone, nickname, password, confirmPassword } = this.data.formData

    // 表单验证
    if (!phone) {
      Common.toast('请输入手机号')
      return
    }

    if (!Validator.isPhone(phone)) {
      Common.toast('手机号格式不正确')
      return
    }

    if (!nickname) {
      Common.toast('请输入昵称')
      return
    }

    if (!password) {
      Common.toast('请设置密码')
      return
    }

    if (password.length < 6 || password.length > 20) {
      Common.toast('密码长度应在6-20位之间')
      return
    }

    if (password !== confirmPassword) {
      Common.toast('两次输入的密码不一致')
      return
    }

    // 显示加载状态
    this.setData({ loading: true })

    try {
      // 调用注册接口
      const result = await Auth.register(phone, password, nickname)

      if (result.success) {
        Common.toast('注册成功，请登录', 'success')

        // 延迟跳转到登录页
        setTimeout(() => {
          wx.navigateBack()
        }, 1000)
      } else {
        Common.toast(result.message || '注册失败')
      }
    } catch (error) {
      console.error('注册失败:', error)
      Common.toast('注册失败，请稍后重试')
    } finally {
      this.setData({ loading: false })
    }
  },

  /**
   * 返回登录页
   */
  goToLogin() {
    wx.navigateBack()
  }
})
