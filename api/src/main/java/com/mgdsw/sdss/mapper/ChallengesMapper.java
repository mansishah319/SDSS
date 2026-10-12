/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.mapper;

import com.mgdsw.sdss.core.projection.DepartmentHierarchyProjection;
import com.mgdsw.sdss.core.service.DepartmentService;
import com.mgdsw.sdss.dto.challenge.ChallengeDocsResponse;
import com.mgdsw.sdss.dto.challenge.ChallengesResponse;
import com.mgdsw.sdss.dto.reminder.ChallengeReminderResponse;
import com.mgdsw.sdss.dto.strategic.directions.StrategicDirectionResponse;
import com.mgdsw.sdss.dto.strategic.goals.FutureStrategicGoalsResponse;
import com.mgdsw.sdss.dto.strategic.goals.StrategicGoalsResponse;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;
import com.mgdsw.sdss.entity.challenge.ChallengeDocuments;
import com.mgdsw.sdss.entity.challenge.ChallengeReminder;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.service.ChallengeReminderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn 9月, 2024
 */

@Component
@RequiredArgsConstructor
public class ChallengesMapper {

    private final ModelMapper modelMapper;
    private final ChallengeReminderService challengeReminderService;
    private final DepartmentService departmentService;

    public ChallengesResponse mapToResponse(Challenges challenge) {
        if (challenge == null) {
            return null;
        }

        // Create the response object
        ChallengesResponse response = new ChallengesResponse();

        String departmentId = challenge.getDepartmentId();
        DepartmentHierarchyProjection departmentHierarchy = getDepartmentHierarchy(departmentId);

        response.setId(challenge.getId());
        response.setCreatedAt(challenge.getCreatedAt());
        response.setCreatedBy(challenge.getCreatedBy());
        response.setUpdatedAt(challenge.getUpdatedAt());
        response.setUpdatedBy(challenge.getUpdatedBy());

        response.setIsFinal(challenge.getIsFinal());
        response.setQueLevel(challenge.getQueLevel());

        // Simple fields
        response.setTitleAr(challenge.getTitleAr());
        response.setTitleEn(challenge.getTitleEn());
        response.setDescriptionAr(challenge.getDescriptionAr());
        response.setDescriptionEn(challenge.getDescriptionEn());
        response.setStatus(challenge.getStatus() != null ? challenge.getStatus().name() : null);
        response.setSurveyStatus(challenge.getSurveyStatus() != null ? challenge.getSurveyStatus().name() : null);
        response.setVisibility(challenge.getVisibility() != null ? challenge.getVisibility().name() : null);
        response.setAllowImmediateAccess(challenge.getAllowImmediateAccess());

        // Department-related fields
        response.setSectorId(Objects.nonNull(departmentHierarchy) ? departmentHierarchy.getSectorId() : null);
        response.setSectorName(Objects.nonNull(departmentHierarchy) ? departmentHierarchy.getSectorName() : null);
        response.setDepartmentId(departmentId);
        response.setDepartmentName(challenge.getDepartmentName());
        response.setSubDepartmentId(challenge.getSubDepartmentId());
        response.setSubDepartmentName(challenge.getSubDepartmentName());
        response.setSectionId(challenge.getSectionId());
        response.setSectionName(challenge.getSectionName());

        // Time-related fields
        response.setStartTime(challenge.getStartTime());
        response.setEndTime(challenge.getEndTime());

        // Map moderator and assistant moderator
        if (challenge.getModerator() != null) {
            response.setModerator(modelMapper.map(challenge.getModerator(), UserDropDownListResponse.class));
        }

        if (challenge.getAssistantModerator() != null) {
            response.setAssistantModerator(modelMapper.map(challenge.getAssistantModerator(), UserDropDownListResponse.class));
        }

        // Map strategic goals and direction
        if (challenge.getStrategicGoals() != null) {
            response.setStrategicGoals(modelMapper.map(challenge.getStrategicGoals(), StrategicGoalsResponse.class));
        }

        if (challenge.getStrategicDirection() != null) {
            response.setStrategicDirection(modelMapper.map(challenge.getStrategicDirection(), StrategicDirectionResponse.class));
        }

        // Map future strategic direction and goals
        if (challenge.getFutureStrategicGoals() != null) {
            response.setFutureStrategicGoals(modelMapper.map(challenge.getFutureStrategicGoals(), FutureStrategicGoalsResponse.class));
        }

        if (challenge.getFutureStrategicDirection() != null) {
            response.setFutureStrategicDirection(modelMapper.map(challenge.getFutureStrategicDirection(), StrategicDirectionResponse.class));
        }

        // Map documents, reminders, solutions, etc. (list mapping)
        if (challenge.getChallengeDocuments() != null) {
            response.setChallengeDocsResponseList(this.mapChallengeDocs(challenge.getChallengeDocuments()));
        }

        response.setChallengeReminders(mapChallengeReminders(challengeReminderService.findByChallenges(challenge)));

        return response;
    }

    private List<ChallengeDocsResponse> mapChallengeDocs(List<ChallengeDocuments> documents) {
        if (documents.isEmpty()) {
            return new ArrayList<>();
        } else {
            return documents.stream().map(doc -> modelMapper.map(doc, ChallengeDocsResponse.class)).collect(Collectors.toList());
        }
    }

    private List<ChallengeReminderResponse> mapChallengeReminders(List<ChallengeReminder> reminders) {
        if (reminders.isEmpty()) {
            return new ArrayList<>();
        } else {
            return reminders.stream().map(reminder -> modelMapper.map(reminder, ChallengeReminderResponse.class)).collect(Collectors.toList());
        }
    }

    private DepartmentHierarchyProjection getDepartmentHierarchy(String departmentId) {
        return Objects.nonNull(departmentId) ? departmentService.getDepartmentHierarchy(Long.valueOf(departmentId)) : null;
    }
}

