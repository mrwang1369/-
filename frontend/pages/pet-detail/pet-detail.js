// pages/pet-detail/pet-detail.js
const app = getApp()
const api = require('../../api/index.js')
const DateUtil = require('../../utils/date.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    petId: null,
    pet: null,
    vaccineCount: 0,
    dewormingCount: 0,
    checkupCount: 0,
    medicalCount: 0,
    growthEvents: []
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ petId: options.id })
      this.loadPetDetail()
    }
  },

  onShow() {
    // 页面显示时刷新数据
    if (this.data.petId) {
      this.loadPetDetail()
    }
  },

  /**
   * 加载宠物详情
   */
  async loadPetDetail() {
    try {
      const result = await api.pet.getPetDetail(this.data.petId)

      if (result) {
        // 计算年龄
        const age = DateUtil.calculateAge(result.birthDate)

        this.setData({
          pet: {
            ...result,
            age
          }
        })

        // 加载统计数据
        await this.loadStatistics()
      }
    } catch (error) {
      console.error('加载宠物详情失败:', error)
      Common.toast('加载失败')
    }
  },

  /**
   * 加载统计数据
   */
  async loadStatistics() {
    try {
      // 加载各类型记录数量
      const [vaccineResult, dewormingResult, checkupResult, medicalResult] = await Promise.all([
        api.vaccine.getPetRecords(this.data.petId),
        api.deworming.getPetRecords(this.data.petId),
        api.checkup.getPetRecords(this.data.petId),
        api.medical.getPetRecords(this.data.petId)
      ])

      this.setData({
        vaccineCount: vaccineResult?.length || 0,
        dewormingCount: dewormingResult?.length || 0,
        checkupCount: checkupResult?.length || 0,
        medicalCount: medicalResult?.length || 0
      })

      // 构建成长时光轴数据
      this.buildGrowthTimeline(vaccineResult, checkupResult, medicalResult)
    } catch (error) {
      console.error('加载统计数据失败:', error)
    }
  },

  /**
   * 构建成长时光轴
   */
  buildGrowthTimeline(vaccineRecords, checkupRecords, medicalRecords) {
    const events = []

    // 疫苗记录
    if (vaccineRecords && Array.isArray(vaccineRecords)) {
      vaccineRecords.forEach(record => {
        events.push({
          id: `vaccine_${record.recordId}`,
          type: 'vaccine',
          title: `接种${record.vaccineName}`,
          date: DateUtil.formatDate(record.vaccinationDate)
        })
      })
    }

    // 体检记录
    if (checkupRecords && Array.isArray(checkupRecords)) {
      checkupRecords.forEach(record => {
        events.push({
          id: `checkup_${record.recordId}`,
          type: 'checkup',
          title: '健康体检',
          date: DateUtil.formatDate(record.checkupDate)
        })
      })
    }

    // 病历记录
    if (medicalRecords && Array.isArray(medicalRecords)) {
      medicalRecords.forEach(record => {
        events.push({
          id: `medical_${record.recordId}`,
          type: 'medical',
          title: record.diagnosisResult || '就医记录',
          date: DateUtil.formatDate(record.visitDate)
        })
      })
    }

    // 按日期倒序排序
    events.sort((a, b) => new Date(b.date) - new Date(a.date))

    this.setData({ growthEvents: events })
  },

  /**
   * 更换头像
   */
  async changeAvatar() {
    try {
      const res = await Common.chooseImage(1)

      if (res.tempFilePaths && res.tempFilePaths.length > 0) {
        Common.showLoading('上传中...')

        const filePath = res.tempFilePaths[0]
        const result = await api.pet.uploadPetAvatar(this.data.petId, filePath)

        Common.hideLoading()

        if (result && result.fileUrl) {
          Common.toast('上传成功', 'success')
          // 刷新宠物详情
          await this.loadPetDetail()
        }
      }
    } catch (error) {
      Common.hideLoading()
      console.error('更换头像失败:', error)
      Common.toast('上传失败')
    }
  },

  /**
   * 编辑宠物信息
   */
  editPet() {
    wx.navigateTo({
      url: `/pages/pet-add/pet-add?id=${this.data.petId}`
    })
  },

  /**
   * 删除宠物档案
   */
  async deletePet() {
    const confirmed = await Common.confirm('确定要删除该宠物档案吗？此操作不可恢复。', '删除确认')

    if (confirmed) {
      try {
        await api.pet.deletePet(this.data.petId)
        Common.toast('删除成功', 'success')

        // 返回宠物列表
        setTimeout(() => {
          wx.navigateBack()
        }, 1000)
      } catch (error) {
        console.error('删除失败:', error)
        Common.toast('删除失败')
      }
    }
  },

  /**
   * 导航到疫苗记录
   */
  navigateToVaccine() {
    wx.navigateTo({
      url: `/pages/vaccine/vaccine?petId=${this.data.petId}`
    })
  },

  /**
   * 导航到驱虫记录
   */
  navigateToDeworming() {
    wx.navigateTo({
      url: `/pages/deworming/deworming?petId=${this.data.petId}`
    })
  },

  /**
   * 导航到体检记录
   */
  navigateToCheckup() {
    wx.navigateTo({
      url: `/pages/checkup/checkup?petId=${this.data.petId}`
    })
  },

  /**
   * 导航到病历记录
   */
  navigateToMedical() {
    wx.navigateTo({
      url: `/pages/medical/medical?petId=${this.data.petId}`
    })
  },

  /**
   * 导航到成长时光轴
   */
  navigateToGrowth() {
    wx.navigateTo({
      url: `/pages/growth/growth?petId=${this.data.petId}`
    })
  }
})
