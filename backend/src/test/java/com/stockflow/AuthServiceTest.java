package com.stockflow;

import com.stockflow.dto.AuthResponse;
import com.stockflow.dto.LoginRequest;
import com.stockflow.dto.SignupRequest;
import com.stockflow.exception.BadRequestException;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void testSignup() {
        SignupRequest request = new SignupRequest("test@example.com", "password123", "password123", "TestOrg");
        AuthResponse response = authService.signup(request);

        assertNotNull(response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("TestOrg", response.getOrganizationName());
        assertNotNull(response.getOrganizationId());
    }

    @Test
    void testSignupDuplicateEmail() {
        SignupRequest request1 = new SignupRequest("dup@example.com", "password123", "password123", "Org1");
        authService.signup(request1);

        SignupRequest request2 = new SignupRequest("dup@example.com", "password123", "password123", "Org2");
        assertThrows(DuplicateResourceException.class, () -> authService.signup(request2));
    }

    @Test
    void testSignupPasswordMismatch() {
        SignupRequest request = new SignupRequest("mismatch@example.com", "password123", "different", "MismatchOrg");
        assertThrows(BadRequestException.class, () -> authService.signup(request));
    }

    @Test
    void testLogin() {
        SignupRequest signupRequest = new SignupRequest("login@example.com", "password123", "password123", "LoginOrg");
        authService.signup(signupRequest);

        LoginRequest loginRequest = new LoginRequest("login@example.com", "password123");
        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response.getToken());
        assertEquals("login@example.com", response.getEmail());
        assertEquals("LoginOrg", response.getOrganizationName());
    }

    @Test
    void testLoginWrongPassword() {
        SignupRequest signupRequest = new SignupRequest("wrong@example.com", "password123", "password123", "WrongOrg");
        authService.signup(signupRequest);

        LoginRequest loginRequest = new LoginRequest("wrong@example.com", "wrongpassword");
        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    }
}
