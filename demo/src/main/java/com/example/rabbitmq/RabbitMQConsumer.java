package com.example.rabbitmq;

import com.example.websocket.NewsWebSocketHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {
    
    @RabbitListener(queues = "news")
    public void receiveMessage(String message) {
        System.out.println("========================================");
        System.out.println(">>> Received from RabbitMQ: " + message);
        
        try {
            NewsWebSocketHandler.broadcast(message);
            System.out.println(">>> Broadcasted to WebSocket clients");
        } catch (Exception e) {
            System.err.println(">>> ERROR broadcasting to WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("========================================");
    }
}
