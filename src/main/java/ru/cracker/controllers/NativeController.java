package ru.cracker.controllers;

import ru.cracker.exceptions.CreateMerchandiseException;
import ru.cracker.exceptions.MerchandiseAlreadyBought;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongClassCallException;
import ru.cracker.model.Model;
import ru.cracker.model.merchandises.Merchandise;
import ru.cracker.view.View;
import ru.cracker.view.cli.CommandLineView;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Map;


/**
 * Implementation of Controller interface.
 */
public class NativeController implements Controller {

  /**
   * Model to access data.
   */
  private final Model model;

  /**
   * View to send data to it.
   */
  private final View view;

  /**
   * Initial constructor with model.
   *
   * @param model model to manage.
   */
  public NativeController(Model model) {
    this.model = model;
    view = new CommandLineView(model, this);

  }

  /**
   * Add slave in model.
   *
   * @param merch Slave to add
   * @param user  user who performed action
   * @param price merchandise's price
   */
  @Override
  public void addMerchant(Merchandise merch, String user, String token, int price) {
    model.addMerchandise(merch, user, token, price);
  }

  /**
   * tells to model for remove the slave.
   *
   * @param merch slave to remove
   * @param user  user who performed action
   */
  public void removeMerchant(Merchandise merch, String user, String token) {
    model.removeMerchandise(merch, user, token);
  }

  /**
   * Gjt[fkb c yfvb 
   * tells to model for remove the slave by id.
   *
   * @param id   slaves's id to remove the slave by it.
   * @param user user who performed action
   */
  public void removeMerchant(int id, String user, String token) throws MerchandiseAlreadyBought {
    model.removeMerchandise(id, user, token);
  }

  /**
   * tells to model for search  the slave by the query.
   *
   * @param query querry for search
   * @return list of founded slaves
   */
  public List<String> searchMerchant(String query) {
    return model.searchMerchandise(query);
  }


  /**
   * Returns merchandise by id or exception.
   *
   * @param id id of Merchandise
   * @return Founded merchandise or Exception
   */
  @Override
  public String getMerchantById(int id) throws MerchandiseNotFoundException {
    return model.getMerchantById(id);
  }

  /**
   * Marks merchandise as bought.
   *
   * @param id   unique merchandise identity
   * @param user user who performed action
   * @return bought merchandise
   * @throws MerchandiseNotFoundException throws if merchandise with that id is not found
   */
  @Override
  public String buyMerchandise(int id, String user, String token) throws MerchandiseNotFoundException {
    return model.buyMerchandise(id, user, token);
  }

  /**
   * Set new values  to merchandise.
   *
   * @param id     id of merchandise to be changed
   * @param params String of parameters with values to change
   * @param user   user who performed action
   */
  public void setValuesToMerchandise(int id, String params, String user, String token) {
    model.setValuesToMerchandise(id, params, user, token);
  }

  public List<String> getAvailableClasses() {
    return model.getAvailableClasses();
  }

  @Override
  public List<String> getMandatoryFields(String className) throws WrongClassCallException {
    return model.getMandatoryFields(className);
  }

  @Override
  public void addMerchandiseByMap(String className, Map<String, String> kvs, String user, String token, int price) throws CreateMerchandiseException {
    model.addMerchandiseByMap(className, kvs, user, token, price);
  }

  @Override
  public String login(String username, String password) {
    return model.login(username, encrypt(username, password));
  }

  public void start() {
    view.launch();
  }

  @Override
  public List<String> getDealsByUser(String username, String token) {
    return model.getDealsByUser(username, token);
  }

  @Override
  public boolean changeLogin(String username, String newLogin, String token) {
    return model.changeLogin(username, newLogin, token);
  }

  @Override
  public void changePassword(String username, String newPassword, String token) {
    model.changePassword(username, newPassword, token);
  }

  @Override
  public boolean exportAllData(String fileName) {
    return model.exportAllData(fileName);
  }

  @Override
  public boolean importAllData(String filename) {
    return model.importAllData(filename);
  }

  @Override
  public boolean register(String username, String pass) {
    return model.register(username, encrypt(username, pass));
  }

  @Override
  public void disconnect(String username, String token) {
    model.disconnect(username, token);
  }

  private String encrypt(String username, String pass) {
    try {
      String key = username;
      if (username.length() < 16) {
        char[] chars = new char[16 - username.length()];
        Arrays.fill(chars, '1');
        key = username + new String(chars);
      }
      Cipher cipher = Cipher.getInstance("AES");
      Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
      cipher.init(Cipher.ENCRYPT_MODE, aesKey);
      byte[] encrypted = cipher.doFinal(pass.getBytes());
      Encoder encoder = Base64.getEncoder();
      pass = encoder.encodeToString(encrypted);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      System.out.println("Can't send credentials");
    }
    return pass;
  }
}
