package com.example.ai_proj_one.chat.dockermodelrunner.dto;

import lombok.*;

@Builder
public record Order (
        String orderId,
        String carrier,
        OrderStatus status,
        String userId,
        String userName
) {
}
