/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.data;

import com.jvidia.aimlbda.clients.AuditLogClient;
import com.jvidia.aimlbda.clients.TestUserClient;
import com.jvidia.aimlbda.clients.UserInfoClient;
import com.jvidia.aimlbda.config.DatabaseProperties;
import com.jvidia.aimlbda.utils.ResourceAccessUtils;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {

    final String TRUNC_TABLE = "TRUNCATE TABLE %s CASCADE";
    final String USER_ROLES_CONSTRAINTS_1 = "alter table if exists user_roles "
            + " add constraint FK_role_id_8r4j43ygjdswug4d foreign key (role_id) references roles";
    final String USER_ROLES_CONSTRAINTS_2 = "alter table if exists user_roles "
            + " add constraint FK_user_info_id_kndbx0sibxi foreign key (userinfo_id) references user_info ";

    final AuditLogClient auditLogClient;
    final UserInfoClient userInfoClient;
    final TestUserClient testUserClient;
    final DatabaseProperties dbProps;
    final JdbcTemplate jdbcTemplate;
    final Environment env;

    @Override
    public void run(ApplicationArguments args) {
        log.info("DataInitializer Spring Boot {} database {} skipDataInit {} activeProfiles {}",
                SpringBootVersion.getVersion(), dbProps.getDatabase(), dbProps.getSkipDataInit(), Arrays.toString(env.getActiveProfiles()));

        if (!dbProps.getSkipDataInit()) {
            initData();
        }
    }

    private void initData() {
        log.info("createTables ... ");
        createTables();
        log.info("Done createTables ");
        seedData();
    }

    protected void seedData() {
        auditLogClient.setup();
        userInfoClient.setup();
        testUserClient.setup();
    }

    protected void truncateData() {
        log.info("truncateData ... ");
        for (String table : DDL_TABLES) {
            truncateDataWhenNeeded(table);
        }
        log.info("truncateData Done ");
    }

    protected void createTables() {

        for (String table : DDL_TABLES) {
            Boolean tableExists = checkTableExists(table);
            if (!tableExists) {
                String ddlSql = getDdlSql(table);
                if (!ddlSql.isEmpty()) {
                    addTableBySql(ddlSql);
                    if ("user_roles".equals(table)) {
                        addUserRolesConstraintsBySql();
                    }
                }
            } else {
                Boolean hasData = checkTableDataExists(table);
                if (hasData && dbProps.getTruncateMockData() && !"user_info".equals(table)) {
                    String ddlSql = String.format(TRUNC_TABLE, table);
                    if (!ddlSql.isEmpty()) {
                        jdbcTemplate.execute(ddlSql);
                    }
                }
            }
        }
    }

    private Boolean checkTableExists(String tableName) {
        String sql = " SELECT count(*) FROM information_schema.tables "
                + " WHERE table_name = ?"; // Adjust for your DB
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
        return count != null && count > 0;
    }

    private String getDdlSql(String tableName) {
        return ResourceAccessUtils.getResourceContent(dbProps.getDdlSchemaDir() + tableName + ".sql");
    }

    private void addTableBySql(String sql) {
        jdbcTemplate.execute(sql);
    }

    private void addUserRolesConstraintsBySql() {
        if (!checkConstraintExists("user_roles", "FK_role_id_8r4j43ygjdswug4d")) {
            String ddlSql = String.format(USER_ROLES_CONSTRAINTS_1);
            jdbcTemplate.execute(ddlSql);
        }
        if (!checkConstraintExists("user_roles", "FK_user_info_id_kndbx0sibxi")) {
            String ddlSql = String.format(USER_ROLES_CONSTRAINTS_2);
            jdbcTemplate.execute(ddlSql);
        }
    }

    private Boolean checkConstraintExists(String tableName, String constraintName) {
        String sql = " SELECT count(*) FROM information_schema.table_constraints "
                + "WHERE table_name = ? AND constraint_name = ? ";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName, constraintName);
        return count != null && count > 0;
    }

    protected void truncateDataWhenNeeded(String tableName) {
        Boolean hasData = checkTableDataExists(tableName);
        if (hasData && dbProps.getTruncateMockData()) {
            String ddlSql = String.format(TRUNC_TABLE, tableName);
            if (!ddlSql.isEmpty()) {
                jdbcTemplate.execute(ddlSql);
            }
        }
    }

    private Boolean checkTableDataExists(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null && count > 0;
    }

    public static final List<String> DDL_TABLES = List.of("audit_logs", "roles", "test_users", "user_info", "users", "user_roles", "privileges", "role_privileges");    
}