package com.celements.invoice.service;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryManager;

import com.celements.common.test.AbstractComponentTest;
import com.celements.invoice.builder.IInvoice;
import com.celements.nextfreedoc.INextFreeDocRole;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.web.Utils;

public class InvoiceServiceTest extends AbstractComponentTest {

  private InvoiceService invoiceService;
  private XWikiContext context;
  private XWiki xwiki;
  private QueryManager queryManagerMock;
  private INextFreeDocRole nextFreeDocMock;

  @Before
  public void setUp() throws Exception {
    nextFreeDocMock = registerComponentMock(INextFreeDocRole.class);
    queryManagerMock = registerComponentMock(QueryManager.class);
    context = getXContext();
    xwiki = getMock(XWiki.class);
    invoiceService = (InvoiceService) Utils.getComponent(IInvoiceServiceRole.class, "xobject");
  }

  @Test
  public void testGetNewInvoiceNumber() throws Exception {
    String invoiceNumber = "12758";
    String lastMaxInvoiceNumber = "12757";
    Query mockQuery = createDefaultMock(Query.class);
    expect(queryManagerMock.createQuery(isA(String.class), eq(Query.HQL))).andReturn(
        mockQuery).once();
    expect(mockQuery.<String>execute()).andReturn(Arrays.<String>asList(
        lastMaxInvoiceNumber)).once();
    expect(xwiki.getXWikiPreferenceAsInt(eq(InvoiceService.PREF_MIN_INVOICE_NUMBER),
        eq(InvoiceService.XWIKICFG_MIN_INVOICE_NUMBER), eq(1), same(context))).andReturn(
            1).anyTimes();
    replayDefault();
    assertNotNull(invoiceNumber, invoiceService.getNewInvoiceNumber());
    verifyDefault();
  }

  @Test
  public void testGetNewInvoiceDocRef_nameHint_null() throws Exception {
    DocumentReference expectedDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    invoice.setDocumentNameHint(null);
    expect(nextFreeDocMock.getNextTitledPageDocRef(
        eq(expectedDocRef.getLastSpaceReference()), eq("Invoice"))).andReturn(
            expectedDocRef);
    replayDefault();
    assertEquals(expectedDocRef, invoiceService.getNewInvoiceDocRef(invoice));
    verifyDefault();
  }

  @Test
  public void testGetNewInvoiceDocRef_nameHint_empty() throws Exception {
    DocumentReference expectedDocRef = new DocumentReference(context.getDatabase(),
        "Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    invoice.setDocumentNameHint("");
    expect(nextFreeDocMock.getNextTitledPageDocRef(
        eq(expectedDocRef.getLastSpaceReference()), eq("Invoice"))).andReturn(
            expectedDocRef);
    replayDefault();
    assertEquals(expectedDocRef, invoiceService.getNewInvoiceDocRef(invoice));
    verifyDefault();
  }

  @Test
  public void testGetNewInvoiceDocRef() throws Exception {
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    DocumentReference expDocRef = new DocumentReference(context.getDatabase(), "Invoices",
        "Invoice1");
    expect(nextFreeDocMock.getNextTitledPageDocRef(eq(expDocRef.getLastSpaceReference()),
        eq("Invoice"))).andReturn(expDocRef).once();
    replayDefault();
    assertEquals(expDocRef, invoiceService.getNewInvoiceDocRef(invoice));
    verifyDefault();
  }

  @Test
  public void testGetNewInvoiceDocRef_withHint() throws Exception {
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    invoice.setDocumentNameHint("test");
    DocumentReference expDocRef = new DocumentReference(context.getDatabase(),
        "test-Invoices", "Invoice1");
    expect(nextFreeDocMock.getNextTitledPageDocRef(eq(expDocRef.getLastSpaceReference()),
        eq("Invoice"))).andReturn(expDocRef).once();
    replayDefault();
    assertEquals(expDocRef, invoiceService.getNewInvoiceDocRef(invoice));
    verifyDefault();
  }

}
