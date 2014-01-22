package com.dynamease.addressbooks.impl;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.dynamease.addressbooks.ProfilePersister;

public class CSVProfilePersisterImpl implements ProfilePersister, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(CSVProfilePersisterImpl.class);

    private Class<? extends Object> type;

    private String filePath;

    private ICsvMapWriter mapWriter;

    private String[] csvHeader = null;

    public CSVProfilePersisterImpl() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Minimum constructor. If this constructor is invoked, the header is automatically constructed
     * with all fields from the type
     * 
     * @param type
     *            : the class of objects to be persisted as CSV in files
     * @param filePath
     *            : the absolute file path to write to
     * @throws IOException
     */
    public CSVProfilePersisterImpl(Class<? extends Object> type, String filePath) throws IOException {
        this(type, filePath, null, true);
    }

    /**
     * Alternative constructor, the fields stated in csv header will appear first in the columns in
     * the order they are given, the other fields of the type will be added subsequently with no
     * specific order
     * 
     * @param type
     *            : the class of objects to be persisted as CSV in files
     * @param filePath
     * @param csvHeader
     * @throws IOException
     */
    public CSVProfilePersisterImpl(Class<? extends Object> type, String filePath, String[] csvHeader)
            throws IOException {
        this(type, filePath, csvHeader, true);
    }

    /**
     * Alternative constructor, the fields stated in csv header will appear first in the columns in
     * the order they are given,
     * 
     * @param type
     *            : the class of objects to be persisted as CSV in files
     * @param filePath
     *            : the absolute file path to write to
     * @param csvHeader
     *            : fields as columns to be persisted
     * @param partial
     *            : if false, only the fields given in csvHeader will be persisted in the order they
     *            appear in Csv Header. If true, the other fields of the type will be added
     *            subsequently with no specific order
     * @throws IOException
     */
    public CSVProfilePersisterImpl(Class<? extends Object> type, String filePath, String[] csvHeader, boolean partial)
            throws IOException {
        this.type = type;
        this.filePath = filePath;

        // Opening Map Writer

        mapWriter = new CsvMapWriter(new FileWriter(filePath), CsvPreference.STANDARD_PREFERENCE);

        // Initiate the header

        if (partial) {
            List<String> headersAsList;
            if (csvHeader == null) {
                headersAsList = new ArrayList<>();
            } else {
                headersAsList = new ArrayList<>(Arrays.asList(csvHeader));
            }

            // add non existing fields in the list
            for (Method m : type.getMethods()) {
                if (m.getName().startsWith("set")) {
                    String fieldName = m.getName().substring(3);
                    if (!headersAsList.contains(fieldName))
                        headersAsList.add(fieldName);
                }
            }

            // convert back the headers to an array
            this.csvHeader = new String[headersAsList.size()];
            headersAsList.toArray(this.csvHeader);
        } else
            this.csvHeader = csvHeader;

        if (logger.isDebugEnabled())
            for (int i = 0; i < this.csvHeader.length; i++) {
                logger.info(String.format("Element %d du header :  %s", i, this.csvHeader[i]));
            }

        // write Header
        mapWriter.writeHeader(this.csvHeader);

    }

    @Override
    public void persist(Object Profile) {
        Map<String, String> toWrite = new HashMap<>();
    
        // Building the Attributes map :
        for (int i = 0; i < csvHeader.length; i++) {
            String methodName = "get" + csvHeader[i];
            try {
                
                Method m = type.getMethod(methodName);
                String value = (String) m.invoke(Profile);
                toWrite.put(csvHeader[i], value);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                logger.error(
                        String.format("Error invoking method %s on %s: %s", methodName, Profile.toString(),
                                e.getMessage()), e);
            }
        }
        
        // Do the actual writing :
        try {
            mapWriter.write(toWrite, csvHeader);
        } catch (IOException e) {
           logger.error(String.format("Error calling mapWriter.write : %s", e.getMessage()),e );
        }

    }

    @Override
    public void close() throws IOException {
        mapWriter.close();

    }

}
