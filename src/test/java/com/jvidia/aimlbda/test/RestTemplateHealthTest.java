/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test;

import com.jvidia.aimlbda.MyApplicationBaseTests;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

//@Disabled
public class RestTemplateHealthTest extends MyApplicationBaseTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnHealthStatusUpByRest() {
        ResponseEntity<String> response = restTemplate.getForEntity(HEALTH_URL, String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    public void shouldReturnHealthStatusUpByRestForce() {
        var response = restTemplate.getForEntity(HEALTH_URL, String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
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
