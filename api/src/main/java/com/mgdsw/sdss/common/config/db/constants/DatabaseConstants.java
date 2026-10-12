/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.db.constants;

import static com.mgdsw.sdss.common.constants.DpConstants.UTILITY_CLASS;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */
public class DatabaseConstants {

    public static final String LOCAL_PROFILE = "local";
    public static final String TEST_PROFILE = "test";
    public static final String NOT_LOCAL_PROFILE = "!local";
    public static final String NOT_TEST_PROFILE = "!test";

    public static class SdssDB {

        public static final String SDSS_DATASOURCE_PREFIX = "spring.datasource.primary";
        public static final String SDSS_JNDI_NAME = "spring.datasource.primary.jndi-name";

        public static final String SDSS_DATA_SOURCE = "primaryDataSource";
        public static final String SDSS_PERSISTENCE_UNIT = "primary";

        public static final String SDSS_ENTITY_PACKAGE = "com.mgdsw.sdss.entity";
        public static final String SDSS_REPOSITORY_PACKAGE = "com.mgdsw.sdss.repository.*";

        public static final String SDSS_ENTITY_MANAGER_FACTORY_BEAN_NAME = "primaryEntityManagerFactory";
        public static final String SDSS_TRANSACTION_MANAGER_BEAN_NAME = "primaryTransactionManager";

        private SdssDB() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    public static class WeblisherDB {

        public static final String WEBLISHER_SCHEMA_NAME = "weblisher";

        public static final String WEBLISHER_DATASOURCE_PREFIX = "spring.datasource.secondary";
        public static final String WEBLISHER_JNDI_NAME = "spring.datasource.secondary.jndi-name";

        public static final String WEBLISHER_DATA_SOURCE = "secondaryDataSource";
        public static final String WEBLISHER_PERSISTENCE_UNIT = "secondary";

        public static final String WEBLISHER_ENTITY_PACKAGE = "com.mgdsw.sdss.core.entity";
        public static final String WEBLISHER_REPOSITORY_PACKAGE = "com.mgdsw.sdss.core.repository";

        public static final String WEBLISHER_ENTITY_MANAGER_FACTORY_BEAN_NAME = "secondaryEntityManagerFactory";
        public static final String WEBLISHER_TRANSACTION_MANAGER_BEAN_NAME = "secondaryTransactionManager";

        private WeblisherDB() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    public static class CommitteeDB {

        public static final String COMMITTEE_DATASOURCE_PREFIX = "spring.datasource.tertiary";
        public static final String COMMITTEE_JNDI_NAME = "spring.datasource.tertiary.jndi-name";

        public static final String COMMITTEE_DATA_SOURCE = "tertiaryDataSource";
        public static final String COMMITTEE_PERSISTENCE_UNIT = "tertiary";

        public static final String COMMITTEE_ENTITY_PACKAGE = "com.mgdsw.sdss.committee.entity";
        public static final String COMMITTEE_REPOSITORY_PACKAGE = "com.mgdsw.sdss.committee.repository";

        public static final String COMMITTEE_ENTITY_MANAGER_FACTORY_BEAN_NAME = "tertiaryEntityManagerFactory";
        public static final String COMMITTEE_TRANSACTION_MANAGER_BEAN_NAME = "tertiaryTransactionManager";

        private CommitteeDB() {
            throw new IllegalStateException(UTILITY_CLASS);
        }
    }

    private DatabaseConstants() {
        throw new IllegalStateException(UTILITY_CLASS);
    }
}
