package com.dynamease.serviceproviders;

public class Person {

    private String firstName;
    private String lastName;
    
    private final String SPACE=" ";
    
    public Person() {
           }

    public Person(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    
    public String fullName() {
        StringBuilder sb = new StringBuilder(this.firstName);
        sb.append(SPACE);
        sb.append(this.lastName);
        return sb.toString();
    }
}
