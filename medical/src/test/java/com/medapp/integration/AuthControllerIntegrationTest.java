package com.medapp.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void otpLoginFlowShouldIssueAccessAndRefreshTokens() throws Exception {
        String otpReqBody = """
                {
                  "phone":"9876543210"
                }
                """;

        String otpResponse = mockMvc.perform(post("/api/v1/auth/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(otpReqBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode otpJson = objectMapper.readTree(otpResponse);
        String challengeId = otpJson.path("data").path("challengeId").asText();
        String mockOtp = otpJson.path("data").path("mockOtp").asText();

        String loginRequest = """
                {
                  "challengeId":"%s",
                  "phone":"9876543210",
                  "otp":"%s",
                  "role":"ROLE_USER"
                }
                """.formatted(challengeId, mockOtp);

        String loginResponse = mockMvc.perform(post("/api/v1/auth/otp/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginResponse);
        assertThat(loginJson.path("data").path("accessToken").asText()).isNotBlank();
        assertThat(loginJson.path("data").path("refreshToken").asText()).isNotBlank();
    }
}
