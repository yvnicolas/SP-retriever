package com.dynamease.addressbooks;


/**
 * Interface which gives a uniform access to an address book so that it can be categorized and used
 * inside Dynamease Server. To enable using partial recovery of information to optimize network
 * access for social networks, the address book is seen as a list on which we can iterate
 * 
 * This is the basic interface set used with no possibility of "jumping" inside the address book
 * 
 * @author Yves Nicolas
 * 
 */
public interface DynExternalAddressBookBasic {
   
    
    /**
     * Tells whether we have reached the end of the addressbook.
     * @return
     */
    public boolean hasNext();


 
    /**
     * Gives the total number of entries in the address book.
     * 
     */
    public int getContactNber();

    /**
     * returns as a new instance the next Person in the address Book
     */
    public Object next(Class<?> type);

  

}
