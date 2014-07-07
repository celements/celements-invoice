package com.celements.invoice.migrations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.classes.IClassCollectionRole;
import com.celements.invoice.InvoiceClassCollection;
import com.celements.migrations.SubSystemHibernateMigrationManager;
import com.celements.migrator.AbstractCelementsHibernateMigrator;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.classes.NumberClass;
import com.xpn.xwiki.store.migration.XWikiDBVersion;
import com.xpn.xwiki.web.Utils;

@Component("ChangeUnitOfPriceToFloat")
public class ChangeUnitOfPriceToFloat extends AbstractCelementsHibernateMigrator {

  private static Log LOGGER = LogFactory.getFactory().getInstance(
      ChangeUnitOfPriceToFloat.class);

  @Override
  public boolean shouldExecute(XWikiDBVersion startupVersion) {
    return true;
  }

  @Override
  public void migrate(SubSystemHibernateMigrationManager manager, XWikiContext context
      ) throws XWikiException {
    LOGGER.info("Starting ChangeUnitOfPriceToFloat migration");
    DocumentReference invoiceItemClassRef = getInvoiceClasses().getInvoiceItemClassRef(
        context.getDatabase());
    if(context.getWiki().exists(invoiceItemClassRef, context)) {
      XWikiDocument doc = context.getWiki().getDocument(invoiceItemClassRef, context);
      BaseClass bclass = doc.getXClass();
      if(bclass.get(InvoiceClassCollection.FIELD_UNIT_OF_PRICE) != null) {
        NumberClass numClass = (NumberClass) bclass.get(
            InvoiceClassCollection.FIELD_UNIT_OF_PRICE);
        if("integer".equals(numClass.getNumberType())) {
          numClass.setNumberType("float");
          context.getWiki().saveDocument(doc, "migration: change unit of price to float", 
              context);
        }
      }
    }
    LOGGER.info("Finished ChangeUnitOfPriceToFloat migration");
  }

  public String getDescription() {
    return "Change type of unitOfPrice from int to float.";
  }

  public String getName() {
    return "ChangeUnitOfPriceToFloat";
  }

  private InvoiceClassCollection getInvoiceClasses() {
    return (InvoiceClassCollection) Utils.getComponent(IClassCollectionRole.class,
        "com.celements.invoice.classcollection");
  }

  /**
   * getVersion is using days since
   * 1.1.2010 until the day of committing this migration
   * 26.6.2014 -> 1637
   * http://www.convertunits.com/dates/from/Jan+1,+2010/to/Jun+26,+2014
   */
  public XWikiDBVersion getVersion() {
    return new XWikiDBVersion(1637);
  }
}
