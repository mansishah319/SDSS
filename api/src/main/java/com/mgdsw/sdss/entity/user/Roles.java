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
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Setter
@Getter
@Audited
@AuditTable("roles_audit")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles_Entity")
@AuditOverride(forClass = BaseEntity.class)
public class Roles extends BaseEntity<Integer> {

    @Column(unique = true)
    private String nameAr;
    @Column(unique = true)
    private String nameEn;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Boolean active;

    @NotAudited
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> user;

    @NotAudited
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "permission_id", referencedColumnName = "id"))
    private List<Permissions> permissions;
}
