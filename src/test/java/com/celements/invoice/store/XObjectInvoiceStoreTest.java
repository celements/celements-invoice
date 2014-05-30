package com.celements.invoice.store;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.test.AbstractBridgedComponentTestCase;
import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.Invoice;
import com.celements.invoice.service.IInvoiceServiceRole;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.web.Utils;

public class XObjectInvoiceStoreTest extends AbstractBridgedComponentTestCase {

  private XObjectInvoiceStore invoiceStore;
  private IInvoiceServiceRole invoiceServiceMock;
  private XWikiContext context;
  private XWiki xwiki;

  @Before
  public void setUp_XObjectInvoiceStoreTest() throws Exception {
    context = getContext();
    xwiki = getWikiMock();
    invoiceStore = (XObjectInvoiceStore) Utils.getComponent(IInvoiceStoreRole.class,
        "xobject");
    invoiceServiceMock = createMockAndAddToDefault(IInvoiceServiceRole.class);
    invoiceStore.invoiceService = invoiceServiceMock;
  }

  @Test
  public void testLoadInvoiceByInvoiceNumber_notExists() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(invoiceServiceMock.getInvoiceDocRefForInvoiceNumber(eq(invoiceNumber))
        ).andReturn(invoiceDocRef).anyTimes();
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(false);
    replayDefault();
    assertNull(invoiceStore.loadInvoiceByInvoiceNumber(invoiceNumber));
    verifyDefault();
  }

  @Test
  public void testLoadInvoiceByInvoiceNumber_Exception() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(invoiceServiceMock.getInvoiceDocRefForInvoiceNumber(eq(invoiceNumber))
        ).andReturn(invoiceDocRef).anyTimes();
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andThrow(
        new XWikiException()).atLeastOnce();
    replayDefault();
    assertNull(invoiceStore.loadInvoiceByInvoiceNumber(invoiceNumber));
    verifyDefault();
  }

  @Test
  public void testLoadInvoiceByInvoiceNumber() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(invoiceServiceMock.getInvoiceDocRefForInvoiceNumber(eq(invoiceNumber))
        ).andReturn(invoiceDocRef).anyTimes();
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andReturn(invoiceDoc
        ).atLeastOnce();
    replayDefault();
    assertNotNull(invoiceStore.loadInvoiceByInvoiceNumber(invoiceNumber));
    verifyDefault();
  }

  @Test
  public void testStoreInvoice() {
    IInvoice theInvoice = new Invoice();
    replayDefault();
    invoiceStore.storeInvoice(theInvoice);
    verifyDefault();
  }

}
