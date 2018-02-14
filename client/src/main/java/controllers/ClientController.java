package controllers;

import com.google.gson.*;
import exceptions.CreateMerchandiseException;
import exceptions.MerchandiseAlreadyBought;
import exceptions.MerchandiseNotFoundException;
import exceptions.WrongQueryException;
import javafx.scene.control.Alert;
import model.database.Logger;
import model.merchandises.Merchandise;
import util.Util;
import view.View;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
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
  String address;
  int port;

  public ClientController(View view, String address, int port) {
    gui = view;
    this.address = address;
    this.port =port;
    connectServer();
  }

  /**
   * Method to update socket information
   */
  private void connectServer() {
    if (input == null && output == null) {
      try {
        socket = new Socket(address, port);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        if (!ping(address, port)
                && !address.equals(ResourceBundle.getBundle("app").getString("serverAddress"))
                && port!=Integer.parseInt(ResourceBundle.getBundle("app").getString("serverPort"))){
          address = ResourceBundle.getBundle("app").getString("serverAddress");
          port = Integer.parseInt(ResourceBundle.getBundle("app").getString("serverPort"));
          connectServer();
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
    writeAndGetResponse(request.toString());
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
    JsonObject request = new JsonObject();
    request.add("action", new JsonPrimitive("getMerchant"));
    request.add("id", new JsonPrimitive(id));
    JsonObject response = writeAndGetResponse(request.toString());
    return response.get("status").getAsInt() == 400 ? ""
            : response.get("merchandise")
            .getAsString();
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
    JsonObject object1 =writeAndGetResponse(object.toString());
    if (object1.get("status").getAsInt()==400){
      throw new IllegalArgumentException(object1.get("errorKey").getAsString());
    }
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
  public void addMerchandiseByMap(String className, Map<String, String> kvs, String user, String token, int price) throws CreateMerchandiseException {
    JsonObject request = new JsonObject();
    request.add("action", new JsonPrimitive("add merchandise"));
    request.add("className", new JsonPrimitive(className));
    Gson gson = new GsonBuilder().create();
    request.add("merchandise", new JsonPrimitive(gson.toJson(kvs)));
    request.add("username", new JsonPrimitive(user));
    request.add("token", new JsonPrimitive(token));
    request.add("price", new JsonPrimitive(price));
    JsonObject object = writeAndGetResponse(request.toString());
    if (object.get("status").getAsInt() == 400) {
      throw new CreateMerchandiseException(object.get("errorKey").getAsString());
    }
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
   * Method to write objects to server and wait for response.
   *
   * @param object String of object to write in json format
   * @return response
   */
  private JsonObject writeAndGetResponse(String object) {
    connectServer();
    try {
      output.writeUTF(object);
      output.flush();
      Object response = input.readUTF();
      JsonParser parser = new JsonParser();
      JsonObject answer = parser.parse((String) response).getAsJsonObject();
      String action = parser.parse(object)
              .getAsJsonObject()
              .get("action")
              .getAsString();
      if (answer.get("status").getAsInt() == 400
              && ! action.equals("search")
              && ! action.equals("new Values")
              && ! action.equals("add merchandise")) {
        Util.runAlert(Alert.AlertType.ERROR, "Error",
                answer.get("info").getAsString(),
                "server returned status 400 with message: " + answer.get("info").getAsString());
      }
      return answer;
    } catch (IOException | NullPointerException e) {
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
  public String getDealById(int id) {
    JsonObject object = new JsonObject();
    object.add("action", new JsonPrimitive("getDeal"));
    object.add("id", new JsonPrimitive(id));
    JsonObject response = writeAndGetResponse(object.toString());
    return null != response.get("deal") ? response.get("deal").getAsString() : "";
  }

  @Override
  public boolean exportAllData(String fileName) {
    return false;
  }

  @Override
  public boolean importAllData(String filename) {
    return false;
  }

  @Override
  public boolean ping(String address, int port) {
    try {
      socket = new Socket();
      socket.connect(new InetSocketAddress(address, port), 4000);
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      JsonObject object = new JsonObject();
      object.add("action", new JsonPrimitive("ping"));
      out.writeUTF(object.toString());
      out.flush();
      Object response = in.readUTF();
      JsonParser parser = new JsonParser();
      JsonObject answer = parser.parse((String) response).getAsJsonObject();
      if (answer.get("status").getAsInt()==200){
        return true;
      }
    } catch (Exception e) {
      return false;
    }
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
      Base64.Encoder encoder = Base64.getEncoder();
      pass = encoder.encodeToString(cipher.doFinal(pass.getBytes()));
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      logger.logError("Can't send credentials. Info: " + e.getMessage());
    }
    return pass;
  }
}
