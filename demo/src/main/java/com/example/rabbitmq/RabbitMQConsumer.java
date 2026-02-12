package com.example.rabbitmq;

import com.example.websocket.NewsWebSocket;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {
    
    private static final String QUEUE_NAME = "news";
    
    @RabbitListener(queues = QUEUE_NAME)
    public void receiveMessage(String message) {
        System.out.println("========================================");
        System.out.println(">>> Received from RabbitMQ: " + message);
        
        // Broadcast to all connected WebSocket clients
        try {
            NewsWebSocket.broadcast(message);
            System.out.println(">>> Broadcasted to WebSocket clients");
        } catch (Exception e) {
            System.err.println(">>> ERROR broadcasting to WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("========================================");
    }
}
