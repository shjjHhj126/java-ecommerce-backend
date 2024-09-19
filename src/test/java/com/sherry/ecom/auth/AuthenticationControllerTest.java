package com.sherry.ecom.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherry.ecom.user.Role;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private CommandLineRunner commandLineRunner;

    @Test
    void testRegister() throws Exception {
        // Test data
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Test")
                .lastName("User")
                .email("testuser@mail.com")
                .password("password")
                .build();

        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .build();

        // Mock the service response
        when(authenticationService.register(any(RegisterRequest.class)))
                .thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect( jsonPath("$.accessToken").value("test-access-token"))
                .andExpect( jsonPath("$.refreshToken").value("test-refresh-token"));
    }

    @Test
    void testAuthenticate() throws Exception {
        // Test data
        AuthenticationRequest authRequest = new AuthenticationRequest("testuser@mail.com", "password");

        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .build();

        // Mock the service response
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(authResponse);

        // Perform the POST request
        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect( jsonPath("$.accessToken").value("test-access-token"))
                .andExpect( jsonPath("$.refreshToken").value("test-refresh-token"));
    }

    // Todo : add a test for the refresh-token endpoint
}



