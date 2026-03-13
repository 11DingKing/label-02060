package com.webrtc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("room_user")
public class RoomUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String roomId;
    private String userId;
    private String userName;
    private Integer videoEnabled;
    private Integer audioEnabled;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime joinedAt;
}
