// pages/pets/pets.js
const app = getApp()
const api = require('../../api/index.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    pets: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    hasMore: true
  },

  onLoad() {
    // 检查登录状态
    if (!app.isLogin()) {
      wx.reLaunch({
        url: '/pages/login/login'
      })
      return
    }

    // 加载宠物列表
    this.loadPets()
  },

  onPullDownRefresh() {
    // 下拉刷新
    this.setData({
      pageNum: 1,
      pets: [],
      hasMore: true
    })
    this.loadPets().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom() {
    // 上拉加载更多
    if (this.data.hasMore) {
      this.setData({ pageNum: this.data.pageNum + 1 })
      this.loadPets()
    }
  },

  /**
   * 加载宠物列表
   */
  async loadPets() {
    const { pageNum, pageSize } = this.data

    try {
      const result = await api.pet.getPets({
        pageNum,
        pageSize
      })

      if (result) {
        const newPets = result.pets || []
        const pets = pageNum === 1 ? newPets : [...this.data.pets, ...newPets]
        const total = result.total || 0
        const hasMore = pets.length < total

        this.setData({
          pets,
          total,
          hasMore
        })
      }
    } catch (error) {
      console.error('加载宠物列表失败:', error)
      Common.toast('加载失败')
    }
  },

  /**
   * 导航到详情页
   */
  navigateToDetail(e) {
    const petId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/pet-detail/pet-detail?id=${petId}`
    })
  },

  /**
   * 导航到添加页面
   */
  navigateToAdd() {
    wx.navigateTo({
      url: '/pages/pet-add/pet-add'
    })
  }
})
