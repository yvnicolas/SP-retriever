package com.dynamease.serviceproviders;

import java.util.List;

/**
 * @author Yves Nicolas
 *
 */
public interface SPConnectionRetriever {

    public List<Person> getConnections() throws SpInfoRetrievingException;
    
    public ServiceProviders getActiveSP();
    
    @SuppressWarnings("rawtypes")
    public java.lang.Class getSPType();
    
    public String getConnectUrl();
    
    
   public boolean isconnected();
    
    public String getPermissions();
    /**
     * Meant to retrieve information on a person from a service provider connection
     * Returns info as a list if several matches are found
     * @param person
     * @return
     * @throws SpInfoRetrievingException 
     */
    public List<SpInfoPerson> getPersonInfo(Person person) throws SpInfoRetrievingException;
}
