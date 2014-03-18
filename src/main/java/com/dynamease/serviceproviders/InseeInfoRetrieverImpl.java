package com.dynamease.serviceproviders;

import java.util.List;

import com.dynamease.entities.PersonBasic;
import com.dynamease.entities.PersonWthAddress;
import com.dynamease.profiles.InseeProfile;

public class InseeInfoRetrieverImpl extends DynSPConnectionRetriever<InseeProfile> {

	
	public InseeInfoRetrieverImpl() {
	    super();
	    // TODO Initialisation de l'accès base de donnee
    }

	@Override
    public ServiceProviders getActiveSP() {
	    
	    return ServiceProviders.INSEE;
    }

	@Override
    public Class getSPType() {
	    return InseeProfile.class;
    }

	@Override
    public String getConnectUrl() {
	    // Not relevant
	    return null;
    }

	@Override
    public boolean isconnected() {
	   
	    return true;
    }

	@Override
    public String getPermissions() {
	    
	    return "Not relevant";
    }

	@Override
    public List<PersonBasic> getConnections() throws SpInfoRetrievingException {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    protected List<InseeProfile> getConnectionsasProfilesSpecific() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    PersonWthAddress mapProfile(InseeProfile profile) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    List<InseeProfile> getMatchesAsProfiles(PersonWthAddress person) {
	    // TODO Auto-generated method stub
	    return null;
    }

	
}
