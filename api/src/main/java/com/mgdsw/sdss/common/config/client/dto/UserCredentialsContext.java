/**
 * **************************************************************
 * Copyright ©1956-2025 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * **************************************************************
 */

package com.mgdsw.sdss.common.config.client.dto;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @email eng.mo.abdu@gmail.com
 * @creationDate 09 Jan 2025
 */
public final class UserCredentialsContext {
    private static final ThreadLocal<String> username = new ThreadLocal<>();
    private static final ThreadLocal<String> password = new ThreadLocal<>();
    private static final ThreadLocal<String> domain = new ThreadLocal<>();

    public static void setUsername(String username) {
        UserCredentialsContext.username.set(username);
    }

    public static String getUsername() {
        return username.get();
    }

    public static void setPassword(String password) {
        UserCredentialsContext.password.set(password);
    }

    public static String getPassword() {
        return password.get();
    }

    public static void setDomain(String domain) {
        UserCredentialsContext.domain.set(domain);
    }

    public static String getDomain() {
        return domain.get();
    }

    public static void clear() {
        username.remove();
        password.remove();
        domain.remove();
    }

    private UserCredentialsContext() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}

