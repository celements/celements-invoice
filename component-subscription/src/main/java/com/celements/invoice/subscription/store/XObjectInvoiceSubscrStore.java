package com.celements.invoice.subscription.store;

import org.xwiki.component.annotation.Component;

import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.store.IInvoiceStoreExtenderRole;
import com.xpn.xwiki.doc.XWikiDocument;

@Component("celInvoiceSubscr.xobject")
public class XObjectInvoiceSubscrStore implements IInvoiceStoreExtenderRole {

  public void loadInvoice(XWikiDocument invoiceDoc, IInvoice invoice) {
    // TODO Auto-generated method stub

  }

  public void storeInvoice(IInvoice theInvoice, XWikiDocument invoiceDoc) {
    // TODO Auto-generated method stub

  }

}
