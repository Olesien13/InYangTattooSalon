package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    // хеширование пароля с помощью SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256"); // получаем экземпляр алгоритма
            byte[] hash = md.digest(password.getBytes()); // хешируем байты пароля
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b); // преобразуем каждый байт в hex
                if (hex.length() == 1) hexString.append('0'); // добавляем ведущий ноль
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка хеширования пароля", e);
        }
    }

    // проверка пароля: сравниваем хеш введенного пароля с сохраненным хешем
    public static boolean checkPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
}