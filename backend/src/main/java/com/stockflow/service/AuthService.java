package com.stockflow.service;

import com.stockflow.dto.AuthResponse;
import com.stockflow.dto.LoginRequest;
import com.stockflow.dto.SignupRequest;
import com.stockflow.entity.Organization;
import com.stockflow.entity.OrganizationSettings;
import com.stockflow.entity.User;
import com.stockflow.exception.BadRequestException;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.exception.ResourceNotFoundException;
import com.stockflow.repository.OrganizationRepository;
import com.stockflow.repository.OrganizationSettingsRepository;
import com.stockflow.repository.UserRepository;
import com.stockflow.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationSettingsRepository settingsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, OrganizationRepository organizationRepository,
                       OrganizationSettingsRepository settingsRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.settingsRepository = settingsRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (organizationRepository.findByName(request.getOrganizationName()).isPresent()) {
            throw new DuplicateResourceException("Organization name already exists");
        }

        Organization organization = new Organization(request.getOrganizationName());
        organization = organizationRepository.save(organization);

        OrganizationSettings settings = new OrganizationSettings(organization);
        settingsRepository.save(settings);

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                organization
        );
        user = userRepository.save(user);

        String token = tokenProvider.generateToken(user.getEmail(), organization.getId(), user.getId());

        return new AuthResponse(token, user.getEmail(), organization.getName(), organization.getId());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        String token = tokenProvider.generateToken(user.getEmail(), user.getOrganization().getId(), user.getId());

        return new AuthResponse(token, user.getEmail(),
                user.getOrganization().getName(), user.getOrganization().getId());
    }
}
