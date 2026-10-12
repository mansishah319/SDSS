/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.strategic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgdsw.sdss.common.annotations.AlphaNum;
import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.common.constants.DpConstants;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class StrategicDirection extends BaseEntity<Integer> {

    @NotBlank
    @AlphaNum(allowedChars = DpConstants.Regex.SPACE_HYPHEN_AND_ALLOWED_CHARS)
    private String nameAr;

    @NotBlank
    @AlphaNum(allowedChars = DpConstants.Regex.SPACE_HYPHEN_AND_ALLOWED_CHARS)
    private String nameEn;

    @JsonFormat(pattern = DpConstants.DateFormat.DATETIME_PATTERN)
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "strategicDirection")
    private List<Challenges> challenges;
}