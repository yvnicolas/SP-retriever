package com.dynamease.serviceproviders;

import static org.junit.Assert.*;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dynamease.serviceproviders.CpainsConnectionRetriever.CPPersonComparator;
import com.dynamease.serviceproviders.CpainsConnectionRetriever.Level1Comparator;


public class CpainsConnectionRetrieverTest {

    private static final Logger logger = LoggerFactory.getLogger(CpainsConnectionRetrieverTest.class);
    private static final CpainsConnectionRetriever underTest = new CpainsConnectionRetriever();
    private static final HtmlDocRetriever DOCRETRIEVER = new HtmlDocRetrieverConnectedImpl();
    private static final DynProfilePrinter PRINTER = new DynProfilePrinter();
    
    @BeforeClass
    public static void beforeTest() {
    	underTest.setDocRetriever(DOCRETRIEVER);
    	underTest.setPRINTER(PRINTER);
    }
    
    // Was used for initial testing during development. Some functions not valid anymore.
//    @Test
//    public void testGetPersonInfo() throws SpInfoRetrievingException {
//        String url = "http://copainsdavant.linternaute.com/glossary/users/n";
//       
//        Level1Comparator comparator = underTest.level1Comparator;
//        Person p = new Person("Yves", "Nicolas");
//        L
//        assertEquals("/glossary/users/n-31", underTest.findSubLink(p, url, comparator));
//      
//
//        // check somebody in the first chunk
//        p = new Person("François", "Nabur");
//        assertEquals("/glossary/users/n-1", underTest.findSubLink(p, url, comparator));
//
//        // check somebody before
//        p = new Person("Arthur", "N.");
//        assertNull(underTest.findSubLink(p,url, comparator));
//
//        // check the last one
//        p = new Person("Jerome", "Nuxa");
//        assertEquals("/glossary/users/n-55", underTest.findSubLink(p, url, comparator));
//
//        // check post last one
//        p = new Person("Jerome", "Nzz");
//        assertNull(underTest.findSubLink(p,url, comparator));
//        
//        // check Equality to last
//        p = new Person("Dhakino", "Nzwange");
//        assertEquals("/glossary/users/n-55", underTest.findSubLink(p, url, comparator));
//
//        // check Equality to first
//        p = new Person("Audrey", "N.");
//        assertEquals("/glossary/users/n-1", underTest.findSubLink(p, url, comparator));
//
//        // cas des espaces dans les noms
//        p = new Person("Thierry", "Nait");
//        assertEquals("/glossary/users/n-4", underTest.findSubLink(p, url, comparator));
//
//    }

    @Test
    public void testExtractName() {
        assertTrue(underTest.extractLast("NICOLAS Yves").equals("NICOLAS"));
        assertTrue(underTest.extractFirst("NICOLAS Yves").equals("Yves"));
        assertTrue(underTest.extractLast("NAIT SAID Hakim").equals("NAIT SAID"));
        assertTrue(underTest.extractFirst("NAIT SAID Hakim").equals("Hakim"));
        assertTrue(underTest.extractLast("NKELETELA BANZOUZI Ngounga ines").equals("NKELETELA BANZOUZI"));
        assertTrue(underTest.extractFirst("NKELETELA BANZOUZI Ngounga ines").equals("Ngounga ines"));
    }
    
    @Test
    public void testExtracName2() {
        assertTrue(underTest.extractlast2("Yves NICOLAS").equals("NICOLAS"));
        assertTrue(underTest.extractfirst2("Yves NICOLAS").equals("Yves"));
        assertTrue(underTest.extractlast2("Marlène NICOLAS (GAVILAN)").equals("NICOLAS (GAVILAN)"));
        assertTrue(underTest.extractfirst2("Marlène NICOLAS (GAVILAN)").equals("Marlène"));
    }
    
    @Test
    public void testl2comparator() {
        
        CPPersonComparator l2cp = underTest.level2Comparator;
        Element e;
        Person p;
        
        e = Jsoup.parse("<li><a href=\"/p/yves-nicolas-16618197\">NICOLAS Yves (LUCON)</a></li>").getElementsByTag("li").get(0);
        p = new Person("Yves", "Nicolas");
        assertEquals(0, l2cp.compare(p, e));
        
        p = new Person("Dhakino", "Nzwange");
        assertEquals(1, l2cp.compare(p, e));
        
        p = new Person ("Ngounga ines", "NAELETELA BANZOUZI");
        assertEquals(-1, l2cp.compare(p, e));
        
    }
    
    @Test
    public void testBuildProfile() {
        Elements e = DOCRETRIEVER.fetch("http://copainsdavant.linternaute.com/p/marlene-nicolas-gavilan-6003415").getElementsByClass("copains_career__general");
        underTest.buildProfileFromHtml(e);
    }
    
    
    @Test
    public void testGetInfoPerson() throws SpInfoRetrievingException {
        Person p;
        List<SpInfoPerson> result;
        
        // Check a person with lots of homonyms, splitting on several page
        p = new Person("Yves", "Nicolas");
        result = underTest.getPersonInfo(p);
        assertEquals(24, result.size());
        logger.debug(String.format("Found %s entries for Yves Nicolas", result.size()));
        
        // An entry which doesnt exist
        p = new Person("Derrick", "Nicolau");
        result = underTest.getPersonInfo(p);
        assertEquals(0, result.size());
        logger.debug(String.format("Found %s entries for Derrick Nicolau", result.size()));
        
        // A name with only one Entry
        p = new Person("Cosma", "Nicolau");
        result = underTest.getPersonInfo(p);
        assertEquals(1, result.size());
        logger.debug(String.format("Found %s entries for  Cosma Nicolau", result.size()));
    }
    

}
