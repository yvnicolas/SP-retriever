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
public class LIConnectionRetrieverImpl implements SPConnectionRetriever {

    private static final Logger logger = LoggerFactory.getLogger(LIConnectionRetrieverImpl.class);

    static final String DEFAULTPERMISSIONS = "r_fullprofile,r_network";

    @Autowired
    private ProfilePrinter PRINTER;

    @Autowired
    private DynDisambiguer dynDisambiguer;

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
    public List<SpInfoPerson> getPersonInfo(PersonBasic person) throws SpInfoRetrievingException {

        if (!linkedIn.isAuthorized()) {
            throw new SpInfoRetrievingException("Not connected to linkedIn");
        }
        List<SpInfoPerson> toReturn = new ArrayList<SpInfoPerson>();

        SearchParameters searchQuery = new SearchParameters();
        searchQuery.setFirstName(person.getFirstName());
        searchQuery.setLastName(person.getLastName());
        List<LinkedInProfile> queryResponse = linkedIn.profileOperations().search(searchQuery).getPeople();

        if (queryResponse != null) {
            for (LinkedInProfile profile : queryResponse) {

                if (dynDisambiguer.matches(person, profile)) {
                    SpInfoPerson spInfo = new SpInfoPerson(person, ServiceProviders.LINKEDIN);
                    toReturn.add(spInfo);
                    spInfo.setInfo(PRINTER.prettyPrintasString(profile));
                    logger.info(String.format("Succesfully retrieved Linked profile info for %s : %s",
                            person.fullName(), spInfo.getInfo()));
                } else
                    logger.debug(String.format("Discarded non matching LinkedIn profile info : %s",
                            PRINTER.prettyPrintasString(profile)));
            }
        }

        return toReturn;
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
    

    private boolean selected = false;
    
    @Override
    public boolean isSelected() {
       
        return selected;
    }

    @Override
    public void select() {
        selected = true;
        
    }

    @Override
    public void unselect() {
       selected = false;
        
    }


}
