package view;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import controllers.ClientController;
import controllers.Controller;
import exceptions.CreateMerchandiseException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class ClientGui extends Application implements View, Observer {

  private Controller controller;
  private String token = "";
  private String username = "";
  private String password = "";
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
    loadFonts();
    Parent content = loader.load();
    rootStage.setResizable(false);
    controller = new ClientController(this,
            ResourceBundle.getBundle("app")
                    .getString("serverAddress"),
            Integer.parseInt(ResourceBundle.getBundle("app")
                    .getString("serverPort")));
    Scene scene = new Scene(content);
    rootStage.initStyle(StageStyle.UNIFIED);
    rootStage.setScene(scene);
    setListeners(scene);
    rootStage.show();
  }

  private void loadFonts() {
    Font.loadFont(getClass().getClassLoader().getResource("fonts/trench100free.ttf").toExternalForm(), 20);
    Font.loadFont(getClass().getClassLoader().getResource("fonts/timeburnernormal.ttf").toExternalForm(), 20);
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
    Button loginButton = (Button) scene.lookup("#loginButton");
    exitButton.setOnAction(event -> Platform.exit());
    loginButton.setDefaultButton(true);
    loginButton.setOnAction(event -> {
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
      }
      Platform.runLater(() -> scene.setCursor(Cursor.DEFAULT));
    });

    Button registerButton = (Button) scene.lookup("#registerButton");
    registerButton.setOnAction(event -> {
      Platform.runLater(() -> scene.setCursor(Cursor.WAIT));
      if (checkFields(login) && checkFields(pass)) {
        Boolean register = controller.register(login.getText().trim(), pass.getText().trim());
        if (register) {
          Util.runAlert(Alert.AlertType.INFORMATION,
                  "registered",
                  "Successfully registered",
                  "Successfully registered");
        } else {
          Util.runAlert(
                  Alert.AlertType.ERROR,
                  "Registration",
                  "Can't register.\nThat username already taken\n",
                  "");
        }
      }
      Platform.runLater(() -> scene.setCursor(Cursor.DEFAULT));
    });
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
    menu = (ListView<String>) mainScene.lookup("#actions");
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
    JsonParser parser = new JsonParser();
    ObservableList<JsonObject> objects = FXCollections.observableArrayList();
    for (int i = 0; i < merchandises.size(); i++) {
      objects.add(parser.parse(merchandises.get(i)).getAsJsonObject());

    }
    TableView<JsonObject> tableView = new TableView<>();

    tableView.prefHeightProperty().bind(mainContent.heightProperty());
    tableView.prefWidthProperty().bind(mainContent.widthProperty());
    TableColumn<JsonObject, String> classColumn = new TableColumn<>("Class");
    classColumn.setMinWidth(150);
    classColumn.setCellValueFactory(object -> new SimpleStringProperty(object.getValue().get("class").getAsString()));
    classColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));

    TableColumn<JsonObject, String> priceColumn = new TableColumn<>("Price");
    priceColumn.setMinWidth(150);
    priceColumn.setCellValueFactory(object -> new SimpleStringProperty(object.getValue().get("price").getAsString()));
    priceColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));

    TableColumn<JsonObject, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setMinWidth(150);
    nameColumn.setCellValueFactory(object -> new SimpleStringProperty(object.getValue().get("name").getAsString()));
    nameColumn.prefWidthProperty().bind(tableView.widthProperty().divide(3));


    showTable(objects, tableView, new TableColumn[]{classColumn, nameColumn, priceColumn});

    tableView.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2 && !merchandises.isEmpty()) {

        openMerchandise(tableView.getItems().get(tableView.getSelectionModel().getFocusedIndex()).toString());
        setActions(GuiActions.MERCHANDISE, menu);
        lastScreen = () -> {
          setActions(GuiActions.MAIN, menu);
          showSearch("all");
        };
      }
    });

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
    }
    if (selectedItem.equals("Show Merchandises")) {
      lastScreen = () -> {
        setActions(GuiActions.MAIN, menu);
        showSearch("all");
      };
      showSearch("all");
    }
    if (selectedItem.equals("Change password")) {
      openChangePasswordForm();
    }
    if (selectedItem.equals("Change login")) {
      openChangeLoginForm();
    }
    if (selectedItem.equals("Buy merchandise")) {
      buyCurrentMerchandise();
    }
    if (selectedItem.equals("delete")) {
      controller.removeMerchant(currentMerchandise.get("id").getAsInt(), username, token);
      lastScreen.apply();
    }
    if (selectedItem.equals("Set new values")) {
      setNewValuesToMerchandise();
    }
    if (selectedItem.equals("Open settings")) {
      openSettings();
    }
  }

  private void openSettings() {
    mainContent.getChildren().clear();
    List<String> settings = Arrays.asList("Server ip address", "Server port");
    generateForm(settings,
            settings.stream()
                    .map(field -> false)
                    .collect(toList()),
            submitter -> {
              String ipPatern = "^(?:(?:\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}" +
                      "(?:\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])$";
              TextField addressField = submitter.get("Server ip address");
              TextField portField = submitter.get("Server port");
              String address = ResourceBundle.getBundle("app").getString("serverAddress");
              int port = Integer.parseInt(ResourceBundle.getBundle("app").getString("serverPort"));
              boolean allCool = false;
              if (!"".equals(addressField.getText().trim())) {
                Matcher matcher = Pattern
                        .compile(ipPatern)
                        .matcher(addressField.getText().trim());
                if (matcher.matches() &&
                        !ResourceBundle.getBundle("app")
                                .getString("serverAddress")
                                .equals(addressField.getText().trim())) {
                  address = addressField.getText().trim();
                  allCool = true;
                } else {
                  allCool = false;
                  Util.runAlert(Alert.AlertType.INFORMATION,
                          "Wrong address",
                          "Check address format",
                          "");
                }
              }
              if (!"".equals(portField.getText().trim())) {
                try {
                  port = Integer.parseInt(portField.getText().trim());
                  allCool = true;
                } catch (NumberFormatException e) {
                  allCool = false;
                  Util.runAlert(Alert.AlertType.INFORMATION,
                          "Wrong port",
                          "Port must be integer",
                          "");
                }
              }
              if (allCool) {
                boolean ping = controller.ping(address, port);
                if (ping) {
                  controller.disconnect(username, token);
                  controller = new ClientController(this, address, port);
                  token = controller.login(username, password);
                  Util.runAlert(Alert.AlertType.INFORMATION,
                          "Reconnection",
                          "Successfully reconnected",
                          "reconnected to " + address + ":" + port);
                } else {
                  controller.disconnect(username, token);
                  controller = new ClientController(this,
                          ResourceBundle.getBundle("app")
                                  .getString("serverAddress"),
                          Integer.parseInt(ResourceBundle.getBundle("app")
                                  .getString("serverPort")));
                  token = controller.login(username, password);
                  Util.runAlert(Alert.AlertType.INFORMATION,
                          "Reconnection",
                          "Can't ping chosen sever " + address + ":" + port
                                  + "\nReconnected back to default server.",
                          "reconnected back to " + address + ":" + port);
                }
              }

            }, "Settings");
  }

  /**
   * Method to set new values to current merchandise.
   */
  private void setNewValuesToMerchandise() {
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
              boolean allGood = true;
              try {
                controller.setValuesToMerchandise(
                        currentMerchandise.get("id").getAsInt(),
                        builder.toString().trim(),
                        username,
                        token);
              } catch (IllegalArgumentException e) {
                allGood = false;
                for (String key : fieldMap.keySet()) {
                  if (e.getMessage().toUpperCase().equals(key.toUpperCase())) {
                    parseNumberFields(fieldMap.get(key));
                  }
                }
              }
              if (allGood) {
                lastScreen.apply();
              }
            },
            "Change values that you want to change");
  }

  /**
   * Method to buy currently opened merchandise.
   */
  private void buyCurrentMerchandise() {
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

  /**
   * Open menu to change login.
   */
  private void openChangeLoginForm() {
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

  /**
   * Method generate form to change password
   */
  private void openChangePasswordForm() {
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

  /**
   * Open adding merchandise menu.
   */
  private void startAddingMerchandise() {
    List<String> classes = controller.getAvailableClasses();
    showList(classes);
    Platform.runLater(() ->
            mainList.setOnMouseClicked(event -> {
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
                            } catch (NumberFormatException e) {
                              parseNumberFields(map.get("price"));
                            } catch (CreateMerchandiseException e) {
                              for (String key : map.keySet()) {
                                if (e.getMessage().toUpperCase().equals(key.toUpperCase())) {
                                  parseNumberFields(map.get(key));
                                }
                              }
                            }
                          }
                        }, "You must fill in all the fields to create an item");
              }
            }));
  }

  private boolean parseNumberFields(TextField field) {
    field.setStyle("-fx-border-style: none;");
    boolean good = true;
    try {
      if (Double.parseDouble(field.getText().trim()) < 0) {
        good = false;
      }
    } catch (NumberFormatException e) {
      good = false;
    }
    if (good)
      field.setStyle("-fx-border-style: none;");
    else
      field.setStyle("-fx-border-color: red;");

    return good;
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
    ObservableList<JsonObject> dealsView = FXCollections.observableArrayList();
    deals.forEach(deal -> dealsView.add(parser.parse(deal).getAsJsonObject()));

    TableView<JsonObject> tableView = new TableView<>();

    tableView.prefHeightProperty().bind(mainContent.heightProperty());
    tableView.prefWidthProperty().bind(mainContent.widthProperty());
    TableColumn<JsonObject, String> timeColumn = new TableColumn<>("Time");
    timeColumn.setMinWidth(150);
    timeColumn.setCellValueFactory(object -> new SimpleStringProperty(object.getValue().get("date").getAsString()));
    timeColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));

    TableColumn<JsonObject, String> stateColumn = new TableColumn<>("State");
    stateColumn.setMinWidth(150);
    stateColumn.setCellValueFactory(object -> new SimpleStringProperty(object.getValue().get("state").getAsString()));
    stateColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));

    TableColumn<JsonObject, String> merchandiseColumn = new TableColumn<>("Merchandise");
    merchandiseColumn.setMinWidth(150);
    merchandiseColumn.setCellValueFactory(object -> new SimpleStringProperty(
            parser.parse(object.getValue().get("merchandise").getAsString())
                    .getAsJsonObject()
                    .get("name")
                    .getAsString()));
    merchandiseColumn.prefWidthProperty().bind(tableView.widthProperty().divide(3));

    showTable(dealsView, tableView, new TableColumn[]{timeColumn, stateColumn, merchandiseColumn});

    Platform.runLater(() -> tableView.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2 && !deals.isEmpty()) {
        int id = tableView.getItems()
                .get(tableView
                        .getSelectionModel()
                        .getSelectedIndex())
                .get("id")
                .getAsInt();


        openDeal(id);
        backButton.setVisible(true);
        backButton.setOnMouseClicked(event1 -> {
          showDeals();
          backButton.setVisible(false);
        });
//        lastScreen = () -> {
//          setActions(GuiActions.PROFILE, menu);
//          showDeals();
//        };
      }
    }));
  }

  /**
   * Method to show content on main contentPane.
   *
   * @param strings to show in listView
   */
  private void showList(List<String> strings) {
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
   * Method to show list of strings in tableView.
   *
   * @param strings   list of {@link JsonObject}s to display
   * @param columns   massive of {@link TableColumn}s for tableView
   * @param tableView tableView for displaying data
   */
  private void showTable(ObservableList<JsonObject> strings, TableView<JsonObject> tableView, TableColumn[] columns) {

    Platform.runLater(() -> {
      tableView.setItems(strings);
      tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      tableView.getColumns().addAll(columns);

      mainContent.getChildren().clear();
      mainContent.getChildren().add(tableView);
    });
  }

  /**
   * Set "actions" value to ListView.
   *
   * @param actions  {@link GuiActions} enum field with actions
   * @param listView listView to commit actions
   */
  void setActions(GuiActions actions, ListView<String> listView) {
    ObservableList<String> list = FXCollections.observableArrayList();
    list.addAll(actions.getActions());
    listView.setItems(list);
  }

  /**
   * Open merchandise details menu.
   *
   * @param values string of JSON object merchandise
   */
  private void openMerchandise(String values) {
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

  /**
   * Method to open deal details menu
   *
   * @param dealId id of deal to show
   */
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

            backButton.setOnMouseClicked(event2 -> {
              showDeals();
              backButton.setVisible(false);
            });
            setActions(GuiActions.PROFILE, menu);
            lastScreen = () -> {
              setActions(GuiActions.MAIN, menu);
              showSearch("all");
            };
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
