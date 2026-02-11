package com.example;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
public class App {
    
    private static final String QUEUE_NAME = "news";
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @Bean
    public CommandLineRunner runner(RabbitTemplate rabbitTemplate) {
        return args -> {
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
                
                rabbitTemplate.convertAndSend(QUEUE_NAME, message);
                System.out.println("✓ Published: " + message);
            }
        };
    }
}
