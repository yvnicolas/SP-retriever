/**
 * 
 */
package com.dynamease.serviceproviders;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A retriever for french social network "Copains d'avant"
 * 
 * @author Yves Nicolas
 * 
 */
public class CpainsConnectionRetriever implements SPConnectionRetriever {

    private static final Logger logger = LoggerFactory.getLogger(CpainsConnectionRetriever.class);

    private final String DIRURL = "http://copainsdavant.linternaute.com/glossary/users/";

    /**
     * 
     */
    public CpainsConnectionRetriever() {
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dynamease.serviceproviders.SPConnectionRetriever#getConnections()
     */
    @Override
    public List<Person> getConnections() throws SpInfoRetrievingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dynamease.serviceproviders.SPConnectionRetriever#getActiveSP()
     */
    @Override
    public ServiceProviders getActiveSP() {
        return ServiceProviders.COPAINSDAVANT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dynamease.serviceproviders.SPConnectionRetriever#getSPType()
     */
    @Override
    public Class getSPType() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dynamease.serviceproviders.SPConnectionRetriever#getConnectUrl()
     */
    @Override
    public String getConnectUrl() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dynamease.serviceproviders.SPConnectionRetriever#isconnected()
     */
    @Override
    public boolean isconnected() {

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dynamease.serviceproviders.SPConnectionRetriever#getPermissions()
     */
    @Override
    public String getPermissions() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dynamease.serviceproviders.SPConnectionRetriever#getPersonInfo(com.dynamease.serviceproviders
     * .Person)
     */
    @Override
    public List<SpInfoPerson> getPersonInfo(Person person) throws SpInfoRetrievingException {

        String url = DIRURL + person.getLastName().toLowerCase().substring(0, 1);
        try {
            Document doc = Jsoup.connect(url).get();

            // Get the main directory list page for the first letter of the last name
            Element lettre2 = doc.getElementsByClass("listelement").get(0);
            Elements lettre = lettre2.getElementsByTag("li");
            logger.debug(String.format("%s entrees trouvees pour lettre %s", lettre.size(), person.getLastName()
                    .toLowerCase().substring(0, 1)));

            // Find the proper link to follow
            String sublink = findLetterSublink(lettre, person, 0, lettre.size());
            logger.debug(String.format("Corresponding link : %s", sublink));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * On the letter page of the copains d'avant directory, there is an ordered list of links which
     * html code example is
     * 
     * <li>
     * <a href="/glossary/users/n-19"> <strong>NEMER Philippe</strong> à <strong>NERON
     * Damien</strong> </a></li>
     * 
     * 
     * This private method finds the right link to use for the @param Person in the liste of several
     * <li>entries similar to above example,returns the link, in the above example would be
     * "/glossary/users/n-19"
     * 
     * Used indexes as argument as first tries with only lists resulted in following exception:
     * java.lang.ClassCastException: java.util.ArrayList$SubList cannot be cast to
     * org.jsoup.select.Elements
     * 
     * @param liste
     * @param person
     * @return
     */
    private String findLetterSublink(Elements liste, Person person, int start, int end) {

        logger.debug(String.format("appel findLetterSubLink. Start = %s, End = %s", start, end));
        int size = end - start;
        // if liste is empty : we are out of bound, name is not present
        if (size == 0) {
            return null;
        }

        // if liste has only one element, we check the name is inside, if not, no match
        if (size == 1) {
            Element e = liste.get(start);
            if (comparePerson(person, e) == 0)
                return liste.get(start).getElementsByTag("a").get(0).attr("href");
            else
                return null;
        }

        // liste has at least 2 elements.
        // Pick the middle element
        int middleIndex = start + (int) Math.floor(size / 2);
        Element middle = liste.get(middleIndex);

        // Compare it to lastName
        int compare = comparePerson(person, middle);

        // lucky we are : it is the right one
        if (compare == 0) {
            return liste.get(middleIndex).getElementsByTag("a").get(0).attr("href");
        }

        // Choose the right part of the list on which to recursively apply the algorithm depending
        // on the comparison
        if (compare < 0) {
            return (findLetterSublink(liste, person, start, middleIndex));
        } else {
            return (findLetterSublink(liste, person, middleIndex + 1, end));
        }
    }

    /**
     * element looks like this <li><a href="/glossary/users/n-19"> <strong>NEMER Philippe</strong> à
     * <strong>NERON Damien</strong> </a></li>
     * 
     * returns 0 it the person fits inside the range, -1 if it is before, 0, if it is after
     * 
     * @param person
     * @param element
     * @return
     */
    private int comparePerson(Person person, Element element) {

        Elements names = element.getElementsByTag("strong");
        String first = names.get(0).text();

        int compareToFirst = comparePerson(person, first);
        if (compareToFirst == 0) {
            // person equals the first of the 2 names, there is a match
            return 0;
        } else if (compareToFirst == -1) {
            // before the first one means before the intervall
            return -1;
        }

        // We are strictly after the first one, we compare to the last
        String last = names.get(1).text();
        int compareToLast = comparePerson(person, last);
        if (compareToLast == -1) {
            // After the first and before the last : there is a match
            return 0;
        }
        if (compareToLast == 0) {
            // Equals to last : there is a match
            return 0;
        }

        // At this stage, the person is after the interval
        return 1;
    }

    /**
     * example of name would be NEMER Philippe returns 0 if the person matches the name, -1 if
     * before, 1 if after
     * 
     * @param person
     * @param name
     * @return
     */
     private int comparePerson(Person person, String name) {
         String last = extractLast(name);
         String first = extractFirst(name);
  
        int compareToLast = person.getLastName().toLowerCase().compareTo(last.toLowerCase());
        if (compareToLast < 0)
            return -1;
        if (compareToLast > 0)
            return 1;

        // At this stage, equality of last names, we need to compare to first name.
        int compareToFirst = person.getFirstName().toLowerCase().compareTo(first.toLowerCase());
        if (compareToFirst < 0)
            return -1;
        if (compareToFirst > 0)
            return 1;

        // reaching this point means equality.
        return 0;
    }

      String extractFirst(String name) {
         String last = extractLast(name);
         String toReturn = name.replaceFirst(last+"\\s","");
//         logger.debug(String.format("Extracting First name from %s : %s", name, toReturn));
         return toReturn;
     }
     
     String extractLast(String name) {
         String toReturn = name.replaceFirst("\\s[A-Z][a-z].*", "");
//         logger.debug(String.format("Extracting Last Name from %s : %s", name, toReturn));
         return toReturn;
     }
}
