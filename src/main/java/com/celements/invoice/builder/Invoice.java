package com.celements.invoice.builder;

import java.util.ArrayList;
import java.util.List;

import org.xwiki.component.annotation.Component;

@Component
public class Invoice implements IInvoice {
  private String name;
  private int price;
  private String currency;
  List<IInvoiceItem> invoiceItems;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public void addInvoiceItem(IInvoiceItem item) {
    if(invoiceItems == null) {
      invoiceItems = new ArrayList<IInvoiceItem>();
    }
    invoiceItems.add(item);
  }
  
  public List<IInvoiceItem> getInvoiceItems() {
    List<IInvoiceItem> clone = new ArrayList<IInvoiceItem>();
    if(invoiceItems != null) {
      for(IInvoiceItem item : invoiceItems) {
        clone.add(new InvoiceItem(item));
      }
    }
    return clone;
  }

}
