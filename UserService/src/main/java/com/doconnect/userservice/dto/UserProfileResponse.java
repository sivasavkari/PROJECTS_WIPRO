package com.doconnect.userservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class UserProfileResponse {
    private Long id;
    private String authUserId;
    private String displayName;
    private String email;
    private String jobTitle;
    private String location;
    private String bio;
    private String avatarUrl;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
