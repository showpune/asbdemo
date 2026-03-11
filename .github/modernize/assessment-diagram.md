# RabbitMQ News Feed Demo - Architecture Diagram

## Current Architecture

```mermaid
graph TB
    subgraph "Client Application"
        Client[News Publisher Client<br/>democlient JAR<br/>Java 1.8]
    end
    
    subgraph "Message Broker"
        RMQ[RabbitMQ Server<br/>localhost:5672<br/>Queue: news]
    end
    
    subgraph "Web Application"
        WebApp[Demo Service<br/>demo WAR<br/>Jetty Server<br/>Port 8080]
        Consumer[RabbitMQ Consumer<br/>ServletContextListener]
        WS[WebSocket Server<br/>news-websocket endpoint]
    end
    
    subgraph "User Interface"
        Browser[Web Browser<br/>Real-time News Feed]
    end
    
    Client -->|Publishes news messages| RMQ
    RMQ -->|Consumes messages| Consumer
    Consumer -->|Broadcasts via| WS
    WS <-->|WebSocket connection| Browser
    Browser -->|HTTP request| WebApp
    
    style Client fill:#e1f5ff
    style RMQ fill:#ff9999
    style WebApp fill:#99ccff
    style Consumer fill:#99ccff
    style WS fill:#99ccff
    style Browser fill:#ccffcc
```

## Technology Stack

```mermaid
graph LR
    subgraph "Application Stack"
        Java[Java 1.8]
        Maven[Maven Build]
        Servlet[Servlet API 3.1.0]
        WSApi[WebSocket API 1.1]
    end
    
    subgraph "Messaging"
        AMQP[RabbitMQ AMQP Client 5.16.0]
    end
    
    subgraph "Runtime"
        Jetty[Jetty 9.4.48]
    end
    
    subgraph "Utilities"
        Gson[Gson 2.8.9]
        JUnit[JUnit 4.11]
    end
    
    style Java fill:#f9f
    style Maven fill:#f9f
    style AMQP fill:#ff9999
    style Jetty fill:#99ccff
```

## Data Flow Sequence

```mermaid
sequenceDiagram
    participant User
    participant Client as News Publisher<br/>(democlient)
    participant RMQ as RabbitMQ<br/>Queue
    participant Consumer as RabbitMQ<br/>Consumer
    participant WS as WebSocket<br/>Server
    participant Browser as Web Browser
    
    User->>Client: Enter news message
    Client->>RMQ: Publish message to 'news' queue
    RMQ->>Consumer: Deliver message
    Consumer->>WS: Broadcast to all sessions
    WS->>Browser: Push message via WebSocket
    Browser->>User: Display news in real-time
```

## Component Dependencies

```mermaid
graph TD
    Demo[demo WAR Module]
    DemoClient[democlient JAR Module]
    
    Demo --> RabbitMQLib[com.rabbitmq:amqp-client:5.16.0]
    Demo --> WSLib[javax.websocket-api:1.1]
    Demo --> ServletLib[javax.servlet-api:3.1.0]
    Demo --> GsonLib[com.google.code.gson:2.8.9]
    
    DemoClient --> RabbitMQLib
    
    style Demo fill:#99ccff
    style DemoClient fill:#e1f5ff
    style RabbitMQLib fill:#ff9999
    style WSLib fill:#ccffcc
    style ServletLib fill:#ccffcc
    style GsonLib fill:#ffcc99
```

## Azure Migration Architecture (Recommended)

```mermaid
graph TB
    subgraph "Client Application"
        AzClient[News Publisher Client<br/>Containerized or VM<br/>Java 17]
    end
    
    subgraph "Azure Messaging"
        ASB[Azure Service Bus<br/>Queue or Topic<br/>AMQP 1.0 Protocol]
    end
    
    subgraph "Azure App Service"
        AppService[Azure App Service<br/>Tomcat Runtime<br/>Java 17]
        AppConsumer[RabbitMQ Consumer<br/>Migrated to Service Bus]
        AppWS[WebSocket Server<br/>Enabled in App Service]
    end
    
    subgraph "Configuration"
        AppConfig[Azure App Configuration<br/>Environment Settings]
        KeyVault[Azure Key Vault<br/>Connection Strings]
    end
    
    subgraph "Monitoring"
        AppInsights[Application Insights<br/>Telemetry and Logging]
    end
    
    subgraph "User Interface"
        AzBrowser[Web Browser<br/>HTTPS Connection]
    end
    
    AzClient -->|Send messages via AMQP| ASB
    ASB -->|Receive messages| AppConsumer
    AppConsumer -->|Broadcast| AppWS
    AppWS <-->|WSS WebSocket| AzBrowser
    AzBrowser -->|HTTPS| AppService
    
    AppService -.->|Read config| AppConfig
    AppService -.->|Get secrets| KeyVault
    AppService -.->|Send telemetry| AppInsights
    AzClient -.->|Get secrets| KeyVault
    
    style AzClient fill:#e1f5ff
    style ASB fill:#0078d4
    style AppService fill:#0078d4
    style AppConsumer fill:#0078d4
    style AppWS fill:#0078d4
    style AppConfig fill:#7fba00
    style KeyVault fill:#ffb900
    style AppInsights fill:#00bcf2
    style AzBrowser fill:#ccffcc
```

