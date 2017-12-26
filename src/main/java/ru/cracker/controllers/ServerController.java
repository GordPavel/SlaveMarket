package ru.cracker.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import ru.cracker.exceptions.*;
import ru.cracker.model.Model;
import ru.cracker.model.database.Logger;
import ru.cracker.model.merchandises.Merchandise;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ServerController implements Controller {
  ObjectInputStream inputStream;
  ObjectOutputStream outputStream;
  Logger logger = new Logger();
  Model model;


  public ServerController(Model model, Socket socket) {
    this.model = model;
    try {
      inputStream = new ObjectInputStream(socket.getInputStream());
      outputStream = new ObjectOutputStream(socket.getOutputStream());
      String object;
      JsonParser parser = new JsonParser();
      while ((object = inputStream.readUTF()) != null && !socket.isClosed()) {
        act(parser.parse(object).getAsJsonObject());
      }
    } catch (IOException e) {
      logger.logError(e.getMessage());
    }
  }

  private void act(JsonObject json) {
    JsonObject response = new JsonObject();
    response.add("status", new JsonPrimitive(200));
    try {
      if (json.get("action").getAsString().equals("login")) {
        response.add("token",
                new JsonPrimitive(
                        login(
                                json.get("username").getAsString(),
                                json.get("password").getAsString())));
        sendResponse(response.toString());
      } else if (json.get("action").getAsString().equals("register")) {
        response.add("registered",
                new JsonPrimitive(register(
                        json.get("username").getAsString(),
                        json.get("password").getAsString())));
        sendResponse(response.toString());
      } else if (json.get("action").getAsString().equals("disconnect")) {
        disconnect(json.get("username").getAsString(), json.get("token").getAsString());
        sendResponse(response);
      }
    } catch (Exception e) {
      response = new JsonObject();
      response.add("status", new JsonPrimitive(400));
      response.add("info", new JsonPrimitive(e.getMessage()));
      sendResponse(response.toString());
    }
  }

  private void sendResponse(Object response) {
    try {
      outputStream.writeObject(response);
      outputStream.flush();
    } catch (IOException e) {
      logger.logError(e.getMessage());
    }
  }

  @Override
  public void addMerchant(Merchandise merch, String user, String token, int price) {
    model.addMerchandise(merch, user, token, price);
  }

  @Override
  public void removeMerchant(Merchandise merch, String user, String token) {
    model.removeMerchandise(merch, user, token);
  }

  @Override
  public void removeMerchant(int id, String user, String token) throws MerchandiseAlreadyBought {
    model.removeMerchandise(id, user, token);
  }

  @Override
  public List<String> searchMerchant(String query) throws WrongQueryException {
    return model.searchMerchandise(query);
  }

  @Override
  public String getMerchantById(int id) throws MerchandiseNotFoundException {
    return model.getMerchantById(id);
  }

  @Override
  public String buyMerchandise(int id, String user, String token) throws MerchandiseNotFoundException {
    return model.buyMerchandise(id, user, token);
  }

  @Override
  public void setValuesToMerchandise(int id, String params, String user, String token) {
    model.buyMerchandise(id, user, token);
  }

  @Override
  public List<String> getAvailableClasses() {
    return model.getAvailableClasses();
  }

  @Override
  public List<String> getMandatoryFields(String className) throws WrongClassCallException {
    return model.getMandatoryFields(className);
  }

  @Override
  public void addMerchandiseByMap
          (String className,
           Map<String, String> kvs,
           String user,
           String token,
           int price)
          throws CreateMerchandiseException {
    model.addMerchandiseByMap(className, kvs, user, token, price);
  }

  @Override
  public String login(String username, String password) {
    return model.login(username, password);
  }

  @Override
  public boolean register(String username, String pass) {
    return model.register(username, pass);
  }

  @Override
  public void disconnect(String username, String token) {
    model.disconnect(username, token);
  }

  @Override
  public void start() {

  }

  @Override
  public List<String> getDealsByUser(String username, String token) {
    return model.getDealsByUser(username, token);
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
}
