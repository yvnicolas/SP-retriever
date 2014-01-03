SP-retriever
============

Service Provider Connection Retrieval web application

Initially created for work with LITIS on automatic contact classification

## Prerequisites

Have a mysql database set up with following table for jdbc persistence of spring social connexion information as in [Spring Social Reference Documentation](http://docs.spring.io/spring-social/docs/1.0.x/reference/html/serviceprovider.html#service-providers-persisting-connections-jdbc) :


    create table UserConnection (userId varchar(255) not null,
        providerId varchar(255) not null,
        providerUserId varchar(255),
        rank int not null,
        displayName varchar(255),
        profileUrl varchar(512),
        imageUrl varchar(512),
        accessToken varchar(255) not null,					
        secret varchar(255),
        refreshToken varchar(255),
        expireTime bigint,
        primary key (userId, providerId, providerUserId));
    create unique index UserConnectionRank on UserConnection(userId, providerId, rank);


Database connection info are in MainConfig.java

Check your service provider application id in config package application.properties

## Usage

do `mvn tomcat7:run` then direct your browser to `localhost:8082`

