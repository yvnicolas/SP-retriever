package com.dynamease.serviceproviders;

import static org.junit.Assert.*;

import org.junit.Test;

public class CpainsConnectionRetrieverTest {
    
    private static final CpainsConnectionRetriever underTest = new CpainsConnectionRetriever();

    @Test
    public void testGetPersonInfo() throws SpInfoRetrievingException {
       Person p = new Person("Yves", "Nicolas");
       assertNull(underTest.getPersonInfo(p));
    }

}
