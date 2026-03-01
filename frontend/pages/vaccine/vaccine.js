// pages/vaccine/vaccine.js
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
   * 加载疫苗记录
   */
  async loadRecords() {
    try {
      const result = await api.vaccine.getPetRecords(this.data.petId)

      if (result && Array.isArray(result)) {
        // 处理数据，添加状态信息
        const records = result.map(item => {
          const nextDueDate = item.nextDueDate
          const isOverdue = nextDueDate && DateUtil.isPast(nextDueDate)
          const isUpcoming = nextDueDate && DateUtil.daysBetween(new Date(), nextDueDate) <= 7 && !isOverdue

          let status = 'completed'
          let statusText = '已完成'

          if (isOverdue) {
            status = 'overdue'
            statusText = '已过期'
          } else if (isUpcoming) {
            status = 'upcoming'
            statusText = '即将到期'
          }

          return {
            ...item,
            vaccinationDate: DateUtil.formatDate(item.vaccinationDate),
            nextDueDate: nextDueDate ? DateUtil.formatDate(nextDueDate) : '',
            status,
            statusText,
            isOverdue
          }
        })

        // 按接种日期倒序排序
        records.sort((a, b) => new Date(b.vaccinationDate) - new Date(a.vaccinationDate))

        this.setData({ records })
      }
    } catch (error) {
      console.error('加载疫苗记录失败:', error)
      Common.toast('加载失败')
    }
  },

  /**
   * 添加记录
   */
  addRecord() {
    wx.navigateTo({
      url: `/pages/vaccine-add/vaccine-add?petId=${this.data.petId}`
    })
  },

  /**
   * 编辑记录
   */
  editRecord(e) {
    const recordId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/vaccine-add/vaccine-add?petId=${this.data.petId}&recordId=${recordId}`
    })
  },

  /**
   * 删除记录
   */
  async deleteRecord(e) {
    const recordId = e.currentTarget.dataset.id
    const confirmed = await Common.confirm('确定要删除这条疫苗记录吗？', '删除确认')

    if (confirmed) {
      try {
        await api.vaccine.deleteRecord(recordId)
        Common.toast('删除成功', 'success')
        await this.loadRecords()
      } catch (error) {
        console.error('删除失败:', error)
        Common.toast('删除失败')
      }
    }
  }
})
