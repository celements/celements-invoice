package com.celements.invoice.service;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryManager;

import com.celements.common.test.AbstractBridgedComponentTestCase;
import com.celements.invoice.builder.IInvoice;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.web.Utils;

public class InvoiceServiceTest extends AbstractBridgedComponentTestCase {

  private InvoiceService invoiceService;
  private XWikiContext context;
  private XWiki xwiki;
  private QueryManager queryManagerMock;

  @Before
  public void setUp_XObjectInvoiceStoreTest() throws Exception {
    context = getContext();
    xwiki = getWikiMock();
    invoiceService = (InvoiceService) Utils.getComponent(IInvoiceServiceRole.class,
        "xobject");
    queryManagerMock = createMockAndAddToDefault(QueryManager.class);
    invoiceService.query = queryManagerMock;
  }

  @Test
  public void testGetNewInvoiceNumber() throws Exception {
    String invoiceNumber = "12758";
    String lastMaxInvoiceNumber = "12757";
    Query mockQuery = createMockAndAddToDefault(Query.class);
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
    expect(xwiki.exists(eq(expectedDocRef), same(context))).andReturn(false
        ).atLeastOnce();
    XWikiDocument expectedDocMock = createMockAndAddToDefault(XWikiDocument.class);
    expect(xwiki.getDocument(eq(expectedDocRef), same(context))).andReturn(
        expectedDocMock).atLeastOnce();
    expect(expectedDocMock.getLock(same(context))).andReturn(null).atLeastOnce();
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
    expect(xwiki.exists(eq(expectedDocRef), same(context))).andReturn(false
        ).atLeastOnce();
    XWikiDocument expectedDocMock = createMockAndAddToDefault(XWikiDocument.class);
    expect(xwiki.getDocument(eq(expectedDocRef), same(context))).andReturn(
        expectedDocMock).atLeastOnce();
    expect(expectedDocMock.getLock(same(context))).andReturn(null).atLeastOnce();
    replayDefault();
    assertEquals(expectedDocRef, invoiceService.getNewInvoiceDocRef(invoice));
    verifyDefault();
  }

  @Test
  public void testGetNewInvoiceDocRef() throws Exception {
    DocumentReference expectedDocRef = new DocumentReference(context.getDatabase(),
        "test-Invoices", "Invoice1");
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    invoice.setDocumentNameHint("test");
    expect(xwiki.exists(eq(expectedDocRef), same(context))).andReturn(false
        ).atLeastOnce();
    XWikiDocument expectedDocMock = createMockAndAddToDefault(XWikiDocument.class);
    expect(xwiki.getDocument(eq(expectedDocRef), same(context))).andReturn(
        expectedDocMock).atLeastOnce();
    expect(expectedDocMock.getLock(same(context))).andReturn(null).atLeastOnce();
    replayDefault();
    assertEquals(expectedDocRef, invoiceService.getNewInvoiceDocRef(invoice));
    verifyDefault();
  }

}
