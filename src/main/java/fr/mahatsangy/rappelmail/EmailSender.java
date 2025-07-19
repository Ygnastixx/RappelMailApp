// src/main/java/com/example/rappelmail/EmailSender.java
package fr.mahatsangy.rappelmail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Cette classe gère l'envoi d'e-mails via SMTP.
 * Elle supporte l'envoi de messages texte ou HTML, avec ou sans pièces jointes.
 */
public class EmailSender {

    private final String username; // Adresse e-mail de l'expéditeur
    private final String password; // Mot de passe ou mot de passe d'application de l'expéditeur
    private final String host;     // Hôte SMTP (ex: smtp.gmail.com)
    private final String port;     // Port SMTP (ex: 587 pour TLS, 465 pour SSL)

    /**
     * Constructeur pour initialiser l'expéditeur d'e-mails.
     */
    public EmailSender() throws IOException {
        // Charge les propriétés depuis email.properties
        Properties appProps = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            if (input == null) {
                throw new IOException("Impossible de trouver 'email.properties' dans le classpath.");
            }
            appProps.load(input);
        }

        this.username = appProps.getProperty("sender.email");
        this.password = appProps.getProperty("sender.password");
        this.host = appProps.getProperty("smtp.host");
        this.port = appProps.getProperty("smtp.port");
    }

    /**
     * Envoie un e-mail au destinataire spécifié avec le sujet, le corps du message
     * (pouvant être HTML) et une pièce jointe optionnelle.
     *
     * @param toAddress          L'adresse e-mail du destinataire.
     * @param subject            Le sujet de l'e-mail.
     * @param body               Le corps du message (texte brut ou HTML).
     * @param isHtml             Vrai si le corps est en HTML, faux pour texte brut.
     * @param attachmentFilePath Le chemin complet vers le fichier à joindre (peut être null ou vide).
     * @throws MessagingException Si une erreur survient lors de l'envoi de l'e-mail.
     */
    public void sendEmail(String toAddress, String subject, String body, boolean isHtml, String attachmentFilePath) throws MessagingException {
        // 1. Configuration des propriétés de la session Mail
        // Ces propriétés disent à JavaMail comment se connecter au serveur SMTP.
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");             // Active l'authentification
        props.put("mail.smtp.starttls.enable", "true"); // Active STARTTLS pour la sécurité (important pour Gmail 587)
        // Si vous utilisez le port 465 (SSL/TLS direct), utilisez :
        // props.put("mail.smtp.ssl.enable", "true");
        // props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.host", this.host);               // Serveur SMTP (ex: smtp.gmail.com)
        props.put("mail.smtp.port", this.port);               // Port SMTP (ex: 587 ou 465)

        // 2. Création de la session Mail avec un Authenticator
        // L'Authenticator fournit le nom d'utilisateur et le mot de passe.
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // 3. Création du message MIME
            Message message = new MimeMessage(session);

            // Définition de l'expéditeur
            message.setFrom(new InternetAddress(username));

            // Définition du destinataire(s)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));

            // Définition du sujet
            message.setSubject(subject);

            // Création de la partie principale du message (texte ou HTML)
            BodyPart messageBodyPart = new MimeBodyPart();
            if (isHtml) {
                messageBodyPart.setContent(body, "text/html; charset=utf-8"); // Spécifier l'encodage
            } else {
                messageBodyPart.setText(body);
            }

            // Création de l'objet Multipart pour combiner les parties (texte/HTML et pièces jointes)
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Ajout de la pièce jointe si le chemin est fourni et valide
            if (attachmentFilePath != null && !attachmentFilePath.trim().isEmpty()) {
                File attachmentFile = new File(attachmentFilePath);
                if (attachmentFile.exists() && attachmentFile.isFile()) {
                    MimeBodyPart attachPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(attachmentFile);
                    attachPart.setDataHandler(new DataHandler(source));
                    attachPart.setFileName(attachmentFile.getName()); // Nom du fichier dans l'e-mail
                    multipart.addBodyPart(attachPart);
                    System.out.println("Pièce jointe ajoutée : " + attachmentFile.getName());
                } else {
                    System.err.println("Attention : Le fichier de pièce jointe spécifié n'existe pas ou n'est pas un fichier : " + attachmentFilePath);
                }
            }

            // Définition du contenu final du message comme étant multipart
            message.setContent(multipart);

            // 4. Envoi du message
            Transport.send(message);

            System.out.println("E-mail envoyé avec succès à " + toAddress + (isHtml ? " (HTML)" : " (texte simple)") +
                    (attachmentFilePath != null && !attachmentFilePath.trim().isEmpty() ? " avec pièce jointe." : "."));

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
            // Il est bon de logger la stack trace pour le débogage
            e.printStackTrace();
            throw e; // Propage l'exception pour une meilleure gestion par l'appelant
        }
    }
}
