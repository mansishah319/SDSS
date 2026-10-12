/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.user;

import com.mgdsw.sdss.common.annotations.Phone;
import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.entity.solution.Solution;
import com.mgdsw.sdss.enums.Language;
import com.mgdsw.sdss.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@Audited
@AuditTable("user_audit")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@AuditOverride(forClass = BaseEntity.class)
public class User extends BaseEntity<Integer> {

    private String fullName;

    private Integer failLoginCount;

    @Column(name = "GRP_NUMBER", unique = true, nullable = false)
    private Long grpNumber;

    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime lastFailLogin;

    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime lastSuccessLoginDate;

    @Email
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Email
    @Column(name = "email2")
    private String alternativeEmail;

    private String mobile;

    @Phone
    @Column(name = "mobile2")
    private String alternativeMobile;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Column(nullable = false)
    private Boolean isActiveBefore;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Language language;

    @Column(name = "is_mail_send")
    private Boolean isMailSend;

    @Column(name = "isinternal", nullable = false)
    private Boolean isInternal;

    private Boolean onlineStatus;
    private String type;
    @Column(unique = true)
    private String confirmationToken;

    private Boolean internalDocuments;
    private Boolean chatContents;
    private Boolean socialMediaFeeds;
    private Boolean decision;
    private Boolean questionView;
    private Boolean ratings;
    private Boolean scrollingText;

    @Column(columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean isDelphiModerator;

    private String departmentName;
    private Long departmentId;
    private Long jobId;
    private String jobName;

    @Column(name = "deleted_at")
    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime deletedAt;

    @NotAudited
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "roles_id", referencedColumnName = "id")})
    private List<Roles> roles;

    @NotAudited
    @OneToMany(mappedBy = "user")
    private List<UsersExpertises> expertisesList;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Validators> validators;

    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Solution> solutions;


    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<GuestAccess> guestAccess;
}
