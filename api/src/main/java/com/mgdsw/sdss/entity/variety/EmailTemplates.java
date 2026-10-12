/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.variety;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.common.constants.DpConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Transactional
@DynamicUpdate
public class EmailTemplates extends BaseEntity<Integer> {

    private String subjectAr;
    private String subjectEn;
    @Column(length = 1500)
    private String contentAr;
    @Column(length = 1500)
    private String contentEn;
    @Column(nullable = false, columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean isDefault;

    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "template_type_id")
    private TemplateType templateType;
}
