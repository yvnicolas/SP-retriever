package com.dynamease.serviceproviders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dynamease.entities.PersonBasic;
import com.dynamease.entities.PersonWthAddress;
import com.dynamease.profiles.LinkedInternetProfile;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Component("VINternetRetriever")
public class ViadeoInternetConnectionRetriever extends DynSPConnectionRetriever<LinkedInternetProfile> {

	private static final Logger logger = LoggerFactory.getLogger(ViadeoInternetConnectionRetriever.class);

	private static final String DOT = ".";

	private String DIRURL = "http://fr.viadeo.com/fr/profile/";

	public ViadeoInternetConnectionRetriever() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ServiceProviders getActiveSP() {

		return ServiceProviders.VIADEOPUBLIC;
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

		return "Not relevant for public Internet access to Viadeo";
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
	List<LinkedInternetProfile> getMatchesAsProfiles(PersonBasic person) {

		List<LinkedInternetProfile> toReturn = new ArrayList<>();

		int index = 0;
		boolean fin = false;
		Elements resultList;

		String url = "";
		try {
			do {
				url = getUrltoFetch(person, index);
				try {
					resultList = Jsoup.connect(url).get().getElementsByClass("cardContainers");
					logger.debug(String.format("%s %s profile Nber %s fetched successfully", person.getFirstName(), person.getLastName(), index + 1));
					toReturn.add(getProfileFromHtml(resultList.get(0), person));
					index++;
				} catch (HttpStatusException e) {
					switch (e.getStatusCode()) {
					case 403:
					case 410:
						logger.debug(String.format("%s %s profile Nber %s not publicly accessible", person.getFirstName(), person.getLastName(),
						        index + 1));
						index++;
						break;
					case 404:
						fin = true;
						break;
					default:
						throw new IOException(String.format("Unexpected HTTP Exception %s", e.getStatusCode()), e);
					}
				}

			} while (!fin);

		} catch (IOException e) {
			logger.error(String.format("IO Error fetching URL %s : %s", url, e.getMessage()));

		}

		return toReturn;
	}

	private String getUrltoFetch(PersonBasic person, int index) {
		StringBuilder sb = new StringBuilder(DIRURL);
		sb.append(person.getFirstName().toLowerCase());
		sb.append(DOT);
		sb.append(person.getLastName().toLowerCase());
		if (index > 0)
			sb.append(index);
		return sb.toString();
	}

	private LinkedInternetProfile getProfileFromHtml(Element info, PersonBasic person) {
		
		LinkedInternetProfile toReturn = new LinkedInternetProfile();
		toReturn.setFirstName(info.getElementsByClass("firstname").get(0).text());
		toReturn.setLastName(info.getElementsByClass("lastname").get(0).text());
		try {
		toReturn.setLocation(info.select("span[itemprop=addresslocality]").first().text().trim());
		}
		catch (NullPointerException e) {}
		toReturn.setCurrentJob(info.getElementsByTag("h3").get(0).text());
		

		return toReturn;
	}

	@Override
	PersonWthAddress mapProfile(LinkedInternetProfile profile) {
		PersonWthAddress toReturn = new PersonWthAddress(profile.getFirstName(), profile.getLastName());
		toReturn.setCity(profile.getLocation());
		return toReturn;
	}

}
