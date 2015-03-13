package com.celements.invoice.store;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.common.test.AbstractBridgedComponentTestCase;
import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.EInvoiceStatus;
import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.IInvoiceItem;
import com.google.common.base.Function;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.web.Utils;

public class XObjectInvoiceStore_loadInvoiceTest extends AbstractBridgedComponentTestCase {

  private XObjectInvoiceStore invoiceStore;
  private XWikiContext context;
  private XWiki xwiki;
  
  private DocumentReference invoiceDocRef;
  private XWikiDocument invoiceDoc;

  @Before
  public void setUp_XObjectInvoiceStoreTest() throws Exception {
    context = getContext();
    xwiki = getWikiMock();
    invoiceStore = (XObjectInvoiceStore) Utils.getComponent(IInvoiceStoreRole.class,
        "xobject");
    invoiceDocRef = new DocumentReference(context.getDatabase(), "InvoicesSpace", 
        "InvoiceDoc");
    invoiceDoc = new XWikiDocument(invoiceDocRef);
  }
  
  private void expectInvoiceDoc() throws Exception {
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andReturn(invoiceDoc
        ).once();
  }

  @Test
  public void testConvertToInvoice() {
    String orderNr = "OrderNumber1123";
    String invoiceNumber = "A126587";
    String invoiceSubj = "Invoice for May 2014";
    String currency = "CHF";
    String comment = "nothing to comment here";
    Date invoiceDate = new Date();
    int totalPrice = 34560;
    int vatFree = 1050;
    int vatReduced = 2030;
    int vatFull = 4560;
    String invoiceStatus = "printed";
    int invoiceCancelled = 1;
    String customerId = "Customer1234";
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_NUMBER, invoiceNumber);
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER, orderNr);
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_SUBJECT, invoiceSubj);
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_CURRENCY, currency);
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_COMMENT, comment);
    invoiceObj.setDateValue(InvoiceClassCollection.FIELD_INVOICE_DATE, invoiceDate);
    invoiceObj.setIntValue(InvoiceClassCollection.FIELD_TOTAL_PRICE, totalPrice);
    invoiceObj.setIntValue(InvoiceClassCollection.FIELD_TOTAL_VAT_FREE, vatFree);
    invoiceObj.setIntValue(InvoiceClassCollection.FIELD_TOTAL_VAT_REDUCED, vatReduced);
    invoiceObj.setIntValue(InvoiceClassCollection.FIELD_TOTAL_VAT_FULL, vatFull);
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_STATUS, invoiceStatus);
    invoiceObj.setIntValue(InvoiceClassCollection.FIELD_INVOICE_CANCELLED,
        invoiceCancelled);
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_CUSTOMER_ID, customerId);
    replayDefault();
    IInvoice invoice = invoiceStore.convertToInvoice(invoiceObj);
    assertEquals(invoiceNumber, invoice.getInvoiceNumber());
    assertEquals(orderNr, invoice.getOrderNr());
    assertEquals(invoiceSubj, invoice.getName());
    assertEquals(currency, invoice.getCurrency());
    assertEquals(comment, invoice.getComment());
    assertEquals(invoiceDate, invoice.getInvoiceDate());
    assertEquals(totalPrice, invoice.getPrice());
    assertEquals(vatFree, invoice.getTotalVATFree());
    assertEquals(vatReduced, invoice.getTotalVATReduced());
    assertEquals(vatFull, invoice.getTotalVATFull());
    assertEquals(EInvoiceStatus.isPrinted, invoice.getStatus());
    assertTrue(invoice.isCancelled());
    assertEquals(customerId, invoice.getCustomerId());
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_notExists() throws Exception {
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(false);
    replayDefault();
    assertNull(invoiceStore.loadInvoice(invoiceDocRef));
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_Exception() throws Exception {
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andThrow(
        new XWikiException()).once();
    replayDefault();
    assertNull(invoiceStore.loadInvoice(invoiceDocRef));
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_notAnInvoiceDocument() throws Exception {
    expectInvoiceDoc();
    replayDefault();
    assertNull(invoiceStore.loadInvoice(invoiceDocRef));
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_emptyInvoice() throws Exception {
    String invoiceNumber = "A12758";
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setXClassReference(getInvoiceClasses().getInvoiceClassRef(getContext(
        ).getDatabase()));
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_NUMBER, invoiceNumber);
    invoiceDoc.addXObject(invoiceObj);
    
    expectInvoiceDoc();
    
    replayDefault();
    IInvoice invoice = invoiceStore.loadInvoice(invoiceDocRef);
    verifyDefault();
    
    assertNotNull(invoice);
    assertEquals(invoiceNumber, invoice.getInvoiceNumber());
    assertTrue("expecting empty invoiceItems list.", invoice.getInvoiceItems().isEmpty());
  }

  @Test
  public void testLoadInvoice() throws Exception {
    String invoiceNumber = "A12758";
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setXClassReference(getInvoiceClasses().getInvoiceClassRef(getContext(
        ).getDatabase()));
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_NUMBER, invoiceNumber);
    invoiceDoc.addXObject(invoiceObj);
    invoiceDoc.addXObject(getInvoiceItemObj(1));
    invoiceDoc.addXObject(getInvoiceItemObj(2));
    
    expectInvoiceDoc();
    
    replayDefault();
    IInvoice invoice = invoiceStore.loadInvoice(invoiceDocRef);
    verifyDefault();
    
    assertNotNull(invoice);
    assertEquals(invoiceNumber, invoice.getInvoiceNumber());
    List<IInvoiceItem> invoiceItemsList = invoice.getInvoiceItems();
    assertFalse("expecting NOT empty invoiceItems list.", invoiceItemsList.isEmpty());
    assertEquals("expecting 2 invoice itmes in list.", 2, invoiceItemsList.size());
    assertInvoiceItem(invoiceItemsList.get(0), 1);
    assertInvoiceItem(invoiceItemsList.get(1), 2);
  }

  @Test
  public void testLoadInvoice_ItemPosition() throws Exception {
    String invoiceNumber = "A12758";
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setXClassReference(getInvoiceClasses().getInvoiceClassRef(getContext(
        ).getDatabase()));
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_NUMBER, invoiceNumber);
    invoiceDoc.addXObject(invoiceObj);

    //inverse adding order to test sorting on loading!!!
    invoiceDoc.addXObject(getInvoiceItemObj(2));
    invoiceDoc.addXObject(getInvoiceItemObj(1));

    expectInvoiceDoc();
    
    replayDefault();
    IInvoice invoice = invoiceStore.loadInvoice(invoiceDocRef);
    verifyDefault();
    
    assertNotNull(invoice);
    assertEquals(invoiceNumber, invoice.getInvoiceNumber());
    List<IInvoiceItem> invoiceItemsList = invoice.getInvoiceItems();
    assertFalse("expecting NOT empty invoiceItems list.", invoiceItemsList.isEmpty());
    assertEquals("expecting 2 invoice itmes in list.", 2, invoiceItemsList.size());
    assertInvoiceItem(invoiceItemsList.get(0), 1);
    assertInvoiceItem(invoiceItemsList.get(1), 2);
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testLoadInvoice_extended() throws Exception {
    String invoiceNumber = "A12758";
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setXClassReference(getInvoiceClasses().getInvoiceClassRef(getContext(
        ).getDatabase()));
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_NUMBER, invoiceNumber);
    invoiceDoc.addXObject(invoiceObj);
    invoiceDoc.addXObject(getInvoiceItemObj(1));
    invoiceDoc.addXObject(getInvoiceItemObj(2));
    Function<Object, Object> mock = createMock(Function.class);
    invoiceStore.storeExtenderMap.put("test", new TestInvoiceStoreExtender(mock));
    
    expectInvoiceDoc();
    expect(mock.apply(eq("load"))).andReturn(true).once();
    expect(mock.apply(same(invoiceDoc))).andReturn(true).once();
    expect(mock.apply(isA(IInvoice.class))).andReturn(true).once();
    
    replayDefault();
    IInvoice invoice = invoiceStore.loadInvoice(invoiceDocRef);
    verifyDefault();
    
    assertNotNull(invoice);
    assertEquals(invoiceNumber, invoice.getInvoiceNumber());
    List<IInvoiceItem> invoiceItemsList = invoice.getInvoiceItems();
    assertFalse("expecting NOT empty invoiceItems list.", invoiceItemsList.isEmpty());
    assertEquals("expecting 2 invoice itmes in list.", 2, invoiceItemsList.size());
    assertInvoiceItem(invoiceItemsList.get(0), 1);
    assertInvoiceItem(invoiceItemsList.get(1), 2);
  }
  
  @Test
  public void testLoadInvoiceItem_notExists() throws Exception {    
    int pos = 1;
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(false);
    
    replayDefault();
    assertNull(invoiceStore.loadInvoiceItem(invoiceDocRef, pos));
    verifyDefault();
  }
  
  @Test
  public void testLoadInvoiceItem_Exception() throws Exception {
    int pos = 1;
    invoiceDoc.addXObject(getInvoiceItemObj(pos));
    
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andThrow(
        new XWikiException()).once();
    
    replayDefault();
    assertNull(invoiceStore.loadInvoiceItem(invoiceDocRef, pos));
    verifyDefault();
  }
  
  @Test
  public void testLoadInvoiceItem_notAnInvoiceDoc() throws Exception {
    int pos = 1;
    expectInvoiceDoc();
    
    replayDefault();
    assertNull(invoiceStore.loadInvoiceItem(invoiceDocRef, pos));
    verifyDefault();
  }
  
  @Test
  public void testLoadInvoiceItem_noItemForPos() throws Exception {
    int pos = 1;
    invoiceDoc.addXObject(getInvoiceItemObj(pos + 1));
    expectInvoiceDoc();
    
    replayDefault();
    assertNull(invoiceStore.loadInvoiceItem(invoiceDocRef, pos));
    verifyDefault();
  }
  
  @Test
  public void testLoadInvoiceItem_emptyItem() throws Exception {
    int pos = 1;
    BaseObject obj = new BaseObject();
    obj.setXClassReference(getInvoiceClasses().getInvoiceItemClassRef(
        getContext().getDatabase()));
    obj.setIntValue(InvoiceClassCollection.FIELD_ITEM_POSITION, pos);
    invoiceDoc.addXObject(obj);
    
    expectInvoiceDoc();
    
    replayDefault();
    IInvoiceItem ret = invoiceStore.loadInvoiceItem(invoiceDocRef, pos);
    verifyDefault();
    
    assertNotNull(ret);
    assertEquals("", ret.getName());
    assertEquals(0, ret.getAmount());
  }
  
  @Test
  public void testLoadInvoiceItem() throws Exception {
    int pos = 1;
    invoiceDoc.addXObject(getInvoiceItemObj(pos));
    
    expectInvoiceDoc();
    
    replayDefault();
    IInvoiceItem ret = invoiceStore.loadInvoiceItem(invoiceDocRef, pos);
    verifyDefault();
    
    assertInvoiceItem(ret, pos);
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testLoadInvoiceItem_extended() throws Exception {
    int pos = 1;
    invoiceDoc.addXObject(getInvoiceItemObj(pos));
    Function<Object, Object> mock = createMock(Function.class);
    invoiceStore.storeExtenderMap.put("test", new TestInvoiceStoreExtender(mock));
    
    expectInvoiceDoc();
    expect(mock.apply(eq("load"))).andReturn(true).once();
    expect(mock.apply(same(invoiceDoc))).andReturn(true).once();
    expect(mock.apply(isA(IInvoiceItem.class))).andReturn(true).once();
    
    replayDefault(mock);
    IInvoiceItem ret = invoiceStore.loadInvoiceItem(invoiceDocRef, pos);
    verifyDefault(mock);
    
    assertInvoiceItem(ret, pos);
  }

  private BaseObject getInvoiceItemObj(int pos) {
    BaseObject obj = new BaseObject();
    obj.setXClassReference(getInvoiceClasses().getInvoiceItemClassRef(
        getContext().getDatabase()));
    obj.setIntValue(InvoiceClassCollection.FIELD_AMOUNT, 2 + pos);
    obj.setStringValue(InvoiceClassCollection.FIELD_UNIT_OF_MEASURE, "UnitMeasure" + pos);
    obj.setIntValue(InvoiceClassCollection.FIELD_UNIT_PRICE, 3 + pos);
    obj.setFloatValue(InvoiceClassCollection.FIELD_UNIT_OF_PRICE, 1.1f + pos);
    obj.setIntValue(InvoiceClassCollection.FIELD_VAT_CODE, 4 + pos);
    obj.setFloatValue(InvoiceClassCollection.FIELD_VAT_VALUE, 2.3f + pos);
    obj.setStringValue(InvoiceClassCollection.FIELD_ITEM_DESCRIPTION, "Descr" + pos);
    obj.setIntValue(InvoiceClassCollection.FIELD_ITEM_POSITION, pos);
    obj.setStringValue(InvoiceClassCollection.FIELD_ARTICLE_NR, "ArticleNr" + pos);
    obj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER, "OrderNr" + pos);
    return obj;
  }
  
  private void assertInvoiceItem(IInvoiceItem item, int pos) {
    assertNotNull(item);
    assertEquals("Descr" + pos, item.getName());
    assertEquals(2 + pos, item.getAmount());
    assertEquals("UnitMeasure" + pos, item.getUnitOfMeasure());
    assertEquals(3 + pos, item.getUnitPrice());
    assertEquals(1.1f + pos, item.getUnitOfPrice(), 0);
    assertEquals(4 + pos, item.getVATCode());
    assertEquals(2.3f + pos, item.getVATValue(), 0);
    assertEquals("ArticleNr" + pos, item.getArticleNr());
    assertEquals("OrderNr"+ pos, item.getOrderNr());
  }

  private InvoiceClassCollection getInvoiceClasses() {
    return (InvoiceClassCollection) Utils.getComponent(IClassCollectionRole.class,
        "com.celements.invoice.classcollection");
  }

}
