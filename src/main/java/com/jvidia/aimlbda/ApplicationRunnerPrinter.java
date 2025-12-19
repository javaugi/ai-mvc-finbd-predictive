/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda;

import com.jvidia.aimlbda.config.DatabaseProperties;
import com.jvidia.aimlbda.utils.LogUtil;
import com.jvidia.aimlbda.utils.types.AuthProvider;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationRunnerPrinter implements ApplicationRunner {

    @Autowired
    private Environment env;
    @Autowired
    protected DatabaseProperties dbProps;

    @Bean
    public CommandLineRunner verify(ClientRegistrationRepository repo) {
        return args -> {
            for (AuthProvider ap : AuthProvider.values()) {
                ClientRegistration epic = repo.findByRegistrationId(ap.name());
                log.debug("ApplicationRuunerPrinter AuthProvider={} ClientRegistration={}", ap.name(), ((epic != null) ? epic.getClientId() : ""));
            }
        };
    }

    @Override
    public void run(ApplicationArguments args) {
        log.debug("ApplicationRuunerPrinter profiles {} java.home={}", Arrays.toString(env.getActiveProfiles()), System.getProperty("java.home"));
        // This method provides a more comprehensive dump including different property sources
        // Be cautious, this can print a lot of sensitive information in production.
        ((AbstractEnvironment) env).getPropertySources()
                .forEach(ps -> {
                    if (ps instanceof MapPropertySource mapPropertySource) {
                        LogUtil.logRunnerMap("ApplicationRunnerPrinter", mapPropertySource);
                    } else {
                        //log.info("Non MapPropertySource: " + ps.getName() + " (not a MapPropertySource, cannot iterate directly)");
                        // For non-MapPropertySource types (like system properties, command line args),
                        // iterating all keys is not directly supported by the PropertySource interface itself.
                        // You'd typically access specific keys via env.getProperty()
                    }
                });

        log.debug("Application is ready: \n {}", dbProps);
    }
}
