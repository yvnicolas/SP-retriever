package com.dynamease.serviceproviders;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Reference;
import org.springframework.stereotype.Component;

import com.dynamease.entities.PersonBasic;
import com.dynamease.entities.PersonWthAddress;
import com.dynamease.serviceproviders.config.Uris;

@Component("FBConnectionRetriever")
public class FBConnectionRetrieverImpl extends
		DynSPConnectionRetriever<FacebookProfile> {

	private static final Logger logger = LoggerFactory
			.getLogger(FBConnectionRetrieverImpl.class);

	static final String DEFAULTPERMISSIONS = "user_about_me,user_groups,read_friendlists,friends_about_me,friends_hometown,friends_groups";

	@Autowired
	private Facebook facebook;

	public FBConnectionRetrieverImpl() {
	}

	public void setFacebook(Facebook facebook) {
		this.facebook = facebook;
	}

	@Override
	public List<PersonBasic> getConnections() throws SpInfoRetrievingException {

		if (facebook == null) {
			throw new SpInfoRetrievingException(
					"Retrieving information from a null facebook");
		}
		List<Reference> friends = facebook.friendOperations().getFriends();
		List<PersonBasic> toReturn = new ArrayList<PersonBasic>();
		for (Reference ref : friends) {
			String name[] = new String[2];
			name = ref.getName().split(" ", 2);
			toReturn.add(new PersonBasic(name[0], name[1]));
		}

		return toReturn;
	}

	@Override
	protected List<FacebookProfile> getConnectionsasProfilesSpecific() {

		// TODO : comprendre le fonctionnement des paged list
		PagedList <FacebookProfile> toReturn=null;
		if (facebook != null) {
			try {
			toReturn = facebook.friendOperations().getFriendProfiles();
			}
			catch (Exception e) {
				logger.error(String.format("Error retrieving facebook Connection : %s", e.getMessage()));
				logger.error(String.format("Root cause : %s", e.getCause().getMessage()));
			}
			
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
		return FacebookProfile.class;
	}

	@Override
	public String getConnectUrl() {

		return Uris.SIGNINFB;
	}

	@Override
	public boolean isconnected() {

		if (facebook == null)
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

		// Seems to be a bug with the permissions real time info so skip it for
		// the moment.
		// if (isconnected())
		// return facebook.userOperations().getUserPermissions().toString();
		// else
		return DEFAULTPERMISSIONS;

	}

	@Override
	List<FacebookProfile> getMatchesAsProfiles(PersonWthAddress person) {
		List<Reference> queryResponse = facebook.userOperations().search(
				person.fullName());
		List<FacebookProfile> toReturn = new ArrayList<>();
		if (queryResponse != null) {
			for (Reference ref : queryResponse) {

				toReturn.add(facebook.userOperations().getUserProfile(
						ref.getId()));
			}
		}
		logger.debug(String.format("Found %s Facebook profiles matches for %s",
				toReturn.size(), person.fullName()));
		return toReturn;
	}

	@Override
    PersonWthAddress mapProfile(FacebookProfile profile) {
		PersonWthAddress toReturn = new PersonWthAddress();
		try {
			toReturn.setFirstName(profile.getFirstName());
			toReturn.setLastName(profile.getLastName());
			toReturn.setCity(profile.getLocation().getName());
		}
		catch (NullPointerException e) {}
	
		return toReturn;
    }

}
