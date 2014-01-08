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

import javax.inject.Inject;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.linkedin.api.CommunicationOperations;
import org.springframework.social.linkedin.api.CompanyOperations;
import org.springframework.social.linkedin.api.ConnectionOperations;
import org.springframework.social.linkedin.api.GroupOperations;
import org.springframework.social.linkedin.api.JobOperations;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.NetworkUpdateOperations;
import org.springframework.social.linkedin.api.ProfileOperations;
import org.springframework.social.linkedin.connect.LinkedInConnectionFactory;
import org.springframework.web.client.RestOperations;

import com.dynamease.serviceproviders.DynProfilePrinter;
import com.dynamease.serviceproviders.JsonProfilePrinter;
import com.dynamease.serviceproviders.ProfilePrinter;
import com.dynamease.serviceproviders.user.CurrentUserContext;
import com.dynamease.serviceproviders.user.CurrentUserContextImpl;

/**
 * Spring Social Configuration.
 * 
 * @author Keith Donald
 */
@Configuration
public class SocialConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SocialConfig.class);
    private static final UnConnectedLinkedIn VOIDLI = new UnConnectedLinkedIn();

    @Inject
    private Environment environment;

    @Inject
    private DataSource dataSource;

    /**
     * When a new provider is added to the app, register its {@link ConnectionFactory} here.
     * 
     * @see FacebookConnectionFactory
     */
    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(new FacebookConnectionFactory(environment.getProperty("facebook.clientId"),
                environment.getProperty("facebook.clientSecret")));
        registry.addConnectionFactory(new LinkedInConnectionFactory(environment.getProperty("linkedin.consumerKey"),
                environment.getProperty("linkedin.consumerSecret")));
        return registry;
    }

    /**
     * Singleton data access object providing access to connections across all users. We do not set
     * any ConnectionSignup here compared to initial Keith Donald project as main signup is managed
     * by the application and there should not be any call to the usersConnectionRepository when a
     * user is not signed in in the application (ie the user id is always known)
     */
    @Bean
    public UsersConnectionRepository usersConnectionRepository() {
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource,
                connectionFactoryLocator(), Encryptors.noOpText());
        return repository;

    }

    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
    public CurrentUserContext currentUser() {
        return new CurrentUserContextImpl();
    }

    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
    public ConnectionRepository connectionRepository() {
        String id = currentUser().getId();
        if (id == null) {
            throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
        }
        return usersConnectionRepository().createConnectionRepository(id);
    }

    /**
     * A proxy to a request-scoped object representing the current user's primary Facebook account.
     * As Facebook Spring Social allows it, returns a public access to facebook if not connected.
     * 
     * @throws NotConnectedException
     *             if the user is not connected to facebook.
     */
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public Facebook facebook() {
        Facebook toReturn = null;
        try {
            toReturn = connectionRepository().getPrimaryConnection(Facebook.class).getApi();
        }
        catch (NotConnectedException e){
            toReturn = new FacebookTemplate();
        }
        return toReturn;
    }

    /**
     * A proxy to a request-scoped object representing the current user's primary LinkedIn account.
     * 
     * @throws NotConnectedException
     *             if the user is not connected to Linkedin.
     */
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public LinkedIn linkedIn() {
        LinkedIn toReturn = VOIDLI;
        try {
              toReturn = connectionRepository().getPrimaryConnection(LinkedIn.class).getApi(); 
        }
        catch (Exception e){
            logger.warn(String.format("Unable to find a proper LinkedIn Connection for %s : %s", currentUser().getId(), e.getMessage()));
        }
        
        return toReturn;
    }

    /**
     * The standard spring MVC connection Controller, see chapter 4 of reference manual. Once the
     * connection established, the Controller points back to /connect/facebookConnected.jsp view
     * This controller replaces the use of PoviderSigninController in initial Keith Donald example.
     */
    @Bean
    public ConnectController connectController() {
        return new ConnectController(connectionFactoryLocator(), connectionRepository());
    }
    
    
    @Bean
    public ProfilePrinter PRINTER() {
        return new DynProfilePrinter();
    }
    
    
    // Void Linked In class used by default when user is not connected
    
    private static final class UnConnectedLinkedIn implements LinkedIn {

        @Override
        public boolean isAuthorized() {
            return false;
        }

        @Override
        public CommunicationOperations communicationOperations() {
            return null;
        }

        @Override
        public CompanyOperations companyOperations() {
             return null;
        }

        @Override
        public ConnectionOperations connectionOperations() {
            return null;
        }

        @Override
        public GroupOperations groupOperations() {
            return null;
        }

        @Override
        public JobOperations jobOperations() {
            return null;
        }

        @Override
        public NetworkUpdateOperations networkUpdateOperations() {
            return null;
        }

        @Override
        public ProfileOperations profileOperations() {
            return null;
        }

        @Override
        public RestOperations restOperations() {
            return null;
        }
        
    }

}
