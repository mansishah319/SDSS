/**
 * ***************************************************************
 * Copyright ©1956-2023 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.util;

import com.google.common.collect.ImmutableMap;
import com.mgdsw.sdss.entity.challenge.Challenges;
import com.mgdsw.sdss.entity.user.User;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.Objects;

import static com.mgdsw.sdss.common.constants.DpConstants.Characters.QUESTION_MARK;
import static com.mgdsw.sdss.common.constants.DpConstants.Characters.SLASH;
import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;
import static com.mgdsw.sdss.common.constants.DpConstants.Urls.ACCEPT_INVITATION;
import static com.mgdsw.sdss.common.constants.DpConstants.Urls.HASH_KEY_PARAM;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.CHALLENGE;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.CHALLENGE_USER_INVITATION_URL;
import static com.mgdsw.sdss.util.ContentHelperUtils.Email.MY_CHALLENGES_URL;
import static com.mgdsw.sdss.util.UserUtils.Fields.USER;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Dec, 2023
 */

@Log4j2
public final class InvitationUtil {

    public static ImmutableMap<String, Object> retrieveChallengeCreationMappedData(User user, Challenges challenge, String inviteLink) {
        ImmutableMap.Builder<String, Object> modelBuilder = ImmutableMap.builder();
        modelBuilder.put(USER, user);
        modelBuilder.put(CHALLENGE, challenge);
        modelBuilder.put(CHALLENGE_USER_INVITATION_URL, inviteLink);
        return modelBuilder.build();
    }

    public static ImmutableMap<String, Object> retrieveChallengeReminderMappedData(User user, Challenges challenge, String myChallengesUrl) {
        ImmutableMap.Builder<String, Object> modelBuilder = ImmutableMap.builder();
        modelBuilder.put(USER, user);
        modelBuilder.put(CHALLENGE, challenge);
        modelBuilder.put(MY_CHALLENGES_URL, myChallengesUrl);
        return modelBuilder.build();
    }

    public static String[] getReceiverEmailsForTheUser(User user) {
        String email = user.getEmail();
        String alternativeEmail = user.getAlternativeEmail();
        log.info("User main email is -> {} and alternativeEmail is -> {}", email, alternativeEmail);
        return Objects.nonNull(alternativeEmail) ? new String[]{email, alternativeEmail} : new String[]{email};
    }

    public static String generateInvitationURL(User user, Challenges challenge,
                                               String hashKey, String serverContext,
                                               HttpServletRequest httpServletRequest) {
        String baseUrl = DpUrlUtils.getBaseUrl(httpServletRequest);
        log.info("Base URL is {} and the Server Context is --> {}", baseUrl, serverContext);
        return ObjectUtils.isEmpty(serverContext) ?
                baseUrl + SLASH + user.getId() + ACCEPT_INVITATION + SLASH + challenge.getId() + QUESTION_MARK + HASH_KEY_PARAM + hashKey :
                baseUrl + SLASH + serverContext + SLASH + user.getId() + ACCEPT_INVITATION + SLASH + challenge.getId() + QUESTION_MARK + HASH_KEY_PARAM + hashKey;
    }

    private InvitationUtil() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
