/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import java.io.Serializable;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Data
@ConfigurationProperties(prefix = "spring.datasource")
@Configuration
@Profile(ProfileDevConfig.DEV_PROFILE)
public class ProfileDevConfig implements Serializable {
    private static final long serialVersionUID = 321357244048L;
    public static final String DEV_PROFILE = "dev";

    private String url;
    private String username;
    private String password;
    private String host;
    private String port;
    private String ddlSchemaDir;
    private String database;
    private String driverClassName;
    private String name;
}
