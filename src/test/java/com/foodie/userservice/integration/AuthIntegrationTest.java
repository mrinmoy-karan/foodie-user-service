package com.foodie.userservice.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.foodie.userservice.models.User;
import com.foodie.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User inactiveUser = new User();
        inactiveUser.setName("Inactive User");
        inactiveUser.setEmail("deactivated@office.com");
        inactiveUser.setMobile("1234567890"); // <--- ADD THIS LINE
        inactiveUser.setPassword(passwordEncoder.encode("password123"));
        inactiveUser.setActive(false);

        // Also, if your entity requires a 'createdAt' timestamp manually:
        // inactiveUser.setCreatedAt(LocalDateTime.now());

        userRepository.save(inactiveUser);
    }

    @Test
    @DisplayName("Login Fail: Should return 401 when user is deactivated")
    void login_ShouldFail_WhenUserIsInactive() throws Exception {
        String loginJson = "{\"email\":\"deactivated@office.com\", \"password\":\"password123\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                // AuthIntegrationTest.java line 59
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("Unauthorized"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.error").value("Invalid email or password")); // Match the handler above
    }


    @Test
    @DisplayName("Login Failure: Should return 400 when email is malformed")
    void login_ShouldFail_WhenEmailIsInvalid() throws Exception {
        String invalidJson = "{\"email\":\"not-an-email\", \"password\":\"short\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Bad Request"))
                // Checking nested errors
                .andExpect(jsonPath("$.errors.email").value("Invalid email format"))
                .andExpect(jsonPath("$.errors.password").value("Password must be at least 8 characters"));
    }


    @Test
    @DisplayName("Registration Failure: Should return 400 when mobile is invalid")
    void register_ShouldFail_WhenMobileIsInvalid() throws Exception {
        String invalidRegisterJson = """
        {
            "name": "J",
            "email": "wrong-email",
            "mobile": "123",
            "password": "short"
        }
        """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRegisterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Bad Request"))
                // Check that multiple fields are caught at once
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").value("Invalid email format"))
                .andExpect(jsonPath("$.errors.mobile").value("Mobile number must be exactly 10 digits"))
                .andExpect(jsonPath("$.errors.password").exists());
    }
}