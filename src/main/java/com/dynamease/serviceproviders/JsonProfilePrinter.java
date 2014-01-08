/**
 * 
 */
package com.dynamease.serviceproviders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A default Profile Printer Implementation using a Json mapper
 * @author Yves Nicolas
 *
 */
public class JsonProfilePrinter implements ProfilePrinter {

    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    /**
     * 
     */
    public JsonProfilePrinter() {
       
    }

    /* (non-Javadoc)
     * @see com.dynamease.serviceproviders.ProfilePrinter#prettyPrintasString(java.lang.Object)
     */
    @Override
    public String prettyPrintasString(Object profile) {
       
        try {
            return MAPPER.writeValueAsString(profile);
        } catch (JsonProcessingException e) {
          
            return ("Unable to Print");
        }
    }

}
