/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.survey;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.entity.survey.CommanderSurvey;
import com.mgdsw.sdss.repository.survey.CommanderSurveyRepository;
import com.mgdsw.sdss.service.CommanderSurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Aug, 2023
 */

@Service
@RequiredArgsConstructor
public class CommanderSurveyServiceImpl extends AbstractBaseServiceImpl<Integer, CommanderSurvey> implements CommanderSurveyService {

    private final CommanderSurveyRepository commanderSurveyRepository;

    @Override
    protected CommanderSurveyRepository repository() {
        return commanderSurveyRepository;
    }
}