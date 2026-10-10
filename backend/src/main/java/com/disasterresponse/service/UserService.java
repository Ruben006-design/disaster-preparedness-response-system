package com.disasterresponse.service;

import com.disasterresponse.dao.UserDAO;
import com.disasterresponse.dto.ChangePasswordRequest;
import com.disasterresponse.dto.PageResponse;
import com.disasterresponse.dto.UpdateProfileRequest;
import com.disasterresponse.dto.UserProfileResponse;
import com.disasterresponse.enums.UserStatus;
import com.disasterresponse.model.User;
import com.disasterresponse.util.BCrypt;

import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserProfileResponse getProfile(Long userId) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        return mapToProfile(user);
    }

    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }

        userDAO.update(user);
        return mapToProfile(user);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        if (!BCrypt.checkpw(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password.");
        }

        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters.");
        }

        String newHash = BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt());
        userDAO.updatePassword(userId, newHash);
    }

    public PageResponse<UserProfileResponse> getAllUsers(int page, int size) {
        List<User> list = userDAO.findAll(page, size);
        long total = userDAO.countAll();
        List<UserProfileResponse> dtos = list.stream().map(this::mapToProfile).collect(Collectors.toList());
        return new PageResponse<>(dtos, total, page, size);
    }

    public UserProfileResponse toggleUserStatus(Long userId) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        UserStatus newStatus = (user.getStatus() == UserStatus.ACTIVE) ? UserStatus.SUSPENDED : UserStatus.ACTIVE;
        userDAO.updateStatus(userId, newStatus);
        user.setStatus(newStatus);
        return mapToProfile(user);
    }

    private UserProfileResponse mapToProfile(User u) {
        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setRole(u.getRole());
        dto.setStatus(u.getStatus());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        return dto;
    }
}
