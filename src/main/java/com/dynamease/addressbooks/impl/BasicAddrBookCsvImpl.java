/**
 * 
 */
package com.dynamease.addressbooks.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvBeanReader;
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
    
    private CsvMapReader mapReader = null;
    
    private Map<String, String> currentBuffer;
    
    private boolean emptyBuffer;
    
    private int nberRead;
    
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
        
        // Initiate the MapReader 
        
         mapReader = null;
         try {
             b = new InputStreamReader(new BufferedInputStream(new FileInputStream(input)));
             mapReader = new CsvMapReader(b, CsvPreference.STANDARD_PREFERENCE);
             // beanReader starts reading from line 2 (see above)
             // it is as if we would be reading a file without a header
             mapReader.getHeader(false);
             emptyBuffer = true;
             nberRead = 0;
             
         } catch (IOException e) {
             logger.error("Did not manage to get a working CSVMapReader.", e);
            return;
         }
         finally {
             try {
                 mapReader.close();
             } catch (IOException e1) {
                 logger.error("Problem trying to close the readers", e1);
             }
         }
        
    }
    

    @Override
    public boolean hasNext() {
       if (!emptyBuffer)
           return true;
       else  {
           readNext();
           return !emptyBuffer;
       }        
    }

    private void readNext() {
        if (emptyBuffer) {
            try {
                currentBuffer = mapReader.read(csvHeader);
            } catch (IOException e) {
               logger.error(String.format("Error reading Csv row; %s read so far", nberRead),e);
               return;
            }
            
            if (currentBuffer != null) {
                nberRead+=1;
                emptyBuffer = false;
            }
        }
        
    }

    @Override
    public int getContactNber() {
        return nberRead;
    }

    @Override
    public Person next(Class<?> type) {
        // TODO Auto-generated method stub
        
        Object toReturn;
        
        if (!this.hasNext())
            return null;
        else {
            
        try {
            toReturn = type.newInstance();
            for (String key : currentBuffer.keySet()) {
                String methodName = "set"+key;
                try {
                Method setter = type.getMethod(methodName, String.class);
                try {
                    setter.invoke(toReturn, currentBuffer.get(key));
                } catch (IllegalArgumentException | InvocationTargetException e) {
                    logger.warn(String.format("Unable to set %s to %s", key, currentBuffer.get(key)),e);
                }
                }
                catch (NoSuchMethodException e) {
                    logger.warn(String.format("Trying to invoke method %s on type %s", methodName, type.getName()));
                }
            }
            
            
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(String.format("Can not instantiate object of type %s : %s", type.getName(), e.getMessage()), e);
            return null;
        }
        }
        return (Person) toReturn;
    }

}
