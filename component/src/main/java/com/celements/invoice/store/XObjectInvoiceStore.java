package com.celements.invoice.store;

import groovy.lang.Singleton;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.invoice.InvoiceClassCollection;
import com.celements.invoice.builder.EInvoiceStatus;
import com.celements.invoice.builder.IInvoice;
import com.celements.invoice.builder.IInvoiceItem;
import com.celements.invoice.service.IInvoiceServiceRole;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.web.Utils;

@Component("xobject")
@Singleton
public class XObjectInvoiceStore implements IInvoiceStoreRole {

  private static Log LOGGER = LogFactory.getFactory().getInstance(
      XObjectInvoiceStore.class);

  @Requirement("xobject")
  IInvoiceServiceRole invoiceService;

  @Requirement("com.celements.invoice.classcollection")
  IClassCollectionRole invoiceClasses;

  @Requirement(role = IInvoiceStoreExtenderRole.class)
  Map<String, IInvoiceStoreExtenderRole> storeExtenderMap;

  @Requirement
  Execution execution;

  private XWikiContext getContext() {
    return (XWikiContext)execution.getContext().getProperty("xwikicontext");
  }

  private InvoiceClassCollection getInvoiceClasses() {
    return (InvoiceClassCollection) invoiceClasses;
  }

  public IInvoice loadInvoiceByInvoiceNumber(String invoiceNumber) {
    DocumentReference invoiceDocRef = invoiceService.getInvoiceDocRefForInvoiceNumber(
        invoiceNumber);
    if (getContext().getWiki().exists(invoiceDocRef, getContext())) {
      try {
        XWikiDocument invoiceDoc = getContext().getWiki().getDocument(invoiceDocRef,
            getContext());
        BaseObject invoiceObj = invoiceDoc.getXObject(getInvoiceClasses(
            ).getInvoiceClassRef(getWikiName()));
        if (invoiceObj != null) {
          IInvoice invoice = convertToInvoice(invoiceObj);
          callLoadInvoiceExtender(invoiceNumber, invoiceDoc, invoice); 
          List<BaseObject> invoiceItemObjList = invoiceDoc.getXObjects(getInvoiceClasses(
              ).getInvoiceItemClassRef(getWikiName()));
          if (invoiceItemObjList != null) {
            SortedMap<Integer, IInvoiceItem> itemMap =
                new TreeMap<Integer, IInvoiceItem>();
            for(BaseObject invoiceItemObj : invoiceItemObjList) {
              if (invoiceItemObj != null) {
                int position = invoiceItemObj.getIntValue(
                    InvoiceClassCollection.FIELD_ITEM_POSITION);
                while (itemMap.containsKey(position)) {
                  position++;
                }
                itemMap.put(position, convertToInvoiceItem(invoiceItemObj));
              }
            }
            invoice.addAllInvoiceItem(itemMap.values());
          }
          return invoice;
        }
      } catch (XWikiException exp) {
        LOGGER.error("Failed to load invoice document [" + invoiceDocRef + "].", exp);
      }
    }
    return null;
  }

  private void callLoadInvoiceExtender(String invoiceNumber, XWikiDocument invoiceDoc,
      IInvoice invoice) {
    for (IInvoiceStoreExtenderRole storeExtender : storeExtenderMap.values()) {
      try {
        storeExtender.loadInvoice(invoiceDoc, invoice);
      } catch (Exception exp) {
        LOGGER.error("IInvoiceStoreExtender [" + storeExtender.getClass()
            + "] failed to load invoice [" + invoiceNumber + "].", exp);
      }
    }
  }

