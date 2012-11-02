package com.celements.invoice.service;

import groovy.lang.Singleton;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

import com.celements.invoice.InvoiceClassCollection;
import com.xpn.xwiki.XWikiContext;

@Component
@Singleton
public class InvoiceService implements IInvoiceServiceRole {

  private static Log LOGGER = LogFactory.getFactory().getInstance(InvoiceService.class);

  @Requirement
  QueryManager query;

  @Requirement
  Execution execution;

  private XWikiContext getContext() {
    return (XWikiContext)execution.getContext().getProperty("xwikicontext");
  }

  synchronized public String getNewInvoiceNumber() {
    int latestInvoiceNumber = getLatestInvoiceNumber() + 1;
    return Integer.toString(latestInvoiceNumber);
  }

  private int getLatestInvoiceNumber() {
    Integer latestInvoiceNumberFromDb = getLatestInvoiceNumberFromDb();
    int minInvoiceNumberFromConfig = getContext().getWiki().getXWikiPreferenceAsInt(
        "minInvoiceNumber", "com.celements.invoice.minInvoiceNumber", 1, getContext());
    if ((latestInvoiceNumberFromDb == null)
        || (latestInvoiceNumberFromDb < minInvoiceNumberFromConfig)) {
      return minInvoiceNumberFromConfig;
    } else {
      return latestInvoiceNumberFromDb;
    }
  }

  private String getLatestInvoiceNumberXWQL() {
    return "select max(invoice.invoiceNumber) from "
      + InvoiceClassCollection.INVOICE_CLASSES_SPACE + "."
      + InvoiceClassCollection.INVOICE_CLASS_DOC;
  }

  private Integer getLatestInvoiceNumberFromDb() {
    try {
      List<Integer> result = query.createQuery(getLatestInvoiceNumberXWQL(), Query.XWQL
          ).execute();
      if (!result.isEmpty()) {
        return result.get(0);
      }
    } catch (QueryException exp) {
      LOGGER.error("Failed to get latest invoice number from db.", exp);
    }
    return null;
  }

}
