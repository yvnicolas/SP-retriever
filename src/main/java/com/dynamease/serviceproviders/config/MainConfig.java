/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dynamease.serviceproviders.config;

import java.io.IOException;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.dynamease.addressbooks.PersisterFactory;
import com.dynamease.addressbooks.ProfilePersister;
import com.dynamease.addressbooks.impl.CSVProfilePersisterImpl;
import com.dynamease.serviceproviders.user.UserCookieGenerator;


/**
 * Main configuration class for the application. Turns on @Component scanning, loads externalized
 * application.properties, and sets up the database.
 * 
 * @author Yves Nicolas
 */
@Configuration
@ComponentScan(basePackages = "com.dynamease.serviceproviders")
@PropertySource("classpath:application.properties")
public class MainConfig {

    
    private static final Logger logger = LoggerFactory.getLogger(MainConfig.class);
    
    @Inject
    private Environment environment;

    @Bean
    public DataSource datasource() {
        DriverManagerDataSource toReturn = new DriverManagerDataSource("jdbc:mysql://localhost:3306/"
                + environment.getProperty("database.name"));
        toReturn.setDriverClassName("com.mysql.jdbc.Driver");
        toReturn.setUsername(environment.getProperty("database.user"));
        toReturn.setPassword(environment.getProperty("database.pwd"));
        return toReturn;

    }
    
    @Bean
    public UserCookieGenerator userCookieGenerator() {
        return new UserCookieGenerator(environment.getProperty("cookie.name"));
    }
    
    public class CSVPersisterFactoryImpl implements PersisterFactory {

        @Override
        public ProfilePersister create(String name) {
           
            ProfilePersister toReturn = null;
            try {
                toReturn = new CSVProfilePersisterImpl(System.getProperty("java.io.tmpdir") + "/"+name);
            } catch (IOException e) {
               
                logger.error(String.format("Unable to create Profile persister for name %s : %s", name, e.getMessage()),e);
            }
            
            return toReturn;
        }
        
    }
    
    @Bean
    public PersisterFactory persisterFactory() {
        return new CSVPersisterFactoryImpl();
    }
}
