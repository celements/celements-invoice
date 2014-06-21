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

  public void setUnitPrice(int unitPrice);
  public int getUnitPrice();

  public void setUnitOfPrice(int unitOfPrice);
  public int getUnitOfPrice();
  
  public int getTotalPrice();
  public void setTotalPrice(int totalPrice);
  
  public int getAmount();
  public void setAmount(int amount);
  
  public int getVATCode();
  public void setVATCode(int vatCode);

  public String getArticleNr();
  public void setArticleNr(String articleNr);

  public String getOrderNr();
  public void setOrderNr(String orderNr);

  public List<IInvoiceReferenceDocument> getReferenceDocs();
  public void addInvoiceReferenceDocument(IInvoiceReferenceDocument refDoc);

  public float getVATValue();
  public void setVATValue(float vatValue);

  public String getUnitOfMeasure();
  public void setUnitOfMeasure(String unitOfMeasure);

}
