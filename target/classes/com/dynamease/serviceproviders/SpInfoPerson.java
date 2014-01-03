/**
 * 
 */
package com.dynamease.serviceproviders;

/**
 * An entity to store information retrieved on a person from a service provider connection.
 * @author Yves Nicolas
 *
 */
public class SpInfoPerson {

    
    private Person person;
    private ServiceProviders sp;
    private String info;
    
    
    public SpInfoPerson(Person person, ServiceProviders sp) {
        this.person = person;
        this.sp = sp;
    }


    public Person getPerson() {
        return person;
    }


    public ServiceProviders getSp() {
        return sp;
    }


    public String getInfo() {
        return info;
    }


    public void setInfo(String info) {
        this.info = info;
    }

 
}
