/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.service.impl.surveys;

import com.mgdsw.sdss.common.base.service.impl.AbstractBaseServiceImpl;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsRequest;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsResponse;
import com.mgdsw.sdss.dto.surveys.questions.options.UpdateSurveyQuestionOptionsRequest;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestionOptions;
import com.mgdsw.sdss.repository.surveys.SdssSurveyQuestionOptionsRepository;
import com.mgdsw.sdss.service.SdssSurveyQuestionOptionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2024
 */

@Service
@RequiredArgsConstructor
public class SdssSurveyQuestionOptionsServiceImpl extends AbstractBaseServiceImpl<Integer, SdssSurveyQuestionOptions> implements SdssSurveyQuestionOptionsService {

    private final SdssSurveyQuestionOptionsRepository repository;

    @Override
    protected SdssSurveyQuestionOptionsRepository repository() {
        return repository;
    }

    @Override
    @Transactional
    public SdssSurveyQuestionOptionsResponse createNewOptionForSpecificQuestion(SdssSurveyQuestion surveyQuestion, SdssSurveyQuestionOptionsRequest optionsRequest) {
        return this.createSingleOption(surveyQuestion, optionsRequest);
    }

    @Override
    @Transactional
    public SdssSurveyQuestionOptionsResponse updateOptionForSpecificQuestion(SdssSurveyQuestion surveyQuestion, UpdateSurveyQuestionOptionsRequest optionsRequest) {
        return this.updateSingleOption(surveyQuestion, optionsRequest);
    }

    @Override
    @Transactional
    public void createNewListOfOptionsForSpecificQuestion(SdssSurveyQuestion surveyQuestion, List<SdssSurveyQuestionOptionsRequest> optionsRequestList) {
        if (!optionsRequestList.isEmpty()) {
            optionsRequestList.forEach(option -> this.createSingleOption(surveyQuestion, option));
        }
    }

    @Override
    public void updateListOfOptionsForSpecificQuestion(SdssSurveyQuestion updatedSurveyQuestion, List<UpdateSurveyQuestionOptionsRequest> optionsRequestList) {
        if (!optionsRequestList.isEmpty()) {
            optionsRequestList.forEach(option -> this.updateSingleOption(updatedSurveyQuestion, option));
        }
    }

    private SdssSurveyQuestionOptionsResponse createSingleOption(SdssSurveyQuestion surveyQuestion, SdssSurveyQuestionOptionsRequest optionsRequest) {
        SdssSurveyQuestionOptions questionOptions = repository.save(
                SdssSurveyQuestionOptions.builder()
                        .optionAr(optionsRequest.getOptionAr())
                        .optionEn(optionsRequest.getOptionEn())
                        .question(surveyQuestion)
                        .build());
        return modelMapper.map(questionOptions, SdssSurveyQuestionOptionsResponse.class);
    }

    private SdssSurveyQuestionOptionsResponse updateSingleOption(SdssSurveyQuestion updatedSurveyQuestion, UpdateSurveyQuestionOptionsRequest optionsRequest) {
        SdssSurveyQuestionOptions surveyQuestionOptions = super.findOptionalById(optionsRequest.getId()).orElse(null);
        if (Objects.nonNull(surveyQuestionOptions)) {
            this.updateIfExist(surveyQuestionOptions, optionsRequest);
        } else {
            surveyQuestionOptions =
                    SdssSurveyQuestionOptions.builder()
                            .optionAr(optionsRequest.getOptionAr())
                            .optionEn(optionsRequest.getOptionEn())
                            .question(updatedSurveyQuestion)
                            .build();
        }
        SdssSurveyQuestionOptions updatedSurveyOption = repository.save(surveyQuestionOptions);
        return modelMapper.map(updatedSurveyOption, SdssSurveyQuestionOptionsResponse.class);
    }

    private void updateIfExist(SdssSurveyQuestionOptions surveyQuestionOptions, UpdateSurveyQuestionOptionsRequest optionsRequest) {
        if (Objects.nonNull(optionsRequest.getOptionAr()))
            surveyQuestionOptions.setOptionAr(optionsRequest.getOptionAr());
        if (Objects.nonNull(optionsRequest.getOptionEn()))
            surveyQuestionOptions.setOptionEn(optionsRequest.getOptionEn());
    }
}
