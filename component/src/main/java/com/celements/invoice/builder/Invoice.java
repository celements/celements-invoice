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
  private int totalVatFree;
  private int totalVatReduced;
  private EInvoiceStatus status = EInvoiceStatus.isNew;
  private boolean cancelled = false;

  @Override
  public String getDocumentNameHint() {
    return documentNameHint;
  }

  @Override
  public void setDocumentNameHint(String documentNameHint) {
    this.documentNameHint = documentNameHint;
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
  public int getPrice() {
    return price;
  }

  @Override
  public void setPrice(int price) {
    this.price = price;
  }

  @Override
  public String getCurrency() {
    return currency;
  }

  @Override
  public void setCurrency(String currency) {
    this.currency = currency;
  }

  @Override
  public void addInvoiceItem(IInvoiceItem item) {
    getInvoiceItemsList().add(item);
  }

  private List<IInvoiceItem> getInvoiceItemsList() {
    if (invoiceItems == null) {
      invoiceItems = new ArrayList<IInvoiceItem>();
    }
    return invoiceItems;
  }

  @Override
  public void addAllInvoiceItem(Collection<IInvoiceItem> items) {
    getInvoiceItemsList().addAll(items);
  }

  @Override
  public List<IInvoiceItem> getInvoiceItems() {
    List<IInvoiceItem> clone = new ArrayList<IInvoiceItem>();
    for (IInvoiceItem item : getInvoiceItemsList()) {
      clone.add(new InvoiceItem(item));
    }
    return clone;
  }

  @Override
  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  @Override
  public String getInvoiceNumber() {
    return invoiceNumber;
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
  public String getOrderNr() {
    return this.orderNr;
  }

  @Override
  public void setOrderNr(String orderNr) {
    this.orderNr = orderNr;
  }

  @Override
  public String getComment() {
    return this.comment;
  }

  @Override
  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public Date getInvoiceDate() {
    return this.invoiceDate;
  }

  @Override
  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }

  @Override
  public IBillingAddress getBillingAddress() {
    return billingAddress;
  }

  @Override
  public void setBillingAddress(IBillingAddress billingAddress) {
    this.billingAddress = billingAddress;
  }

  @Override
  public int getTotalVATFull() {
    return totalVatFull;
  }

  @Override
  public void setTotalVATFull(int totalVatFull) {
    this.totalVatFull = totalVatFull;
  }

  @Override
  public int getTotalVATFree() {
    return this.totalVatFree;
  }

  @Override
  public void setTotalVATFree(int totalVatFree) {
    this.totalVatFree = totalVatFree;
  }

  @Override
  public int getTotalVATReduced() {
    return this.totalVatReduced;
  }

  @Override
  public void setTotalVATReduced(int totalVATReduced) {
    this.totalVatReduced = totalVATReduced;
  }

  @Override
  public EInvoiceStatus getStatus() {
    return this.status;
  }

  @Override
  public void setStatus(EInvoiceStatus status) {
    this.status = status;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean isCancelled) {
    this.cancelled = isCancelled;
  }

}
