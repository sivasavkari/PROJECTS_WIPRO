package com.doconnect.questionservice.repository;

import com.doconnect.questionservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByApprovedTrueOrderByCreatedAtDesc();

    List<Question> findByApprovedFalseOrderByCreatedAtAsc();

    List<Question> findByApprovedTrueAndResolvedTrueOrderByResolvedAtDesc();

    List<Question> findByTopicIgnoreCaseAndApprovedTrue(String topic);

    List<Question> findByAskedByAndApprovedTrue(String askedBy);

    @Query("""
            SELECT DISTINCT q FROM Question q LEFT JOIN q.tags t
            WHERE (
                LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(q.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(q.topic) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(q.askedBy) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                (t IS NOT NULL AND LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%')))
            )
              AND q.approved = TRUE
            """)
    List<Question> searchByKeyword(@Param("keyword") String keyword);
}
