const api = require('../../api/index.js')
const common = require('../../utils/common.js')

Page({
  data: {
    petList: [],
    petIndex: 0,
    formData: {
      petId: null,
      type: '体外',
      drugName: '',
      dewormingDate: '',
      nextDewormingDate: '',
      institution: '',
      dosage: '',
      remark: ''
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
      this.loadDewormingRecord(options.id)
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
   * 加载驱虫记录
   */
  async loadDewormingRecord(id) {
    try {
      const res = await api.health.getDewormingById(id)
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
   * 选择驱虫类型
   */
  selectType(e) {
    const type = e.currentTarget.dataset.type
    this.setData({
      'formData.type': type
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
    const date = e.detail.value
    this.setData({
      'formData.dewormingDate': date
    })
    
    // 自动计算下次驱虫日期（默认30天后）
    const nextDate = this.addDays(date, 30)
    this.setData({
      'formData.nextDewormingDate': nextDate
    })
  },

  /**
   * 下次日期选择
   */
  onNextDateChange(e) {
    this.setData({
      'formData.nextDewormingDate': e.detail.value
    })
  },

  /**
   * 输入事件
   */
  onDrugInput(e) {
    this.setData({
      'formData.drugName': e.detail.value
    })
  },

  onInstitutionInput(e) {
    this.setData({
      'formData.institution': e.detail.value
    })
  },

  onDosageInput(e) {
    this.setData({
      'formData.dosage': e.detail.value
    })
  },

  onRemarkInput(e) {
    this.setData({
      'formData.remark': e.detail.value
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
    if (!formData.drugName.trim()) {
      common.showToast('请输入药品名称')
      return
    }
    if (!formData.dewormingDate) {
      common.showToast('请选择驱虫日期')
      return
    }
    if (!formData.nextDewormingDate) {
      common.showToast('请选择下次驱虫日期')
      return
    }

    // 提交
    this.saveDewormingRecord()
  },

  /**
   * 保存驱虫记录
   */
  async saveDewormingRecord() {
    try {
      this.setData({ loading: true })
      wx.showLoading({ title: '保存中...' })

      const { formData } = this.data
      const res = await api.health.addDeworming(formData)

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
      console.error('保存驱虫记录失败:', error)
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
  },

  /**
   * 日期加天数
   */
  addDays(dateStr, days) {
    const date = new Date(dateStr)
    date.setDate(date.getDate() + days)
    return this.formatDate(date)
  }
})
