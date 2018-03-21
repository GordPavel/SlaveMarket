package model.merchandises.classes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import exceptions.WrongQueryException;
import model.merchandises.FoodInterface;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "food")
@XmlAccessorType(XmlAccessType.FIELD)
public class Food implements FoodInterface {
  private String composition;
  private Float energy;
  private Float weight;
  private int id;
  private String name;

  /**
   * Method to list all of params required to create object. And that fields can be changed.
   *
   * @return list of strings with minimum required fields for creating instance of class
   */
  public static List<String> mandatoryFields() {
    return Arrays.asList("Name", "Weight", "Composition", "Energy value");
  }

  public static Food buildFromMap(HashMap<String, String> map) {
    Food food = new Food();
    List<String> requiredFields = mandatoryFields();
    for (String s : requiredFields) {
      if (!map.containsKey(s.toUpperCase())) {
        throw new WrongQueryException("Missed key \"" + s + "\"");
      }
    }
    try {
      food.composition = map.get("COMPOSITION");
      food.name = map.get("NAME");
      food.energy = parseAndCheck("ENERGY VALUE", map.get("ENERGY VALUE"));
      food.weight = parseAndCheck("WEIGHT", map.get("WEIGHT"));
    } catch (Exception e) {
      throw new WrongQueryException(e.getMessage());
    }
    return food;
  }

  /**
   * Method to check and parse values that comes out from map.
   *
   * @param value value with integer or float.
   * @return value from string in float if it's >=1
   */
  private static Float parseAndCheck(String key, String value) {
    Float dValue;
    dValue = getaFloat(key, value);
    return dValue;
  }

  static Float getaFloat(String key, String value) {
    Float dValue;
    try {
      dValue = Float.parseFloat(value);
      if (dValue <= 0) {
        JsonObject mistake = new JsonObject();
        mistake.add("errorType", new JsonPrimitive("Must be grater than 0"));
        mistake.add("errorKey", new JsonPrimitive(key));
        throw new WrongQueryException(mistake.toString());
      }
    } catch (Exception e) {
      JsonObject mistake = new JsonObject();
      mistake.add("errorType", new JsonPrimitive("Can't parse"));
      mistake.add("errorKey", new JsonPrimitive(key));
      throw new IllegalArgumentException(mistake.toString());
    }
    return dValue;
  }


  @Override
  public String getComposition() {
    return composition;
  }

  @Override
  public Float getEnergyValue() {
    return energy;
  }

  @Override
  public Float getWeight() {
    return weight;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public float getBenefit() {
    return Double.valueOf(Math.sqrt(Math.pow(energy, 2.0) * Math.pow(weight, 2.0)) % 100).floatValue();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getAllInfo() {
    JsonObject food = new JsonObject();
    food.add("class", new JsonPrimitive("Food"));
    food.add("id", new JsonPrimitive(id));
    food.add("name", new JsonPrimitive(name));
    food.add("energy", new JsonPrimitive(energy));
    food.add("composition", new JsonPrimitive(composition));
    food.add("weight", new JsonPrimitive(weight));
    food.add("benefit", new JsonPrimitive(getBenefit()));
    return food.toString();
  }

  /**
   * Set up object params with map values.
   *
   * @param map of params. Where key is field name and value is field value.
   */
  public void setParamsByMap(Map<String, String> map) {
    List<String> fields = mandatoryFields();
    map.forEach((key, value) -> {
      boolean contains = false;
      for (String field : fields) {
        if (field.toUpperCase().equals(key.toUpperCase())) {
          contains = true;
        }
      }
      if (!contains) {
        throw new WrongQueryException(key + "=" + value);
      }
      switch (key) {
        case "WEIGHT":
          this.weight = parseAndCheck(key, value);
          break;
        case "ENERGY VALUE":
          this.energy = parseAndCheck(key, value);
          break;
        case "COMPOSITION":
          this.composition = value;
          break;
        case "NAME":
          this.name = value;
          break;
        default:
          break;
      }
    });
  }

  @Override
  public boolean equals(Object obj) {
    return obj.getClass().getName().equals(this.getClass().getName())
            && ((Food) obj).getAllInfo().equals(getAllInfo());
  }

  @Override
  public int hashCode() {
    return this.id
            + this.energy.hashCode()
            + this.weight.hashCode()
            + this.composition.hashCode()
            + this.name.hashCode();
  }
}