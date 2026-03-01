// pages/checkup/checkup.js
const app = getApp()
const api = require('../../api/index.js')
const DateUtil = require('../../utils/date.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    petId: null,
    records: []
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
   * 加载体检记录
   */
  async loadRecords() {
    try {
      const result = await api.checkup.getPetRecords(this.data.petId)

      if (result && Array.isArray(result)) {
        // 处理数据
        const records = result.map(item => {
          const checkupDate = new Date(item.checkupDate)
          const lastYear = new Date()
          lastYear.setFullYear(lastYear.getFullYear() - 1)

          const isOverdue = checkupDate < lastYear

          return {
            ...item,
            checkupDate: DateUtil.formatDate(item.checkupDate),
            status: isOverdue ? 'overdue' : 'completed',
            statusText: isOverdue ? '需要体检' : '已完成',
            attachments: item.attachments || []
          }
        })

        // 按体检日期倒序排序
        records.sort((a, b) => new Date(b.checkupDate) - new Date(a.checkupDate))

        this.setData({ records })
      }
    } catch (error) {
      console.error('加载体检记录失败:', error)
      Common.toast('加载失败')
    }
  },

  /**
   * 预览附件
   */
  previewAttachment(e) {
    const url = e.currentTarget.dataset.url
    wx.previewImage({
      urls: [url],
      current: url
    })
  },

  /**
   * 添加记录
   */
  addRecord() {
    wx.navigateTo({
      url: `/pages/checkup-add/checkup-add?petId=${this.data.petId}`
    })
  },

  /**
   * 编辑记录
   */
  editRecord(e) {
    const recordId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/checkup-add/checkup-add?petId=${this.data.petId}&recordId=${recordId}`
    })
  },

  /**
   * 删除记录
   */
  async deleteRecord(e) {
    const recordId = e.currentTarget.dataset.id
    const confirmed = await Common.confirm('确定要删除这条体检记录吗？', '删除确认')

    if (confirmed) {
      try {
        await api.checkup.deleteRecord(recordId)
        Common.toast('删除成功', 'success')
        await this.loadRecords()
      } catch (error) {
        console.error('删除失败:', error)
        Common.toast('删除失败')
      }
    }
  }
})
