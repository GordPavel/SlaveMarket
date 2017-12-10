package ru.cracker.view;


/**
 * Class for notifications.
 */
public interface Observer {


  /**
   * Apply action performed after the trigger.
   */
  void update();

  /**
   * Update information of Merchandise with id.
   *
   * @param id id of changed element
   */
  void deleted(int id);

  /**
   * Update information of Merchandise with id.
   *
   * @param id id of changed element
   */
  void changed(int id);

}