package com.dynamease.serviceproviders;

import java.util.List;

import com.dynamease.entities.PersonBasic;

/**
 * @author Yves Nicolas
 *
 */
public interface SPConnectionRetriever {

    public List<PersonBasic> getConnections() throws SpInfoRetrievingException;
    
    public ServiceProviders getActiveSP();
    
    @SuppressWarnings("rawtypes")
    public java.lang.Class getSPType();
    
    public String getConnectUrl();
    
    
   public boolean isconnected();
    
    public String getPermissions();
    /**
     * Meant to retrieve information on a person from a service provider connection
     * Returns info as a list if several matches are found
     * If no match is found, returns an empty list.
     * @param person
     * @return
     * @throws SpInfoRetrievingException 
     */
    public List<SpInfoPerson> getPersonInfo(PersonBasic person) throws SpInfoRetrievingException;
    
    public boolean isSelected();
    
    public void select();
    public void unselect();
}
