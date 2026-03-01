// pages/medical/medical.js
const app = getApp()
const api = require('../../api/index.js')
const DateUtil = require('../../utils/date.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    petId: null,
    records: [],
    keyword: ''
  },

  onLoad(options) {
    if (options.petId) {
      this.setData({ petId: options.petId })
      this.loadRecords()
    }
  },

  onPullDownRefresh() {
    this.loadRecords().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  /**
   * 加载病历记录
   */
  async loadRecords(keyword = '') {
    try {
      let result

      if (keyword) {
        // 搜索
        result = await api.medical.searchRecords(keyword)
      } else {
        // 获取全部记录
        result = await api.medical.getPetRecords(this.data.petId)
      }

      if (result && Array.isArray(result)) {
        // 处理数据
        const records = result.map(item => ({
          ...item,
          visitDate: DateUtil.formatDate(item.visitDate),
          hasMedicationReminder: item.medicationList && item.medicationList.length > 0
        }))

        // 按就诊日期倒序排序
        records.sort((a, b) => new Date(b.visitDate) - new Date(a.visitDate))

        this.setData({ records })
      }
    } catch (error) {
      console.error('加载病历记录失败:', error)
      Common.toast('加载失败')
    }
  },

  /**
   * 搜索输入
   */
  onSearchInput(e) {
    this.setData({
      keyword: e.detail.value
    })
  },

  /**
   * 搜索
   */
  onSearch() {
    const keyword = this.data.keyword.trim()
    this.loadRecords(keyword)
  },

  /**
   * 查看详情
   */
  viewDetail(e) {
    const recordId = e.currentTarget.dataset.id
    // 这里可以跳转到详情页或直接弹出详情弹窗
    wx.navigateTo({
      url: `/pages/medical-add/medical-add?petId=${this.data.petId}&recordId=${recordId}&mode=view`
    })
  },

  /**
   * 添加记录
   */
  addRecord() {
    wx.navigateTo({
      url: `/pages/medical-add/medical-add?petId=${this.data.petId}`
    })
  }
})
