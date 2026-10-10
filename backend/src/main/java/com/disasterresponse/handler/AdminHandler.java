package com.disasterresponse.handler;

import com.disasterresponse.dto.*;
import com.disasterresponse.model.*;
import com.disasterresponse.service.*;
import com.disasterresponse.util.HttpUtil;
import com.disasterresponse.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class AdminHandler extends BaseHandler {

    private final UserService userService;
    private final EducationService educationService;
    private final DepartmentService departmentService;
    private final EmergencyContactService contactService;

    public AdminHandler() {
        this.userService = new UserService();
        this.educationService = new EducationService();
        this.departmentService = new DepartmentService();
        this.contactService = new EmergencyContactService();
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        requireRole(exchange, "ADMIN");

        String method = exchange.getRequestMethod();
        String sub = getSubPath(exchange, "/api/admin");
        Map<String, String> query = HttpUtil.parseQueryParams(exchange);

        // --- Users ---
        if (sub.startsWith("users")) {
            if ("GET".equalsIgnoreCase(method) && "users".equalsIgnoreCase(sub)) {
                int page = parseInt(query.get("page"), 0);
                int size = parseInt(query.get("size"), 10);
                PageResponse<UserProfileResponse> users = userService.getAllUsers(page, size);
                sendSuccess(exchange, users);
                return;
            } else if ("PATCH".equalsIgnoreCase(method) && sub.endsWith("/toggle-status")) {
                String idStr = sub.substring(6, sub.indexOf("/toggle-status"));
                Long id = Long.parseLong(idStr);
                UserProfileResponse updated = userService.toggleUserStatus(id);
                sendSuccess(exchange, 200, "User status toggled.", updated);
                return;
            }
        }

        // --- Disasters ---
        if (sub.startsWith("disasters")) {
            if ("POST".equalsIgnoreCase(method)) {
                String body = HttpUtil.readRequestBody(exchange);
                DisasterType dt = JsonUtil.fromJson(body, DisasterType.class);
                DisasterType saved = educationService.createDisaster(dt);
                sendSuccess(exchange, 201, "Disaster type created.", saved);
                return;
            } else if ("PUT".equalsIgnoreCase(method)) {
                Long id = Long.parseLong(sub.substring("disasters/".length()));
                String body = HttpUtil.readRequestBody(exchange);
                DisasterType dt = JsonUtil.fromJson(body, DisasterType.class);
                DisasterType updated = educationService.updateDisaster(id, dt);
                sendSuccess(exchange, 200, "Disaster type updated.", updated);
                return;
            } else if ("DELETE".equalsIgnoreCase(method)) {
                Long id = Long.parseLong(sub.substring("disasters/".length()));
                educationService.deleteDisaster(id);
                sendSuccess(exchange, 200, "Disaster type deleted.", null);
                return;
            }
        }

        // --- Modules ---
        if (sub.startsWith("modules")) {
            if ("POST".equalsIgnoreCase(method)) {
                String body = HttpUtil.readRequestBody(exchange);
                LearningModule m = JsonUtil.fromJson(body, LearningModule.class);
                LearningModule saved = educationService.createModule(m);
                sendSuccess(exchange, 201, "Learning module created.", saved);
                return;
            } else if ("PUT".equalsIgnoreCase(method)) {
                Long id = Long.parseLong(sub.substring("modules/".length()));
                String body = HttpUtil.readRequestBody(exchange);
                LearningModule m = JsonUtil.fromJson(body, LearningModule.class);
                LearningModule updated = educationService.updateModule(id, m);
                sendSuccess(exchange, 200, "Learning module updated.", updated);
                return;
            } else if ("DELETE".equalsIgnoreCase(method)) {
                Long id = Long.parseLong(sub.substring("modules/".length()));
                educationService.deleteModule(id);
                sendSuccess(exchange, 200, "Learning module deleted.", null);
                return;
            }
        }

        // --- Quizzes ---
        if (sub.startsWith("quizzes")) {
            if ("POST".equalsIgnoreCase(method)) {
                String body = HttpUtil.readRequestBody(exchange);
                Quiz q = JsonUtil.fromJson(body, Quiz.class);
                Quiz saved = educationService.createQuiz(q);
                sendSuccess(exchange, 201, "Assessment quiz created.", saved);
                return;
            } else if ("PUT".equalsIgnoreCase(method)) {
                Long id = Long.parseLong(sub.substring("quizzes/".length()));
                String body = HttpUtil.readRequestBody(exchange);
                Quiz q = JsonUtil.fromJson(body, Quiz.class);
                Quiz updated = educationService.updateQuiz(id, q);
                sendSuccess(exchange, 200, "Assessment quiz updated.", updated);
                return;
            } else if ("DELETE".equalsIgnoreCase(method)) {
                Long id = Long.parseLong(sub.substring("quizzes/".length()));
                educationService.deleteQuiz(id);
                sendSuccess(exchange, 200, "Assessment quiz deleted.", null);
                return;
            }
        }

        // --- Simulations ---
        if (sub.startsWith("simulations")) {
            if ("POST".equalsIgnoreCase(method)) {
                String body = HttpUtil.readRequestBody(exchange);
                Simulation sim = JsonUtil.fromJson(body, Simulation.class);
                Simulation saved = educationService.createSimulation(sim);
                sendSuccess(exchange, 201, "Scenario simulation created.", saved);
                return;
            } else if ("PUT".equalsIgnoreCase(method)) {
                Long id = Long.parseLong(sub.substring("simulations/".length()));
                String body = HttpUtil.readRequestBody(exchange);
                Simulation sim = JsonUtil.fromJson(body, Simulation.class);
                Simulation updated = educationService.updateSimulation(id, sim);
                sendSuccess(exchange, 200, "Scenario simulation updated.", updated);
                return;
            } else if ("DELETE".equalsIgnoreCase(method)) {
                Long id = Long.parseLong(sub.substring("simulations/".length()));
                educationService.deleteSimulation(id);
                sendSuccess(exchange, 200, "Scenario simulation deleted.", null);
                return;
            }
        }

        // --- Departments ---
        if (sub.startsWith("departments")) {
            if ("GET".equalsIgnoreCase(method)) {
                sendSuccess(exchange, departmentService.getAllDepartments());
                return;
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = HttpUtil.readRequestBody(exchange);
                CreateDepartmentRequest req = JsonUtil.fromJson(body, CreateDepartmentRequest.class);
                GovernmentDepartment saved = departmentService.createDepartment(req);
                sendSuccess(exchange, 201, "Department registered.", saved);
                return;
            } else if ("PUT".equalsIgnoreCase(method)) {
                Long id = Long.parseLong(sub.substring("departments/".length()));
                String body = HttpUtil.readRequestBody(exchange);
                CreateDepartmentRequest req = JsonUtil.fromJson(body, CreateDepartmentRequest.class);
                GovernmentDepartment updated = departmentService.updateDepartment(id, req);
                sendSuccess(exchange, 200, "Department updated.", updated);
                return;
            }
        }

        // --- Teams ---
        if (sub.startsWith("teams")) {
            if ("GET".equalsIgnoreCase(method)) {
                sendSuccess(exchange, departmentService.getAllTeams());
                return;
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = HttpUtil.readRequestBody(exchange);
                CreateTeamRequest req = JsonUtil.fromJson(body, CreateTeamRequest.class);
                ResponseTeam saved = departmentService.createTeam(req);
                sendSuccess(exchange, 201, "Response team registered.", saved);
                return;
            }
        }

        // --- Emergency Contacts ---
        if (sub.startsWith("contacts")) {
            if ("POST".equalsIgnoreCase(method)) {
                String body = HttpUtil.readRequestBody(exchange);
                EmergencyContact c = JsonUtil.fromJson(body, EmergencyContact.class);
                EmergencyContact saved = contactService.createContact(c);
                sendSuccess(exchange, 201, "Emergency contact added.", saved);
                return;
            } else if ("PUT".equalsIgnoreCase(method)) {
                Long id = Long.parseLong(sub.substring("contacts/".length()));
                String body = HttpUtil.readRequestBody(exchange);
                EmergencyContact c = JsonUtil.fromJson(body, EmergencyContact.class);
                EmergencyContact updated = contactService.updateContact(id, c);
                sendSuccess(exchange, 200, "Emergency contact updated.", updated);
                return;
            } else if ("DELETE".equalsIgnoreCase(method)) {
                Long id = Long.parseLong(sub.substring("contacts/".length()));
                contactService.deleteContact(id);
                sendSuccess(exchange, 200, "Emergency contact deleted.", null);
                return;
            }
        }

        sendError(exchange, 404, "Endpoint not found: " + exchange.getRequestURI().getPath());
    }

    private int parseInt(String val, int def) {
        if (val == null || val.trim().isEmpty()) return def;
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return def;
        }
    }
}
