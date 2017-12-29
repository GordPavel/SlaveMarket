import controllers.Controller;
import controllers.NativeController;
import controllers.ServerController;
import model.Model;
import model.SlaveMarketModel;
import model.database.Logger;
import view.cli.CommandLineView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Start of program.
 */
public class StartServer {

  /**
   * Function to start the program.
   *
   * @param args some arguments.
   */
  public static void main(String[] args) {
    int port = 22033;
    Model model = new SlaveMarketModel();
    ServerSocket serverSocket;
    Logger logger = new Logger();
    Thread adminPanel = new Thread(() -> {
      NativeController mainController = new NativeController(model);
      CommandLineView view = new CommandLineView(model, mainController);
      view.launch();
    });
    adminPanel.start();
    try {
      serverSocket = new ServerSocket(port);
      while (!serverSocket.isClosed()) {
        Socket connection = serverSocket.accept();
        Thread client = new Thread(() -> {
          Controller controller = new ServerController(model, connection);
          controller.start();
        });
        client.start();
        logger.systemLog("Connection accepted");
      }
    } catch (IOException e) {
      logger.logError("Can't open server socket");
      System.out.println("Can't open server socket");
      System.exit(-1);
    }
  }
}
