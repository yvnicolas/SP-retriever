package com.dynamease.serviceproviders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.dynamease.entities.PersonWthAddress;
import com.dynamease.profiles.DynProfilePrinter;

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
		SPConnectionMatchesResults results = underTest.getMatches(fixture);
		assertEquals(1, results.nameMatchesCount());
	
	}
	
	@Test
	public void testGetMatchesUnknown() throws SpInfoRetrievingException {
		PersonWthAddress fixture = new PersonWthAddress("Yves", "Nicolas");
		fixture.setCity("lsdkfjsldk");
		fixture.setZip("08554");
		SPConnectionMatchesResults results = underTest.getMatches(fixture);
		assertEquals(0, results.nameMatchesCount());
	
	}

}
