package com.jvidia.aimlbda.config;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/*
Key Features

RESTful API with HATEOAS: - Hypermedia as the Engine of Application State, is a key concept in REST 
        (Representational State Transfer) that dictates how clients and servers interact in a RESTful application. 
    Resources include links to related resources
    Follows REST principles
    Self-descriptive messages

Web Interface with Spring MVC:
    Traditional server-side rendering
    Thymeleaf templates for HTML generation
    Simple CRUD operations through web forms

Data Model:
    JPA entities with proper relationships
    Repository pattern for data access

Separation of Concerns:
    API endpoints separate from web interface
    Clear distinction between data model and resource representation

This implementation provides a solid foundation that can be extended with additional features like:
    Authentication and authorization
    Validation
    Advanced search capabilities
    Pagination
    Caching
    API documentation with Swagger


Here are the Maven dependencies for integrating Swagger (OpenAPI) into a Spring Boot project. 
    The recommended library is springdoc-openapi-starter-webmvc-ui.

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
</dependency>

This dependency includes everything needed to expose API documentation through Swagger UI. It automatically 
    configures Swagger for your Spring Boot application, eliminating the need for manual configuration 
    in most cases. After adding this dependency and rebuilding the project, 
    the Swagger UI can be accessed at http://localhost:8080/swagger-ui.html (the port may vary depending on your configuration).

*/

@Configuration
@EnableWebMvc
//http://localhost:8088/swagger-ui/index.html 
//-- @Param("category") is required in the Repository for swagger-ui to work
// io.swagger.core.v3 upgrade required
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }    

    
    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory(); // Explicit initialization
    }
    
    
    /*
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
       registry.jsp("/WEB-INF/views/", ".jsp");
    }
    // */
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
        registry.addMapping("/h2-console/**");
        registry.addMapping("/v3/api-docs/**").allowedOrigins("*");
        registry.addMapping("/swagger-ui/**").allowedOrigins("*");
        registry.addMapping("/swagger-ui.html").allowedOrigins("*");
    }    

    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/"); // Configure your prefix
        resolver.setSuffix(".jsp");
        resolver.setOrder(0); // Set the order for resolution
        return resolver;
    }

    @Bean
    public InternalResourceViewResolver htmlViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/dockerhtml/"); // Use the same prefix or a different one
        resolver.setSuffix(".html");
        resolver.setOrder(1); // Set a higher order than the previous resolver
        return resolver;
    }    
    
    @Bean
    public InternalResourceViewResolver angularclientResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/angularclient/"); // Use the same prefix or a different one
        resolver.setSuffix(".*");
        resolver.setOrder(1); // Set a higher order than the previous resolver
        return resolver;
    }    

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        return source;
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource());
        return validator;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Register resource handler for CSS and JS
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/resources/", "classpath:/statics/")
                .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());

        // Register resource handler for images
        registry.addResourceHandler("/images/**").addResourceLocations("/WEB-INF/images/")
                .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());

        registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/")
                .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());

        registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/")
                .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());

        registry.addResourceHandler("/tags/**").addResourceLocations("/WEB-INF/tags/")
                .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());

        registry.addResourceHandler("/**").addResourceLocations("classpath:/angularclient/").setCachePeriod(0);        
        registry.addResourceHandler("/**").addResourceLocations("classpath:/angularclient/public/").setCachePeriod(0);        
        
    }

    @Controller
    static class FaviconController {

        @RequestMapping("favicon.ico")
        String favicon() {
            return "forward:/images/favicon.ico";
        }
    }

    @Controller
    static class ServiceWorkerController {

        @RequestMapping("sw.js")
        @ResponseBody
        void serviceWorker() {
        }
    } 
    
    private void curlCommands() {
        /*
 1. GET Request - Used to retrieve data from a server.
    curl -X GET "https://jsonplaceholder.typicode.com/posts/1" \
      -H "Accept: application/json"
        🔍 What it does:
            -X GET — explicitly specifies the HTTP method (optional for GET).
            -H — sets the header (here, asking for JSON response).
            URL — target resource.
✅ 2. POST Request - Used to create a new resource.
        curl -X POST "https://jsonplaceholder.typicode.com/posts" \
          -H "Content-Type: application/json" \
          -d '{
                "title": "foo",
                "body": "bar",
                "userId": 1
              }'
🔍 What it does:
        -X POST — specifies POST method.
        -H "Content-Type: application/json" — sets content type.
        -d — sends the request body as JSON.
✅ 3. PUT Request - Used to update/replace an existing resource.
        curl -X PUT "https://jsonplaceholder.typicode.com/posts/1" \
          -H "Content-Type: application/json" \
          -d '{
                "id": 1,
                "title": "updated title",
                "body": "updated body",
                "userId": 1
              }'
    🔍 What it does: PUT replaces the entire resource.
    JSON body contains all fields, including the ID.
✅ 4. DELETE Request - Used to delete a resource.
        curl -X DELETE "https://jsonplaceholder.typicode.com/posts/1"
    Sends a request to delete the resource at the specified URL.
✅ Optional Add-ons
    🔐 Authorization Header (e.g., Bearer token):
        -H "Authorization: Bearer <your_token_here>"
    🌐 Add Query Parameters (for GET):
            curl -G "https://api.example.com/items" \
              --data-urlencode "type=book" \
              --data-urlencode "limit=10"        
        */
    }
} 

