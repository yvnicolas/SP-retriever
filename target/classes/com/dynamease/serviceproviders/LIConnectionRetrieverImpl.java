package com.dynamease.serviceproviders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.SearchParameters;
import org.springframework.stereotype.Component;

import com.dynamease.serviceproviders.config.Uris;

@Component("LIConnectionRetriever")
public class LIConnectionRetrieverImpl implements SPConnectionRetriever {

    private static final Logger logger = LoggerFactory.getLogger(LIConnectionRetrieverImpl.class);
    private LinkedIn linkedIn;

    @Inject
    public LIConnectionRetrieverImpl(LinkedIn linkedIn) {
        this.linkedIn = linkedIn;
    }

    @Override
    public List<Person> getConnections() {
        List<LinkedInProfile> connections = linkedIn.connectionOperations().getConnections();
        List<Person> toReturn = new ArrayList<Person>();
        for (LinkedInProfile connection : connections) {
            toReturn.add(new Person(connection.getFirstName(), connection.getLastName()));
        }
        return toReturn;
    }

    @Override
    public ServiceProviders getActiveSP() {

        return ServiceProviders.LINKEDIN;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getSPType() {
        return LinkedIn.class;
    }

    @Override
    public String getConnectUrl() {

        return Uris.SIGNINLI;
    }

    @Override
    public List<SpInfoPerson> getPersonInfo(Person person) {

        List<SpInfoPerson> toReturn = new ArrayList<SpInfoPerson>();

        SearchParameters searchQuery = new SearchParameters();
        searchQuery.setFirstName(person.getFirstName());
        searchQuery.setLastName(person.getLastName());
        List<LinkedInProfile> queryResponse = linkedIn.profileOperations().search(searchQuery).getPeople();

        for (LinkedInProfile profile : queryResponse) {

            SpInfoPerson spInfo = new SpInfoPerson(person, ServiceProviders.LINKEDIN);
            toReturn.add(spInfo);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(profile);
                spInfo.setInfo(baos.toString());
                oos.close();
                logger.info(String.format("Succesfully retrieved Linked profile info for %s : %s", person.fullName(),
                        spInfo.getInfo()));
            } catch (IOException e) {
                logger.error(
                        String.format("Serializing LinkedIn Profile for %s %s: %s", profile.getFirstName(),
                                profile.getLastName(), e.getMessage()), e);
            }

        }

        return null;
    }

}
