package com.dynamease.serviceproviders;

import java.util.ArrayList;
import java.util.Collection;
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
public abstract class DynSPConnectionRetriever<T> implements SPConnectionRetriever {

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

	@SuppressWarnings("rawtypes")
	public abstract java.lang.Class getSPType();

	public abstract String getConnectUrl();

	public abstract boolean isconnected();

	public abstract String getPermissions();

	public abstract List<PersonBasic> getConnections() throws SpInfoRetrievingException;

	protected abstract List<T> getConnectionsasProfilesSpecific();

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

			// TODO Auto-generated method stub
			try {
				return dynDisambiguer.matches(person, arg0);
			} catch (SpInfoRetrievingException e) {
				return false;
			}
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
	public List<T> getMatches(PersonWthAddress person) throws SpInfoRetrievingException {

		if (!this.isconnected()) {
			throw new SpInfoRetrievingException(String.format("Can not get matches if not connected to service Provider %s", this.getActiveSP()
			        .toString()));
		}

		Checker checker = new Checker(person);
		ProfileComparator profComparator = new ProfileComparator(person);

		List<T> queryResponse = this.getMatchesAsProfiles(person);
		logger.debug(String.format("Found %s matches before homonym check", queryResponse.size()));
		Collection<T> onlyNameMatches = Collections2.filter(queryResponse, checker);
		logger.debug(String.format("Found %s matches pos homonym check", onlyNameMatches.size()));
		List<T> toReturn = new ArrayList<>(onlyNameMatches);
		if (toReturn.size() != 0) {
			logger.debug("------------------------");
			logger.debug("Matches before sorting :");
			logger.debug("------------------------");

			if (logger.isDebugEnabled()) {
				int i = 1;
				for (T profile : toReturn) {
					logger.debug(String.format("--- Match %s ---", i));
					logger.debug(PRINTER.prettyPrintasString(profile));
					i++;
				}
			}
			Collections.sort(toReturn, profComparator);

			logger.debug("------------------------");
			logger.debug("Matches after sorting :");
			logger.debug("------------------------");

			if (logger.isDebugEnabled()) {
				int i = 1;
				for (T profile : toReturn) {
					logger.debug(String.format("--- Match %s ---", i));
					logger.debug(PRINTER.prettyPrintasString(profile));
					i++;
				}
			}
		}
		return toReturn;
	}

	abstract List<T> getMatchesAsProfiles(PersonWthAddress person);

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
		List<T> resultsAsProfiles = getMatches(person);
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
