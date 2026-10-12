/**
 * ***************************************************************
 * Copyright ©1956-2024 DubaiPolice. All rights reserved
 * ——————————————————————————————————
 * NOTICE: All information contained herein is a property of DubaiPolice.
 * ***************************************************************
 */

package com.mgdsw.sdss.common.config.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.LOCAL_PROFILE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.NOT_LOCAL_PROFILE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.NOT_TEST_PROFILE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.SdssDB.SDSS_DATASOURCE_PREFIX;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.SdssDB.SDSS_DATA_SOURCE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.SdssDB.SDSS_ENTITY_MANAGER_FACTORY_BEAN_NAME;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.SdssDB.SDSS_ENTITY_PACKAGE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.SdssDB.SDSS_JNDI_NAME;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.SdssDB.SDSS_PERSISTENCE_UNIT;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.SdssDB.SDSS_REPOSITORY_PACKAGE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.SdssDB.SDSS_TRANSACTION_MANAGER_BEAN_NAME;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.TEST_PROFILE;

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn Jun, 2024
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = SDSS_ENTITY_MANAGER_FACTORY_BEAN_NAME,
        transactionManagerRef = SDSS_TRANSACTION_MANAGER_BEAN_NAME,
        basePackages = {
                SDSS_REPOSITORY_PACKAGE
        },
        repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class
)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SdssDatabaseConfiguration {

    private final Environment env;
    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;
    private final List<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers;

    @Primary
    @Profile({TEST_PROFILE, LOCAL_PROFILE})
    @Bean(name = SDSS_DATA_SOURCE, destroyMethod = "")
    @ConfigurationProperties(prefix = SDSS_DATASOURCE_PREFIX)
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Profile({NOT_TEST_PROFILE, NOT_LOCAL_PROFILE})
    @Bean(name = SDSS_DATA_SOURCE, destroyMethod = "")
    public DataSource dataSourceGNDI() {
        JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        return lookup.getDataSource(Objects.requireNonNull(env.getProperty(SDSS_JNDI_NAME)));
    }

    @Primary
    @Bean(name = SDSS_ENTITY_MANAGER_FACTORY_BEAN_NAME)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(SDSS_DATA_SOURCE) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(SDSS_ENTITY_PACKAGE)
                .persistenceUnit(SDSS_PERSISTENCE_UNIT)
                .properties(this.getTheProperties())
                .build();
    }

    @Primary
    @Bean(name = SDSS_TRANSACTION_MANAGER_BEAN_NAME)
    public PlatformTransactionManager transactionManager(
            @Qualifier(SDSS_ENTITY_MANAGER_FACTORY_BEAN_NAME) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> getTheProperties() {
        return new LinkedHashMap<>(
                this.hibernateProperties
                        .determineHibernateProperties(
                                this.jpaProperties.getProperties(),
                                new HibernateSettings().hibernatePropertiesCustomizers(this.hibernatePropertiesCustomizers)
                        ));
    }
}

