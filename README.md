# MCP Server Remote

A remote [Model Context Protocol](https://modelcontextprotocol.io) (MCP) server built with Spring Boot and Spring AI. It exposes a small help desk as MCP tools, so any MCP client (Claude Desktop, an AI agent, etc.) can create and look up support tickets.

Built while following the Udemy course [Java Spring AI](https://www.udemy.com/course/java-spring-ai/).

## Tech stack

- Java 25
- Spring Boot 4.1.2
- Spring AI 2.0.1 (`spring-ai-starter-mcp-server-webmvc`)
- Spring Data JPA with an H2 file database
- Lombok

## MCP tools

| Tool              | Parameters                            | Description                                    |
|-------------------|---------------------------------------|------------------------------------------------|
| `createTicket`    | `issue`, `username`                   | Creates a support ticket                       |
| `getTicketStatus` | `username`                            | Returns all tickets belonging to the given user, reporting progress notifications while it works |
| `summarizeTickets` | `username`                           | Returns a short natural-language summary of the user's tickets, generated through MCP sampling |

`summarizeTickets` uses MCP **sampling**: the server has no LLM of its own and asks the connected client to run the completion (`ctx.sample(...)`). If the client does not advertise the sampling capability, the tool falls back to returning the raw ticket data.

Tickets are stored in the `helpdesk_tickets` table (`id`, `username`, `issue`, `status`, `createdAt`, `eta`).

## Running

Requires JDK 25.

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The server listens on port `8090` and uses the **streamable HTTP** transport (`spring.ai.mcp.server.protocol=streamable`). With Spring AI's default settings the MCP endpoint is `http://localhost:8090/mcp`.

## Connecting a client

Point any MCP client that supports streamable HTTP at the endpoint above. For example, with the MCP Inspector:

```bash
npx @modelcontextprotocol/inspector
```

Then choose the *Streamable HTTP* transport and enter `http://localhost:8090/mcp`.

## Database

Data is persisted in an H2 file database at `~/mcpserverremote` and the schema is created automatically (`ddl-auto=update`). The H2 console is enabled, so you can inspect tickets at `http://localhost:8090/h2-console` using the JDBC URL from `src/main/resources/application.properties`.

> The datasource credentials in `application.properties` are demo values for a local database. Change them before using this anywhere beyond local experiments.

## Project layout

```
src/main/java/com/demo/mcpserverremote
├── McpServerRemoteApplication.java   # Spring Boot entry point
├── entity/HelpDeskTicket.java        # JPA entity
├── model/TicketRequest.java          # Tool input
├── repository/                       # Spring Data repository
├── service/                          # Ticket business logic
└── tool/HelpDeskTools.java           # MCP tool definitions
```
