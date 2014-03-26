package com.dynamease.serviceproviders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.stereotype.Service;

import com.dynamease.profiles.CopainsDAvantProfile;
import com.dynamease.profiles.InseeProfile;
import com.dynamease.profiles.LinkedInternetProfile;
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
    private SPConnectionRetriever<FacebookProfile> FBConnectionRetriever;
    
    @Autowired 
    private SPConnectionRetriever<LinkedInProfile> LIConnectionRetriever;
    
    @Autowired
    private SPConnectionRetriever<CopainsDAvantProfile> CPConnectionRetriever;
    
//    @Autowired
//    private SPConnectionRetriever<ViadeoProfile> ViadeoConnectionRetriever;
//    
    @Autowired
    private SPConnectionRetriever<LinkedInternetProfile> LINternetRetriever;
    
    @Autowired
    private SPConnectionRetriever <LinkedInternetProfile> VINternetRetriever;
    
    @Autowired
    private SPConnectionRetriever <InseeProfile> InseeRetriever;

  
     public void connectUser(String id) {
     currentUser.connect(id);
         }

    public void disconnectUser() {
       currentUser.disconnect();
    }

 
    public SPConnectionRetriever<? extends Object> getSPConnection(ServiceProviders sp) {
        SPConnectionRetriever<? extends Object> toReturn = null;
      
            switch (sp) {
            case FACEBOOK:
              return FBConnectionRetriever;

            case LINKEDIN:
                return LIConnectionRetriever;
                
            case COPAINSDAVANT :
                return CPConnectionRetriever;
                
//            case VIADEO :
//                return ViadeoConnectionRetriever;
                
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
