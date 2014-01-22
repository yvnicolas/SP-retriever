/**
 * 
 */
package com.dynamease.addressbooks;


/**
 * An abstract class used to store a list of profiles whatever they come from
 * @author Yves Nicolas
 *
 */
public interface ProfilePersister {
      
    
    public void persist (Object Profile);

}
