package com.celements.invoice.store;

import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.IInvoiceItem;
import com.google.common.base.Function;
import com.xpn.xwiki.doc.XWikiDocument;

public class TestInvoiceStoreExtender implements IInvoiceStoreExtenderRole {

  private Function<Object, Object> func;

  TestInvoiceStoreExtender(Function<Object, Object> func) {
    this.func = func;
  }

  @Override
  public void loadInvoice(XWikiDocument invoiceDoc, IInvoice invoice) {
    func.apply("load");
    func.apply(invoiceDoc);
    func.apply(invoice);
  }

  @Override
  public void loadInvoiceItem(XWikiDocument invoiceDoc, IInvoiceItem invoiceItem) {
    func.apply("load");
    func.apply(invoiceDoc);
    func.apply(invoiceItem);
  }

  @Override
  public void storeInvoice(IInvoice theInvoice, XWikiDocument invoiceDoc) {
    func.apply("store");
    func.apply(invoiceDoc);
    func.apply(theInvoice);
  }

}