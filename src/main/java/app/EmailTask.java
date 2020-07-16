package app;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailTask extends TimerTask {

    private Settings settings;
    private Settings attachmentSettings;
    Logger logger = Logger.getGlobal();

    @Override
    public void run() {
        // Loading the settings parameters
        this.settings = new Settings("settings.xml");
        this.attachmentSettings= new Settings("attach.xml");

        // Loading the time to compare
        long compareToTimeMills = System.currentTimeMillis() - (Long.parseLong(settings.get("timeToBack")) * 60000);
        String compareToTimeString = new SimpleDateFormat("HH:mm").format(new Date(compareToTimeMills));

        System.out.println(new Date());

        if (settings.get("timeToMatch").equals(compareToTimeString) || Misc.sendTestMail) {

            Platform.runLater(() -> {
                if (Misc.sendTestMail) {
                    Toast.makeToastInfo("Test Mail sending...");
                    logger.info("Test Mail sending...");
                } else {
                    Toast.makeToastInfo("Mail sending...");
                    logger.info("Mail sending...");
                }
            });


            EmailResolver emailResolver = new EmailResolver(settings.get("smtpHost"), Integer.parseInt(settings.get("smtpPort")), settings.get("smtpFrom"));
            emailResolver.setMailSmtpSslRequired(Boolean.parseBoolean(settings.get("smtpEnableSsl")));
            emailResolver.setMailSmtpSslEnable(Boolean.parseBoolean(settings.get("smtpEnableSsl")));
            emailResolver.setMailSmtpStartTlsRequired(Boolean.parseBoolean(settings.get("smtpEnableStartTls")));
            emailResolver.setMailSmtpStartTlsEnable(Boolean.parseBoolean(settings.get("smtpEnableStartTls")));
            emailResolver.setMailSmtpAuth(Boolean.parseBoolean(settings.get("smtpEnableAuth")));
            emailResolver.setMailSmtpUser(settings.get("smtpUsername"));
            emailResolver.setMailSmtpPassword(settings.getDecrypt("smtpPassword"));

            emailResolver.setMailSubject(Misc.patternsToDateTime(settings.get("emailSubject"), compareToTimeMills));
            emailResolver.setMailBody(Misc.patternsToDateTime(settings.get("emailBody"), compareToTimeMills));

            // adding attachments
            Map<String, String> attachmentList = attachmentSettings.getAll();
            for (Map.Entry<String, String> attachment: attachmentList.entrySet()) {
                try {
                    emailResolver.mailAttachmentAdd(Misc.patternsToDateTime(attachment.getValue(), compareToTimeMills));
                    System.out.println(attachment.getValue());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            // Multiple email address setup and send
            String emailAddreses = settings.get("emailTo");
            String[] emails = emailAddreses.split(";");
            System.out.println(Arrays.toString(emails));
            List<Address> addressList = new ArrayList<>();
            for (String email : emails) {
                try {
                    addressList.add(new InternetAddress(email));
                } catch (AddressException e) {
                    Platform.runLater(() -> {
                        logger.log(Level.WARNING, "File attachment exception", e);
                    });
                }
            }
            Address[] addresses = addressList.toArray(new InternetAddress[0]);
            try {
                emailResolver.sendMail(addresses);
                Platform.runLater(() -> {
                    Toast.makeToastInfo("Mail sent successfully");
                    logger.info("Mail sent successfully");
                });
            } catch (MessagingException e) {
                Platform.runLater(() -> {
                    Toast.makeToastError("Failed to send mail");
                    logger.log(Level.SEVERE, "Failed to send mail", e);
                });
            }

        }
    }
}

