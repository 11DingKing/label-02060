package com.webrtc.controller;

import com.webrtc.dto.Result;
import com.webrtc.dto.RoomDTO;
import com.webrtc.entity.Room;
import com.webrtc.service.LogService;
import com.webrtc.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoomController {
    
    private final RoomService roomService;
    private final LogService logService;
    
    @PostMapping("/create")
    public Result<Room> createRoom(@Valid @RequestBody RoomDTO dto) {
        Room room = roomService.createRoom(dto);
        logService.log(null, null, "CREATE_ROOM", room.getRoomId(), "创建房间: " + room.getRoomName(), null);
        return Result.success(room);
    }
    
    @GetMapping("/list")
    public Result<List<Room>> listRooms() {
        return Result.success(roomService.listRooms());
    }
    
    @GetMapping("/{roomId}")
    public Result<Room> getRoom(@PathVariable String roomId) {
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            return Result.error(404, "房间不存在");
        }
        return Result.success(room);
    }
    
    @DeleteMapping("/{roomId}")
    public Result<Void> deleteRoom(@PathVariable String roomId) {
        roomService.deleteRoom(roomId);
        logService.log(null, null, "DELETE_ROOM", roomId, "删除房间", null);
        return Result.success();
    }
}
