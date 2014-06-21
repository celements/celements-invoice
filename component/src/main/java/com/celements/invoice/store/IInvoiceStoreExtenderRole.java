package com.celements.invoice.store;

import org.xwiki.component.annotation.ComponentRole;

import com.celements.invoice.builder.IInvoice;
import com.xpn.xwiki.doc.XWikiDocument;

@ComponentRole
public interface IInvoiceStoreExtenderRole {

  public void loadInvoice(XWikiDocument invoiceDoc, IInvoice invoice);

  public void storeInvoice(XWikiDocument invoiceDoc, IInvoice theInvoice);

}
