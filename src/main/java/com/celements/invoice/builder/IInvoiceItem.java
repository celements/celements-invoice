package com.celements.invoice.builder;

import org.xwiki.component.annotation.ComponentRole;

@ComponentRole
public interface IInvoiceItem {
  public String getId();
  public void setId(String id);
  
  public String getName();
  public void setName(String name);
  
  public int getPricePerPiece();
  public void setPricePerPiece(int price);
  
  public int getAmount();
  public void setAmount(int amount);
  
  public String getCurrency();
  public void setCurrency(String currency);
  
  public String getVATCode();
  public void setVATCode(String vatCode);
}
