package com.celements.invoice.subscription;

import java.util.Date;

import org.xwiki.component.annotation.Component;

import com.celements.invoice.builder.IInvoiceReferenceDocument;

@Component("subscription")
public class InvoiceSubscrReferenceDocument implements IInvoiceReferenceDocument {

  private Date fromDate;
  private Date toDate;
  private String subscriptionReference;

  public Date getFrom() {
    return fromDate;
  }
  
  public void setFrom(Date fromDate) {
    this.fromDate = fromDate;
  }
  
  public Date getTo() {
    return toDate;
  }
  
  public void setTo(Date toDate) {
    this.toDate = toDate;
  }

  public String getSubscriptionReference() {
    return this.subscriptionReference;
  }

  public void setSubscriptionReference(String subscriptionReference) {
    this.subscriptionReference = subscriptionReference;
  }

}
