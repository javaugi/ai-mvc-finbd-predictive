/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.main;

import com.jvidia.aimlbda.MyApplication;
import com.jvidia.aimlbda.clients.RestControllerClient;
import java.util.Optional;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@lombok.extern.slf4j.Slf4j
public class MainRestControllerClient {

    static final MainRestControllerClient MN = new MainRestControllerClient();
    static final String BASE_TEST_URI = "/api/testonly/";

    public static void main(String[] args) {
        System.out.println("throws NPE if null. Optional.of(null) ");
        System.out.println("safe for nulls Optional.ofNullable() " + Optional.ofNullable(null));
        System.out.println("returns empty Optional. Optional.empty() " + Optional.empty());

        ConfigurableApplicationContext context = SpringApplication.run(MyApplication.class, args);
        log.debug("Contains RestControllerClient  {}", context.containsBeanDefinition("restControllerClient"));
        RestControllerClient client = context.getBean(RestControllerClient.class);
        MN.demo(client);
    }

    private void demo(RestControllerClient client) {

        String uri = BASE_TEST_URI + "jdbc/{city}";
        client.getData(uri, "North Antonia");

        uri = BASE_TEST_URI + "entitymanager/{city}";
        client.getData(uri, "North Antonia");

        uri = BASE_TEST_URI + "procedure1/{city}";
        client.getData(uri, "North Antonia");

        uri = BASE_TEST_URI + "procedure2/{city}";
        client.getData(uri, "North Antonia");

        uri = BASE_TEST_URI + "procedure3/{userId}";
        client.getData(uri, "1");
    }

}
