package com.dynamease.serviceproviders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
    private User currentUser;

    @Autowired
    private ConnectionRepository connectionRepository;

    // private LIConnectionRetrieverImpl LIConnectionRetriever = new LIConnectionRetrieverImpl();

     public void connectUser(String id) {
     currentUser.setId(id);
         }

    public void disconnectUser() {
        this.currentUser = null;
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
        if (currentUser != null) {

            switch (sp) {
            case FACEBOOK:
                try {
                    toReturn = new FBConnectionRetrieverImpl(connectionRepository.getPrimaryConnection(Facebook.class)
                            .getApi());
                } catch (NotConnectedException e) {

                }
                break;

            case LINKEDIN:
                try {
                    toReturn = new LIConnectionRetrieverImpl(connectionRepository.getPrimaryConnection(LinkedIn.class)
                            .getApi());

                } catch (NotConnectedException e) {

                }
                break;

            }
        }
        return toReturn;
    }
}
