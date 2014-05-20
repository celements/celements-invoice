package com.celements.invoice.builder;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class InvoiceItemTest {
  private final String NAME = "item name";
  private final int PRICE_PER_PIECE = 1320;
  private final int AMOUNT = 3;
  private final String CURRENCY = "CHF";
  private final String VAT_CODE = "VAT123";
  
  InvoiceItem item;
  
  @Before
  public void setUp() throws Exception {
    item = new InvoiceItem();
    item.setName(NAME);
    item.setPricePerPiece(PRICE_PER_PIECE);
    item.setAmount(AMOUNT);
    item.setCurrency(CURRENCY);
    item.setVATCode(VAT_CODE);
  }

  @Test
  public void testBuildObject() {
    assertEquals(NAME, item.getName());
    assertEquals(PRICE_PER_PIECE, item.getPricePerPiece());
    assertEquals(AMOUNT, item.getAmount());
    assertEquals(CURRENCY, item.getCurrency());
    assertEquals(VAT_CODE, item.getVATCode());
  }
  
  @Test
  public void testCopyObject() {
    InvoiceItem copy = new InvoiceItem(item);
    assertEquals(NAME, copy.getName());
    assertEquals(PRICE_PER_PIECE, copy.getPricePerPiece());
    assertEquals(AMOUNT, copy.getAmount());
    assertEquals(CURRENCY, copy.getCurrency());
    assertEquals(VAT_CODE, copy.getVATCode());
  }
  
  @Test
  public void testCopyObject_withChanges() {
    assertEquals(NAME, item.getName());
    assertEquals(PRICE_PER_PIECE, item.getPricePerPiece());
    assertEquals(AMOUNT, item.getAmount());
    assertEquals(CURRENCY, item.getCurrency());
    assertEquals(VAT_CODE, item.getVATCode());
    InvoiceItem copy = new InvoiceItem(item);
    assertEquals(NAME, copy.getName());
    assertEquals(PRICE_PER_PIECE, copy.getPricePerPiece());
    assertEquals(AMOUNT, copy.getAmount());
    assertEquals(CURRENCY, copy.getCurrency());
    assertEquals(VAT_CODE, copy.getVATCode());
    copy.setCurrency("EUR");
    assertEquals(CURRENCY, item.getCurrency());
    assertEquals("EUR", copy.getCurrency());
  }
}
