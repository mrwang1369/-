// api/index.js - API接口统一导出

/**
 * API接口统一管理
 * 按模块组织所有API请求方法
 */
const request = require('../utils/request.js')

// ==================== 认证模块 ====================
const authAPI = {
  // 用户登录
  login(data) {
    return request.post('/auth/login', data, false)
  },

  // 用户注册
  register(data) {
    return request.post('/auth/register', data, false)
  },

  // 微信登录
  wxLogin(data) {
    return request.post('/auth/wxlogin', data, false)
  },

  // 获取用户信息
  getProfile() {
    return request.get('/auth/profile')
  },

  // 用户登出
  logout() {
    return request.post('/auth/logout')
  },

  // 刷新Token
  refreshToken() {
    return request.post('/auth/refresh')
  }
}

// ==================== 宠物档案模块 ====================
const petAPI = {
  // 获取宠物列表
  getPets(params) {
    return request.get('/pets', params)
  },

  // 获取宠物详情
  getPetDetail(petId) {
    return request.get(`/pets/${petId}`)
  },

  // 创建宠物档案
  createPet(data) {
    return request.post('/pets', data)
  },

  // 更新宠物信息
  updatePet(petId, data) {
    return request.put(`/pets/${petId}`, data)
  },

  // 删除宠物档案
  deletePet(petId) {
    return request.delete(`/pets/${petId}`)
  },

  // 上传宠物头像
  uploadPetAvatar(petId, filePath) {
    return request.uploadFile(`/pets/${petId}/avatar`, filePath)
  }
}

// ==================== 疫苗记录模块 ====================
const vaccineAPI = {
  // 创建疫苗记录
  createRecord(data) {
    return request.post('/vaccination-records', data)
  },

  // 更新疫苗记录
  updateRecord(recordId, data) {
    return request.put(`/vaccination-records/${recordId}`, data)
  },

  // 删除疫苗记录
  deleteRecord(recordId) {
    return request.delete(`/vaccination-records/${recordId}`)
  },

  // 获取宠物疫苗记录列表
  getPetRecords(petId) {
    return request.get(`/vaccination-records/pet/${petId}`)
  },

  // 获取即将到期疫苗
  getUpcomingRecords() {
    return request.get('/vaccination-records/upcoming')
  },

  // 获取过期疫苗
  getExpiredRecords() {
    return request.get('/vaccination-records/expired')
  }
}

// ==================== 驱虫记录模块 ====================
const dewormingAPI = {
  // 创建驱虫记录
  createRecord(data) {
    return request.post('/deworming-records', data)
  },

  // 更新驱虫记录
  updateRecord(recordId, data) {
    return request.put(`/deworming-records/${recordId}`, data)
  },

  // 删除驱虫记录
  deleteRecord(recordId) {
    return request.delete(`/deworming-records/${recordId}`)
  },

  // 获取宠物驱虫记录列表
  getPetRecords(petId) {
    return request.get(`/deworming-records/pet/${petId}`)
  },

  // 获取即将驱虫提醒
  getUpcomingRecords() {
    return request.get('/deworming-records/upcoming')
  },

  // 获取过期未驱虫记录
  getOverdueRecords() {
    return request.get('/deworming-records/overdue')
  }
}

// ==================== 体检记录模块 ====================
const checkupAPI = {
  // 创建体检记录
  createRecord(data) {
    return request.post('/checkup-records', data)
  },

  // 更新体检记录
  updateRecord(recordId, data) {
    return request.put(`/checkup-records/${recordId}`, data)
  },

  // 删除体检记录
  deleteRecord(recordId) {
    return request.delete(`/checkup-records/${recordId}`)
  },

  // 获取宠物体检记录列表
  getPetRecords(petId) {
    return request.get(`/checkup-records/pet/${petId}`)
  },

  // 获取近期体检记录
  getRecentRecords() {
    return request.get('/checkup-records/recent')
  },

  // 获取过期未体检记录
  getOverdueRecords() {
    return request.get('/checkup-records/overdue')
  }
}

