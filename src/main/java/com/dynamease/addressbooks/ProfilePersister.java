/**
 * 
 */
package com.dynamease.addressbooks;

import java.io.Closeable;
import java.io.IOException;

/**
 * An abstract class used to store a list of profiles whatever they come from
 * 
 * @author Yves Nicolas
 * 
 */
public interface ProfilePersister extends Closeable{

    /**
     * Adds the type fields to the list of fields to be recorded, adding the prefix string to
     * differentiate from other fields that would have the same name in differente types. Should
     * record the fields for which a getField method is available
     * 
     * @param type
     * @param prefix
     * @throws IllegalStateException
     *             : if the status of the writer does not enable to add more types to be persisted.
     */
    public void setTypeToRecord(Class<? extends Object> type, String prefix) throws IllegalStateException;

    /**
     * Adds the type fields to the list of fields to be recorded, adding the prefix string to
     * differentiate from other fields that would have the same name in differente types. Should
     * record the fields for which a getField method is available if and only if they are part of
     * the fields array passed as an argument.
     * If relevant for the implementation, the fields order should be kept from the fields array.
     * 
     * @param type
     * @param prefix
     * @param fields
     * @throws IllegalStateException
     */
    public void setTypeToRecord(Class<? extends Object> type, String prefix, String fields[])
            throws IllegalStateException;
    
    
    
    /**
     * @param Field
     * @throws IllegalStateException
     */
    public void setFieldToRecord(String Field) throws IllegalStateException;
    

    /**
     * This method enable to persist one object as a whole. Equivalent to persist(profile,"");
     * flush();
     * 
     * @param Profile
     * @throws IOException 
     */
    public void persist(Object Profile) throws IOException;

    /**
     * Enable to add several sub information objects on a profile that will appear only once on the
     * final project. the prefix will be added to all files of the object in the fields names of the
     * persistence mechanism.
     * 
     * @param Profile
     * @param prefix
     * @throws IOException 
     */
    public void persistPartial(Object Profile, String prefix) throws IOException;
    
    /**
     * Enable to add one String information to the object currently being persisted.
     * @param value
     * @param field
     * @throws IOException
     */
    public void persistPartialOneValue(String value, String field) throws IOException;
    
    /**
     * Do the actual persistence writing after several persistPartial
     */
    public void flush();

}
