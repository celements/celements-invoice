package com.celements.invoice.builder;

import java.util.List;

import org.xwiki.component.annotation.ComponentRole;

@ComponentRole
public interface IInvoiceItem {

  public String getId();
  public void setId(String id);

  public String getName();
  public void setName(String name);

  public void setUnitPrice(int unitPrice);
  public int getUnitPrice();

  public void setUnitOfPrice(float unitOfPrice);
  public float getUnitOfPrice();

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

  public int getTotalPrice();

}
