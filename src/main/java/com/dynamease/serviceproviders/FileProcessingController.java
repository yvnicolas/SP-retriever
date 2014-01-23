package com.dynamease.serviceproviders;

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

import com.dynamease.addressbooks.DynExternalAddressBookBasic;
import com.dynamease.addressbooks.impl.BasicAddrBookCsvImpl;
import com.dynamease.entities.PersonWthAddress;
import com.dynamease.serviceproviders.user.CurrentUserContext;


@Controller
public class FileProcessingController {

    private static final Logger logger = LoggerFactory.getLogger(FileProcessingController.class);
    
    @Autowired
    private SPResolver spResolver;

    @Autowired
    private CurrentUserContext currentUser;

    
     
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ModelAndView process(@RequestParam("file") MultipartFile file) {
        try {
            DynExternalAddressBookBasic namesInput = new BasicAddrBookCsvImpl(file.getInputStream());
            while (namesInput.hasNext()) {
                PersonWthAddress person = (PersonWthAddress) namesInput.next(PersonWthAddress.class);
                for(ServiceProviders sp : ServiceProviders.values()) {
                    SPConnectionRetriever spAccess = spResolver.getSPConnection(sp);
                    if (spAccess.isSelected()) {
                        List<? extends Object> matches = spAccess.getMatches(person);
                        
                        // Prepare for storing
                        
                    }
                }
            }
        } catch (IOException e) {
            logger.error(String.format("Access Error to file %s", file.getOriginalFilename()),e);
        } catch (SpInfoRetrievingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
}
