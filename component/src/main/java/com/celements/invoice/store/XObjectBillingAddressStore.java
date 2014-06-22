package com.celements.invoice.store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.IBillingAddress;
import com.celements.invoice.builder.IInvoice;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.web.Utils;

@Component("celBillingAddress.xobject")
public class XObjectBillingAddressStore implements IInvoiceStoreExtenderRole {

  private static Log LOGGER = LogFactory.getFactory().getInstance(
      XObjectBillingAddressStore.class);

  @Requirement("com.celements.invoice.classcollection")
  IClassCollectionRole invoiceClasses;

  @Requirement
  Execution execution;

  private XWikiContext getContext() {
    return (XWikiContext)execution.getContext().getProperty("xwikicontext");
  }

  private InvoiceClassCollection getInvoiceClasses() {
    return (InvoiceClassCollection) invoiceClasses;
  }

  public void loadInvoice(XWikiDocument invoiceDoc, IInvoice invoice) {
    BaseObject addressObj = invoiceDoc.getXObject(getInvoiceClasses(
        ).getInoviceAddressClassRef(getWikiName()),
        InvoiceClassCollection.FIELD_ADDRESS_TYPE, "Billing", false);
    if (addressObj != null) {
      IBillingAddress billingAddress = Utils.getComponent(IBillingAddress.class);
      billingAddress.setCompany(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_COMPANY));
      billingAddress.setTitle(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_TITLE));
      billingAddress.setFirstName(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_FIRST_NAME));
      billingAddress.setLastName(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_NAME));
      billingAddress.setStreetNameAndNumber(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_STREET));
      billingAddress.setAddressAddition(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_ADDITION));
      billingAddress.setPObox(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_POBOX));
      billingAddress.setZipNumber(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_ZIP));
      billingAddress.setCity(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_CITY));
      billingAddress.setCountry(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_COUNTRY));
      billingAddress.setEmail(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_EMAIL));
      billingAddress.setPhoneNumber(addressObj.getStringValue(
          InvoiceClassCollection.FIELD_ADDRESS_PHONE));
      invoice.setBillingAddress(billingAddress);
    }
  }

  public void storeInvoice(IInvoice theInvoice, XWikiDocument invoiceDoc) {
    IBillingAddress billingAddress = theInvoice.getBillingAddress();
    if (billingAddress != null) {
      try {
        BaseObject addressObj = getOrCreateAddressObject(invoiceDoc);
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_COMPANY,
            billingAddress.getCompany());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_TITLE,
            billingAddress.getTitle());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_FIRST_NAME,
            billingAddress.getFirstName());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_NAME,
            billingAddress.getLastName());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_STREET,
            billingAddress.getStreetNameAndNumber());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_ADDITION,
            billingAddress.getAddressAddition());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_POBOX,
            billingAddress.getPObox());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_ZIP,
            billingAddress.getZipNumber());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_CITY,
            billingAddress.getCity());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_COUNTRY,
            billingAddress.getCountry());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_EMAIL,
            billingAddress.getEmail());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_PHONE,
            billingAddress.getPhoneNumber());
        addressObj.setStringValue(InvoiceClassCollection.FIELD_ADDRESS_TYPE, "Billing");
      } catch (XWikiException exp) {
        LOGGER.error("Failed to store invoice address details on ["
            + invoiceDoc.getDocumentReference() + "].", exp);
      }
    }
  }

  BaseObject getOrCreateAddressObject(XWikiDocument invoiceDoc)
      throws XWikiException {
    BaseObject invoiceObj = invoiceDoc.getXObject(getInvoiceClasses(
        ).getInoviceAddressClassRef(getWikiName()),
        InvoiceClassCollection.FIELD_ADDRESS_TYPE, "Billing", false);
    if (invoiceObj == null) {
      invoiceObj = invoiceDoc.newXObject(getInvoiceClasses().getInoviceAddressClassRef(
          getWikiName()), getContext());
    }
    return invoiceObj;
  }

  private String getWikiName() {
    return getContext().getDatabase();
  }

}
