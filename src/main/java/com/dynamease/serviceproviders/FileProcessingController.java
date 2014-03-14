package com.dynamease.serviceproviders;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.dynamease.addressbooks.DynExternalAddressBookBasic;
import com.dynamease.addressbooks.PersisterFactory;
import com.dynamease.addressbooks.ProfilePersister;
import com.dynamease.addressbooks.impl.BasicAddrBookCsvImpl;
import com.dynamease.entities.PersonWthAddress;
import com.dynamease.serviceproviders.config.Uris;
import com.dynamease.serviceproviders.user.CurrentUserContext;

@Controller
public class FileProcessingController {

	private static final Logger logger = LoggerFactory
			.getLogger(FileProcessingController.class);

	private static final String[] pwaFields = { "FirstName", "LastName",
			"Phone", "Address", "Zip", "City" };

	@Autowired
	private SPResolver spResolver;

	@Autowired
	private CurrentUserContext currentUser;

	@Autowired
	private PersisterFactory persisterFactory;

	@RequestMapping(value = "/import", method = RequestMethod.POST)
	public RedirectView process(@RequestParam("file") MultipartFile file) {
		ProfilePersister persister = null;
		try {
			String localFilename = System.getProperty("java.io.tmpdir") + "/"
					+ file.getOriginalFilename();
			File localFile = new File(localFilename);
			file.transferTo(localFile);
			DynExternalAddressBookBasic namesInput = new BasicAddrBookCsvImpl(
					localFile);

			// Prepare the output file for headers
			persister = initPersister(file.getOriginalFilename());

			while (namesInput.hasNext()) {
				PersonWthAddress person = (PersonWthAddress) namesInput
						.next(PersonWthAddress.class);
				logger.debug(String.format("Processing %s - %s - %s - %s", person.fullName(), person.getAddress(), person.getZip(), person.getPhone()));

				// First write the initial values
				persister.persistPartial(person, "");

				for (ServiceProviders sp : ServiceProviders.values()) {
					SPConnectionRetriever spAccess = spResolver
							.getSPConnection(sp);
					if (spAccess.isSelected()) {
						logger.debug(String.format("Starting %s lookup",
								spAccess.getActiveSP().toString()));
						List<? extends Object> matches;
						try {
							matches = spAccess.getMatches(person);

							logger.debug(String.format("Found " + "%s matches",
									matches.size()));
							// Stores count and then the first element in list
							// (best
							// data considered)
							persister.persistPartialOneValue(
									String.format("%s", matches.size()),
									spAccess.getActiveSP().toString()
											+ "_count");
							if (matches.size() >= 1)
								persister
										.persistPartial(matches.get(0),
												spAccess.getActiveSP()
														.toString() + "_");
						} catch (Exception e) {
							logger.error(String.format(
									"Error accessing service provider : %s",
									e.getMessage()));
						}
					}
				}

				// For this name, all data has been extracted, flush the result
				// to outputfile
				persister.flush();
			}

			// All names have been processed, close the output

		} catch (IOException e) {
			logger.error(
					String.format("Access Error to file %s",
							file.getOriginalFilename()), e);

		}

		finally {
			if (persister != null)
				try {
					persister.close();
				} catch (IOException e) {
					logger.error(
							String.format("IO Exception trying to close persister : %s"),
							e.getMessage());
					logger.error(String.format("Cause : %s", e.getCause()
							.getMessage()));
				}
		}

		return new RedirectView("/");
	}

	/**
	 * For all selected service providers, persist the connections on this
	 * service provider in a file
	 * 
	 * @return
	 */
	@RequestMapping(value = Uris.PERSIST, method = RequestMethod.GET)
	public RedirectView persistConnections() {

		logger.debug(String.format("Persisting connections for id : %s",
				currentUser.getId()));

		for (ServiceProviders sp : ServiceProviders.values()) {
			SPConnectionRetriever spAccess = spResolver.getSPConnection(sp);
			if (spAccess.isSelected()) {

				logger.debug(String.format("getting connections for %s",
						sp.name()));

				ProfilePersister persister = null;

				try {
					// get the connections
					List<? extends Object> connections;

					try {
						connections = spAccess.getConnectionsasProfiles();

						// init the persister
						if (connections != null) {
							persister = persisterFactory.create(sp.name()
									+ currentUser.getId());
							persister.setTypeToRecord(spAccess.getSPType(), "");

							// persist all connections
							for (Object connection : connections) {
								persister.persist(connection);
							}

							persister.close();
							logger.debug(String.format(
									"Successfully persisted %s connections",
									connections.size()));
						} else
							logger.debug("No connections found, nothing to persist");
					} catch (SpInfoRetrievingException e) {
						logger.error(String.format(
								"Cannot get connections : %s", e.getMessage()));
						logger.error("Root cause : "
								+ e.getCause().getMessage());
					}

				} catch (IOException e) {
					logger.error(String.format(
							"Error persisting %s connections for %s : %s",
							sp.name(), currentUser.getId(), e.getMessage()));
					logger.error("Root cause : " + e.getCause().getMessage());
				} finally {
					if (persister != null)
						try {
							persister.close();
						} catch (IOException e) {
							logger.error(
									String.format("IO Exception trying to close persister : %s"),
									e.getMessage());
							logger.error(String.format("Cause : %s", e
									.getCause().getMessage()));
						}
				}

			}
		}

		return new RedirectView("/");
	}

	@SuppressWarnings("unchecked")
	private ProfilePersister initPersister(String fileName) {

		// Set the PersonWithAdrress fields to be listed first
		ProfilePersister toReturn = persisterFactory.create(fileName
				+ "enriched");
		toReturn.setTypeToRecord(PersonWthAddress.class, "", pwaFields);

		// Add count and information fields for all selected sps.
		for (ServiceProviders sp : ServiceProviders.values()) {
			SPConnectionRetriever spAccess = spResolver.getSPConnection(sp);
			if (spAccess.isSelected()) {
				toReturn.setFieldToRecord(spAccess.getActiveSP().toString()
						+ "_count");
				toReturn.setTypeToRecord(spAccess.getSPType(), spAccess
						.getActiveSP().toString() + "_");

			}
		}
		return toReturn;

	}
}
