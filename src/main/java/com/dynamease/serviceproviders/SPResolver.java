package com.dynamease.serviceproviders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.linkedin.api.LinkedIn;

import com.dynamease.serviceproviders.user.User;

/**
 * A factory that can find from the service provider enum the right SPConnectionRetriever
 * Implementation
 * 
 * @author Yves Nicolas
 * 
 */

public class SPResolver {

    public SPResolver() {
    }

    @Autowired
    private UsersConnectionRepository usersConnectionRepository;

    private FBConnectionRetrieverImpl FBConnectionRetriever = new FBConnectionRetrieverImpl();

    private User currentUser = null;
    
    private ServiceProviders currentSP=null;

    private ConnectionRepository connectionRepository = null;

    private LIConnectionRetrieverImpl LIConnectionRetriever = new LIConnectionRetrieverImpl();
    
    

    public ServiceProviders getCurrentSP() {
        return currentSP;
    }

    public void setCurrentSP(ServiceProviders currentSP) {
        this.currentSP = currentSP;
    }

    public void connectUser(User user) {
        this.currentUser = user;
        connectionRepository = usersConnectionRepository.createConnectionRepository(user.getId());
        FBConnectionRetriever.setFacebook(connectionRepository.getPrimaryConnection(Facebook.class).getApi());
        LIConnectionRetriever.setLinkedIn(connectionRepository.getPrimaryConnection(LinkedIn.class).getApi());
    }

    public void disconnectUser() {
        this.currentUser = null;
    }
    

    public ConnectionRepository getConnectionRepository() {
        return connectionRepository;
    }

    public SPConnectionRetriever getSPConnectionRetriever() throws SpInfoRetrievingException {

        if (currentSP==null) {
            throw new SpInfoRetrievingException("Service provider unset, can not get connections");
        }
        if (currentUser != null)  {
            switch (currentSP) {
            case FACEBOOK:
                return FBConnectionRetriever;
            case LINKEDIN:
                return LIConnectionRetriever;
            }
        }
        return null;
    }
}
