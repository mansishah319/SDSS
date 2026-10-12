/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.variety;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.common.utils.ListActionsUtil;
import com.mgdsw.sdss.enums.TemplatesTypeKeys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateType extends BaseEntity<Integer> {

    @Column(unique = true, nullable = false)
    private String titleEn;
    @Column(unique = true, nullable = false)
    private String titleAr;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TemplatesTypeKeys templateTypeKey;
    private String mandatoryFieldsAr;
    private String mandatoryFieldsEn;

    public void setMandatoryFieldsAr(List<String> mandatoryFieldsListAr) {
        this.mandatoryFieldsAr = ListActionsUtil.convert(mandatoryFieldsListAr);
    }

    public void setMandatoryFieldsEn(List<String> mandatoryFieldsListEn) {
        this.mandatoryFieldsEn = ListActionsUtil.convert(mandatoryFieldsListEn);
    }
}
