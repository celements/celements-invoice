package com.celements.invoice.builder;

import static org.junit.Assert.*;

import org.junit.Test;

import com.celements.common.test.AbstractBridgedComponentTestCase;

public class BillingAddressTest extends AbstractBridgedComponentTestCase {

  @Test
  public void testCompany() {
    String value = "Company";
    BillingAddress ba = new BillingAddress();
    ba.setCompany(value);
    assertEquals(value, ba.getCompany());
  }

  @Test
  public void testTitle() {
    String value = "Title";
    BillingAddress ba = new BillingAddress();
    ba.setTitle(value);
    assertEquals(value, ba.getTitle());
  }

  @Test
  public void testFirstName() {
    String value = "FirstName";
    BillingAddress ba = new BillingAddress();
    ba.setFirstName(value);
    assertEquals(value, ba.getFirstName());
  }

  @Test
  public void testLastName() {
    String value = "LastName";
    BillingAddress ba = new BillingAddress();
    ba.setLastName(value);
    assertEquals(value, ba.getLastName());
  }

  @Test
  public void testStreetNameAndNumber() {
    String value = "StreetNameAndNumber";
    BillingAddress ba = new BillingAddress();
    ba.setStreetNameAndNumber(value);
    assertEquals(value, ba.getStreetNameAndNumber());
  }

  @Test
  public void testZipNumber() {
    String value = "ZipNumber";
    BillingAddress ba = new BillingAddress();
    ba.setZipNumber(value);
    assertEquals(value, ba.getZipNumber());
  }

  @Test
  public void testCity() {
    String value = "City";
    BillingAddress ba = new BillingAddress();
    ba.setCity(value);
    assertEquals(value, ba.getCity());
  }

  @Test
  public void testCountry() {
    String value = "Country";
    BillingAddress ba = new BillingAddress();
    ba.setCountry(value);
    assertEquals(value, ba.getCountry());
  }

  @Test
  public void testEmail() {
    String value = "Email";
    BillingAddress ba = new BillingAddress();
    ba.setEmail(value);
    assertEquals(value, ba.getEmail());
  }

  @Test
  public void testPhoneNumber() {
    String value = "PhoneNumber";
    BillingAddress ba = new BillingAddress();
    ba.setPhoneNumber(value);
    assertEquals(value, ba.getPhoneNumber());
  }

  @Test
  public void testAddressAddition() {
    String value = "AddressAddition";
    BillingAddress ba = new BillingAddress();
    ba.setAddressAddition(value);
    assertEquals(value, ba.getAddressAddition());
  }

  @Test
  public void testPObox() {
    String value = "PObox";
    BillingAddress ba = new BillingAddress();
    ba.setPObox(value);
    assertEquals(value, ba.getPObox());
  }

}
