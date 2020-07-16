package app;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public final class Toast
{
    private static Stage ownerStage = null;

    public static Stage getOwnerStage() {
        return ownerStage;
    }

    public static void setOwnerStage(Stage ownerStage) {
        Toast.ownerStage = ownerStage;
    }

    public  static void makeToast(String toastMsg, String bgColor, String borderColor, String fontColor, int toastDelay, int fadeInDelay, int fadeOutDelay) {
        Stage ownerStage = Toast.getOwnerStage();

        Stage toastStage=new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(toastMsg);
        text.setFont(Font.font("Verdana", 18));
        text.setFill(Color.web(fontColor));

        StackPane root = new StackPane(text);
        // root.setStyle("-fx-background-radius: 3; -fx-background-color: rgba(0,160,0,1); -fx-padding: 10px 20px;");
        root.setStyle("-fx-background-radius: 3; -fx-background-color: "+ bgColor +"; -fx-padding: 10px 20px; -fx-border-width: 1px; -fx-border-style: solid; -fx-border-radius: 3px; -fx-border-color: " + borderColor);
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);

        toastStage.setX(ownerStage.getX() + 20);
        toastStage.setY(ownerStage.getY() + 350);

        toastStage.show();
        // toastStage.showAndWait();

        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(fadeInDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 1));
        fadeInTimeline.getKeyFrames().add(fadeInKey1);
        fadeInTimeline.setOnFinished((ae) ->
        {
            new Thread(() -> {
                try
                {
                    Thread.sleep(toastDelay);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Timeline fadeOutTimeline = new Timeline();
                KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(fadeOutDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0));
                fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
                fadeOutTimeline.setOnFinished((aeb) -> toastStage.close());
                fadeOutTimeline.play();
            }).start();
        });
        fadeInTimeline.play();
    }

    public static void makeToastInfo(String toastMsg)
    {
        makeToast(toastMsg, "#d1ecf1", "#bee5eb", "#0c5460", 2000, 300, 300);
    }

    public static void makeToastWarn(String toastMsg)
    {
        makeToast(toastMsg, "#fff3cd", "#ffeeba", "#856404", 2000, 300, 300);
    }

    public static void makeToastError(String toastMsg)
    {
        makeToast(toastMsg, "#f8d7da", "#f5c6cb", "#721c24", 2000, 300, 300);
    }

    public static void makeToastSuccess(String toastMsg)
    {
        makeToast(toastMsg, "#d4edda", "#c3e6cb", "#155724", 2000, 300, 300);
    }


}
