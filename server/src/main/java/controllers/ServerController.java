package controllers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import exceptions.CreateMerchandiseException;
import exceptions.MerchandiseAlreadyBought;
import exceptions.MerchandiseNotFoundException;
import exceptions.WrongQueryException;
import model.Model;
import model.database.Logger;
import model.merchandises.Merchandise;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerController implements Controller {
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private Logger logger = new Logger();
  private Model model;

  /**
   * Simple Constructor.
   * Start waiting for data.
   *
   * @param model  link to {@link Model}
   * @param socket socket, to send and receive data.
   */
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

  /**
   * Method to do something with received data.
   *
   * @param json received json object.
   */
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
      } else if (json.get("action").getAsString().equals("register")) {
        response.add("registered",
                new JsonPrimitive(register(
                        json.get("username").getAsString(),
                        json.get("password").getAsString())));
      } else if (json.get("action").getAsString().equals("disconnect")) {
        disconnect(json.get("username").getAsString(), json.get("token").getAsString());
      } else if (json.get("action").getAsString().equals("search")) {
        List<String> merchandises = model.searchMerchandise(json.get("query").getAsString());
        JsonArray array = new JsonArray();
        JsonParser parser = new JsonParser();
        merchandises.forEach(merchandise -> array.add(parser.parse(merchandise)));
        response.add("merchandises", array);
      } else if (json.get("action").getAsString().equals("deals")) {
        List<String> deals = model.getDealsByUser(json.get("username").getAsString(), json.get("token").getAsString());
        JsonArray array = new JsonArray();
        JsonParser parser = new JsonParser();
        deals.forEach(deal -> array.add(parser.parse(deal)));
        response.add("deals", array);
      } else if (json.get("action").getAsString().equals("changeLogin")) {
        response.add("changed", new JsonPrimitive(model.changeLogin(
                json.get("username").getAsString(),
                json.get("newUsername").getAsString(),
                json.get("token").getAsString())));
      } else if (json.get("action").getAsString().equals("getClasses")) {
        JsonArray array = new JsonArray();
        for (String classString :
                model.getAvailableClasses()) {
          array.add(classString);
        }
        response.add("classes", array);
      } else if (json.get("action").getAsString().equals("mandatoryFields")) {
        List<String> fields = model.getMandatoryFields(json.get("className").getAsString());
        JsonArray array = new JsonArray();
        for (String field : fields) {
          array.add(field);
        }
        response.add("fields", array);
      } else if (json.get("action").getAsString().equals("changeUsername")) {
        response.add("changed", new JsonPrimitive(model.changeLogin(
                json.get("username").getAsString(),
                json.get("newLogin").getAsString(),
                json.get("token").getAsString())));
      } else if (json.get("action").getAsString().equals("changePassword")) {
        model.changePassword(
                json.get("username").getAsString(),
                json.get("newPassword").getAsString(),
                json.get("token").getAsString()
        );
      } else if (json.get("action").getAsString().equals("Buy merchandise")) {
        response.add("merchandise", new JsonPrimitive(model.buyMerchandise(
                json.get("id").getAsInt(),
                json.get("username").getAsString(),
                json.get("token").getAsString()
        )));
      } else if (json.get("action").getAsString().equals("Remove merchandise")) {
        model.removeMerchandise(
                json.get("id").getAsInt(),
                json.get("username").getAsString(),
                json.get("token").getAsString()
        );
      } else if (json.get("action").getAsString().equals("add merchandise")) {
//        JsonArray merchandise = json.get("merchandise").getAsJsonArray();
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        Gson gson = new GsonBuilder().create();
//        Map<String, String> merchandise = ;
        model.addMerchandiseByMap(
                json.get("className").getAsString(),
                gson.fromJson(json.get("merchandise").getAsString(), type),
                json.get("username").getAsString(),
                json.get("token").getAsString(),
                json.get("price").getAsInt());
      } else if (json.get("action").getAsString().equals("new Values")) {
        model.setValuesToMerchandise(
                json.get("id").getAsInt(),
                json.get("values").getAsString(),
                json.get("username").getAsString(),
                json.get("token").getAsString());
      }
      sendResponse(response);
    } catch (Exception e) {
      response = new JsonObject();
      response.add("status", new JsonPrimitive(400));
      response.add("info", new JsonPrimitive(e.getMessage()));
      sendResponse(response);
    }
  }

  /**
   * Method to send objects through socket.
   *
   * @param response object to send.
   */
  private void sendResponse(Object response) {
    try {
      outputStream.writeObject(response.toString());
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
  public List<String> getMandatoryFields(String className) {
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
    return model.changeLogin(username, newLogin, token);
  }

  @Override
  public void changePassword(String username, String newPassword, String token) {
    model.changePassword(username, newPassword, token);
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
