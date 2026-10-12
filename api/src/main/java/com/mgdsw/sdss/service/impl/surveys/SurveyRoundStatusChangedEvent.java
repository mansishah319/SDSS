/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.surveys;

import lombok.Value;

/**
 * Contains identifiers only so background processing never depends on
 * request-scoped or detached JPA state.
 */
@Value
public class SurveyRoundStatusChangedEvent {

    Integer surveyId;
    Integer roundId;
    String baseUrl;
}
