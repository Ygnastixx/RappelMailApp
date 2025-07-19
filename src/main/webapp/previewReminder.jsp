<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Prévisualisation du Rappel</title>
    <style>
        /* Styles CSS pour le mail et la page web */
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 20px; background-color: #f4f4f4; }
        .container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .header { background-color: #007bff; color: white; padding: 10px 20px; border-radius: 8px 8px 0 0; text-align: center; }
        .content { padding: 20px 0; }
        .content h2 { color: #007bff; margin-top: 0; }
        .footer { text-align: center; font-size: 0.8em; color: #777; margin-top: 20px; padding-top: 10px; border-top: 1px solid #eee; }
        .attachment { margin-top: 15px; padding: 10px; background-color: #e9ecef; border-left: 5px solid #007bff; }
        .html-content { /* Styles spécifiques si le contenu est HTML */ }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Rappel Planifié</h1>
        </div>
        <div class="content">
            <h2>Sujet: <%= request.getAttribute("subject") %></h2>
            <p><strong>Destinataire:</strong> <%= request.getAttribute("recipientEmail") %></p>
            <p><strong>Date d'envoi:</strong> <%= request.getAttribute("scheduledDateTime") %></p>

            <h3>Message:</h3>
            <% if (Boolean.TRUE.equals(request.getAttribute("isHtml"))) { %>
                <div class="html-content">
                    <%= request.getAttribute("body") %>
                </div>
            <% } else { %>
                <p style="white-space: pre-wrap;"><%= request.getAttribute("body") %></p>
            <% } %>

            <% if (request.getAttribute("attachmentFileName") != null && !((String)request.getAttribute("attachmentFileName")).isEmpty()) { %>
                <div class="attachment">
                    <strong>Pièce Jointe:</strong> <%= request.getAttribute("attachmentFileName") %>
                </div>
            <% } %>
        </div>
        <div class="footer">
            Ceci est un rappel automatique généré par notre application.
        </div>
    </div>
</body>
</html>