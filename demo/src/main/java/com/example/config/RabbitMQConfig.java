package com.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    
    private static final String QUEUE_NAME = "news";
    
    @Bean
    public Queue newsQueue() {
        return new Queue(QUEUE_NAME, false);
    }
}
