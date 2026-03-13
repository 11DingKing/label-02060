<template>
  <div class="home">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
      <div class="grid-pattern"></div>
    </div>

    <div class="home-container">
      <!-- 顶部导航 -->
      <nav class="nav-bar">
        <div class="logo">
          <div class="logo-icon">
            <VideoCameraOutlined />
          </div>
          <span class="logo-text">WebRTC Meet</span>
        </div>
        <div class="nav-user" v-if="userStore.userName">
          <a-avatar :style="{ background: 'var(--primary-gradient)', cursor: 'pointer' }" @click="openNicknameModal">
            {{ userStore.userName.charAt(0).toUpperCase() }}
          </a-avatar>
          <span style="cursor: pointer" @click="openNicknameModal">{{ userStore.userName }}</span>
        </div>
        <a-button v-else type="primary" @click="openNicknameModal">设置昵称</a-button>
      </nav>

      <!-- 主要内容区 -->
      <main class="main-content">
        <div class="hero-section">
          <div class="hero-text">
            <h1 class="hero-title">
              <span class="gradient-text">高清视频会议</span>
              <br />随时随地连接
            </h1>
            <p class="hero-desc">
              基于 WebRTC 技术的实时音视频通信平台，支持多人视频会议，
              让远程协作更加高效便捷。
            </p>
            
            <!-- 快速操作区 -->
            <div class="quick-actions">
              <div class="action-card create" @click="handleQuickCreate">
                <div class="action-icon">
                  <PlusOutlined />
                </div>
                <div class="action-info">
                  <h3>新建会议</h3>
                  <p>立即创建一个视频会议</p>
                </div>
                <RightOutlined class="action-arrow" />
              </div>
              
              <div class="action-card join" @click="showJoinModal = true">
                <div class="action-icon">
                  <LoginOutlined />
                </div>
                <div class="action-info">
                  <h3>加入会议</h3>
                  <p>输入会议 ID 加入</p>
                </div>
                <RightOutlined class="action-arrow" />
              </div>
            </div>
          </div>

          <div class="hero-visual">
            <div class="preview-card">
              <div class="preview-header">
                <div class="preview-dots">
                  <span></span><span></span><span></span>
                </div>
                <span class="preview-title">视频预览</span>
              </div>
              <div class="preview-content">
                <div class="preview-grid">
                  <div class="preview-item main">
                    <UserOutlined />
                    <span>主持人</span>
                  </div>
                  <div class="preview-item">
                    <UserOutlined />
                    <span>参与者 1</span>
                  </div>
                  <div class="preview-item">
                    <UserOutlined />
                    <span>参与者 2</span>
                  </div>
                  <div class="preview-item">
                    <UserOutlined />
                    <span>参与者 3</span>
                  </div>
                </div>
                <div class="preview-controls">
                  <div class="ctrl-btn active"><VideoCameraOutlined /></div>
                  <div class="ctrl-btn active"><AudioOutlined /></div>
                  <div class="ctrl-btn danger"><PhoneOutlined style="transform: rotate(225deg)" /></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 房间列表 -->
        <section class="rooms-section">
          <div class="section-header">
            <h2>
              <TeamOutlined />
              进行中的会议
            </h2>
            <a-button type="text" @click="loadRooms" :loading="loading">
              <template #icon><ReloadOutlined /></template>
              刷新
            </a-button>
          </div>
          
          <div class="rooms-grid" v-if="rooms.length > 0">
            <div 
              v-for="room in rooms" 
              :key="room.roomId" 
              class="room-card"
              @click="joinRoom(room.roomId)"
            >
              <div class="room-avatar">
                <VideoCameraOutlined />
              </div>
              <div class="room-info">
                <h4>{{ room.roomName }}</h4>
                <span class="room-id">ID: {{ room.roomId }}</span>
              </div>
              <div class="room-meta">
                <div class="room-users">
                  <TeamOutlined />
                  <span>{{ room.currentUsers }}/{{ room.maxUsers }}</span>
                </div>
                <div class="room-status">
                  <span class="status-dot"></span>
                  进行中
                </div>
              </div>
              <a-button type="primary" size="small" class="join-btn">
                加入
              </a-button>
            </div>
          </div>
          
          <div class="empty-state" v-else>
            <div class="empty-icon">
              <InboxOutlined />
            </div>
            <h3>暂无进行中的会议</h3>
            <p>创建一个新会议开始视频通话</p>
            <a-button type="primary" @click="handleQuickCreate">
              <PlusOutlined /> 创建会议
            </a-button>
          </div>
        </section>

        <!-- 功能特性 -->
        <section class="features-section">
          <h2>为什么选择我们</h2>
          <div class="features-grid">
            <div class="feature-card">
              <div class="feature-icon">
                <ThunderboltOutlined />
              </div>
              <h3>超低延迟</h3>
              <p>基于 WebRTC 技术，实现毫秒级音视频传输</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <SafetyOutlined />
              </div>
              <h3>端到端加密</h3>
              <p>所有通信数据加密传输，保护您的隐私安全</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <GlobalOutlined />
              </div>
              <h3>跨平台支持</h3>
              <p>支持各种现代浏览器，无需安装任何插件</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <TeamOutlined />
              </div>
              <h3>多人会议</h3>
              <p>支持多人同时在线，轻松组织团队会议</p>
            </div>
          </div>
        </section>
      </main>

      <!-- 页脚 -->
      <footer class="footer">
        <p>© 2024 WebRTC Meet. 基于 Vue 3 + Spring Boot 构建</p>
      </footer>
    </div>
    
    <!-- 创建房间弹窗 -->
    <a-modal 
      v-model:open="showCreateModal" 
      title="创建新会议" 
      @ok="handleCreate" 
      :confirmLoading="creating"
      centered
      :width="420"
    >
      <div class="modal-content">
        <a-form layout="vertical">
          <a-form-item label="会议名称" required>
            <a-input 
              v-model:value="newRoomName" 
              placeholder="例如：产品评审会议"
              size="large"
            />
          </a-form-item>
          <a-form-item label="最大参与人数">
            <a-slider v-model:value="maxUsers" :min="2" :max="20" :marks="{2: '2人', 10: '10人', 20: '20人'}" />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
    
    <!-- 加入房间弹窗 -->
    <a-modal 
      v-model:open="showJoinModal" 
      title="加入会议" 
      @ok="handleJoin"
      centered
      :width="420"
    >
      <div class="modal-content">
        <a-form layout="vertical">
          <a-form-item label="会议 ID" required>
            <a-input 
              v-model:value="joinRoomId" 
              placeholder="输入会议 ID"
              size="large"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>

    <!-- 昵称设置弹窗 -->
    <a-modal
      v-model:open="showNicknameModal"
      title="设置昵称"
      @ok="saveUserName"
      centered
      :width="420"
      okText="确认"
      cancelText="取消"
    >
      <div class="modal-content">
        <a-form layout="vertical">
          <a-form-item label="您的昵称" required>
            <a-input
              v-model:value="userName"
              placeholder="输入您的昵称"
              size="large"
              @pressEnter="saveUserName"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  PlusOutlined, LoginOutlined, ReloadOutlined, UserOutlined, 
  VideoCameraOutlined, AudioOutlined, PhoneOutlined,
  RightOutlined, TeamOutlined, InboxOutlined, ThunderboltOutlined,
  SafetyOutlined, GlobalOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '../stores/user'
