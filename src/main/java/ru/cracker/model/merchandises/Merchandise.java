package ru.cracker.model.merchandises;

import java.io.Serializable;
import java.util.Map;

/**
 *
 */
public interface Merchandise extends Serializable{

//    public static Merchandise buildFromMap(Class className, Map<String, String> map) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
//
//        for (Field field : className.getDeclaredFields()) {
//            if (!Objects.equals(field.getName(), "id") || !map.containsKey(field.getName()))
//                throw new WrongQueryException("Missed key \"" + field.getName() + "\"");
//
//        }
////            return (Merchandise) className.getMethod("buildFromMap", map.getClass()).invoke(null, map);
//        return null;
//    }

    /**
     * get Merchandise's id
     *
     * @return
     */
    public int getId();

    /**
     * method to set new id to mercandise
     *
     * @param id new id
     * @return
     */
    public void setId(int id);

    /**
     * @return price
     */
    public int getPrice();

    /**
     * Returns merchandise quality in percentage
     *
     * @return
     */
    public float getBenefit();

    /**
     * Method mark merchandise as bought
     *
     * @return
     */
    public boolean buy(String user);

    /**
     * Get merchant's name
     *
     * @return Merchant's name
     */
    public String getName();

    /**
     * Returns all information about merchandise.
     *
     * @return Complicated string of merchandise info.
     * Formatted like "ClassName Param1:Value1 Param2:Value2 ..."
     */
    public String getAllInfo();

    /**
     * Returns merchandise boughtFlag.
     *
     * @return true if merchandise already bought.
     */
    public boolean isBought();

    /**
     * Set up object params with map values
     *
     * @param map of params. Where key is field name and value is field value.
     */
    public void setParamsByMap(Map<String, String> map);
}
