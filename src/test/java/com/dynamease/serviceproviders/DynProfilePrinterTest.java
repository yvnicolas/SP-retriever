package com.dynamease.serviceproviders;

import java.io.IOException;

import org.junit.Test;
import org.springframework.social.facebook.api.FacebookProfile;

import com.dynamease.entities.PersonBasic;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class DynProfilePrinterTest {

    private static final DynProfilePrinter underTest = new DynProfilePrinter();
  
    @Test
    public void test() {
        PersonBasic p = new PersonBasic("Yves", "Nicolas");
        System.out.println(underTest.prettyPrintasString(p));

    }

    @Test
    public void testfb() throws JsonParseException, JsonMappingException, IOException {
        FacebookProfile profile = new FacebookProfile("123", "lqsdkjf mlqdjsf", "coucou", "Yves", "Nicolas", null, null);
        System.out.println(underTest.prettyPrintasString(profile));
    }

}
