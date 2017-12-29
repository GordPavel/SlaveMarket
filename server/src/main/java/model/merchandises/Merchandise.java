package model.merchandises;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Model of Merchandise. That can be anything what we can sell or buy.
 */
@XmlRootElement
public interface Merchandise extends Serializable {

  //    public static Merchandise buildFromMap(Class className, Map<String, String> map)
  //    throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException,
  //    IllegalAccessException {
  //
  //        for (Field field : className.getDeclaredFields()) {
  //            if (!Objects.equals(field.getName(), "id") || !map.containsKey(field.getName()))
  //                throw new WrongQueryException("Missed key \"" + field.getName() + "\"");
  //
  //        }
  ////            return (Merchandise) className.getMethod("buildFromMap",
  // map.getClass()).invoke(null, map);
  //        return null;
  //    }

  /**
   * Returns minimum of required fields for create an object of chosen class.
   *
   * @return field names
   */
  static List<String> getMandatoryFields(String className) {
    try {
      return MandatoryFields.valueOf(className).getFields();
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("wrong class called");
    }
  }

  /**
   * get Merchandise's id.
   *
   * @return id
   */
  int getId();

  /**
   * method to set new id to merchandise.
   *
   * @param id new id
   */
  void setId(int id);

  /**
   * Returns merchandise quality in percentage.
   */
  float getBenefit();


  /**
   * Get merchant's name.
   *
   * @return Merchant's name
   */
  String getName();

  /**
   * Returns all information about merchandise.
   *
   * @return Complicated string of merchandise info. Formatted like "ClassName Param1:Value1 etc.
   */
  String getAllInfo();


  /**
   * Set up object params with map values.
   *
   * @param map of params. Where key is field name and value is field value.
   */
  void setParamsByMap(Map<String, String> map);
}
