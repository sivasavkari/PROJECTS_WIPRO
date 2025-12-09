package com.doconnect.userservice.mapper;

import com.doconnect.userservice.dto.UserProfileRequest;
import com.doconnect.userservice.dto.UserProfileResponse;
import com.doconnect.userservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserProfileRequest request, String authUserId) {
        User user = new User();
        user.setAuthUserId(authUserId);
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getEmail());
        user.setBio(request.getBio());
        user.setJobTitle(request.getJobTitle());
        user.setLocation(request.getLocation());
        user.setAvatarUrl(request.getAvatarUrl());
        return user;
    }

    public void updateEntity(User user, UserProfileRequest request) {
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getEmail());
        user.setBio(request.getBio());
        user.setJobTitle(request.getJobTitle());
        user.setLocation(request.getLocation());
        user.setAvatarUrl(request.getAvatarUrl());
    }

    public UserProfileResponse toResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .authUserId(user.getAuthUserId())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .bio(user.getBio())
                .jobTitle(user.getJobTitle())
                .location(user.getLocation())
                .avatarUrl(user.getAvatarUrl())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
