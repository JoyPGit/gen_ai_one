package com.example.ai_proj_one.chat.dockermodelrunner.tool;

import com.example.ai_proj_one.chat.dockermodelrunner.dto.*;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.tool.annotation.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderStatusTools {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        for(int i =0; i<10; i++) {
            String orderId = "ORD-"+i;
            Order order = Order.builder()
                    .orderId(orderId)
                    .carrier(i%2 == 0?"Fedex":"UPS")
                    .status(OrderStatus.CREATED)
                    .userId("user-"+i)
                    .userName("User "+i)
                    .build();
            orders.put(orderId, order);
        }
    }

    /**
     * this method will be called by LLM when instructed, since it is a tool
     * @param orderId
     * @param userId
     * @return
     */
    @Tool
    public String getOrderStatus(@ToolParam(description = "Order Id") String orderId,
            @ToolParam(description = "User Id") String userId) {
        Order order = this.orders.getOrDefault(orderId, null);

        if(Objects.isNull(order)) {
            return "Order not found";
        }
        if(!order.userId().equalsIgnoreCase(userId)) {
            return "This " + orderId + " doesn't belong to this user";
        }

        OrderStatus nextStatus = order.status();
        OrderStatus nextStage = nextStage(nextStatus);

        Order updatedOrder = Order.builder()
                .orderId(orderId)
                .carrier(order.carrier())
                .status(nextStatus)
                .userId(userId)
                .userName(order.userName())
                .build();
        orders.put(orderId, updatedOrder);

        return "Order " + orderId + " for user name "+ order.userName() + "is currently" +
                " at status " + order.status();
    }

    private OrderStatus nextStage(OrderStatus orderStatus) {
        return switch (orderStatus) {
            case CREATED -> OrderStatus.PROCESSING;
            case PROCESSING -> OrderStatus.SHIPPED;
            case SHIPPED -> OrderStatus.DELIVERED;
            case DELIVERED -> OrderStatus.DELIVERED; // stay at final stage
        };
    }
}
