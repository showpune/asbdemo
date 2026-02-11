package com.example;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * RabbitMQ News Publisher Client (Spring Boot 3.4)
 */
@SpringBootApplication
public class App implements CommandLineRunner {
    
    private static final String QUEUE_NAME = "news";
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== RabbitMQ News Publisher ===");
        System.out.println("Connected successfully!");
        System.out.println("Enter news messages (type 'exit' or 'quit' to stop):\n");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        while (true) {
            System.out.print("News > ");
            String message = reader.readLine();
            
            if (message == null || message.trim().equalsIgnoreCase("exit") 
                || message.trim().equalsIgnoreCase("quit")) {
                System.out.println("\nExiting...");
                System.exit(0);
            }
            
            if (message.trim().isEmpty()) {
                continue;
            }
            
            // Publish message to queue
            rabbitTemplate.convertAndSend(QUEUE_NAME, message);
            System.out.println("✓ Published: " + message);
        }
    }
}
