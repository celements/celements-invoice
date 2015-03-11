package com.celements.invoice.store;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
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

  @Before
  public void setUp_XObjectInvoiceStoreTest() throws Exception {
    context = getContext();
    xwiki = getWikiMock();
    invoiceStore = (XObjectInvoiceStore) Utils.getComponent(IInvoiceStoreRole.class,
        "xobject");
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
    verifyDefault();
  }

  @Test
  public void testConvertToInvoiceItem() {
    int amount1 = 2;
    String unitOfMeasure = "EA";
    String articleNr1 = "ArtNr4665";
    String orderNr1 = "OrderNumber1123";
    int unitPrice1 = 34560;
    float unitOfPrice1 = 10;
    int vatCode = 2;
    float vatValue = 23.24F;
    String descr = "the item you ordered";
    BaseObject invoiceItemObj = new BaseObject();
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_ARTICLE_NR, articleNr1);
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER, orderNr1);
    invoiceItemObj.setIntValue(InvoiceClassCollection.FIELD_AMOUNT, amount1);
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_UNIT_OF_MEASURE,
        unitOfMeasure);
    invoiceItemObj.setIntValue(InvoiceClassCollection.FIELD_UNIT_PRICE, unitPrice1);
    invoiceItemObj.setFloatValue(InvoiceClassCollection.FIELD_UNIT_OF_PRICE, unitOfPrice1);
    invoiceItemObj.setIntValue(InvoiceClassCollection.FIELD_VAT_CODE, vatCode);
    invoiceItemObj.setFloatValue(InvoiceClassCollection.FIELD_VAT_VALUE, vatValue);
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_ITEM_DESCRIPTION, descr);
    replayDefault();
    IInvoiceItem invoiceItem = invoiceStore.convertToInvoiceItem(invoiceItemObj);
    assertEquals(amount1, invoiceItem.getAmount());
    assertEquals(unitOfMeasure, invoiceItem.getUnitOfMeasure());
    assertEquals(articleNr1, invoiceItem.getArticleNr());
    assertEquals(orderNr1, invoiceItem.getOrderNr());
    assertEquals(unitPrice1, invoiceItem.getUnitPrice());
    assertEquals(unitOfPrice1, invoiceItem.getUnitOfPrice(), 0.00001);
    assertEquals(vatCode, invoiceItem.getVATCode());
    assertEquals(vatValue, invoiceItem.getVATValue(), 0.001);
    assertEquals(descr, invoiceItem.getName());
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_notExists() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(false);
    replayDefault();
    assertNull(invoiceStore.loadInvoice(invoiceDocRef));
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_Exception() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andThrow(
        new XWikiException()).atLeastOnce();
    replayDefault();
    assertNull(invoiceStore.loadInvoice(invoiceDocRef));
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_notAnInvoiceDocument() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andReturn(invoiceDoc
        ).atLeastOnce();
    replayDefault();
    assertNull(invoiceStore.loadInvoice(invoiceDocRef));
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_emptyInvoice() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setXClassReference(getInvoiceClasses().getInvoiceClassRef(getContext(
        ).getDatabase()));
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_NUMBER,
        invoiceNumber);
    invoiceDoc.addXObject(invoiceObj);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andReturn(invoiceDoc
        ).atLeastOnce();
    replayDefault();
    IInvoice invoice = invoiceStore.loadInvoice(invoiceDocRef);
    assertNotNull(invoice);
    assertEquals(invoiceNumber, invoice.getInvoiceNumber());
    assertTrue("expecting empty invoiceItems list.", invoice.getInvoiceItems().isEmpty());
    verifyDefault();
  }

  @Test
  public void testLoadInvoice() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setXClassReference(getInvoiceClasses().getInvoiceClassRef(getContext(
        ).getDatabase()));
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_NUMBER,
        invoiceNumber);
    invoiceDoc.addXObject(invoiceObj);
    BaseObject invoiceItem1Obj = new BaseObject();
    invoiceItem1Obj.setXClassReference(getInvoiceClasses().getInvoiceItemClassRef(
        getContext().getDatabase()));
    String articleNr1 = "ArticleNr1";
    invoiceItem1Obj.setStringValue(InvoiceClassCollection.FIELD_ARTICLE_NR, articleNr1);
    String orderNr1 = "OrderNr1";
    invoiceItem1Obj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER, orderNr1);
    int amount1 = 8;
    invoiceItem1Obj.setIntValue(InvoiceClassCollection.FIELD_AMOUNT, amount1);
    invoiceDoc.addXObject(invoiceItem1Obj);
    BaseObject invoiceItem2Obj = new BaseObject();
    invoiceItem2Obj.setXClassReference(getInvoiceClasses().getInvoiceItemClassRef(
        getContext().getDatabase()));
    String articleNr2 = "ArticleNr2";
    invoiceItem2Obj.setStringValue(InvoiceClassCollection.FIELD_ARTICLE_NR, articleNr2);
    String orderNr2 = "OrderNr2";
    invoiceItem2Obj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER, orderNr2);
    int amount2 = 5;
    invoiceItem2Obj.setIntValue(InvoiceClassCollection.FIELD_AMOUNT, amount2);
    invoiceDoc.addXObject(invoiceItem2Obj);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andReturn(invoiceDoc
        ).atLeastOnce();
    replayDefault();
    IInvoice invoice = invoiceStore.loadInvoice(invoiceDocRef);
    assertNotNull(invoice);
    assertEquals(invoiceNumber, invoice.getInvoiceNumber());
    List<IInvoiceItem> invoiceItemsList = invoice.getInvoiceItems();
    assertFalse("expecting NOT empty invoiceItems list.", invoiceItemsList.isEmpty());
    assertEquals("expecting 2 invoice itmes in list.", 2, invoiceItemsList.size());
    IInvoiceItem firstItem = invoiceItemsList.get(0);
    assertEquals(amount1, firstItem.getAmount());
    assertEquals(articleNr1, firstItem.getArticleNr());
    assertEquals(orderNr1, firstItem.getOrderNr());
    IInvoiceItem secondItem = invoiceItemsList.get(1);
    assertEquals(amount2, secondItem.getAmount());
    assertEquals(articleNr2, secondItem.getArticleNr());
    assertEquals(orderNr2, secondItem.getOrderNr());
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_deletedItemObject() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setXClassReference(getInvoiceClasses().getInvoiceClassRef(getContext(
        ).getDatabase()));
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_NUMBER,
        invoiceNumber);
    invoiceDoc.addXObject(invoiceObj);
    BaseObject invoiceItem1Obj = new BaseObject();
    invoiceItem1Obj.setXClassReference(getInvoiceClasses().getInvoiceItemClassRef(
        getContext().getDatabase()));
    String articleNr1 = "ArticleNr1";
    invoiceItem1Obj.setStringValue(InvoiceClassCollection.FIELD_ARTICLE_NR, articleNr1);
    String orderNr1 = "OrderNr1";
    invoiceItem1Obj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER, orderNr1);
    int amount1 = 8;
    invoiceItem1Obj.setIntValue(InvoiceClassCollection.FIELD_AMOUNT, amount1);
    BaseObject invoiceItem2Obj = new BaseObject();
    invoiceItem2Obj.setXClassReference(getInvoiceClasses().getInvoiceItemClassRef(
        getContext().getDatabase()));
    String articleNr2 = "ArticleNr2";
    invoiceItem2Obj.setStringValue(InvoiceClassCollection.FIELD_ARTICLE_NR, articleNr2);
    String orderNr2 = "OrderNr2";
    invoiceItem2Obj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER, orderNr2);
    int amount2 = 5;
    invoiceItem2Obj.setIntValue(InvoiceClassCollection.FIELD_AMOUNT, amount2);
    List<BaseObject> invoiceItemList = Arrays.asList(invoiceItem1Obj, null,
        invoiceItem2Obj);
    invoiceDoc.setXObjects(getInvoiceClasses().getInvoiceItemClassRef(
        getContext().getDatabase()), invoiceItemList);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andReturn(invoiceDoc
        ).atLeastOnce();
    replayDefault();
    IInvoice invoice = invoiceStore.loadInvoice(invoiceDocRef);
    assertNotNull(invoice);
    assertEquals(invoiceNumber, invoice.getInvoiceNumber());
    List<IInvoiceItem> invoiceItemsList = invoice.getInvoiceItems();
    assertFalse("expecting NOT empty invoiceItems list.", invoiceItemsList.isEmpty());
    assertEquals("expecting 2 invoice itmes in list.", 2, invoiceItemsList.size());
    IInvoiceItem firstItem = invoiceItemsList.get(0);
    assertEquals(amount1, firstItem.getAmount());
    assertEquals(articleNr1, firstItem.getArticleNr());
    assertEquals(orderNr1, firstItem.getOrderNr());
    IInvoiceItem secondItem = invoiceItemsList.get(1);
    assertEquals(amount2, secondItem.getAmount());
    assertEquals(articleNr2, secondItem.getArticleNr());
    assertEquals(orderNr2, secondItem.getOrderNr());
    verifyDefault();
  }

  @Test
  public void testLoadInvoice_ItemPosition() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setXClassReference(getInvoiceClasses().getInvoiceClassRef(getContext(
        ).getDatabase()));
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_NUMBER,
        invoiceNumber);
    invoiceDoc.addXObject(invoiceObj);
    BaseObject invoiceItem1Obj = new BaseObject();
    invoiceItem1Obj.setXClassReference(getInvoiceClasses().getInvoiceItemClassRef(
        getContext().getDatabase()));
    String articleNr1 = "ArticleNr1";
    invoiceItem1Obj.setStringValue(InvoiceClassCollection.FIELD_ARTICLE_NR, articleNr1);
    String orderNr1 = "OrderNr1";
    invoiceItem1Obj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER, orderNr1);
    int amount1 = 8;
    invoiceItem1Obj.setIntValue(InvoiceClassCollection.FIELD_AMOUNT, amount1);
    invoiceItem1Obj.setIntValue(InvoiceClassCollection.FIELD_ITEM_POSITION, 1);
    BaseObject invoiceItem2Obj = new BaseObject();
    invoiceItem2Obj.setXClassReference(getInvoiceClasses().getInvoiceItemClassRef(
        getContext().getDatabase()));
    String articleNr2 = "ArticleNr2";
    invoiceItem2Obj.setStringValue(InvoiceClassCollection.FIELD_ARTICLE_NR, articleNr2);
    String orderNr2 = "OrderNr2";
    invoiceItem2Obj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER, orderNr2);
    int amount2 = 5;
    invoiceItem2Obj.setIntValue(InvoiceClassCollection.FIELD_AMOUNT, amount2);
    invoiceItem2Obj.setIntValue(InvoiceClassCollection.FIELD_ITEM_POSITION, 2);
    //inverse list to test sorting on loading!!!
    List<BaseObject> invoiceItemList = Arrays.asList(invoiceItem2Obj, invoiceItem1Obj);
    invoiceDoc.setXObjects(getInvoiceClasses().getInvoiceItemClassRef(
        getContext().getDatabase()), invoiceItemList);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andReturn(invoiceDoc
        ).atLeastOnce();
    replayDefault();
    IInvoice invoice = invoiceStore.loadInvoice(invoiceDocRef);
    assertNotNull(invoice);
    assertEquals(invoiceNumber, invoice.getInvoiceNumber());
    List<IInvoiceItem> invoiceItemsList = invoice.getInvoiceItems();
    assertFalse("expecting NOT empty invoiceItems list.", invoiceItemsList.isEmpty());
    assertEquals("expecting 2 invoice itmes in list.", 2, invoiceItemsList.size());
    IInvoiceItem firstItem = invoiceItemsList.get(0);
    assertEquals(articleNr1, firstItem.getArticleNr());
    assertEquals(amount1, firstItem.getAmount());
    assertEquals(orderNr1, firstItem.getOrderNr());
    IInvoiceItem secondItem = invoiceItemsList.get(1);
    assertEquals(amount2, secondItem.getAmount());
    assertEquals(articleNr2, secondItem.getArticleNr());
    assertEquals(orderNr2, secondItem.getOrderNr());
    verifyDefault();
  }

  private InvoiceClassCollection getInvoiceClasses() {
    return (InvoiceClassCollection) Utils.getComponent(IClassCollectionRole.class,
        "com.celements.invoice.classcollection");
  }

}
