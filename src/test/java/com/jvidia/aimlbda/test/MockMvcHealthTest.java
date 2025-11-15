/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test;

import com.jvidia.aimlbda.MyApplicationBaseTests;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
Common Actuator Endpoints
    Endpoint                Description
    /actuator/health        Shows health info
    /actuator/info          Shows info from application.properties
    /actuator/metrics       Metrics like JVM, CPU, DB, HTTP reqs
    /actuator/env           Shows environment variables and props
    /actuator/beans         Shows all Spring beans
    /actuator/loggers       Change log levels at runtime
    /actuator/prometheus    App health details and system gauge from Prometheus

Summary
    @SpringBootApplication + Actuator exposes useful diagnostics /actuator/health gives system health (DB, disk, custom indicators)
    Extend with custom health indicators
    Integrates with Prometheus, Grafana, Azure Monitor, etc.
 */
public class MockMvcHealthTest extends MyApplicationBaseTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
        assertThat(true).isTrue();
    }

    @Test
    public void shouldReturnHealthStatusUp() throws Exception {
        // Test the Spring Boot Actuator health endpoint
       mockMvc.perform(get(HEALTH_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

}

/*
Common Actuator Endpoints
    Endpoint                Description
    /actuator/health        Shows health info
    /actuator/info          Shows info from application.properties
    /actuator/metrics       Metrics like JVM, CPU, DB, HTTP reqs
    /actuator/env           Shows environment variables and props
    /actuator/beans         Shows all Spring beans
    /actuator/loggers       Change log levels at runtime
    /actuator/prometheus    App health details and system gauge from Prometheus

Summary
    @SpringBootApplication + Actuator exposes useful diagnostics /actuator/health gives system health (DB, disk, custom indicators)
    Extend with custom health indicators
    Integrates with Prometheus, Grafana, Azure Monitor, etc.
 */
/*
org.springdoc/springdoc-openapi-starter-webmvc-ui
# default: /v3/api-docs
springdoc.api-docs.path=/api-docs
springdoc.api-docs.swagger-ui.path=/swagger-ui.html

This gives you:
    /v3/api-docs            → JSON OpenAPI JSON    spec
    /swagger-ui.html        → Swagger UI
    /swagger-ui/index.html  → alternative Swagger UI path
    /v3/api-docs.yaml       -> Bonus: You can also generate a YAML spec
 */
