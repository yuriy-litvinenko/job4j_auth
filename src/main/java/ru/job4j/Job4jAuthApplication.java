package ru.job4j;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class Job4jAuthApplication {

    @Bean
    public SpringLiquibase liquibase(DataSource ds) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:db/liquibase-changeLog.xml");
        liquibase.setDataSource(ds);
        return liquibase;
    }

    public static void main(String[] args) {
        SpringApplication.run(Job4jAuthApplication.class, args);
    }

}
