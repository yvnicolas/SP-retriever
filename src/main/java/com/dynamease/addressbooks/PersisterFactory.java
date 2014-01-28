/**
 * 
 */
package com.dynamease.addressbooks;

/**
 * 
 * An interface to get a relevant PersisterFactory
 * @author Yves Nicolas
 *
 */
public interface PersisterFactory {
    
    public ProfilePersister create(String name) ;

}
