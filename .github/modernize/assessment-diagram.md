# Application Architecture Diagram

This document contains architecture diagrams generated from the assessment of the RabbitMQ News Feed application.

## Current Application Architecture

### High-Level Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        Browser[Web Browser]
        CLI[Command Line Publisher]
    end
    
    subgraph "Application Layer"
        WebApp[Demo Web Application<br/>WAR - Jetty 9.4<br/>Java 1.8]
        Publisher[DemoClient Publisher<br/>JAR<br/>Java 1.8]
    end
    
    subgraph "Communication Layer"
        MQ[RabbitMQ Message Queue<br/>Queue: news]
        WS[WebSocket Endpoint<br/>/news-websocket]
    end
    
    CLI -->|Sends News Messages| Publisher
    Publisher -->|Publish to Queue| MQ
    MQ -->|Consume Messages| WebApp
    WebApp -->|Broadcast Updates| WS
    WS -->|Real-time Push| Browser
    
    style WebApp fill:#e1f5ff
    style Publisher fill:#e1f5ff
    style MQ fill:#fff4e1
    style WS fill:#ffe1f5
    style Browser fill:#e8f5e1
    style CLI fill:#e8f5e1
```

### Technology Stack

```mermaid
graph LR
    subgraph "Demo Web Application"
        direction TB
        A1[Java EE Servlet API 3.1]
        A2[WebSocket API 1.1]
        A3[RabbitMQ Client 5.16.0]
        A4[Gson 2.8.9]
        A5[Jetty Maven Plugin 9.4.48]
    end
    
    subgraph "DemoClient Publisher"
        direction TB
        B1[Java SE 1.8]
        B2[RabbitMQ Client 5.16.0]
        B3[Maven Build]
    end
    
    subgraph "Runtime Environment"
        direction TB
        C1[Java 1.8 Runtime]
        C2[Servlet Container<br/>Jetty or Tomcat]
        C3[RabbitMQ Server]
    end
    
    style A1 fill:#e1f5ff
    style A2 fill:#e1f5ff
    style A3 fill:#fff4e1
    style B2 fill:#fff4e1
    style C3 fill:#fff4e1
```

### Data Flow

```mermaid
sequenceDiagram
    participant User as End User
    participant CLI as DemoClient CLI
    participant RMQ as RabbitMQ<br/>news queue
    participant Web as Demo WebApp
    participant WS as WebSocket
    participant Browser as Web Browser
    
    Note over Browser,Web: 1. User opens web page
    Browser->>WS: Connect to WebSocket
    WS-->>Browser: Connection established
    
    Note over User,CLI: 2. User enters news via CLI
    User->>CLI: Type news message
    CLI->>RMQ: Publish message to queue
    
    Note over RMQ,Web: 3. WebApp consumes messages
    RMQ->>Web: Deliver message
    Web->>Web: Process message
    
    Note over Web,Browser: 4. Broadcast to connected clients
    Web->>WS: Broadcast to all sessions
    WS->>Browser: Push message via WebSocket
    Browser->>User: Display news update
```

### Component Architecture

```mermaid
graph TB
    subgraph "Demo Web Application WAR"
        direction TB
        SCL[ServletContextListener<br/>RabbitMQConsumer]
        WSE[WebSocket Endpoint<br/>NewsWebSocket]
        SCL -->|broadcasts to| WSE
    end
    
    subgraph "DemoClient Application JAR"
        direction TB
        Main[Main Class<br/>App.java]
        Input[BufferedReader<br/>Console Input]
        Main -->|reads from| Input
    end
    
    subgraph "External Services"
        direction TB
        RMQ[RabbitMQ Server<br/>localhost:5672]
    end
    
    SCL -->|consumes| RMQ
    Main -->|publishes| RMQ
    
    style SCL fill:#e1f5ff
    style WSE fill:#ffe1f5
    style Main fill:#e1f5ff
    style RMQ fill:#fff4e1
