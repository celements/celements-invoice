package com.celements.invoice.builder;

import java.util.List;

import org.xwiki.component.annotation.ComponentRole;

@ComponentRole
public interface IInvoice {
  public String getName();
  public void setName(String name);
  
  public int getPrice();
  public void setPrice(int price);
  
  public String getCurrency();
  public void setCurrency(String currency);
  
  public void addInvoiceItem(IInvoiceItem item);
  public List<IInvoiceItem> getInvoiceItems();
}
