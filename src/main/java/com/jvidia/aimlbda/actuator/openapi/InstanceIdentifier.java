/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.actuator.openapi;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class InstanceIdentifier {

    public String getLocalInstanceId() {
        String hostIdentifier = System.getenv("HOSTNAME");
        if (hostIdentifier == null || hostIdentifier.isEmpty()) {
            hostIdentifier = System.getenv("COMPUTERNAME");
        }

        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String pid = runtimeBean.getName().split("@")[0];

        return (hostIdentifier != null ? hostIdentifier : "unknown") + "-" + pid;
    }

    public String getInstanceId() {
        return Stream.<Supplier<String>>of(
                this::getInstanceIdFromEnv,
                this::getHostnameFromEnv,
                this::getPodNameFromEnv,
                this::getInstanceIdFromSystemProperty,
                this::getHostnameWithPid
        )
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .filter(id -> !id.isEmpty())
                .findFirst()
                .orElse("unknown-instance");
    }

    private String getInstanceIdFromEnv() {
        return System.getenv("INSTANCE_ID");
    }

    private String getHostnameFromEnv() {
        return System.getenv("HOSTNAME");
    }

    private String getPodNameFromEnv() {
        return System.getenv("POD_NAME");
    }

    private String getInstanceIdFromSystemProperty() {
        return System.getProperty("instance.id");
    }

    private String getHostnameWithPid() {
        try {
            return InetAddress.getLocalHost().getHostName()
                    + "-" + ManagementFactory.getRuntimeMXBean().getPid();
        } catch (UnknownHostException e) {
            return null; // Returns null to be filtered out
        }
    }

    @Override
    public String toString() {
        return "InstanceIdentifier{" + getLocalInstanceId() + "\n getInstanceId()=" + getInstanceId() + '}';
    }

}
