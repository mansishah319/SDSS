/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.challenge;

import com.mgdsw.sdss.service.UserInvitationService;
import com.mgdsw.sdss.service.ValidatorsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Log4j2
@Component
@RequiredArgsConstructor
public class ChallengeInvitationEmailListener {

    private final UserInvitationService userInvitationService;
    private final ValidatorsService validatorsService;

    @Async("challengeEmailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendInvitations(ChallengeInvitationEmailEvent event) {
        try {
            userInvitationService.sendPendingChallengeInvitations(event.getChallengeId(), event.getReminderDateTimes());
            validatorsService.sendPendingChallengeValidatorInvitations(event.getChallengeId());
        } catch (Exception exception) {
            log.error("Failed to send background challenge invitations for challenge [{}]: {}",
                    event.getChallengeId(), exception.getMessage(), exception);
        }
    }
}
