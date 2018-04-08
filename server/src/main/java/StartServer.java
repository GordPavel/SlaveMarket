import controllers.Controller;
import controllers.NativeController;
import controllers.ServerController;
import model.Model;
import model.PostgresCSModel;
import model.database.Logger;
import view.cli.CommandLineView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

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
        ServerSocket serverSocket;
        Logger logger = new Logger();
        Executors.newSingleThreadExecutor()
                .execute(() -> {
                    Model model = new PostgresCSModel();
                    NativeController mainController = new NativeController(model);
                    CommandLineView view = new CommandLineView(mainController);
                    view.launch();
                });
        try {
            serverSocket = new ServerSocket(port);
            while (!serverSocket.isClosed()) {
                Socket connection = serverSocket.accept();
                Executors.newSingleThreadExecutor()
                        .execute(() -> {
                            Model model = new PostgresCSModel();
                            Controller controller = new ServerController(model, connection);
                            controller.start();
                        });
                logger.systemLog("Connection accepted");
            }
        } catch (IOException e) {
            logger.logError("Can't open server socket");
        }
    }
}
