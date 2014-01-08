package com.dynamease.serviceproviders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.SearchParameters;
import org.springframework.stereotype.Component;

import com.dynamease.serviceproviders.config.Uris;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("LIConnectionRetriever")
public class LIConnectionRetrieverImpl implements SPConnectionRetriever {

    private static final Logger logger = LoggerFactory.getLogger(LIConnectionRetrieverImpl.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    static final String DEFAULTPERMISSIONS = "r_fullprofile,r_network";
    
    @Autowired
    private LinkedIn linkedIn;


    public void setLinkedIn(LinkedIn linkedIn) {
        this.linkedIn = linkedIn;
    }

    public LIConnectionRetrieverImpl() {
       
    }

    public LIConnectionRetrieverImpl(LinkedIn linkedIn) {
        this.linkedIn = linkedIn;
    }

    @Override
    public List<Person> getConnections() throws SpInfoRetrievingException {
        
           
        if (!linkedIn.isAuthorized()) {
            throw new SpInfoRetrievingException("Not connected to LinkedIn");
        }
        List<LinkedInProfile> connections = linkedIn.connectionOperations().getConnections();
        List<Person> toReturn = new ArrayList<Person>();
        for (LinkedInProfile connection : connections) {
            toReturn.add(new Person(connection.getFirstName(), connection.getLastName()));
        }
        return toReturn;
    }

    @Override
    public ServiceProviders getActiveSP() {

        return ServiceProviders.LINKEDIN;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getSPType() {
        return LinkedIn.class;
    }

    @Override
    public String getConnectUrl() {

        return Uris.SIGNINLI;
    }

    @Override
    public List<SpInfoPerson> getPersonInfo(Person person) throws SpInfoRetrievingException {

        
        if (!linkedIn.isAuthorized()) {
            throw new SpInfoRetrievingException("Not connected to linkedIn");
        }
        List<SpInfoPerson> toReturn = new ArrayList<SpInfoPerson>();

        SearchParameters searchQuery = new SearchParameters();
        searchQuery.setFirstName(person.getFirstName());
        searchQuery.setLastName(person.getLastName());
        List<LinkedInProfile> queryResponse = linkedIn.profileOperations().search(searchQuery).getPeople();

        for (LinkedInProfile profile : queryResponse) {

            SpInfoPerson spInfo = new SpInfoPerson(person, ServiceProviders.LINKEDIN);
            toReturn.add(spInfo);
           try {
                spInfo.setInfo(MAPPER.writeValueAsString(profile));
                logger.info(String.format("Succesfully retrieved Linked profile info for %s : %s", person.fullName(),
                        spInfo.getInfo()));
            } catch (IOException e) {
                logger.error(
                        String.format("Serializing LinkedIn Profile for %s %s: %s", profile.getFirstName(),
                                profile.getLastName(), e.getMessage()), e);
            }

        }

        return null;
    }
    

    @Override
    public boolean isconnected() {
        boolean toReturn = false;
        try {
            toReturn = linkedIn.isAuthorized();
        } catch (Exception e) {
        }
        return toReturn;
    }

    @Override
    public String getPermissions() {
        // TODO : ameliorer en retournant les permissions autorisees si connectees
        return DEFAULTPERMISSIONS;

    }

}
