package ru.cracker.model.merchandises;

import java.util.*;

/**
 * 
 */
public interface Merchandise {


    /**
     * get Merchandise's id
     * @return
     */
    public int getId();

    /**
     * @return
     */
    public int getPrice();

    /**
     * @param benefit 
     * @return
     */
    public void setBenefit(Double benefit);

    /**
     * Returns merchandise quality in percentage
     * @return
     */
    public Double getBenefit();

    /**
     * Method mark merchandise as bought
     * @return
     */
    public boolean buy();

    /**
     * Get merchant's name
     * @return Merchant's name
     */
    public String getName();

    /**
     * Returns all information about merchandise.
     * @return Complicated string of merchandise info.
     * Formatted like "ClassName Param1:Value1 Param2:Value2 ..."
     */
    public String getAllInfo();

    /**
     * Returns merchandise boughtFlag.
     * @return true if merchandise already bought.
     */
    public boolean isBought();

    /**
     * method to set new id to mercandise
     * @param id new id
     * @return
     */
    public void setId(int id);

}