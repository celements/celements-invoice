package com.celements.invoice.store;

import static com.celements.common.test.CelementsTestUtils.*;
import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.common.test.AbstractComponentTest;
import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.IBillingAddress;
import com.celements.invoice.builder.IInvoice;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.web.Utils;

public class XObjectBillingAddressStore_loadInvoiceTest
    extends AbstractComponentTest {

  private XObjectBillingAddressStore billingAddressStore;
  private XWikiContext context;

  @Before
  public void setUp_InvoiceSubscrStore_loadInvoiceTest() throws Exception {
    context = getContext();
    billingAddressStore = (XObjectBillingAddressStore) Utils.getComponent(
        IInvoiceStoreExtenderRole.class, "celBillingAddress.xobject");
  }

  @Test
  public void testLoadInvoice_noBillingAddressObject_OnInvoiceDoc() {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    replayDefault();
    billingAddressStore.loadInvoice(invoiceDoc, invoice);
    assertNull(invoice.getBillingAddress());
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_BillingAddress() throws Exception {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    String company = "The Company";
    String title = "The Title";
    String firstName = "first name";
    String lastName = "last name";
    String streetName = "the most important street 15";
    String addressAddition = "in the back yard";
    String poBox = "PO Box 156";
    String zipNumber = "DF2345";
    String city = "Wundercity West";
    String country = "Neverland";
    String emailAdr = "somebody@thecompany.zk";
    String phoneNumber = "+41 23 456 8900";
    DocumentReference addressClassRef = getInvoiceClasses().getInoviceAddressClassRef(
        context.getDatabase());
    BaseObject currAddressObject = new BaseObject();
    currAddressObject.setXClassReference(addressClassRef);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_COMPANY,
        company);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_TITLE, title);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_FIRST_NAME,
        firstName);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_NAME, lastName);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_STREET,
        streetName);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_ADDITION,
        addressAddition);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_POBOX, poBox);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_ZIP, zipNumber);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_CITY, city);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_COUNTRY,
        country);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_EMAIL,
        emailAdr);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_PHONE,
        phoneNumber);
    currAddressObject.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_TYPE,
        "Billing");
    invoiceDoc.addXObject(currAddressObject);
    replayDefault();
    billingAddressStore.loadInvoice(invoiceDoc, invoice);
    IBillingAddress billingAddress = invoice.getBillingAddress();
    assertNotNull(billingAddress);
    assertEquals(company, billingAddress.getCompany());
    assertEquals(title, billingAddress.getTitle());
    assertEquals(firstName, billingAddress.getFirstName());
    assertEquals(lastName, billingAddress.getLastName());
    assertEquals(streetName, billingAddress.getStreetNameAndNumber());
    assertEquals(addressAddition, billingAddress.getAddressAddition());
    assertEquals(poBox, billingAddress.getPObox());
    assertEquals(zipNumber, billingAddress.getZipNumber());
    assertEquals(city, billingAddress.getCity());
    assertEquals(country, billingAddress.getCountry());
    assertEquals(emailAdr, billingAddress.getEmail());
    assertEquals(phoneNumber, billingAddress.getPhoneNumber());
    verifyDefault();
  }
  
  private InvoiceClassCollection getInvoiceClasses() {
    return (InvoiceClassCollection) Utils.getComponent(IClassCollectionRole.class,
        "com.celements.invoice.classcollection");
  }

}
