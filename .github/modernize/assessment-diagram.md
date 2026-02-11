# Architecture Diagram - RabbitMQ News Feed Demo

This document contains architecture diagrams for the RabbitMQ News Feed Demo application, generated from the assessment results.

## Table of Contents
- [Current Architecture](#current-architecture)
- [Component Diagram](#component-diagram)
- [Sequence Diagram](#sequence-diagram)
- [Technology Stack](#technology-stack)
- [Azure Migration Architecture](#azure-migration-architecture)

---

## Current Architecture

This diagram shows the high-level architecture of the current application.

```mermaid
graph TB
    subgraph "Client Application"
        Client[Demo Client<br/>JAR Application]
    end
    
    subgraph "Message Broker"
        RabbitMQ[RabbitMQ<br/>Message Queue]
    end
    
    subgraph "Web Application"
        WebApp[Demo Web App<br/>WAR - Jetty Server]
        Consumer[RabbitMQ Consumer<br/>ServletContextListener]
        WebSocket[WebSocket Endpoint<br/>Real-time Communication]
        WebUI[Web UI<br/>HTML/JavaScript]
    end
    
    subgraph "End Users"
        Browser[Web Browser]
    end
    
    Client -->|Publish News| RabbitMQ
    RabbitMQ -->|Consume Messages| Consumer
    Consumer -->|Broadcast| WebSocket
    Browser <-->|WebSocket Connection| WebSocket
    Browser -->|HTTP| WebUI
    
    style Client fill:#e1f5ff
    style WebApp fill:#fff4e1
    style RabbitMQ fill:#ffe1e1
    style Browser fill:#e1ffe1
```

### Architecture Description

**Components:**
1. **Demo Client (democlient)**: Standalone Java application that publishes news messages to RabbitMQ
2. **RabbitMQ**: Message broker that handles asynchronous message delivery
3. **Demo Web App (demo)**: Java EE web application that consumes messages and broadcasts to browsers
4. **Web Browser**: End-user interface that receives real-time updates via WebSocket

**Flow:**
1. User enters news message in Demo Client CLI
2. Client publishes message to RabbitMQ queue named "news"
3. Web App's RabbitMQ Consumer receives message
4. Consumer broadcasts message to all connected WebSocket clients
5. Web browsers display the news in real-time

---

## Component Diagram

Detailed view of application components and their relationships.

```mermaid
graph TB
    subgraph "democlient Module"
        AppMain[App.java<br/>Main Class]
        RMQPub[RabbitMQ Publisher<br/>ConnectionFactory]
    end
    
    subgraph "demo Module"
        RMQCons[RabbitMQConsumer.java<br/>ServletContextListener]
        WSEndpoint[NewsWebSocket.java<br/>ServerEndpoint]
        WebContent[Web Content<br/>HTML/CSS/JS]
    end
    
    subgraph "External Dependencies"
        RMQBroker[RabbitMQ Server<br/>Port 5672]
        RMQMgmt[RabbitMQ Management<br/>Port 15672]
    end
    
    subgraph "Runtime Environment"
        JettyServer[Jetty Server<br/>Port 8080]
        JVM[Java Runtime<br/>JDK 1.8]
    end
    
    AppMain --> RMQPub
    RMQPub -->|AMQP Protocol| RMQBroker
    RMQBroker -->|Deliver Messages| RMQCons
    RMQCons -->|Broadcast| WSEndpoint
    RMQCons -.->|Lifecycle| JettyServer
    WSEndpoint -.->|Runs on| JettyServer
    WebContent -.->|Served by| JettyServer
    JettyServer -.->|Runs on| JVM
    
    style AppMain fill:#b3d9ff
    style RMQCons fill:#ffccb3
    style WSEndpoint fill:#ffccb3
    style RMQBroker fill:#ffb3b3
    style JettyServer fill:#d9b3ff
```

---

## Sequence Diagram

Message flow through the system.

```mermaid
sequenceDiagram
    participant User as User
    participant Client as Demo Client
    participant RMQ as RabbitMQ
    participant Consumer as RabbitMQ Consumer
    participant WS as WebSocket
    participant Browser as Web Browser

    User->>Client: Enter news message
    Client->>RMQ: Publish message to queue
    Note over RMQ: Queue: news
    
    RMQ->>Consumer: Deliver message
    Consumer->>Consumer: Process message
    Consumer->>WS: Broadcast to all sessions
    
    WS->>Browser: Send message via WebSocket
    Browser->>Browser: Display news update
    
    Note over Browser: Real-time update displayed
```

---

## Technology Stack

Key technologies used in the application.

```mermaid
graph LR
    subgraph "Programming"
        Java[Java 1.8]
    end
    
    subgraph "Build Tools"
        Maven[Maven]
    end
    
    subgraph "Frameworks"
        JavaEE[Java EE]
        Servlet[Servlet API 3.1]
        WSApi[WebSocket API 1.1]
    end
    
    subgraph "Libraries"
        RMQClient[RabbitMQ Client 5.16.0]
        Gson[Gson 2.8.9]
        JUnit[JUnit 4.11]
    end
    
    subgraph "Server"
        Jetty[Jetty 9.4.48]
    end
    
    subgraph "External Services"
        RabbitMQ[RabbitMQ 3.x]
    end
    
    Java --> Maven
    Maven --> Servlet
    Maven --> WSApi
    Maven --> RMQClient
    Maven --> Gson
    Servlet --> Jetty
    WSApi --> Jetty
    RMQClient --> RabbitMQ
    
    style Java fill:#f9d71c
    style Maven fill:#c71a36
    style Jetty fill:#ffcc00
    style RabbitMQ fill:#ff6600
```

---

## Azure Migration Architecture

Recommended Azure architecture after migration.

### Option 1: Refactor with Azure Native Services (Recommended)

```mermaid
graph TB
    subgraph "Client Application"
        ClientApp[Demo Client<br/>Container/App Service]
    end
    
    subgraph "Azure Messaging"
        ServiceBus[Azure Service Bus<br/>Queue: news]
    end
    
    subgraph "Azure App Service"
        WebApp[Demo Web App<br/>Java 17 Runtime]
        Consumer[Message Consumer<br/>Service Bus Receiver]
        SignalR[SignalR Integration<br/>Server-side]
    end
    
    subgraph "Azure SignalR Service"
        SignalRService[Azure SignalR<br/>Managed WebSocket]
    end
    
    subgraph "Azure Services"
        AppInsights[Application Insights<br/>Monitoring]
        KeyVault[Key Vault<br/>Secrets Management]
        AppConfig[App Configuration<br/>Settings]
    end
    
    subgraph "End Users"
        Browser[Web Browser<br/>SignalR Client]
    end
    
    ClientApp -->|Send Message| ServiceBus
    ServiceBus -->|Receive Message| Consumer
    Consumer -->|Broadcast| SignalR
    SignalR <-->|Managed Connection| SignalRService
    SignalRService <-->|WebSocket/SSE| Browser
    
    WebApp -.->|Telemetry| AppInsights
    WebApp -.->|Get Secrets| KeyVault
    WebApp -.->|Get Config| AppConfig
    ClientApp -.->|Telemetry| AppInsights
    
    style ServiceBus fill:#00bcf2
    style WebApp fill:#00bcf2
    style SignalRService fill:#00bcf2
    style AppInsights fill:#00bcf2
    style KeyVault fill:#00bcf2
    style AppConfig fill:#00bcf2
```

### Option 2: Lift and Shift with Containers

```mermaid
graph TB
    subgraph "Azure Container Apps"
        ClientContainer[Demo Client<br/>Container]
        WebAppContainer[Demo Web App<br/>Container]
    end
    
    subgraph "Azure Container Instances"
        RMQContainer[RabbitMQ<br/>Container]
    end
    
    subgraph "Supporting Services"
        ACR[Azure Container Registry<br/>Image Storage]
        Storage[Azure Files<br/>Persistent Storage]
        Monitor[Azure Monitor<br/>Logging]
    end
    
    subgraph "End Users"
        Browser2[Web Browser]
    end
    
    ClientContainer -->|AMQP| RMQContainer
    RMQContainer -->|Messages| WebAppContainer
    WebAppContainer <-->|WebSocket| Browser2
    
    ACR -.->|Pull Images| ClientContainer
    ACR -.->|Pull Images| WebAppContainer
    ACR -.->|Pull Images| RMQContainer
    
    RMQContainer -.->|Persist Data| Storage
    WebAppContainer -.->|Logs| Monitor
    ClientContainer -.->|Logs| Monitor
    
    style ACR fill:#00bcf2
    style Storage fill:#00bcf2
    style Monitor fill:#00bcf2
```

---

## Migration Comparison

| Aspect | Current | Azure Native (Option 1) | Containerized (Option 2) |
|--------|---------|------------------------|-------------------------|
| **Messaging** | RabbitMQ (self-hosted) | Azure Service Bus | RabbitMQ (container) |
| **WebSocket** | Custom implementation | Azure SignalR Service | Custom implementation |
| **Hosting** | Jetty (on-premise) | Azure App Service | Azure Container Apps |
| **Configuration** | Hardcoded | Azure App Configuration + Key Vault | Environment variables |
| **Monitoring** | Manual/Custom | Application Insights | Azure Monitor |
| **Scalability** | Manual | Auto-scale | Manual/Auto-scale |
| **Maintenance** | High | Low | Medium |
| **Azure Integration** | None | Native | Moderate |
| **Migration Effort** | N/A | Medium | Low-Medium |
| **Recommended For** | Current state | Production, Long-term | Quick migration, Testing |

---

## Key Findings

### Current Architecture Strengths
- ✅ Simple, easy to understand architecture
- ✅ Asynchronous messaging pattern
- ✅ Real-time updates via WebSocket
- ✅ Separation between publisher and consumer

### Current Architecture Challenges
- ⚠️ Hardcoded configuration (localhost, queue names)
- ⚠️ Outdated Java version (1.8)
- ⚠️ Self-managed RabbitMQ infrastructure
- ⚠️ No cloud-native features
- ⚠️ Limited scalability options
- ⚠️ Manual monitoring and operations

### Azure Migration Benefits
- ✅ Fully managed messaging with Azure Service Bus
- ✅ Scalable WebSocket with Azure SignalR
- ✅ Automated monitoring with Application Insights
- ✅ Secure configuration with Key Vault
- ✅ Auto-scaling capabilities
- ✅ Built-in high availability
- ✅ Reduced operational overhead

---

## Next Steps

1. **Review Assessment Report**: See `.github/modernize/report.json` for detailed findings
2. **Choose Migration Strategy**: Select between refactor (recommended) or lift-and-shift
3. **Plan Migration**: Create detailed migration plan based on chosen strategy
4. **Implement Changes**: Follow recommendations in assessment report
5. **Test Thoroughly**: Validate all functionality in Azure environment
6. **Deploy**: Use CI/CD pipeline for automated deployment

---

**Generated:** 2026-02-11T06:59:46.514Z  
**Tool:** Assessment Diagram Skill  
**Repository:** showpune/asbdemo
