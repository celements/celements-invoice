package com.celements.invoice.builder;

import org.xwiki.component.annotation.ComponentRole;

@ComponentRole
public interface IBillingAddress {

  public String getCompany();
  public void setCompany(String company);

  public String getTitle();
  public void setTitle(String title);

  public String getFirstName();
  public void setFirstName(String firstName);

  public String getLastName();
  public void setLastName(String lastName);

  public String getStreetNameAndNumber();
  public void setStreetNameAndNumber(String streetNameAndNumber);

  public String getZipNumber();
  public void setZipNumber(String zipNumber);

  public String getCountry();
  public void setCountry(String coutry);

  public String getEmail();
  public void setEmail(String email);

  public String getPhoneNumber();
  public void setPhoneNumber(String phoneNumber);

  public String getAddressAddition();
  public void setAddressAddition(String addressAddition);

  public String getPObox();
  public void setPObox(String poBox);

}
