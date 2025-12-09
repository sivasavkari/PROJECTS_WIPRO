package com.doconnect.answerservice.service;

import com.doconnect.answerservice.dto.AnswerApprovalRequest;
import com.doconnect.answerservice.dto.AnswerRequest;
import com.doconnect.answerservice.dto.AnswerResponse;
import com.doconnect.answerservice.dto.LikeUpdateRequest;
import com.doconnect.answerservice.entity.Answer;
import com.doconnect.answerservice.mapper.AnswerMapper;
import com.doconnect.answerservice.repository.AnswerRepository;
import com.doconnect.answerservice.service.impl.AnswerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnswerServiceImplTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private AnswerMapper answerMapper;

    @InjectMocks
    private AnswerServiceImpl answerService;

    @Test
    void updateAnswer_whenRequesterNotOwnerAndNotAdmin_throwsAccessDenied() {
        Answer answer = buildAnswer();
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        AnswerRequest request = new AnswerRequest();
        request.setQuestionId(answer.getQuestionId());
        request.setContent("updated content");

        assertThrows(AccessDeniedException.class,
                () -> answerService.updateAnswer(1L, request, "other-user", false));

        verify(answerRepository, never()).save(any());
    }

    @Test
    void updateApproval_marksAnswerAndReturnsResponse() {
        Answer answer = buildAnswer();
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
        when(answerRepository.save(answer)).thenReturn(answer);

        AnswerApprovalRequest request = new AnswerApprovalRequest();
        request.setApproved(true);
        request.setReviewerId("admin-user");

        AnswerResponse expected = AnswerResponse.builder()
                .id(answer.getId())
                .approved(true)
                .approvedBy("admin-user")
                .build();
        when(answerMapper.toResponse(answer)).thenReturn(expected);

        AnswerResponse response = answerService.updateApproval(1L, request);

        assertThat(answer.isApproved()).isTrue();
        assertThat(answer.getApprovedBy()).isEqualTo("admin-user");
        assertThat(response).isSameAs(expected);
    }

    @Test
    void updateLikes_neverDropsBelowZero() {
        Answer answer = buildAnswer();
        answer.setLikeCount(1);
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
        when(answerRepository.save(answer)).thenReturn(answer);

        LikeUpdateRequest request = new LikeUpdateRequest();
        request.setDelta(-5);

        AnswerResponse expected = AnswerResponse.builder()
                .id(answer.getId())
                .likeCount(0)
                .build();
        when(answerMapper.toResponse(answer)).thenReturn(expected);

        AnswerResponse response = answerService.updateLikes(1L, request);

        assertThat(answer.getLikeCount()).isZero();
        assertThat(response).isSameAs(expected);
    }

    private Answer buildAnswer() {
        Answer answer = new Answer();
        answer.setId(1L);
        answer.setQuestionId(99L);
        answer.setAnsweredBy("owner-user");
        answer.setContent("original content");
        return answer;
    }
}
