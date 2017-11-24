package ru.cracker.view;


/**
 *
 */
public interface Observer {


    /**
     * Apply action performed after the trigger
     *
     * @return
     */
    public void update();

    /**
     * Update information of Merchandise with id.
     *
     * @param id id of changed element
     * @return
     */
    public void deleted(int id);

    /**
     * Update information of Merchandise with id.
     *
     * @param id id of changed element
     * @return
     */
    public void changed(int id);

}