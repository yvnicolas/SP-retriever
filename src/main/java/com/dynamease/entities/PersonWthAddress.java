package com.dynamease.entities;

public class PersonWthAddress extends PersonBasic {

    public PersonWthAddress() {
        
    }


    private String street;
    private String number;
    private String city;
    private String country;

    public PersonWthAddress(String firstName, String lastName) {
        super(firstName, lastName);
       
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
