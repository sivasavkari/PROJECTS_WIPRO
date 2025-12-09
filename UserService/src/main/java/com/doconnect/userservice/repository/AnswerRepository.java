package com.doconnect.userservice.repository;

import com.doconnect.userservice.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestionId(Long questionId);

    // NEW → Only approved answers
    List<Answer> findByQuestionIdAndApprovedTrue(Long questionId);

    // For admin
    List<Answer> findByApprovedFalse();
}


