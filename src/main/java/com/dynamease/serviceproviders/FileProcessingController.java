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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.dynamease.addressbooks.DynExternalAddressBookBasic;
import com.dynamease.addressbooks.PersisterFactory;
import com.dynamease.addressbooks.ProfilePersister;
import com.dynamease.addressbooks.impl.BasicAddrBookCsvImpl;
import com.dynamease.entities.PersonWthAddress;
import com.dynamease.serviceproviders.user.CurrentUserContext;

@Controller
public class FileProcessingController {

    private static final Logger logger = LoggerFactory.getLogger(FileProcessingController.class);

    private static final String[] pwaFields = { "FirstName", "LastName", "Phone", "Address", "Zip", "City" };

    @Autowired
    private SPResolver spResolver;

    @Autowired
    private CurrentUserContext currentUser;

    @Autowired
    private PersisterFactory persisterFactory;

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public RedirectView process(@RequestParam("file") MultipartFile file) {
        try {
            String localFilename = System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename();
            File localFile = new File(localFilename);
            file.transferTo(localFile);
            DynExternalAddressBookBasic namesInput = new BasicAddrBookCsvImpl(localFile);

            // Prepare the output file for headers
            ProfilePersister persister = initPersister(file.getOriginalFilename());

            while (namesInput.hasNext()) {
                PersonWthAddress person = (PersonWthAddress) namesInput.next(PersonWthAddress.class);
                logger.debug(String.format("Processing %s", person.fullName()));

                // First write the initial values
                persister.persistPartial(person, "");

                for (ServiceProviders sp : ServiceProviders.values()) {
                    SPConnectionRetriever spAccess = spResolver.getSPConnection(sp);
                    if (spAccess.isSelected()) {
                        logger.debug(String.format("Starting %s lookup", spAccess.getActiveSP().toString()));
                        List<? extends Object> matches = spAccess.getMatches(person);
                        logger.debug(String.format("Found %s matches", matches.size()));
                        // Stores count and then the first element in list (best data considered)
                        persister.persistPartialOneValue(String.format("%s", matches.size()), spAccess.getActiveSP()
                                .toString() + "_count");
                        if (matches.size() >= 1)
                            persister.persistPartial(matches.get(0), spAccess.getActiveSP().toString() + "_");

                    }
                }

                // For this name, all data has been extracted, flush the result to outputfile
                persister.flush();
            }

            // All names have been processed, close the output
            persister.close();

        } catch (IOException e) {
            logger.error(String.format("Access Error to file %s", file.getOriginalFilename()), e);
        } catch (SpInfoRetrievingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new RedirectView("/");
    }

    @SuppressWarnings("unchecked")
    private ProfilePersister initPersister(String fileName) {

        // Set the PersonWithAdrress fields to be listed first
        ProfilePersister toReturn = persisterFactory.create(fileName + "enriched");
        toReturn.setTypeToRecord(PersonWthAddress.class, "", pwaFields);

        // Add count and information fields for all selected sps.
        for (ServiceProviders sp : ServiceProviders.values()) {
            SPConnectionRetriever spAccess = spResolver.getSPConnection(sp);
            if (spAccess.isSelected()) {
                toReturn.setFieldToRecord(spAccess.getActiveSP().toString() + "_count");
                toReturn.setTypeToRecord(spAccess.getSPType(), spAccess.getActiveSP().toString() + "_");

            }
        }
        return toReturn;

    }
}
