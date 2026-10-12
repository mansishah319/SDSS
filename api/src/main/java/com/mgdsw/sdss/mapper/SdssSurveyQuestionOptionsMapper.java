/**
 * **************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.mapper;

import com.mgdsw.sdss.dto.surveys.enduser.PublicOptionsResponse;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestionOptions;
import org.springframework.stereotype.Component;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 13 Nov 2024
 */

@Component
public class SdssSurveyQuestionOptionsMapper {

    public SdssSurveyQuestionOptionsResponse toResponse(SdssSurveyQuestionOptions entity) {
        if (entity == null) {
            return null;
        }
        return SdssSurveyQuestionOptionsResponse.builder()
                .id(entity.getId())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .optionEn(entity.getOptionEn())
                .optionAr(entity.getOptionAr())
                .build();
    }

    public PublicOptionsResponse toPublicOptionsResponse(SdssSurveyQuestionOptions entity) {
        if (entity == null) {
            return null;
        }
        return PublicOptionsResponse.builder()
                .optionEn(entity.getOptionEn())
                .optionAr(entity.getOptionAr())
                .id(entity.getId())
                .build();
    }
}