// ==================== 病历记录模块 ====================
const medicalAPI = {
  // 创建病历记录
  createRecord(data) {
    return request.post('/medical-records', data)
  },

  // 更新病历记录
  updateRecord(recordId, data) {
    return request.put(`/medical-records/${recordId}`, data)
  },

  // 删除病历记录
  deleteRecord(recordId) {
    return request.delete(`/medical-records/${recordId}`)
  },

  // 获取宠物病历记录列表
  getPetRecords(petId) {
    return request.get(`/medical-records/pet/${petId}`)
  },

  // 获取近期病历记录
  getRecentRecords() {
    return request.get('/medical-records/recent')
  },

  // 搜索病历记录
  searchRecords(keyword) {
    return request.get('/medical-records/search', { keyword })
  },

  // 获取用药提醒
  getMedicationReminders() {
    return request.get('/medical-records/medication-reminders')
  }
}

// ==================== 提醒管理模块 ====================
const reminderAPI = {
  // 创建提醒
  createReminder(data) {
    return request.post('/reminder', data)
  },

  // 更新提醒
  updateReminder(reminderId, data) {
    return request.put(`/reminder/${reminderId}`, data)
  },

  // 删除提醒
  deleteReminder(reminderId) {
    return request.delete(`/reminder/${reminderId}`)
  },

  // 完成提醒
  completeReminder(reminderId) {
    return request.patch(`/reminder/${reminderId}/complete`)
  },

  // 获取提醒详情
  getReminderDetail(reminderId) {
    return request.get(`/reminder/${reminderId}`)
  },

  // 获取提醒列表
  getReminders(params) {
    return request.get('/reminder', params)
  },

  // 获取即将到期提醒
  getUpcomingReminders() {
    return request.get('/reminder/upcoming')
  },

  // 获取逾期提醒
  getOverdueReminders() {
    return request.get('/reminder/overdue')
  },

  // 获取今日提醒
  getTodayReminders() {
    return request.get('/reminder/today')
  },

  // 生成健康提醒
  generateHealthReminders() {
    return request.post('/reminder/generate-health-reminders')
  }
}

// ==================== 文件上传模块 ====================
const fileAPI = {
  // 通用文件上传
  uploadFile(filePath, data) {
    return request.uploadFile('/files/upload', filePath, data)
  },

  // 获取文件信息
  getFileInfo(fileId) {
    return request.get(`/files/${fileId}`)
  },

  // 获取业务相关文件列表
  getBusinessFiles(moduleType, businessId) {
    return request.get(`/files/list/business/${moduleType}/${businessId}`)
  },

  // 获取用户文件列表
  getUserFiles(params) {
    return request.get('/files/list/user', params)
  },

  // 删除文件
  deleteFile(fileId) {
    return request.delete(`/files/${fileId}`)
  },

  // 批量删除文件
  batchDeleteFiles(fileIds) {
    return request.delete('/files/batch', { fileIds })
  },

  // 文件下载
  downloadFile(moduleType, fileName) {
    return request.get(`/files/download/${moduleType}/${fileName}`)
  }
}

// ==================== 周边服务模块 ====================
const serviceAPI = {
  // 获取附近服务点
  getNearbyServicePoints(params) {
    return request.get('/service-points/nearby', params)
  },

  // 搜索服务点
  searchServicePoints(params) {
    return request.get('/service-points/search', params)
  },

  // 地理编码
  geocode(address) {
    return request.post('/service-points/geocode', { address })
  },

  // 逆地理编码
  regeocode(longitude, latitude) {
    return request.post('/service-points/regeocode', {
      longitude,
      latitude
    })
  },

  // 计算距离
  calculateDistance(point1, point2) {
    return request.post('/service-points/distance', {
      point1,
      point2
    })
  }
}

// ==================== 系统接口 ====================
const systemAPI = {
  // 健康检查
  healthCheck() {
    return request.get('/health', {}, false)
  },

  // 获取宠物品种列表
  getBreeds() {
    return request.get('/breeds', {}, false)
  },

  // 获取系统配置
  getConfig() {
    return request.get('/config', {}, false)
  }
}

// 统一导出
module.exports = {
  auth: authAPI,
  pet: petAPI,
  vaccine: vaccineAPI,
  deworming: dewormingAPI,
  checkup: checkupAPI,
  medical: medicalAPI,
  reminder: reminderAPI,
  file: fileAPI,
  service: serviceAPI,
  system: systemAPI
}
