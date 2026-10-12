/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.solution;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.common.converter.IntegerListToStringConverter;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditTable("solution_audit")
@Table(name = "solution")
@DynamicUpdate
@AuditOverride(forClass = BaseEntity.class)
public class Solution extends BaseEntity<Integer> {

    @Column(name = "SOLUTION", nullable = false)
    private String solutionText;

    private Boolean isFinal;
    private Boolean isAdminAgree;

    private int queLevel;
    private int solutionColor;
    private int voteColor;
    private long voteCount;
    private int point;
    private int rank;

    private Boolean initPublic;

    @Convert(converter = IntegerListToStringConverter.class)
    private List<Integer> stageList;

    @Convert(converter = IntegerListToStringConverter.class)
    private List<Integer> mergedSolutionUsers;

    @Column(length = 3000, name = "OLDVALUES")
    private String oldValues;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Challenges challenges;

    @NotAudited
    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolutionComment> solutionCommentList;
}
