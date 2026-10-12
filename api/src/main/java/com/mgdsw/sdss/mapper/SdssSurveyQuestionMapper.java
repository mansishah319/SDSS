package com.mgdsw.sdss.mapper;

import com.mgdsw.sdss.dto.surveys.enduser.PublicOptionsResponse;
import com.mgdsw.sdss.dto.surveys.enduser.PublicQuestionResponse;
import com.mgdsw.sdss.dto.surveys.questions.SdssSurveyQuestionRequest;
import com.mgdsw.sdss.dto.surveys.questions.SdssSurveyQuestionResponse;
import com.mgdsw.sdss.dto.surveys.questions.UpdateSurveyQuestionRequest;
import com.mgdsw.sdss.dto.surveys.questions.options.SdssSurveyQuestionOptionsResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyQuestion;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.enums.QuestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between SdssSurveyQuestion entity and DTOs.
 * Provides methods for mapping to entity, updating entity fields, and mapping to response DTO.
 * This class does not map null values when updating an entity.
 *
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 12 Nov 2024
 */

@Component
@RequiredArgsConstructor
public class SdssSurveyQuestionMapper {

    private final SdssSurveyQuestionOptionsMapper sdssSurveyQuestionOptionsMapper;

    /**
     * Maps the provided request to a new SdssSurveyQuestion entity.
     *
     * @param questionRequest The request object containing question data.
     * @param survey          The survey entity to which the question belongs.
     * @param surveyRound     The survey round entity to which the question belongs.
     * @return A new SdssSurveyQuestion entity populated with the data from the request.
     */
    public SdssSurveyQuestion mapToEntity(SdssSurveyQuestionRequest questionRequest, SdssSurvey survey, SdssSurveyRound surveyRound) {
        return SdssSurveyQuestion.builder()
                .survey(survey)
                .round(surveyRound)
                .titleAr(questionRequest.getTitleAr())
                .titleEn(questionRequest.getTitleEn())
                .descriptionAr(questionRequest.getDescriptionAr())
                .descriptionEn(questionRequest.getDescriptionEn())
                .showPublicQuestions(questionRequest.getShowPublicQuestions())
                .questionType(QuestionType.fromString(questionRequest.getQuestionType()))
                .build();
    }

    /**
     * Updates the fields of an existing SdssSurveyQuestion entity from the provided request.
     * Only non-null fields in the request will be updated.
     *
     * @param questionRequest The request object containing updated question data.
     * @param existingEntity  The existing SdssSurveyQuestion entity to update.
     * @return The updated SdssSurveyQuestion entity.
     */
    public SdssSurveyQuestion updateEntity(UpdateSurveyQuestionRequest questionRequest, SdssSurveyQuestion existingEntity) {
        if (questionRequest.getTitleAr() != null) {
            existingEntity.setTitleAr(questionRequest.getTitleAr());
        }
        if (questionRequest.getTitleEn() != null) {
            existingEntity.setTitleEn(questionRequest.getTitleEn());
        }
        if (questionRequest.getDescriptionAr() != null) {
            existingEntity.setDescriptionAr(questionRequest.getDescriptionAr());
        }
        if (questionRequest.getDescriptionEn() != null) {
            existingEntity.setDescriptionEn(questionRequest.getDescriptionEn());
        }
        if (questionRequest.getQuestionType() != null) {
            existingEntity.setQuestionType(QuestionType.fromString(questionRequest.getQuestionType()));
        }

        if (questionRequest.getShowPublicQuestions() != null) {
            existingEntity.setShowPublicQuestions(questionRequest.getShowPublicQuestions());
        }

        return existingEntity;
    }

    /**
     * The Maps provided SdssSurveyQuestion entity to a response DTO.
     *
     * @param entity The SdssSurveyQuestion entity to map.
     * @return A response DTO populated with the data from the entity.
     */
    public SdssSurveyQuestionResponse toResponse(SdssSurveyQuestion entity) {
        if (entity == null) {
            return null;
        }

        List<SdssSurveyQuestionOptionsResponse> options = entity.getOptions().stream()
                .map(sdssSurveyQuestionOptionsMapper::toResponse)
                .collect(Collectors.toList());

        return SdssSurveyQuestionResponse.builder()
                .id(entity.getId())
                .titleEn(entity.getTitleEn())
                .titleAr(entity.getTitleAr())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .questionType(entity.getQuestionType())
                .descriptionAr(entity.getDescriptionAr())
                .descriptionEn(entity.getDescriptionEn())
                .showPublicQuestions(entity.getShowPublicQuestions())
                .options(options)
                .build();
    }

    public PublicQuestionResponse toPublicQuestionResponse(SdssSurveyQuestion entity) {
        if (entity == null) {
            return null;
        }
        List<PublicOptionsResponse> options = entity.getOptions().stream()
                .map(sdssSurveyQuestionOptionsMapper::toPublicOptionsResponse)
                .collect(Collectors.toList());

        return PublicQuestionResponse.builder()
                .showPublicQuestions(entity.getShowPublicQuestions())
                .descriptionAr(entity.getDescriptionAr())
                .descriptionEn(entity.getDescriptionEn())
                .questionType(entity.getQuestionType())
                .titleEn(entity.getTitleEn())
                .titleAr(entity.getTitleAr())
                .id(entity.getId())
                .options(options)
                .build();
    }
}
