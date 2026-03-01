// pages/growth/growth.js
const app = getApp()
const api = require('../../api/index.js')
const DateUtil = require('../../utils/date.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    petId: null,
    pets: [],
    currentFilter: 'all',
    filterOptions: [
      { label: '全部', value: 'all' },
      { label: '疫苗', value: 'vaccine' },
      { label: '驱虫', value: 'deworming' },
      { label: '体检', value: 'checkup' },
      { label: '病历', value: 'medical' }
    ],
    timelineEvents: [],
    hasMore: false,
    pageNum: 1,
    pageSize: 20
  },

  onLoad(options) {
    if (options.petId) {
      this.setData({ petId: options.petId })
    }

    // 加载宠物列表
    this.loadPets()
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
        const currentPetId = this.data.petId || pets[0].petId

        this.setData({
          pets,
          currentPetId
        })

        // 加载时光轴数据
        this.loadTimeline()
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
    this.setData({
      currentPetId: petId,
      pageNum: 1,
      timelineEvents: []
    })
    this.loadTimeline()
  },

  /**
   * 选择筛选器
   */
  selectFilter(e) {
    const filter = e.currentTarget.dataset.value
    this.setData({
      currentFilter: filter,
      pageNum: 1,
      timelineEvents: []
    })
    this.loadTimeline()
  },

  /**
   * 加载时光轴数据
   */
  async loadTimeline() {
    const { currentPetId, currentFilter, pageNum, pageSize } = this.data

    try {
      // 加载各类记录
      const [vaccineResult, dewormingResult, checkupResult, medicalResult] = await Promise.all([
        api.vaccine.getPetRecords(currentPetId),
        api.deworming.getPetRecords(currentPetId),
        api.checkup.getPetRecords(currentPetId),
        api.medical.getPetRecords(currentPetId)
      ])

      const events = []

      // 疫苗记录
      if (vaccineResult && Array.isArray(vaccineResult)) {
        if (currentFilter === 'all' || currentFilter === 'vaccine') {
          vaccineResult.forEach(record => {
            events.push({
              id: `vaccine_${record.recordId}`,
              type: 'vaccine',
              typeLabel: '疫苗',
              title: `接种${record.vaccineName}`,
              description: record.veterinarian ? `医生：${record.veterinarian}` : '',
              date: record.vaccinationDate,
              images: []
            })
          })
        }
      }

      // 驱虫记录
      if (dewormingResult && Array.isArray(dewormingResult)) {
        if (currentFilter === 'all' || currentFilter === 'deworming') {
          dewormingResult.forEach(record => {
            events.push({
              id: `deworming_${record.recordId}`,
              type: 'deworming',
              typeLabel: '驱虫',
              title: `${record.dewormingType}驱虫`,
              description: record.dewormingMedicine ? `药物：${record.dewormingMedicine}` : '',
              date: record.dewormingDate,
              images: []
            })
          })
        }
      }

      // 体检记录
      if (checkupResult && Array.isArray(checkupResult)) {
        if (currentFilter === 'all' || currentFilter === 'checkup') {
          checkupResult.forEach(record => {
            events.push({
              id: `checkup_${record.recordId}`,
              type: 'checkup',
              typeLabel: '体检',
              title: '健康体检',
              description: record.weight ? `体重：${record.weight}kg` : '',
              date: record.checkupDate,
              images: record.attachments || []
            })
          })
        }
      }

      // 病历记录
      if (medicalResult && Array.isArray(medicalResult)) {
        if (currentFilter === 'all' || currentFilter === 'medical') {
          medicalResult.forEach(record => {
            events.push({
              id: `medical_${record.recordId}`,
              type: 'medical',
              typeLabel: '病历',
              title: record.diagnosisResult || '就医记录',
              description: record.hospital ? `医院：${record.hospital}` : '',
              date: record.visitDate,
              images: record.attachments || []
            })
          })
        }
      }

      // 按日期倒序排序
      events.sort((a, b) => new Date(b.date) - new Date(a.date))

      // 处理日期显示
      const processedEvents = events.map(item => {
        const date = new Date(item.date)
        const day = String(date.getDate()).padStart(2, '0')
        const month = String(date.getMonth() + 1)
        const year = date.getFullYear()

        return {
          ...item,
          day,
          month,
          year,
          time: DateUtil.format(item.date, 'YYYY-MM-DD')
        }
      })

      this.setData({
        timelineEvents: processedEvents,
        hasMore: false // 暂时分页
      })
    } catch (error) {
      console.error('加载时光轴失败:', error)
      Common.toast('加载失败')
    }
  },

  /**
   * 加载更多
   */
  loadMore() {
    // 这里可以实现分页加载
    Common.toast('已经到底啦')
  },

  /**
   * 预览图片
   */
  previewImage(e) {
    const urls = e.currentTarget.dataset.urls
    const current = e.currentTarget.dataset.current

    wx.previewImage({
      urls,
      current
    })
  },

  /**
   * 生成成长报告
   */
  generateReport() {
    wx.showModal({
      title: '提示',
      content: '成长报告功能开发中，敬请期待！',
      showCancel: false
    })
  }
})
