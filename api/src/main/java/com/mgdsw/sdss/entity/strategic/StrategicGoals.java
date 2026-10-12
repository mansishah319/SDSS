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
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "strategic_goals")
public class StrategicGoals extends BaseEntity<Integer> {

    @AlphaNum(allowedChars = DpConstants.Regex.SPACE_HYPHEN_AND_ALLOWED_CHARS)
    private String nameAr;

    @AlphaNum(allowedChars = DpConstants.Regex.SPACE_HYPHEN_AND_ALLOWED_CHARS)
    private String nameEn;

    @Column(name = "deleted_at")
    @DateTimeFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime deletedAt;
}
