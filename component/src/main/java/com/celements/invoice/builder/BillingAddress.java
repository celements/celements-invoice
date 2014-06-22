package com.celements.invoice.builder;

public class BillingAddress implements IBillingAddress {
  private String company;
  private String title;
  private String firstName;
  private String lastName;
  private String streetNameAndNumber;
  private String zipNumber;
  private String city;
  private String country;
  private String email;
  private String phoneNumber;
  private String addressAddition;
  private String poBox;

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public String getStreetNameAndNumber() {
    return streetNameAndNumber;
  }

  public void setStreetNameAndNumber(String streetNameAndNumber) {
    this.streetNameAndNumber = streetNameAndNumber;
  }

  public String getZipNumber() {
    return zipNumber;
  }

  public void setZipNumber(String zipNumber) {
    this.zipNumber = zipNumber;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getAddressAddition() {
    return addressAddition;
  }

  public void setAddressAddition(String addressAddition) {
    this.addressAddition = addressAddition;
  }

  public String getPObox() {
    return poBox;
  }

  public void setPObox(String poBox) {
    this.poBox = poBox;
  }

}
