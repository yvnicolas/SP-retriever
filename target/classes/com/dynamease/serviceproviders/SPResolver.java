package com.dynamease.serviceproviders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.dynamease.serviceproviders.user.CurrentUserContext;
import com.dynamease.serviceproviders.user.User;

/**
 * A factory that can find from the service provider enum the right SPConnectionRetriever
 * Implementation
 * 
 * @author Yves Nicolas
 * 
 */
@Service
public class SPResolver {

    public SPResolver() {
    }

    // @Autowired
    // private UsersConnectionRepository usersConnectionRepository;

    // private FBConnectionRetrieverImpl FBConnectionRetriever = new FBConnectionRetrieverImpl();

    @Autowired
    private CurrentUserContext currentUser;

    @Autowired
    private SPConnectionRetriever FBConnectionRetriever;
    
    @Autowired SPConnectionRetriever LIConnectionRetriever;

    // private LIConnectionRetrieverImpl LIConnectionRetriever = new LIConnectionRetrieverImpl();

     public void connectUser(String id) {
     currentUser.connect(id);
         }

    public void disconnectUser() {
       currentUser.disconnect();
    }

    //
    // public ConnectionRepository getConnectionRepository() {
    // return connectionRepository;
    // }
    //
    // public SPConnectionRetriever getSPConnectionRetriever() throws SpInfoRetrievingException {
    //
    // if (currentSP==null) {
    // throw new SpInfoRetrievingException("Service provider unset, can not get connections");
    // }
    // return getSPConnection(currentSP);
    // }
    //
    public SPConnectionRetriever getSPConnection(ServiceProviders sp) {
        SPConnectionRetriever toReturn = null;
      
            switch (sp) {
            case FACEBOOK:
              return FBConnectionRetriever;

            case LINKEDIN:
                return LIConnectionRetriever;

            }
        
        return toReturn;
    }
}
