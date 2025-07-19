<%-- src/main/webapp/index.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Planifier un Rappel par E-mail</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 20px; background-color: #f4f4f4; }
        .container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #007bff; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input[type="text"],
        .form-group input[type="password"],
        .form-group textarea,
        .form-group input[type="file"] {
            width: calc(100% - 22px); /* Ajuster la largeur en fonction du padding */
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box; /* Inclut padding et border dans la largeur */
        }
        .form-group textarea { resize: vertical; min-height: 80px; }
        .form-group input[type="checkbox"] { margin-right: 5px; }
        .button-group { text-align: center; margin-top: 20px; }
        .button-group button {
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
        }
        .button-group button:hover { background-color: #0056b3; }
        .message.error { color: #dc3545; background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 10px; border-radius: 5px; margin-bottom: 15px; }
        .message.success { color: #28a745; background-color: #d4edda; border: 1px solid #c3e6cb; padding: 10px; border-radius: 5px; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Planifier un Rappel par E-mail</h1>

        <%-- Afficher les messages d'erreur ou de succès --%>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="message error">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>
        <% if (request.getAttribute("successMessage") != null) { %>
            <div class="message success">
                <%= request.getAttribute("successMessage") %>
            </div>
        <% } %>

        <form action="scheduleReminder" method="post" enctype="multipart/form-data">
            <fieldset>
                <legend>Détails du Rappel</legend>
                <div class="form-group">
                    <label for="recipientEmail">Email Destinataire:</label>
                    <input type="text" id="recipientEmail" name="recipientEmail" required>
                </div>
                <div class="form-group">
                    <label for="subject">Sujet du Rappel:</label>
                    <input type="text" id="subject" name="subject" required>
                </div>
                <div class="form-group">
                    <label for="body">Contenu du Message:</label>
                    <textarea id="body" name="body" required></textarea>
                </div>
                <div class="form-group">
                    <input type="checkbox" id="isHtml" name="isHtml">
                    <label for="isHtml">Envoyer comme HTML</label>
                </div>
                <div class="form-group">
                    <label for="attachment">Pièce Jointe (optionnel):</label>
                    <input type="file" id="attachment" name="attachment">
                </div>
                <div class="form-group">
                    <label for="dateTime">Date et Heure du Rappel (AAAA-MM-JJ HH:MM:SS):</label>
                    <input type="text" id="dateTime" name="dateTime" placeholder="Ex: 2025-07-18 10:00:00" required>
                </div>
            </fieldset>

            <div class="button-group">
                <button type="submit">Planifier le Rappel</button>
            </div>
        </form>
    </div>
</body>
</html>