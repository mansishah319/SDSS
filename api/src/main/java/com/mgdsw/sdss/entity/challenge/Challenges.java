/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.challenge;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.common.converter.IntegerListToStringConverter;
import com.mgdsw.sdss.entity.chat.ChatRoom;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.entity.strategic.FutureStrategicDirection;
import com.mgdsw.sdss.entity.strategic.FutureStrategicGoals;
import com.mgdsw.sdss.entity.strategic.StrategicDirection;
import com.mgdsw.sdss.entity.strategic.StrategicGoals;
import com.mgdsw.sdss.entity.user.GuestAccess;
import com.mgdsw.sdss.entity.user.User;
import com.mgdsw.sdss.entity.user.UserInvitation;
import com.mgdsw.sdss.entity.user.Validators;
import com.mgdsw.sdss.entity.variety.Library;
import com.mgdsw.sdss.entity.variety.Voting;
import com.mgdsw.sdss.enums.ChallengeStatus;
import com.mgdsw.sdss.enums.ChallengeVisibility;
import com.mgdsw.sdss.enums.SurveyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "question")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@AuditTable("question_audit")
@AuditOverride(forClass = BaseEntity.class)
public class Challenges extends BaseEntity<Integer> {

    @Size(min = 1, max = 500)
    private String titleAr;

    @Size(min = 1, max = 500)
    private String titleEn;

    @Size(min = 1, max = 500)
    private String descriptionAr;

    @Size(min = 1, max = 500)
    private String descriptionEn;

    private String topicClassification;
    private String levelOfDecision;
    private String importanceOfDecision;
    private String impact;
    private String field;

    @Column(name = "department_ref_id")
    private String departmentId;
    private String departmentName;

    private String subDepartmentId;
    private String subDepartmentName;

    private String sectionId;
    private String sectionName;

    private int durations;

    @Convert(converter = IntegerListToStringConverter.class)
    private List<Integer> stageList;

    @JsonFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime startTime;

    @Column(name = "allow_imidiate_access")
    private Boolean allowImmediateAccess;

    @Enumerated(EnumType.ORDINAL)
    private ChallengeVisibility visibility;

    private Integer committeeId;

    @Enumerated(EnumType.ORDINAL)
    private SurveyStatus surveyStatus;
    @Enumerated(EnumType.ORDINAL)
    private SurveyStatus adminSurvey;
    private Boolean isSendSurvey;
    private int queLevel;
    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime nextRoundStartTime;
    private int isFinal;
    private Boolean canVote;
    private Boolean canAnswer;
    private Boolean justView;
    @Column(columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean updateSolutions;

    @JsonFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime endTime;
    private String locale;

    @Column(name = "deleted_at")
    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime deletedAt;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private ChallengeStatus status;

    @ManyToOne
    @JoinColumn(name = "strategic_goal_id")
    private StrategicGoals strategicGoals;

    @ManyToOne
    @JoinColumn(name = "future_strategic_goal_id")
    private FutureStrategicGoals futureStrategicGoals;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private StrategicDirection strategicDirection;

    @ManyToOne
    @JoinColumn(name = "future_strategic_direction_id")
    private FutureStrategicDirection futureStrategicDirection;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "moderator")
    public User moderator;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "assistant_moderator")
    public User assistantModerator;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenges", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInvitation> userInvitations;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenges", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenges", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Library> libraries;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenges", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuestAccess> guestAccessList;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenges", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solution> solutions;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenges", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeHashtag> challengeHashtags;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenges", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeVideo> youtube;

    @NotAudited
    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "challenges", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeDocuments> challengeDocuments = new ArrayList<>();

    @NotAudited
    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenges", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeReminder> challengeReminders;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenges", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Validators> validators;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY)
    private List<ChallengeExpertises> challengeExpertisesList;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "challenges", orphanRemoval = true)
    private List<Voting> votingList;
}
