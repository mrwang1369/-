const api = require('../../api/index.js')
const common = require('../../utils/common.js')

Page({
  data: {
    petList: [],
    petIndex: 0,
    checkupTypes: ['常规体检', '全面体检', '专项体检', '其他'],
    typeIndex: 0,
    formData: {
      petId: null,
      type: '常规体检',
      checkupDate: '',
      institution: '',
      doctor: '',
      weight: '',
      result: '正常',
      checkItems: '',
      diagnosis: '',
      remark: '',
      reportUrl: ''
    },
    minDate: '2020-01-01',
    maxDate: '',
    loading: false
  },

  onLoad(options) {
    // 设置最大日期为今天
    const today = new Date()
    this.setData({
      maxDate: this.formatDate(today)
    })

    // 加载宠物列表
    this.loadPetList()

    // 如果是编辑模式，加载记录
    if (options.id) {
      this.loadCheckupRecord(options.id)
    }
  },

  /**
   * 加载宠物列表
   */
  async loadPetList() {
    try {
      const res = await api.pet.getMyPets()
      if (res.data && res.data.code === 200) {
        const pets = res.data.data || []
        this.setData({
          petList: pets,
          'formData.petId': pets[0]?.id
        })
      }
    } catch (error) {
      common.showToast('加载宠物列表失败')
    }
  },

  /**
   * 加载体检记录
   */
  async loadCheckupRecord(id) {
    try {
      const res = await api.health.getCheckupById(id)
      if (res.data && res.data.code === 200) {
        const record = res.data.data
        this.setData({
          formData: {
            ...this.data.formData,
            ...record
          }
        })
      }
    } catch (error) {
      common.showToast('加载记录失败')
    }
  },

  /**
   * 体检类型选择
   */
  onTypeChange(e) {
    const index = e.detail.value
    this.setData({
      typeIndex: index,
      'formData.type': this.data.checkupTypes[index]
    })
  },

  /**
   * 体检结果选择
   */
  selectResult(e) {
    const result = e.currentTarget.dataset.result
    this.setData({
      'formData.result': result
    })
  },

  /**
   * 宠物选择
   */
  onPetChange(e) {
    const index = e.detail.value
    this.setData({
      petIndex: index,
      'formData.petId': this.data.petList[index].id
    })
  },

  /**
   * 日期选择
   */
  onDateChange(e) {
    this.setData({
      'formData.checkupDate': e.detail.value
    })
  },

  /**
   * 输入事件
   */
  onInstitutionInput(e) {
    this.setData({
      'formData.institution': e.detail.value
    })
  },

  onDoctorInput(e) {
    this.setData({
      'formData.doctor': e.detail.value
    })
  },

  onWeightInput(e) {
    this.setData({
      'formData.weight': e.detail.value
    })
  },

  onCheckItemsInput(e) {
    this.setData({
      'formData.checkItems': e.detail.value
    })
  },

  onDiagnosisInput(e) {
    this.setData({
      'formData.diagnosis': e.detail.value
    })
  },

  onRemarkInput(e) {
    this.setData({
      'formData.remark': e.detail.value
    })
  },

  /**
   * 选择图片
   */
  chooseImage() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        this.uploadImage(res.tempFilePaths[0])
      }
    })
  },

  /**
   * 上传图片
   */
  async uploadImage(filePath) {
    try {
      wx.showLoading({ title: '上传中...' })
      const res = await api.common.uploadFile(filePath)
      if (res.data && res.data.code === 200) {
        this.setData({
          'formData.reportUrl': res.data.data.url
        })
        common.showToast('上传成功')
      }
    } catch (error) {
      common.showToast('上传失败')
    } finally {
      wx.hideLoading()
    }
  },

  /**
   * 删除图片
   */
  deleteImage() {
    this.setData({
      'formData.reportUrl': ''
    })
  },

  /**
   * 提交表单
   */
  onSubmit() {
    const { formData } = this.data

    // 表单验证
    if (!formData.petId) {
      common.showToast('请选择宠物')
      return
    }
    if (!formData.checkupDate) {
      common.showToast('请选择体检日期')
      return
    }
    if (!formData.institution.trim()) {
      common.showToast('请输入体检机构')
      return
    }
    if (!formData.weight.trim()) {
      common.showToast('请输入体重')
      return
    }

    // 提交
    this.saveCheckupRecord()
  },

  /**
   * 保存体检记录
   */
  async saveCheckupRecord() {
    try {
      this.setData({ loading: true })
      wx.showLoading({ title: '保存中...' })

      const { formData } = this.data
      const res = await api.health.addCheckup(formData)

      if (res.data && res.data.code === 200) {
        common.showToast('保存成功', 'success')
        
        // 延迟返回
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      } else {
        common.showToast(res.data?.message || '保存失败')
      }
    } catch (error) {
      console.error('保存体检记录失败:', error)
      common.showToast('保存失败，请重试')
    } finally {
      this.setData({ loading: false })
      wx.hideLoading()
    }
  },

  /**
   * 取消
   */
  onCancel() {
    wx.showModal({
      title: '提示',
      content: '确定要取消吗？未保存的内容将丢失',
      success: (res) => {
        if (res.confirm) {
          wx.navigateBack()
        }
      }
    })
  },

  /**
   * 格式化日期
   */
  formatDate(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }
})
