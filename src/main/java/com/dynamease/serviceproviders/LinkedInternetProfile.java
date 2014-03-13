package com.dynamease.serviceproviders;

/**
 * A Profile Class to retrieve information from public Linked In profile search on the internet.
 * @author yves
 *
 */
public class LinkedInternetProfile {

	
	private String firstName;
    private String lastName;
    private String fullName;  // full Name as it appears on Linked In public directory
    private String title;
    private String location;
    private String industry;
    private String currentJob;
    private String training;
    private String summary;
    
    
	public LinkedInternetProfile() {
	
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


	public String getFullName() {
		return fullName;
	}


	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getIndustry() {
		return industry;
	}


	public void setIndustry(String industry) {
		this.industry = industry;
	}


	public String getCurrentJob() {
		return currentJob;
	}


	public void setCurrentJob(String currentJob) {
		this.currentJob = currentJob;
	}


	public String getTraining() {
		return training;
	}


	public void setTraining(String training) {
		this.training = training;
	}


	public String getSummary() {
		return summary;
	}


	public void setSummary(String summary) {
		this.summary = summary;
	}

}
