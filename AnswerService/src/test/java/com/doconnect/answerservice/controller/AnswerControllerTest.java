package com.doconnect.answerservice.controller;

import com.doconnect.answerservice.config.SecurityConfig;
import com.doconnect.answerservice.controller.advice.GlobalExceptionHandler;
import com.doconnect.answerservice.dto.AnswerApprovalRequest;
import com.doconnect.answerservice.dto.AnswerRequest;
import com.doconnect.answerservice.dto.AnswerResponse;
import com.doconnect.answerservice.dto.LikeUpdateRequest;
import com.doconnect.answerservice.security.JwtAuthenticationFilter;
import com.doconnect.answerservice.service.AnswerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AnswerController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(username = "alice", authorities = {"ROLE_USER"})
    void createAnswer_returnsCreatedResponse() throws Exception {
        AnswerResponse response = AnswerResponse.builder()
                .id(42L)
                .questionId(7L)
                .answeredBy("alice")
                .content("Answer body")
                .likeCount(0)
                .approved(false)
                .build();
        when(answerService.createAnswer(any(AnswerRequest.class), eq("alice"))).thenReturn(response);

        AnswerRequest request = new AnswerRequest();
        request.setQuestionId(7L);
        request.setContent("Answer body");

        mockMvc.perform(post("/api/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.answeredBy").value("alice"));
    }

    @Test
    @WithMockUser(username = "bob", authorities = {"ROLE_USER"})
    void updateLikes_returnsServicePayload() throws Exception {
        AnswerResponse response = AnswerResponse.builder()
                .id(10L)
                .likeCount(3)
                .build();
        when(answerService.updateLikes(eq(10L), any(LikeUpdateRequest.class))).thenReturn(response);

        LikeUpdateRequest request = new LikeUpdateRequest();
        request.setDelta(1);

        mockMvc.perform(patch("/api/answers/10/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likeCount").value(3));

        verify(answerService).updateLikes(eq(10L), any(LikeUpdateRequest.class));
    }

    @Test
    @WithMockUser(username = "moderator", authorities = {"ROLE_ADMIN"})
    void updateApproval_missingReviewer_returnsBadRequest() throws Exception {
        AnswerApprovalRequest request = new AnswerApprovalRequest();
        request.setApproved(true);

        mockMvc.perform(patch("/api/answers/5/approval")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
