package com.dynamease.serviceproviders;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dynamease.entities.PersonBasic;
import com.dynamease.profiles.DynProfilePrinter;
import com.dynamease.profiles.LinkedInternetProfile;
import com.dynamease.profiles.ProfilePrinter;

public class LinkedInternetConnectionRetrieverTest {
	private static final Logger logger = LoggerFactory.getLogger(LinkedInternetConnectionRetriever.class);
	private static final LinkedInternetConnectionRetriever underTest = new LinkedInternetConnectionRetriever();
	private static final ProfilePrinter printer = new DynProfilePrinter();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testGetMatchesAsProfilesPersonBasic() {
		List<LinkedInternetProfile> result = underTest.getMatchesAsProfiles(new PersonBasic("Yves", "Nicolas"));
		int i=0;
		for (LinkedInternetProfile profile : result) {
			i++;
			logger.debug(String.format("----- Match Nber %s -----", i));
			logger.debug(printer.prettyPrintasString(profile));	
		}
		
		assertEquals(14, result.size());
	}

}
