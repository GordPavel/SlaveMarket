package ru.cracker.model.merchandises;

import java.util.List;

public enum MandatoryFields {
  Slave(ru.cracker.model.merchandises.classes.Slave.mandatoryFields());


  private List<String> fields;

  MandatoryFields(List<String> fields) {
    this.fields = fields;
  }

  public List<String> getFields() {
    return fields;
  }
}
