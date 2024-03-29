package com.dynamease.serviceproviders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dynamease.entities.PersonBasic;
import com.dynamease.entities.PersonWthAddress;
import com.dynamease.profiles.ProfilePrinter;
import com.dynamease.profiles.SpInfoPerson;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * An abstract class to retrieve information from a service provider Type T has
 * to be replaced with the profile type dedicated to the service provider
 * 
 * @author Yves Nicolas
 * 
 */
public abstract class DynSPConnectionRetriever<T> implements SPConnectionRetriever<T> {

	private static final Logger logger = LoggerFactory.getLogger(DynSPConnectionRetriever.class);

	private ProfilePrinter PRINTER;

	public ProfilePrinter getPRINTER() {
		return PRINTER;
	}

	@Autowired
	public void setPRINTER(ProfilePrinter pRINTER) {
		PRINTER = pRINTER;
	}

	DynDisambiguer dynDisambiguer;

	public DynDisambiguer getDynDisambiguer() {
		return dynDisambiguer;
	}

	@Autowired
	public void setDynDisambiguer(DynDisambiguer dynDisambiguer) {
		this.dynDisambiguer = dynDisambiguer;
	}

	private boolean selected = false;

	public boolean isSelected() {

		return selected;
	}

	public void select() {
		selected = true;

	}

	public void unselect() {
		selected = false;

	}

	public abstract ServiceProviders getActiveSP();

	@Override
	public abstract Class<? extends Object> getSPType();

	@Override
	public abstract String getConnectUrl();

	@Override
	public abstract boolean isconnected();

	@Override
	public abstract String getPermissions();

	@Override
	public abstract List<PersonBasic> getConnections() throws SpInfoRetrievingException;

	protected abstract List<T> getConnectionsasProfilesSpecific();

	@Override
	public List<T> getConnectionsasProfiles() throws SpInfoRetrievingException {
		if (!this.isconnected()) {
			throw new SpInfoRetrievingException(String.format("Can not get matches if not connected to service Provider %s", this.getActiveSP()
			        .toString()));
		}

		return this.getConnectionsasProfilesSpecific();
	}

	// helper classes for comparaison and filtering
	class Checker implements Predicate<T> {
		private PersonBasic person;

		public Checker(PersonBasic person) {
			this.person = person;
		}

		@Override
		public boolean apply(T arg0) {

			try {
				return dynDisambiguer.matches(person, arg0);
			} catch (SpInfoRetrievingException e) {

				// Disambiguer has not been able to compares the name
				// typically if there is no first name or last name.
				// we return true to allow keeping non name information
				return true;
			}
		}

	}

	class RegionalChecker implements Predicate<T> {

		private String referenceCity;

		public RegionalChecker(String referenceCity) {
			this.referenceCity = referenceCity;
			if (referenceCity.toLowerCase().startsWith("st"))
				this.referenceCity = "SAINT " + referenceCity.substring(3);
			else if (referenceCity.toLowerCase().startsWith("ste"))
				this.referenceCity = "SAINTE " + referenceCity.substring(3);
			else
				this.referenceCity = referenceCity;

		}

		@Override
		public boolean apply(T arg0) {
			return dynDisambiguer.regionalMatch(referenceCity, mapProfile(arg0).getCity());
		}

	}

	class ProfileComparator implements Comparator<T> {
		private PersonWthAddress person;

		public ProfileComparator(PersonWthAddress person) {
			this.person = person;
		}

		@Override
		public int compare(T o1, T o2) {
			return dynDisambiguer.rateWthAddress(person, mapProfile(o2)) - dynDisambiguer.rateWthAddress(person, mapProfile(o1));
		}

	}

	/**
	 * gets the translation
	 * 
	 * @param person
	 * @return
	 */
	abstract PersonWthAddress mapProfile(T profile);

	/**
	 * Meant to retrieve information on a person from a service provider
	 * connection Returns info as a list if several matches are found, the
	 * "best one" being the first one If no match is found, returns an empty
	 * list.
	 * 
	 * @param person
	 * @return
	 * @throws SpInfoRetrievingException
	 */
	@Override
	public SPConnectionMatchesResults getMatches(PersonWthAddress person) throws SpInfoRetrievingException {

		if (!this.isconnected()) {
			throw new SpInfoRetrievingException(String.format("Can not get matches if not connected to service Provider %s", this.getActiveSP()
			        .toString()));
		}

		Checker checker = new Checker(person);
		ProfileComparator profComparator = new ProfileComparator(person);
		SPConnectionMatchesResults toReturn = new SPConnectionMatchesResults();

		List<T> queryResponse = this.getMatchesAsProfiles(person);
		logger.debug(String.format("Found %s matches before homonym check", queryResponse.size()));
		ArrayList<T> onlyNameMatches = new ArrayList<>(Collections2.filter(queryResponse, checker));
		logger.debug(String.format("Found %s matches pos homonym check", onlyNameMatches.size()));

		if (onlyNameMatches.size() != 0) {
			
			// sorting in best match order

			if (logger.isDebugEnabled()) 
				prettyDebug(onlyNameMatches, "Matches before sorting");
			
			Collections.sort(onlyNameMatches, profComparator);
			toReturn.setNameMatches(onlyNameMatches);
	
			if (logger.isDebugEnabled()) 
				prettyDebug(onlyNameMatches, "Matches after sorting");
			
			// Identifying the very likely ones
			RegionalChecker rCheck = new RegionalChecker(person.getCity());
			List<T> likelyMatches = new ArrayList<T>(Collections2.filter(onlyNameMatches, rCheck));
			toReturn.setVeryLikelyMatches(likelyMatches);
			if (logger.isDebugEnabled() && (toReturn.veryLikelyMatchesCount()!=0)) 
				prettyDebug(likelyMatches, "Found following very likely matches");
			}
		
	
		

		
		return toReturn;
	}

	private void prettyDebug(List<T> profileList, String title) {
		logger.debug("------------------------");
		logger.debug(title);
		logger.debug("------------------------");
		int i = 1;
		for (T profile : profileList) {
			logger.debug(String.format("--- Match %s ---", i));
			logger.debug(PRINTER.prettyPrintasString(profile));
			i++;
		}
	}

	abstract List<T> getMatchesAsProfiles(PersonWthAddress person);
//
//	public List<T> FilterRegionalMatches(PersonWthAddress person, List<T> initialMatches) {
//		RegionalChecker rCheck = new RegionalChecker(person.getCity());
//		return new ArrayList<T>(Collections2.filter(initialMatches, rCheck));
//
//	}

	/**
	 * Meant to retrieve information on a person from a service provider
	 * connection Returns info as a list if several matches are found If no
	 * match is found, returns an empty list.
	 * 
	 * @param person
	 * @return
	 * @throws SpInfoRetrievingException
	 */
	public List<SpInfoPerson> getPersonInfo(PersonWthAddress person) throws SpInfoRetrievingException {
		@SuppressWarnings("unchecked")
        List<T> resultsAsProfiles = (List<T>) getMatches(person);
		List<SpInfoPerson> toReturn = new ArrayList<>();
		for (int i = 0; i < resultsAsProfiles.size(); i++) {
			T profile = resultsAsProfiles.get(i);
			SpInfoPerson spInfo = new SpInfoPerson(person, this.getActiveSP());
			toReturn.add(spInfo);
			spInfo.setInfo(PRINTER.prettyPrintasString(profile));

		}
		return toReturn;
	}

}
