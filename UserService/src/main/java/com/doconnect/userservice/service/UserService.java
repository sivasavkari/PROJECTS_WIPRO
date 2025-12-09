package com.doconnect.userservice.service;

import com.doconnect.userservice.dto.UserProfileRequest;
import com.doconnect.userservice.dto.UserProfileResponse;
import com.doconnect.userservice.dto.UserStatusUpdateRequest;

import java.util.List;

public interface UserService {

	UserProfileResponse createProfile(String authUserId, UserProfileRequest request);

	UserProfileResponse updateProfile(Long id, UserProfileRequest request);

	UserProfileResponse updateStatus(Long id, UserStatusUpdateRequest request);

	UserProfileResponse getUserById(Long id);

	UserProfileResponse getUserByAuthId(String authUserId);

	List<UserProfileResponse> getActiveUsers();

	List<UserProfileResponse> searchUsers(String query);
}


