package com.mgdsw.sdss.common.config.db;

import lombok.RequiredArgsConstructor;
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
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
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

import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.CommitteeDB.COMMITTEE_DATASOURCE_PREFIX;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.CommitteeDB.COMMITTEE_DATA_SOURCE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.CommitteeDB.COMMITTEE_ENTITY_MANAGER_FACTORY_BEAN_NAME;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.CommitteeDB.COMMITTEE_ENTITY_PACKAGE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.CommitteeDB.COMMITTEE_JNDI_NAME;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.CommitteeDB.COMMITTEE_PERSISTENCE_UNIT;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.CommitteeDB.COMMITTEE_REPOSITORY_PACKAGE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.CommitteeDB.COMMITTEE_TRANSACTION_MANAGER_BEAN_NAME;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.LOCAL_PROFILE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.NOT_LOCAL_PROFILE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.NOT_TEST_PROFILE;
import static com.mgdsw.sdss.common.config.db.constants.DatabaseConstants.TEST_PROFILE;

@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = COMMITTEE_ENTITY_MANAGER_FACTORY_BEAN_NAME,
        transactionManagerRef = COMMITTEE_TRANSACTION_MANAGER_BEAN_NAME,
        basePackages = {
                COMMITTEE_REPOSITORY_PACKAGE
        }
)
public class CommitteeDatabaseConfiguration {

    private final Environment env;
    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;
    private final List<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers;

    @Profile({TEST_PROFILE, LOCAL_PROFILE})
    @Bean(name = COMMITTEE_DATA_SOURCE, destroyMethod = "")
    @ConfigurationProperties(prefix = COMMITTEE_DATASOURCE_PREFIX)
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Profile({NOT_TEST_PROFILE, NOT_LOCAL_PROFILE})
    @Bean(name = COMMITTEE_DATA_SOURCE, destroyMethod = "")
    public DataSource dataSourceGNDI() {
        JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        return lookup.getDataSource(Objects.requireNonNull(env.getProperty(COMMITTEE_JNDI_NAME)));
    }

    @Bean(name = COMMITTEE_ENTITY_MANAGER_FACTORY_BEAN_NAME)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(COMMITTEE_DATA_SOURCE) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(COMMITTEE_ENTITY_PACKAGE)
                .persistenceUnit(COMMITTEE_PERSISTENCE_UNIT)
                .properties(this.getTheProperties())
                .build();
    }

    @Bean(name = COMMITTEE_TRANSACTION_MANAGER_BEAN_NAME)
    public PlatformTransactionManager transactionManager(
            @Qualifier(COMMITTEE_ENTITY_MANAGER_FACTORY_BEAN_NAME) EntityManagerFactory entityManagerFactory) {
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
