package com.dynamease.serviceproviders;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.dynamease.entities.PersonWthAddress;
import com.dynamease.profiles.DynProfilePrinter;
import com.dynamease.profiles.InseeProfile;

public class InseeInfoRetrieverImplTest {
	
	private static InseeInfoRetrieverImpl underTest = new InseeInfoRetrieverImpl();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		underTest.setDynDisambiguer(new DynDisambiguer());
		underTest.setPRINTER(new DynProfilePrinter());
	}

	@Test
	public void testInseeInfoRetrieverImpl() {
		assertNotNull(ReflectionTestUtils.getField(underTest, "dbAccess"));
	}

	@Test
	public void testGetMatchesAsProfilesPersonWthAddress() throws SpInfoRetrievingException {
		PersonWthAddress fixture = new PersonWthAddress("Yves", "Nicolas");
		fixture.setCity("Apremont");
		fixture.setZip("08554");
		List <InseeProfile> results = underTest.getMatches(fixture);
		assertEquals(1, results.size());
	
	}

}
