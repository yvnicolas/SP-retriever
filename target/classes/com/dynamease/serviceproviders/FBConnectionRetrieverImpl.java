package com.dynamease.serviceproviders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.Reference;

import com.dynamease.serviceproviders.config.Uris;


public class FBConnectionRetrieverImpl implements SPConnectionRetriever {

    private static final Logger logger = LoggerFactory.getLogger(FBConnectionRetrieverImpl.class);
    
    static final String DEFAULTPERMISSIONS = "user_about_me,user_groups,read_friendlists,friends_about_me,friends_hometown,friends_groups";


    private Facebook facebook;


    public FBConnectionRetrieverImpl(Facebook facebook) {
        this.facebook = facebook;
    }

    public FBConnectionRetrieverImpl() {
        this.facebook = null;
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
        
        
        if (facebook==null) {
            throw new SpInfoRetrievingException("Retrieving information from a null facebook");
        }
        List<Reference> queryResponse = facebook.userOperations().search(person.fullName());
        List<SpInfoPerson> toReturn = new ArrayList<SpInfoPerson>();
        for (Reference ref : queryResponse) {

            SpInfoPerson spInfo = new SpInfoPerson(person, ServiceProviders.FACEBOOK);
            toReturn.add(spInfo);
            FacebookProfile profile = facebook.userOperations().getUserProfile(ref.getId());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(profile);
                spInfo.setInfo(baos.toString());
                oos.close();
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
