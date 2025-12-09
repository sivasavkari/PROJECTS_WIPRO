package com.doconnect.answerservice.feign;

import com.doconnect.answerservice.feign.dto.QuestionSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "question-service", configuration = com.doconnect.answerservice.config.FeignConfig.class)
public interface QuestionServiceClient {

    @GetMapping("/api/questions/{id}")
    QuestionSummary getQuestionById(@PathVariable("id") Long id);
}
