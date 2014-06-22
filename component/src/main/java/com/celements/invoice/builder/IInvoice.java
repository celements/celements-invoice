package com.celements.invoice.builder;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.xwiki.component.annotation.ComponentRole;

@ComponentRole
public interface IInvoice {
  public String getDocumentNameHint();
  public void setDocumentNameHint(String documentNameHint);

  public String getName();
  public void setName(String name);

  public int getPrice();
  public void setPrice(int price);
  
  public String getCurrency();
  public void setCurrency(String currency);
  
  public List<IInvoiceItem> getInvoiceItems();
  public void addInvoiceItem(IInvoiceItem item);
  public void addAllInvoiceItem(Collection<IInvoiceItem> items);

  public List<IInvoiceReferenceDocument> getReferenceDocs();
  public void addInvoiceReferenceDocument(IInvoiceReferenceDocument refDoc);

  public String getInvoiceNumber();
  public void setInvoiceNumber(String invoiceNumber);

  public String getOrderNr();
  public void setOrderNr(String orderNr);

  public String getComment();
  public void setComment(String comment);

  public Date getInvoiceDate();
  public void setInvoiceDate(Date invoiceDate);

  public IBillingAddress getBillingAddress();
  public void setBillingAddress(IBillingAddress billingAddress);
}
