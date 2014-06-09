package com.celements.invoice.store;

import org.xwiki.component.annotation.ComponentRole;

import com.celements.invoice.builder.IInvoice;

@ComponentRole
public interface IInvoiceStoreRole {

  public IInvoice loadInvoiceByInvoiceNumber(String invoiceNumber);

  public void storeInvoice(IInvoice theInvoice);

  public void storeInvoice(IInvoice theInvoice, String comment);

  public void storeInvoice(IInvoice theInvoice, String comment, boolean isMinorEdit);

}
