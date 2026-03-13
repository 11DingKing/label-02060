# WebRTC 项目

这是一个基于 WebRTC 的实时视频通话项目，包含前端和后端两个部分。

## 技术栈

- 前端：Vue 3 + Vite + Ant Design Vue
- 后端：Spring Boot 3 + WebSocket + MyBatis Plus
- 数据库：MySQL
- 部署：Docker

## 快速启动

### 前提条件

- 安装 Docker
- 安装 Docker Compose

### 启动步骤

1. 克隆项目到本地

2. 进入项目根目录

3. 运行以下命令启动项目：

```bash
docker-compose up -d
```

4. 访问以下地址：
   - 前端：http://localhost
   - 后端 API：http://localhost:8080/api

### 停止项目

```bash
docker-compose down
```

## 项目结构

```
├── webrtc/              # 前端项目
│   ├── public/          # 静态资源
│   ├── src/             # 源代码
│   │   ├── api/         # API 调用
│   │   ├── router/      # 路由
│   │   ├── stores/      # 状态管理
│   │   ├── styles/      # 样式
│   │   ├── utils/       # 工具函数
│   │   ├── views/       # 页面组件
│   │   ├── App.vue      # 根组件
│   │   ├── config.js    # 配置文件
│   │   └── main.js      # 入口文件
│   ├── index.html       # HTML 模板
│   ├── nginx.conf       # Nginx 配置
│   ├── package.json     # 依赖配置
│   └── vite.config.js   # Vite 配置
├── webrtc-service/      # 后端项目
│   ├── src/             # 源代码
│   │   └── main/        # 主代码
│   │       ├── java/    # Java 代码
│   │       └── resources/ # 资源文件
│   └── pom.xml          # Maven 配置
├── docker-compose.yml   # Docker Compose 配置
└── README.md            # 项目说明
```

## 功能特性

- 创建和管理视频通话房间
- 实时视频通话
- 音频和视频控制
- 多用户同时通话

## 注意事项

- 本项目使用了 Google 公共 STUN 服务器，适用于局域网或大部分可直连的网络环境。
- 公网部署时，建议补充 TURN 服务器作为 STUN 穿透失败后的 fallback。
- 默认数据库配置为：
  - 数据库名：webrtc_db
  - 用户名：root
  - 密码：root123456

## 故障排查

如果遇到启动问题，可以尝试以下步骤：

1. 检查 Docker 是否正常运行
2. 检查端口是否被占用
3. 查看容器日志：

```bash
docker logs webrtc-frontend
docker logs webrtc-service
docker logs mysql-webrtc
```

4. 重新构建镜像：

```bash
docker-compose up -d --build
```