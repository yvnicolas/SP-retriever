package com.dynamease.serviceproviders;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.SearchParameters;
import org.springframework.stereotype.Component;

import com.dynamease.entities.PersonBasic;
import com.dynamease.serviceproviders.config.Uris;

@Component("LIConnectionRetriever")
public class LIConnectionRetrieverImpl extends DynSPConnectionRetriever<LinkedInProfile> {

    private static final Logger logger = LoggerFactory.getLogger(LIConnectionRetrieverImpl.class);

    static final String DEFAULTPERMISSIONS = "r_fullprofile,r_network";

    @Autowired
    private LinkedIn linkedIn;

    public LIConnectionRetrieverImpl() {

    }

    @Override
    public List<PersonBasic> getConnections() throws SpInfoRetrievingException {

        if (!linkedIn.isAuthorized()) {
            throw new SpInfoRetrievingException("Not connected to LinkedIn");
        }
        List<LinkedInProfile> connections = linkedIn.connectionOperations().getConnections();
        List<PersonBasic> toReturn = new ArrayList<PersonBasic>();
        for (LinkedInProfile connection : connections) {
            toReturn.add(new PersonBasic(connection.getFirstName(), connection.getLastName()));
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

    @Override
    List<LinkedInProfile> getMatchesAsProfiles(PersonBasic person) {
        List<LinkedInProfile> toReturn;
        SearchParameters searchQuery = new SearchParameters();
        searchQuery.setFirstName(person.getFirstName());
        searchQuery.setLastName(person.getLastName());
       toReturn = linkedIn.profileOperations().search(searchQuery).getPeople();
       logger.debug(String.format("Found %s Linkedin profiles matches for %s", toReturn.size(), person.fullName()));
       return toReturn;

    }
    



}
