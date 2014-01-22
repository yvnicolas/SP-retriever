package com.dynamease.addressbooks.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AddrBookTestConfig {
    
    @Bean
    public DynHeaderNormalizer headerDico() {
        return new DynHeaderNormalizer();
    }

}
