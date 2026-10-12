/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.surveys;

import com.mgdsw.sdss.service.SdssSurveyInvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Log4j2
@Component
@RequiredArgsConstructor
public class SurveyRoundStatusChangedEmailListener {

    private final SdssSurveyInvitationService surveyInvitationService;

    @Async("surveyEmailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendInvitations(SurveyRoundStatusChangedEvent event) {
        try {
            surveyInvitationService.sendManualInvitation(
                    event.getSurveyId(), event.getRoundId(), event.getBaseUrl());
        } catch (Exception exception) {
            log.error("Failed to send background invitations for survey [{}], round [{}]: {}",
                    event.getSurveyId(), event.getRoundId(), exception.getMessage(), exception);
        }
    }
}
