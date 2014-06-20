package com.celements.invoice.store;

import groovy.lang.Singleton;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.invoice.InvoiceClassCollection;
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
          IInvoice invoice = Utils.getComponent(IInvoice.class);
          invoice.setInvoiceNumber(invoiceObj.getStringValue(
              InvoiceClassCollection.INVOICE_CLASSES_SPACE));
          List<BaseObject> invoiceItemObjList = invoiceDoc.getXObjects(getInvoiceClasses(
              ).getInvoiceItemClassRef(getWikiName()));
          if (invoiceItemObjList != null) {
            for(BaseObject invoiceItemObj : invoiceItemObjList) {
              if (invoiceItemObj != null) {
                invoice.addInvoiceItem(convertToInvoiceItem(invoiceItemObj));
              }
            }
          }
          return invoice;
        }
      } catch (XWikiException exp) {
        LOGGER.error("Failed to load invoice document [" + invoiceDocRef + "].", exp);
      }
    }
    return null;
  }

  IInvoiceItem convertToInvoiceItem(BaseObject invoiceItemObj) {
    IInvoiceItem invoiceItem = Utils.getComponent(IInvoiceItem.class);
    invoiceItem.setAmount(invoiceItemObj.getIntValue(
        InvoiceClassCollection.FIELD_AMOUNT, 0));
    invoiceItem.setArticleNr(invoiceItemObj.getStringValue(
        InvoiceClassCollection.FIELD_ARTICLE_NR));
    invoiceItem.setOrderNr(invoiceItemObj.getStringValue(
        InvoiceClassCollection.FIELD_ORDER_NUMBER));
    return invoiceItem;
  }

  void convertInvoiceItemTo(IInvoiceItem invoiceItem, BaseObject invoiceItemObj) {
    invoiceItemObj.setIntValue(InvoiceClassCollection.FIELD_AMOUNT,
        invoiceItem.getAmount());
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_ARTICLE_NR,
        invoiceItem.getArticleNr());
    invoiceItemObj.setStringValue(InvoiceClassCollection.FIELD_ORDER_NUMBER,
        invoiceItem.getOrderNr());
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
      invoiceObj.setStringValue(InvoiceClassCollection.INVOICE_CLASSES_SPACE,
          invoiceNumber);
      DocumentReference invoiceItemClassRef = getInvoiceClasses().getInvoiceItemClassRef(
          getWikiName());
      invoiceDoc.removeXObjects(invoiceItemClassRef);
      for (IInvoiceItem item : theInvoice.getInvoiceItems()) {
        BaseObject invItemObj = invoiceDoc.newXObject(invoiceItemClassRef, getContext());
        convertInvoiceItemTo(item, invItemObj);
      }
      getContext().getWiki().saveDocument(invoiceDoc, comment, isMinorEdit, getContext());
    } catch (XWikiException exp) {
      LOGGER.error("Failed to get invoice document [" + invoiceDocRef + "].", exp);
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

}
