package com.celements.invoice.subscription.store;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.test.AbstractBridgedComponentTestCase;
import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.store.IInvoiceStoreExtenderRole;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.web.Utils;

public class XObjectInvoiceSubscrStore_storeInvoiceTest extends AbstractBridgedComponentTestCase {

  private XObjectInvoiceSubscrStore invoiceSubscrStore;
  private XWikiContext context;

  @Before
  public void setUp_InvoiceSubscrStore_loadInvoiceTest() throws Exception {
    context = getContext();
    invoiceSubscrStore = (XObjectInvoiceSubscrStore) Utils.getComponent(
        IInvoiceStoreExtenderRole.class, "celInvoiceSubscr.xobject");
  }

  @Test
  public void testStoreInvoice_nothingToDo() {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    replayDefault();
    invoiceSubscrStore.storeInvoice(invoice, invoiceDoc);
    verifyDefault();
  }

}
