package com.dynamease.serviceproviders;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.viadeo.api.Viadeo;
import org.springframework.social.viadeo.api.ViadeoProfile;
import org.springframework.stereotype.Component;

import com.dynamease.entities.PersonBasic;
import com.dynamease.serviceproviders.config.Uris;

@Component("ViadeoConnectionRetriever")
public class ViadeoConnectionRetrieverImpl extends DynSPConnectionRetriever<ViadeoProfile> {

    private static final Logger logger = LoggerFactory.getLogger(ViadeoConnectionRetrieverImpl.class);

    static final String DEFAULTPERMISSIONS = "";

    @Autowired
    private Viadeo viadeo;

    public ViadeoConnectionRetrieverImpl() {
    }

    public void setViadeo(Viadeo viadeo) {
        this.viadeo = viadeo;
    }

    @Override
    public List<PersonBasic> getConnections() throws SpInfoRetrievingException {

        if (viadeo == null) {
            throw new SpInfoRetrievingException("Retrieving information from a null viadeo");
        }
        List<ViadeoProfile> connections = viadeo.userOperations().getContacts(100);
        List<PersonBasic> toReturn = new ArrayList<PersonBasic>();
        for (ViadeoProfile profile : connections) {

            toReturn.add(new PersonBasic(profile.getFirstName(), profile.getLastName()));
        }

        return toReturn;
    }

    @Override
    public ServiceProviders getActiveSP() {

        return ServiceProviders.VIADEO;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getSPType() {
        return Viadeo.class;

    }

    @Override
    public String getConnectUrl() {

        return Uris.SIGNINVI;
    }

    @Override
    public boolean isconnected() {

        if (viadeo == null) {
            logger.debug(String.format("is connected : null viadeo returning false"));
            return false;
        }
        boolean toReturn = false;
        try {
            toReturn = viadeo.isAuthorized();
            logger.debug(String.format("Viadeo authorization succesfully checked returned %s", toReturn));
        } catch (Exception e) {
            logger.debug(String.format("Problem checking viadeo authorization : %s", e.getMessage()),e);
        }
        return toReturn;
    	
    
    }

    @Override
    public String getPermissions() {

        return null;

    }


    @Override
    List<ViadeoProfile> getMatchesAsProfiles(PersonBasic person) {
        List<ViadeoProfile> toReturn = viadeo.userOperations().search(person.fullName());
        logger.debug(String.format("Found %s Viadeo profiles matches for %s", toReturn.size(), person.fullName()));
        return toReturn;
    	
    }

	@Override
	protected List<ViadeoProfile> getConnectionsasProfilesSpecific() {
		// TODO Auto-generated method stub
		return null;
	}


}
