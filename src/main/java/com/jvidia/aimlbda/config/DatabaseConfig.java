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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.jdbc.core.JdbcTemplate;
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
    
    @PostConstruct
    public void init() {
        log.debug("DatabaseConfig profiles {}", Arrays.toString(env.getActiveProfiles()));
        if (env.acceptsProfiles(Profiles.of(ProfileProdConfig.PROD_PROFILE))) {
            profileSetting = ProfileSetting.PROD;
        } else if (env.acceptsProfiles(Profiles.of(ProfileDevConfig.DEV_PROFILE))) {
            profileSetting = ProfileSetting.DEV;
        } else {
            profileSetting = ProfileSetting.TEST;
        }
        
        dbProps.setupBaseDbProps(profileSetting);
        log.debug("DatabaseConfig props {}", dbProps);
    }

    /*
    Transaction Best Practices - see MedicalDocumentService
        1. Keep transactions short - Especially with pessimistic locks
        2. Use appropriate isolation levels - Configure in @Transactional
        3. Implement retry logic - For optimistic locking scenarios
        4. Monitor for deadlocks - Set up proper logging
        5. Consider alternative approaches - Like event sourcing for high-contention scenarios
        6. Example of setting isolation level:
            @Transactional(isolation = Isolation.SERIALIZABLE)
            public Mono<Void> highIsolationOperation() {
                // Your transactional code
            }
     */
    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbProps.getUrl());
        dataSource.setUsername(dbProps.getUsername());
        dataSource.setPassword(dbProps.getPassword());
        dataSource.setDriverClassName(dbProps.getDriverClassName());

        // HikariCP settings
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setIdleTimeout(300000);
        dataSource.setConnectionTimeout(20000);
        dataSource.setMaxLifetime(1200000);
        /*
        return org.springframework.boot.jdbc.DataSourceBuilder.create()
                .driverClassName(dbProps.getDriverClassName())
                .url(dbProps.getUrl())
                .username(dbProps.getUsername())
                .password(dbProps.getPassword())
                .build();
        // */
        return dataSource;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter bean = new HibernateJpaVendorAdapter();
        bean.setDatabase(Database.POSTGRESQL);
        bean.setGenerateDdl(true);
        bean.setShowSql(true);
        return bean;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.setFetchSize(10000); // Large fetch size for batch processing
        return template;
    }
}
