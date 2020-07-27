package app;

import com.sun.org.apache.xpath.internal.SourceTree;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
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
        this.attachmentSettings = new Settings("attach.xml");

        // Loading the time to compare
        long compareToTimeMills = System.currentTimeMillis() - (Long.parseLong(settings.get("timeToBack")) * 60000);
        String compareToTimeString = new SimpleDateFormat("HH:mm").format(new Date());

        System.out.println(new Date());

        if (settings.get("timeToMatch").equals(compareToTimeString) || Misc.sendTestMail) {

            Platform.runLater(() -> {
                logger.info("Getting ready to send mail...");
            });

            // setting the mailResolver object
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
                    String singleFilePath = Misc.patternsToDateTime(attachment.getValue(), compareToTimeMills);
                    File file = new File(singleFilePath);
                    if (file.isFile() && file.canRead()) {
                        emailResolver.mailAttachmentAdd(singleFilePath);
                    } else {
                        Platform.runLater(() -> {
                            logger.log(Level.WARNING, "File missing: " + singleFilePath);
                        });
                    }
                    System.out.println(singleFilePath);
                } catch (MessagingException e) {
                    Platform.runLater(() -> {
                        logger.log(Level.SEVERE, "Attachment add Exception", e);
                    });
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
                        logger.log(Level.WARNING, "Email Address exception: " + email, e);
                    });
                }
            }
            Address[] addresses = addressList.toArray(new InternetAddress[0]);


            // sending mail

            if (Misc.sendTestMail) {
                // run test mail send
                Misc.sendTestMail = false;
                try {
                    Platform.runLater(() -> {
                        Toast.makeToastInfo("Attempt to send test mail");
                        logger.info("Attempt to send test mail");
                    });
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

             } else {
                if (Misc.isValidLicense) {
                    // mail sending by schedule with retry capability
                    // it will try for 7 times to send mail with 30 minutes interval
                    // approximate maximum execution time 3 hours
                    // variable to control retry flow
                    boolean retryToSendMail = true;
                    int attemptCount = 0;

                    while (retryToSendMail) {
                        try {
                            int finalAttemptCount = ++attemptCount;
                            if (finalAttemptCount >= Integer.parseInt(settings.get("retryLimit","7")))
                                retryToSendMail = false;
                            Platform.runLater(() -> {
                                Toast.makeToastInfo("Attempt "+ finalAttemptCount +" to send mail");
                                logger.info("Attempt "+ finalAttemptCount +" to send mail");
                            });
                            emailResolver.sendMail(addresses);
                            retryToSendMail = false;
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

                        // Wait to next try if failed
                        if (retryToSendMail) {
                            for (int i = 0; i<Integer.parseInt(settings.get("retryInterval", "30")); i++) {
                                System.out.println("counting time in minute: " + i);
                                try {
                                    // One minute delay
                                    Thread.sleep(60000);
                                } catch (InterruptedException e) {
                                    Platform.runLater(() -> {
                                        logger.log(Level.WARNING, "Thread sleep exception", e);
                                    });
                                }
                            }
                        }
                    }
                } else {
                    Platform.runLater(() -> {
                        Toast.makeToastError("Failed to send mail due to license failure");
                        logger.log(Level.WARNING, "Failed to send mail due to license failure");
                    });
                }
            }
        }
    }
}

