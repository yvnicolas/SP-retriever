package com.dynamease.serviceproviders;

import java.util.List;

import com.dynamease.entities.PersonBasic;
import com.dynamease.entities.PersonWthAddress;
import com.dynamease.profiles.SpInfoPerson;

/**
 * An interface to retrive information from a service provider
 * Type T has to be replaced with the profile type dedicated to the service provider
 * @author Yves Nicolas
 *
 */
public interface SPConnectionRetriever<T>{

    public List<PersonBasic> getConnections() throws SpInfoRetrievingException;
    
    public List<T> getConnectionsasProfiles() throws SpInfoRetrievingException;
    
    public ServiceProviders getActiveSP();
    
      public Class<? extends Object> getSPType();
    
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
    public List<SpInfoPerson> getPersonInfo(PersonWthAddress person) throws SpInfoRetrievingException;
    
    public List<T> getMatches(PersonWthAddress person) throws SpInfoRetrievingException;
    
    public List<T> FilterRegionalMatches (PersonWthAddress person, List<? extends Object> initialMatches);

    public boolean isSelected();
    
    public void select();
    public void unselect();

	
}
