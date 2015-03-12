package com.celements.invoice.service;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.script.service.ScriptService;

import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.IInvoiceItem;
import com.celements.invoice.store.IInvoiceStoreRole;

@Component("celInvoice")
public class InvoiceScriptService implements ScriptService {

  @Requirement("xobject")
  IInvoiceServiceRole invoiceService;

  @Requirement("xobject")
  IInvoiceStoreRole invoiceStore;

  public DocumentReference getInvoiceDocRefForInvoiceNumber(String invoiceNumber) {
    return invoiceService.getInvoiceDocRefForInvoiceNumber(invoiceNumber);
  }

  public IInvoice getInvoice(DocumentReference invoiceDocRef) {
    return invoiceStore.loadInvoice(invoiceDocRef);
  }

  public IInvoiceItem getInvoiceItem(DocumentReference invoiceDocRef, int position) {
    return invoiceStore.loadInvoiceItem(invoiceDocRef, position);
  }

  public String getInvoiceClass() {
    return InvoiceClassCollection.INVOICE_CLASSES_SPACE + "."
          + InvoiceClassCollection.INVOICE_CLASS_DOC;
  }

}
