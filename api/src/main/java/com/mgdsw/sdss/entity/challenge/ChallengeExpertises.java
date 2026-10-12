/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.challenge;

import com.mgdsw.sdss.common.base.BaseEntity;
import com.mgdsw.sdss.entity.variety.Expertises;
import com.mgdsw.sdss.enums.ExpertLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Nov, 2023
 */

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeExpertises extends BaseEntity<Integer> {

    @ManyToOne
    @JoinColumn(name = "expertises_id")
    private Expertises expertises;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "challenge_id")
    private Challenges challenges;

    @Enumerated(EnumType.STRING)
    private ExpertLevel expertLevel;
}
