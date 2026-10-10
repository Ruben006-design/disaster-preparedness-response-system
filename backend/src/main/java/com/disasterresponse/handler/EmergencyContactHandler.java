package com.disasterresponse.handler;

import com.disasterresponse.model.EmergencyContact;
import com.disasterresponse.service.EmergencyContactService;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;

public class EmergencyContactHandler extends BaseHandler {

    private final EmergencyContactService contactService;

    public EmergencyContactHandler() {
        this.contactService = new EmergencyContactService();
    }

    public EmergencyContactHandler(EmergencyContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String sub = getSubPath(exchange, "/api/contacts");

        if ("GET".equalsIgnoreCase(method) && sub.isEmpty()) {
            List<EmergencyContact> contacts = contactService.getAllContacts();
            sendSuccess(exchange, contacts);
            return;
        }

        sendError(exchange, 404, "Endpoint not found: " + exchange.getRequestURI().getPath());
    }
}
