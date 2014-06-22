package com.celements.invoice.store;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.easymock.Capture;
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
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.web.Utils;

public class XObjectInvoiceStore_storeInvoiceTest extends AbstractBridgedComponentTestCase {

  private XObjectInvoiceStore invoiceStore;
  private IInvoiceServiceRole invoiceServiceMock;
  private XWikiContext context;
  private XWiki xwiki;

  @Before
  public void setUp_XObjectInvoiceStoreTest_storeInvoiceTest() throws Exception {
    context = getContext();
    xwiki = getWikiMock();
    invoiceStore = (XObjectInvoiceStore) Utils.getComponent(IInvoiceStoreRole.class,
        "xobject");
    invoiceServiceMock = createMockAndAddToDefault(IInvoiceServiceRole.class);
    invoiceStore.invoiceService = invoiceServiceMock;
  }

  @Test
  public void testConvertInvoiceTo() {
    String orderNr = "OrderNumber1123";
    String invoiceNumber = "A126587";
    String invoiceSubj = "Invoice for May 2014";
    String currency = "CHF";
    String comment = "nothing to comment here";
    Date invoiceDate = new Date();
    int totalPrice = 34560;
    BaseObject invoiceObj = new BaseObject();
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    invoice.setInvoiceNumber(invoiceNumber);
    invoice.setOrderNr(orderNr);
    invoice.setName(invoiceSubj);
    invoice.setCurrency(currency);
    invoice.setComment(comment);
    invoice.setInvoiceDate(invoiceDate);
    invoice.setPrice(totalPrice);
    replayDefault();
    invoiceStore.convertInvoiceTo(invoice, invoiceObj);
    assertEquals(invoiceNumber, invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_NUMBER));
    assertEquals(orderNr, invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_ORDER_NUMBER));
    assertEquals(invoiceSubj, invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_SUBJECT));
    assertEquals(currency, invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_CURRENCY));
    assertEquals(comment, invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_COMMENT));
    assertEquals(invoiceDate, invoiceObj.getDateValue(
        InvoiceClassCollection.FIELD_INVOICE_DATE));
    assertEquals(totalPrice, invoiceObj.getIntValue(
        InvoiceClassCollection.FIELD_TOTAL_PRICE));
    verifyDefault();
  }

  @Test
  public void testConvertInvoiceItemTo() {
    int amount1 = 2;
    String articleNr1 = "ArtNr4665";
    String orderNr1 = "OrderNumber1123";
    String unitOfMeasure = "EA";
    int unitPrice1 = 34560;
    int unitOfPrice1 = 10;
    int vatCode = 2;
    float vatValue = 23.24F;
    String descr = "the item you ordered";
    int position = 3;
    IInvoiceItem invoiceItem = Utils.getComponent(IInvoiceItem.class);
    invoiceItem.setArticleNr(articleNr1);
    invoiceItem.setOrderNr(orderNr1);
    invoiceItem.setAmount(amount1);
    invoiceItem.setUnitOfMeasure(unitOfMeasure);
    invoiceItem.setUnitPrice(unitPrice1);
    invoiceItem.setUnitOfPrice(unitOfPrice1);
    invoiceItem.setVATCode(vatCode);
    invoiceItem.setVATValue(vatValue);
    invoiceItem.setName(descr);
    replayDefault();
    BaseObject invoiceItemObj = new BaseObject();
    invoiceStore.convertInvoiceItemTo(invoiceItem, invoiceItemObj, position);
    assertEquals(amount1, invoiceItemObj.getIntValue(
        InvoiceClassCollection.FIELD_AMOUNT));
    assertEquals(articleNr1, invoiceItemObj.getStringValue(
        InvoiceClassCollection.FIELD_ARTICLE_NR));
    assertEquals(orderNr1, invoiceItemObj.getStringValue(
        InvoiceClassCollection.FIELD_ORDER_NUMBER));
    assertEquals(unitPrice1, invoiceItemObj.getIntValue(
        InvoiceClassCollection.FIELD_UNIT_PRICE));
    assertEquals(unitOfPrice1, invoiceItemObj.getIntValue(
        InvoiceClassCollection.FIELD_UNIT_OF_PRICE));
    assertEquals(vatCode, invoiceItemObj.getIntValue(
        InvoiceClassCollection.FIELD_VAT_CODE));
    assertEquals(vatValue, invoiceItemObj.getFloatValue(
        InvoiceClassCollection.FIELD_VAT_VALUE), 0.001);
    assertEquals(descr, invoiceItemObj.getStringValue(
        InvoiceClassCollection.FIELD_ITEM_DESCRIPTION));
    assertEquals(position, invoiceItemObj.getIntValue(
        InvoiceClassCollection.FIELD_ITEM_POSITION));
    assertEquals(unitOfMeasure, invoiceItemObj.getStringValue(
        InvoiceClassCollection.FIELD_UNIT_OF_MEASURE));
    verifyDefault();
  }

  @Test
  public void testGetOrCreateInvoiceObject_createNewObject() throws Exception {
    String invoiceNumber = "A184984";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    DocumentReference invoiceClassRef = getInvoiceClasses().getInvoiceClassRef(
        context.getDatabase());
    BaseClass invClassMock = createMockAndAddToDefault(BaseClass.class);
    expect(xwiki.getXClass(eq(invoiceClassRef), same(context))).andReturn(invClassMock);
    expect(invClassMock.newCustomClassInstance(same(context))).andReturn(
        new BaseObject()).once();
    replayDefault();
    BaseObject invoiceObjBefore = invoiceDoc.getXObject(invoiceClassRef);
    assertNull("ensure that no invoice object exists on the invoice document"
        + " before testing", invoiceObjBefore);
    BaseObject invoiceObj = invoiceStore.getOrCreateInvoiceObject(invoiceDoc);
    assertNotNull(invoiceObj);
    BaseObject invoiceObjAfter = invoiceDoc.getXObject(invoiceClassRef);
    assertNotNull("ensure that an invoice object exists on the invoice document"
        + " after testing", invoiceObjAfter);
    verifyDefault();
  }

  @Test
  public void testGetOrCreateInvoiceObject_getExistingObject() throws Exception {
    String invoiceNumber = "A184984";
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    DocumentReference invoiceClassRef = getInvoiceClasses().getInvoiceClassRef(
        context.getDatabase());
    BaseObject invoiceObject = new BaseObject();
    invoiceObject.setXClassReference(invoiceClassRef);
    invoiceDoc.addXObject(invoiceObject);
    replayDefault();
    BaseObject invoiceObjBefore = invoiceDoc.getXObject(invoiceClassRef);
    assertNotNull("ensure that no invoice object exists on the invoice document"
        + " before testing", invoiceObjBefore);
    BaseObject invoiceObj = invoiceStore.getOrCreateInvoiceObject(invoiceDoc);
    assertNotNull(invoiceObj);
    assertSame(invoiceObject, invoiceObj);
    BaseObject invoiceObjAfter = invoiceDoc.getXObject(invoiceClassRef);
    assertNotNull("ensure that an invoice object exists on the invoice document"
        + " after testing", invoiceObjAfter);
    verifyDefault();
  }

  @Test
  public void testStoreInvoice_invoiceObj_invoiceNum() throws Exception {
    IInvoice theInvoice = new Invoice();
    String invoiceNumber = "A184984";
    theInvoice.setInvoiceNumber(invoiceNumber);
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(invoiceServiceMock.getInvoiceDocRefForInvoiceNumber(eq(invoiceNumber))
        ).andReturn(invoiceDocRef).anyTimes();
    DocumentReference invoiceClassRef = getInvoiceClasses().getInvoiceClassRef(
        context.getDatabase());
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    BaseObject invoiceObject = new BaseObject();
    invoiceObject.setXClassReference(invoiceClassRef);
    invoiceDoc.addXObject(invoiceObject);
    // do not call getDocument more than once otherwise you will get different
    // java objects.
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andReturn(invoiceDoc
        ).once();
    Capture<XWikiDocument> capturedInvoiceDoc = new Capture<XWikiDocument>();
    xwiki.saveDocument(capture(capturedInvoiceDoc), eq(""), eq(false), same(context));
    expectLastCall().once();
    replayDefault();
    invoiceStore.storeInvoice(theInvoice);
    verifyDefault();
    XWikiDocument theInvoiceDoc = capturedInvoiceDoc.getValue();
    assertEquals(invoiceDocRef, theInvoiceDoc.getDocumentReference());
    assertSame(invoiceDoc, theInvoiceDoc);
    BaseObject invoiceObj = theInvoiceDoc.getXObject(invoiceClassRef);
    assertNotNull("no invoice object found", invoiceObj);
    assertEquals(invoiceNumber, invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_NUMBER));
  }

  @Test
  public void testStoreInvoice_invoiceItemObjs() throws Exception {
    IInvoice theInvoice = new Invoice();
    String invoiceNumber = "A184984";
    theInvoice.setInvoiceNumber(invoiceNumber);
    IInvoiceItem item1 = Utils.getComponent(IInvoiceItem.class);
    item1.setOrderNr("myOrder1");
    item1.setAmount(5);
    theInvoice.addInvoiceItem(item1);
    IInvoiceItem item2 = Utils.getComponent(IInvoiceItem.class);
    item2.setOrderNr("myOrder2");
    item2.setAmount(4);
    theInvoice.addInvoiceItem(item2);
    DocumentReference invoiceDocRef = new DocumentReference(context.getDatabase(),
        "InvoicesSpace", invoiceNumber);
    expect(invoiceServiceMock.getInvoiceDocRefForInvoiceNumber(eq(invoiceNumber))
        ).andReturn(invoiceDocRef).anyTimes();
    DocumentReference invoiceClassRef = getInvoiceClasses().getInvoiceClassRef(
        context.getDatabase());
    XWikiDocument invoiceDoc = new XWikiDocument(invoiceDocRef);
    BaseObject invoiceObject = new BaseObject();
    invoiceObject.setXClassReference(invoiceClassRef);
    invoiceDoc.addXObject(invoiceObject);
    // do not call getDocument more than once otherwise you will get different
    // java objects.
    expect(xwiki.getDocument(eq(invoiceDocRef), same(context))).andReturn(invoiceDoc
        ).once();
    Capture<XWikiDocument> capturedInvoiceDoc = new Capture<XWikiDocument>();
    xwiki.saveDocument(capture(capturedInvoiceDoc), eq(""), eq(false), same(context));
    expectLastCall().once();
    DocumentReference invoiceItemClassRef = getInvoiceClasses(
        ).getInvoiceItemClassRef(context.getDatabase());
    BaseClass invItemClassMock = createMockAndAddToDefault(BaseClass.class);
    expect(xwiki.getXClass(eq(invoiceItemClassRef), same(context))).andReturn(
        invItemClassMock).atLeastOnce();
    expect(invItemClassMock.newCustomClassInstance(same(context))).andReturn(
        new BaseObject()).once(); // first item
    expect(invItemClassMock.newCustomClassInstance(same(context))).andReturn(
        new BaseObject()).once(); // second item
    replayDefault();
    invoiceStore.storeInvoice(theInvoice);
    verifyDefault();
    XWikiDocument theInvoiceDoc = capturedInvoiceDoc.getValue();
    assertEquals(invoiceDocRef, theInvoiceDoc.getDocumentReference());
    assertSame(invoiceDoc, theInvoiceDoc);
    List<BaseObject> invoiceItemObjList = invoiceDoc.getXObjects(invoiceItemClassRef);
    assertNotNull(invoiceItemObjList);
    assertEquals(2, invoiceItemObjList.size());
    BaseObject invoiceItemObj1 = invoiceItemObjList.get(0);
    assertNotNull(invoiceItemObj1);
    assertEquals(item1.getAmount(), invoiceItemObj1.getIntValue(
        InvoiceClassCollection.FIELD_AMOUNT, 0));
    BaseObject invoiceItemObj2 = invoiceItemObjList.get(1);
    assertNotNull(invoiceItemObj2);
    assertEquals(item2.getAmount(), invoiceItemObj2.getIntValue(
        InvoiceClassCollection.FIELD_AMOUNT, 0));
  }

  private InvoiceClassCollection getInvoiceClasses() {
    return (InvoiceClassCollection) Utils.getComponent(IClassCollectionRole.class,
        "com.celements.invoice.classcollection");
  }

}
