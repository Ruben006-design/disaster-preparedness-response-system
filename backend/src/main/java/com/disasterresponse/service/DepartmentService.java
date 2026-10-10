package com.disasterresponse.service;

import com.disasterresponse.dao.DepartmentDAO;
import com.disasterresponse.dao.ResponseTeamDAO;
import com.disasterresponse.dto.CreateDepartmentRequest;
import com.disasterresponse.dto.CreateTeamRequest;
import com.disasterresponse.model.GovernmentDepartment;
import com.disasterresponse.model.ResponseTeam;

import java.util.List;

public class DepartmentService {

    private final DepartmentDAO departmentDAO;
    private final ResponseTeamDAO teamDAO;

    public DepartmentService() {
        this.departmentDAO = new DepartmentDAO();
        this.teamDAO = new ResponseTeamDAO();
    }

    public List<GovernmentDepartment> getAllDepartments() {
        return departmentDAO.findAll();
    }

    public GovernmentDepartment getDepartmentById(Long id) {
        GovernmentDepartment dept = departmentDAO.findById(id);
        if (dept == null) {
            throw new IllegalArgumentException("Department not found with ID: " + id);
        }
        return dept;
    }

    public GovernmentDepartment createDepartment(CreateDepartmentRequest req) {
        GovernmentDepartment dept = new GovernmentDepartment();
        dept.setName(req.getName());
        dept.setCode(req.getCode());
        dept.setDepartmentType(req.getDepartmentType());
        dept.setDescription(req.getDescription());
        dept.setContactEmail(req.getContactEmail());
        dept.setContactPhone(req.getContactPhone());
        dept.setHeadName(req.getHeadName());
        return departmentDAO.save(dept);
    }

    public GovernmentDepartment updateDepartment(Long id, CreateDepartmentRequest req) {
        GovernmentDepartment existing = getDepartmentById(id);
        existing.setName(req.getName());
        existing.setCode(req.getCode());
        existing.setDepartmentType(req.getDepartmentType());
        existing.setDescription(req.getDescription());
        existing.setContactEmail(req.getContactEmail());
        existing.setContactPhone(req.getContactPhone());
        existing.setHeadName(req.getHeadName());
        departmentDAO.update(existing);
        return existing;
    }

    public ResponseTeam createTeam(CreateTeamRequest req) {
        ResponseTeam team = new ResponseTeam(req.getDepartmentId(), req.getTeamName(), req.getTeamLead(), req.getContactPhone());
        return teamDAO.save(team);
    }

    public List<ResponseTeam> getAllTeams() {
        return teamDAO.findAll();
    }
}
