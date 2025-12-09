package com.doconnect.authservice.config;

import com.doconnect.authservice.entity.UserCredential;
import com.doconnect.authservice.repository.UserCredentialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final UserCredentialRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.seed.enabled:true}")
    private boolean seedEnabled;

    @Value("${admin.seed.email:admin@doconnect.com}")
    private String adminEmail;

    @Value("${admin.seed.password:Admin@123}")
    private String adminPassword;

    @Value("${admin.seed.full-name:System Administrator}")
    private String adminFullName;

    @Value("${admin.seed.role:ROLE_ADMIN}")
    private String adminRole;

    public AdminUserInitializer(UserCredentialRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Admin seeding disabled; skipping bootstrap user");
            return;
        }

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user {} already exists; nothing to seed", adminEmail);
            return;
        }

        UserCredential adminUser = UserCredential.builder()
                .fullName(adminFullName)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(adminRole == null ? "ROLE_ADMIN" : adminRole.toUpperCase())
                .active(true)
                .build();

        userRepository.save(adminUser);
        log.warn("Seeded default admin user {}. Change the password immediately in production environments.", adminEmail);
    }
}
