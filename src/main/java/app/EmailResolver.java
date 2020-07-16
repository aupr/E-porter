package app;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmailResolver {

    private boolean mailSmtpAuth;
    private boolean mailSmtpStartTlsEnable;
    private boolean mailSmtpStartTlsRequired;
    private boolean mailSmtpSslEnable;
    private boolean mailSmtpSslRequired;
    private String mailSmtpHost;
    private int mailSmtpPort;
    private String mailSmtpUser;
    private String mailSmtpPassword;

    private String mailFromDisplayName;
    private String mailFromAddress;

    private String mailReplyToDisplayName;
    private String mailReplyToAddress;

    private String mailSubject;
    private String mailBody;

    private List<MimeBodyPart> mailAttachments;

    public EmailResolver(String mailSmtpHost, int mailSmtpPort, String mailFromAddress) {
        this.mailSmtpAuth = false;
        this.mailSmtpStartTlsEnable = true;
        this.mailSmtpStartTlsRequired = true;
        this.mailSmtpSslEnable = true;
        this.mailSmtpSslRequired = true;
        this.mailSmtpHost = mailSmtpHost;
        this.mailSmtpPort = mailSmtpPort;
        this.mailSmtpUser = "";
        this.mailSmtpPassword = "";
        this.mailFromDisplayName = "";
        this.mailFromAddress = mailFromAddress;
        this.mailReplyToDisplayName = "";
        this.mailReplyToAddress = "";
        this.mailAttachments = new ArrayList<MimeBodyPart>();
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", Boolean.toString(this.mailSmtpAuth));
        properties.put("mail.smtp.starttls.enable", Boolean.toString(this.mailSmtpStartTlsEnable));
        properties.put("mail.smtp.starttls.required", Boolean.toString(this.mailSmtpStartTlsRequired));
        properties.put("mail.smtp.ssl.required", Boolean.toString(this.mailSmtpSslRequired));
        properties.put("mail.smtp.ssl.enable", Boolean.toString(this.mailSmtpSslEnable));
        properties.put("mail.smtp.host", this.mailSmtpHost);
        properties.put("mail.smtp.port", Integer.toString(this.mailSmtpPort));
        return properties;
    }

    public void sendMail(Object mailToAddress) throws javax.mail.MessagingException {
        Session session;
        if (mailSmtpAuth) {
            final String mailSmtpUser = this.mailSmtpUser;
            final String mailSmtpPassword = this.mailSmtpPassword;
            session = Session.getInstance(getProperties(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailSmtpUser, mailSmtpPassword);
                }
            });
        } else {
            session = Session.getInstance(getProperties());
        }

        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(this.mailFromAddress));

        if (mailToAddress instanceof String) {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress((String) mailToAddress));
        } else if (mailToAddress instanceof Address[]) {
            message.setRecipients(Message.RecipientType.TO, (Address[]) mailToAddress);
        }




        message.setSubject(this.mailSubject);

        if (mailAttachments.size() > 0) {
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(this.mailBody);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);

            for(MimeBodyPart mailAttachment: mailAttachments) {
                multipart.addBodyPart(mailAttachment);
            }
            message.setContent(multipart);
        } else {
            message.setText(this.mailBody);
        }

        Transport.send(message);
    }

    public void mailAttachmentAdd(String fileLocation) throws MessagingException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        DataSource dataSource = new FileDataSource(fileLocation);
        mimeBodyPart.setDataHandler(new DataHandler(dataSource));
        mimeBodyPart.setFileName(dataSource.getName());
        this.mailAttachments.add(mimeBodyPart);
    }


    public boolean isMailSmtpAuth() {
        return mailSmtpAuth;
    }

    public void setMailSmtpAuth(boolean mailSmtpAuth) {
        this.mailSmtpAuth = mailSmtpAuth;
    }

    public boolean isMailSmtpStartTlsEnable() {
        return mailSmtpStartTlsEnable;
    }

    public void setMailSmtpStartTlsEnable(boolean mailSmtpStartTlsEnable) {
        this.mailSmtpStartTlsEnable = mailSmtpStartTlsEnable;
    }

    public boolean isMailSmtpStartTlsRequired() {
        return mailSmtpStartTlsRequired;
    }

    public void setMailSmtpStartTlsRequired(boolean mailSmtpStartTlsRequired) {
        this.mailSmtpStartTlsRequired = mailSmtpStartTlsRequired;
    }

    public String getMailSmtpHost() {
        return mailSmtpHost;
    }

    public void setMailSmtpHost(String mailSmtpHost) {
        this.mailSmtpHost = mailSmtpHost;
    }

    public int getMailSmtpPort() {
        return mailSmtpPort;
    }

    public void setMailSmtpPort(int mailSmtpPort) {
        this.mailSmtpPort = mailSmtpPort;
    }

    public String getMailSmtpUser() {
        return mailSmtpUser;
    }

    public void setMailSmtpUser(String mailSmtpUser) {
        this.mailSmtpUser = mailSmtpUser;
    }

    public String getMailSmtpPassword() {
        return mailSmtpPassword;
    }

    public void setMailSmtpPassword(String mailSmtpPassword) {
        this.mailSmtpPassword = mailSmtpPassword;
    }

    public boolean isMailSmtpSslEnable() {
        return mailSmtpSslEnable;
    }

    public void setMailSmtpSslEnable(boolean mailSmtpSslEnable) {
        this.mailSmtpSslEnable = mailSmtpSslEnable;
    }

    public boolean isMailSmtpSslRequired() {
        return mailSmtpSslRequired;
    }

    public void setMailSmtpSslRequired(boolean mailSmtpSslRequired) {
        this.mailSmtpSslRequired = mailSmtpSslRequired;
    }

    public String getMailFromDisplayName() {
        return mailFromDisplayName;
    }

    public void setMailFromDisplayName(String mailFromDisplayName) {
        this.mailFromDisplayName = mailFromDisplayName;
    }

    public String getMailFromAddress() {
        return mailFromAddress;
    }

    public void setMailFromAddress(String mailFromAddress) {
        this.mailFromAddress = mailFromAddress;
    }

    public String getMailReplyToDisplayName() {
        return mailReplyToDisplayName;
    }

    public void setMailReplyToDisplayName(String mailReplyToDisplayName) {
        this.mailReplyToDisplayName = mailReplyToDisplayName;
    }

    public String getMailReplyToAddress() {
        return mailReplyToAddress;
    }

    public void setMailReplyToAddress(String mailReplyToAddress) {
        this.mailReplyToAddress = mailReplyToAddress;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }
}
