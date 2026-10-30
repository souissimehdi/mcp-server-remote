package com.demo.mcpserverremote.tool;

import com.demo.mcpserverremote.entity.HelpDeskTicket;
import com.demo.mcpserverremote.model.TicketRequest;
import com.demo.mcpserverremote.service.HelpDeskTicketService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HelpDeskTools {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskTools.class);

    private final HelpDeskTicketService service;

    @McpTool(name = "createTicket", description = "Create the Support Ticket")
    String createTicket(@McpToolParam(description = "Details to create a Support ticket")
                        TicketRequest ticketRequest) {
        LOGGER.info("Creating support ticket with details: {}", ticketRequest);
        HelpDeskTicket savedTicket = service.createTicket(ticketRequest);
        LOGGER.info("Ticket created successfully. Ticket ID: {}, Username: {}", savedTicket.getId(), savedTicket.getUsername());
        return "Ticket #" + savedTicket.getId() + " created successfully for user " + savedTicket.getUsername();
    }

    @McpTool(name="getTicketStatus", description = "Fetch the status of the tickets based on a given username")
    List<HelpDeskTicket> getTicketStatus(@McpToolParam(description =
            "Username to fetch the status of the help desk tickets") String username,
                                         McpSyncRequestContext context) throws InterruptedException {
        LOGGER.info("Fetching tickets for user: {}", username);
        context.info("Fetching tickets for user: " + username);
        List<HelpDeskTicket> tickets =  service.getTicketsByUsername(username);
        LOGGER.info("Found {} tickets for user: {}", tickets.size(), username);
        context.info("Found " + tickets.size() + " tickets for user: " + username);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            int percent = (i * 100) / 10;
            context.progress(spec -> spec.progress(percent)
                    .message("Fetching tickets for user: " + username + " - " + percent + "% complete"));
        }
        return tickets;
    }

}