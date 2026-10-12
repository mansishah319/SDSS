/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.user;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Sep, 2023
 */

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions")
public class Permissions extends BaseEntity<Integer> {

    @Column(unique = true)
    private String nameAr;
    @Column(unique = true)
    private String nameEn;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Boolean active;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.EAGER)
    private List<Roles> roles;
}