  IInvoice convertToInvoice(BaseObject invoiceObj) {
    IInvoice invoice = Utils.getComponent(IInvoice.class);
    invoice.setInvoiceNumber(invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_NUMBER));
    invoice.setOrderNr(invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_ORDER_NUMBER));
    invoice.setName(invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_SUBJECT));
    invoice.setCurrency(invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_CURRENCY));
    invoice.setComment(invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_COMMENT));
    invoice.setInvoiceDate(invoiceObj.getDateValue(
        InvoiceClassCollection.FIELD_INVOICE_DATE));
    invoice.setPrice(invoiceObj.getIntValue(InvoiceClassCollection.FIELD_TOTAL_PRICE));
    invoice.setTotalVATFree(invoiceObj.getIntValue(
        InvoiceClassCollection.FIELD_TOTAL_VAT_FREE));
    invoice.setTotalVATReduced(invoiceObj.getIntValue(
        InvoiceClassCollection.FIELD_TOTAL_VAT_REDUCED));
    invoice.setTotalVATFull(invoiceObj.getIntValue(
        InvoiceClassCollection.FIELD_TOTAL_VAT_FULL));
    invoice.setCancelled((invoiceObj.getIntValue(
        InvoiceClassCollection.FIELD_INVOICE_CANCELLED, 0) == 1));
    invoice.setStatus(EInvoiceStatus.parse(invoiceObj.getStringValue(
        InvoiceClassCollection.FIELD_INVOICE_STATUS)));
    return invoice;
  }

  IInvoiceItem convertToInvoiceItem(BaseObject invoiceItemObj) {
    IInvoiceItem invoiceItem = Utils.getComponent(IInvoiceItem.class);
    invoiceItem.setAmount(invoiceItemObj.getIntValue(
        InvoiceClassCollection.FIELD_AMOUNT, 0));
    invoiceItem.setArticleNr(invoiceItemObj.getStringValue(
        InvoiceClassCollection.FIELD_ARTICLE_NR));
    invoiceItem.setOrderNr(invoiceItemObj.getStringValue(
        InvoiceClassCollection.FIELD_ORDER_NUMBER));
    invoiceItem.setUnitPrice(invoiceItemObj.getIntValue(
        InvoiceClassCollection.FIELD_UNIT_PRICE));
    invoiceItem.setUnitOfPrice(invoiceItemObj.getIntValue(
        InvoiceClassCollection.FIELD_UNIT_OF_PRICE));
    invoiceItem.setVATCode(invoiceItemObj.getIntValue(
        InvoiceClassCollection.FIELD_VAT_CODE));
    invoiceItem.setVATValue(invoiceItemObj.getFloatValue(
        InvoiceClassCollection.FIELD_VAT_VALUE));
    invoiceItem.setName(invoiceItemObj.getStringValue(
        InvoiceClassCollection.FIELD_ITEM_DESCRIPTION));
    invoiceItem.setUnitOfMeasure(invoiceItemObj.getStringValue(
        InvoiceClassCollection.FIELD_UNIT_OF_MEASURE));
    return invoiceItem;
  }

  private String getWikiName() {
    return getContext().getDatabase();
  }

  public void storeInvoice(IInvoice theInvoice) {
    storeInvoice(theInvoice, "", false);
  }

  public void storeInvoice(IInvoice theInvoice, String comment) {
    storeInvoice(theInvoice, comment, false);
  }

  /**
   * theInvoice MUST provide a invoiceNumber
   */
  public void storeInvoice(IInvoice theInvoice, String comment, boolean isMinorEdit) {
    String invoiceNumber = theInvoice.getInvoiceNumber();
    DocumentReference invoiceDocRef = invoiceService.getInvoiceDocRefForInvoiceNumber(
        invoiceNumber);
    if (invoiceDocRef == null) {
      invoiceDocRef = invoiceService.getNewInvoiceDocRef(theInvoice);
    }
    try {
      XWikiDocument invoiceDoc = getContext().getWiki().getDocument(invoiceDocRef,
          getContext());
      BaseObject invoiceObj = getOrCreateInvoiceObject(invoiceDoc);
      convertInvoiceTo(theInvoice, invoiceObj);
      DocumentReference invoiceItemClassRef = getInvoiceClasses().getInvoiceItemClassRef(
          getWikiName());
      invoiceDoc.removeXObjects(invoiceItemClassRef);
      int position = 0;
      for (IInvoiceItem item : theInvoice.getInvoiceItems()) {
        position++;
        BaseObject invItemObj = invoiceDoc.newXObject(invoiceItemClassRef, getContext());
        convertInvoiceItemTo(item, invItemObj, position);
      }
      callStoreInvoiceExtender(theInvoice, invoiceNumber, invoiceDoc);
      getContext().getWiki().saveDocument(invoiceDoc, comment, isMinorEdit, getContext());
    } catch (XWikiException exp) {
      LOGGER.error("Failed to store invoice document [" + invoiceDocRef + "].", exp);
    }
  }

  private void callStoreInvoiceExtender(IInvoice theInvoice, String invoiceNumber,
      XWikiDocument invoiceDoc) {
    for (IInvoiceStoreExtenderRole storeExtender : storeExtenderMap.values()) {
      try {
        storeExtender.storeInvoice(theInvoice, invoiceDoc);
      } catch (Exception exp) {
        LOGGER.error("IInvoiceStoreExtender [" + storeExtender.getClass()
            + "] failed to store invoice [" + invoiceNumber + "].", exp);
      }
    }
  }

  BaseObject getOrCreateInvoiceObject(XWikiDocument invoiceDoc)
      throws XWikiException {
    BaseObject invoiceObj = invoiceDoc.getXObject(getInvoiceClasses(
        ).getInvoiceClassRef(getWikiName()));
    if (invoiceObj == null) {
      invoiceObj = invoiceDoc.newXObject(getInvoiceClasses().getInvoiceClassRef(
          getWikiName()), getContext());
    }
    return invoiceObj;
  }

  void convertInvoiceTo(IInvoice theInvoice, BaseObject invoiceObj) {
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_NUMBER,
        theInvoice.getInvoiceNumber());
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER,
        theInvoice.getOrderNr());
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_SUBJECT,
        theInvoice.getName());
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_CURRENCY,
        theInvoice.getCurrency());
    invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_COMMENT,
        theInvoice.getComment());
    invoiceObj.setDateValue(InvoiceClassCollection.FIELD_INVOICE_DATE,
        theInvoice.getInvoiceDate());
    invoiceObj.setIntValue(InvoiceClassCollection.FIELD_TOTAL_PRICE,
        theInvoice.getPrice());
    invoiceObj.setIntValue(InvoiceClassCollection.FIELD_TOTAL_VAT_FREE,
        theInvoice.getTotalVATFree());
    invoiceObj.setIntValue(InvoiceClassCollection.FIELD_TOTAL_VAT_REDUCED,
        theInvoice.getTotalVATReduced());
    invoiceObj.setIntValue(InvoiceClassCollection.FIELD_TOTAL_VAT_FULL,
        theInvoice.getTotalVATFull());
    if (theInvoice.getStatus() != null) {
      invoiceObj.setStringValue(InvoiceClassCollection.FIELD_INVOICE_STATUS,
          theInvoice.getStatus().getStoredValue());
    }
    if (theInvoice.isCancelled()) {
      invoiceObj.setIntValue(InvoiceClassCollection.FIELD_INVOICE_CANCELLED, 1);
    } else {
      invoiceObj.setIntValue(InvoiceClassCollection.FIELD_INVOICE_CANCELLED, 0);
    }
  }

  void convertInvoiceItemTo(IInvoiceItem invoiceItem, BaseObject invoiceItemObj,
      int position) {
    invoiceItemObj.setIntValue(InvoiceClassCollection.FIELD_ITEM_POSITION, position);
    invoiceItemObj.setIntValue(InvoiceClassCollection.FIELD_AMOUNT,
        invoiceItem.getAmount());
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_ARTICLE_NR,
        invoiceItem.getArticleNr());
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER,
        invoiceItem.getOrderNr());
    invoiceItemObj.setIntValue(InvoiceClassCollection.FIELD_UNIT_PRICE,
        invoiceItem.getUnitPrice());
    invoiceItemObj.setFloatValue(InvoiceClassCollection.FIELD_UNIT_OF_PRICE,
        invoiceItem.getUnitOfPrice());
    invoiceItemObj.setIntValue(InvoiceClassCollection.FIELD_VAT_CODE,
        invoiceItem.getVATCode());
    invoiceItemObj.setFloatValue(InvoiceClassCollection.FIELD_VAT_VALUE,
        invoiceItem.getVATValue());
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_ITEM_DESCRIPTION,
        invoiceItem.getName());
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_UNIT_OF_MEASURE,
        invoiceItem.getUnitOfMeasure());
  }

}
