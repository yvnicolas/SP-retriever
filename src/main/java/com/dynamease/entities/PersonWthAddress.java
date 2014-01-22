package com.dynamease.entities;

public class PersonWthAddress extends PersonBasic {

    public PersonWthAddress() {
        
    }


    private String address;
    private String phone;
    private String city;
    private String zip;

    public PersonWthAddress(String firstName, String lastName) {
        super(firstName, lastName);
       
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

   
}
