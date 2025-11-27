/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "spring.datasource")
@Component
public class DatabaseProperties {
   
    private String h2Url;
    private String h2Username;
    private String h2Password;
    private String h2DdlSchemaDir;
    private String h2Host;
    private String h2Port;
    private String h2Database;
    private String h2DriverClassName;
    private String h2Name;

    private String devUrl;
    private String devUsername;
    private String devPassword;
    private String devDdlSchemaDir;
    private String devHost;
    private String devPort;
    private String devDatabase;
    private String devDriverClassName;
    private String devName;

    private String testUrl;
    private String testUsername;
    private String testPassword;
    private String testDdlSchemaDir;
    private String testHost;
    private String testPort;
    private String testDatabase;
    private String testDriverClassName;
    private String testName;

    private String prodUrl;
    private String prodUsername;
    private String prodPassword;
    private String prodDdlSchemaDir;
    private String prodHost;
    private String prodPort;
    private String prodDatabase;
    private String prodDriverClassName;
    private String prodName;

    private ProfileSetting profileSetting;
    private Boolean setupMockTestUserOnly;
    private Boolean truncateMockData;
    private Boolean skipDataInit;

    private String url;
    private String username;
    private String password;
    private String host;
    private String port;
    private String ddlSchemaDir;
    private String database;
    private String driverClassName;
    private String name;

    private Integer poolInitialSize = 8;
    private Integer poolMaxSize = 20;
    private Integer poolMinSize = 5;
    private Integer connTimeout = 2000;

    public enum ProfileSetting {
        dev, test, prod, h2
    }
    
    public void setupBaseDbProps(ProfileSetting ps) {
        profileSetting = ps;
        
        switch(profileSetting) {
            case ProfileSetting.prod -> {
                this.url = this.prodUrl;
                this.host = this.prodHost;
                this.port = this.prodPort;
                this.username = this.prodUsername;
                this.password = this.prodPassword;
                this.database = this.prodDatabase;
                this.ddlSchemaDir = this.prodDdlSchemaDir;
                this.driverClassName = this.prodDriverClassName;
                this.name = this.prodName;
            }
            case ProfileSetting.dev -> {
                this.url = this.devUrl;
                this.host = this.devHost;
                this.port = this.devPort;
                this.username = this.devUsername;
                this.password = this.devPassword;
                this.database = this.devDatabase;
                this.ddlSchemaDir = this.devDdlSchemaDir;
                this.driverClassName = this.devDriverClassName;
                this.name = this.devName;
            }
            case ProfileSetting.h2 -> {
                this.url = this.h2Url;
                this.host = this.h2Host;
                this.port = this.h2Port;
                this.username = this.h2Username;
                this.password = this.h2Password;
                this.database = this.h2Database;
                this.ddlSchemaDir = this.h2DdlSchemaDir;
                this.driverClassName = this.h2DriverClassName;
                this.name = this.h2Name;
            }
            default -> {
                this.url = this.testUrl;
                this.host = this.testHost;
                this.port = this.testPort;
                this.username = this.testUsername;
                this.password = this.testPassword;
                this.database = this.testDatabase;
                this.ddlSchemaDir = this.testDdlSchemaDir;
                this.driverClassName = this.testDriverClassName;
                this.name = this.testName;
            }

        }
    }

    @Override
    public String toString() {
        return "DatabaseProperties{" + "driverClassName=" + driverClassName + ", setupMockTestUserOnly=" + setupMockTestUserOnly
                + ", truncateMockData=" + truncateMockData + ", skipDataInit=" + skipDataInit + ", url=" + url + ", username=" + username
                + ", ddlSchemaDir=" + ddlSchemaDir + ", database=" + database + ", poolInitialSize=" + poolInitialSize
                + ", poolMaxSize=" + poolMaxSize + ", poolMinSize=" + poolMinSize + ", connTimeout=" + connTimeout + '}';
    }
}
