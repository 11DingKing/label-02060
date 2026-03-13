package com.webrtc.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoomDTO {
    @NotBlank(message = "房间名称不能为空")
    @Size(min = 1, max = 50, message = "房间名称长度1-50字符")
    private String roomName;
    
    @Min(value = 2, message = "最大人数不能少于2人")
    @Max(value = 20, message = "最大人数不能超过20人")
    private Integer maxUsers = 10;
}
