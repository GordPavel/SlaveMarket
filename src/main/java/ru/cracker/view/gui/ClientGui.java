package ru.cracker.view.gui;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.cracker.controllers.ClientController;
import ru.cracker.controllers.Controller;
import ru.cracker.view.Observer;
import ru.cracker.view.View;

import java.io.IOException;
import java.util.Objects;

public class ClientGui extends Application implements View, Observer {

  private Controller controller;
  private String token = "";
  private String username = "";
  //  private Logger logger = new Logger();
  private Stage rootStage;
  private GuiActions lastMenu;
  private ListView<String> menu;

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
    Font.loadFont(getClass().getClassLoader().getResource("fonts/trench100free.ttf").toExternalForm(), 20);
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
      if (checkFields(scene)) {
        username = login.getText().trim();
        token = controller.login(login.getText().trim(), pass.getText().trim());
        if (("-1").equals(token)) {
          Util.runAlert(
                  Alert.AlertType.INFORMATION,
                  "Info",
                  "User already in",
                  "");
        }
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
      if (checkFields(scene)) {
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

  boolean checkFields(Scene scene) {
    TextField login = (TextField) scene.lookup("#loginField");
    PasswordField pass = (PasswordField) scene.lookup("#passField");
    Platform.runLater(() -> scene.setCursor(Cursor.WAIT));
    login.setStyle("-fx-border-style: none;");
    pass.setStyle("-fx-border-style: none;");
    boolean good = true;
    if (login.getText().trim().equals("")) {
      login.setStyle("-fx-border-color: red;");
      good = false;
    }
    if ("".equals(pass.getText().trim())) {
      pass.setStyle("-fx-border-color: red;");
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

  public void setupMainListeners(Scene mainScene) {
    Text loginText = (Text) mainScene.lookup("#CurrentUser");
    loginText.setText(username);
    menu = (ListView<String>) mainScene.lookup("#actions");
    setActions(GuiActions.MAIN, menu);
    lastMenu = GuiActions.MAIN;
    menu.setOnMouseClicked(event -> {
      action(menu.getSelectionModel().getSelectedItem());
    });
  }

  /**
   * Method to do something by selected item in menu
   *
   * @param selectedItem string of selected item
   */
  private void action(String selectedItem) {
    if (selectedItem.equals("Go to profile")) {
      setActions(GuiActions.PROFILE, menu);
    }
    if (selectedItem.equals("Add merchandise")) {

    }
    if (selectedItem.equals("Exit")) {
      Platform.exit();
    }
    if (selectedItem.equals("Get all deals")) {

    }
    if (selectedItem.equals("Go back")) {
      setActions(lastMenu, menu);
    }
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
}
