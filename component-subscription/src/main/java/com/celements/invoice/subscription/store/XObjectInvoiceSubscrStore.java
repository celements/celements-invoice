package com.celements.invoice.subscription.store;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.IInvoiceItem;
import com.celements.invoice.builder.IInvoiceReferenceDocument;
import com.celements.invoice.store.IInvoiceStoreExtenderRole;
import com.celements.invoice.subscription.InvoiceSubscrReferenceDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.web.Utils;

@Component("celInvoiceSubscr.xobject")
public class XObjectInvoiceSubscrStore implements IInvoiceStoreExtenderRole {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      XObjectInvoiceSubscrStore.class);

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

  @Override
  public void loadInvoice(XWikiDocument invoiceDoc, IInvoice invoice) {
    BaseObject subscrObj = invoiceDoc.getXObject(getInvoiceClasses(
        ).getSubscriptionItemClassRef(getWikiName()));
    if (subscrObj != null) {
      InvoiceSubscrReferenceDocument invoiceRefDoc = (InvoiceSubscrReferenceDocument)
          Utils.getComponent(IInvoiceReferenceDocument.class, "subscription");
      invoiceRefDoc.setFrom(subscrObj.getDateValue(
          InvoiceClassCollection.FIELD_INVOICE_SUBSCR_FROM));
      invoiceRefDoc.setTo(subscrObj.getDateValue(
          InvoiceClassCollection.FIELD_INVOICE_SUBSCR_TO));
      invoiceRefDoc.setSubscriptionReference(subscrObj.getStringValue(
          InvoiceClassCollection.FIELD_INVOICE_SUBSCR_REF));
      invoice.addInvoiceReferenceDocument(invoiceRefDoc);
    }
  }

  @Override
  public void loadInvoiceItem(XWikiDocument invoiceDoc, IInvoiceItem invoiceItem) {
  }

  @Override
  public void storeInvoice(IInvoice theInvoice, XWikiDocument invoiceDoc) {
    List<IInvoiceReferenceDocument> invReferenceDocs = theInvoice.getReferenceDocs();
    for (IInvoiceReferenceDocument invRefDoc : invReferenceDocs) {
      if (invRefDoc instanceof InvoiceSubscrReferenceDocument) {
        InvoiceSubscrReferenceDocument invSubRefDoc =
            (InvoiceSubscrReferenceDocument) invRefDoc;
        try {
          BaseObject subscrObj = getOrCreateSubscrObject(invoiceDoc);
          subscrObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_SUBSCR_REF,
              invSubRefDoc.getSubscriptionReference());
          subscrObj.setDateValue(InvoiceClassCollection.FIELD_INVOICE_SUBSCR_FROM,
              invSubRefDoc.getFrom());
          subscrObj.setDateValue(InvoiceClassCollection.FIELD_INVOICE_SUBSCR_TO,
              invSubRefDoc.getTo());
        } catch (XWikiException exp) {
          LOGGER.error("Failed to store invoice subscription details on ["
              + invoiceDoc.getDocumentReference() + "].", exp);
        }
      }
    }
  }

  BaseObject getOrCreateSubscrObject(XWikiDocument invoiceDoc)
      throws XWikiException {
    BaseObject invoiceObj = invoiceDoc.getXObject(getInvoiceClasses(
        ).getSubscriptionItemClassRef(getWikiName()));
    if (invoiceObj == null) {
      invoiceObj = invoiceDoc.newXObject(getInvoiceClasses().getSubscriptionItemClassRef(
          getWikiName()), getContext());
    }
    return invoiceObj;
  }

  private String getWikiName() {
    return getContext().getDatabase();
  }

}
