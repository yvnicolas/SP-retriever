package com.dynamease.serviceproviders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.Reference;
import org.springframework.stereotype.Component;

import com.dynamease.serviceproviders.config.Uris;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("FBConnectionRetriever")
public class FBConnectionRetrieverImpl implements SPConnectionRetriever {

    private static final Logger logger = LoggerFactory.getLogger(FBConnectionRetrieverImpl.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    static final String DEFAULTPERMISSIONS = "user_about_me,user_groups,read_friendlists,friends_about_me,friends_hometown,friends_groups";


    @Autowired
    private Facebook facebook;

 
    public FBConnectionRetrieverImpl() {
    }

    public void setFacebook(Facebook facebook) {
        this.facebook = facebook;
    }

    @Override
    public List<Person> getConnections() throws SpInfoRetrievingException {
        
        if (facebook==null) {
            throw new SpInfoRetrievingException("Retrieving information from a null facebook");
        }
        List<Reference> friends = facebook.friendOperations().getFriends();
        List<Person> toReturn = new ArrayList<Person>();
        for (Reference ref : friends) {
            String name[] = new String[2];
            name = ref.getName().split(" ", 2);
            toReturn.add(new Person(name[0], name[1]));
        }

        return toReturn;
    }

    @Override
    public ServiceProviders getActiveSP() {

        return ServiceProviders.FACEBOOK;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getSPType() {
        return Facebook.class;
    }

    @Override
    public String getConnectUrl() {

        return Uris.SIGNINFB;
    }

    @Override
    public List<SpInfoPerson> getPersonInfo(Person person) throws SpInfoRetrievingException {
        
        
        if (!facebook.isAuthorized()) {
            throw new SpInfoRetrievingException("Not connected to facebook");
        }
        List<Reference> queryResponse = facebook.userOperations().search(person.fullName());
        List<SpInfoPerson> toReturn = new ArrayList<SpInfoPerson>();
        for (Reference ref : queryResponse) {

            SpInfoPerson spInfo = new SpInfoPerson(person, ServiceProviders.FACEBOOK);
            toReturn.add(spInfo);
            FacebookProfile profile = facebook.userOperations().getUserProfile(ref.getId());
            try {
                spInfo.setInfo(MAPPER.writeValueAsString(profile));
                logger.info(String.format("Succesfully retrieved facebook profile info for %s : %s", ref.getName(), spInfo.getInfo()));
            } catch (IOException e) {
                logger.error(String.format("Serializing Facebook Profile for %s : %s", ref.getName(), e.getMessage()),e);
            }

        }
        return toReturn;
    }

    @Override
    public boolean isconnected() {

        if (facebook==null)
            return false;
        boolean toReturn = false;
        try {
            toReturn = facebook.isAuthorized();
        } catch (Exception e) {
        }
        return toReturn;
    }

    @Override
    public String getPermissions() {

        if (isconnected())
            return facebook.userOperations().getUserPermissions().toString();
        else
            return DEFAULTPERMISSIONS;

    }
    
}
