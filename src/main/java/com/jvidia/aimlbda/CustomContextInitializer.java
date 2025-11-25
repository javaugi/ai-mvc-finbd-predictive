/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda;

import com.jvidia.aimlbda.config.ProfileDevConfig;
import com.jvidia.aimlbda.config.ProfileH2Config;
import com.jvidia.aimlbda.config.ProfileProdConfig;
import com.jvidia.aimlbda.config.ProfileTestConfig;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        if (env.getActiveProfiles() == null || env.getActiveProfiles().length == 0) {
            env.addActiveProfile(ProfileH2Config.H2_PROFILE);
        }

        List<String> activeprofiles = Arrays.asList(env.getActiveProfiles());
        // Add properties based on active profiles
        Properties dbProps = null;
        if (!activeprofiles.contains(ProfileH2Config.H2_PROFILE)) {
            dbProps = configureDataSourceProperties(activeprofiles);
        }
        // Add properties to environment
        if (dbProps != null && !dbProps.isEmpty()) {
            PropertiesPropertySource propertySource = new PropertiesPropertySource("customDataSourceConfig", dbProps);
            env.getPropertySources().addFirst(propertySource);
        }
        log.debug("MyApplication setEnvironment profiles {} \n dbProps {}", activeprofiles, dbProps);
    }

    private Properties configureDataSourceProperties(List<String> activeprofiles) {
        Properties props = new Properties();
        if (activeprofiles.contains(ProfileProdConfig.PROD_PROFILE)) {
            configureProdProperties(props);
        } else if (activeprofiles.contains(ProfileDevConfig.DEV_PROFILE)) {
            configureDevProperties(props);
        } else if (activeprofiles.contains(ProfileTestConfig.TEST_PROFILE)) {
            configureTestProperties(props);
        }

        return props;
    }

    private void configureProdProperties(Properties props) {
        props.setProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/FINBDA_PROD");
        props.setProperty("spring.datasource.username", "finbda_prod_user");
        props.setProperty("spring.datasource.password", "${PG_DB_PROD_PWD}");
        props.setProperty("custom.datasource.host", "localhost");
        props.setProperty("custom.datasource.port", "5432");
        props.setProperty("custom.datasource.database", "FINBDA_PROD");
        props.setProperty("spring.datasource.name", "FINBDA_PROD");
        props.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
    }

    private void configureDevProperties(Properties props) {
        props.setProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/FINBDA_DEV");
        props.setProperty("spring.datasource.username", "finbda_dev_user");
        props.setProperty("spring.datasource.password", "${PG_DB_DEV_PWD}");
        props.setProperty("custom.datasource.host", "localhost");
        props.setProperty("custom.datasource.port", "5432");
        props.setProperty("custom.datasource.database", "FINBDA_DEV");
        props.setProperty("spring.datasource.name", "FINBDA_DEV");
        props.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
    }

    private void configureTestProperties(Properties props) {
        props.setProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/FINBDA_TEST");
        props.setProperty("spring.datasource.username", "finbda_test_user");
        props.setProperty("spring.datasource.password", "${PG_DB_TEST_PWD}");
        props.setProperty("custom.datasource.host", "localhost");
        props.setProperty("custom.datasource.port", "5432");
        props.setProperty("custom.datasource.database", "FINBDA_TEST");
        props.setProperty("spring.datasource.name", "FINBDA_TEST");
        props.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
    }
}
