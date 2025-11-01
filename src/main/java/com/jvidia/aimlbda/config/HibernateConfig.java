/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import com.jvidia.aimlbda.MyApplication;
import com.jvidia.aimlbda.utils.MapToJsonConverter;
import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author javaugi
 */
@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Primary
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
    } 
    
    @Primary
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter bean = new HibernateJpaVendorAdapter();
        bean.setDatabase(Database.H2);
        bean.setGenerateDdl(true);
        bean.setShowSql(true);
        return bean;
    } 

    //*
    @Bean
    public DataSource dataSourcePostgreSQL() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5432/algo_tdb")
                .username("postgres")
                .password("admin")                 
                .build();
    }    

    @Bean
    public JpaVendorAdapter jpaVendorAdapterPostgreSQL() {
        HibernateJpaVendorAdapter bean = new HibernateJpaVendorAdapter();
        bean.setDatabase(Database.POSTGRESQL);
        bean.setGenerateDdl(true);
        bean.setShowSql(true);
        return bean;
    } 

    @Bean
    public MapToJsonConverter mapToJsonConverter() {
        return new MapToJsonConverter();
    }
    // */

    /*
    @Primary
    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db_changelog/changelog-master.yaml");
        liquibase.setShouldRun(true);
        return liquibase;
    } 
    // */
    
    @Bean
    //@DependsOn("liquibase") // Tell Spring to initialize liquibase first 
    //Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'entityManagerFactory' defined in class path
    //resource [com/spring5/HibernateConfig.class]: Circular depends-on relationship between 'entityManagerFactory' and 'liquibase'
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
            JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setJpaVendorAdapter(jpaVendorAdapter);
        bean.setPackagesToScan(MyApplication.BASE_PACKAGES_TO_SCAN);

        //*
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.attribute-converters", mapToJsonConverter());
        properties.put("hibernate.query.ignore_undeclared_entities", "true"); // Skip records
        bean.setJpaPropertyMap(properties);
        // */
        return bean;
    } 

    /*
    A component required a single bean, but 2 were found:
        - transactionManager: defined by method 'transactionManager' in class path resource [com/spring5/HibernateConfig.class]
        - connectionFactoryTransactionManager: defined by method 'connectionFactoryTransactionManager' in class path resource 
            [org/springframework/boot/autoconfigure/r2dbc/R2dbcTransactionManagerAutoConfiguration.class]    
     */
    //
    @Primary
    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    } 


//*    
    @Bean
    public PlatformTransactionManager platformTransactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
    // */
        
}
/*
In Hibernate/JPA, "bag" means: A @OneToMany or @ManyToMany collection without any ordering (like List, no @OrderBy).
    You are trying to fetch join two collections (probably both List<...> types) at the same time —
    Hibernate can't handle multiple fetched bags in a single query because it can't properly de-duplicate the Cartesian product.
🎯 Example causing the problem:
Suppose your entity:
    @Entity
    public class UserAccount {
        @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
        private List<Trade> trades;

        @ManyToMany(fetch = FetchType.LAZY)
        private List<Role> roles;
    }
    Now, if you do a JPQL/HQL like:
        SELECT ua FROM UserAccount ua
        LEFT JOIN FETCH ua.trades
        LEFT JOIN FETCH ua.roles
        WHERE ua.id = :id
💥 Boom! MultipleBagFetchException — because you're fetching two bags at once.
 */
 /*
1. Fetch one collection at a time (Best Simple Fix)
    Change your query to only fetch one bag, and load the other lazily later. Example:
        SELECT ua FROM UserAccount ua
        LEFT JOIN FETCH ua.trades
        WHERE ua.id = :id
    Then separately lazy load ua.getRoles() when needed.
2. Change List to Set in your entity (Best Long-term Fix)
        Hibernate treats Set differently (no bag semantics) — it can fetch multiple Sets.

        Change:
            private List<Trade> trades;
            private List<Role> roles;
        to:
            private Set<Trade> trades;
            private Set<Role> roles;
        and update getters/setters.
    ✅ Then Hibernate allows multiple fetches safely.
3. Use two separate queries (Avoid Fetch Join entirely)
    First fetch the UserAccount,
    then separately initialize trades and roles:

    UserAccount account = userAccountRepository.findById(id).orElseThrow();
    Hibernate.initialize(account.getTrades());
    Hibernate.initialize(account.getRoles());
    (You need a Hibernate Session open for this — works well with Spring's OpenSessionInView.)
4. Use @BatchSize (Hibernate Optimization)
    Instead of fetch joins, let Hibernate load collections with batch fetching:
        @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
        @BatchSize(size = 20)
        private List<Trade> trades;

        @ManyToMany(fetch = FetchType.LAZY)
        @BatchSize(size = 20)
        private List<Role> roles;
    Spring JPA will load efficiently behind the scenes without join-flooding the DB.
🔥 Quick Comparison
    Fix             Pros                            Cons
    Fetch only      one collection	Very simple	Needs extra lazy loading
    Change to Set	Clean and correct               Might affect existing code
    Separate queries Clear control                  Needs careful transaction/session management
    @BatchSize	Good performance                Slightly more Hibernate config knowledge needed
 */
