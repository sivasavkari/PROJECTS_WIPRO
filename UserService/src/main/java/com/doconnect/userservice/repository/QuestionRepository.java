package com.doconnect.userservice.repository;

import com.doconnect.userservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByApprovedTrue();
    List<Question> findByApprovedFalse();

    @Query("SELECT q FROM Question q WHERE LOWER(q.title) LIKE %:keyword% OR LOWER(q.description) LIKE %:keyword%")
    List<Question> searchQuestions(String keyword);
}



