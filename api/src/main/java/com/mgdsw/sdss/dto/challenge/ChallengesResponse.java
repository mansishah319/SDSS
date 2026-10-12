/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.dto.challenge;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgdsw.sdss.common.base.BaseResponse;
import com.mgdsw.sdss.common.utils.DateTimeHandlerUtil;
import com.mgdsw.sdss.common.utils.DpCommonUtil;
import com.mgdsw.sdss.dto.challenge.settings.ChallengeSettingsResponse;
import com.mgdsw.sdss.dto.chat.messages.ChallengeChatDashboardResponse;
import com.mgdsw.sdss.dto.invitation.UserInvitationCountResponse;
import com.mgdsw.sdss.dto.reminder.ChallengeReminderResponse;
import com.mgdsw.sdss.dto.solution.SolutionFullResponse;
import com.mgdsw.sdss.dto.solution.SolutionsPerStageResponse;
import com.mgdsw.sdss.dto.strategic.directions.StrategicDirectionResponse;
import com.mgdsw.sdss.dto.strategic.goals.FutureStrategicGoalsResponse;
import com.mgdsw.sdss.dto.strategic.goals.StrategicGoalsResponse;
import com.mgdsw.sdss.dto.user.ChallengeUsersResponse;
import com.mgdsw.sdss.dto.user.UserDropDownListResponse;
import com.mgdsw.sdss.dto.voting.VotingResultResponse;
import com.mgdsw.sdss.enums.ChallengeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.mgdsw.sdss.common.constants.DpConstants.DateFormat.DATETIME_PATTERN;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "The Question response object for Question responses.")
public class ChallengesResponse extends BaseResponse<Integer> {

    private String status;
    private String surveyStatus;
    private String localTitle;
    private String titleAr;
    private String titleEn;
    private String visibility;
    private String localDescription;
    private String descriptionAr;
    private String descriptionEn;
    private Long moderatorGrpNumber;
    private boolean allowImmediateAccess;

    private Long sectorId;
    private String sectorName;

    private String departmentId;
    private String departmentName;

    private String subDepartmentId;
    private String subDepartmentName;

    private String sectionId;
    private String sectionName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
    private LocalDateTime endTime;

    private String field;
    private ChallengeSettingsResponse fieldResponse;

    private String impact;
    private ChallengeSettingsResponse impactResponse;

    private String levelOfDecision;
    private ChallengeSettingsResponse levelOfDecisionResponse;

    private String topicClassification;
    private ChallengeSettingsResponse topicClassificationResponse;

    private String importanceOfDecision;
    private ChallengeSettingsResponse importanceOfDecisionResponse;

    private Integer isFinal;
    private int queLevel;

    private Integer countOfSolutions;
    private Integer lastQueueLevel;

    private List<String> youtubeUrls;

    private LocalDateTime votingTime;

    private Integer committeeId;

    private List<String> solutionsTexts;
    private List<String> hashtagTexts;

    private UserDropDownListResponse moderator;
    private UserDropDownListResponse assistantModerator;

    private VotingResultResponse votingResultResponse;
    private List<SolutionsPerStageResponse> solutionsPerStageResponseList;
    private UserInvitationCountResponse userInvitationCountedResponse;
    private List<ChallengeDocsResponse> challengeDocsResponseList;
    private List<SolutionFullResponse> solutionFullResponseList;
    private List<ChallengeUsersResponse> validatorList;
    private List<ChallengeUsersResponse> teamMembersList;
    private List<ChallengeUsersResponse> guestUsersList;
    private StrategicDirectionResponse strategicDirection;
    private StrategicGoalsResponse strategicGoals;
    private List<ChallengeChatDashboardResponse> chatHistoryList;
    private StrategicDirectionResponse futureStrategicDirection;
    private FutureStrategicGoalsResponse futureStrategicGoals;
    private List<ChallengeReminderResponse> challengeReminders;

    @JsonProperty("currentYear")
    public Integer getCurrentYear() {
        return DateTimeHandlerUtil.getCurrentYear();
    }

    @JsonProperty("isAllowedToUpdateOrDelete")
    private Boolean isAllowedToUpdateOrDelete() {
        ChallengeStatus challengeStatus = ChallengeStatus.fromString(status);
        return challengeStatus.equals(ChallengeStatus.OPEN) || challengeStatus.equals(ChallengeStatus.NEW);
    }

    @JsonProperty("currentUserIsTheChallengeModeratorOrAssistantModerator")
    public Boolean currentUserIsTheChallengeModeratorOrAssistantModerator() {
        Long currentUserGrpNumber = DpCommonUtil.retrieveCurrentUserGrpNumber();

        if (currentUserGrpNumber == null) {
            return false;
        }

        return Optional.ofNullable(moderator)
                .map(m -> currentUserGrpNumber.equals(m.getGrpNumber()))
                .orElse(false)
                ||
                Optional.ofNullable(assistantModerator)
                        .map(am -> currentUserGrpNumber.equals(am.getGrpNumber()))
                        .orElse(false);
    }
}
