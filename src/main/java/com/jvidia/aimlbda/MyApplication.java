package com.jvidia.aimlbda;

import java.util.Arrays;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Configuration
@EnableCaching
@ComponentScan(basePackages = {MyApplication.BASE_PACKAGES_TO_SCAN})
@EnableJpaRepositories(basePackages = {MyApplication.BASE_PACKAGES_TO_SCAN})
@ConfigurationProperties
public class MyApplication {
    public static final String BASE_PACKAGES_TO_SCAN = "com.jvidia.aimlbda";

	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
	}
    
    @Bean
    public ApplicationRunner printBeans(ApplicationContext ctx) {
        return args -> {
            String[] beans = ctx.getBeanDefinitionNames();
            Arrays.sort(beans);
            for (String bean : beans) {
                System.out.println(bean);
            }
        };
    }    
}
