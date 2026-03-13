package com.webrtc.service;

import com.webrtc.entity.OperationLog;
import com.webrtc.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogService {
    
    private final OperationLogMapper operationLogMapper;
    
    @Async
    public void log(String userId, String userName, String action, String target, String detail, String ip) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUserName(userName);
        log.setAction(action);
        log.setTarget(target);
        log.setDetail(detail);
        log.setIp(ip);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
