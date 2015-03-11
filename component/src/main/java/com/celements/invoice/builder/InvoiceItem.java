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
  private int vatCode;
  private String articleNr;
  private String orderNr;
  private List<IInvoiceReferenceDocument> refDocs;
  private float vatValue;
  private int unitPrice;
  private float unitOfPrice;
  private String unitOfMeasure;

  public InvoiceItem() {
    super();
  }

  public InvoiceItem(IInvoiceItem item) {
    setId(item.getId());
    setName(item.getName());
    setPricePerPiece(item.getPricePerPiece());
    setTotalPrice(item.getTotalPrice());
    setAmount(item.getAmount());
    setVATCode(item.getVATCode());
    setArticleNr(item.getArticleNr());
    setOrderNr(item.getOrderNr());
    if (item.getReferenceDocs() != null) {
      for (IInvoiceReferenceDocument refDoc : item.getReferenceDocs()) {
        addInvoiceReferenceDocument(refDoc);
      }
    }
    setVATValue(item.getVATValue());
    setUnitPrice(item.getUnitPrice());
    setUnitOfPrice(item.getUnitOfPrice());
    setUnitOfMeasure(item.getUnitOfMeasure());
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int getPricePerPiece() {
    return pricePerPiece;
  }

  @Override
  public void setPricePerPiece(int price) {
    this.pricePerPiece = price;
  }

  @Override
  public int getTotalPrice() {
    return totalPrice;
  }

  @Override
  public void setTotalPrice(int totalPrice) {
    this.totalPrice = totalPrice;
  }

  @Override
  public int getAmount() {
    return amount;
  }

  @Override
  public void setAmount(int amount) {
    this.amount = amount;
  }

  @Override
  public int getVATCode() {
    return vatCode;
  }

  @Override
  public void setVATCode(int vatCode) {
    this.vatCode = vatCode;
  }

  @Override
  public String toString() {
    return "[" + getId() + ", '" + getName() + "', " + getPricePerPiece() + " x "
        + getAmount() + ", VAT: " + getVATCode() + "]";
  }

  @Override
  public String getArticleNr() {
    return articleNr;
  }

  @Override
  public void setArticleNr(String articleNr) {
    this.articleNr = articleNr;
  }

  @Override
  public String getOrderNr() {
    return orderNr;
  }

  @Override
  public void setOrderNr(String orderNr) {
    this.orderNr = orderNr;
  }

  @Override
  public List<IInvoiceReferenceDocument> getReferenceDocs() {
    if (refDocs == null) {
      return Collections.emptyList();
    }
    return refDocs;
  }

  @Override
  public void addInvoiceReferenceDocument(IInvoiceReferenceDocument refDoc) {
    if (refDocs == null) {
      refDocs = new ArrayList<IInvoiceReferenceDocument>();
    }
    refDocs.add(refDoc);
  }

  @Override
  public float getVATValue() {
    return this.vatValue;
  }

  @Override
  public void setVATValue(float vatValue) {
    this.vatValue = vatValue;
  }

  @Override
  public int getUnitPrice() {
    return this.unitPrice;
  }

  @Override
  public void setUnitPrice(int unitPrice) {
    this.unitPrice = unitPrice;
  }

  @Override
  public float getUnitOfPrice() {
    return this.unitOfPrice;
  }

  @Override
  public void setUnitOfPrice(float unitOfPrice) {
    this.unitOfPrice = unitOfPrice;
  }

  @Override
  public String getUnitOfMeasure() {
    return this.unitOfMeasure;
  }

  @Override
  public void setUnitOfMeasure(String unitOfMeasure) {
    this.unitOfMeasure = unitOfMeasure;
  }

}
