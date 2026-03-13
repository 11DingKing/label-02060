package com.webrtc.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webrtc.entity.RoomUser;
import com.webrtc.service.LogService;
import com.webrtc.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignalingHandler extends TextWebSocketHandler {
    
    private final ObjectMapper objectMapper;
    private final RoomService roomService;
    private final LogService logService;
    
    // roomId -> (userId -> session)
    private final Map<String, Map<String, WebSocketSession>> rooms = new ConcurrentHashMap<>();
    // sessionId -> (roomId, userId)
    private final Map<String, String[]> sessionInfo = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket连接建立: sessionId={}", session.getId());
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SignalingMessage msg = objectMapper.readValue(message.getPayload(), SignalingMessage.class);
        log.debug("收到消息: type={}, roomId={}, userId={}", msg.getType(), msg.getRoomId(), msg.getUserId());
        
        switch (msg.getType()) {
            case "JOIN" -> handleJoin(session, msg);
            case "LEAVE" -> handleLeave(session, msg);
            case "OFFER", "ANSWER", "CANDIDATE" -> handleSignaling(session, msg);
            default -> log.warn("未知消息类型: {}", msg.getType());
        }
    }
    
    private void handleJoin(WebSocketSession session, SignalingMessage msg) throws IOException {
        String roomId = msg.getRoomId();
        String userId = msg.getUserId();
        String userName = msg.getUserName();
        
        try {
            roomService.joinRoom(roomId, userId, userName);
        } catch (Exception e) {
            sendError(session, e.getMessage());
            return;
        }
        
        rooms.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(userId, session);
        sessionInfo.put(session.getId(), new String[]{roomId, userId});
        
        logService.log(userId, userName, "JOIN_ROOM", roomId, "加入房间", null);
        broadcastUserList(roomId);
    }
    
    private void handleLeave(WebSocketSession session, SignalingMessage msg) throws IOException {
        String roomId = msg.getRoomId();
        String userId = msg.getUserId();
        
        removeUserFromRoom(roomId, userId);
        sessionInfo.remove(session.getId());
        
        logService.log(userId, msg.getUserName(), "LEAVE_ROOM", roomId, "离开房间", null);
        broadcastUserList(roomId);
    }
    
    private void handleSignaling(WebSocketSession session, SignalingMessage msg) throws IOException {
        String roomId = msg.getRoomId();
        String targetUserId = msg.getTargetUserId();
        
        Map<String, WebSocketSession> roomSessions = rooms.get(roomId);
        if (roomSessions != null) {
            WebSocketSession targetSession = roomSessions.get(targetUserId);
            if (targetSession != null && targetSession.isOpen()) {
                targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            }
        }
    }
    
    private void broadcastUserList(String roomId) throws IOException {
        List<RoomUser> users = roomService.getRoomUsers(roomId);
        SignalingMessage msg = new SignalingMessage();
        msg.setType("USER_LIST");
        msg.setRoomId(roomId);
        msg.setPayload(users);
        
        String json = objectMapper.writeValueAsString(msg);
        Map<String, WebSocketSession> roomSessions = rooms.get(roomId);
        if (roomSessions != null) {
            for (WebSocketSession s : roomSessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(json));
                }
            }
        }
    }
    
    private void sendError(WebSocketSession session, String error) throws IOException {
        SignalingMessage msg = new SignalingMessage();
        msg.setType("ERROR");
        msg.setPayload(error);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
    }
    
    private void removeUserFromRoom(String roomId, String userId) {
        Map<String, WebSocketSession> roomSessions = rooms.get(roomId);
        if (roomSessions != null) {
            roomSessions.remove(userId);
            if (roomSessions.isEmpty()) {
                rooms.remove(roomId);
            }
        }
        roomService.leaveRoom(roomId, userId);
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        String[] info = sessionInfo.remove(session.getId());
        if (info != null) {
            removeUserFromRoom(info[0], info[1]);
            broadcastUserList(info[0]);
        }
        log.info("WebSocket连接关闭: sessionId={}, status={}", session.getId(), status);
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket传输错误: sessionId={}", session.getId(), exception);
    }
}
