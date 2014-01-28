package com.dynamease.addressbooks.impl;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
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

    private ICsvMapWriter mapWriter;

    private String[] csvHeader = null;

    private static final String NULLSTRING = "";

    private Map<String, String> partialBeingWritten = null;

    // Once the writing has begun , no elements of the headers can be added anymore.
    private boolean writingBegan = false;

    public CSVProfilePersisterImpl(String filePath) throws IOException {
        this.mapWriter = new CsvMapWriter(new FileWriter(filePath), CsvPreference.STANDARD_PREFERENCE);
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
    // public CSVProfilePersisterImpl(String filePath, String[] csvHeader, boolean partial) throws
    // IOException {
    //
    // // Opening Map Writer
    //
    // mapWriter = new CsvMapWriter(new FileWriter(filePath), CsvPreference.STANDARD_PREFERENCE);
    //
    // // Initiate the header
    //
    // if (partial) {
    // List<String> headersAsList;
    // if (csvHeader == null) {
    // headersAsList = new ArrayList<>();
    // } else {
    // headersAsList = new ArrayList<>(Arrays.asList(csvHeader));
    // }
    //
    // // add non existing fields in the list
    // for (Method m : type.getMethods()) {
    // if (m.getName().startsWith("set")) {
    // String fieldName = m.getName().substring(3);
    // if (!headersAsList.contains(fieldName))
    // headersAsList.add(fieldName);
    // }
    // }
    //
    // // convert back the headers to an array
    // this.csvHeader = new String[headersAsList.size()];
    // headersAsList.toArray(this.csvHeader);
    // } else
    // this.csvHeader = csvHeader;
    //
    // if (logger.isDebugEnabled())
    // for (int i = 0; i < this.csvHeader.length; i++) {
    // logger.info(String.format("Element %d du header :  %s", i, this.csvHeader[i]));
    // }
    //
    // // write Header
    // mapWriter.writeHeader(this.csvHeader);
    //
    // }

    private void initWriting() throws IOException {

        // write Header

        mapWriter.writeHeader(this.csvHeader);
        this.writingBegan = true;

    }

    private void checkReadyForWriting() throws IOException {
        if (this.csvHeader == null) {
            throw new IllegalStateException("CSV Profile Writer need a set up for types before writing");
        }
        if (!writingBegan)
            try {
                initWriting();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                logger.error(String.format("Error initializing writing : %s", e1.getMessage()));
                throw (e1);
            }

    }

    @Override
    public void persist(Object Profile) throws IOException {
        checkReadyForWriting();

        Map<String, String> toWrite = new HashMap<>();

        // Building the Attributes map :
        for (int i = 0; i < csvHeader.length; i++) {
            String methodName = "get" + csvHeader[i];
            try {

                Method m = Profile.getClass().getMethod(methodName);
                String value = (String) m.invoke(Profile);
                toWrite.put(csvHeader[i], value);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                logger.error(
                        String.format("Error invoking method %s on %s: %s", methodName, Profile.toString(),
                                e.getMessage()));
            }
        }

        // Do the actual writing :
        try {
            mapWriter.write(toWrite, csvHeader);
        } catch (IOException e) {
            logger.error(String.format("Error calling mapWriter.write : %s", e.getMessage()));
            throw (e);
        }

    }

    @Override
    public void close() throws IOException {
        mapWriter.close();

    }

    @Override
    public void persistPartial(Object profile, String prefix) throws IOException {

        checkReadyForWriting();
        
        // initiate the map that will finally be written if writing process has not begun
        if (partialBeingWritten == null) {
            partialBeingWritten = new HashMap<>();
        }

        Class<? extends Object> profileClass = profile.getClass();

        for (int i = 0; i < csvHeader.length; i++) {
            if (csvHeader[i].startsWith(prefix)) {
                String methodName = "get" + csvHeader[i].replaceFirst(prefix, NULLSTRING);
                try {

                    Method m = profileClass.getMethod(methodName);
                    String value = (String) m.invoke(profile);
                    partialBeingWritten.put(csvHeader[i], value);
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    logger.error(
                            String.format("Error invoking method %s on %s: %s", methodName, profile.toString(),
                                    e.getMessage()));
                }
            }
        }

    }
    
    @Override
    public void persistPartialOneValue(String value, String field) throws IOException {
        checkReadyForWriting();
        
        // initiate the map that will finally be written if writing process has not begun
        if (partialBeingWritten == null) {
            partialBeingWritten = new HashMap<>();
        }
        
        if (Arrays.asList(csvHeader).contains(field)) {
            partialBeingWritten.put(field, value);
        }
        else {
            logger.warn(String.format("Field %s not present in CSV Headers", field));
        }

        
    }

    @Override
    public void flush() {
        if (partialBeingWritten != null) {
            try {
                mapWriter.write(partialBeingWritten, csvHeader);
                partialBeingWritten = null;
            } catch (IOException e) {
                logger.error(
                        String.format("Error calling mapWriter.write while flushing partial : %s", e.getMessage()), e);
            }
        }

    }

    @Override
    public void setTypeToRecord(Class<? extends Object> type, String prefix) throws IllegalStateException {

        if (writingBegan)
            throw new IllegalStateException(
                    "Can not add a type to record in Profile persister : writing has already begun");

        List<String> headersAsList;
        if (csvHeader == null) {
            headersAsList = new ArrayList<>();
        } else {
            headersAsList = new ArrayList<>(Arrays.asList(csvHeader));
        }

        for (Method m : type.getMethods()) {
            if ((m.getName().startsWith("get")) && (m.getReturnType().getName()=="java.lang.String")) {
                String fieldName = prefix + m.getName().substring(3);
                if (!headersAsList.contains(fieldName))
                    headersAsList.add(fieldName);
            }
        }

        // convert back the headers to an array
        this.csvHeader = new String[headersAsList.size()];
        headersAsList.toArray(this.csvHeader);

    }

    @Override
    public void setTypeToRecord(Class<? extends Object> type, String prefix, String[] fields)
            throws IllegalStateException {

        if (writingBegan)
            throw new IllegalStateException(
                    "Can not add a type to record in Profile persister : writing has already begun");

        List<String> headersAsList;
        if (csvHeader == null) {
            headersAsList = new ArrayList<>();
        } else {
            headersAsList = new ArrayList<>(Arrays.asList(csvHeader));
        }

        for (int i = 0; i < fields.length; i++) {
            try {
                type.getMethod("get" + fields[i]);
                String fieldName = prefix + fields[i];
                if (!headersAsList.contains(fieldName))
                    headersAsList.add(fieldName);
            } catch (NoSuchMethodException | SecurityException e) {
                logger.warn(String.format("%s is not a valid field for type %s : %s", fields[i], type.getName(),
                        e.getMessage()));
            }
        }

        // convert back the headers to an array
        this.csvHeader = new String[headersAsList.size()];
        headersAsList.toArray(this.csvHeader);

    }

    @Override
    public void setFieldToRecord(String field) throws IllegalStateException {
        if (writingBegan)
            throw new IllegalStateException(
                    "Can not add a type to record in Profile persister : writing has already begun");

        List<String> headersAsList;
        if (csvHeader == null) {
            headersAsList = new ArrayList<>();
        } else {
            headersAsList = new ArrayList<>(Arrays.asList(csvHeader));
        }

        if (!headersAsList.contains(field))
            headersAsList.add(field);
        
        
        // convert back the headers to an array
        this.csvHeader = new String[headersAsList.size()];
        headersAsList.toArray(this.csvHeader);
    }

   

}
