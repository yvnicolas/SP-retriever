package com.dynamease.addressbooks.impl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Manages the header dictionnary for CSV file uniformization
 * 
 * @author Yves Nicolas
 * 
 */

public class DynHeaderNormalizer {

	private static Properties properties=null;
	static final String fileLocation = "csvHeader.properties";
	
	private Logger logger = LoggerFactory.getLogger(DynHeaderNormalizer.class);


	public DynHeaderNormalizer() {
		super();
		initIfNot();

	}

	
	private void initIfNot() {
		if (properties == null) {
			try {
				Reader r = new InputStreamReader(new ClassPathResource(fileLocation).getInputStream());	
				properties = new Properties();
				properties.load(r);
			}
			catch (Exception e) {
				logger.debug("Not able to initialze the CSV Header dictionnary", e);
			}
	}
	}
	
	public String lookup(String header) {
		return properties.getProperty(header);
	}

}
