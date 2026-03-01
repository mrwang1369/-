// pages/reminder/reminder.js
const app = getApp()
const api = require('../../api/index.js')
const DateUtil = require('../../utils/date.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    currentTab: 'today',
    reminders: [],
    filteredReminders: [],
    todayCount: 0,
    upcomingCount: 0,
    overdueCount: 0,
    frequencyText: {
      once: '一次',
      daily: '每日',
      weekly: '每周',
      monthly: '每月'
    }
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

  onPullDownRefresh() {
    this.loadData().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  /**
   * 加载数据
   */
  async loadData() {
    try {
      const result = await api.reminder.getReminders()

      if (result && Array.isArray(result)) {
        // 处理提醒数据
        const reminders = this.processReminders(result)
        const { today, upcoming, overdue } = this.categorizeReminders(reminders)

        this.setData({
          reminders,
          todayCount: today.length,
          upcomingCount: upcoming.length,
          overdueCount: overdue.length
        })

        // 根据当前标签过滤
        this.filterReminders()
      }
    } catch (error) {
      console.error('加载提醒失败:', error)
      Common.toast('加载失败')
    }
  },

  /**
   * 处理提醒数据
   */
  processReminders(reminders) {
    return reminders.map(item => {
      const dueDate = new Date(item.dueDate)
      const isToday = DateUtil.isToday(dueDate)
      const isPast = DateUtil.isPast(dueDate)
      const daysUntilDue = DateUtil.daysBetween(new Date(), dueDate)

      return {
        ...item,
        dueDate: DateUtil.format(item.dueDate, 'YYYY-MM-DD HH:mm'),
        isToday,
        isPast,
        daysUntilDue
      }
    })
  },

  /**
   * 分类提醒
   */
  categorizeReminders(reminders) {
    const today = []
    const upcoming = []
    const overdue = []

    reminders.forEach(item => {
      if (item.completed) {
        return
      }

      if (item.isToday) {
        today.push(item)
      } else if (item.isPast) {
        overdue.push(item)
      } else if (item.daysUntilDue <= 7) {
        upcoming.push(item)
      }
    })

    return { today, upcoming, overdue }
  },

  /**
   * 切换标签
   */
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ currentTab: tab })
    this.filterReminders()
  },

  /**
   * 过滤提醒
   */
  filterReminders() {
    const { currentTab, reminders } = this.data
    let filtered = []

    switch (currentTab) {
      case 'today':
        filtered = reminders.filter(item => item.isToday && !item.completed)
        break
      case 'upcoming':
        filtered = reminders.filter(item => !item.completed && !item.isToday && !item.isPast && item.daysUntilDue <= 7)
        break
      case 'overdue':
        filtered = reminders.filter(item => item.isPast && !item.completed)
        break
      case 'all':
        filtered = reminders
        break
    }

    // 按截止时间排序
    filtered.sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))

    this.setData({ filteredReminders: filtered })
  },

  /**
   * 切换完成状态
   */
  async toggleComplete(e) {
    const reminderId = e.currentTarget.dataset.id

    try {
      await api.reminder.completeReminder(reminderId)
      Common.toast('操作成功', 'success')
      await this.loadData()
    } catch (error) {
      console.error('操作失败:', error)
      Common.toast('操作失败')
    }
  },

  /**
   * 编辑提醒
   */
  editReminder(e) {
    const reminderId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/reminder-add/reminder-add?id=${reminderId}`
    })
  },

  /**
   * 删除提醒
   */
  async deleteReminder(e) {
    const reminderId = e.currentTarget.dataset.id
    const confirmed = await Common.confirm('确定要删除这条提醒吗？', '删除确认')

    if (confirmed) {
      try {
        await api.reminder.deleteReminder(reminderId)
        Common.toast('删除成功', 'success')
        await this.loadData()
      } catch (error) {
        console.error('删除失败:', error)
        Common.toast('删除失败')
      }
    }
  },

  /**
   * 生成健康提醒
   */
  async generateHealthReminders() {
    try {
      Common.showLoading('生成中...')

      await api.reminder.generateHealthReminders()

      Common.hideLoading()
      Common.toast('生成成功', 'success')

      await this.loadData()
    } catch (error) {
      Common.hideLoading()
      console.error('生成健康提醒失败:', error)
      Common.toast('生成失败')
    }
  },

  /**
   * 添加提醒
   */
  addReminder() {
    wx.navigateTo({
      url: '/pages/reminder-add/reminder-add'
    })
  }
})
