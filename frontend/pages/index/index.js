// pages/index/index.js
const app = getApp()
const api = require('../../api/index.js')
const DateUtil = require('../../utils/date.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    userInfo: {},
    pets: [],
    todayReminders: [],
    upcomingItems: []
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
   * 加载首页数据
   */
  async loadData() {
    // 获取用户信息
    this.getUserInfo()

    // 加载宠物列表
    await this.loadPets()

    // 加载今日提醒
    await this.loadTodayReminders()

    // 加载即将到期项目
    await this.loadUpcomingItems()
  },

  /**
   * 获取用户信息
   */
  getUserInfo() {
    const userInfo = app.globalData.userInfo || {}
    this.setData({ userInfo })
  },

  /**
   * 加载宠物列表
   */
  async loadPets() {
    try {
      const result = await api.pet.getPets({
        pageNum: 1,
        pageSize: 10
      })

      if (result && result.pets) {
        this.setData({ pets: result.pets })
      }
    } catch (error) {
      console.error('加载宠物列表失败:', error)
    }
  },

  /**
   * 加载今日提醒
   */
  async loadTodayReminders() {
    try {
      const result = await api.reminder.getTodayReminders()

      if (result && Array.isArray(result)) {
        // 格式化提醒数据
        const reminders = result.map(item => ({
          ...item,
          dueDate: DateUtil.format(item.dueDate, 'HH:mm')
        }))

        this.setData({ todayReminders: reminders })
      }
    } catch (error) {
      console.error('加载今日提醒失败:', error)
    }
  },

  /**
   * 加载即将到期项目
   */
  async loadUpcomingItems() {
    try {
      // 获取即将到期的疫苗
      const vaccineResult = await api.vaccine.getUpcomingRecords()
      // 获取即将驱虫提醒
      const dewormingResult = await api.deworming.getUpcomingRecords()

      const upcomingItems = []

      // 处理疫苗数据
      if (vaccineResult && Array.isArray(vaccineResult)) {
        vaccineResult.forEach(item => {
          const days = DateUtil.daysBetween(new Date(), item.nextDueDate)
          if (days <= 7) {
            upcomingItems.push({
              id: `vaccine_${item.recordId}`,
              type: '疫苗',
              title: item.vaccineName,
              petName: item.petName,
              date: DateUtil.formatDate(item.nextDueDate),
              days: days
            })
          }
        })
      }

      // 处理驱虫数据
      if (dewormingResult && Array.isArray(dewormingResult)) {
        dewormingResult.forEach(item => {
          const days = DateUtil.daysBetween(new Date(), item.nextDueDate)
          if (days <= 7) {
            upcomingItems.push({
              id: `deworming_${item.recordId}`,
              type: '驱虫',
              title: item.dewormingType,
              petName: item.petName,
              date: DateUtil.formatDate(item.nextDueDate),
              days: days
            })
          }
        })
      }

      // 按天数排序
      upcomingItems.sort((a, b) => a.days - b.days)

      this.setData({ upcomingItems })
    } catch (error) {
      console.error('加载即将到期项目失败:', error)
    }
  },

  /**
   * 完成提醒
   */
  async completeReminder(e) {
    const reminderId = e.currentTarget.dataset.id

    try {
      await api.reminder.completeReminder(reminderId)
      Common.toast('已完成', 'success')

      // 刷新提醒列表
      await this.loadTodayReminders()
    } catch (error) {
      console.error('完成提醒失败:', error)
      Common.toast('操作失败')
    }
  },

  /**
   * 导航到添加宠物页面
   */
  navigateToPetAdd() {
    if (!app.isLogin()) {
      wx.reLaunch({
        url: '/pages/login/login'
      })
      return
    }

    wx.navigateTo({
      url: '/pages/pet-add/pet-add'
    })
  },

  /**
   * 导航到宠物列表
   */
  navigateToPets() {
    wx.switchTab({
      url: '/pages/pets/pets'
    })
  },

  /**
   * 导航到宠物详情
   */
  navigateToPetDetail(e) {
    const petId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/pet-detail/pet-detail?id=${petId}`
    })
  },

  /**
   * 导航到健康记录
   */
  navigateToHealth() {
    wx.switchTab({
      url: '/pages/health/health'
    })
  },

  /**
   * 导航到提醒
   */
  navigateToReminder() {
    wx.switchTab({
      url: '/pages/reminder/reminder'
    })
  },

  /**
   * 导航到地图
   */
  navigateToMap() {
    wx.switchTab({
      url: '/pages/map/map'
    })
  }
})
