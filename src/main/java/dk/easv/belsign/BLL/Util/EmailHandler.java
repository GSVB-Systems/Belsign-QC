package dk.easv.belsign.BLL.Util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import static javax.mail.Message.RecipientType.TO;

public class EmailHandler {
    /**
     * Constructor that initializes the Gmail API service.
     * Sets up the transport, JSON factory, and credentials for API access.
     *
     * @throws GeneralSecurityException If there's an error with the security configuration
     * @throws IOException If there's an error accessing the client secrets file
     */
    public EmailHandler() throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        service = new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
                .setApplicationName("EventHub")
                .build();

    }

    private final Gmail service;                                                // Gmail API service instance
    private static final String fromEmailAddress = "eventhubticket@gmail.com";  // Sender email address



    /**
     * Gets OAuth2 credentials for Gmail API access.
     * Loads client secrets from resources, builds an authorization flow,
     * and manages user authentication through a local server.
     *
     * @param httpTransport The HTTP transport to use for requests
     * @param jsonFactory The factory for parsing JSON responses
     * @return Credential object containing the user's access and refresh tokens
     * @throws IOException If there's an error reading credentials or client secrets
     */
    public Credential getCredentials(final NetHttpTransport httpTransport, GsonFactory jsonFactory)
            throws IOException {
        // Load client secrets from resource file
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(Objects.requireNonNull(EmailHandler.class.
                getResourceAsStream("insert name of our clientsecrets file"))));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Set.of(GmailScopes.GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

    }

    /**
     * Sends an email with an attachment using the Gmail API.
     * Creates a MIME message with the specified subject, message body, and attachment,
     * then encodes and sends it via the Gmail API.
     *
     * @param subject The subject line of the email
     * @param message The plain text body of the email
     * @param attachment The file to attach to the email
     * @param toEmailAddress The recipient's email address
     * @throws Exception If there's an error creating or sending the email
     */
    public void send(String subject, String message, File attachment, String toEmailAddress) throws Exception {
        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmailAddress));
        email.addRecipient(TO, new InternetAddress(toEmailAddress));
        email.setSubject(subject);
        email.setText(message);

        // Set up multipart message with text and attachment
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(message, "text/plain");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        // Add attachment
        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(attachment);
        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName("ticket.pdf");
        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        // Encode and wrap the MIME message into a gmail message
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message msg = new Message();
        msg.setRaw(encodedEmail);

        try {
            // Send the email and log success
            Message sentMessage = service.users().messages().send("me", msg).execute();
            System.out.println("Message sent: " + sentMessage.getId());
            System.out.println(sentMessage.toPrettyString());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }
    }
}
