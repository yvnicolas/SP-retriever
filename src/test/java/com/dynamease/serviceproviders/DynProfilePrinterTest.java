package com.dynamease.serviceproviders;

import java.io.IOException;

import org.junit.Test;
import org.springframework.social.facebook.api.FacebookProfile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DynProfilePrinterTest {

    private static final DynProfilePrinter underTest = new DynProfilePrinter();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String FBJsonProfile = "{\"extraData\":{},\"id\":\"100002493751532\",\"username\":\"pauline.joly.56\",\"name\":\"Pauline Joly\",\"firstName\":\"Pauline\",\"middleName\":null,\"lastName\":\"Joly\",\"gender\":\"female\",\"locale\":\"fr_FR\",\"link\":\"https://www.facebook.com/pauline.joly.56\",\"website\":null,\"email\":null,\"thirdPartyId\":null,\"timezone\":null,\"updatedTime\":1389034391000,\"verified\":null,\"about\":null,\"bio\":null,\"birthday\":null,\"location\":null,\"hometown\":null,\"interestedIn\":null,\"inspirationalPeople\":null,\"languages\":null,\"sports\":null,\"favoriteTeams\":null,\"religion\":null,\"political\":null,\"quotes\":null,\"relationshipStatus\":null,\"significantOther\":null,\"work\":null,\"education\":null,\"favoriteAtheletes\":null} \"";

    @Test
    public void test() {
        Person p = new Person("Yves", "Nicolas");
        System.out.println(underTest.prettyPrintasString(p));

    }

    @Test
    public void testfb() throws JsonParseException, JsonMappingException, IOException {
        FacebookProfile profile = new FacebookProfile("123", "lqsdkjf mlqdjsf", "coucou", "Yves", "Nicolas", null, null);
//        FacebookProfile profile = MAPPER.readValue(FBJsonProfile, FacebookProfile.class);
        System.out.println(underTest.prettyPrintasString(profile));
    }

}