```

### Deployment Model

```mermaid
graph TB
    subgraph "Current Deployment"
        direction TB
        D1[WAR File Deployment]
        D2[External Servlet Container<br/>Jetty or Tomcat]
        D3[Standalone JAR Execution]
        D4[Local RabbitMQ Instance]
        
        D1 -->|deployed to| D2
        D2 -->|connects to| D4
        D3 -->|connects to| D4
    end
    
    subgraph "Infrastructure Requirements"
        direction TB
        I1[Java 1.8+ Runtime]
        I2[Servlet Container]
        I3[RabbitMQ Server]
        I4[Network Connectivity]
    end
    
    style D2 fill:#e1f5ff
    style D3 fill:#e1f5ff
    style D4 fill:#fff4e1
```

## Azure Migration Architecture Options

### Option 1: Lift and Shift to Azure

```mermaid
graph TB
    subgraph "Azure Cloud"
        subgraph "App Service"
            direction TB
            AS[Azure App Service<br/>Tomcat Runtime<br/>Demo WAR]
        end
        
        subgraph "Messaging"
            direction TB
            VM[Azure VM<br/>RabbitMQ Server]
        end
        
        subgraph "Compute"
            direction TB
            CI[Azure Container Instances<br/>DemoClient JAR]
        end
        
        AS -->|connects to| VM
        CI -->|publishes to| VM
    end
    
    Browser[Web Browser] -->|HTTPS| AS
    
    style AS fill:#0078d4,color:#fff
    style VM fill:#0078d4,color:#fff
    style CI fill:#0078d4,color:#fff
```

### Option 2: Modernize with Azure Native Services

```mermaid
graph TB
    subgraph "Azure Cloud"
        subgraph "Web Tier"
            direction TB
            ACA[Azure Container Apps<br/>Spring Boot JAR<br/>Embedded Tomcat]
        end
        
        subgraph "Messaging Tier"
            direction TB
            ASB[Azure Service Bus<br/>Queue or Topic]
        end
        
        subgraph "Real-time Communication"
            direction TB
            SR[Azure SignalR Service<br/>WebSocket Management]
        end
        
        subgraph "Monitoring"
            direction TB
            AI[Application Insights<br/>Telemetry and Logging]
        end
        
        subgraph "Configuration"
            direction TB
            AC[App Configuration<br/>Centralized Settings]
            KV[Key Vault<br/>Secrets Management]
        end
        
        ACA -->|consumes messages| ASB
        ACA -->|broadcasts via| SR
        ACA -->|telemetry| AI
        ACA -->|reads config| AC
        ACA -->|reads secrets| KV
        ASB -->|monitoring| AI
    end
    
    Publisher[Publisher App<br/>Container or Function] -->|publishes to| ASB
    Browser[Web Browser] -->|HTTPS| ACA
    Browser -->|WebSocket| SR
    
    style ACA fill:#0078d4,color:#fff
    style ASB fill:#0078d4,color:#fff
    style SR fill:#0078d4,color:#fff
    style AI fill:#50e6ff
    style AC fill:#50e6ff
    style KV fill:#50e6ff
```

### Option 3: Cloud-Native Microservices

```mermaid
graph TB
    subgraph "Azure Kubernetes Service AKS"
        subgraph "Ingress"
            direction TB
            AGW[Azure Application Gateway<br/>Ingress Controller]
        end
        
        subgraph "Microservices"
            direction TB
            WEB[Web Service Pod<br/>Spring Boot]
            PUB[Publisher Service Pod<br/>Spring Boot]
            API[API Gateway Pod<br/>Spring Cloud Gateway]
        end
        
        subgraph "Azure Services"
            direction TB
            ASB[Azure Service Bus<br/>Premium Tier]
            SR[Azure SignalR Service]
            REDIS[Azure Cache for Redis]
        end
        
        subgraph "Data and Monitoring"
            direction TB
            AI[Application Insights]
            ACR[Azure Container Registry]
        end
        
        AGW -->|routes to| API
        API -->|forwards to| WEB
        API -->|forwards to| PUB
        WEB -->|consumes| ASB
        PUB -->|publishes| ASB
        WEB -->|uses| SR
        WEB -->|caches in| REDIS
        WEB -->|logs to| AI
        PUB -->|logs to| AI
    end
    
    Browser[Web Browser] -->|HTTPS| AGW
    DevOps[CI/CD Pipeline] -->|deploys images| ACR
    ACR -->|pulls images| WEB
    ACR -->|pulls images| PUB
    
    style AGW fill:#0078d4,color:#fff
    style WEB fill:#0078d4,color:#fff
    style PUB fill:#0078d4,color:#fff
    style API fill:#0078d4,color:#fff
    style ASB fill:#00b294
    style SR fill:#00b294
    style REDIS fill:#00b294
    style AI fill:#50e6ff
    style ACR fill:#50e6ff
