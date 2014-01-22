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
        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(PersonWthAddress.class, outputPath);
        assertNotNull(underTest);
        underTest.close();
    }

    @Test
    public void testOrdered() throws IOException {
        String[] header = { "FirstName", "LastName" };
        logger.debug(String
                .format("CSVProfilePersisterImpl Initailisation test With given FirstName and LastName as 2 first columns"));
        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(PersonWthAddress.class, outputPath, header);
        assertNotNull(underTest);
        underTest.close();
    }

    @Test
    public void testOrderedstrict() throws IOException {
        String[] header = { "FirstName", "LastName" };
        logger.debug(String
                .format("CSVProfilePersisterImpl Initailisation test With given FirstName and LastName as 2 first only columns"));
        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(PersonWthAddress.class, outputPath, header,
                false);
        assertNotNull(underTest);
        underTest.close();
    }

    @Test
    public void testEcritureSimple() throws IOException {

        String[] resultats = { "Address,City,Phone,Zip,LastName,FirstName",
                "4 rue de la ravine,Houlbec Cocherel,0610278087,27120,Nicolas,Yves", ",Breuilpont,,,Joly,Pauline" };
        String fileName = System.getProperty("java.io.tmpdir") + "/ecriture1.csv";
        logger.debug(String.format("Test ecriture Simple"));
        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(PersonWthAddress.class, fileName);
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
        CSVProfilePersisterImpl underTest = new CSVProfilePersisterImpl(PersonWthAddress.class, fileName, header, false);
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
}
