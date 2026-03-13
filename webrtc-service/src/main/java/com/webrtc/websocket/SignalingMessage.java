package com.webrtc.websocket;

import lombok.Data;

@Data
public class SignalingMessage {
    private String type;      // JOIN, LEAVE, OFFER, ANSWER, CANDIDATE, USER_LIST, ERROR
    private String roomId;
    private String userId;
    private String userName;
    private String targetUserId;
    private Object payload;   // SDP or ICE Candidate
}
