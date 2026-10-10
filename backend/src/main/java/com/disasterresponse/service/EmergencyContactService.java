package com.disasterresponse.service;

import com.disasterresponse.dao.EmergencyContactDAO;
import com.disasterresponse.model.EmergencyContact;

import java.util.List;

public class EmergencyContactService {

    private final EmergencyContactDAO contactDAO;

    public EmergencyContactService() {
        this.contactDAO = new EmergencyContactDAO();
    }

    public List<EmergencyContact> getAllContacts() {
        return contactDAO.findAll();
    }

    public List<EmergencyContact> getAllAdminContacts() {
        return contactDAO.findAllIncludingInactive();
    }

    public EmergencyContact getContactById(Long id) {
        EmergencyContact c = contactDAO.findById(id);
        if (c == null) {
            throw new IllegalArgumentException("Emergency contact not found with ID: " + id);
        }
        return c;
    }

    public EmergencyContact createContact(EmergencyContact c) {
        return contactDAO.save(c);
    }

    public EmergencyContact updateContact(Long id, EmergencyContact c) {
        EmergencyContact existing = getContactById(id);
        c.setId(existing.getId());
        contactDAO.update(c);
        return c;
    }

    public void deleteContact(Long id) {
        contactDAO.delete(id);
    }
}
