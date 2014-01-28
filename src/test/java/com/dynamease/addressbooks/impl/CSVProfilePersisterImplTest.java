package com.dynamease.addressbooks.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dynamease.entities.PersonWthAddress;

public class CSVProfilePersisterImplTest {

    private static final Logger logger = LoggerFactory.getLogger(CSVProfilePersisterImplTest.class);

    private static final String outputPath = System.getProperty("java.io.tmpdir") + "/output.csv";

    private static final PersonWthAddress yni = new PersonWthAddress("Yves", "Nicolas");
    private static final PersonWthAddress pjo = new PersonWthAddress("Pauline", "Joly");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        yni.setCity("Houlbec Cocherel");
        pjo.setCity("Breuilpont");
        yni.setAddress("4 rue de la ravine");
        yni.setPhone("0610278087");
        yni.setZip("27120");
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testinit() throws IOException {
        logger.debug(String.format("CSVProfilePersisterImpl Initailisation test basic case"));
        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(outputPath);
        assertNotNull(underTest);
        underTest.close();
    }

//    @Test
//    public void testOrdered() throws IOException {
//        String[] header = { "FirstName", "LastName" };
//        logger.debug(String
//                .format("CSVProfilePersisterImpl Initailisation test With given FirstName and LastName as 2 first columns"));
//        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(PersonWthAddress.class, outputPath, header);
//        assertNotNull(underTest);
//        underTest.close();
//    }
//
//    @Test
//    public void testOrderedstrict() throws IOException {
//        String[] header = { "FirstName", "LastName" };
//        logger.debug(String
//                .format("CSVProfilePersisterImpl Initailisation test With given FirstName and LastName as 2 first only columns"));
//        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(PersonWthAddress.class, outputPath, header,
//                false);
//        assertNotNull(underTest);
//        underTest.close();
//    }

    
    @Test
    public void testIllegalState() throws IOException {
        String fileName = System.getProperty("java.io.tmpdir") + "/ecritureIllegal.csv";
        logger.debug(String.format("Test illegal state"));
        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(fileName);
        try {
            underTest.persist(yni);
            fail("Should raise Illegal State Exception here");
        }
        catch (IllegalStateException e) {}
        finally {
            underTest.close();
        }
    
    }
    @Test
    public void testEcritureSimple() throws IOException {


        String fileName = System.getProperty("java.io.tmpdir") + "/ecriture1.csv";
        logger.debug(String.format("Test ecriture Simple"));
        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(fileName);
        underTest.setTypeToRecord(PersonWthAddress.class,"");
        underTest.persist(yni);
        underTest.persist(pjo);
        underTest.close();

        InputStreamReader f = new InputStreamReader(new FileInputStream(fileName));
        Scanner in = new Scanner(f);
        int lineCount = 0;
        while (in.hasNext()) {
            String line = in.nextLine();
//            assertTrue(resultats[lineCount].equals(line));
            lineCount++;
            logger.debug(String.format("Ligne %s fichier ecrit : %s", lineCount, line));
        }
        assertEquals(3, lineCount);
        in.close();

    }
    
    @Test
    public void testEcritureContrainte() throws IOException {
        String[] resultats = { "FirstName,LastName,City,Phone","Yves,Nicolas,Houlbec Cocherel,0610278087","Pauline,Joly,Breuilpont,"};
        String[] header = { "FirstName", "LastName","City","Phone" };
        String fileName = System.getProperty("java.io.tmpdir") + "/ecriture2.csv";
        logger.debug(String.format("Test ecriture contrainte"));
        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(fileName);
        underTest.setTypeToRecord(PersonWthAddress.class, "", header);
        underTest.persist(yni);
        underTest.persist(pjo);
        underTest.close();

        InputStreamReader f = new InputStreamReader(new FileInputStream(fileName));
        Scanner in = new Scanner(f);
        int lineCount = 0;
        while (in.hasNext()) {
            String line = in.nextLine();
            assertTrue(resultats[lineCount].equals(line));
            lineCount++;
            logger.debug(String.format("Ligne %s fichier ecrit : %s", lineCount, line));
        }
        assertEquals(3, lineCount);
        in.close();

    }
    
    @Test
    public void testEcritureMultiple() throws IOException {
        String[] header1 = { "FirstName", "LastName","City"};
        String[] header2= { "Address", "Phone","Zip"};
        String fileName = System.getProperty("java.io.tmpdir") + "/ecriture3.csv";
        logger.debug(String.format("Test ecriture Mutliple"));
        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(fileName);
        underTest.setTypeToRecord(PersonWthAddress.class, "", header1);
        underTest.setFieldToRecord("LinkedIn_count");
        underTest.setTypeToRecord(PersonWthAddress.class, "LinkedIn_", header2);
 
        
        // first record
        underTest.persistPartial(yni, "");
        underTest.persistPartialOneValue("2","LinkedIn_count");
        underTest.persistPartial(yni, "LinkedIn_");
        underTest.flush();
        
        // second redcord
        underTest.persistPartial(pjo, "");
        underTest.persistPartialOneValue("5","LinkedIn_count");
        underTest.persistPartial(pjo, "LinkedIn_");
        underTest.flush();
        
        underTest.close();

        InputStreamReader f = new InputStreamReader(new FileInputStream(fileName));
        Scanner in = new Scanner(f);
        int lineCount = 0;
        while (in.hasNext()) {
            String line = in.nextLine();
//            assertTrue(resultats[lineCount].equals(line));
            lineCount++;
            logger.debug(String.format("Ligne %s fichier ecrit : %s", lineCount, line));
        }
        assertEquals(3, lineCount);
        in.close();


    }
}
