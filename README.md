# WebRTC 实时音视频系统

基于 WebRTC 技术的实时音视频通话系统，支持多人视频会议。

## How to Run

### 方式一：Docker Compose（推荐）

```bash
# 1. 确保已安装 Docker 和 Docker Compose
# 2. 在项目根目录执行
docker-compose up --build -d

# 3. 访问应用
# 前端: http://localhost:8081
# 后端API: http://localhost:8080
```

### 方式二：手动启动

#### 1. 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE webrtc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 执行建表脚本
mysql -u root -p webrtc_db < webrtc-service/src/main/resources/schema.sql
```

#### 2. 启动后端

```bash
cd webrtc-service
# 修改 application.yml 中的数据库配置
mvn spring-boot:run
```

#### 3. 启动前端

```bash
cd webrtc
npm install
npm run dev
# 访问 http://localhost:5173
```

## Services

| 服务                  | 端口 | 说明                 |
| --------------------- | ---- | -------------------- |
| webrtc (前端-Docker)  | 8081 | Vue3 前端应用        |
| webrtc (前端-开发)    | 5173 | Vite 开发服务器      |
| webrtc-service (后端) | 8080 | Spring Boot 后端服务 |
| MySQL                 | 3306 | 数据库服务           |

## 测试步骤

本系统无需登录，直接输入昵称即可使用。

1. 打开浏览器访问应用
   - Docker 部署: http://localhost:8081
   - 手动启动: http://localhost:5173
2. 输入昵称，创建或加入房间
3. 允许浏览器访问摄像头和麦克风
4. 开始视频通话

## 运行验证

1. 在两个浏览器窗口（或两台设备）打开应用
2. 分别输入不同昵称
3. 加入同一房间
4. 双方应能看到对方视频并听到声音

## 功能特性

- ✅ 实时音视频通话
- ✅ 多人视频会议
- ✅ 音视频开关控制
- ✅ 房间管理
- ✅ 用户列表实时同步

## 浏览器要求

- Chrome 72+ / Edge 79+ / Firefox 68+ / Safari 14.1+（推荐使用最新版本）
- 需要允许浏览器访问摄像头和麦克风权限
- **重要**: 非 localhost 访问时，必须使用 HTTPS，否则无法获取摄像头权限

## ICE 服务器配置

项目默认使用 Google 公共 STUN 服务器（见 `webrtc/src/config.js`），适用于局域网或大部分可直连的网络环境。

**公网部署时**，建议在 `webrtc/src/config.js` 中补充 TURN 服务器配置，作为 STUN 穿透失败后的 fallback：

```js
export const ICE_SERVERS = [
  { urls: "stun:stun.l.google.com:19302" },
  {
    urls: "turn:your-turn-server.com:3478",
    username: "your-username",
    credential: "your-credential",
  },
];
```

常用的开源 TURN 服务器方案：[coturn](https://github.com/coturn/coturn)。

## 技术架构

- **前端**: Vue 3 + Vite + Ant Design Vue + Pinia
- **后端**: Spring Boot 3 + MyBatis-Plus + MySQL 8
- **实时通信**: WebSocket + WebRTC

## 题目内容

使用webrtc技术，开发一个项目，实现实时语音视频，前端基于vue3开发，后端使用springboot，前端项目名叫webrtc，后端项目名叫webrtc-service
