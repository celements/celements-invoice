package com.celements.invoice.store;

import org.xwiki.component.annotation.ComponentRole;
import org.xwiki.model.reference.DocumentReference;

import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.IInvoiceItem;

@ComponentRole
public interface IInvoiceStoreRole {

  public IInvoice loadInvoice(DocumentReference invoiceDocRef);

  public IInvoiceItem loadInvoiceItem(DocumentReference invoiceDocRef, int position);

  public void storeInvoice(IInvoice theInvoice);

  public void storeInvoice(IInvoice theInvoice, String comment);

  public void storeInvoice(IInvoice theInvoice, String comment, boolean isMinorEdit);

}
