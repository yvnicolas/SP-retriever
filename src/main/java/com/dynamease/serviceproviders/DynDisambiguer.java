/**
 * 
 */
package com.dynamease.serviceproviders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dynamease.entities.Person;
import com.dynamease.entities.PersonBasic;
import com.dynamease.entities.PersonWthAddress;

/**
 * to filter homonyms
 * 
 * @author Yves Nicolas
 * 
 */
@Service
public class DynDisambiguer {

	private static final Logger logger = LoggerFactory.getLogger(DynDisambiguer.class);

	/**
     * 
     */
	public DynDisambiguer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Filters homonyms At this stage, just check first name and last name
	 * matches.
	 * 
	 * @param person
	 * @param profile
	 * @return
	 * @throws SpInfoRetrievingException
	 */
	public boolean matches(PersonBasic person, Object profile) throws SpInfoRetrievingException {
		Method first;
		Method last;
		Class<? extends Object> c = profile.getClass();
		try {
			first = c.getMethod("getFirstName");
			last = c.getMethod("getLastName");
		} catch (NoSuchMethodException | SecurityException e) {
			logger.error(String.format("Disambiguation : %s", e.getMessage()));
			throw new SpInfoRetrievingException(String.format("Disambiguation for %s : %s", person.toString(), e.getMessage()), e.getCause());
		}

		try {
			if (person.getFirstName().equalsIgnoreCase((String) first.invoke(profile)))
				if (person.getLastName().equalsIgnoreCase((String) last.invoke(profile)))
					return true;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error(String.format("Disambiguation : %s", e.getMessage()));
			throw new SpInfoRetrievingException(String.format("Disambiguation for %s : %s", person.toString(), e.getMessage()), e.getCause());
		}

		return false;

	}

	/**
	 * Purpose is to rate by best match the different matches if several. The
	 * higher the rate, the best is the match between the 2 persons. A this
	 * stage, returns always the same number
	 * 
	 * @param p
	 * @param profile
	 * @return
	 */
	public int rate(Person p, Object profile) {

		// TODO : do a real rating
		return 1;
	}

	public int rateWthAddress(PersonWthAddress refPerson, PersonWthAddress contact) {
		int result = 0;
		try {
			if (stringMatch(refPerson.getFirstName(), contact.getFirstName()))
				result++;
		} catch (NullPointerException e) {
		}
		try {
			if (stringMatch(refPerson.getLastName(), contact.getLastName()))
				result++;
		} catch (NullPointerException e) {
		}
		try {
			if (stringMatch(refPerson.getAddress(), contact.getAddress()))
				result++;
		} catch (NullPointerException e) {
		}
		try {
			if (stringMatch(refPerson.getZip(), contact.getZip()))
				result++;
		} catch (NullPointerException e) {
		}
		try {
			if (stringMatch(refPerson.getCity(), contact.getCity()))
				result++;
		} catch (NullPointerException e) {
		}
		try {
			if (stringMatch(refPerson.getPhone(), contact.getPhone()))
				result++;
		} catch (NullPointerException e) {
		}

		return result;

	}

	private boolean stringMatch(String s1, String s2) {
		return (s1.toLowerCase().equals(s2.toLowerCase()));
	}

	public boolean regionalMatch(String referenceCity, String city) {
		if (stringMatch(referenceCity, city))
			return true;
		if (city.toLowerCase().contains("grenoble"))
			return true;
		if (city.toLowerCase().contains("chambery"))
			return true;
		if (city.toLowerCase().contains("lyon"))
			return true;
	
		return false;
	}

}
