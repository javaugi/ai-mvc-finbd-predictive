/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import com.jvidia.aimlbda.config.DatabaseProperties.ProfileSetting;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Slf4j
@Configuration
public class DatabaseConfig {
    @Autowired
    protected DatabaseProperties dbProps;
    @Autowired
    private Environment env;
    
    private ProfileSetting profileSetting;
    private Database database;

    @PostConstruct
    public void init() {
        log.debug("DatabaseConfig profiles {}", Arrays.toString(env.getActiveProfiles()));
        this.database = Database.POSTGRESQL;
        if (env.acceptsProfiles(Profiles.of(ProfileProdConfig.PROD_PROFILE))) {
            profileSetting = ProfileSetting.prod;
        } else if (env.acceptsProfiles(Profiles.of(ProfileDevConfig.DEV_PROFILE))) {
            profileSetting = ProfileSetting.dev;
        } else if (env.acceptsProfiles(Profiles.of(ProfileH2Config.H2_PROFILE))) {
            profileSetting = ProfileSetting.h2;
            this.database = Database.H2;
        } else {
            profileSetting = ProfileSetting.test;
        }

        dbProps.setupBaseDbProps(profileSetting);
        log.debug("DatabaseConfig props {}", dbProps);
    }

    @Bean
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties dsProps = new DataSourceProperties();
        BeanUtils.copyProperties(dbProps, dsProps);
        return dsProps;
    }

    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        if (env.acceptsProfiles(Profiles.of(ProfileH2Config.H2_PROFILE))) {
            return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
        }
        HikariDataSource dataSource = (HikariDataSource) dataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setMaximumPoolSize(20);
        dataSource.setMinimumIdle(2);
        dataSource.setIdleTimeout(300000);
        dataSource.setConnectionTimeout(20000);
        dataSource.setMaxLifetime(1200000);

        return dataSource;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter bean = new HibernateJpaVendorAdapter();
        bean.setDatabase(this.database);
        return bean;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.setFetchSize(10000); // Large fetch size for batch processing
        return template;
    }

    /*
    @Bean
    public MapToJsonConverter mapToJsonConverter() {
        return new MapToJsonConverter();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
            JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setJpaVendorAdapter(jpaVendorAdapter);
        bean.setPackagesToScan(com.jvidia.aimlbda.MyApplication.BASE_PACKAGES_TO_SCAN);

        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.attribute-converters", mapToJsonConverter());
        bean.setJpaPropertyMap(properties);
        return bean;
    }

    @Primary
    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan(com.jvidia.aimlbda.MyApplication.BASE_PACKAGES_TO_SCAN); // Package containing your entities
        return sessionFactory;
    }
    // */

}
