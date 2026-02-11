package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.example.websocket.NewsWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final NewsWebSocketHandler newsWebSocketHandler;

    public WebSocketConfig(NewsWebSocketHandler newsWebSocketHandler) {
        this.newsWebSocketHandler = newsWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(newsWebSocketHandler, "/news-websocket")
                .setAllowedOriginPatterns("*");  // Consider restricting to specific origins in production
    }
}
