package com.dynamease.serviceproviders;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CpainsConnectionRetrieverTest {

    private static final Logger logger = LoggerFactory.getLogger(CpainsConnectionRetrieverTest.class);
    private static final CpainsConnectionRetriever underTest = new CpainsConnectionRetriever("src/test/copains-n.htm");
    private static final HtmlDocRetriever DOCRETRIEVER = new HtmlDocRetrieverFileInput();
    
    @BeforeClass
    public static void beforeTest() {
    	underTest.setDocRetriever(DOCRETRIEVER);
    }
    
    @Test
    public void testGetPersonInfo() throws SpInfoRetrievingException {
        Person p = new Person("Yves", "Nicolas");
        assertNull(underTest.getPersonInfo(p));

        // check somebody in the first chunk
        p = new Person("François", "Nabur");
        assertNull(underTest.getPersonInfo(p));

        // check somebody before
        p = new Person("Arthur", "N.");
        assertNull(underTest.getPersonInfo(p));

        // check the last one
        p = new Person("Jerome", "Nuxa");
        assertNull(underTest.getPersonInfo(p));

        // check post last one
        p = new Person("Jerome", "Nzz");
        assertNull(underTest.getPersonInfo(p));

        // check Equality to last
        p = new Person("Dhakino", "Nzwange");
        assertNull(underTest.getPersonInfo(p));

        // check Equality to first
        p = new Person("Audrey", "N.");
        assertNull(underTest.getPersonInfo(p));

        // cas des espaces dans les noms
        p = new Person("Thierry", "Nait");
        assertNull(underTest.getPersonInfo(p));

    }

    @Test
    public void testExtractName() {
        assertTrue(underTest.extractLast("NICOLAS Yves").equals("NICOLAS"));
        assertTrue(underTest.extractFirst("NICOLAS Yves").equals("Yves"));
        assertTrue(underTest.extractLast("NAIT SAID Hakim").equals("NAIT SAID"));
        assertTrue(underTest.extractFirst("NAIT SAID Hakim").equals("Hakim"));
        assertTrue(underTest.extractLast("NKELETELA BANZOUZI Ngounga ines").equals("NKELETELA BANZOUZI"));
        assertTrue(underTest.extractFirst("NKELETELA BANZOUZI Ngounga ines").equals("Ngounga ines"));
    }

}
