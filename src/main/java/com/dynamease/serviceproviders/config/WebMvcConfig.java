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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.dynamease.serviceproviders.user.UserInterceptor;

/**
 * Spring MVC Configuration.
 * 
 * @author Keith Donald
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private @Inject
    UserInterceptor userinterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userinterceptor);
    }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(Uris.SIGNIN);
        registry.addViewController(Uris.SIGNOUT);
        registry.addViewController(Uris.SIGNINCONFIRM);
        registry.addViewController(Uris.SIGNINFB);
        registry.addViewController(Uris.SIGNINLI);
        registry.addViewController(Uris.SIGNINVI);
        registry.addViewController(Uris.APPLICATIONIDINPUT);
        registry.addViewController(Uris.SEARCHRESULT);
        registry.addViewController(Uris.BYE);
        registry.addViewController(Uris.FILEUPLOAD);
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix(Uris.VIEWS);
        viewResolver.setSuffix(Uris.SUFFIX);
        return viewResolver;
    }

    
    
    @Bean 
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }
}
