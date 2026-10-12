/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.challenge;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.common.utils.DpDateUtil;
import com.mgdsw.sdss.entity.challenge.ChallengeReminder;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.enums.Operations;
import com.mgdsw.sdss.repository.challenge.ChallengeReminderRepository;
import com.mgdsw.sdss.service.ChallengeReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.mgdsw.sdss.common.helper.util.FilesUtil.DATE_TIME_FORMATTER;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class ChallengeReminderServiceImpl extends AbstractBaseServiceImpl<Integer, ChallengeReminder> implements ChallengeReminderService {

    private final ChallengeReminderRepository challengeReminderRepository;

    @Override
    protected ChallengeReminderRepository repository() {
        return challengeReminderRepository;
    }

    @Override
    public void create(List<String> challengeRemindersDateTimeList, Challenges challenges, Operations operations) {
        if (operations.equals(Operations.UPDATE))
            challengeReminderRepository.deleteByChallenges(challenges);

        List<ChallengeReminder> challengeReminders = challengeRemindersDateTimeList.stream()
                .map(dateTimeString -> ChallengeReminder.builder()
                        .reminderTime(DpDateUtil.parseDateTime(dateTimeString, DATE_TIME_FORMATTER))
                        .isSent(Boolean.FALSE)
                        .challenges(challenges)
                        .build())
                .collect(Collectors.toList());
        repository().saveAll(challengeReminders);
    }

    @Override
    @Transactional
    public void removeAllByChallenge(Challenges challengeNeedToRemove) {
        challengeReminderRepository.deleteByChallenges(challengeNeedToRemove);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeReminder> findByChallenges(Challenges challenges) {
        return challengeReminderRepository.findByChallenges(challenges);
    }
}