// pages/health/health.js
const app = getApp()
const api = require('../../api/index.js')
const DateUtil = require('../../utils/date.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    pets: [],
    currentPetId: null,
    vaccineCount: 0,
    dewormingCount: 0,
    checkupCount: 0,
    medicalCount: 0,
    recentRecords: []
  },

  onLoad() {
    // 检查登录状态
    if (!app.isLogin()) {
      wx.reLaunch({
        url: '/pages/login/login'
      })
      return
    }

    // 加载数据
    this.loadData()
  },

  onShow() {
    // 页面显示时刷新数据
    if (app.isLogin()) {
      this.loadData()
    }
  },

  /**
   * 加载数据
   */
  async loadData() {
    await this.loadPets()
    if (this.data.currentPetId) {
      await this.loadHealthData()
    }
  },

  /**
   * 加载宠物列表
   */
  async loadPets() {
    try {
      const result = await api.pet.getPets({
        pageNum: 1,
        pageSize: 20
      })

      if (result && result.pets && result.pets.length > 0) {
        const pets = result.pets
        const currentPetId = this.data.currentPetId || pets[0].petId

        this.setData({
          pets,
          currentPetId
        })

        // 加载选中宠物的健康数据
        await this.loadHealthData()
      }
    } catch (error) {
      console.error('加载宠物列表失败:', error)
    }
  },

  /**
   * 选择宠物
   */
  selectPet(e) {
    const petId = e.currentTarget.dataset.id
    this.setData({ currentPetId: petId })
    this.loadHealthData()
  },

  /**
   * 加载健康数据
   */
  async loadHealthData() {
    const { currentPetId } = this.data

    try {
      // 加载各类型记录
      const [vaccineResult, dewormingResult, checkupResult, medicalResult] = await Promise.all([
        api.vaccine.getPetRecords(currentPetId),
        api.deworming.getPetRecords(currentPetId),
        api.checkup.getPetRecords(currentPetId),
        api.medical.getPetRecords(currentPetId)
      ])

      this.setData({
        vaccineCount: vaccineResult?.length || 0,
        dewormingCount: dewormingResult?.length || 0,
        checkupCount: checkupResult?.length || 0,
        medicalCount: medicalResult?.length || 0
      })

      // 构建近期记录
      this.buildRecentRecords(vaccineResult, dewormingResult, checkupResult, medicalResult)
    } catch (error) {
      console.error('加载健康数据失败:', error)
    }
  },

  /**
   * 构建近期记录
   */
  buildRecentRecords(vaccineRecords, dewormingRecords, checkupRecords, medicalRecords) {
    const records = []
    const pets = this.data.pets

    const getPetName = (petId) => {
      const pet = pets.find(p => p.petId === petId)
      return pet ? pet.name : '未知'
    }

    // 疫苗记录
    if (vaccineRecords && Array.isArray(vaccineRecords)) {
      vaccineRecords.forEach(record => {
        records.push({
          id: `vaccine_${record.recordId}`,
          type: '疫苗',
          title: `接种${record.vaccineName}`,
          petName: getPetName(record.petId),
          date: DateUtil.formatDate(record.vaccinationDate)
        })
      })
    }

    // 驱虫记录
    if (dewormingRecords && Array.isArray(dewormingRecords)) {
      dewormingRecords.forEach(record => {
        records.push({
          id: `deworming_${record.recordId}`,
          type: '驱虫',
          title: `${record.dewormingType}驱虫`,
          petName: getPetName(record.petId),
          date: DateUtil.formatDate(record.dewormingDate)
        })
      })
    }

    // 体检记录
    if (checkupRecords && Array.isArray(checkupRecords)) {
      checkupRecords.forEach(record => {
        records.push({
          id: `checkup_${record.recordId}`,
          type: '体检',
          title: '健康体检',
          petName: getPetName(record.petId),
          date: DateUtil.formatDate(record.checkupDate)
        })
      })
    }

    // 病历记录
    if (medicalRecords && Array.isArray(medicalRecords)) {
      medicalRecords.forEach(record => {
        records.push({
          id: `medical_${record.recordId}`,
          type: '病历',
          title: record.diagnosisResult || '就医记录',
          petName: getPetName(record.petId),
          date: DateUtil.formatDate(record.visitDate)
        })
      })
    }

    // 按日期倒序排序，取前5条
    records.sort((a, b) => new Date(b.date) - new Date(a.date))

    this.setData({
      recentRecords: records.slice(0, 5)
    })
  },

  /**
   * 导航到疫苗记录
   */
  navigateToVaccine() {
    wx.navigateTo({
      url: `/pages/vaccine/vaccine?petId=${this.data.currentPetId}`
    })
  },

  /**
   * 导航到驱虫记录
   */
  navigateToDeworming() {
    wx.navigateTo({
      url: `/pages/deworming/deworming?petId=${this.data.currentPetId}`
    })
  },

  /**
   * 导航到体检记录
   */
  navigateToCheckup() {
    wx.navigateTo({
      url: `/pages/checkup/checkup?petId=${this.data.currentPetId}`
    })
  },

  /**
   * 导航到病历记录
   */
  navigateToMedical() {
    wx.navigateTo({
      url: `/pages/medical/medical?petId=${this.data.currentPetId}`
    })
  },

  /**
   * 导航到成长时光轴
   */
  navigateToGrowth() {
    wx.navigateTo({
      url: `/pages/growth/growth?petId=${this.data.currentPetId}`
    })
  }
})
