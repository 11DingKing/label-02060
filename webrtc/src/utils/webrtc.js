import { ICE_SERVERS } from "../config.js";

export class WebRTCManager {
  constructor(options) {
    this.roomId = options.roomId;
    this.userId = options.userId;
    this.userName = options.userName;
    this.onRemoteStream = options.onRemoteStream;
    this.onRemoteStreamRemoved = options.onRemoteStreamRemoved;
    this.onUserList = options.onUserList;
    this.onError = options.onError;

    this.ws = null;
    this.localStream = null;
    this.peerConnections = new Map();
    this.pendingCandidates = new Map();
  }

  async init() {
    try {
      if (!navigator.mediaDevices?.getUserMedia) {
        this.onError?.("当前环境不支持摄像头访问，请使用 HTTPS 或 localhost");
        this.localStream = null;
      } else {
        this.localStream = await navigator.mediaDevices.getUserMedia({
          video: { width: 640, height: 480 },
          audio: true,
        });
      }
    } catch (error) {
      const errMsg = this.getMediaErrorMessage(error);
      this.onError?.(errMsg);
      this.localStream = null;
    }
    this.connectWebSocket();
    return this.localStream;
  }

  getMediaErrorMessage(error) {
    switch (error.name) {
      case "NotAllowedError":
        return "摄像头/麦克风权限被拒绝，请在浏览器设置中允许访问";
      case "NotFoundError":
        return "未检测到摄像头或麦克风设备";
      case "NotReadableError":
        return "摄像头或麦克风被其他程序占用";
      case "OverconstrainedError":
        return "摄像头不支持请求的分辨率";
      default:
        return `无法访问摄像头/麦克风: ${error.message}`;
    }
  }

  connectWebSocket() {
    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    const host = window.location.host;
    const wsUrl = `${protocol}//${host}/ws/signaling`;

    this.ws = new WebSocket(wsUrl);

    this.ws.onopen = () => {
      this.send({
        type: "JOIN",
        roomId: this.roomId,
        userId: this.userId,
        userName: this.userName,
      });
    };

    this.ws.onmessage = (event) => {
      const msg = JSON.parse(event.data);
      this.handleMessage(msg);
    };

    this.ws.onerror = () => this.onError?.("WebSocket连接失败，请检查网络");
    this.ws.onclose = (event) => {
      if (event.code !== 1000) {
        this.onError?.("连接已断开，请刷新页面重试");
      }
    };
  }

  async handleMessage(msg) {
    try {
      switch (msg.type) {
        case "USER_LIST":
          this.onUserList?.(msg.payload);
          await this.handleUserList(msg.payload);
          break;
        case "OFFER":
          await this.handleOffer(msg);
          break;
        case "ANSWER":
          await this.handleAnswer(msg);
          break;
        case "CANDIDATE":
          await this.handleCandidate(msg);
          break;
        case "ERROR":
          this.onError?.(msg.payload || "服务器错误");
          break;
      }
    } catch (error) {
      console.error("[WebRTC] handleMessage error:", error);
    }
  }

  async handleUserList(users) {
    const currentUserIds = new Set(users.map((u) => u.userId));
    for (const [oderId] of this.peerConnections) {
      if (!currentUserIds.has(oderId)) {
        this.peerConnections.get(oderId)?.close();
        this.peerConnections.delete(oderId);
        this.onRemoteStreamRemoved?.(oderId);
      }
    }

    const otherUsers = users.filter((u) => u.userId !== this.userId);
    for (const user of otherUsers) {
      if (!this.peerConnections.has(user.userId)) {
        if (this.userId < user.userId) {
          await this.createOffer(user.userId);
        }
      }
    }
  }

  createPeerConnection(targetUserId) {
    if (this.peerConnections.has(targetUserId)) {
      this.peerConnections.get(targetUserId).close();
    }

    const pc = new RTCPeerConnection({ iceServers: ICE_SERVERS });

    pc.onicecandidate = (event) => {
      if (event.candidate) {
        this.send({
          type: "CANDIDATE",
          roomId: this.roomId,
          userId: this.userId,
          targetUserId,
          payload: event.candidate,
        });
      }
    };

    pc.ontrack = (event) => {
      if (event.streams && event.streams[0]) {
        this.onRemoteStream?.(targetUserId, event.streams[0]);
      }
    };

    if (this.localStream) {
      this.localStream.getTracks().forEach((track) => {
        pc.addTrack(track, this.localStream);
      });
    }

    this.peerConnections.set(targetUserId, pc);
    return pc;
  }

  async createOffer(targetUserId) {
    const pc = this.createPeerConnection(targetUserId);

    const offer = await pc.createOffer({
      offerToReceiveAudio: true,
      offerToReceiveVideo: true,
    });
    await pc.setLocalDescription(offer);

    this.send({
      type: "OFFER",
      roomId: this.roomId,
      userId: this.userId,
      targetUserId,
      payload: pc.localDescription,
    });
  }

  async handleOffer(msg) {
    const fromUserId = msg.userId;

    const existingPc = this.peerConnections.get(fromUserId);
    if (
      existingPc &&
      existingPc.signalingState !== "stable" &&
      this.userId < fromUserId
    ) {
      return;
    }

    const pc = this.createPeerConnection(fromUserId);
    await pc.setRemoteDescription(new RTCSessionDescription(msg.payload));

    const pending = this.pendingCandidates.get(fromUserId) || [];
    for (const candidate of pending) {
      await pc.addIceCandidate(new RTCIceCandidate(candidate));
    }
    this.pendingCandidates.delete(fromUserId);

    const answer = await pc.createAnswer();
    await pc.setLocalDescription(answer);

    this.send({
      type: "ANSWER",
      roomId: this.roomId,
      userId: this.userId,
      targetUserId: fromUserId,
      payload: pc.localDescription,
    });
  }

  async handleAnswer(msg) {
    const pc = this.peerConnections.get(msg.userId);

    if (pc && pc.signalingState === "have-local-offer") {
      await pc.setRemoteDescription(new RTCSessionDescription(msg.payload));

      const pending = this.pendingCandidates.get(msg.userId) || [];
      for (const candidate of pending) {
        await pc.addIceCandidate(new RTCIceCandidate(candidate));
      }
      this.pendingCandidates.delete(msg.userId);
    }
  }

  async handleCandidate(msg) {
    const pc = this.peerConnections.get(msg.userId);
    if (pc?.remoteDescription?.type) {
      await pc.addIceCandidate(new RTCIceCandidate(msg.payload));
    } else {
      if (!this.pendingCandidates.has(msg.userId)) {
        this.pendingCandidates.set(msg.userId, []);
      }
      this.pendingCandidates.get(msg.userId).push(msg.payload);
    }
  }

  send(data) {
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(data));
    }
  }

  toggleVideo(enabled) {
    const track = this.localStream?.getVideoTracks()[0];
    if (track) track.enabled = enabled;
  }

  toggleAudio(enabled) {
    const track = this.localStream?.getAudioTracks()[0];
    if (track) track.enabled = enabled;
  }

  leave() {
    this.send({
      type: "LEAVE",
      roomId: this.roomId,
      userId: this.userId,
      userName: this.userName,
    });
    this.peerConnections.forEach((pc) => pc.close());
    this.peerConnections.clear();
    this.localStream?.getTracks().forEach((track) => track.stop());
    this.ws?.close();
  }
}
