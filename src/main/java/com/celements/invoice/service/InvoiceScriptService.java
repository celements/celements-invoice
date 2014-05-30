package com.celements.invoice.service;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.script.service.ScriptService;

import com.celements.invoice.InvoiceClassCollection;

@Component("celInvoice")
public class InvoiceScriptService implements ScriptService {

  @Requirement("xobject")
  IInvoiceServiceRole invoiceService;

  public DocumentReference getInvoiceDocRefForInvoiceNumber(String invoiceNumber) {
    return invoiceService.getInvoiceDocRefForInvoiceNumber(invoiceNumber);
  }

  public String getInvoiceClass() {
    return InvoiceClassCollection.INVOICE_CLASSES_SPACE + "."
          + InvoiceClassCollection.INVOICE_CLASS_DOC;
  }

}
