// pages/map/map.js
const app = getApp()
const api = require('../../api/index.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    keyword: '',
    currentFilter: 'all',
    filterOptions: [
      { label: '全部', value: 'all', icon: '/images/icons/all.png' },
      { label: '宠物医院', value: 'hospital', icon: '/images/icons/hospital.png' },
      { label: '宠物店', value: 'shop', icon: '/images/icons/pet-shop.png' },
      { label: '美容店', value: 'grooming', icon: '/images/icons/grooming.png' },
      { label: '宠物店', value: 'clinic', icon: '/images/icons/clinic.png' }
    ],
    servicePoints: [],
    userLocation: null,
    loading: false
  },

  onLoad() {
    // 获取用户位置
    this.getUserLocation()
  },

  onShow() {
    // 页面显示时刷新
    this.getUserLocation()
  },

  /**
   * 获取用户位置
   */
  getUserLocation() {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        const { latitude, longitude } = res
        this.setData({
          userLocation: { latitude, longitude }
        })

        // 加载附近服务点
        this.loadNearbyServicePoints(latitude, longitude)
      },
      fail: (err) => {
        console.error('获取位置失败:', err)
        Common.toast('获取位置失败，请检查权限设置')
      }
    })
  },

  /**
   * 加载附近服务点
   */
  async loadNearbyServicePoints(latitude, longitude) {
    this.setData({ loading: true })

    try {
      const { currentFilter, keyword } = this.data

      const params = {
        longitude,
        latitude,
        radius: 3000,
        types: currentFilter === 'all' ? undefined : currentFilter
      }

      if (keyword) {
        params.keywords = keyword
      }

      const result = await api.service.getNearbyServicePoints(params)

      if (result && Array.isArray(result)) {
        // 处理数据
        const servicePoints = result.map(item => {
          const distance = this.calculateDistance(latitude, longitude, item.latitude, item.longitude)

          return {
            ...item,
            distance: this.formatDistance(distance)
          }
        })

        // 按距离排序
        servicePoints.sort((a, b) => parseFloat(a.distance) - parseFloat(b.distance))

        this.setData({ servicePoints })
      }
    } catch (error) {
      console.error('加载服务点失败:', error)
      Common.toast('加载失败')
    } finally {
      this.setData({ loading: false })
    }
  },

  /**
   * 计算距离
   */
  calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371 // 地球半径（公里）
    const dLat = this.toRad(lat2 - lat1)
    const dLon = this.toRad(lon2 - lon1)
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(this.toRad(lat1)) * Math.cos(this.toRad(lat2)) *
      Math.sin(dLon / 2) * Math.sin(dLon / 2)
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    const d = R * c
    return d * 1000 // 转换为米
  },

  /**
   * 转换为弧度
   */
  toRad(deg) {
    return deg * (Math.PI / 180)
  },

  /**
   * 格式化距离
   */
  formatDistance(meters) {
    if (meters < 1000) {
      return `${Math.round(meters)}m`
    } else {
      return `${(meters / 1000).toFixed(1)}km`
    }
  },

  /**
   * 选择筛选器
   */
  selectFilter(e) {
    const value = e.currentTarget.dataset.value
    this.setData({ currentFilter: value })

    // 重新加载服务点
    if (this.data.userLocation) {
      this.loadNearbyServicePoints(
        this.data.userLocation.latitude,
        this.data.userLocation.longitude
      )
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
    if (this.data.userLocation) {
      this.loadNearbyServicePoints(
        this.data.userLocation.latitude,
        this.data.userLocation.longitude
      )
    }
  },

  /**
   * 显示详情
   */
  showDetail(e) {
    const item = e.currentTarget.dataset.item
    const detail = `
${item.name}
类型：${item.type}
地址：${item.address}
电话：${item.phone || '暂无'}
距离：${item.distance}
评分：${item.rating || '暂无'}
营业时间：${item.businessHours || '暂无'}
    `.trim()

    wx.showModal({
      title: '服务点详情',
      content: detail,
      showCancel: false
    })
  },

  /**
   * 拨打电话
   */
  callPhone(e) {
    const phone = e.currentTarget.dataset.phone
    wx.makePhoneCall({
      phoneNumber: phone
    })
  },

  /**
   * 导航
   */
  navigate(e) {
    const item = e.currentTarget.dataset.item
    wx.openLocation({
      latitude: item.latitude,
      longitude: item.longitude,
      name: item.name,
      address: item.address,
      scale: 15
    })
  },

  /**
   * 打开地图
   */
  openMap() {
    if (!this.data.userLocation) {
      Common.toast('请先授权位置信息')
      this.getUserLocation()
      return
    }

    wx.openLocation({
      latitude: this.data.userLocation.latitude,
      longitude: this.data.userLocation.longitude,
      name: '我的位置',
      address: '当前位置',
      scale: 15
    })
  },

  /**
   * 重新定位
   */
  refreshLocation() {
    this.getUserLocation()
  }
})
