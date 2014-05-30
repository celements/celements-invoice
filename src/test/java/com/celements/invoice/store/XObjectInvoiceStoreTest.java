package com.celements.invoice.store;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.common.test.AbstractBridgedComponentTestCase;
import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.IInvoiceItem;
import com.celements.invoice.builder.Invoice;
import com.celements.invoice.service.IInvoiceServiceRole;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
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
  public void testConvertToInvoiceItem() {
    int amount1 = 2;
    String articleNr1 = "ArtNr4665";
    String orderNr1 = "OrderNumber1123";
    BaseObject invoiceItemObj = new BaseObject();
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_ARTICLE_NR, articleNr1);
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER, orderNr1);
    invoiceItemObj.setIntValue(InvoiceClassCollection.FIELD_AMOUNT, amount1);
    replayDefault();
    IInvoiceItem invoiceItem = invoiceStore.convertToInvoiceItem(invoiceItemObj);
    assertEquals(amount1, invoiceItem.getAmount());
    assertEquals(articleNr1, invoiceItem.getArticleNr());
    assertEquals(orderNr1, invoiceItem.getOrderNr());
    verifyDefault();
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
  public void testLoadInvoiceByInvoiceNumber_notAnInvoiceDocument() throws Exception {
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
    assertNull(invoiceStore.loadInvoiceByInvoiceNumber(invoiceNumber));
    verifyDefault();
  }

  @Test
  public void testLoadInvoiceByInvoiceNumber_emptyInvoice() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(invoiceServiceMock.getInvoiceDocRefForInvoiceNumber(eq(invoiceNumber))
        ).andReturn(invoiceDocRef).anyTimes();
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setXClassReference(getInvoiceClasses().getInvoiceClassRef(getContext(
        ).getDatabase()));
    invoiceObj.setStringValue(InvoiceClassCollection.INVOICE_CLASSES_SPACE,
        invoiceNumber);
    invoiceDoc.addXObject(invoiceObj);
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andReturn(invoiceDoc
        ).atLeastOnce();
    replayDefault();
    IInvoice invoice = invoiceStore.loadInvoiceByInvoiceNumber(invoiceNumber);
    assertNotNull(invoice);
    assertEquals(invoiceNumber, invoice.getInvoiceNumber());
    assertTrue("expecting empty invoiceItems list.", invoice.getInvoiceItems().isEmpty());
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
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setXClassReference(getInvoiceClasses().getInvoiceClassRef(getContext(
        ).getDatabase()));
    invoiceObj.setStringValue(InvoiceClassCollection.INVOICE_CLASSES_SPACE,
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
    IInvoice invoice = invoiceStore.loadInvoiceByInvoiceNumber(invoiceNumber);
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
  public void testLoadInvoiceByInvoiceNumber_deletedItemObject() throws Exception {
    String invoiceNumber = "A12758";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(invoiceServiceMock.getInvoiceDocRefForInvoiceNumber(eq(invoiceNumber))
        ).andReturn(invoiceDocRef).anyTimes();
    expect(xwiki.exists(eq(invoiceDocRef), same(context))).andReturn(true);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    BaseObject invoiceObj = new BaseObject();
    invoiceObj.setXClassReference(getInvoiceClasses().getInvoiceClassRef(getContext(
        ).getDatabase()));
    invoiceObj.setStringValue(InvoiceClassCollection.INVOICE_CLASSES_SPACE,
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
    IInvoice invoice = invoiceStore.loadInvoiceByInvoiceNumber(invoiceNumber);
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
  public void testStoreInvoice() {
    IInvoice theInvoice = new Invoice();
    replayDefault();
    invoiceStore.storeInvoice(theInvoice);
    verifyDefault();
  }


  private InvoiceClassCollection getInvoiceClasses() {
    return (InvoiceClassCollection) Utils.getComponent(IClassCollectionRole.class,
        "com.celements.invoice.classcollection");
  }

}
