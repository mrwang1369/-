// pages/pet-add/pet-add.js
const app = getApp()
const api = require('../../api/index.js')
const Common = require('../../utils/common.js')

Page({
  data: {
    isEdit: false,
    petId: null,
    currentDate: '',
    loading: false,
    speciesOptions: ['狗', '猫', '鸟', '兔', '仓鼠', '其他'],
    formData: {
      name: '',
      species: '',
      breed: '',
      birthDate: '',
      gender: '',
      weight: '',
      neuteredStatus: false,
      allergyHistory: '',
      avatarUrl: ''
    }
  },

  onLoad(options) {
    // 设置当前日期
    this.setData({
      currentDate: this.formatDate(new Date())
    })

    // 判断是编辑还是新增
    if (options.id) {
      this.setData({
        isEdit: true,
        petId: options.id
      })
      this.loadPetDetail()
    }
  },

  /**
   * 格式化日期
   */
  formatDate(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  },

  /**
   * 加载宠物详情（编辑模式）
   */
  async loadPetDetail() {
    try {
      Common.showLoading('加载中...')

      const result = await api.pet.getPetDetail(this.data.petId)

      Common.hideLoading()

      if (result) {
        this.setData({
          formData: {
            name: result.name || '',
            species: result.species || '',
            breed: result.breed || '',
            birthDate: result.birthDate || '',
            gender: result.gender || '',
            weight: result.weight || '',
            neuteredStatus: result.neuteredStatus || false,
            allergyHistory: result.allergyHistory || '',
            avatarUrl: result.avatarUrl || ''
          }
        })
      }
    } catch (error) {
      Common.hideLoading()
      console.error('加载宠物详情失败:', error)
      Common.toast('加载失败')
      wx.navigateBack()
    }
  },

  /**
   * 选择头像
   */
  async chooseAvatar() {
    try {
      const res = await Common.chooseImage(1)

      if (res.tempFilePaths && res.tempFilePaths.length > 0) {
        this.setData({
          'formData.avatarUrl': res.tempFilePaths[0]
        })
      }
    } catch (error) {
      console.error('选择图片失败:', error)
    }
  },

  /**
   * 姓名输入
   */
  onNameInput(e) {
    this.setData({
      'formData.name': e.detail.value
    })
  },

  /**
   * 物种选择
   */
  onSpeciesChange(e) {
    const index = e.detail.value
    this.setData({
      'formData.species': this.data.speciesOptions[index]
    })
  },

  /**
   * 品种输入
   */
  onBreedInput(e) {
    this.setData({
      'formData.breed': e.detail.value
    })
  },

  /**
   * 出生日期选择
   */
  onBirthDateChange(e) {
    this.setData({
      'formData.birthDate': e.detail.value
    })
  },

  /**
   * 性别选择
   */
  selectGender(e) {
    const gender = e.currentTarget.dataset.gender
    this.setData({
      'formData.gender': gender
    })
  },

  /**
   * 体重输入
   */
  onWeightInput(e) {
    this.setData({
      'formData.weight': e.detail.value
    })
  },

  /**
   * 绝育状态切换
   */
  onNeuteredChange(e) {
    this.setData({
      'formData.neuteredStatus': e.detail.value
    })
  },

  /**
   * 过敏史输入
   */
  onAllergyInput(e) {
    this.setData({
      'formData.allergyHistory': e.detail.value
    })
  },

  /**
   * 提交表单
   */
  async handleSubmit() {
    const { formData, isEdit, petId } = this.data

    // 表单验证
    if (!formData.name) {
      Common.toast('请输入宠物姓名')
      return
    }

    if (!formData.species) {
      Common.toast('请选择物种')
      return
    }

    if (!formData.breed) {
      Common.toast('请输入品种')
      return
    }

    if (!formData.birthDate) {
      Common.toast('请选择出生日期')
      return
    }

    if (!formData.gender) {
      Common.toast('请选择性别')
      return
    }

    // 显示加载状态
    this.setData({ loading: true })

    try {
      // 如果有头像,先上传
      let avatarUrl = formData.avatarUrl
      if (avatarUrl && !avatarUrl.startsWith('http')) {
        Common.showLoading('上传头像中...')
        const uploadResult = await api.file.uploadFile(avatarUrl, {
          moduleType: 'pet_avatar',
          businessId: petId
        })
        avatarUrl = uploadResult.fileUrl
      }

      // 构建提交数据
      const submitData = {
        ...formData,
        avatarUrl
      }

      // 调用API
      if (isEdit) {
        await api.pet.updatePet(petId, submitData)
        Common.toast('修改成功', 'success')
      } else {
        await api.pet.createPet(submitData)
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
