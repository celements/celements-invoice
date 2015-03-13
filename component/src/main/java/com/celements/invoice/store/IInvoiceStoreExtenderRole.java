package com.celements.invoice.store;

import org.xwiki.component.annotation.ComponentRole;

import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.IInvoiceItem;
import com.xpn.xwiki.doc.XWikiDocument;

@ComponentRole
public interface IInvoiceStoreExtenderRole {

  public void loadInvoice(XWikiDocument invoiceDoc, IInvoice invoice);

  public void loadInvoiceItem(XWikiDocument invoiceDoc, IInvoiceItem invoiceItem);

  public void storeInvoice(IInvoice theInvoice, XWikiDocument invoiceDoc);

}
