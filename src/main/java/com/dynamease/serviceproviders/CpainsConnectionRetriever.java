/**
 * 
 */
package com.dynamease.serviceproviders;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
            Elements lettre = doc.getElementsByTag("body");
            System.out.println(String.format("%s entrees trouvees pour lettre %s", lettre.size(), person.getLastName().toLowerCase().substring(0, 1)));
            logger.debug(String.format("%s entrees trouvees pour lettre %s", lettre.size(), person.getLastName().toLowerCase().substring(0, 1)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       

        return null;
    }

}
