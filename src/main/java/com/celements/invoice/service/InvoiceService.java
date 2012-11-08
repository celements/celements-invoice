/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.celements.invoice.service;

import groovy.lang.Singleton;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

import com.celements.invoice.InvoiceClassCollection;
import com.celements.web.service.IWebUtilsService;
import com.xpn.xwiki.XWikiContext;

@Component
@Singleton
public class InvoiceService implements IInvoiceServiceRole {

  private static Log LOGGER = LogFactory.getFactory().getInstance(InvoiceService.class);

  @Requirement
  QueryManager query;

  @Requirement
  Execution execution;

  @Requirement
  IWebUtilsService webUtilsService;

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

  private String getMaxInvoiceNumberHQL() {
    return "select max(invoice.invoiceNumber) from "
      + InvoiceClassCollection.INVOICE_CLASSES_SPACE + "."
      + InvoiceClassCollection.INVOICE_CLASS_DOC + " as invoice";
  }

  private Integer getLatestInvoiceNumberFromDb() {
    try {
      List<String> result = query.createQuery(getMaxInvoiceNumberHQL(), Query.HQL
          ).execute();
      if (!result.isEmpty()) {
        String maxInvoiceNumberStr = result.get(0);
        try {
          return Integer.parseInt(maxInvoiceNumberStr);
        } catch (NumberFormatException nFE) {
          LOGGER.info("Failed to parse max invoice number [" + maxInvoiceNumberStr
              + "]. Counting down instead.");
          return getHighestInvoiceNumberByCountingDown();
        }
      }
    } catch (QueryException exp) {
      LOGGER.error("Failed to get latest invoice number from db.", exp);
    }
    return null;
  }

  private Integer getHighestInvoiceNumberByCountingDown() throws QueryException {
    List<String> resultDesc = query.createQuery(getInvoiceNumbersDescHQL(), Query.HQL
        ).execute();
    for (String invoiceNumberStr : resultDesc) {
      try {
        return Integer.parseInt(invoiceNumberStr);
      } catch (NumberFormatException nFE) {
        LOGGER.debug("Failed to parse invoice number [" + invoiceNumberStr
            + "]. Skipping");
      }
    }
    LOGGER.info("now invoice number found by counting down.");
    return null;
  }

  private String getInvoiceNumbersDescHQL() {
    return "select invoice.invoiceNumber from "
      + InvoiceClassCollection.INVOICE_CLASSES_SPACE + "."
      + InvoiceClassCollection.INVOICE_CLASS_DOC + " as invoice"
      + " order by length(invoice.invoiceNumber) desc, invoice.invoiceNumber desc";
  }

  public DocumentReference getInvoiceDocRefForInvoiceNumber(String invoiceNumber) {
    DocumentReference invoiceDocRef = null;
    try {
      Query theQuery = query.createQuery(getInvoiceForInvoiceNumberXWQL(), Query.XWQL);
      theQuery.bindValue("invoiceNumber", invoiceNumber);
      List<Object> result = theQuery.execute();
      if (result.size() > 0) {
        return webUtilsService.resolveDocumentReference(result.get(0).toString());
      }
    } catch (QueryException queryExp) {
      LOGGER.error("Failed to execute getInvoiceForInvoiceNumber query ["
          + invoiceNumber + "].", queryExp);
    }
    return invoiceDocRef;
  }

  private String getInvoiceForInvoiceNumberXWQL() {
    return "from doc.object(" + InvoiceClassCollection.INVOICE_CLASSES_SPACE + "."
      + InvoiceClassCollection.INVOICE_CLASS_DOC + ") as invoice"
      + " where invoice.invoiceNumber = :invoiceNumber";
  }

}
