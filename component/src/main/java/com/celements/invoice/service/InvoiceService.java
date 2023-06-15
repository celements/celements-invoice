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

import java.util.List;

import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.IInvoice;
import com.celements.nextfreedoc.INextFreeDocRole;
import com.celements.web.service.IWebUtilsService;
import com.xpn.xwiki.XWikiContext;

@Singleton
@Component("xobject")
public class InvoiceService implements IInvoiceServiceRole {

  public static final String XWIKICFG_MIN_INVOICE_NUMBER = "com.celements.invoice.minInvoiceNumber";
  public static final String PREF_MIN_INVOICE_NUMBER = "minInvoiceNumber";

  private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);

  @Requirement
  private QueryManager queryManager;

  @Requirement
  private INextFreeDocRole nextFreeDocService;

  @Requirement
  private IWebUtilsService webUtilsService;

  @Requirement
  private Execution execution;

  private XWikiContext getContext() {
    return (XWikiContext) execution.getContext().getProperty("xwikicontext");
  }

  @Override
  synchronized public String getNewInvoiceNumber() {
    int latestInvoiceNumber = getLatestInvoiceNumber() + 1;
    return Integer.toString(latestInvoiceNumber);
  }

  private int getLatestInvoiceNumber() {
    Integer latestInvoiceNumberFromDb = getLatestInvoiceNumberFromDb();
    LOGGER.trace("getLatestInvoiceNumber got [" + latestInvoiceNumberFromDb + "].");
    if (!isValidInvoiceNumber(latestInvoiceNumberFromDb)) {
      int minInvoiceNumberFromConfig = getMinInvoiceNumberFromConfig();
      LOGGER.debug("getLatestInvoiceNumber invalid invoice number found ["
          + latestInvoiceNumberFromDb + "] returning min from config [" + minInvoiceNumberFromConfig
          + "].");
      return minInvoiceNumberFromConfig;
    } else {
      LOGGER.debug("getLatestInvoiceNumber returning [" + latestInvoiceNumberFromDb + "].");
      return latestInvoiceNumberFromDb;
    }
  }

  private int getMinInvoiceNumberFromConfig() {
    return getContext().getWiki().getXWikiPreferenceAsInt(PREF_MIN_INVOICE_NUMBER,
        XWIKICFG_MIN_INVOICE_NUMBER, 1, getContext());
  }

  private boolean isValidInvoiceNumber(Integer latestInvoiceNumberFromDb) {
    return (latestInvoiceNumberFromDb != null)
        && (latestInvoiceNumberFromDb >= getMinInvoiceNumberFromConfig());
  }

  private String getMaxInvoiceNumberHQL() {
    return "select max(invoice.invoiceNumber) from " + InvoiceClassCollection.INVOICE_CLASSES_SPACE
        + "." + InvoiceClassCollection.INVOICE_CLASS_DOC + " as invoice";
  }

  private Integer getLatestInvoiceNumberFromDb() {
    LOGGER.trace("getLatestInvoiceNumberFromDb: looking for latest invoice number in db.");
    try {
      List<String> result = queryManager.createQuery(getMaxInvoiceNumberHQL(), Query.HQL).execute();
      if (!result.isEmpty()) {
        String maxInvoiceNumberStr = result.get(0);
        try {
          int latestInvoiceNumberFromDb = Integer.parseInt(maxInvoiceNumberStr);
          if (isValidInvoiceNumber(latestInvoiceNumberFromDb)) {
            return latestInvoiceNumberFromDb;
          }
          LOGGER.trace("getLatestInvoiceNumberFromDb: no valid invoice number found ["
              + latestInvoiceNumberFromDb + "].");
        } catch (NumberFormatException nFE) {
          LOGGER.info("Failed to parse max invoice number [" + maxInvoiceNumberStr
              + "]. Counting down instead.");
        }
        return getHighestInvoiceNumberByCountingDown();
      }
    } catch (QueryException exp) {
      LOGGER.error("Failed to get latest invoice number from db.", exp);
    }
    return null;
  }

  private Integer getHighestInvoiceNumberByCountingDown() throws QueryException {
    LOGGER.trace("getHighestInvoiceNumberByCountingDown: counting down.");
    List<String> resultDesc = queryManager.createQuery(getInvoiceNumbersDescHQL(),
        Query.HQL).execute();
    for (String invoiceNumberStr : resultDesc) {
      try {
        int highestInvoiceNumber = Integer.parseInt(invoiceNumberStr);
        if (isValidInvoiceNumber(highestInvoiceNumber)) {
          LOGGER.info("getHighestInvoiceNumberByCountingDown: invoice number found by"
              + " counting [" + highestInvoiceNumber + "]");
          return highestInvoiceNumber;
        } else {
          LOGGER.trace("getHighestInvoiceNumberByCountingDown: skip [" + highestInvoiceNumber
              + "]");
        }
      } catch (NumberFormatException nFE) {
        LOGGER.debug("Failed to parse invoice number [" + invoiceNumberStr + "]. Skipping");
      }
    }
    LOGGER.info("no invoice number found by counting down.");
    return null;
  }

  private String getInvoiceNumbersDescHQL() {
    return "select invoice.invoiceNumber from " + InvoiceClassCollection.INVOICE_CLASSES_SPACE + "."
        + InvoiceClassCollection.INVOICE_CLASS_DOC + " as invoice"
        + " order by length(invoice.invoiceNumber) desc, invoice.invoiceNumber desc";
  }

  @Override
  public DocumentReference getInvoiceDocRefForInvoiceNumber(String invoiceNumber) {
    DocumentReference invoiceDocRef = null;
    try {
      LOGGER.debug("getInvoiceDocRefForInvoiceNumber for [" + invoiceNumber + "].");
      String docForInvoiceNumberXWQL = getInvoiceForInvoiceNumberXWQL();
      LOGGER.trace("getInvoiceDocRefForInvoiceNumber: " + docForInvoiceNumberXWQL);
      Query theQuery = queryManager.createQuery(docForInvoiceNumberXWQL, Query.XWQL);
      theQuery.bindValue("invoiceNumber", invoiceNumber);
      List<Object> result = theQuery.execute();
      if (result.size() > 0) {
        invoiceDocRef = webUtilsService.resolveDocumentReference(result.get(0).toString());
      }
    } catch (QueryException queryExp) {
      LOGGER.error("Failed to execute getInvoiceForInvoiceNumber query [" + invoiceNumber + "].",
          queryExp);
    }
    LOGGER.info("getInvoiceDocRefForInvoiceNumber for [" + invoiceNumber + "] returning ["
        + invoiceDocRef + "].");
    return invoiceDocRef;
  }

  private String getInvoiceForInvoiceNumberXWQL() {
    return "from doc.object(" + InvoiceClassCollection.INVOICE_CLASSES_SPACE + "."
        + InvoiceClassCollection.INVOICE_CLASS_DOC + ") as invoice"
        + " where invoice.invoiceNumber = :invoiceNumber";
  }

  @Override
  public DocumentReference getNewInvoiceDocRef(IInvoice invoice) {
    String spaceName = "Invoices";
    if (!StringUtils.isBlank(invoice.getDocumentNameHint())) {
      spaceName = invoice.getDocumentNameHint() + "-" + spaceName;
    }
    return nextFreeDocService.getNextTitledPageDocRef(webUtilsService.resolveSpaceReference(
        spaceName), "Invoice");
  }

}
