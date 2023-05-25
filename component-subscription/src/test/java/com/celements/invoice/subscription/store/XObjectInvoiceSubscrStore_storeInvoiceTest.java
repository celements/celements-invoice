package com.celements.invoice.subscription.store;

import static com.celements.common.test.CelementsTestUtils.*;
import static junit.framework.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.common.test.AbstractComponentTest;
import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.IInvoiceReferenceDocument;
import com.celements.invoice.store.IInvoiceStoreExtenderRole;
import com.celements.invoice.subscription.InvoiceSubscrReferenceDocument;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.web.Utils;

public class XObjectInvoiceSubscrStore_storeInvoiceTest
    extends AbstractComponentTest {

  private XObjectInvoiceSubscrStore invoiceSubscrStore;
  private XWikiContext context;
  private XWiki xwiki;

  @Before
  public void setUp_InvoiceSubscrStore_loadInvoiceTest() throws Exception {
    context = getContext();
    xwiki = getWikiMock();
    invoiceSubscrStore = (XObjectInvoiceSubscrStore) Utils.getComponent(
        IInvoiceStoreExtenderRole.class, "celInvoiceSubscr.xobject");
  }

  @Test
  public void testStoreInvoice_emptyRefDocList() {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    replayDefault();
    invoiceSubscrStore.storeInvoice(invoice, invoiceDoc);
    BaseObject subscrObj = invoiceDoc.getXObject(getInvoiceClasses(
        ).getSubscriptionItemClassRef(context.getDatabase()));
    assertNull("wrong subscription object on invoice found.", subscrObj);
    verifyDefault();
  }

  @Test
  public void testStoreInvoice_noSubRefDoc_in_refDocList() {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    IInvoiceReferenceDocument invoiceRefDoc = createDefaultMock(
        IInvoiceReferenceDocument.class);
    invoice.addInvoiceReferenceDocument(invoiceRefDoc);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    replayDefault();
    invoiceSubscrStore.storeInvoice(invoice, invoiceDoc);
    BaseObject subscrObj = invoiceDoc.getXObject(getInvoiceClasses(
        ).getSubscriptionItemClassRef(context.getDatabase()));
    assertNull("wrong subscription object on invoice found.", subscrObj);
    verifyDefault();
  }

  @Test
  public void testStoreInvoice_SubscriptionReference() throws Exception {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    Calendar cal = Calendar.getInstance();
    Date to = cal.getTime();
    cal.add(Calendar.MONTH, -1);
    Date from = cal.getTime();
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    InvoiceSubscrReferenceDocument invoiceRefDoc = (InvoiceSubscrReferenceDocument)
        Utils.getComponent(IInvoiceReferenceDocument.class, "subscription");
    invoiceRefDoc.setFrom(from);
    invoiceRefDoc.setTo(to);
    String subscrRef = "Abonnement.Reference";
    invoiceRefDoc.setSubscriptionReference(subscrRef);
    invoice.addInvoiceReferenceDocument(invoiceRefDoc);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    assertFalse("Precondition failure 'to maynot equal from'", to.equals(from));
    DocumentReference subscrItemClassRef = getInvoiceClasses(
        ).getSubscriptionItemClassRef(context.getDatabase());
    BaseClass invClassMock = createDefaultMock(BaseClass.class);
    expect(xwiki.getXClass(eq(subscrItemClassRef), same(context))).andReturn(invClassMock);
    expect(invClassMock.newCustomClassInstance(same(context))).andReturn(
        new BaseObject()).once();
    replayDefault();
    invoiceSubscrStore.storeInvoice(invoice, invoiceDoc);
    BaseObject subscrObj = invoiceDoc.getXObject(subscrItemClassRef);
    assertNotNull("subscription object on invoice document missing.", subscrObj);
    assertEquals(subscrRef, subscrObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_SUBSCR_REF));
    assertEquals(from, subscrObj.getDateValue(
        InvoiceClassCollection.FIELD_INVOICE_SUBSCR_FROM));
    assertEquals(to, subscrObj.getDateValue(
        InvoiceClassCollection.FIELD_INVOICE_SUBSCR_TO));
    verifyDefault();
  }
  
  @Test
  public void testStoreInvoice_update_SubscriptionReference() throws Exception {
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    Calendar cal = Calendar.getInstance();
    Date to = cal.getTime();
    cal.add(Calendar.MONTH, -1);
    Date from = cal.getTime();
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    InvoiceSubscrReferenceDocument invoiceRefDoc = (InvoiceSubscrReferenceDocument)
        Utils.getComponent(IInvoiceReferenceDocument.class, "subscription");
    invoiceRefDoc.setFrom(from);
    invoiceRefDoc.setTo(to);
    String subscrRef = "Abonnement.Reference";
    invoiceRefDoc.setSubscriptionReference(subscrRef);
    invoice.addInvoiceReferenceDocument(invoiceRefDoc);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    assertFalse("Precondition failure 'to maynot equal from'", to.equals(from));
    DocumentReference subscrItemClassRef = getInvoiceClasses(
        ).getSubscriptionItemClassRef(context.getDatabase());
    BaseObject currentSubObject = new BaseObject();
    currentSubObject.setXClassReference(subscrItemClassRef);
    invoiceDoc.addXObject(currentSubObject);
    replayDefault();
    invoiceSubscrStore.storeInvoice(invoice, invoiceDoc);
    assertEquals(1, invoiceDoc.getXObjects(subscrItemClassRef).size());
    BaseObject subscrObj = invoiceDoc.getXObject(subscrItemClassRef);
    assertNotNull("subscription object on invoice document missing.", subscrObj);
    assertEquals(subscrRef, subscrObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_SUBSCR_REF));
    assertEquals(from, subscrObj.getDateValue(
        InvoiceClassCollection.FIELD_INVOICE_SUBSCR_FROM));
    assertEquals(to, subscrObj.getDateValue(
        InvoiceClassCollection.FIELD_INVOICE_SUBSCR_TO));
    verifyDefault();
  }

  private InvoiceClassCollection getInvoiceClasses() {
    return (InvoiceClassCollection) Utils.getComponent(IClassCollectionRole.class,
        "com.celements.invoice.classcollection");
  }

}
