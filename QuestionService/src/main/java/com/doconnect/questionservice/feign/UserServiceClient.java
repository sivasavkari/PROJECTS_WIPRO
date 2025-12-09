package com.doconnect.questionservice.feign;

import com.doconnect.questionservice.feign.dto.UserProfileClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = com.doconnect.questionservice.config.FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/api/users/by-auth/{authUserId}")
    UserProfileClientResponse getUserProfile(@PathVariable("authUserId") String authUserId);
}
