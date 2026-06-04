package coursework.itms206.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

// класс для отправки электронных писем
public class EmailUtils {

    // данные отправителя
    private static final String FROM_EMAIL = "olesya.holenko@yandex.ru";
    private static final String APP_PASSWORD = "rraqtbodvloffwfk";

    // отправка письма с новым паролем при восстановлении
    public static void sendPasswordResetEmail(String toEmail, String newPassword) {
        sendEmail(toEmail, "Восстановление пароля",
                "Здравствуйте!\n\nВы запросили восстановление пароля.\nВаш новый пароль: "
                        + newPassword + "\n\nС уважением, Тату-салон Инь-Янь.");
    }

    // метод отправки email
    private static void sendEmail(String toEmail, String subject, String text) {
        Properties pr = new Properties(); // настройки подключения к почтовому серверу
        pr.put("mail.smtp.host", "smtp.yandex.ru"); // сервер Яндекс
        pr.put("mail.smtp.port", "465"); // порт для SSL
        pr.put("mail.smtp.auth", "true"); // требуется авторизация
        pr.put("mail.smtp.ssl.enable", "true"); // включаем SSL
        pr.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // создаем сессию с аутентификацией
        Session session = Session.getInstance(pr, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });
        try {

            // формируем письмо
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message); // отправляем
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}