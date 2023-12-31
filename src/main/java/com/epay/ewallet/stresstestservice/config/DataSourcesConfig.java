package com.epay.ewallet.stresstestservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourcesConfig {
    @Bean
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.one")
    DataSource dsOne(){
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    @Autowired
    @Primary
    DataSourceTransactionManager db1Tm(@Qualifier("dsOne") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
