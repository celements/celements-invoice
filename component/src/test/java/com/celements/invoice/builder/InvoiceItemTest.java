package com.celements.invoice.builder;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.celements.common.test.AbstractBridgedComponentTestCase;
import com.xpn.xwiki.web.Utils;

public class InvoiceItemTest extends AbstractBridgedComponentTestCase {
  private final String NAME = "item name";
  private final int PRICE_PER_PIECE = 1320;
  private final int AMOUNT = 3;
  private final String VAT_CODE = "VAT123";
  private final String ARTICLE_NR = "ArtNR123";
  private final String ORDER_NR = "OrderNR123";
  
  InvoiceItem item;
  
  @Before
  public void setUp_InvoiceItemTest() throws Exception {
    item = new InvoiceItem();
    item.setName(NAME);
    item.setPricePerPiece(PRICE_PER_PIECE);
    item.setAmount(AMOUNT);
    item.setVATCode(VAT_CODE);
    item.setArticleNr(ARTICLE_NR);
    item.setOrderNr(ORDER_NR);
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
    assertEquals(PRICE_PER_PIECE, item.getPricePerPiece());
    assertEquals(AMOUNT, item.getAmount());
    assertEquals(VAT_CODE, item.getVATCode());
    assertEquals(ARTICLE_NR, item.getArticleNr());
    assertEquals(ORDER_NR, item.getOrderNr());
  }
  
  @Test
  public void testCopyObject() {
    InvoiceItem copy = new InvoiceItem(item);
    assertEquals(NAME, copy.getName());
    assertEquals(PRICE_PER_PIECE, copy.getPricePerPiece());
    assertEquals(AMOUNT, copy.getAmount());
    assertEquals(VAT_CODE, copy.getVATCode());
    assertEquals(ARTICLE_NR, copy.getArticleNr());
    assertEquals(ORDER_NR, copy.getOrderNr());
  }
  
  @Test
  public void testCopyObject_withChanges() {
    assertEquals(NAME, item.getName());
    assertEquals(PRICE_PER_PIECE, item.getPricePerPiece());
    assertEquals(AMOUNT, item.getAmount());
    assertEquals(VAT_CODE, item.getVATCode());
    InvoiceItem copy = new InvoiceItem(item);
    assertEquals(NAME, copy.getName());
    assertEquals(PRICE_PER_PIECE, copy.getPricePerPiece());
    assertEquals(AMOUNT, copy.getAmount());
    assertEquals(VAT_CODE, copy.getVATCode());
    copy.setArticleNr("newArticleNr124");
    assertEquals(ARTICLE_NR, item.getArticleNr());
    assertEquals("newArticleNr124", copy.getArticleNr());
    copy.setOrderNr("newOrderNr12123");
    assertEquals(ORDER_NR, item.getOrderNr());
    assertEquals("newOrderNr12123", copy.getOrderNr());
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

}
