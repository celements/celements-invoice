package com.celements.invoice.builder;

import org.xwiki.component.annotation.Component;

@Component
public class InvoiceItem implements IInvoiceItem {
  private String name;
  private int price;
  private int amount;
  private String currency;
  private String vatCode;


  public InvoiceItem() {
    super();
  }
  
  public InvoiceItem(IInvoiceItem item) {
    setName(item.getName());
    setPricePerPiece(item.getPricePerPiece());
    setAmount(item.getAmount());
    setCurrency(item.getCurrency());
    setVATCode(item.getVATCode());
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getPricePerPiece() {
    return price;
  }
  
  public void setPricePerPiece(int price) {
    this.price = price;
  }
  
  public int getAmount() {
    return amount;
  }
  
  public void setAmount(int amount) {
    this.amount = amount;
  }
  
  public String getCurrency() {
    return currency;
  }
  
  public void setCurrency(String currency) {
    this.currency = currency;
  }
  
  public String getVATCode() {
    return vatCode;
  }
  
  public void setVATCode(String vatCode) {
    this.vatCode = vatCode;
  }
  
  @Override
  public String toString() {
    return "[" + getName() + ", " + getCurrency() + " " + getPricePerPiece() + " x " 
        + getAmount() + ", VAT: " + getVATCode() + "]";
  }
}
