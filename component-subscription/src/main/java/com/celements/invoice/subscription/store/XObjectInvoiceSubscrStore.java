package com.celements.invoice.subscription.store;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.IInvoiceReferenceDocument;
import com.celements.invoice.store.IInvoiceStoreExtenderRole;
import com.celements.invoice.subscription.InvoiceSubscrReferenceDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

@Component("celInvoiceSubscr.xobject")
public class XObjectInvoiceSubscrStore implements IInvoiceStoreExtenderRole {

  private static Log LOGGER = LogFactory.getFactory().getInstance(
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

  public void loadInvoice(XWikiDocument invoiceDoc, IInvoice invoice) {
    // TODO Auto-generated method stub

  }

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
