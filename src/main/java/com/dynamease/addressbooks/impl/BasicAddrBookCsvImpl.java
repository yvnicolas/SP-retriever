/**
 * 
 */
package com.dynamease.addressbooks.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.dynamease.addressbooks.DynExternalAddressBookBasic;
import com.dynamease.entities.Person;


/**
 * @author Yves Nicolas
 *
 */
public class BasicAddrBookCsvImpl implements DynExternalAddressBookBasic {
    
    private static final Logger logger = LoggerFactory.getLogger(BasicAddrBookCsvImpl.class);

    @Autowired
    private DynHeaderNormalizer headerDico;
    
    private File input;
    
    private String[] csvHeader = null;
    
    private CsvBeanReader beanReader=null;
    
    /**
     * 
     */
    public BasicAddrBookCsvImpl(File linkToFile) {
        this.input = linkToFile;
        initBasicAddrBookCsvImpl();
    }

    private void initBasicAddrBookCsvImpl() {
       
        DynHeaderNormalizer headerDico = new DynHeaderNormalizer();

        // Reading the Header. A CsvListReader object is used here as it can
        // read a variable number of columns in the first line (see
        // http://supercsv.sourceforge.net/readers.html)
        CsvListReader listReader = null;
        InputStreamReader b = null;
        try {
            b = new InputStreamReader(new BufferedInputStream(new FileInputStream(input)));
            listReader = new CsvListReader(b, CsvPreference.STANDARD_PREFERENCE);
            csvHeader = listReader.getHeader(true);
        } catch (IOException e) {
            logger.info("Did not manage to get the Csv Header", e);
        } finally {
            try {
                listReader.close();
            } catch (IOException e1) {
                logger.info("Problem trying to close the readers", e1);
                return;
            }
        }

        // Header Normalization
        for (int i = 0; i < csvHeader.length; i++) {
            logger.info(String.format("Element %d du header :  %s", i, csvHeader[i]));
            csvHeader[i] = headerDico.lookup(csvHeader[i]);
        }

        // Trace the header after Normalization
        if (logger.isDebugEnabled()) {
            logger.info("Header apres normalisation");
            for (int i = 0; i < csvHeader.length; i++) {
                if (csvHeader[i] != null) {
                    logger.info(String.format("Element %d du header :  %s", i, csvHeader[i]));
                }
            }
        }
        
        // Initiate the CsvBeanReader for proper access
        
         beanReader = null;
         try {
             b = new InputStreamReader(new BufferedInputStream(new FileInputStream(input)));
             beanReader = new CsvBeanReader(b, CsvPreference.STANDARD_PREFERENCE);
             // beanReader starts reading from line 2 (see above)
             // it is as if we would be reading a file without a header
             beanReader.getHeader(false);
         } catch (IOException e) {
             logger.error("Did not manage to get a working CsvBeanReader.", e);
            return;
         }
         finally {
             try {
                 beanReader.close();
             } catch (IOException e1) {
                 logger.error("Problem trying to close the readers", e1);
             }
         }
        
    }

    @Override
    public boolean hasNext() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getContactNber() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Person next(Class<?> type) {
        // TODO Auto-generated method stub
        return null;
    }

}
