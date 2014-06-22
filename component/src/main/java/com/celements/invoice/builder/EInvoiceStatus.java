package com.celements.invoice.builder;

public enum EInvoiceStatus {

  isNew("new"), isPrinted("printed"), finance("finance");

  final private String storedValue;

  private EInvoiceStatus(String storedValue) {
    this.storedValue = storedValue;
  }

  public static EInvoiceStatus parse(String savedValue) {
    for (EInvoiceStatus invStatus : values()) {
      if (invStatus.getStoredValue().equals(savedValue)) {
        return invStatus;
      }
    }
    return null;
  }

  public String getStoredValue() {
    return storedValue;
  }

}
