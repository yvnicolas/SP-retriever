/**
 * 
 */
package com.dynamease.serviceproviders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
     * Filters homonyms At this stage, just check first name and last name matches.
     * 
     * @param person
     * @param profile
     * @return
     * @throws SpInfoRetrievingException
     */
    public boolean matches(Person person, Object profile) throws SpInfoRetrievingException {
        Method first;
        Method last;
        Class<? extends Object> c = profile.getClass();
        try {
            first = c.getMethod("getFirstName");
            last = c.getMethod("getLastName");
        } catch (NoSuchMethodException | SecurityException e) {
            logger.error(String.format("Disambiguation : %s", e.getMessage()));
            throw new SpInfoRetrievingException(String.format("Disambiguation for %s : %s", person.toString(),
                    e.getMessage()), e.getCause());
        }

        try {
            if (person.getFirstName().equalsIgnoreCase((String) first.invoke(profile)))
                if (person.getLastName().equalsIgnoreCase((String) last.invoke(profile)))
                    return true;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.error(String.format("Disambiguation : %s", e.getMessage()));
            throw new SpInfoRetrievingException(String.format("Disambiguation for %s : %s", person.toString(),
                    e.getMessage()), e.getCause());
        }

        return false;

    }

}
