package com.doconnect.questionservice.service;

import com.doconnect.questionservice.entity.Question;

/**
 * Placeholder contract for future email/notification integrations.
 */
public interface NotificationService {

    void notifyQuestionCreated(Question question);

    void notifyQuestionApproved(Question question);

    void notifyQuestionResolutionChanged(Question question);
}
