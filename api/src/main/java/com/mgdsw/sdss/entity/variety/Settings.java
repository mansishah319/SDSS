/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.entity.variety;

import com.mgdsw.sdss.common.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Map;


@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "settings")
@Cacheable()
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Settings extends BaseEntity<Integer> {

    private String key;
    private String value;

    @Transient
    private Map<String, String> settingList;

    @Transient
    private String siteName;

    @Transient
    private String siteFailLoginCount;

    @Transient
    private String fileUploadPath;

    @Transient
    private String fileDisplayPath;

    @Transient
    private String twitterConsumerKey;

    @Transient
    private String twitterConsumerSecret;

    @Transient
    private String twitterAccessToken;

    @Transient
    private String twitterAccessTokenSecret;

    @Transient
    private String siteUrl;

    @Transient
    private String siteLogo;

    @Transient
    private String siteFromName;

    @Transient
    private String siteFromEmail;

    @Transient
    private String siteQuestionTimeFrame;

    @Transient
    private String siteVotingTime;

    @Transient
    private String siteDashboardNotification;

    @Transient
    private String siteDashboardNotificationArabic;

    @Transient
    private Boolean siteSmsOtp;

    @Transient
    private Boolean googleApiKey;

    @Transient
    private Boolean googleSearchEngineId;

    @Transient
    private Boolean siteEmailOtp;

    @Transient
    private String siteEmailHost;

    @Transient
    private String siteEmailUsername;

    @Transient
    private String siteEmailPort;

    @Transient
    private String siteEmailPassword;

    @Transient
    private String siteStar1Min;

    @Transient
    private String siteStar1Max;

    @Transient
    private String siteStar2Min;

    @Transient
    private String siteStar2Max;

    @Transient
    private String siteStar3Min;

    @Transient
    private String siteStar3Max;

    @Transient
    private String siteStar4Min;

    @Transient
    private String siteStar4Max;

    @Transient
    private String siteStar5Min;

    @Transient
    private String siteStar5Max;
}