/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.strategic;

import com.mgdsw.sdss.common.annotations.AlphaNum;
import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.common.constants.DpConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@AuditTable("FutureStrategicGoals_audit")
@AuditOverride(forClass = BaseEntity.class)
public class FutureStrategicGoals extends BaseEntity<Integer> {

    @AlphaNum(allowedChars = DpConstants.Regex.SPACE_HYPHEN_AND_ALLOWED_CHARS)
    private String nameAr;

    @AlphaNum(allowedChars = DpConstants.Regex.SPACE_HYPHEN_AND_ALLOWED_CHARS)
    private String nameEn;

    @Column(name = "deleted_at")
    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime deletedAt;
}
