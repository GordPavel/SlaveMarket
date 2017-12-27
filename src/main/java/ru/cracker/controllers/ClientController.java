package ru.cracker.controllers;

import com.google.gson.*;
import javafx.scene.control.Alert;
import ru.cracker.exceptions.MerchandiseAlreadyBought;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongQueryException;
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
import java.util.*;

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
    JsonObject request = new JsonObject();
    request.add("action", new JsonPrimitive("Remove merchandise"));
    request.add("username", new JsonPrimitive(user));
    request.add("id", new JsonPrimitive(id));
    request.add("token", new JsonPrimitive(token));
    JsonObject object = writeAndGetResponse(request.toString());
  }

  @Override
  public List<String> searchMerchant(String query) throws WrongQueryException {
    JsonObject request = new JsonObject();
    request.add("action", new JsonPrimitive("search"));
    request.add("query", new JsonPrimitive(query));
    JsonObject object = writeAndGetResponse(request.toString());
    List<String> response = new ArrayList<>();
    if (object.get("status").getAsInt() == 200) {
      JsonArray array = object.get("merchandises").getAsJsonArray();
      for (int i = 0; i < array.size(); i++) {
        JsonObject element = array.get(i).getAsJsonObject();
        response.add(element.toString());
      }
    }
    return response;
  }

  @Override
  public String getMerchantById(int id) throws MerchandiseNotFoundException {
    return null;
  }

  @Override
  public String buyMerchandise(int id, String user, String token) throws MerchandiseNotFoundException {
    JsonObject request = new JsonObject();
    request.add("action", new JsonPrimitive("Buy merchandise"));
    request.add("username", new JsonPrimitive(user));
    request.add("id", new JsonPrimitive(id));
    request.add("token", new JsonPrimitive(token));
    JsonObject object = writeAndGetResponse(request.toString());
    return null != object.get("merchandise") ? object.get("merchandise").getAsString() : "";
  }

  @Override
  public void setValuesToMerchandise(int id, String params, String user, String token) {
    JsonObject object = new JsonObject();
    object.add("action", new JsonPrimitive("new Values"));
    object.add("id", new JsonPrimitive(id));
    object.add("values", new JsonPrimitive(params));
    object.add("username", new JsonPrimitive(user));
    object.add("token", new JsonPrimitive(token));
    writeAndGetResponse(object.toString());
  }

  @Override
  public List<String> getAvailableClasses() {
    JsonObject object = new JsonObject();
    object.add("action", new JsonPrimitive("getClasses"));
    JsonObject response = writeAndGetResponse(object.toString());
    List<String> classes = new ArrayList<>();
    if (null != response.get("classes")) {
      JsonArray array = response.get("classes").getAsJsonArray();
      for (JsonElement e : array) {
        classes.add(e.getAsString());
      }
    }
    return classes;
  }

  @Override
  public List<String> getMandatoryFields(String className) {
    JsonObject object = new JsonObject();
    object.add("action", new JsonPrimitive("mandatoryFields"));
    object.add("className", new JsonPrimitive(className));
    JsonObject response = writeAndGetResponse(object.toString());
    List<String> fields = new ArrayList<>();
    if (null != response.get("fields")) {
      JsonArray array = response.get("fields").getAsJsonArray();
      for (JsonElement e : array) {
        fields.add(e.getAsString());
      }
    }
    return fields;
  }

  @Override
  public void addMerchandiseByMap(String className, Map<String, String> kvs, String user, String token, int price) {
    JsonObject request = new JsonObject();
    request.add("action", new JsonPrimitive("add merchandise"));
    request.add("className", new JsonPrimitive(className));
    Gson gson = new GsonBuilder().create();
    request.add("merchandise", new JsonPrimitive(gson.toJson(kvs)));
    request.add("username", new JsonPrimitive(user));
    request.add("token", new JsonPrimitive(token));
    request.add("price", new JsonPrimitive(price));
    writeAndGetResponse(request.toString());
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
      if (answer.get("status").getAsInt() == 400
              && !parser.parse(object)
              .getAsJsonObject()
              .get("action")
              .getAsString()
              .equals("search")) {
        Util.runAlert(Alert.AlertType.ERROR, "Error",
                answer.get("info").getAsString(),
                "server returned status 400 with message: " + answer.get("info").getAsString());
      }
      return answer;
    } catch (IOException | ClassNotFoundException | NullPointerException e) {
      if (null == input && null == output) {
        Util.runAlert(Alert.AlertType.ERROR,
                "error",
                "Can't connect to the server",
                e.getMessage());
      } else {
        Util.runAlert(Alert.AlertType.ERROR,
                "error",
                "some errors occurred. We're sorry.\nTry again later",
                e.getMessage());
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
    JsonObject object = new JsonObject();
    object.add("action", new JsonPrimitive("deals"));
    object.add("username", new JsonPrimitive(username));
    object.add("token", new JsonPrimitive(token));
    JsonObject response = writeAndGetResponse(object.toString());
    List<String> responseDeals = new ArrayList<>();
    if (null != response) {
      JsonArray deals = response.getAsJsonArray("deals");
      for (int i = 0; i < deals.size(); i++) {
        responseDeals.add(deals.get(i).toString());
      }
    }
    return responseDeals;
  }

  @Override
  public boolean changeLogin(String username, String newLogin, String token) {
    JsonObject object = new JsonObject();
    object.add("action", new JsonPrimitive("changeUsername"));
    object.add("username", new JsonPrimitive(username));
    object.add("newLogin", new JsonPrimitive(newLogin));
    object.add("token", new JsonPrimitive(token));
    JsonObject response = writeAndGetResponse(object.toString());
    return null != response.get("changed") && response.get("changed").getAsBoolean();
  }

  @Override
  public void changePassword(String username, String newPassword, String token) {
    JsonObject object = new JsonObject();
    object.add("action", new JsonPrimitive("changePassword"));
    object.add("username", new JsonPrimitive(username));
    object.add("newPassword", new JsonPrimitive(encrypt(username, newPassword)));
    object.add("token", new JsonPrimitive(token));
    writeAndGetResponse(object.toString());
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
