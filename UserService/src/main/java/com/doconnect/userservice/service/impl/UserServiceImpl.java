package com.doconnect.userservice.service.impl;

import com.doconnect.userservice.dto.UserProfileRequest;
import com.doconnect.userservice.dto.UserProfileResponse;
import com.doconnect.userservice.dto.UserStatusUpdateRequest;
import com.doconnect.userservice.entity.User;
import com.doconnect.userservice.exception.EmailAlreadyUsedException;
import com.doconnect.userservice.exception.ProfileAlreadyExistsException;
import com.doconnect.userservice.exception.UserNotFoundException;
import com.doconnect.userservice.mapper.UserMapper;
import com.doconnect.userservice.repository.UserRepository;
import com.doconnect.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserProfileResponse createProfile(String authUserId, UserProfileRequest request) {
        // Guard against duplicate profile creation for the same auth user id.
        userRepository.findByAuthUserId(authUserId)
                .ifPresent(u -> {
                    throw new ProfileAlreadyExistsException(authUserId);
                });

        // Enforce unique email addresses at the profile level.
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new EmailAlreadyUsedException(request.getEmail());
                });

        User saved = userRepository.save(userMapper.toEntity(request, authUserId));
        return userMapper.toResponse(saved);
    }

    @Override
    public UserProfileResponse updateProfile(Long id, UserProfileRequest request) {
        User existing = getUser(id);
        userRepository.findByEmail(request.getEmail()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new EmailAlreadyUsedException(request.getEmail());
            }
        });
        userMapper.updateEntity(existing, request);
        return userMapper.toResponse(existing);
    }

    @Override
    public UserProfileResponse updateStatus(Long id, UserStatusUpdateRequest request) {
        User user = getUser(id);
        user.setActive(request.getActive());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long id) {
        return userMapper.toResponse(getUser(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserByAuthId(String authUserId) {
        User user = userRepository.findByAuthUserId(authUserId)
            .orElseThrow(() -> new UserNotFoundException(authUserId));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getActiveUsers() {
        return userRepository.findByActiveTrue().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> searchUsers(String query) {
        if (query == null || query.isBlank()) {
            return getActiveUsers();
        }
        return userRepository.findByDisplayNameContainingIgnoreCase(query).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
    }
}