package com.dynamease.addressbooks.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dynamease.entities.PersonWthAddress;
import com.dynamease.profiles.DynProfilePrinter;
import com.dynamease.profiles.ProfilePrinter;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AddrBookTestConfig.class})
public class BasicAddrBookCsvImplTest {
    
    
    private static final Logger logger = LoggerFactory.getLogger(BasicAddrBookCsvImplTest.class);

    private static File file1;
    private static ProfilePrinter printer;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        file1 = new ClassPathResource("Test Dynamease.csv").getFile();
        printer = new DynProfilePrinter();
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testInit() throws FileNotFoundException {
        BasicAddrBookCsvImpl underTest = new BasicAddrBookCsvImpl(file1);
        assertNotNull(underTest);
    }
    
    @Test
    public void testParcours() throws FileNotFoundException {
        BasicAddrBookCsvImpl underTest = new BasicAddrBookCsvImpl(file1);
        int compteur=0;
        while (underTest.hasNext()) {
            compteur += 1;
            assertEquals(compteur, underTest.getContactNber());
            Object p = underTest.next(PersonWthAddress.class);
            logger.info(String.format("Entree %s : %s", compteur, printer.prettyPrintasString(p)));
            
        }
        assertEquals(10, compteur);
    }

}
