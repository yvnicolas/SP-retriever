/**
 * 
 */
package com.dynamease.profiles;

import com.dynamease.entities.PersonBasic;
import com.dynamease.serviceproviders.ServiceProviders;

/**
 * An entity to store information retrieved on a person from a service provider connection.
 * @author Yves Nicolas
 *
 */
public class SpInfoPerson {

    
    private PersonBasic person;
    private ServiceProviders sp;
    private String info;
    
    
    public SpInfoPerson(PersonBasic person, ServiceProviders sp) {
        this.person = person;
        this.sp = sp;
    }


    public PersonBasic getPerson() {
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
