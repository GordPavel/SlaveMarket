package model.merchandises.classes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import exceptions.WrongQueryException;
import model.merchandises.FoodInterface;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement(name = "poison")
@XmlAccessorType(XmlAccessType.FIELD)
public class Poison implements FoodInterface {

  private String onset;
  private int id;
  private String frequency;
  private String effect;
  private Float chance;
  private Float weight;
  private String type;
  private String name;

  /**
   * Method to check and parse values that comes out from map.
   *
   * @param value value with integer or float.
   * @return value from string in float if it's >=1
   */
  private static Float parseAndCheck(String key, String value) {
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
      throw new WrongQueryException(mistake.toString());
    }
    return dValue;
  }


  /**
   * Method to list all of params required to create object. And that fields can be changed.
   *
   * @return list of strings with minimum required fields for creating instance of class
   */
  public static List<String> mandatoryFields() {
    return Arrays.asList("Name",
            "Poisoning type",
            "Onset",
            "Frequency",
            "Effect",
            "Death chance",
            "Weight");
  }

  public static Poison buildFromMap(HashMap<String, String> map) {
    Poison poison = new Poison();
    List<String> requiredFields = mandatoryFields();
    for (String s : requiredFields) {
      if (!map.containsKey(s.toUpperCase())) {
        throw new WrongQueryException("Missed key \"" + s + "\"");
      }
    }
    try {
      poison.name = map.get("NAME");
      poison.weight = parseAndCheck("WEIGHT", map.get("WEIGHT"));
      poison.onset = map.get("ONSET");
      poison.type = map.get("POISONING TYPE");
      poison.frequency = map.get("FREQUENCY");
      poison.chance = parseAndCheck("DEATH CHANCE", map.get("DEATH CHANCE"));
      poison.effect = map.get("EFFECT");
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return poison;
  }

  @Override
  public String getComposition() {
    return "poison";
  }

  @Override
  public Float getEnergyValue() {
    return (float) 0;
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
    return (weight * 1000 * chance) % 100;
  }

  @Override
  public String getName() {
    return name;
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
        case "NAME":
          this.name = value;
          break;
        case "ONSET":
          this.onset = value;
          break;
        case "DEATH CHANCE":
          this.chance = parseAndCheck(key, value);
          break;
        case "POISONING TYPE":
          type = value;
          break;
        case "FREQUENCY":
          frequency = value;
          break;
        case "EFFECT":
          effect = value;
          break;
        default:
          break;
      }
    });
  }

  @Override
  public String getAllInfo() {
    JsonObject food = new JsonObject();
    food.add("class", new JsonPrimitive("Poison"));
    food.add("id", new JsonPrimitive(id));
    food.add("name", new JsonPrimitive(name));
    food.add("poisoning type", new JsonPrimitive(type));
    food.add("onset", new JsonPrimitive(onset));
    food.add("frequency", new JsonPrimitive(frequency));
    food.add("death chance", new JsonPrimitive(chance));
    food.add("effect", new JsonPrimitive(effect));
    food.add("weight", new JsonPrimitive(weight));
    food.add("benefit", new JsonPrimitive(getBenefit()));
    return food.toString();
  }


  @Override
  public boolean equals(Object obj) {
    return obj.getClass().getName().equals(this.getClass().getName())
            && ((Poison) obj).getAllInfo().equals(getAllInfo());
  }

  @Override
  public int hashCode() {
    return Objects.hash(onset, id, frequency, effect, chance, weight, type, name);
  }
}