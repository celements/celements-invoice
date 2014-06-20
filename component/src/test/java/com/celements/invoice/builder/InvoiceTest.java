package com.celements.invoice.builder;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.celements.common.test.AbstractBridgedComponentTestCase;
import com.xpn.xwiki.web.Utils;

public class InvoiceTest extends AbstractBridgedComponentTestCase {
  private final String NAME = "invoice name";
  private final int PRICE = 2640;
  private final String CURRENCY = "CHF";
  
  Invoice invoice;

  @Before
  public void setUp_InvoiceTest() throws Exception {
    invoice = new Invoice();
    invoice.setName(NAME);
    invoice.setPrice(PRICE);
    invoice.setCurrency(CURRENCY);
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
    assertEquals(NAME, invoice.getName());
    assertEquals(PRICE, invoice.getPrice());
    assertEquals(CURRENCY, invoice.getCurrency());
    assertNotNull(invoice.getInvoiceItems());
    assertEquals(0, invoice.getInvoiceItems().size());
  }

  @Test
  public void testAddInvoiceItem() {
    String itemName = "Item Name";
    InvoiceItem item = new InvoiceItem();
    item.setName(itemName);
    invoice.addInvoiceItem(item);
    assertNotNull(invoice.getInvoiceItems());
    assertEquals(1, invoice.getInvoiceItems().size());
    assertEquals(itemName, invoice.getInvoiceItems().get(0).getName());
  }

  @Test
  public void testGetInvoiceItems() {
    String itemName1 = "Item Name 1";
    String itemName2 = "Item Name 2";
    String itemName3 = "Item Name 3";
    InvoiceItem item = new InvoiceItem();
    item.setName(itemName1);
    invoice.addInvoiceItem(item);
    item = new InvoiceItem();
    item.setName(itemName2);
    invoice.addInvoiceItem(item);
    item = new InvoiceItem();
    item.setName(itemName3);
    invoice.addInvoiceItem(item);
    assertNotNull(invoice.getInvoiceItems());
    assertEquals(3, invoice.getInvoiceItems().size());
    assertEquals(itemName1, invoice.getInvoiceItems().get(0).getName());
    assertEquals(itemName2, invoice.getInvoiceItems().get(1).getName());
    assertEquals(itemName3, invoice.getInvoiceItems().get(2).getName());
  }
  
  @Test
  public void testGetInvoiceItems_withChanges() {
    String itemName1 = "Item Name 1";
    String itemName2 = "Item Name 2";
    InvoiceItem item = new InvoiceItem();
    item.setName(itemName1);
    invoice.addInvoiceItem(item);
    List<IInvoiceItem> items = invoice.getInvoiceItems();
    assertNotNull(items);
    assertEquals(1, items.size());
    assertEquals(itemName1, items.get(0).getName());
    items.get(0).setName(itemName2);
    assertEquals(itemName1, invoice.getInvoiceItems().get(0).getName());
    assertEquals(itemName2, items.get(0).getName());
  }

}
