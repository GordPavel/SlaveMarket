package model.merchandises;

import java.util.List;

public enum MandatoryFields {
  Slave(model.merchandises.classes.Slave.mandatoryFields()),
  Food(model.merchandises.classes.Food.mandatoryFields());


  private final List<String> fields;

  MandatoryFields(List<String> fields) {
    this.fields = fields;
  }

  public List<String> getFields() {
    return fields;
  }
}
