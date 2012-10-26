package com.celements.invoice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.classes.AbstractClassCollection;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;

@Component("com.celements.invoice.classcollection")
public class InvoiceClassCollection extends AbstractClassCollection {

  public static final String INVOICE_CLASS_DOC = "InvoiceClass";
  public static final String INVOICE_CLASSES_SPACE = "InvoiceClasses";

  private static Log LOGGER = LogFactory.getFactory().getInstance(
      InvoiceClassCollection.class);

  @Override
  protected Log getLogger() {
    return LOGGER;
  }

  public InvoiceClassCollection() {}

  @Override
  protected void initClasses() throws XWikiException {
    getInvoiceClass();
  }

  public String getConfigName() {
    return "celInvoice";
  }

  public DocumentReference getInvoiceClassRef(String wikiName) {
    return new DocumentReference(wikiName, INVOICE_CLASSES_SPACE, INVOICE_CLASS_DOC);
  }

  BaseClass getInvoiceClass() throws XWikiException {
    DocumentReference classRef = getInvoiceClassRef(getContext().getDatabase());
    XWikiDocument doc;
    XWiki xwiki = getContext().getWiki();
    boolean needsUpdate = false;
    
    try {
      doc = xwiki.getDocument(classRef, getContext());
    } catch (XWikiException exp) {
      LOGGER.error("Failed to get xClass document for [" + classRef + "].", exp);
      doc = new XWikiDocument(classRef);
      needsUpdate = true;
    }
    
    BaseClass bclass = doc.getXClass();
    bclass.setDocumentReference(classRef);
    
    needsUpdate |= bclass.addTextField("invoiceNumber", "Invoice Number", 30);
    needsUpdate |= bclass.addTextField("subject", "Subject", 30);
    needsUpdate |= bclass.addTextAreaField("comment", "Comment", 80, 15);
    needsUpdate |= bclass.addDateField("invoiceDate" , "Invoice Date", "dd.MM.yyyy", 0);
    
    if(!"internal".equals(bclass.getCustomMapping())){
      needsUpdate = true;
      bclass.setCustomMapping("internal");
    }
    
    setContentAndSaveClassDocument(doc, needsUpdate);
    return bclass;
  }

}
