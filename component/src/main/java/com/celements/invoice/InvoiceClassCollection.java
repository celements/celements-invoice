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
package com.celements.invoice;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.classes.AbstractClassCollection;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.classes.BooleanClass;

@Component("com.celements.invoice.classcollection")
public class InvoiceClassCollection extends AbstractClassCollection {

  public static final String INVOICE_CLASSES_SPACE = "InvoiceClasses";

  public static final String INVOICE_CLASS_DOC = "InvoiceClass";
  public static final String FIELD_INVOICE_NUMBER = "invoiceNumber";
  public static final String FIELD_UNIT_OF_MEASURE = "unitOfMeasure";
  public static final String FIELD_INVOICE_SUBJECT = "subject";
  public static final String FIELD_INVOICE_CURRENCY = "currency";
  public static final String FIELD_INVOICE_COMMENT = "comment";
  public static final String FIELD_INVOICE_DATE = "invoiceDate";
  public static final String FIELD_TOTAL_PRICE = "totalPrice";
  public static final String FIELD_TOTAL_VAT_FREE = "totalVATfree";
  public static final String FIELD_TOTAL_VAT_REDUCED = "totalVATreduced";
  public static final String FIELD_TOTAL_VAT_FULL = "totalVATfull";
  public static final String FIELD_INVOICE_STATUS = "status";
  public static final String FIELD_INVOICE_CANCELLED = "cancelled";
  public static final String FIELD_CUSTOMER_ID = "customerId";

  public static final String INVOICE_SUBSCRIPTION_ITEM_CLASS_DOC = "SubscriptionItemClass";
  public static final String FIELD_INVOICE_SUBSCR_REF = "subscrRef";
  public static final String FIELD_INVOICE_SUBSCR_FROM = "from";
  public static final String FIELD_INVOICE_SUBSCR_TO = "to";

  public static final String INVOICE_ITEM_CLASS_DOC = "InvoiceItemClass";
  public static final String FIELD_AMOUNT = "amount";
  public static final String FIELD_UNIT_PRICE = "unitPrice";
  public static final String FIELD_ORDER_NUMBER = "orderNumber";
  public static final String FIELD_ARTICLE_NR = "articleNr";
  public static final String FIELD_ITEM_POSITION = "position";
  public static final String FIELD_ITEM_DESCRIPTION = "description";
  public static final String FIELD_VAT_VALUE = "vatValue";
  public static final String FIELD_VAT_CODE = "vatCode";
  public static final String FIELD_UNIT_OF_PRICE = "unitOfPrice";

  public static final String INVOICE_ADDRESS_CLASS_DOC = "AddressClass";
  public static final String FIELD_ADDRESS_COMPANY = "company";
  public static final String FIELD_ADDRESS_TITLE = "title";
  public static final String FIELD_ADDRESS_FIRST_NAME = "first_name";
  public static final String FIELD_ADDRESS_NAME = "name";
  public static final String FIELD_ADDRESS_STREET = "address";
  public static final String FIELD_ADDRESS_ADDITION = "addressAddition";
  public static final String FIELD_ADDRESS_POBOX = "pobox";
  public static final String FIELD_ADDRESS_ZIP = "zip";
  public static final String FIELD_ADDRESS_CITY = "city";
  public static final String FIELD_ADDRESS_COUNTRY = "country";
  public static final String FIELD_ADDRESS_EMAIL = "email";
  public static final String FIELD_ADDRESS_PHONE = "phone";
  public static final String FIELD_ADDRESS_TYPE = "address_type";

  public InvoiceClassCollection() {}

  @Override
  public String getConfigName() {
    return "celInvoice";
  }

  @Override
  protected void initClasses() throws XWikiException {
    getInvoiceClass();
    getInvoiceSubscriptionItemClass();
    getInvoiceItemClass();
    getInvoiceAddressClass();
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

    needsUpdate |= bclass.addTextField(FIELD_INVOICE_NUMBER, "Invoice Number", 30);
    needsUpdate |= bclass.addTextField(FIELD_INVOICE_SUBJECT, "Subject", 30);
    needsUpdate |= bclass.addTextField(FIELD_INVOICE_CURRENCY, "Currency (iso4217)", 30);
    needsUpdate |= bclass.addTextAreaField(FIELD_INVOICE_COMMENT, "Comment", 80, 15);
    needsUpdate |= bclass.addDateField(FIELD_INVOICE_DATE, "Invoice Date", "dd.MM.yyyy",
        0);
    needsUpdate |= bclass.addTextField(FIELD_ORDER_NUMBER, "Order number (use for single"
        + " invoice)", 30);
    needsUpdate |= bclass.addNumberField(FIELD_TOTAL_PRICE, "Total of Invoice (in"
        + " smallest unit of currency)", 5, "integer");
    needsUpdate |= bclass.addNumberField(FIELD_TOTAL_VAT_FREE, "Total VAT free of Invoice"
        + " (in smallest unit of currency)", 5, "integer");
    needsUpdate |= bclass.addNumberField(FIELD_TOTAL_VAT_REDUCED, "Total VAT reduced of"
        + " Invoice (in smallest unit of currency)", 5, "integer");
    needsUpdate |= bclass.addNumberField(FIELD_TOTAL_VAT_FULL, "Total VAT full of Invoice"
        + " (in smallest unit of currency)", 5, "integer");
    needsUpdate |= bclass.addStaticListField(FIELD_INVOICE_STATUS, "Status",
        "new|printed|finance");
    if (bclass.get(FIELD_INVOICE_CANCELLED) == null) {
      BooleanClass element = new BooleanClass();
      element.setDisplayType("yesno");
      element.setName(FIELD_INVOICE_CANCELLED);
      element.setPrettyName("Cancelled");
      element.setDefaultValue(0);
      bclass.addField(FIELD_INVOICE_CANCELLED, element);
      needsUpdate = true;
    }
    needsUpdate |= bclass.addTextField(FIELD_CUSTOMER_ID, "Customer ID", 30);

    if (!"internal".equals(bclass.getCustomMapping())) {
      needsUpdate = true;
      bclass.setCustomMapping("internal");
    }

    setContentAndSaveClassDocument(doc, needsUpdate);
    return bclass;
  }

