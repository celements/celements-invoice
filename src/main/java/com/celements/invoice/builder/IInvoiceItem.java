package com.celements.invoice.builder;

import java.util.List;

import org.xwiki.component.annotation.ComponentRole;

@ComponentRole
public interface IInvoiceItem {
  public String getId();
  public void setId(String id);
  
  public String getName();
  public void setName(String name);
  
  public int getPricePerPiece();
  public void setPricePerPiece(int price);
  
  public int getTotalPrice();
  public void setTotalPrice(int totalPrice);
  
  public float getAmount();
  public void setAmount(float amount);
  
  public String getCurrency();
  public void setCurrency(String currency);
  
  public String getVATCode();
  public void setVATCode(String vatCode);

  public String getArticleNr();
  public void setArticleNr(String articleNr);

  public String getOrderNr();
  public void setOrderNr(String orderNr);

  public List<IInvoiceReferenceDocument> getReferenceDocs();
  public void addInvoiceReferenceDocument(IInvoiceReferenceDocument refDoc);
}
