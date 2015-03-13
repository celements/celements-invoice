package com.celements.invoice.subscription;

import java.util.Date;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;

import com.celements.invoice.builder.IInvoiceReferenceDocument;

@Component(InvoiceSubscrReferenceDocument.NAME)
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class InvoiceSubscrReferenceDocument implements IInvoiceReferenceDocument {
  
  public static final String NAME = "subscription";

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
