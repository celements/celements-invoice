package com.celements.invoice.subscription.store;

import static junit.framework.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.common.test.AbstractBridgedComponentTestCase;
import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.store.IInvoiceStoreExtenderRole;
import com.celements.invoice.subscription.InvoiceSubscrReferenceDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.web.Utils;

public class XObjectInvoiceSubscrStore_loadInvoiceTest
    extends AbstractBridgedComponentTestCase {

  private XObjectInvoiceSubscrStore invoiceSubscrStore;
  private XWikiContext context;

  @Before
  public void setUp_InvoiceSubscrStore_loadInvoiceTest() throws Exception {
    context = getContext();
    invoiceSubscrStore = (XObjectInvoiceSubscrStore) Utils.getComponent(
        IInvoiceStoreExtenderRole.class, "celInvoiceSubscr.xobject");
  }

  @Test
  public void testLoadInvoice_noScriptionObject_OnInvoiceDoc() {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    replayDefault();
    invoiceSubscrStore.loadInvoice(invoiceDoc, invoice);
    assertTrue(invoice.getReferenceDocs().isEmpty());
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_SubscriptionReference() throws Exception {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    Calendar cal = Calendar.getInstance();
    Date to = cal.getTime();
    cal.add(Calendar.MONTH, -1);
    Date from = cal.getTime();
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    String subscrRef = "Abonnement.Reference";
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    assertFalse("Precondition failure 'to maynot equal from'", to.equals(from));
    DocumentReference subscrItemClassRef = getInvoiceClasses(
        ).getSubscriptionItemClassRef(context.getDatabase());
    BaseObject currentSubObject = new BaseObject();
    currentSubObject.setXClassReference(subscrItemClassRef);
    currentSubObject.setStringValue(InvoiceClassCollection.FIELD_INVOICE_SUBSCR_REF,
        subscrRef);
    currentSubObject.setDateValue(InvoiceClassCollection.FIELD_INVOICE_SUBSCR_FROM, from);
    currentSubObject.setDateValue(InvoiceClassCollection.FIELD_INVOICE_SUBSCR_TO, to);
    invoiceDoc.addXObject(currentSubObject);
    replayDefault();
    invoiceSubscrStore.loadInvoice(invoiceDoc, invoice);
    assertEquals(1, invoice.getReferenceDocs().size());
    InvoiceSubscrReferenceDocument invoiceRefDoc =
        (InvoiceSubscrReferenceDocument) invoice.getReferenceDocs().get(0);
    assertEquals(subscrRef, invoiceRefDoc.getSubscriptionReference());
    assertEquals(from, invoiceRefDoc.getFrom());
    assertEquals(to, invoiceRefDoc.getTo());
    verifyDefault();
  }
  
  private InvoiceClassCollection getInvoiceClasses() {
    return (InvoiceClassCollection) Utils.getComponent(IClassCollectionRole.class,
        "com.celements.invoice.classcollection");
  }

}
