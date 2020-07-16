package app;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Controller {
    Stage primaryStage;
    Settings settings;
    Settings attachmentSettings;
    TimerTask emailTask;
    Logger logger;
    @FXML
    Button removeAttachmentLinkBtn;
    @FXML
    TextField timeToBack, timeToMatch, smtpHost, smtpPort, smtpFrom, smtpUsername, smtpPassword, attachmentLink, emailSubject;
    @FXML
    TextArea emailTo, emailBody, logView;
    @FXML
    CheckBox smtpEnableAuth, smtpEnableSsl, smtpEnableStartTls;
    @FXML
    Label currentTimeiView;

    @FXML
    ListView<String> attachmentList;

    public Controller(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.settings = new Settings("settings.xml");
        this.attachmentSettings= new Settings("attach.xml");
        this.logger = Logger.getGlobal();
    }

    @FXML
    private void initialize() {


        // No Text Selection Allowed in Log view
        logView.setEditable(false);

        // time fields initialization
        timeToBack.setText(settings.get("timeToBack", "0"));
        timeToMatch.setText(settings.get("timeToMatch"));

        // smtp settings fields initialization
        smtpHost.setText(settings.get("smtpHost", "localhost"));
        smtpPort.setText(settings.get("smtpPort", "465"));
        smtpFrom.setText(settings.get("smtpFrom", "contact@example.com"));
        smtpEnableAuth.setSelected(Boolean.parseBoolean(settings.get("smtpEnableAuth", "false")));
        smtpEnableSsl.setSelected(Boolean.parseBoolean(settings.get("smtpEnableSsl", "false")));
        smtpEnableStartTls.setSelected(Boolean.parseBoolean(settings.get("smtpEnableStartTls", "false")));
        smtpUsername.setText(settings.get("smtpUsername"));
        smtpPassword.setText(settings.getDecrypt("smtpPassword"));

        // Email fields initialization
        emailTo.setText(settings.get("emailTo"));
        emailSubject.setText(settings.get("emailSubject"));
        emailBody.setText(settings.get("emailBody"));

        // file links listView initializations
        Map<String, String> fileLinkList = attachmentSettings.getAll();
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(fileLinkList.values());
        attachmentList.setItems(observableList);

        removeAttachmentLinkBtn.setDisable(true);

        // starting the email task
        emailTask = new EmailTask();
        Timer timer = new Timer();
        timer.schedule(emailTask, 5000, 60000);

        // running the time to gui
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                while (true) {
                    long currentTimeMills = System.currentTimeMillis();
                    // String currntTimeString = new SimpleDateFormat("HH:mm:ss").format(new Date(currentTimeMills));
                    String currntTimeString = new SimpleDateFormat("HH:mm").format(new Date(currentTimeMills));
                    Platform.runLater(() -> {
                        currentTimeiView.setText(currntTimeString);
                    });
                    Thread.sleep(1000);
                }
            }
        };

        new Thread(task).start();

    }

    public void saveSmtpSettings() {
        System.out.println("SMTP settings save button pressed.");
        settings.set("smtpHost", smtpHost.getText());
        settings.set("smtpPort", smtpPort.getText());
        settings.set("smtpFrom", smtpFrom.getText());
        settings.set("smtpEnableAuth", Boolean.toString(smtpEnableAuth.isSelected()));
        settings.set("smtpEnableSsl", Boolean.toString(smtpEnableSsl.isSelected()));
        settings.set("smtpEnableStartTls", Boolean.toString(smtpEnableStartTls.isSelected()));
        settings.set("smtpUsername", smtpUsername.getText());
        settings.setEncrypt("smtpPassword", smtpPassword.getText());

        settings.store();

        Toast.makeToastInfo("SMTP settings saved");
        logger.info("SMTP settings saved");
    }

    @FXML
    public void testAction() {

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                Misc.sendTestMail = true;
                emailTask.run();
                Misc.sendTestMail = false;
                return null;
            }
        };

        new Thread(task).start();




        /*System.out.println("Main sending in progress");
        EmailResolver emailResolver = new EmailResolver("sg2plcpnl0128.prod.sin2.secureserver.net", 465, "soud@stsvinc.com");
        emailResolver.setMailSmtpAuth(true);
        emailResolver.setMailSmtpUser("soud@stsvinc.com");
        emailResolver.setMailSmtpPassword("zBISn~lJZNtF");

        emailResolver.setMailSubject("Email resolver test with Attachment");
        emailResolver.setMailBody("Hello<br/>this is a test mail");

        emailResolver.mailAttachmentAdd("C:\\temp\\attach.txt");

        // Multiple email address setup
        String emailAddreses = "siemens.aman@gmail.com\namanullah.snet@gmail.com";
        String[] emails = emailAddreses.split("\n");
        List<Address> addressList = new ArrayList<>();
        for (String email : emails) {
            addressList.add(new InternetAddress(email));
        }

        Address[] addresses = addressList.toArray(new InternetAddress[0]);
        //System.out.println(Arrays.toString(addresses));
        emailResolver.sendMail(addresses);
        System.out.println("Mail Sent");*/
    }

    @FXML
    public void addAttachmentLink() {
        ObservableList<String> observableList;// = FXCollections.observableArrayList();

        observableList = attachmentList.getItems();
        observableList.add(attachmentLink.getText());
        attachmentList.setItems(observableList);
        int i = 0;
        attachmentSettings.clear();
        for (String value: observableList) {
            attachmentSettings.set(Integer.toString(i++), value);
        }
        attachmentSettings.store();

        Toast.makeToastInfo("Attachment link added");
        logger.info("Attachment link added");
    }

    @FXML
    public void removeAttachmentLink() {
        int indx = attachmentList.getSelectionModel().getSelectedIndex();

        ObservableList<String> observableList;
        observableList = attachmentList.getItems();
        observableList.remove(indx);
        attachmentList.setItems(observableList);
        int i = 0;
        attachmentSettings.clear();
        for (String value: observableList) {
            attachmentSettings.set(Integer.toString(i++), value);
        }
        attachmentSettings.store();

        if (observableList.isEmpty())
            removeAttachmentLinkBtn.setDisable(true);

        Toast.makeToastInfo("Attachment link removed");
        logger.info("Attachment link removed");
    }

    @FXML
    public void attachmentListMouseClick() {
        String selectedLink = attachmentList.getSelectionModel().getSelectedItem();
        if (selectedLink == null || selectedLink.isEmpty());
        else {
            removeAttachmentLinkBtn.setDisable(false);
        }
    }

    @FXML
    public void saveTimes() {
        System.out.println("Time settings save button pressed.");
        settings.set("timeToBack", timeToBack.getText());
        settings.set("timeToMatch", timeToMatch.getText());
        settings.store();
        Toast.makeToastInfo("Scheduler time settings saved");
        logger.info("Scheduler time settings saved");
    }

    @FXML
    public void saveEmail() {
        System.out.println("Email settings save button pressed...");
        settings.set("emailTo", emailTo.getText());
        settings.set("emailSubject", emailSubject.getText());
        settings.set("emailBody", emailBody.getText());
        settings.store();

        Toast.makeToastInfo("Email settings saved");
        logger.info("Email settings saved");
    }

    @FXML
    public void logReload() {
        Toast.makeToastInfo("Logfile reloaded");
        logView.setText(LogKeeper.getLog());
    }
}
