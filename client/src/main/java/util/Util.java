package util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import model.database.Logger;

public class Util {
    private static Logger logger = new Logger();

    /**
     * @param type
     * @param header
     * @param content
     * @param log
     */
    public static void runAlert(Alert.AlertType type, String header, String content, String log) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle("Error!");
            alert.setHeaderText(header);
            alert.setContentText(content);
            logger.logError(log);
            alert.show();
//      if (alert.showAndWait().get().equals(ButtonType.OK)) {
//
//        alert.close();
//      }
        });
    }
}
