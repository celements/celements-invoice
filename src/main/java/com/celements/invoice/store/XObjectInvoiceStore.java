package com.celements.invoice.store;

import groovy.lang.Singleton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;

import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.Invoice;
import com.celements.invoice.service.IInvoiceServiceRole;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

@Component("xobject")
@Singleton
public class XObjectInvoiceStore implements IInvoiceStoreRole {

  private static Log LOGGER = LogFactory.getFactory().getInstance(
      XObjectInvoiceStore.class);

  @Requirement("xobject")
  IInvoiceServiceRole invoiceService;

  @Requirement
  Execution execution;

  private XWikiContext getContext() {
    return (XWikiContext)execution.getContext().getProperty("xwikicontext");
  }

  public IInvoice loadInvoiceByInvoiceNumber(String invoiceNumber) {
    DocumentReference invoiceDocRef = invoiceService.getInvoiceDocRefForInvoiceNumber(
        invoiceNumber);
    if (getContext().getWiki().exists(invoiceDocRef, getContext())) {
      try {
        XWikiDocument invoiceDoc = getContext().getWiki().getDocument(invoiceDocRef,
            getContext());
        IInvoice invoice = new Invoice();
        //TODO
        return invoice;
      } catch (XWikiException exp) {
        LOGGER.error("Failed to load invoice document [" + invoiceDocRef + "].", exp);
      }
    }
    return null;
  }

  public void storeInvoice(IInvoice theInvoice) {
    // TODO Auto-generated method stub

  }

}
