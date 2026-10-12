/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Oct, 2023
 */
@Getter
@AllArgsConstructor
public enum TemplatesImages {

    GOVT_OF_DUBAI_LOGO("government-of-dubai.png", "/static/images/government-of-dubai.png"),
    MONITOR_DASHBOARD("monitor-dashboard.png", "/static/images/monitor-dashboard.png"),
    CHALLENGE_SESSION("challenge-session.png", "/static/images/challenge-session.png"),
    DUBAI_POLICE_ICN("dubai-police-icn.png", "/static/images/dubai-police-icn.png"),
    DUBAI_POLICE_LOGO("dubai-police.png", "/static/images/dubai-police.png"),
    GOOGLE_PLAY_LOGO("google-play.png", "/static/images/google-play.png"),
    APPLE_STORE_LOGO("apple-store.png", "/static/images/apple-store.png"),
    SHAPE_BOTTOM("shape-bottom.png", "/static/images/shape-bottom.png"),
    SHAPE_TOP_IMAGE("shape-top.png", "/static/images/shape-top.png"),
    HUAWEI_IMAGE("huawei-btn.png", "/static/images/huawei-btn.png"),
    SMART_02("smart-02.png", "/static/images/smart-02.png"),
    SMART_01("smart-01.png", "/static/images/smart-01.png"),
    INS_ICON("ins.png", "/static/images/ins.png"),
    FB_ICON("fb.png", "/static/images/fb.png"),
    TW_ICON("tw.png", "/static/images/tw.png"),
    YT_ICON("yt.png", "/static/images/yt.png"),
    DH_ICON("dh.png", "/static/images/dh.png");

    private final String name;
    private final String path;
}
