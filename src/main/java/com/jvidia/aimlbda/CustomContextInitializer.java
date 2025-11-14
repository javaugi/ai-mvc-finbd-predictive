/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda;

import com.jvidia.aimlbda.config.ProfileTestConfig;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
public class CustomContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        if (env.getActiveProfiles() == null || env.getActiveProfiles().length == 0) {
            env.addActiveProfile(ProfileTestConfig.TEST_PROFILE);
        }
        log.debug("MyApplication setEnvironment profiles {}", Arrays.toString(env.getActiveProfiles()));
    }
}
