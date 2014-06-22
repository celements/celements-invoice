package com.celements.invoice.store;

import static junit.framework.Assert.*;
import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.common.test.AbstractBridgedComponentTestCase;
import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.IBillingAddress;
import com.celements.invoice.builder.IInvoice;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.web.Utils;

public class XObjectBillingAddressStore_storeInvoiceTest
    extends AbstractBridgedComponentTestCase {

  private XObjectBillingAddressStore billingAddressStore;
  private XWikiContext context;
  private XWiki xwiki;

  @Before
  public void setUp_InvoiceSubscrStore_loadInvoiceTest() throws Exception {
    context = getContext();
    xwiki = getWikiMock();
    billingAddressStore = (XObjectBillingAddressStore) Utils.getComponent(
        IInvoiceStoreExtenderRole.class, "celBillingAddress.xobject");
  }

  @Test
  public void testStoreInvoice_noBillingAddress() {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    assertNull("precondition check", invoice.getBillingAddress());
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    replayDefault();
    billingAddressStore.storeInvoice(invoice, invoiceDoc);
    BaseObject addressObj = invoiceDoc.getXObject(getInvoiceClasses(
        ).getInoviceAddressClassRef(context.getDatabase()));
    assertNull("wrong address object on invoice found.", addressObj);
    verifyDefault();
  }

  @Test
  public void testStoreInvoice_BillingAddress() throws Exception {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    IBillingAddress billingAddress = Utils.getComponent(IBillingAddress.class);
    String company = "The Company";
    billingAddress.setCompany(company);
    String title = "The Title";
    billingAddress.setTitle(title);
    String firstName = "first name";
    billingAddress.setFirstName(firstName);
    String lastName = "last name";
    billingAddress.setLastName(lastName);
    String streetName = "the most important street 15";
    billingAddress.setStreetNameAndNumber(streetName);
    String addressAddition = "in the back yard";
    billingAddress.setAddressAddition(addressAddition);
    String poBox = "PO Box 156";
    billingAddress.setPObox(poBox);
    String zipNumber = "DF2345";
    billingAddress.setZipNumber(zipNumber);
    String city = "Wundercity West";
    billingAddress.setCity(city);
    String country = "Neverland";
    billingAddress.setCountry(country);
    String emailAdr = "somebody@thecompany.zk";
    billingAddress.setEmail(emailAdr);
    String phoneNumber = "+41 23 456 8900";
    billingAddress.setPhoneNumber(phoneNumber);
    invoice.setBillingAddress(billingAddress);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    DocumentReference addressClassRef = getInvoiceClasses().getInoviceAddressClassRef(
        context.getDatabase());
    BaseClass invClassMock = createMockAndAddToDefault(BaseClass.class);
    expect(xwiki.getXClass(eq(addressClassRef), same(context))).andReturn(
        invClassMock);
    expect(invClassMock.newCustomClassInstance(same(context))).andReturn(new BaseObject()
        ).once();
    replayDefault();
    billingAddressStore.storeInvoice(invoice, invoiceDoc);
    BaseObject addressObj = invoiceDoc.getXObject(addressClassRef);
    assertNotNull("address object on invoice document missing.", addressObj);
    assertEquals(company, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_COMPANY));
    assertEquals(title, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_TITLE));
    assertEquals(firstName, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_FIRST_NAME));
    assertEquals(lastName, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_NAME));
    assertEquals(streetName, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_STREET));
    assertEquals(addressAddition, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_ADDITION));
    assertEquals(poBox, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_POBOX));
    assertEquals(zipNumber, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_ZIP));
    assertEquals(city, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_CITY));
    assertEquals(country, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_COUNTRY));
    assertEquals(emailAdr, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_EMAIL));
    assertEquals(phoneNumber, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_PHONE));
    assertEquals("Billing", addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_TYPE));
    verifyDefault();
  }
  
  @Test
  public void testStoreInvoice_BillingAddress_shippingAddress() throws Exception {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    IBillingAddress billingAddress = Utils.getComponent(IBillingAddress.class);
    String company = "The Company";
    billingAddress.setCompany(company);
    String title = "The Title";
    billingAddress.setTitle(title);
    String firstName = "first name";
    billingAddress.setFirstName(firstName);
    String lastName = "last name";
    billingAddress.setLastName(lastName);
    String streetName = "the most important street 15";
    billingAddress.setStreetNameAndNumber(streetName);
    String addressAddition = "in the back yard";
    billingAddress.setAddressAddition(addressAddition);
    String poBox = "PO Box 156";
    billingAddress.setPObox(poBox);
    String zipNumber = "DF2345";
    billingAddress.setZipNumber(zipNumber);
    String city = "Wundercity West";
    billingAddress.setCity(city);
    String country = "Neverland";
    billingAddress.setCountry(country);
    String emailAdr = "somebody@thecompany.zk";
    billingAddress.setEmail(emailAdr);
    String phoneNumber = "+41 23 456 8900";
    billingAddress.setPhoneNumber(phoneNumber);
    invoice.setBillingAddress(billingAddress);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    DocumentReference addressClassRef = getInvoiceClasses().getInoviceAddressClassRef(
        context.getDatabase());
    BaseObject currentAddressObject = new BaseObject();
    currentAddressObject.setXClassReference(addressClassRef);
    currentAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_TYPE,
        "Shipping");
    invoiceDoc.addXObject(currentAddressObject);
    BaseClass invClassMock = createMockAndAddToDefault(BaseClass.class);
    expect(xwiki.getXClass(eq(addressClassRef), same(context))).andReturn(
        invClassMock);
    expect(invClassMock.newCustomClassInstance(same(context))).andReturn(new BaseObject()
        ).once();
    replayDefault();
    billingAddressStore.storeInvoice(invoice, invoiceDoc);
    BaseObject addressObj = invoiceDoc.getXObject(addressClassRef,
        InvoiceClassCollection.FIELD_ADDRESS_TYPE, "Billing", false);
    assertNotNull("address object on invoice document missing.", addressObj);
    assertNotNull("Shipping Address object may not be touched!",
        invoiceDoc.getXObject(addressClassRef, InvoiceClassCollection.FIELD_ADDRESS_TYPE,
            "Shipping", false));
    assertEquals(company, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_COMPANY));
    assertEquals(title, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_TITLE));
    assertEquals(firstName, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_FIRST_NAME));
    assertEquals(lastName, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_NAME));
    assertEquals(streetName, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_STREET));
    assertEquals(addressAddition, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_ADDITION));
    assertEquals(poBox, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_POBOX));
    assertEquals(zipNumber, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_ZIP));
    assertEquals(city, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_CITY));
    assertEquals(country, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_COUNTRY));
    assertEquals(emailAdr, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_EMAIL));
    assertEquals(phoneNumber, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_PHONE));
    assertEquals("Billing", addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_TYPE));
    verifyDefault();
  }
  
  @Test
  public void testStoreInvoice_update_BillingAddress() throws Exception {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    IBillingAddress billingAddress = Utils.getComponent(IBillingAddress.class);
    String company = "The Company";
    billingAddress.setCompany(company);
    String title = "The Title";
    billingAddress.setTitle(title);
    String firstName = "first name";
    billingAddress.setFirstName(firstName);
    String lastName = "last name";
    billingAddress.setLastName(lastName);
    String streetName = "the most important street 15";
    billingAddress.setStreetNameAndNumber(streetName);
    String addressAddition = "in the back yard";
    billingAddress.setAddressAddition(addressAddition);
    String poBox = "PO Box 156";
    billingAddress.setPObox(poBox);
    String zipNumber = "DF2345";
    billingAddress.setZipNumber(zipNumber);
    String city = "Wundercity West";
    billingAddress.setCity(city);
    String country = "Neverland";
    billingAddress.setCountry(country);
    String emailAdr = "somebody@thecompany.zk";
    billingAddress.setEmail(emailAdr);
    String phoneNumber = "+41 23 456 8900";
    billingAddress.setPhoneNumber(phoneNumber);
    invoice.setBillingAddress(billingAddress);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    DocumentReference addressClassRef = getInvoiceClasses().getInoviceAddressClassRef(
        context.getDatabase());
    BaseObject currentAddressObject = new BaseObject();
    currentAddressObject.setXClassReference(addressClassRef);
    currentAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_TYPE,
        "Billing");
    invoiceDoc.addXObject(currentAddressObject);
    replayDefault();
    billingAddressStore.storeInvoice(invoice, invoiceDoc);
    assertEquals(1, invoiceDoc.getXObjects(addressClassRef).size());
    BaseObject addressObj = invoiceDoc.getXObject(addressClassRef);
    assertNotNull("address object on invoice document missing.", addressObj);
    assertEquals(company, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_COMPANY));
    assertEquals(title, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_TITLE));
    assertEquals(firstName, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_FIRST_NAME));
    assertEquals(lastName, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_NAME));
    assertEquals(streetName, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_STREET));
    assertEquals(addressAddition, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_ADDITION));
    assertEquals(poBox, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_POBOX));
    assertEquals(zipNumber, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_ZIP));
    assertEquals(city, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_CITY));
    assertEquals(country, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_COUNTRY));
    assertEquals(emailAdr, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_EMAIL));
    assertEquals(phoneNumber, addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_PHONE));
    assertEquals("Billing", addressObj.getStringValue(
        InvoiceClassCollection.FIELD_ADDRESS_TYPE));
    verifyDefault();
  }

  private InvoiceClassCollection getInvoiceClasses() {
    return (InvoiceClassCollection) Utils.getComponent(IClassCollectionRole.class,
        "com.celements.invoice.classcollection");
  }

}