```

## Key Architecture Insights

### Current State Assessment

**Strengths:**
- Simple, straightforward architecture
- Clear separation of concerns (publisher vs consumer)
- Real-time communication via WebSocket
- Message queue for decoupling

**Challenges:**
- Legacy Java 1.8 platform
- Hardcoded configuration values
- Traditional WAR deployment model
- Direct RabbitMQ dependency
- Limited cloud-native features
- No authentication or authorization
- Minimal error handling and monitoring

### Migration Recommendations

**Short-term (1-2 weeks):**
1. Upgrade to Java 17 or 21 LTS
2. Externalize configuration to environment variables
3. Update dependencies to latest versions
4. Add basic health check endpoints

**Medium-term (3-4 weeks):**
1. Migrate to Spring Boot framework
2. Replace RabbitMQ with Azure Service Bus
3. Containerize with Docker
4. Deploy to Azure Container Apps or App Service
5. Implement Azure Application Insights
6. Add authentication via Azure AD

**Long-term (6-8 weeks):**
1. Consider microservices architecture if scalability is needed
2. Implement API Gateway pattern
3. Add caching layer (Azure Redis Cache)
4. Implement comprehensive CI/CD pipeline
5. Add blue-green deployment capability

## Technology Migration Path

```mermaid
graph LR
    subgraph "Current Stack"
        direction TB
        C1[Java 1.8]
        C2[Java EE/Servlet]
        C3[javax namespace]
        C4[RabbitMQ Client]
        C5[WAR Deployment]
        C6[Manual Config]
    end
    
    subgraph "Target Stack"
        direction TB
        T1[Java 17/21 LTS]
        T2[Spring Boot 3.x]
        T3[jakarta namespace]
        T4[Azure Service Bus]
        T5[JAR with Embedded Server]
        T6[Azure App Configuration]
    end
    
    C1 -.->|upgrade| T1
    C2 -.->|migrate| T2
    C3 -.->|migrate| T3
    C4 -.->|replace| T4
    C5 -.->|modernize| T5
    C6 -.->|externalize| T6
    
    style C1 fill:#ffcccc
    style C2 fill:#ffcccc
    style C3 fill:#ffcccc
    style C4 fill:#ffcccc
    style C5 fill:#ffcccc
    style C6 fill:#ffcccc
    style T1 fill:#ccffcc
    style T2 fill:#ccffcc
    style T3 fill:#ccffcc
    style T4 fill:#ccffcc
    style T5 fill:#ccffcc
    style T6 fill:#ccffcc
```

## Summary

This RabbitMQ News Feed application demonstrates a classic message-driven architecture with real-time WebSocket communication. While the current implementation is functional, migrating to Azure with modern frameworks like Spring Boot and Azure-native services (Service Bus, SignalR) will provide:

- **Better Scalability:** Auto-scaling, load balancing, and managed services
- **Enhanced Security:** Azure AD integration, managed identities, Key Vault
- **Improved Reliability:** Built-in high availability, disaster recovery
- **Easier Operations:** Centralized monitoring, configuration, and deployment
- **Cost Efficiency:** Pay-as-you-go pricing, serverless options, resource optimization

The recommended approach is **Option 2: Modernize with Azure Native Services** as it provides the best balance of effort, risk, and cloud benefits for this application's size and complexity.