## Migration Options Comparison

```mermaid
graph LR
    subgraph "Option 1: Rehost"
        O1[RabbitMQ on Azure VM<br/>+ App Service<br/>Minimal changes<br/>1-2 weeks]
    end
    
    subgraph "Option 2: Replatform (Recommended)"
        O2[Azure Service Bus<br/>+ App Service<br/>Moderate changes<br/>3-4 weeks]
    end
    
    subgraph "Option 3: Refactor"
        O3[Microservices<br/>+ Container Apps<br/>Significant changes<br/>6-8 weeks]
    end
    
    Current[Current App] --> O1
    Current --> O2
    Current --> O3
    
    style Current fill:#ff9999
    style O1 fill:#ffcc99
    style O2 fill:#99ccff
    style O3 fill:#ccffcc
```

## Key Migration Considerations

### Configuration Changes Required
- **Hardcoded Values**: Externalize RabbitMQ host and queue names
- **Credentials**: Move to Azure Key Vault
- **Environment Settings**: Use Azure App Configuration

### Messaging Migration
- **Current**: RabbitMQ with AMQP client
- **Target**: Azure Service Bus with AMQP 1.0 protocol
- **Changes**: Connection string format, authentication model
- **Benefit**: Minimal code changes, managed service

### Runtime Migration
- **Current**: Embedded Jetty server
- **Target**: Azure App Service with Tomcat
- **Changes**: Deploy as WAR, configure App Service
- **Benefit**: Managed platform, auto-scaling, WebSocket support

### WebSocket Support
- **Current**: javax.websocket-api
- **Target**: Same API on Azure App Service
- **Changes**: Enable WebSocket in App Service configuration
- **Benefit**: No code changes required

### Java Version
- **Current**: Java 1.8
- **Recommended**: Java 17 LTS
- **Changes**: Update pom.xml, test compatibility
- **Benefit**: Long-term support, performance improvements

## Architecture Assessment Summary

### ✅ Cloud-Ready Components
- WebSocket implementation (compatible with Azure App Service)
- Servlet-based web application (standard WAR deployment)
- Maven build system (integrates with Azure DevOps/GitHub Actions)
- AMQP messaging protocol (supported by Azure Service Bus)

### ⚠️ Components Requiring Changes
- Hardcoded configuration (host, queue names)
- RabbitMQ-specific connection code
- Missing authentication/authorization
- No structured logging or monitoring
- Embedded Jetty server (needs platform migration)

### 🔧 Recommended Enhancements
- Add Application Insights for monitoring
- Implement Azure Key Vault integration
- Upgrade to Java 17 LTS
- Add CI/CD pipeline configuration
- Create Dockerfile for containerization option
- Implement health check endpoints
- Add structured logging (SLF4J/Logback)

## Deployment Architecture Options

### Option 1: Azure App Service (Recommended for Quick Start)
```mermaid
graph LR
    LB[Azure Load Balancer] --> AS1[App Service Instance 1]
    LB --> AS2[App Service Instance 2]
    AS1 --> ASB[Azure Service Bus]
    AS2 --> ASB
    ASB --> |Queue| Q[news Queue]
    
    style LB fill:#0078d4
    style AS1 fill:#0078d4
    style AS2 fill:#0078d4
    style ASB fill:#7fba00
    style Q fill:#7fba00
```

### Option 2: Azure Container Apps (For Microservices)
```mermaid
graph LR
    AGW[Azure API Gateway] --> CA1[Container App 1]
    AGW --> CA2[Container App 2]
    CA1 --> ASB[Azure Service Bus]
    CA2 --> ASB
    CA1 --> ACR[Azure Container Registry]
    CA2 --> ACR
    
    style AGW fill:#0078d4
    style CA1 fill:#00bcf2
    style CA2 fill:#00bcf2
    style ASB fill:#7fba00
    style ACR fill:#ffb900
```

## Next Steps

1. **Immediate**: Externalize configuration to environment variables
2. **Short-term**: Set up Azure resources (App Service, Service Bus)
3. **Medium-term**: Migrate messaging to Azure Service Bus
4. **Long-term**: Add monitoring, upgrade Java version, consider containerization

---

*Generated from application assessment on 2026-02-11*
