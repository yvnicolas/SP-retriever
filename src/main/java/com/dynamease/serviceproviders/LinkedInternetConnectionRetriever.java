package com.dynamease.serviceproviders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dynamease.entities.PersonBasic;
import com.dynamease.entities.PersonWthAddress;
import com.dynamease.profiles.LinkedInternetProfile;

@Component("LINternetRetriever")
public class LinkedInternetConnectionRetriever extends DynSPConnectionRetriever<LinkedInternetProfile> {

	private static final Logger logger = LoggerFactory.getLogger(LinkedInternetConnectionRetriever.class);

	private static final String SLASH = "/";

	private String DIRURL = "http://fr.linkedin.com/pub/dir/";

	public LinkedInternetConnectionRetriever() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ServiceProviders getActiveSP() {

		return ServiceProviders.LINKEDINPUBLIC;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getSPType() {

		return LinkedInternetProfile.class;
	}

	@Override
	public String getConnectUrl() {

		return DIRURL;
	}

	@Override
	public boolean isconnected() {

		return true;
	}

	@Override
	public String getPermissions() {

		return "Not relevant for public Internet access to Linked In";
	}

	@Override
	public List<PersonBasic> getConnections() throws SpInfoRetrievingException {
		// Method not relevant for this SP, no connection concept
		return null;
	}

	@Override
	protected List<LinkedInternetProfile> getConnectionsasProfilesSpecific() {
		// Method not relevant for this SP, no connection concept
		return null;
	}

	@Override
	List<LinkedInternetProfile> getMatchesAsProfiles(PersonWthAddress person) {

		List<LinkedInternetProfile> toReturn = new ArrayList<>();

		// Do the Internet Search
		StringBuilder urlBuilder = new StringBuilder(DIRURL);
		urlBuilder.append(person.getFirstName());
		urlBuilder.append(SLASH);
		urlBuilder.append(person.getLastName());

		Document resultPage;
		try {
			resultPage = Jsoup.connect(urlBuilder.toString()).get();

			Element results = resultPage.getElementById("result-set");

			Elements resultList = results.getElementsByClass("vcard");

			for (Element oneResult : resultList) {

				LinkedInternetProfile match = getProfileFromHtml(oneResult, person);
				if (match != null) {
					toReturn.add(match);
				}
			}
		} catch (IOException e) {
			logger.error(String.format("IO Error fetching URL %s : %s", urlBuilder.toString(), e.getMessage()));

		}

		return toReturn;
	}

	private LinkedInternetProfile getProfileFromHtml(Element oneResult, PersonBasic person) {

		// Get full name and check correct match with Person full name
		String fullName = oneResult.getElementsByTag("h2").get(0).getElementsByTag("a").get(0).attr("title");
		if (fullName.toLowerCase().equals(person.fullName().toLowerCase())) {
			LinkedInternetProfile toReturn = new LinkedInternetProfile();
			toReturn.setFirstName(person.getFirstName());
			toReturn.setLastName(person.getLastName());
			toReturn.setFullName(fullName);

			Element vcardBasic = oneResult.getElementsByClass("vcard-basic").get(0);
			try {
				toReturn.setTitle(vcardBasic.getElementsByClass("title").get(0).text());
			} catch (IndexOutOfBoundsException e) {
			}
			try {
			} catch (IndexOutOfBoundsException e) {
			}
			try {
				toReturn.setLocation(vcardBasic.getElementsByClass("location").get(0).text());
			} catch (IndexOutOfBoundsException e) {
			}
			try {
				toReturn.setIndustry(vcardBasic.getElementsByClass("industry").get(0).text());

			} catch (IndexOutOfBoundsException e) {
			}
			try {
				Element vcardExtended = oneResult.getElementsByClass("vcard-expanded").get(0);
				try {
					toReturn.setCurrentJob(vcardExtended.getElementsByClass("current-content").get(0).text());
				} catch (IndexOutOfBoundsException e) {
				}
				try {
					toReturn.setSummary(vcardExtended.getElementsByClass("summary-content").get(0).text());
				} catch (IndexOutOfBoundsException e) {
				}
			} catch (IndexOutOfBoundsException e) {
			}
			return toReturn;

		} else {
			logger.debug(String.format("Skipping linkedIn result %s", fullName));
			return null;
		}
	}

	@Override
    PersonWthAddress mapProfile(LinkedInternetProfile profile) {
	    PersonWthAddress toReturn = new PersonWthAddress(profile.getFirstName(), profile.getLastName());
	    toReturn.setCity(profile.getLocation());
	    return toReturn;
    }


}
