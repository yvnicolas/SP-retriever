package com.dynamease.serviceproviders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * A factory that can find from the service provider enum the right SPConnectionRetriever Implementation
 * @author Yves Nicolas
 *
 */
@Component
public class SPResolver {
    @Autowired
    SPConnectionRetriever FBConnectionRetriever;
    
    @Autowired
    SPConnectionRetriever LIConnectionRetriever;
    
    public SPConnectionRetriever getSPConnection(ServiceProviders sp) {
        
        switch (sp) {
        case FACEBOOK :
            return FBConnectionRetriever;   
        case LINKEDIN :
            return LIConnectionRetriever;
        }
        return null;
    }
}

