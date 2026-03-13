package com.webrtc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.webrtc.dto.RoomDTO;
import com.webrtc.entity.Room;
import com.webrtc.entity.RoomUser;
import com.webrtc.mapper.RoomMapper;
import com.webrtc.mapper.RoomUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
    
    private final RoomMapper roomMapper;
    private final RoomUserMapper roomUserMapper;
    
    public Room createRoom(RoomDTO dto) {
        Room room = new Room();
        room.setRoomId(UUID.randomUUID().toString().substring(0, 8));
        room.setRoomName(dto.getRoomName());
        room.setMaxUsers(dto.getMaxUsers() != null ? dto.getMaxUsers() : 10);
        room.setCurrentUsers(0);
        room.setStatus(1);
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());
        roomMapper.insert(room);
        log.info("创建房间: roomId={}, roomName={}", room.getRoomId(), room.getRoomName());
        return room;
    }
    
    public List<Room> listRooms() {
        return roomMapper.selectList(
            new LambdaQueryWrapper<Room>()
                .eq(Room::getStatus, 1)
                .orderByDesc(Room::getCreatedAt)
        );
    }
    
    public Room getRoomById(String roomId) {
        return roomMapper.selectOne(
            new LambdaQueryWrapper<Room>().eq(Room::getRoomId, roomId)
        );
    }
    
    @Transactional
    public void deleteRoom(String roomId) {
        roomMapper.delete(new LambdaQueryWrapper<Room>().eq(Room::getRoomId, roomId));
        roomUserMapper.delete(new LambdaQueryWrapper<RoomUser>().eq(RoomUser::getRoomId, roomId));
        log.info("删除房间: roomId={}", roomId);
    }
    
    @Transactional
    public void joinRoom(String roomId, String userId, String userName) {
        Room room = getRoomById(roomId);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        if (room.getCurrentUsers() >= room.getMaxUsers()) {
            throw new RuntimeException("房间已满");
        }
        
        RoomUser existing = roomUserMapper.selectOne(
            new LambdaQueryWrapper<RoomUser>()
                .eq(RoomUser::getRoomId, roomId)
                .eq(RoomUser::getUserId, userId)
        );
        if (existing == null) {
            RoomUser roomUser = new RoomUser();
            roomUser.setRoomId(roomId);
            roomUser.setUserId(userId);
            roomUser.setUserName(userName);
            roomUser.setVideoEnabled(1);
            roomUser.setAudioEnabled(1);
            roomUser.setJoinedAt(LocalDateTime.now());
            roomUserMapper.insert(roomUser);
            
            room.setCurrentUsers(room.getCurrentUsers() + 1);
            roomMapper.updateById(room);
        }
        log.info("用户加入房间: roomId={}, userId={}, userName={}", roomId, userId, userName);
    }
    
    @Transactional
    public void leaveRoom(String roomId, String userId) {
        int deleted = roomUserMapper.delete(
            new LambdaQueryWrapper<RoomUser>()
                .eq(RoomUser::getRoomId, roomId)
                .eq(RoomUser::getUserId, userId)
        );
        if (deleted > 0) {
            Room room = getRoomById(roomId);
            if (room != null && room.getCurrentUsers() > 0) {
                room.setCurrentUsers(room.getCurrentUsers() - 1);
                roomMapper.updateById(room);
            }
        }
        log.info("用户离开房间: roomId={}, userId={}", roomId, userId);
    }
    
    public List<RoomUser> getRoomUsers(String roomId) {
        return roomUserMapper.selectList(
            new LambdaQueryWrapper<RoomUser>().eq(RoomUser::getRoomId, roomId)
        );
    }
}
