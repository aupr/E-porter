package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("E-Mail Reporter ");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
        final Controller controller = new Controller(primaryStage);
        fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/style.css");

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new javafx.scene.image.Image("/img/EmailReporterLogo_32.PNG"));
        Platform.setImplicitExit(false);

        Toast.setOwnerStage(primaryStage);
        LogKeeper.init("logfiles/logfile");

        if (!Boolean.parseBoolean(new Settings("settings.xml").get("startSystemTray"))) {
            primaryStage.show();
        }


        primaryStage.setOnCloseRequest(event -> {
            //Platform.exit();
            ///System.exit(0);
            System.out.println("UI Closed");
        });

        //System tray start///////////////////////////////////////////////////////////////////////////////
        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        URL url = System.class.getResource("/img/EmailReporterLogo_16.png");

        Image image = Toolkit.getDefaultToolkit().getImage(url);

        final TrayIcon trayIcon = new TrayIcon(image);
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem openItem = new MenuItem("Open");

        openItem.addActionListener(e -> {
            Platform.runLater(() -> primaryStage.show());
        });

        MenuItem closeItem = new MenuItem("Close");

        closeItem.addActionListener(e -> {
            Platform.runLater(() -> {
                primaryStage.fireEvent(
                        new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)
                );
            });
        });

        MenuItem exitItem = new MenuItem("Exit");

        exitItem.addActionListener(e -> {
            Platform.runLater(() -> {
                Platform.exit();
                System.exit(0);
            });
        });

        //Add components to pop-up menu
        popup.add(openItem);
        popup.add(closeItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
        //System tray end///////////////////////////////////////////////////////////////////////////////

        // listener for duplication try and show the window

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                while (true) {
                    Socket socket = Misc.serverSocket.accept();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    if (bufferedReader.readLine().equals("email-reporter")) Platform.runLater(() -> primaryStage.show());
                }
            }
        };
        new Thread(task).start();
    }


    public static void main(String[] args) {
        // Startup preventing the multiple instance opening
        boolean isGotException = false;
        try {
            Misc.serverSocket = new ServerSocket(64567, 1, InetAddress.getLocalHost());
        } catch (IOException e) {
            isGotException = true;
        }
        if (isGotException){
            try {
                Socket socket = new Socket(InetAddress.getLocalHost(),64567);
                socket.getOutputStream().write("email-reporter\n".getBytes());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        }

        launch(args);
    }
}
