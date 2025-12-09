package com.doconnect.questionservice.service.impl;

import com.doconnect.questionservice.entity.Question;
import com.doconnect.questionservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Minimal notification stub so downstream email delivery can be wired later.
 */
@Slf4j
@Service
public class LoggingNotificationService implements NotificationService {

    @Override
    public void notifyQuestionCreated(Question question) {
        log.info("[notification] New question posted id={} title={} askedBy={}",
                question.getId(), question.getTitle(), question.getAskedBy());
    }

    @Override
    public void notifyQuestionApproved(Question question) {
        log.info("[notification] Question approved id={} reviewer={} approved={}, resolved={} ",
                question.getId(), question.getApprovedBy(), question.isApproved(), question.isResolved());
    }

    @Override
    public void notifyQuestionResolutionChanged(Question question) {
        String state = question.isResolved() ? "resolved" : "reopened";
        log.info("[notification] Question {} id={} resolver={} resolvedAt={}",
                state, question.getId(), question.getResolvedBy(), question.getResolvedAt());
    }
}