import { createRoom, getRoomList } from '../api/room'

const router = useRouter()
const userStore = useUserStore()

const userName = ref(userStore.userName)
const rooms = ref([])
const loading = ref(false)
const showCreateModal = ref(false)
const showJoinModal = ref(false)
const showNicknameModal = ref(false)
const newRoomName = ref('')
const maxUsers = ref(10)
const joinRoomId = ref('')
const creating = ref(false)

const pendingAction = ref(null)

const openNicknameModal = () => {
  userName.value = userStore.userName || ''
  showNicknameModal.value = true
}

const saveUserName = () => {
  if (!userName.value.trim()) {
    message.warning('请输入昵称')
    return
  }
  userStore.setUserName(userName.value.trim())
  showNicknameModal.value = false
  message.success('昵称设置成功')
  
  if (pendingAction.value) {
    const action = pendingAction.value
    pendingAction.value = null
    action()
  }
}

const ensureNickname = (action) => {
  if (!userStore.userName) {
    pendingAction.value = action
    openNicknameModal()
    return false
  }
  return true
}

const loadRooms = async () => {
  loading.value = true
  try {
    const res = await getRoomList()
    rooms.value = res.data || []
  } catch (e) {
    message.error('获取房间列表失败')
  } finally {
    loading.value = false
  }
}

const handleQuickCreate = () => {
  if (!ensureNickname(handleQuickCreate)) return
  showCreateModal.value = true
}

const handleCreate = async () => {
  if (!newRoomName.value.trim()) {
    message.warning('请输入会议名称')
    return
  }
  creating.value = true
  try {
    const res = await createRoom({ roomName: newRoomName.value, maxUsers: maxUsers.value })
    message.success('会议创建成功')
    showCreateModal.value = false
    router.push(`/room/${res.data.roomId}`)
  } catch (e) {
    message.error('创建会议失败')
  } finally {
    creating.value = false
  }
}

const handleJoin = () => {
  if (!ensureNickname(handleJoin)) return
  if (!joinRoomId.value.trim()) {
    message.warning('请输入会议 ID')
    return
  }
  router.push(`/room/${joinRoomId.value.trim()}`)
  showJoinModal.value = false
}

