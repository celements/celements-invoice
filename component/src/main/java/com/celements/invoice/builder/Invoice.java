package com.celements.invoice.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;

@Component
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class Invoice implements IInvoice {
  private String name;
  private int price;
  private int totalVatFull;
  private String currency;
  List<IInvoiceItem> invoiceItems;
  private String invoiceNumber;
  private String documentNameHint;
  private List<IInvoiceReferenceDocument> refDocs;
  private String orderNr;
  private String comment;
  private Date invoiceDate;
  private IBillingAddress billingAddress;

  public String getDocumentNameHint() {
    return documentNameHint;
  }

  public void setDocumentNameHint(String documentNameHint) {
    this.documentNameHint = documentNameHint;
  }

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
    getInvoiceItemsList().add(item);
  }

  private List<IInvoiceItem> getInvoiceItemsList() {
    if(invoiceItems == null) {
      invoiceItems = new ArrayList<IInvoiceItem>();
    }
    return invoiceItems;
  }

  public void addAllInvoiceItem(Collection<IInvoiceItem> items) {
    getInvoiceItemsList().addAll(items);
  }

  public List<IInvoiceItem> getInvoiceItems() {
    List<IInvoiceItem> clone = new ArrayList<IInvoiceItem>();
    for(IInvoiceItem item : getInvoiceItemsList()) {
      clone.add(new InvoiceItem(item));
    }
    return clone;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
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

  public String getOrderNr() {
    return this.orderNr;
  }

  public void setOrderNr(String orderNr) {
    this.orderNr = orderNr;
  }

  public String getComment() {
    return this.comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Date getInvoiceDate() {
    return this.invoiceDate;
  }

  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  public IBillingAddress getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(IBillingAddress billingAddress) {
    this.billingAddress = billingAddress;
  }
  
  public int getTotalVATFull() {
    return totalVatFull;
  }

  public void setTotalVATFull(int totalVatFull) {
    this.totalVatFull = totalVatFull;
  }

}
