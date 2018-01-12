package view;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import controllers.ClientController;
import controllers.Controller;
import exceptions.CreateMerchandiseException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.Util;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class ClientGui extends Application implements View, Observer {

  private Controller controller;
  private String token = "";
  private String username = "";
  private String password = "";
  //  private Logger logger = new Logger();
  private AnchorPane mainContent;
  private Stage rootStage;
  private GuiActions currentMenu;
  private ListView<String> menu;
  private ListView<String> mainList;
  private List<String> merchandises;
  private List<String> deals;
  private ImageView backButton;
  private FunctionalUtil lastScreen;
  private FunctionalUtil mainScreen;
  private Text currentUser;
  private JsonObject currentMerchandise;


  /**
   * Default constructor for javaFx
   */
  public ClientGui() {
  }

  @Override
  public void update() {

  }

  @Override
  public void deleted(int id) {

  }

  @Override
  public void changed(int id) {

  }

  @Override
  public void launch() {
    Application.launch(getClass());
  }

  /**
   * Start javaFx application
   *
   * @param stage basic jfx parameter
   * @throws Exception basic jfx Exception
   */
  @Override
  public void start(Stage stage) throws IOException {
    rootStage = stage;
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(
            getClass()
                    .getClassLoader()
                    .getResource("fxml/login.fxml")
    );
    stage.getIcons().add(new Image("images/icon.png"));
    Font.loadFont(getClass().getClassLoader().getResource("fonts/trench100free.ttf").toExternalForm(), 20);
    Font.loadFont(getClass().getClassLoader().getResource("fonts/timeburnernormal.ttf").toExternalForm(), 20);
    Parent content = loader.load();
    rootStage.setResizable(false);
    controller = new ClientController(this);
    Scene scene = new Scene(content);
    rootStage.initStyle(StageStyle.UNIFIED);
    rootStage.setScene(scene);
    setListeners(scene);
    rootStage.show();
  }

  /**
   * Add listeners to Login Scene
   *
   * @param scene login Scene
   */
  public void setListeners(Scene scene) {
    TextField login = (TextField) scene.lookup("#loginField");
    PasswordField pass = (PasswordField) scene.lookup("#passField");
    Button exitButton = (Button) scene.lookup("#exitButton");

    exitButton.setOnAction(event -> Platform.exit());

    Button loginButton = (Button) scene.lookup("#loginButton");
    loginButton.setDefaultButton(true);
    loginButton.setOnAction(event -> new Thread(() -> {
      Platform.runLater(() -> scene.setCursor(Cursor.WAIT));
      if (checkFields(login) && checkFields(pass)) {
        password = pass.getText().trim();
        username = login.getText().trim();
        token = controller.login(login.getText().trim(), pass.getText().trim());
        if ("-1".equals(token))
          Util.runAlert(
                  Alert.AlertType.INFORMATION,
                  "Info",
                  "User already in",
                  "");
        if (!"-1".equals(token) && !"".equals(token)) {
          try {
            Parent pane = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/main.fxml")));
            Scene mainScene = new Scene(pane);
            Platform.runLater(() -> rootStage.setScene(mainScene));
            setupMainListeners(mainScene);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
//        System.out.println("token: " + token);
      }
      Platform.runLater(() -> scene.setCursor(Cursor.DEFAULT));
    }).start());

    Button registerButton = (Button) scene.lookup("#registerButton");
    registerButton.setOnAction(event -> new Thread(() -> {
      Platform.runLater(() -> scene.setCursor(Cursor.WAIT));
      if (checkFields(login) && checkFields(pass)) {
        Boolean register = controller.register(login.getText().trim(), pass.getText().trim());
        if (register) {
          Util.runAlert(Alert.AlertType.INFORMATION,
                  "registered",
                  "Successfully registered",
                  "Successfully registered");
          System.out.println("successfully registered");
        } else {
          Util.runAlert(
                  Alert.AlertType.ERROR,
                  "Registration",
                  "Can't register.\nThat username already taken\n",
                  "");
        }
      }
      Platform.runLater(() -> scene.setCursor(Cursor.DEFAULT));
    }).start());
  }

  /**
   * Method to check that textField have at least one symbol.
   *
   * @param textField textField to check.
   * @return true if have at least one symbol
   */
  boolean checkFields(TextField textField) {
    textField.setStyle("-fx-border-style: none;");
    boolean good = true;
    if (textField.getText().trim().equals("")) {
      textField.setStyle("-fx-border-color: red;");
      good = false;
    }
    return good;
  }

  /**
   * Method called when app is closing.
   * That method disconnect user from server.
   *
   * @throws Exception
   */
  @Override
  public void stop() throws Exception {
    if (!"".equals(token) && !token.equals("-1")) {
      controller.disconnect(username, token);
    }
    super.stop();
  }

  /**
   * Add listeners on elements of main window.
   *
   * @param mainScene main window {@link Scene}
   */
  public void setupMainListeners(Scene mainScene) {
    backButton = (ImageView) mainScene.lookup("#backButton");
    backButton.setVisible(false);
    mainContent = (AnchorPane) mainScene.lookup("#mainContent");
    currentUser = (Text) mainScene.lookup("#CurrentUser");
    currentUser.setText("Current user: " + username);
    menu = (ListView) mainScene.lookup("#actions");
    setActions(GuiActions.MAIN, menu);
    currentMenu = GuiActions.MAIN;
    menu.setOnMouseClicked(event ->
    {
      if (event.getClickCount() == 2) {
        action(menu.getSelectionModel().getSelectedItem());
      }
    });
    showSearch("all");
    TextField searchField = (TextField) mainScene.lookup("#search");
    searchField.editableProperty().setValue(true);
    searchField.onActionProperty();
    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
      if ("".equals(newValue)) {
        showSearch("all");
      } else {
        showSearch(newValue);
      }
    });
  }

  /**
   * Show search results from the server.
   * And update current
   *
   * @param query search query
   */
  private void showSearch(String query) {
    merchandises = controller.searchMerchant(query);
    mainScreen = () -> {
      setActions(GuiActions.MAIN, menu);
      showSearch("all");
    };
    List<String> viewList = new ArrayList<>();
    JsonParser parser = new JsonParser();
    for (int i = 0; i < merchandises.size(); i++) {
      JsonObject element = parser.parse(merchandises.get(i)).getAsJsonObject();
      StringBuilder builder = new StringBuilder();
      builder.append("class: ")
              .append(element.get("class").getAsString())
              .append(" name: ")
              .append(element.get("name").getAsString())
              .append(" price: ")
              .append(element.get("price").getAsString());
      viewList.add(builder.toString());
    }
    showList(viewList);
    Platform.runLater(() -> mainList.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2 && !merchandises.isEmpty()) {
        openMerchandise(merchandises.get(mainList.getSelectionModel().getSelectedIndex()));
        setActions(GuiActions.MERCHANDISE, menu);
        lastScreen = () -> {
          setActions(GuiActions.MAIN, menu);
          showSearch("all");
        };
      }
    }));
  }

  /**
   * Method to do something by selected item in menu.
   *
   * @param selectedItem string of selected item
   */
  private void action(String selectedItem) {
    if (selectedItem.equals("Go to profile")) {
      currentMenu = GuiActions.PROFILE;
      setActions(currentMenu, menu);
      showDeals();
      lastScreen = () -> {
        setActions(GuiActions.MAIN, menu);
        showSearch("all");
      };
    }
    if (selectedItem.equals("Add merchandise")) {
      startAddingMerchandise();
    }
    if (selectedItem.equals("Exit")) {
      Platform.exit();
    }
    if (selectedItem.equals("Get all deals")) {
      showDeals();
    }
    if (selectedItem.equals("Go back")) {
      lastScreen.apply();
//      setActions(mainMenu, menu);
//      currentMenu = mainMenu;
//      setMainListListeners();
//      showSearch("all");
    }
    if (selectedItem.equals("Show Merchandises")) {
      lastScreen = () -> {
        setActions(GuiActions.MAIN, menu);
        showSearch("all");
      };
      showSearch("all");
//      showList(controller.searchMerchant("all"));
    }
    if (selectedItem.equals("Change password")) {
      generateForm(
              Arrays.asList(
                      "Current password",
                      "New password",
                      "Submit new password"),
              Arrays.asList(true, true, true),
              (map) -> {
                Boolean good = true;
                for (String key : map.keySet()) {
                  if (!checkFields(map.get(key))) {
                    good = false;
                  }
                }
                if (good) {
                  if (map.get("Current password").getText().trim().equals(password)) {
                    if (map.get("New password").getText().trim().equals(map.get("Submit new password").getText().trim())) {
                      controller.changePassword(username, map.get("New password").getText().trim(), token);
                      Util.runAlert(Alert.AlertType.INFORMATION,
                              "success",
                              "password successfully changed",
                              "");
                    } else {
                      Util.runAlert(Alert.AlertType.ERROR,
                              "error",
                              "passwords does not match",
                              "");
                    }
                  } else {
                    Util.runAlert(Alert.AlertType.ERROR,
                            "error",
                            "Wrong current password",
                            "");
                  }
                }
              },
              "Please enter this fields to change your password");
    }
    if (selectedItem.equals("Change login")) {
      generateForm(
              Arrays.asList(
                      "Current password",
                      "New login"),
              Arrays.asList(true, false),
              (map) -> {
                Boolean good = true;
                for (String key : map.keySet()) {
                  if (!checkFields(map.get(key))) {
                    good = false;
                  }
                }
                if (good) {
                  if (map.get("Current password").getText().trim().equals(password)) {
                    if (controller.changeLogin(username, map.get("New login").getText().trim(), token)) {
                      username = map.get("New login").getText().trim();
                      controller.changePassword(username, password, token);
                      currentUser.setText("Current user: " + username);
                      Util.runAlert(Alert.AlertType.INFORMATION,
                              "success",
                              "login successfully changed",
                              "changed login to " + map.get("New login").getText().trim());

                    } else {
                      Util.runAlert(Alert.AlertType.INFORMATION,
                              "failed",
                              "Failed to change username.\n" +
                                      "chosen login already in use.", "");
                    }
                  } else {
                    Util.runAlert(Alert.AlertType.ERROR,
                            "error",
                            "Wrong current password",
                            "");
                  }
                }

              },
              "Please enter this fields to change your login");
    }
    if (selectedItem.equals("Buy merchandise")) {
      String merchandise = controller.buyMerchandise(currentMerchandise.get("id").getAsInt(), username, token);
      if (!merchandise.equals("")) {
        Util.runAlert(Alert.AlertType.INFORMATION,
                "information",
                "Merchandise successfully bought",
                "Bought merchandise with id " + currentMerchandise.get("id").getAsInt());
        openMerchandise(merchandise);
        lastScreen = mainScreen;
      }
    }
    if (selectedItem.equals("delete")) {
      controller.removeMerchant(currentMerchandise.get("id").getAsInt(), username, token);
      lastScreen.apply();
    }
    if (selectedItem.equals("Set new values")) {
      List<String> labels = new ArrayList<>();
      for (Map.Entry<String, JsonElement> entry : currentMerchandise.entrySet()) {
        if ("id".equals(entry.getKey())
                || "class".equals(entry.getKey())
                || "state".equals(entry.getKey())
                || "user".equals(entry.getKey())
                || "benefit".equals(entry.getKey())) {
          continue;
        }
        labels.add(entry.getKey());
      }
      lastScreen = () -> {
        openMerchandise(controller.getMerchantById(currentMerchandise.get("id").getAsInt()));
        lastScreen = () -> {
          mainScreen.apply();
          backButton.setVisible(false);
          lastScreen = () -> {
            setActions(GuiActions.MAIN, menu);
            showSearch("all");
          };
        };
      };
      generateForm(labels,
              labels.stream().map(s -> false).collect(toList()),
              (fieldMap) -> {
                StringBuilder builder = new StringBuilder();
                for (String key : fieldMap.keySet()) {
                  if (fieldMap.get(key).getText().trim().equals("")) {
                    continue;
                  }
//                  map.put(key, fieldMap.get(key).getText().trim());
                  builder.append(key + "=" + fieldMap.get(key).getText().trim() + " ");
                }
                controller.setValuesToMerchandise(
                        currentMerchandise.get("id").getAsInt(),
                        builder.toString().trim(),
                        username,
                        token);
                lastScreen.apply();
              },
              "Change values that you want to change");
    }
  }

  private void startAddingMerchandise() {
    List<String> classes = controller.getAvailableClasses();
    showList(classes);
    Platform.runLater(() ->
            mainList.setOnMouseClicked(event -> {
//              System.out.println(classes.get(mainList.getSelectionModel().getSelectedIndex()));
              if (event.getClickCount() == 2) {
                List<String> fields = controller
                        .getMandatoryFields(
                                classes.get(mainList.getSelectionModel().getSelectedIndex()));
                fields.add("price");
                generateForm(fields,
                        fields.stream().map(s -> false).collect(toList()),
                        (map) -> {
                          Boolean good = true;
                          Map<String, String> request = new HashMap<>();
                          for (String key : map.keySet()) {
                            if (!checkFields(map.get(key))) {
                              good = false;
                            } else {
                              if (!key.equals("price")) {
                                request.put(key.toUpperCase(), map.get(key).getText().trim());
                              }
                            }
                          }
                          if (good) {
                            try {
                              controller.addMerchandiseByMap(classes.get(mainList.getSelectionModel().getSelectedIndex()), request, username, token, Integer.parseInt(map.get("price").getText()));
                              Util.runAlert(Alert.AlertType.INFORMATION,
                                      "success",
                                      "Merchandise added successfully",
                                      "");
                              showSearch("all");
                            } catch (CreateMerchandiseException | NumberFormatException e) {
                              if (e instanceof NumberFormatException) {
                                Util.runAlert(Alert.AlertType.ERROR,
                                        "error",
                                        e.getMessage(),
                                        "");
                              }
                            }
                          }
                        }, "You must fill in all the fields to create an item");
              }
            }));
  }

  /**
   * Method to generate input form.
   *
   * @param fields    names of textFields
   * @param submitter action after submitting.
   * @param infoText  text to show under fields
   */
  private void generateForm(List<String> fields, List<Boolean> passwords, Submitter submitter, String infoText) {
    GridPane gridPane = new GridPane();
    gridPane.setAlignment(Pos.CENTER);
    gridPane.setHgap(10);
    gridPane.setVgap(10);
    gridPane.setPadding(new Insets(25, 25, 25, 25));
    Text info = new Text(infoText);
    HBox.setHgrow(gridPane, Priority.ALWAYS);
    gridPane.add(info, 0, 0, 2, 1);
    ArrayList<TextField> area = new ArrayList<>();
    for (int i = 0; i < fields.size(); i++) {
      gridPane.add(new Text(fields.get(i)), 0, i + 1);
      TextField field;
      if (passwords.get(i)) {
        field = new PasswordField();
        area.add(field);
      } else {
        field = new TextField();
        area.add(field);
      }

//      field.setMaxHeight(Double.POSITIVE_INFINITY);
//      HBox.setHgrow(field, Priority.ALWAYS);
      gridPane.add(field, 1, i + 1);
    }
    Button submitButton = new Button("Submit");
    submitButton.setOnAction(event -> {
      Map<String, TextField> map = new HashMap<>();
      for (int i = 0; i < fields.size(); i++) {
        map.put(fields.get(i), area.get(i));
      }
      submitter.submit(map);
    });
    gridPane.add(submitButton, 0, fields.size() + 1, 2, 1);
    ScrollPane pane = new ScrollPane(gridPane);
    pane.setPrefSize(mainContent.getPrefWidth(), mainContent.getPrefHeight());
    mainContent.getChildren().clear();
    mainContent.getChildren().addAll(pane);
  }

  /**
   * Method to show user's deals on main contentPane.
   */
  private void showDeals() {
    deals = controller.getDealsByUser(username, token);
    mainScreen = () -> {
      setActions(GuiActions.PROFILE, menu);
      showDeals();
    };
    JsonParser parser = new JsonParser();
    showList(deals.stream().map(value -> {
      JsonObject deal = parser.parse(value).getAsJsonObject();
      StringBuilder builder = new StringBuilder();
      builder.append(deal.get("date").getAsString())
              .append(" ")
              .append(deal.get("state").getAsString())
              .append(" ")
              .append(parser
                      .parse(deal.get("merchandise").getAsString())
                      .getAsJsonObject()
                      .get("name")
                      .getAsString());
      return builder.toString();
    }).collect(toList()));
    Platform.runLater(() -> mainList.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2 && !deals.isEmpty()) {
        int id = new JsonParser()
                .parse(deals.get(mainList
                        .getSelectionModel()
                        .getSelectedIndex()))
                .getAsJsonObject().get("id").getAsInt();
        openDeal(id);
        backButton.setVisible(true);
        backButton.setOnMouseClicked(event1 -> {
          showDeals();
          backButton.setVisible(false);
        });
//          lastScreen = () -> {
//            setActions(GuiActions.PROFILE, menu);
//            showDeals();
//          };
      }
    }));

  }

  /**
   * Method to show content on main contentPane.
   *
   * @param strings to show in listView
   */
  private void showList(List<String> strings) {
//    List<String> merchandises = controller.searchMerchant(query);
//    Uncomment if partitional downloading realized
//    list.setOnScrollTo(e -> {
//      if (e.getScrollTarget() >= list.getItems().size() -1) {
//        doStuff();
//      }
//    });
    if (!mainContent.getChildren().isEmpty()) {
      mainContent.getChildren().clear();
    }
    Platform.runLater(() -> {
      ObservableList<String> list = FXCollections.observableArrayList();
      list.addAll(strings);
      mainList = new ListView<>(list);
      mainContent.getChildren().add(mainList);
      mainList.prefHeightProperty().bind(mainContent.heightProperty());
      mainList.prefWidthProperty().bind(mainContent.widthProperty());
    });
  }

  /**
   * Set "actions" value to ListView
   *
   * @param actions  {@link GuiActions} enum field with actions
   * @param listView listView to commit actions
   */
  void setActions(GuiActions actions, ListView<String> listView) {
    ObservableList<String> list = FXCollections.observableArrayList();
    list.addAll(actions.getActions());
    listView.setItems(list);
  }


  private void openMerchandise(String values) {
//    lastScreen = ()->{}
    mainContent.getChildren().clear();
    setActions(GuiActions.MERCHANDISE, menu);
    JsonParser parser = new JsonParser();
    currentMerchandise = parser.parse(values).getAsJsonObject();
    VBox box = new VBox(4);
    box.setPadding(new Insets(0, 0, 0, 8));
    for (Map.Entry<String, JsonElement> entry : currentMerchandise.entrySet()) {
      Text info = new Text(entry.getKey() + ": " + entry.getValue().getAsString());
      info.setWrappingWidth(mainContent.getPrefWidth() - 8);
      box.getChildren().add(info);
      box.setPrefHeight(Region.USE_PREF_SIZE);
    }
    ScrollPane pane = new ScrollPane(box);
    pane.setPrefSize(mainContent.getPrefWidth(), mainContent.getPrefHeight());
    mainContent.getChildren().add(pane);
  }

  private void openDeal(int dealId) {
    String deal = controller.getDealById(dealId);
    mainContent.getChildren().clear();
    JsonParser parser = new JsonParser();
    JsonObject merchandise = parser.parse(deal).getAsJsonObject();
    VBox box = new VBox(4);
    box.setPadding(new Insets(0, 0, 0, 12));
    box.setAlignment(Pos.TOP_LEFT);
    box.setPrefSize(mainContent.getPrefWidth(), mainContent.getPrefHeight());
    for (Map.Entry<String, JsonElement> entry : merchandise.entrySet()) {
      if (entry.getKey().equals("merchandise")) {
        JsonObject object = parser.parse(entry.getValue().getAsString()).getAsJsonObject();
        StringBuilder builder = new StringBuilder();
        builder.append("class: ")
                .append(object.get("class").getAsString())
                .append(" name: ")
                .append(object.get("name").getAsString());
        Hyperlink merch = new Hyperlink("merchandise: " + builder.toString());
        merch.setPadding(new Insets(0, 0, 0, -2));
        merch.setOnMouseClicked(event -> {
          openMerchandise(object.toString());
          backButton.setVisible(true);
          backButton.setOnMouseClicked(event1 -> {
//            lastScreen = (() -> {
//              setActions(GuiActions.PROFILE, menu);
//              openDeal(deal);
            backButton.setOnMouseClicked(event2 -> {
              showDeals();
              backButton.setVisible(false);
            });
            setActions(GuiActions.PROFILE, menu);
            lastScreen = () -> {
              setActions(GuiActions.MAIN, menu);
              showSearch("all");
            };
//            backButton.setVisible(false);
            openDeal(parser.parse(deal).getAsJsonObject().get("id").getAsInt());
          });
        });
        box.getChildren().add(merch);
      } else {
        Text info = new Text(entry.getKey() + ": " + entry.getValue().getAsString());
        box.getChildren().add(info);
      }
    }
    mainContent.getChildren().add(box);
  }
}
