package ru.cracker.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import javafx.scene.control.Alert;
import ru.cracker.exceptions.*;
import ru.cracker.model.database.Logger;
import ru.cracker.model.merchandises.Merchandise;
import ru.cracker.view.View;
import ru.cracker.view.gui.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class ClientController implements Controller {
  Socket socket;
  Logger logger = new Logger();
  ObjectOutputStream output;
  ObjectInputStream input;
  View gui;

  {

  }

  public ClientController(View view) {
    gui = view;
    int connections = 0;
    while (null == output && null == input && connections < 10000) {
      connections++;
      int finalConnections = connections;
      try {
        socket = new Socket("127.0.0.1", 22033);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
      } catch (ConnectException e) {
        if (connections == 10000) {
          Util.runAlert(
                  Alert.AlertType.ERROR,
                  "Error",
                  "Can't connect servers",
                  e.getMessage());
        }
      } catch (IOException e) {
        logger.logError("Can't open socket or streams because: " + e.getMessage());
      }
    }
  }

  @Override
  public void addMerchant(Merchandise merch, String user, String token, int price) {

  }

  @Override
  public void removeMerchant(Merchandise merch, String user, String token) {

  }

  @Override
  public void removeMerchant(int id, String user, String token) throws MerchandiseAlreadyBought {

  }

  @Override
  public List<String> searchMerchant(String query) throws WrongQueryException {
    return null;
  }

  @Override
  public String getMerchantById(int id) throws MerchandiseNotFoundException {
    return null;
  }

  @Override
  public String buyMerchandise(int id, String user, String token) throws MerchandiseNotFoundException {
    return null;
  }

  @Override
  public void setValuesToMerchandise(int id, String params, String user, String token) {

  }

  @Override
  public List<String> getAvailableClasses() {
    return null;
  }

  @Override
  public List<String> getMandatoryFields(String className) throws WrongClassCallException {
    return null;
  }

  @Override
  public void addMerchandiseByMap(String className, Map<String, String> kvs, String user, String token, int price) throws CreateMerchandiseException {

  }

  @Override
  public String login(String username, String password) {
    JsonObject object = new JsonObject();
    object.add("action", new JsonPrimitive("login"));
    object.add("username", new JsonPrimitive(username));
    object.add("password", new JsonPrimitive(encrypt(username, password)));
//    output.writeObject();
    JsonObject answer = writeAndGetResponse(object.toString());
    return null == answer.get("token") ? "" : answer.get("token").getAsString();
  }

  /**
   * Method to write objects to server and wait for response
   *
   * @param object String of object to write in json format
   * @return
   */
  private JsonObject writeAndGetResponse(String object) {
    try {
      output.writeUTF(object);
      output.flush();
      Object response = input.readObject();
      JsonParser parser = new JsonParser();
      JsonObject answer = parser.parse((String) response).getAsJsonObject();
      if (answer.get("status").getAsInt() == 400) {
        Util.runAlert(Alert.AlertType.ERROR, "Error",
                answer.get("info").getAsString(),
                "server returned status 400 with message: " + answer.get("info").getAsString());
      }
      return answer;
    } catch (IOException | ClassNotFoundException | NullPointerException e) {
      if (null == input && null == output) {
        Util.runAlert(Alert.AlertType.ERROR, "error", "Can't connect to the server", e.getMessage());
      } else {
        Util.runAlert(Alert.AlertType.ERROR, "error", "some errors occurred. We're sorry.", e.getMessage());
      }
    }
    return null;
  }


  @Override
  public boolean register(String username, String pass) {
    JsonObject object = new JsonObject();
    object.add("action", new JsonPrimitive("register"));
    object.add("username", new JsonPrimitive(username));
    object.add("password", new JsonPrimitive(encrypt(username, pass)));
//    output.writeObject();
    JsonObject response = writeAndGetResponse(object.toString());
    return null != response.get("registered") && response.get("registered").getAsBoolean();
  }

  @Override
  public void disconnect(String username, String token) {
    JsonObject object = new JsonObject();
    object.add("action", new JsonPrimitive("disconnect"));
    object.add("username", new JsonPrimitive(username));
    object.add("token", new JsonPrimitive(token));
    writeAndGetResponse(object.toString());
  }


  @Override
  public void start() {
    gui.launch();
  }

  @Override
  public List<String> getDealsByUser(String username, String token) {
    return null;
  }

  @Override
  public boolean changeLogin(String username, String newLogin, String token) {
    return false;
  }

  @Override
  public void changePassword(String username, String newPassword, String token) {

  }

  @Override
  public boolean exportAllData(String fileName) {
    return false;
  }

  @Override
  public boolean importAllData(String filename) {
    return false;
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
      Base64.Encoder encoder = Base64.getEncoder();
      pass = encoder.encodeToString(encrypted);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      System.out.println("Can't send credentials");
    }
    return pass;
  }
}
