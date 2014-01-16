/**
 * 
 */
package com.dynamease.serviceproviders;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A retriever for french social network "Copains d'avant"
 * 
 * @author Yves Nicolas
 * 
 */
@Component("CPConnectionRetriever")
public class CpainsConnectionRetriever implements SPConnectionRetriever {

    private static final Logger logger = LoggerFactory.getLogger(CpainsConnectionRetriever.class);

    private String DIRURL = "http://copainsdavant.linternaute.com/";

    final Level1Comparator level1Comparator = new Level1Comparator();
    final Level2Comparator level2Comparator = new Level2Comparator();

    @Autowired
    private HtmlDocRetriever docRetriever;

    @Autowired
    private ProfilePrinter PRINTER;

    /**
     * 
     */
    public CpainsConnectionRetriever() {

    }

    public CpainsConnectionRetriever(String dIRURL) {
        DIRURL = dIRURL;
    }

    public HtmlDocRetriever getDocRetriever() {
        return docRetriever;
    }

    public void setDocRetriever(HtmlDocRetriever docRetriever) {
        this.docRetriever = docRetriever;
    }

    public ProfilePrinter getPRINTER() {
        return PRINTER;
    }

    public void setPRINTER(ProfilePrinter pRINTER) {
        PRINTER = pRINTER;
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

        return DIRURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dynamease.serviceproviders.SPConnectionRetriever#isconnected()
     */
    @Override
    public boolean isconnected() {

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dynamease.serviceproviders.SPConnectionRetriever#getPermissions()
     */
    @Override
    public String getPermissions() {
       
        return "Not Relevant for Copains d'avant";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dynamease.serviceproviders.SPConnectionRetriever#getPersonInfo(com.dynamease.serviceproviders
     * .Person)
     */
    // TODO : Attention, ne traite pas les cas ou Noms et prenoms s'étendent sur deux sub index :
    // exemple NICOLAS Martine
    @Override
    public List<SpInfoPerson> getPersonInfo(Person person) throws SpInfoRetrievingException {

        List<SpInfoPerson> toReturn = new ArrayList<>();

        // Out of the 3 levels : get the first one
        List<String> level1links = findSubLinks(person, DIRURL + "glossary/users/"
                + person.getLastName().toLowerCase().substring(0, 1), level1Comparator);

        // Follow all the relevant links
        for (String linkLevel1 : level1links) {
            List<String> level2links = findSubLinks(person, DIRURL + linkLevel1, level1Comparator);

            // Follow all relevant level2 links
            for (String linklevel2 : level2links) {
                // Now on the 3rd final level, Needs to get all matches
                Elements liste = docRetriever.fetch(DIRURL + linklevel2).getElementsByClass("listelement").get(0)
                        .getElementsByTag("li");
                int firstMatch = findFirstMatchIndex(liste, person, 0, liste.size(), level2Comparator);
                if (firstMatch != -1) {
                    int lastMatch = findLastMatchIndex(liste, person, firstMatch, liste.size(), level2Comparator);

                    // for Each of the matches, adds the relevant information
                    toReturn.addAll(buildProfilesFromList(liste, person, firstMatch, lastMatch));
                }
            }

        }
        return toReturn;

    }

    private List<SpInfoPerson> buildProfilesFromList(Elements liste, Person person, int first, int last) {
        List<SpInfoPerson> toReturn = new ArrayList<>();

        for (int i = first; i <= last; i++) {
            String link = liste.get(i).getElementsByTag("a").get(0).attr("href");
            Elements htmlProfile = docRetriever.fetch(DIRURL + link).getElementsByClass("copains_career__general");
            SpInfoPerson spInfoPerson = new SpInfoPerson(person, ServiceProviders.COPAINSDAVANT);
            spInfoPerson.setInfo(PRINTER.prettyPrintasString(buildProfileFromHtml(htmlProfile)));
            toReturn.add(spInfoPerson);
        }

        return toReturn;
    }

    /**
     * Returns all the matching links to follow for a particular person
     * 
     * @param person
     * @param url
     * @param comparator
     * @return
     */
    private List<String> findSubLinks(Person person, String url, CPPersonComparator comparator) {
        Document doc = docRetriever.fetch(url);
        List<String> toReturn = new ArrayList<>();

        // Strip the not relevant information from the Html documents
        Element lettre2 = doc.getElementsByClass("listelement").get(0);
        Elements lettre = lettre2.getElementsByTag("li");

        // Finds first and Last match
        int firstMatch = findFirstMatchIndex(lettre, person, 0, lettre.size(), level1Comparator);
        int lastMatch;
        if (firstMatch == -1)
            lastMatch = -2;
        else {
            lastMatch = findLastMatchIndex(lettre, person, firstMatch + 1, lettre.size(), level1Comparator);
            if (lastMatch == -1)
                lastMatch = firstMatch;
        }

        logger.debug(String.format("Found %s link to follow for %s %s", lastMatch + 1 - firstMatch,
                person.getFirstName(), person.getLastName()));
        // For all matches, add the link to the result
        for (int i = firstMatch; i <= lastMatch; i++) {
            String link = lettre.get(i).getElementsByTag("a").get(0).attr("href");
            toReturn.add(link);
            logger.debug(String.format("link %s : %s", i, link));
        }

        return toReturn;
    }

    /**
     * Returns the index of the first match in the list, -1 if not found
     * 
     * @param liste
     * @param person
     * @param start
     * @param end
     * @param comparator
     * @return
     */
    private int findFirstMatchIndex(Elements liste, Person person, int start, int end, CPPersonComparator comparator) {

//        logger.debug(String.format("appel FirstMatch. Start = %s, End = %s", start, end));
        int size = end - start;
        // if liste is empty : we are out of bound, name is not present
        if (size == 0) {
            return -1;
        }

        // if liste has only one element, we check the name matches, if not, no match
        if (size == 1) {
            Element e = liste.get(start);
            if (comparator.compare(person, e) == 0)
                return start;
            else
                return -1;
        }

        // liste has at least 2 elements.
        // Pick the middle element
        int middleIndex = start + (int) Math.floor(size / 2);
        Element middle = liste.get(middleIndex);

        // Compare it to person
        int compare = comparator.compare(person, middle);

        // if there is a match the middle is a tentative potential result depending on the recursive
        // result on the start of the list
        if (compare == 0) {
            int getBetterFirst = findFirstMatchIndex(liste, person, 0, middleIndex, comparator);
            if (getBetterFirst == -1)
                return middleIndex;
            else
                return getBetterFirst;

        }

        // Choose the right part of the list on which to recursively apply the algorithm depending
        // on the comparison
        if (compare < 0) {
            return (findFirstMatchIndex(liste, person, start, middleIndex, comparator));
        } else {
            return (findFirstMatchIndex(liste, person, middleIndex + 1, end, comparator));
        }
    }

    /**
     * Returns the index of the first match in the list, -1 if not found
     * 
     * @param liste
     * @param person
     * @param start
     * @param end
     * @param comparator
     * @return
     */
    private int findLastMatchIndex(Elements liste, Person person, int start, int end, CPPersonComparator comparator) {

        // logger.debug(String.format("appel Last Match. Start = %s, End = %s", start, end));
        
        int size = end - start;
        // if liste is empty : we are out of bound, name is not present
        if (size == 0) {
            return -1;
        }

        // if liste has only one element, we check the name matches, if not, no match
        if (size == 1) {
            Element e = liste.get(start);
            if (comparator.compare(person, e) == 0)
                return start;
            else
                return -1;
        }

        // liste has at least 2 elements.
        // Pick the middle element
        int middleIndex = start + (int) Math.floor(size / 2);
        Element middle = liste.get(middleIndex);

        // Compare it to person
        int compare = comparator.compare(person, middle);

        // if there is a match the middle is a tentative potential result depending on the recursive
        // result on the end of the list
        if (compare == 0) {
            int getBetterLast = findLastMatchIndex(liste, person, middleIndex + 1, end, comparator);
            if (getBetterLast == -1)
                return middleIndex;
            else
                return getBetterLast;

        }

        // Choose the right part of the list on which to recursively apply the algorithm depending
        // on the comparison
        if (compare < 0) {
            return (findLastMatchIndex(liste, person, start, middleIndex, comparator));
        } else {
            return (findLastMatchIndex(liste, person, middleIndex + 1, end, comparator));
        }
    }

    // Name Comparison helper methods :

    // Extracting first name and last name on glossary entry pages.
    // Name example would be : NKELETELA BANZOUZI Ngounga ines

    String extractFirst(String name) {
        String last = extractLast(name);
        String toReturn = name.replaceFirst(last + "\\s", "");
        return toReturn;
    }

    String extractLast(String name) {
        String toReturn = name.replaceFirst("\\s[A-Z][a-z].*", "");
        return toReturn;
    }

    // Extracting first name and last name from user profile page
    // name example : Marlène NICOLAS (GAVILAN)
    // In parentheses : spouse name

    String extractfirst2(String name) {
        String toReturn = name.replaceFirst("\\s[A-Z][A-Z].*", "");
        return toReturn;
    }

    String extractlast2(String name) {
        String first = extractfirst2(name);
        return name.replaceFirst(first + "\\s", "");
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

    // Helper Classes use to find the person in the different levels
    abstract class CPPersonComparator {
        abstract int compare(Person p, Element e);
    }

    /**
     * Exemple of entries to compar here is <li>
     * <a href="/glossary/users/n-19"> <strong>NEMER Philippe</strong> à <strong>NERON
     * Damien</strong> </a></li>
     */
    class Level1Comparator extends CPPersonComparator {

        @Override
        int compare(Person person, Element element) {

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

    }

    /**
     * Example entry to compare to is * <li><a href="/p/yves-nicolas-16618197">NICOLAS Yves
     * (LUCON)</a></li>
     * 
     * the town (LUCON) might not be present
     * 
     * @author Yves Nicolas
     * 
     */
    class Level2Comparator extends CPPersonComparator {

        @Override
        int compare(Person p, Element e) {
            String text = e.getElementsByTag("a").get(0).text();
            // skip what is inside parenthesis to get the bare name :
            String name = text.replaceFirst("\\s\\(.*", "");
            return comparePerson(p, name);
        }

    }

    CopainsDAvantProfile buildProfileFromHtml(Elements elements) {

        CopainsDAvantProfile toReturn = new CopainsDAvantProfile();

        // First Name, Last Name , City and Country have their own class and hence can be found
        // directly

        String full_name = elements.select("span.fn").text();
        toReturn.setFirstName(extractfirst2(full_name));
        toReturn.setLastName(extractlast2(full_name));

        toReturn.setCity(elements.select("span.locality").text());
        toReturn.setCountry(elements.select("span.country-name").text());

        // Others are under <li> elements with title on <h4> and description under <p>
        Elements others = elements.select("li");
        for (Element e : others) {
            String title = e.select("h4").text();
            String value = e.select("p").text();

            switch (title) {
            case "Né le :":
            case "Né en :":
                toReturn.setBirthDate(value);
                break;
            case "Description":
                toReturn.setSummary(value);
                break;
            case "Profession :":
                toReturn.setJob(value);
                break;
            case "Enfants :":
                toReturn.setChildren(Integer.parseInt(value));
                break;
            case "Situation familiale :":
                toReturn.setMaritalStatus(value);
                break;
            default:
                break;

            }

        }
        logger.debug(String.format("Extracted Copains d'avant profile : %s", PRINTER.prettyPrintasString(toReturn)));
        return toReturn;
    }

    

    private boolean selected = false;
    
    @Override
    public boolean isSelected() {
       
        return selected;
    }

    @Override
    public void select() {
        selected = true;
        
    }

    @Override
    public void unselect() {
       selected = false;
        
    }

}
