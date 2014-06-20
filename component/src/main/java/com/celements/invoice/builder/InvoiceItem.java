package com.celements.invoice.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;

@Component
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class InvoiceItem implements IInvoiceItem {
  private String id;
  private String name;
  private int pricePerPiece;
  private int totalPrice;
  private int amount;
  private String currency;
  private String vatCode;
  private String articleNr;
  private String orderNr;
  private List<IInvoiceReferenceDocument> refDocs;

  public InvoiceItem() {
    super();
  }
  
  public InvoiceItem(IInvoiceItem item) {
    setId(item.getId());
    setName(item.getName());
    setPricePerPiece(item.getPricePerPiece());
    setTotalPrice(item.getTotalPrice());
    setAmount(item.getAmount());
    setCurrency(item.getCurrency());
    setVATCode(item.getVATCode());
    setArticleNr(item.getArticleNr());
    setOrderNr(item.getOrderNr());
    if(item.getReferenceDocs() != null) {
      for(IInvoiceReferenceDocument refDoc : item.getReferenceDocs()) {
        addInvoiceReferenceDocument(refDoc);
      }
    }
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getPricePerPiece() {
    return pricePerPiece;
  }
  
  public void setPricePerPiece(int price) {
    this.pricePerPiece = price;
  }
  
  public int getTotalPrice() {
    return totalPrice;
  }  
  
  public void setTotalPrice(int totalPrice) {
    this.totalPrice = totalPrice;
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
    return "[" + getId() + ", '" + getName() + "', " + getCurrency() + " " + getPricePerPiece() + " x " 
        + getAmount() + ", VAT: " + getVATCode() + "]";
  }

  public String getArticleNr() {
    return articleNr;
  }

  public void setArticleNr(String articleNr) {
    this.articleNr = articleNr;
  }

  public String getOrderNr() {
    return orderNr;
  }

  public void setOrderNr(String orderNr) {
    this.orderNr = orderNr;
  }

  public List<IInvoiceReferenceDocument> getReferenceDocs() {
    if(refDocs == null) {
      return Collections.emptyList();
    }
    return refDocs;
  }

  public void addInvoiceReferenceDocument(IInvoiceReferenceDocument refDoc) {
    if(refDocs == null) {
      refDocs = new ArrayList<IInvoiceReferenceDocument>();
    }
    refDocs.add(refDoc);
  }

}