const joinRoom = (roomId) => {
  if (!ensureNickname(() => joinRoom(roomId))) return
  router.push(`/room/${roomId}`)
}

onMounted(() => {
  loadRooms()
})
</script>

<style lang="scss" scoped>
.home {
  min-height: 100vh;
  background: var(--light-bg);
  position: relative;
  overflow-x: hidden;
}

// 背景装饰
.bg-decoration {
  position: fixed;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
  z-index: 0;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  
  &.orb-1 {
    width: 600px;
    height: 600px;
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
    top: -200px;
    right: -100px;
    animation: float 20s ease-in-out infinite;
  }
  
  &.orb-2 {
    width: 400px;
    height: 400px;
    background: linear-gradient(135deg, #ec4899 0%, #f43f5e 100%);
    bottom: -100px;
    left: -100px;
    animation: float 15s ease-in-out infinite reverse;
  }
  
  &.orb-3 {
    width: 300px;
    height: 300px;
    background: linear-gradient(135deg, #06b6d4 0%, #3b82f6 100%);
    top: 50%;
    left: 30%;
    animation: float 18s ease-in-out infinite;
  }
}

.grid-pattern {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(99, 102, 241, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(99, 102, 241, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.home-container {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--space-6);
}

// 导航栏
.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-5) 0;
  
  .logo {
    display: flex;
    align-items: center;
    gap: var(--space-3);
    
    .logo-icon {
      width: 44px;
      height: 44px;
      background: var(--primary-gradient);
      border-radius: var(--radius-lg);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 20px;
      box-shadow: var(--shadow-md), 0 0 20px rgba(99, 102, 241, 0.3);
    }
    
    .logo-text {
      font-size: 22px;
      font-weight: 700;
      background: var(--primary-gradient);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }
  
  .nav-user {
    display: flex;
    align-items: center;
    gap: var(--space-3);
    padding: var(--space-2) var(--space-4);
    background: var(--light-surface);
    border-radius: var(--radius-full);
    box-shadow: var(--shadow-sm);
    
    span {
      font-weight: 500;
      color: var(--light-text);
    }
  }
}

// 主内容区
.main-content {
  padding: var(--space-8) 0;
}

// Hero 区域
.hero-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-12);
  align-items: center;
  min-height: 500px;
  margin-bottom: var(--space-12);
}

.hero-text {
  .hero-title {
    font-size: 52px;
    font-weight: 700;
    line-height: 1.2;
    color: var(--light-text);
    margin-bottom: var(--space-6);
    
    .gradient-text {
      background: var(--primary-gradient);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }
  
  .hero-desc {
    font-size: 18px;
    color: var(--light-text-secondary);
    line-height: 1.7;
    margin-bottom: var(--space-8);
  }
}

// 快速操作卡片
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.action-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
  background: var(--light-surface);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-md);
  border: 1px solid transparent;
  
  &:hover {
    transform: translateX(8px);
    box-shadow: var(--shadow-lg);
    border-color: var(--primary-color);
    
    .action-arrow {
      transform: translateX(4px);
      color: var(--primary-color);
    }
  }
  
  .action-icon {
    width: 52px;
    height: 52px;
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
  }
  
  &.create .action-icon {
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
    color: white;
  }
  
  &.join .action-icon {
    background: linear-gradient(135deg, #10b981 0%, #06b6d4 100%);
    color: white;
  }
  
  .action-info {
    flex: 1;
    
    h3 {
      font-size: 16px;
      font-weight: 600;
      color: var(--light-text);
      margin-bottom: 2px;
    }
    
    p {
      font-size: 13px;
      color: var(--light-text-secondary);
      margin: 0;
    }
  }
  
  .action-arrow {
    color: var(--light-text-secondary);
    transition: all var(--transition-base);
  }
}

// 预览卡片
.hero-visual {
  display: flex;
  justify-content: center;
}

.preview-card {
  width: 100%;
  max-width: 480px;
  background: var(--dark-surface);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-xl), 0 0 60px rgba(99, 102, 241, 0.2);
  animation: slideUp 0.8s ease-out;
  
  .preview-header {
    display: flex;
    align-items: center;
    gap: var(--space-3);
    padding: var(--space-4);
    background: var(--dark-card);
    border-bottom: 1px solid var(--dark-border);
    
    .preview-dots {
      display: flex;
      gap: 6px;
      
      span {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        
        &:nth-child(1) { background: #ef4444; }
        &:nth-child(2) { background: #f59e0b; }
        &:nth-child(3) { background: #10b981; }
      }
    }
    
    .preview-title {
      color: var(--dark-text-secondary);
      font-size: 13px;
    }
  }
  
  .preview-content {
    padding: var(--space-5);
  }
  
  .preview-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-3);
    margin-bottom: var(--space-5);
    
    .preview-item {
      aspect-ratio: 16/10;
      background: var(--dark-card);
      border-radius: var(--radius-md);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: var(--space-2);
      color: var(--dark-text-secondary);
      font-size: 12px;
      border: 1px solid var(--dark-border);
      
      &.main {
        grid-column: span 2;
        aspect-ratio: 16/7;
        background: linear-gradient(135deg, var(--dark-card) 0%, rgba(99, 102, 241, 0.1) 100%);
        color: var(--dark-text);
        font-size: 14px;
      }
      
      :deep(.anticon) {
        font-size: 24px;
      }
    }
  }
  
  .preview-controls {
    display: flex;
    justify-content: center;
    gap: var(--space-4);
    
    .ctrl-btn {
      width: 44px;
      height: 44px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--dark-card);
      color: var(--dark-text-secondary);
      font-size: 18px;
      border: 1px solid var(--dark-border);
      
      &.active {
        background: var(--primary-color);
        color: white;
        border-color: var(--primary-color);
      }
      
      &.danger {
        background: #ef4444;
        color: white;
        border-color: #ef4444;
      }
    }
  }
}

