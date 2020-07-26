package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
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

    }


    public static void main(String[] args) {
        launch(args);
    }
}
