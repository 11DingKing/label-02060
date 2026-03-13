<template>
  <div class="room">
    <header class="room-header">
      <div class="header-left">
        <h1>{{ roomInfo?.roomName || '视频会议' }}</h1>
        <span class="room-id" @click="copyRoomId">ID: {{ roomId }}</span>
      </div>
      <div class="waiting-hint" v-if="webrtcStore.users.length <= 1">
        等待其他参与者加入...
      </div>
      <a-button type="primary" danger @click="leaveRoom">离开会议</a-button>
    </header>
    
    <main class="room-main">
      <div class="video-grid" :class="'grid-' + Math.min(webrtcStore.users.length, 4)">
        <!-- 本地视频 -->
        <div class="video-tile local">
          <video ref="localVideoRef" autoplay muted playsinline></video>
          <div class="video-placeholder" v-show="!webrtcStore.videoEnabled">
            <div class="avatar">{{ userStore.userName?.charAt(0)?.toUpperCase() }}</div>
          </div>
          <div class="video-name">{{ userStore.userName }} (我)</div>
        </div>
        
        <!-- 远程用户 -->
        <div 
          v-for="user in remoteUsers" 
          :key="user.userId" 
          class="video-tile"
        >
          <video 
            :ref="el => bindRemoteVideo(el, user.userId)"
            autoplay 
            playsinline
            @click="handleVideoClick"
          ></video>
          <div class="video-placeholder" v-show="!hasRemoteVideo(user.userId)">
            <div class="avatar">{{ user.userName?.charAt(0)?.toUpperCase() }}</div>
          </div>
          <div class="video-name">{{ user.userName }}</div>
        </div>
      </div>
    </main>
    
    <footer class="control-bar">
      <div class="control-btn" :class="{ off: !webrtcStore.audioEnabled }" @click="toggleAudio">
        <AudioOutlined v-if="webrtcStore.audioEnabled" />
        <AudioMutedOutlined v-else />
      </div>
      <div class="control-btn" :class="{ off: !webrtcStore.videoEnabled }" @click="toggleVideo">
        <VideoCameraOutlined />
      </div>
      <div class="control-btn danger" @click="leaveRoom">
        <PhoneOutlined style="transform: rotate(225deg)" />
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { VideoCameraOutlined, AudioOutlined, AudioMutedOutlined, PhoneOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '../stores/user'
import { useWebRTCStore } from '../stores/webrtc'
import { getRoomInfo } from '../api/room'
import { WebRTCManager } from '../utils/webrtc'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const webrtcStore = useWebRTCStore()

const roomId = route.params.roomId
const roomInfo = ref(null)
const localVideoRef = ref(null)
const videoRefs = new Map()
let webrtcManager = null

const remoteUsers = computed(() => 
  webrtcStore.users.filter(u => u.userId !== userStore.userId)
)

const hasRemoteVideo = (userId) => {
  const stream = webrtcStore.remoteStreams.get(userId)
  return stream && stream.getVideoTracks().length > 0
}

const bindRemoteVideo = (el, userId) => {
  if (!el) return
  videoRefs.set(userId, el)
  const stream = webrtcStore.remoteStreams.get(userId)
  if (stream && el.srcObject !== stream) {
    el.srcObject = stream
    el.play().catch(() => {
      el.muted = true
      el.play().catch(() => {})
    })
  }
}

watch(() => webrtcStore.remoteStreams, () => {
  nextTick(() => {
    for (const [userId, stream] of webrtcStore.remoteStreams) {
      const el = videoRefs.get(userId)
      if (el && el.srcObject !== stream) {
        el.srcObject = stream
        el.play().catch(() => {})
      }
    }
  })
}, { deep: true })

const copyRoomId = () => {
  navigator.clipboard.writeText(roomId)
  message.success('会议 ID 已复制')
}

const initRoom = async () => {
  if (!userStore.userName) {
    message.warning('请先设置昵称')
    router.push('/')
    return
  }
  
  try {
    const res = await getRoomInfo(roomId)
    if (res.code !== 200) {
      message.error(res.message || '房间不存在')
      router.push('/')
      return
    }
    roomInfo.value = res.data
    
    webrtcManager = new WebRTCManager({
      roomId,
      userId: userStore.userId,
      userName: userStore.userName,
      onRemoteStream: (userId, stream) => {
        webrtcStore.addRemoteStream(userId, stream)
      },
      onRemoteStreamRemoved: (userId) => {
        webrtcStore.removeRemoteStream(userId)
        videoRefs.delete(userId)
      },
      onUserList: (users) => {
        webrtcStore.setUsers(users)
      },
      onError: (err) => message.warning(err)
    })
    
    const localStream = await webrtcManager.init()
    webrtcStore.setLocalStream(localStream)
    webrtcStore.videoEnabled = localStream?.getVideoTracks().length > 0
    webrtcStore.audioEnabled = localStream?.getAudioTracks().length > 0
    
    await nextTick()
    if (localVideoRef.value && localStream) {
      localVideoRef.value.srcObject = localStream
    }
  } catch (e) {
    message.error('加入会议失败')
  }
}

const toggleVideo = () => {
  webrtcStore.videoEnabled = !webrtcStore.videoEnabled
  webrtcManager?.toggleVideo(webrtcStore.videoEnabled)
}

const toggleAudio = () => {
  webrtcStore.audioEnabled = !webrtcStore.audioEnabled
  webrtcManager?.toggleAudio(webrtcStore.audioEnabled)
}

const leaveRoom = () => {
  webrtcManager?.leave()
  webrtcStore.cleanup()
  router.push('/')
}

const handleVideoClick = (e) => {
  const video = e.target
  if (video.paused) {
    video.play().catch(() => {})
  }
}

onMounted(initRoom)
onUnmounted(() => {
  webrtcManager?.leave()
  webrtcStore.cleanup()
})
</script>

<style lang="scss" scoped>
.room {
  height: 100vh;
  background: #1a1a2e;
  display: flex;
  flex-direction: column;
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #16213e;
  .header-left {
    h1 { color: white; font-size: 18px; margin: 0; }
    .room-id { color: #888; font-size: 12px; cursor: pointer; }
  }
  .waiting-hint { color: #fbbf24; font-size: 14px; }
}

.room-main {
  flex: 1;
  padding: 16px;
  overflow: hidden;
}

.video-grid {
  width: 100%;
  height: 100%;
  display: grid;
  gap: 16px;
  &.grid-1 { grid-template-columns: 1fr; max-width: 800px; margin: 0 auto; }
  &.grid-2 { grid-template-columns: repeat(2, 1fr); }
  &.grid-3, &.grid-4 { grid-template-columns: repeat(2, 1fr); grid-template-rows: repeat(2, 1fr); }
}

.video-tile {
  position: relative;
  background: #0f0f23;
  border-radius: 12px;
  overflow: hidden;
  min-height: 200px;
  &.local video { transform: scaleX(-1); }
  video {
    width: 100%;
    height: 100%;
    object-fit: cover;
    position: absolute;
    inset: 0;
    z-index: 1;
    background: #000;
  }
  .video-placeholder {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #0f0f23;
    z-index: 2;
    .avatar {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      background: #6366f1;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 32px;
      font-weight: bold;
    }
  }
  .video-name {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 8px 12px;
    background: linear-gradient(transparent, rgba(0,0,0,0.8));
    color: white;
    font-size: 14px;
    z-index: 3;
  }
}

.control-bar {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 16px;
  background: #16213e;
  .control-btn {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: #333;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 20px;
    cursor: pointer;
    &:hover { background: #444; }
    &.off { background: #ef4444; }
    &.danger { background: #ef4444; }
  }
}
</style>
