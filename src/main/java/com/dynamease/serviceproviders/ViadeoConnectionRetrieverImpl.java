package com.dynamease.serviceproviders;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.viadeo.api.Viadeo;
import org.springframework.social.viadeo.api.ViadeoProfile;
import org.springframework.stereotype.Component;

import com.dynamease.serviceproviders.config.Uris;

@Component("ViadeoConnectionRetriever")
public class ViadeoConnectionRetrieverImpl implements SPConnectionRetriever {

    private static final Logger logger = LoggerFactory.getLogger(ViadeoConnectionRetrieverImpl.class);

    static final String DEFAULTPERMISSIONS = "";

    @Autowired
    private ProfilePrinter PRINTER;

    @Autowired
    private DynDisambiguer dynDisambiguer;

    @Autowired
    private Viadeo viadeo;

    public ViadeoConnectionRetrieverImpl() {
    }

    public void setViadeo(Viadeo viadeo) {
        this.viadeo = viadeo;
    }

    @Override
    public List<Person> getConnections() throws SpInfoRetrievingException {

        if (viadeo == null) {
            throw new SpInfoRetrievingException("Retrieving information from a null viadeo");
        }
        List<ViadeoProfile> connections = viadeo.userOperations().getContacts(100);
        List<Person> toReturn = new ArrayList<Person>();
        for (ViadeoProfile profile : connections) {

            toReturn.add(new Person(profile.getFirstName(), profile.getLastName()));
        }

        return toReturn;
    }

    @Override
    public ServiceProviders getActiveSP() {

        return ServiceProviders.VIADEO;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getSPType() {
        return Viadeo.class;
    }

    @Override
    public String getConnectUrl() {

        return Uris.SIGNINVI;
    }

    @Override
    public List<SpInfoPerson> getPersonInfo(Person person) throws SpInfoRetrievingException {

        if (!viadeo.isAuthorized()) {
            throw new SpInfoRetrievingException("Not connected to viadeo");
        }
        List<ViadeoProfile> queryResponse = viadeo.userOperations().search(person.fullName());
        List<SpInfoPerson> toReturn = new ArrayList<SpInfoPerson>();
        if (queryResponse != null) {
            for (ViadeoProfile profile : queryResponse) {

                if (dynDisambiguer.matches(person, profile)) {
                    SpInfoPerson spInfo = new SpInfoPerson(person, ServiceProviders.VIADEO);
                    toReturn.add(spInfo);
                    spInfo.setInfo(PRINTER.prettyPrintasString(profile));
                    logger.debug(String.format("Succesfully retrieved video profile info for %s : %s",
                            profile.getName(), spInfo.getInfo()));
                } else
                    logger.debug(String.format("Discarded non matching facebook profile info for %s : %s",
                            profile.getName(), PRINTER.prettyPrintasString(profile)));
            }
        }
        return toReturn;
    }

    @Override
    public boolean isconnected() {

        if (viadeo == null) {
            logger.debug(String.format("is connected : null viadeo returning false"));
            return false;
        }
        boolean toReturn = false;
        try {
            toReturn = viadeo.isAuthorized();
            logger.debug(String.format("Viadeo authorization succesfully checked returned %s", toReturn));
        } catch (Exception e) {
            logger.debug(String.format("Problem checking viadeo authorization : %s", e.getMessage()),e);
        }
        return toReturn;
    }

    @Override
    public String getPermissions() {

        return null;

    }

    private boolean selected = false;
    
    @Override
    public boolean isSelected() {
       
        return selected;
    }

    @Override
    public void select() {
        selected = true;
        
    }

    @Override
    public void unselect() {
       selected = false;
        
    }


}
