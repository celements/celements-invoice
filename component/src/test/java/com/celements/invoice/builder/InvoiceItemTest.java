package com.celements.invoice.builder;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.celements.common.test.AbstractBridgedComponentTestCase;
import com.xpn.xwiki.web.Utils;

public class InvoiceItemTest extends AbstractBridgedComponentTestCase {
  private final String NAME = "item name";
  private final int AMOUNT = 3;
  private final int VAT_CODE = 2;
  private final String ARTICLE_NR = "ArtNR123";
  private final String ORDER_NR = "OrderNR123";
  private final float VAT_VALUE = 8f;
  public static final int UNIT_PRICE = 10000;
  public static final int UNIT_OF_PRICE = 13;
  public static final String UNIT_OF_MEASURE = "EA";
  
  IInvoiceItem item;
  
  @Before
  public void setUp_InvoiceItemTest() throws Exception {
    item = Utils.getComponent(IInvoiceItem.class);
    item.setName(NAME);
    item.setAmount(AMOUNT);
    item.setVATCode(VAT_CODE);
    item.setArticleNr(ARTICLE_NR);
    item.setOrderNr(ORDER_NR);
    item.setVATValue(VAT_VALUE);
    item.setUnitPrice(UNIT_PRICE);
    item.setUnitOfPrice(UNIT_OF_PRICE);
    item.setUnitOfMeasure(UNIT_OF_MEASURE);
  }

  @Test
  public void testNotSingleton() {
    IInvoice firstInvoice = Utils.getComponent(IInvoice.class);
    IInvoice secondInvoice = Utils.getComponent(IInvoice.class);
    replayDefault();
    assertNotSame("Instanciation Strategy must be PER_LOOKUP", firstInvoice,
        secondInvoice);
    verifyDefault();
  }

  @Test
  public void testBuildObject() {
    assertEquals(NAME, item.getName());
    assertEquals(AMOUNT, item.getAmount());
    assertEquals(VAT_CODE, item.getVATCode());
    assertEquals(ARTICLE_NR, item.getArticleNr());
    assertEquals(ORDER_NR, item.getOrderNr());
  }
  
  @Test
  public void testCopyObject() {
    InvoiceItem copy = new InvoiceItem(item);
    assertEquals(NAME, copy.getName());
    assertEquals(AMOUNT, copy.getAmount());
    assertEquals(VAT_CODE, copy.getVATCode());
    assertEquals(ARTICLE_NR, copy.getArticleNr());
    assertEquals(ORDER_NR, copy.getOrderNr());
  }
  
  @Test
  public void testCopyObject_withChanges() {
    assertEquals(NAME, item.getName());
    assertEquals(AMOUNT, item.getAmount());
    assertEquals(VAT_CODE, item.getVATCode());
    InvoiceItem copy = new InvoiceItem(item);
    assertEquals(NAME, copy.getName());
    assertEquals(AMOUNT, copy.getAmount());
    assertEquals(VAT_CODE, copy.getVATCode());
    copy.setArticleNr("newArticleNr124");
    assertEquals(ARTICLE_NR, item.getArticleNr());
    assertEquals("newArticleNr124", copy.getArticleNr());
    copy.setOrderNr("newOrderNr12123");
    assertEquals(ORDER_NR, item.getOrderNr());
    assertEquals("newOrderNr12123", copy.getOrderNr());
    copy.setVATValue(6f);
    assertTrue(VAT_VALUE - item.getVATValue() < 0.00001);
    assertTrue(6f - item.getVATValue() < 0.00001);
    copy.setUnitPrice(3);
    assertEquals(UNIT_PRICE, item.getUnitPrice());
    assertEquals(3, copy.getUnitPrice());
    copy.setUnitOfPrice(5);
    assertEquals(UNIT_OF_PRICE, item.getUnitOfPrice(), 0.00001);
    assertEquals(5, copy.getUnitOfPrice(), 0.00001);
    copy.setUnitOfMeasure("AB");
    assertEquals(UNIT_OF_MEASURE, item.getUnitOfMeasure());
    assertEquals("AB", copy.getUnitOfMeasure());
  }

  @Test
  public void testSetGetArticleNr() {
    IInvoiceItem invoiceItem = Utils.getComponent(IInvoiceItem.class);
    String articleNr = "Article123";
    invoiceItem.setArticleNr(articleNr);
    assertEquals(articleNr, invoiceItem.getArticleNr());
  }

  @Test
  public void testSetGetOrderNr() {
    IInvoiceItem invoiceItem = Utils.getComponent(IInvoiceItem.class);
    String orderNr = "OrderNr123";
    invoiceItem.setOrderNr(orderNr);
    assertEquals(orderNr, invoiceItem.getOrderNr());
  }

  @Test
  public void testGetTotalPrice() {
    assertEquals(UNIT_PRICE * AMOUNT, item.getTotalPrice());
  }

}
