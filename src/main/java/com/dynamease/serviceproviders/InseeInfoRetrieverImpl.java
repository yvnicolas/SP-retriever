package com.dynamease.serviceproviders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.dynamease.entities.PersonBasic;
import com.dynamease.entities.PersonWthAddress;
import com.dynamease.profiles.InseeProfile;

@Component("InseeRetriever")
public class InseeInfoRetrieverImpl extends DynSPConnectionRetriever<InseeProfile> {
	private static final Logger logger = LoggerFactory.getLogger(InseeInfoRetrieverImpl.class);

	private JdbcTemplate dbAccess;

	public InseeInfoRetrieverImpl() {
		super();

		// Database access Initialisation

		DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:mysql://localhost:3306/insee", "insee", "insee");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dbAccess = new JdbcTemplate(dataSource);
		logger.info("Insee Info Retriever from Database set up successfully");

	}

	@Override
	public ServiceProviders getActiveSP() {

		return ServiceProviders.INSEE;
	}

	@Override
	public Class getSPType() {
		return InseeProfile.class;
	}

	@Override
	public String getConnectUrl() {
		// Not relevant
		return null;
	}

	@Override
	public boolean isconnected() {

		return true;
	}

	@Override
	public String getPermissions() {

		return "Not relevant";
	}

	@Override
	public List<PersonBasic> getConnections() throws SpInfoRetrievingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<InseeProfile> getConnectionsasProfilesSpecific() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	PersonWthAddress mapProfile(InseeProfile profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	List<InseeProfile> getMatchesAsProfiles(PersonWthAddress person) {
		String queryString = String.format("SELECT C10_PMEN, P10_POP6579, P10_POP80P FROM cccouplfammen WHERE LIBGEO=\'%s\' AND DEP=\'%s\'", person.getCity(), person.getZip().substring(0, 2));
		logger.debug(String.format("Executing query to insee database : %s", queryString));
		return dbAccess.query(queryString, new InseeRowMapper(person));
	}

	private class InseeRowMapper implements RowMapper<InseeProfile> {
		private PersonBasic person;

		public InseeRowMapper(PersonBasic person) {
			super();
			this.person = person;
		}

		@Override
		public InseeProfile mapRow(ResultSet rs, int line) throws SQLException {
			ResultSetExtractor<InseeProfile> extractor = new InseeResultSetExtractorImpl(person);
			return extractor.extractData(rs);
		}

	}

	private  class InseeResultSetExtractorImpl implements ResultSetExtractor<InseeProfile> {
		private PersonBasic person;
		
		public InseeResultSetExtractorImpl(PersonBasic person) {
	        super();
	        this.person = person;
        }

		@Override
		public InseeProfile extractData(ResultSet rs) throws SQLException, DataAccessException {
			InseeProfile result = new InseeProfile();
			result.setFirstName(person.getFirstName());
			result.setLastName(person.getLastName());
			result.setTotalInhab(Integer.parseInt(rs.getString(1)));
			result.setSup65to79Inhab(Integer.parseInt(rs.getString(2)));
			result.setSup80Inhab(Integer.parseInt(rs.getString(3)));
			result.setSup65to79pct((int) ((100*result.getSup65to79Inhab()) / result.getTotalInhab()));
			result.setSup80pct((int) ((100*result.getSup80Inhab()) / result.getTotalInhab()));
			return result;
		}
	}

}
