/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.committee.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "COMMITTEES_MAIN", schema = "COMMITTEES")
public class Committee implements Serializable {

    @Id
    @Column(name = "COMTT_ID")
    private Long id;

    @Column(name = "COMTT_NAME")
    private String name;

    @Column(name = "COMTT_CAT_CODE")
    private Integer categoryCode;

    @Column(name = "COMTT_TEAM_TYPE")
    private Integer teamType;

    @Column(name = "CREATEION_DATE", columnDefinition = "DATE DEFAULT SYSDATE")
    private LocalDateTime creationDate;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "MODIFICATION_DATE")
    private LocalDateTime modificationDate;

    @Column(name = "MODIFIED_BY")
    private Long modifiedBy;

    @Column(name = "COMTT_REF_NO")
    private String referenceNo;

    @Column(name = "COMTT_INDICATOR")
    private String indicator;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "SUPERVISIOR_CODE")
    private Long supervisorCode;

    @Column(name = "FLOW_STATUS", columnDefinition = "NUMBER DEFAULT 0")
    private Integer flowStatus;

    @Column(name = "FLOW_STATUS_KIND", columnDefinition = "NUMBER DEFAULT 0")
    private Integer flowStatusKind;

    @Column(name = "COMTT_YEAR", columnDefinition = "NUMBER DEFAULT 0")
    private Integer year;

    @Column(name = "COMTT_REF_ID", columnDefinition = "NUMBER DEFAULT 0")
    private Long referenceId;

    @Column(name = "COMTT_REPEAT", columnDefinition = "NUMBER DEFAULT 0")
    private Integer repeat;

    @Column(name = "COMTT_PARENT", columnDefinition = "NUMBER DEFAULT 0")
    private Long parent;

    @Column(name = "COMTT_FLOW_TYPE", columnDefinition = "NUMBER DEFAULT 0")
    private Integer flowType;

    @Column(name = "COMTT_STATUS", columnDefinition = "NUMBER DEFAULT 1")
    private Integer status;

    @Column(name = "QA_SUPERVISIOR_CODE", columnDefinition = "NUMBER DEFAULT 0")
    private Long qaSupervisorCode;

    @Column(name = "IS_DELETED", columnDefinition = "NUMBER DEFAULT 0")
    private Integer isDeleted;

    @Column(name = "COMTT_PERIOD")
    private Integer period;

    @Column(name = "MEMBER_TYPE")
    private Integer memberType;

    @Column(name = "IS_REFERRED_BACK", columnDefinition = "NUMBER DEFAULT 0")
    private Integer isReferredBack;
}
