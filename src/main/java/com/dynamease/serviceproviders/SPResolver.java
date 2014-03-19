package com.dynamease.serviceproviders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dynamease.serviceproviders.user.CurrentUserContext;

/**
 * A factory that can find from the service provider enum the right SPConnectionRetriever
 * Implementation
 * 
 * @author Yves Nicolas
 * 
 */
@Service
public class SPResolver {

    public SPResolver() {
    }



    @Autowired
    private CurrentUserContext currentUser;

    @Autowired
    private SPConnectionRetriever FBConnectionRetriever;
    
    @Autowired 
    private SPConnectionRetriever LIConnectionRetriever;
    
    @Autowired
    private SPConnectionRetriever CPConnectionRetriever;
    
    @Autowired
    private SPConnectionRetriever ViadeoConnectionRetriever;
    
    @Autowired
    private SPConnectionRetriever LINternetRetriever;
    
    @Autowired
    private SPConnectionRetriever VINternetRetriever;
    
    @Autowired
    private SPConnectionRetriever InseeRetriever;

  
     public void connectUser(String id) {
     currentUser.connect(id);
         }

    public void disconnectUser() {
       currentUser.disconnect();
    }

 
    public SPConnectionRetriever getSPConnection(ServiceProviders sp) {
        SPConnectionRetriever toReturn = null;
      
            switch (sp) {
            case FACEBOOK:
              return FBConnectionRetriever;

            case LINKEDIN:
                return LIConnectionRetriever;
                
            case COPAINSDAVANT :
                return CPConnectionRetriever;
                
            case VIADEO :
                return ViadeoConnectionRetriever;
                
            case LINKEDINPUBLIC :
            	return LINternetRetriever;
            	
            case VIADEOPUBLIC :
            	return VINternetRetriever;
            	
			case INSEE:
				return InseeRetriever;
				
			default:
				break;

            }
        
        return toReturn;
    }
}
