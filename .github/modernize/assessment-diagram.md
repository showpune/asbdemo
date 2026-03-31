# Architecture Diagram - RabbitMQ News Feed Demo

## Application Overview

This is a real-time news feed application that demonstrates message-driven architecture using RabbitMQ for messaging and WebSocket for browser updates.

## High-Level Architecture

```mermaid
graph TB
    subgraph "Client Applications"
        Publisher[News Publisher Client<br/>democlient JAR]
        Browser[Web Browser<br/>User Interface]
    end
    
    subgraph "Message Broker"
        RMQ[RabbitMQ<br/>Message Queue: news]
    end
    
    subgraph "Server Application - demo WAR"
        subgraph "Presentation Layer"
            WS[WebSocket Endpoint<br/>NewsWebSocket]
            Static[Static Web Content]
        end
        
        subgraph "Integration Layer"
            Consumer[RabbitMQ Consumer<br/>ServletContextListener]
        end
        
        Server[Jetty Server<br/>Port 8080]
    end
    
    Publisher -->|Publish Messages| RMQ
    RMQ -->|Consume Messages| Consumer
    Consumer -->|Broadcast| WS
    Browser <-->|WebSocket Connection| WS
    Browser -->|HTTP Request| Static
    Static -.->|Served by| Server
    WS -.->|Hosted on| Server
    Consumer -.->|Runs in| Server
    
    style Publisher fill:#e1f5ff
    style Browser fill:#e1f5ff
    style RMQ fill:#fff4e1
    style Consumer fill:#e8f5e9
    style WS fill:#e8f5e9
    style Static fill:#e8f5e9
    style Server fill:#f3e5f5
```

## Technology Stack

```mermaid
graph LR
    subgraph "Runtime Environment"
        Java[Java 1.8]
        Jetty[Jetty 9.4.48]
    end
    
    subgraph "Core Libraries"
        RabbitMQLib[RabbitMQ AMQP Client 5.16.0]
        WebSocketAPI[WebSocket API 1.1]
        ServletAPI[Servlet API 3.1.0]
        Gson[Gson 2.8.9]
    end
    
    subgraph "Build Tool"
        Maven[Apache Maven]
    end
    
    Java --> Jetty
    Jetty --> ServletAPI
    Jetty --> WebSocketAPI
    RabbitMQLib --> Java
    Gson --> Java
    Maven --> Java
    
    style Java fill:#b3d9ff
    style Jetty fill:#b3d9ff
    style RabbitMQLib fill:#ffccbc
    style WebSocketAPI fill:#ffccbc
    style ServletAPI fill:#ffccbc
    style Gson fill:#ffccbc
    style Maven fill:#c8e6c9
```

## Data Flow

```mermaid
sequenceDiagram
    participant User
    participant Publisher as Publisher Client
    participant RabbitMQ
    participant Consumer as RabbitMQ Consumer
    participant WebSocket
    participant Browser
    
    User->>Publisher: Enter news message
    Publisher->>RabbitMQ: Publish to queue: news
    RabbitMQ->>Consumer: Deliver message
    Consumer->>WebSocket: Broadcast to all sessions
    WebSocket->>Browser: Push message via WebSocket
    Browser->>User: Display news update
    
    Note over Publisher,RabbitMQ: AMQP Protocol
    Note over Consumer,WebSocket: In-memory communication
    Note over WebSocket,Browser: WebSocket Protocol
```

## Component Details

### Publisher Client (democlient)
- **Type**: Standalone Java application (JAR)
- **Purpose**: Publishes news messages to RabbitMQ queue
- **Technology**: Java 1.8, RabbitMQ AMQP Client
- **Execution**: Command-line interface with user input

### Server Application (demo)
- **Type**: Web application (WAR)
- **Purpose**: Consumes messages from RabbitMQ and broadcasts to browsers
- **Technology**: Java 1.8, Jetty, WebSocket, Servlet API
- **Components**:
  - **RabbitMQConsumer**: ServletContextListener that connects to RabbitMQ on startup and consumes messages
  - **NewsWebSocket**: WebSocket endpoint that manages browser connections and broadcasts messages
  - **Static Content**: HTML/JS for browser interface

### Message Broker (RabbitMQ)
- **Type**: External message broker
- **Purpose**: Decouples message publishing from consumption
- **Queue**: "news" queue for message storage
- **Deployment**: Docker container (for development)

## Architecture Characteristics

### Strengths
- ✅ **Loose Coupling**: Publisher and consumer are decoupled through RabbitMQ
- ✅ **Real-time Updates**: WebSocket provides instant message delivery to browsers
- ✅ **Scalability**: Message queue can handle high message throughput
- ✅ **Simplicity**: Clean separation of concerns with clear component boundaries

### Considerations for Azure Migration
- ⚠️ **Configuration Management**: RabbitMQ host and queue names are hardcoded
- ⚠️ **State Management**: WebSocket sessions stored in-memory (limits horizontal scaling)
- ⚠️ **Infrastructure Dependencies**: Requires RabbitMQ infrastructure
- ⚠️ **Java Version**: Java 1.8 is outdated (recommend Java 11+ for Azure)

## Azure Migration Options

### Option 1: Lift and Shift
```mermaid
graph LR
    Publisher[Publisher Client]
    AppService[Azure App Service<br/>demo WAR]
    ACI[Azure Container Instances<br/>RabbitMQ]
    Browser[Web Browsers]
    
    Publisher -->|AMQP| ACI
    ACI -->|AMQP| AppService
    Browser <-->|WebSocket| AppService
    
    style AppService fill:#0078d4,color:#fff
    style ACI fill:#0078d4,color:#fff
```

### Option 2: Azure Native Services
```mermaid
graph LR
    Publisher[Publisher Client<br/>Modified]
    AppService[Azure App Service<br/>demo WAR]
    ServiceBus[Azure Service Bus]
    SignalR[Azure SignalR Service]
    Browser[Web Browsers]
    
    Publisher -->|Service Bus SDK| ServiceBus
    ServiceBus -->|Service Bus SDK| AppService
    AppService -->|SignalR SDK| SignalR
    Browser <-->|SignalR Protocol| SignalR
    
    style AppService fill:#0078d4,color:#fff
    style ServiceBus fill:#0078d4,color:#fff
    style SignalR fill:#0078d4,color:#fff
```

### Option 3: Container-Based
```mermaid
graph LR
    Publisher[Publisher Container]
    Demo[Demo Container]
    RabbitMQ[RabbitMQ Container]
    Browser[Web Browsers]
    ACA[Azure Container Apps]
    
    Publisher -->|AMQP| RabbitMQ
    RabbitMQ -->|AMQP| Demo
    Browser <-->|WebSocket| Demo
    
    ACA -.->|Hosts| Publisher
    ACA -.->|Hosts| Demo
    ACA -.->|Hosts| RabbitMQ
    
    style ACA fill:#0078d4,color:#fff
```

## Deployment Architecture

### Current Development Setup
- **Publisher**: Run via Maven exec plugin
- **Server**: Jetty embedded server (Maven plugin)
- **RabbitMQ**: Docker container on localhost
- **Browser**: Connect to http://localhost:8080/demo/

### Recommended Azure Production Setup
- **Publisher**: Azure Container Apps or scheduled Azure Functions
- **Server**: Azure App Service (Java 11+) or Azure Container Apps
- **Messaging**: Azure Service Bus (recommended) or RabbitMQ in containers
- **Real-time**: Azure SignalR Service (for multi-instance scaling)
- **Monitoring**: Azure Application Insights
- **Secrets**: Azure Key Vault

---

*Generated from assessment results on 2026-02-11*