  public DocumentReference getSubscriptionItemClassRef(String wikiName) {
    return new DocumentReference(wikiName, INVOICE_CLASSES_SPACE,
        INVOICE_SUBSCRIPTION_ITEM_CLASS_DOC);
  }

  BaseClass getInvoiceSubscriptionItemClass() throws XWikiException {
    DocumentReference classRef = getSubscriptionItemClassRef(getContext().getDatabase());
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

    needsUpdate |= bclass.addTextField(FIELD_INVOICE_SUBSCR_REF, "Subscription Reference"
        + " (wiki docRef)", 30);
    needsUpdate |= bclass.addDateField(FIELD_INVOICE_SUBSCR_FROM,
        "Invoice Subscription From Date", "dd.MM.yyyy", 0);
    needsUpdate |= bclass.addDateField(FIELD_INVOICE_SUBSCR_TO,
        "Invoice Subscription To Date", "dd.MM.yyyy", 0);

    if (!"internal".equals(bclass.getCustomMapping())) {
      needsUpdate = true;
      bclass.setCustomMapping("internal");
    }

    setContentAndSaveClassDocument(doc, needsUpdate);
    return bclass;
  }

  public DocumentReference getInvoiceItemClassRef(String wikiName) {
    return new DocumentReference(wikiName, INVOICE_CLASSES_SPACE,
        INVOICE_ITEM_CLASS_DOC);
  }

  BaseClass getInvoiceItemClass() throws XWikiException {
    DocumentReference classRef = getInvoiceItemClassRef(getContext().getDatabase());
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

    needsUpdate |= bclass.addNumberField(FIELD_AMOUNT, "Amount", 5, "integer");
    needsUpdate |= bclass.addTextField(FIELD_UNIT_OF_MEASURE,
        "Unit of measure (UNSPSC-CODE)", 30);
    needsUpdate |= bclass.addNumberField(FIELD_UNIT_PRICE, "Price Unit (in smallest unit"
        + " of currency)", 5, "integer");
    needsUpdate |= bclass.addNumberField(FIELD_UNIT_OF_PRICE, "amount per unit-price", 5,
        "float");
    needsUpdate |= bclass.addNumberField(FIELD_VAT_CODE, "VAT Code", 5, "integer");
    needsUpdate |= bclass.addNumberField(FIELD_VAT_VALUE, "VAT Value", 5, "float");
    needsUpdate |= bclass.addTextField(FIELD_ITEM_DESCRIPTION, "Description", 30);
    needsUpdate |= bclass.addNumberField(FIELD_ITEM_POSITION, "Position on invoice", 5,
        "integer");
    needsUpdate |= bclass.addTextField(FIELD_ARTICLE_NR, "Article number", 30);
    needsUpdate |= bclass.addTextField(FIELD_ORDER_NUMBER, "Order number (use for"
        + " collective billing)", 30);

    if (!"internal".equals(bclass.getCustomMapping())) {
      needsUpdate = true;
      bclass.setCustomMapping("internal");
    }

    setContentAndSaveClassDocument(doc, needsUpdate);
    return bclass;
  }

  public DocumentReference getInoviceAddressClassRef(String wikiName) {
    return new DocumentReference(wikiName, INVOICE_CLASSES_SPACE,
        INVOICE_ADDRESS_CLASS_DOC);
  }

  BaseClass getInvoiceAddressClass() throws XWikiException {
    XWikiDocument doc;
    boolean needsUpdate = false;
    DocumentReference classRef = getInoviceAddressClassRef(getContext().getDatabase());

    try {
      doc = getContext().getWiki().getDocument(classRef, getContext());
    } catch (XWikiException xwe) {
      LOGGER.error("getInvoiceAddressClass failed", xwe);
      doc = new XWikiDocument(classRef);
      needsUpdate = true;
    }

    BaseClass bclass = doc.getXClass();
    bclass.setXClassReference(classRef);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_COMPANY, "Company", 30);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_TITLE, "Title", 30);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_FIRST_NAME, "First Name", 30);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_NAME, "Name", 30);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_STREET, "Address", 30);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_ADDITION, "Address Addition", 30);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_POBOX, "PO Box", 30);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_ZIP, "Zip", 30);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_CITY, "City", 30);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_COUNTRY, "Country", 30);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_EMAIL, "Email", 30);
    needsUpdate |= bclass.addTextField(FIELD_ADDRESS_PHONE, "Phone", 30);
    needsUpdate |= bclass.addStaticListField(FIELD_ADDRESS_TYPE, "Address Type",
        "Shipping|Billing");

    if (!"internal".equals(bclass.getCustomMapping())) {
      needsUpdate = true;
      bclass.setCustomMapping("internal");
    }

    setContentAndSaveClassDocument(doc, needsUpdate);
    return bclass;
  }
}
