/**
 * WebRTC 配置
 *
 * 默认仅配置了 Google 公共 STUN 服务器，适用于局域网或大部分可直连的网络环境。
 * 公网部署时，建议补充 TURN 服务器作为 STUN 穿透失败后的 fallback，例如：
 *
 * {
 *   urls: "turn:your-turn-server.com:3478",
 *   username: "your-username",
 *   credential: "your-credential",
 * }
 */
export const ICE_SERVERS = [
  { urls: "stun:stun.l.google.com:19302" },
  { urls: "stun:stun1.l.google.com:19302" },
];