/*
why the error ? Spring MVC found on classpath, which is incompatible with Spring Cloud Gateway.

Action:  Please set spring.main.web-application-type=reactive or remove spring-boot-starter-web dependency.


ChatGPT said:
Excellent — you just hit one of the most common integration pitfalls when using Spring Cloud Gateway.

Let me carefully break it down:

✅ The cause:
Spring Cloud Gateway is built on Spring WebFlux (reactive stack).

Spring MVC (traditional servlet-based stack) comes in when you include spring-boot-starter-web.

You can't mix both at the same time — they're incompatible in the same application context because they both try to start different web servers (Netty vs Tomcat/Jetty).

🔎 Why you're seeing:
Spring MVC found on classpath, which is incompatible with Spring Cloud Gateway.

Spring Cloud Gateway sees spring-boot-starter-web on the classpath, which pulls in Spring MVC automatically.

✅ The solutions:
Option 1 — Remove spring-boot-starter-web
In your pom.xml or build.gradle, make sure you only have:

xml
Copy
Edit
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
⚠️ No spring-boot-starter-web at all.

Option 2 — Explicitly set web application type to reactive
In application.properties or application.yml:

properties
Copy
Edit
spring.main.web-application-type=reactive
Or programmatically in your main():

java
Copy
Edit
SpringApplication app = new SpringApplication(MyApplication.class);
app.setWebApplicationType(WebApplicationType.REACTIVE);
app.run(args);
⚠️ This only works if you're 100% certain you don't accidentally need MVC classes that might still conflict.

Option 3 — Split Gateway into separate service
Many companies deploy Spring Cloud Gateway as a standalone microservice, completely separate from other Spring Boot apps that might use Spring MVC.

✅ Quick check for your build file:
You probably have this (causing the problem):

xml
Copy
Edit
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
✅ Replace it with:

xml
Copy
Edit
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
🔬 Bonus tip:
If you see:	You're using:
DispatcherServlet	MVC
DispatcherHandler	WebFlux
*/

/*
Swagger API refers to a suite of open-source tools that help developers design, document, test, and use REST APIs. These tools are built around the OpenAPI Specification, which defines a standard format for describing APIs. Swagger offers features like interactive documentation, code generation, and API testing. 
Here's a more detailed breakdown:
Core Components of Swagger:
OpenAPI Specification:
.
This is the instruction manual or "blueprint" that defines the structure of a REST API. It can be written in YAML or JSON format.
Swagger Editor:
.
A browser-based tool for writing and editing API definitions based on the OpenAPI specification.
Swagger UI:
.
An interactive HTML interface that displays API documentation and allows developers to test API endpoints directly within the UI.
Swagger Codegen:
.
A tool that automatically generates server stubs and client libraries from an OpenAPI definition, saving developers time and effort. 
Key Benefits of Using Swagger:
Improved API Documentation:
.
Swagger provides a user-friendly and interactive way to document APIs, making them easier to understand and use. 
Simplified API Development:
.
Swagger tools automate tasks like code generation and API testing, streamlining the development process. 
Enhanced API Collaboration:
.
By using a common API specification, Swagger facilitates collaboration between developers, testers, and other stakeholders. 
Faster API Testing:
.
Swagger UI allows developers to directly test API endpoints within the documentation, ensuring they function as expected before implementing them 
 */