// 房间列表
.rooms-section {
  margin-bottom: var(--space-12);
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: var(--space-6);
    
    h2 {
      display: flex;
      align-items: center;
      gap: var(--space-3);
      font-size: 24px;
      font-weight: 600;
      color: var(--light-text);
    }
  }
}

.rooms-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--space-5);
}

.room-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
  background: var(--light-surface);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-md);
  border: 1px solid transparent;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
    border-color: var(--primary-color);
    
    .join-btn {
      opacity: 1;
    }
  }
  
  .room-avatar {
    width: 52px;
    height: 52px;
    background: var(--primary-gradient);
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 22px;
    flex-shrink: 0;
  }
  
  .room-info {
    flex: 1;
    min-width: 0;
    
    h4 {
      font-size: 16px;
      font-weight: 600;
      color: var(--light-text);
      margin-bottom: 2px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    .room-id {
      font-size: 12px;
      color: var(--light-text-secondary);
    }
  }
  
  .room-meta {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: var(--space-2);
    
    .room-users {
      display: flex;
      align-items: center;
      gap: var(--space-1);
      font-size: 13px;
      color: var(--light-text-secondary);
    }
    
    .room-status {
      display: flex;
      align-items: center;
      gap: var(--space-1);
      font-size: 12px;
      color: var(--success-color);
      
      .status-dot {
        width: 8px;
        height: 8px;
        background: var(--success-color);
        border-radius: 50%;
        animation: pulse 2s infinite;
      }
    }
  }
  
  .join-btn {
    opacity: 0;
    transition: opacity var(--transition-base);
  }
}

// 空状态
.empty-state {
  text-align: center;
  padding: var(--space-12);
  background: var(--light-surface);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  
  .empty-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto var(--space-5);
    background: var(--light-bg);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    color: var(--light-text-secondary);
  }
  
  h3 {
    font-size: 18px;
    font-weight: 600;
    color: var(--light-text);
    margin-bottom: var(--space-2);
  }
  
  p {
    color: var(--light-text-secondary);
    margin-bottom: var(--space-6);
  }
}

// 功能特性
.features-section {
  margin-bottom: var(--space-12);
  
  h2 {
    text-align: center;
    font-size: 28px;
    font-weight: 600;
    color: var(--light-text);
    margin-bottom: var(--space-8);
  }
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-6);
}

.feature-card {
  padding: var(--space-6);
  background: var(--light-surface);
  border-radius: var(--radius-lg);
  text-align: center;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-md);
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
  }
  
  .feature-icon {
    width: 60px;
    height: 60px;
    margin: 0 auto var(--space-4);
    background: var(--primary-light);
    border-radius: var(--radius-lg);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 26px;
    color: var(--primary-color);
  }
  
  h3 {
    font-size: 16px;
    font-weight: 600;
    color: var(--light-text);
    margin-bottom: var(--space-2);
  }
  
  p {
    font-size: 14px;
    color: var(--light-text-secondary);
    line-height: 1.6;
    margin: 0;
  }
}

// 页脚
.footer {
  text-align: center;
  padding: var(--space-8) 0;
  border-top: 1px solid var(--light-border);
  
  p {
    color: var(--light-text-secondary);
    font-size: 14px;
    margin: 0;
  }
}

// 弹窗内容
.modal-content {
  padding: var(--space-4) 0;
}

// 响应式
@media (max-width: 1024px) {
  .hero-section {
    grid-template-columns: 1fr;
    gap: var(--space-8);
  }
  
  .hero-text .hero-title {
    font-size: 40px;
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-text .hero-title {
    font-size: 32px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .rooms-grid {
    grid-template-columns: 1fr;
  }
  
  .setup-card .setup-input {
    flex-direction: column;
  }
}
</style>
