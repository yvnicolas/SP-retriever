package com.dynamease.serviceproviders;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.linkedin.api.LinkedInProfile;

import com.dynamease.entities.PersonBasic;

public class DynDisambiguerTest {

    
    private static final DynDisambiguer underTest = new DynDisambiguer();
    
    @Test
    public void testfb() throws SpInfoRetrievingException {
        PersonBasic p = new PersonBasic("Yves", "Nicolas");
        FacebookProfile profile = new FacebookProfile("123", "lqsdkjf mlqdjsf", "coucou", "Yves", "Nicolas", null, null);
        assertTrue (underTest.matches(p, profile));
        p.setLastName("niCOlas");
        assertTrue (underTest.matches(p, profile));
        p.setFirstName("yvES");
        assertTrue (underTest.matches(p, profile));
        p.setLastName("blurb");
        assertFalse (underTest.matches(p, profile));
        p.setLastName("niCOlas");
        p.setFirstName("lsdfkj");
        assertFalse (underTest.matches(p, profile));
    }
    
    @Test
    public void testli() throws SpInfoRetrievingException {
        PersonBasic p = new PersonBasic("Yves", "Nicolas");
        LinkedInProfile profile = new LinkedInProfile("123", "Yves", "Nicolas", null, "lsdfkjsd", null, null, null);
        assertTrue (underTest.matches(p, profile));
        p.setLastName("niCOlas");
        assertTrue (underTest.matches(p, profile));
        p.setFirstName("yvES");
        assertTrue (underTest.matches(p, profile));
        p.setLastName("blurb");
        assertFalse (underTest.matches(p, profile));
        p.setLastName("niCOlas");
        p.setFirstName("lsdfkj");
        assertFalse (underTest.matches(p, profile));
    }

}
