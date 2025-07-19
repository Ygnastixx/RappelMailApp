// src/main/java/com/example/rappelmail/ReminderServlet.java

package fr.mahatsangy.rappelmail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

// Annotation pour mapper le Servlet à une URL et activer le traitement des formulaires multipart (pour les fichiers)
@WebServlet(name = "ReminderServlet", urlPatterns = "/scheduleReminder")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,   // 10MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
public class ReminderServlet extends HttpServlet {

    // Chemin temporaire pour stocker les fichiers uploadés avant l'envoi de l'email
    private static final String UPLOAD_DIR = "uploads";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupération des paramètres du formulaire
        String recipientEmail = request.getParameter("recipientEmail");
        String subject = request.getParameter("subject");
        String body = request.getParameter("body");
        boolean isHtml = "on".equalsIgnoreCase(request.getParameter("isHtml")); // Checkbox retourne "on" si cochée
        String dateTimeStr = request.getParameter("dateTime");

        File attachmentFile = null;
        String attachmentFileName = null;

        try {
            // Traitement du fichier joint (s'il y en a un)
            Part filePart = request.getPart("attachment"); // "attachment" doit correspondre au name de l'input file dans le JSP
            if (filePart != null && filePart.getSize() > 0) {
                attachmentFileName = getFileName(filePart);
                if (attachmentFileName != null && !attachmentFileName.isEmpty()) {
                    // Créer le répertoire d'upload s'il n'existe pas
                    String applicationPath = request.getServletContext().getRealPath("");
                    String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    attachmentFile = new File(uploadPath + File.separator + attachmentFileName);
                    try (InputStream fileContent = filePart.getInputStream()) {
                        Files.copy(fileContent, attachmentFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    System.out.println("Fichier " + attachmentFileName + " temporairement sauvegardé à : " + attachmentFile.getAbsolutePath());
                }
            }

            // Validation de base des entrées
            if (recipientEmail == null || recipientEmail.isEmpty() || subject == null || subject.isEmpty() ||
                    body == null || body.isEmpty() || dateTimeStr == null || dateTimeStr.isEmpty()) {
                request.setAttribute("errorMessage", "Veuillez remplir tous les champs obligatoires.");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }

            // Parsing de la date et l'heure
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime scheduledDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            Date timeToRun = Date.from(scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant());

            // Instancier EmailSender
            EmailSender emailSender = new EmailSender();

            // Planifier l'envoi de l'e-mail dans un thread séparé (pour ne pas bloquer le serveur web)
            Timer timer = new Timer();
            File finalAttachmentFile = attachmentFile; // Pour être accessible dans le TimerTask
            String finalAttachmentFileName = attachmentFileName;

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        emailSender.sendEmail(recipientEmail, subject, body, isHtml, finalAttachmentFileName);
                        System.out.println("Rappel envoyé avec succès à " + recipientEmail + " à l'heure prévue.");
                    } catch (MessagingException e) {
                        System.err.println("Erreur lors de l'envoi du rappel planifié à " + recipientEmail + ": " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        // Supprimer le fichier temporaire après l'envoi (ou l'échec d'envoi)
                        if (finalAttachmentFile != null && finalAttachmentFile.exists()) {
                            if (finalAttachmentFile.delete()) {
                                System.out.println("Fichier temporaire supprimé : " + finalAttachmentFile.getName());
                            } else {
                                System.err.println("Échec de la suppression du fichier temporaire : " + finalAttachmentFile.getName());
                            }
                        }
                    }
                }
            }, timeToRun);

            // Préparer les attributs pour la page de prévisualisation
            request.setAttribute("successMessage", "Votre rappel a été planifié avec succès pour le " + scheduledDateTime.format(formatter) + ".");
            request.setAttribute("recipientEmail", recipientEmail);
            request.setAttribute("subject", subject);
            request.setAttribute("body", body);
            request.setAttribute("isHtml", isHtml);
            request.setAttribute("scheduledDateTime", scheduledDateTime.format(formatter));
            request.setAttribute("attachmentFileName", finalAttachmentFileName);


            // Rediriger vers la page de prévisualisation/confirmation
            request.getRequestDispatcher("/previewReminder.jsp").forward(request, response);

        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Format de date et heure invalide. Veuillez utiliser AAAA-MM-JJ HH:MM:SS.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (IOException e){
            System.err.println("Erreur de configuration de l'expéditeur : " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur de configuration du service d'envoi d'e-mail, Veuillez contacter l'administrateur.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Erreur inattendue dans le ReminderServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Une erreur interne est survenue: " + e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    // Méthode utilitaire pour extraire le nom du fichier d'une partie (Part)
    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}