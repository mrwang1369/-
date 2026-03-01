// pages/reminder-add/reminder-add.js
const app = getApp()
const api = require('../../api/index.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    isEdit: false,
    reminderId: null,
    loading: false,
    pets: [],
    typeOptions: ['喂食', '洗澡', '疫苗', '驱虫', '体检', '其他'],
    frequencyOptions: ['once', 'daily', 'weekly', 'monthly'],
    frequencyText: {
      once: '一次',
      daily: '每日',
      weekly: '每周',
      monthly: '每月'
    },
    priorityOptions: ['low', 'medium', 'high'],
    priorityText: {
      low: '低',
      medium: '中',
      high: '高'
    },
    timeRange: [],
    formData: {
      petId: null,
      reminderType: '喂食',
      title: '',
      description: '',
      dueDate: '',
      frequency: 'once',
      priority: 'medium'
    }
  },

  onLoad(options) {
    // 初始化时间选择器
    this.initTimeRange()

    // 加载宠物列表
    this.loadPets()

    // 判断是编辑还是新增
    if (options.id) {
      this.setData({
        isEdit: true,
        reminderId: options.id
      })
      this.loadReminderDetail()
    }
  },

  /**
   * 初始化时间选择器
   */
  initTimeRange() {
    const hours = []
    const minutes = []

    for (let i = 0; i < 24; i++) {
      hours.push({
        value: i,
        label: `${String(i).padStart(2, '0')}:00`
      })
    }

    for (let i = 0; i < 60; i += 5) {
      minutes.push({
        value: i,
        label: `${String(i).padStart(2, '0')}`
      })
    }

    this.setData({
      timeRange: [hours, minutes]
    })
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

      if (result && result.pets) {
        this.setData({ pets: result.pets })
      }
    } catch (error) {
      console.error('加载宠物列表失败:', error)
    }
  },

  /**
   * 加载提醒详情（编辑模式）
   */
  async loadReminderDetail() {
    try {
      Common.showLoading('加载中...')

      const result = await api.reminder.getReminderDetail(this.data.reminderId)

      Common.hideLoading()

      if (result) {
        this.setData({
          formData: {
            petId: result.petId || null,
            reminderType: result.reminderType || '喂食',
            title: result.title || '',
            description: result.description || '',
            dueDate: result.dueDate || '',
            frequency: result.frequency || 'once',
            priority: result.priority || 'medium'
          }
        })
      }
    } catch (error) {
      Common.hideLoading()
      console.error('加载提醒详情失败:', error)
      Common.toast('加载失败')
      wx.navigateBack()
    }
  },

  /**
   * 选择提醒类型
   */
  selectType(e) {
    const type = e.currentTarget.dataset.type
    this.setData({
      'formData.reminderType': type,
      'formData.title': type
    })
  },

  /**
   * 选择宠物
   */
  selectPet(e) {
    const petId = e.currentTarget.dataset.id
    this.setData({
      'formData.petId': petId === '' ? null : petId
    })
  },

  /**
   * 标题输入
   */
  onTitleInput(e) {
    this.setData({
      'formData.title': e.detail.value
    })
  },

  /**
   * 描述输入
   */
  onDescriptionInput(e) {
    this.setData({
      'formData.description': e.detail.value
    })
  },

  /**
   * 时间选择
   */
  onTimeChange(e) {
    const [hourIndex, minuteIndex] = e.detail.value
    const { timeRange } = this.data
    const hour = timeRange[0][hourIndex].value
    const minute = timeRange[1][minuteIndex].value

    const now = new Date()
    now.setHours(hour, minute, 0, 0)

    this.setData({
      'formData.time': `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`,
      'formData.dueDate': now.toISOString()
    })
  },

  /**
   * 重复周期选择
   */
  onFrequencyChange(e) {
    const index = e.detail.value
    const frequency = this.data.frequencyOptions[index]
    this.setData({
      'formData.frequency': frequency
    })
  },

  /**
   * 优先级选择
   */
  selectPriority(e) {
    const priority = e.currentTarget.dataset.priority
    this.setData({
      'formData.priority': priority
    })
  },

  /**
   * 提交表单
   */
  async handleSubmit() {
    const { formData, isEdit, reminderId } = this.data

    // 表单验证
    if (!formData.title) {
      Common.toast('请输入提醒标题')
      return
    }

    if (!formData.dueDate) {
      Common.toast('请选择提醒时间')
      return
    }

    // 显示加载状态
    this.setData({ loading: true })

    try {
      // 调用API
      if (isEdit) {
        await api.reminder.updateReminder(reminderId, formData)
        Common.toast('修改成功', 'success')
      } else {
        await api.reminder.createReminder(formData)
        Common.toast('创建成功', 'success')
      }

      // 延迟跳转
      setTimeout(() => {
        wx.navigateBack()
      }, 1000)
    } catch (error) {
      console.error('保存失败:', error)
      Common.toast('保存失败')
    } finally {
      this.setData({ loading: false })
    }
  }
})
