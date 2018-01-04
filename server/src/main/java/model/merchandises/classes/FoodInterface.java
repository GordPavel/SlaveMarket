package model.merchandises.classes;

import model.merchandises.Merchandise;

public interface FoodInterface extends Merchandise {

  /**
   * Food composition data (FCD) are detailed sets of information
   * on the nutritionally important components of foods and provide
   * values for energy and nutrients including protein, carbohydrates,
   * fat, vitamins and minerals and for other important food components such as
   * fibre. The data are presented in food composition databases (FCDBs).
   *
   * @return food composition.
   */
  String getComposition();

  /**
   * Food energy is chemical energy that animals (including humans)
   * derive from food through the process of cellular respiration.
   * Cellular respiration may either involve the chemical reaction
   * of food molecules with molecular oxygen
   * (aerobic respiration) or the process of reorganizing
   * the food molecules without additional oxygen (anaerobic respiration).
   *
   * @return food energyValue.
   */
  Float getEnergyValue();

  /**
   * For most of history, most cookbooks did not specify quantities precisely,
   * instead talking of "a nice leg of spring lamb", a "cupful" of lentils, a piece of butter
   * "the size of a walnut", and "sufficient" salt. Informal measurements such as a "pinch"
   * , a "drop", or a "hint" continue to be used from time to time.
   * In the US, Fannie Farmer introduced the more exact specification of quantities by volume
   * in her 1896 Boston Cooking-School Cook Book.
   * But in our case we present weight in kilos.
   *
   * @return
   */
  Float getWeight();

}
