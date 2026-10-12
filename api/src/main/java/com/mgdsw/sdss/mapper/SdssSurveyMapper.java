package com.mgdsw.sdss.mapper;

import com.mgdsw.sdss.dto.surveys.SdssSurveyResponse;
import com.mgdsw.sdss.dto.surveys.enduser.PublicEndUserMultiRoundSurveyResponse;
import com.mgdsw.sdss.dto.surveys.enduser.PublicEndUserSurveyResponse;
import com.mgdsw.sdss.entity.surveys.SdssSurvey;
import com.mgdsw.sdss.entity.surveys.SdssSurveyInvitation;
import com.mgdsw.sdss.entity.surveys.SdssSurveyRound;
import com.mgdsw.sdss.service.SdssSurveyRoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 12 Nov 2024
 */

@Component
@RequiredArgsConstructor
public class SdssSurveyMapper {

    public final InvitedSurveyUserMapper invitedSurveyUserMapper;
    private final SdssSurveyRoundService sdssSurveyRoundService;
    private final SdssSurveyRoundMapper sdssSurveyRoundMapper;

    public SdssSurveyResponse toResponse(SdssSurvey entity) {
        if (entity == null) {
            return null;
        }
        return SdssSurveyResponse.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .titleAr(entity.getTitleAr())
                .titleEn(entity.getTitleEn())
                .descriptionAr(entity.getDescriptionAr())
                .descriptionEn(entity.getDescriptionEn())
                .active(entity.getActive())
                .status(entity.getStatus())
                .manualHandle(entity.getManualHandle())
                .jobList(entity.getJobList())
                .allowedGrpList(entity.getAllowedGrpList())
                .departmentList(entity.getDepartmentList())
                .rounds(sdssSurveyRoundService.findBySurvey(entity).stream()
                        .map(sdssSurveyRoundMapper::toResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public PublicEndUserSurveyResponse toEndUerPublicResponse(SdssSurvey sdssSurvey, SdssSurveyInvitation sdssSurveyInvitation) {
        return PublicEndUserSurveyResponse.builder()
                .invitedUserResponse(invitedSurveyUserMapper.toPublicEndUserResponse(sdssSurveyInvitation.getInvitedUser()))
                .activeRoundResponse(sdssSurveyRoundMapper.toPublicRoundResponse(sdssSurveyInvitation.getSurveyRound()))
                .descriptionEn(sdssSurvey.getDescriptionEn())
                .descriptionAr(sdssSurvey.getDescriptionAr())
                .titleAr(sdssSurvey.getTitleAr())
                .titleEn(sdssSurvey.getTitleEn())
                .id(sdssSurvey.getId())
                .build();
    }

    public PublicEndUserMultiRoundSurveyResponse toPublicEndUserMultiRoundSurveyResponse(SdssSurvey sdssSurvey, SdssSurveyInvitation sdssSurveyInvitation, List<SdssSurveyRound> completedRoundList) {
        return PublicEndUserMultiRoundSurveyResponse.builder()
                .activeRoundResponseList(completedRoundList.stream().map(sdssSurveyRoundMapper::toPublicRoundResponse).collect(Collectors.toList()))
                .invitedUserResponse(invitedSurveyUserMapper.toPublicEndUserResponse(sdssSurveyInvitation.getInvitedUser()))
                .descriptionEn(sdssSurvey.getDescriptionEn())
                .descriptionAr(sdssSurvey.getDescriptionAr())
                .titleAr(sdssSurvey.getTitleAr())
                .titleEn(sdssSurvey.getTitleEn())
                .id(sdssSurvey.getId())
                .build();
    }

    public PublicEndUserSurveyResponse toEndUerPublicResponse(SdssSurvey sdssSurvey, SdssSurveyRound surveyRound) {
        return PublicEndUserSurveyResponse.builder()
                .activeRoundResponse(sdssSurveyRoundMapper.toPublicRoundResponse(surveyRound))
                .descriptionEn(sdssSurvey.getDescriptionEn())
                .descriptionAr(sdssSurvey.getDescriptionAr())
                .titleAr(sdssSurvey.getTitleAr())
                .titleEn(sdssSurvey.getTitleEn())
                .id(sdssSurvey.getId())
                .build();
    }
}
