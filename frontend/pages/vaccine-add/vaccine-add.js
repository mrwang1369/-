const api = require('../../api/index.js')
const common = require('../../utils/common.js')

Page({
  data: {
    petList: [],
    petIndex: 0,
    vaccineTypes: ['狂犬疫苗', '联合疫苗', '冠状病毒疫苗', '其他'],
    typeIndex: 0,
    formData: {
      petId: null,
      name: '',
      type: '狂犬疫苗',
      vaccinateDate: '',
      hospital: '',
      doctor: '',
      batchNumber: '',
      remark: '',
      certificateUrl: ''
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
      this.loadVaccineRecord(options.id)
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
   * 加载疫苗记录
   */
  async loadVaccineRecord(id) {
    try {
      const res = await api.health.getVaccineById(id)
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
   * 疫苗类型选择
   */
  onTypeChange(e) {
    const index = e.detail.value
    this.setData({
      typeIndex: index,
      'formData.type': this.data.vaccineTypes[index]
    })
  },

  /**
   * 日期选择
   */
  onDateChange(e) {
    this.setData({
      'formData.vaccinateDate': e.detail.value
    })
  },

  /**
   * 输入事件
   */
  onNameInput(e) {
    this.setData({
      'formData.name': e.detail.value
    })
  },

  onHospitalInput(e) {
    this.setData({
      'formData.hospital': e.detail.value
    })
  },

  onDoctorInput(e) {
    this.setData({
      'formData.doctor': e.detail.value
    })
  },

  onBatchInput(e) {
    this.setData({
      'formData.batchNumber': e.detail.value
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
          'formData.certificateUrl': res.data.data.url
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
      'formData.certificateUrl': ''
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
    if (!formData.name.trim()) {
      common.showToast('请输入疫苗名称')
      return
    }
    if (!formData.vaccinateDate) {
      common.showToast('请选择接种日期')
      return
    }
    if (!formData.hospital.trim()) {
      common.showToast('请输入接种医院')
      return
    }

    // 提交
    this.saveVaccineRecord()
  },

  /**
   * 保存疫苗记录
   */
  async saveVaccineRecord() {
    try {
      this.setData({ loading: true })
      wx.showLoading({ title: '保存中...' })

      const { formData } = this.data
      const res = await api.health.addVaccine(formData)

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
      console.error('保存疫苗记录失败:', error)
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
