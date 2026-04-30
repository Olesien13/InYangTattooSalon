package dao;

import java.util.HashMap;
import java.util.Map;
import models.User;
import utils.PasswordUtils;

public class UserDao {

    // хранилище пользователей: ключ - email, значение - объект User
    private static final Map<String, User> userStorage = new HashMap<>();

    // счетчик для генерации уникальных id
    private static int idCounter = 1;

    // Метод для получения следующего id
    private static int getNextId() {
        return idCounter++;
    }

    // инициализация тестовых пользователей
    public static void testUsers() {

        // если администратор еще не добавлен - создаем
        if (!userStorage.containsKey("admin@example.com")) {
            User admin = new User(
                    getNextId(),
                    "admin@example.com",
                    PasswordUtils.hashPassword("admin123"),
                     "admin"
            );
            userStorage.put("admin@example.com", admin);
        }

        // если тестовый клиент еще не добавлен - создаем
        if (!userStorage.containsKey("client@example.com")) {
            User client = new User(
                    getNextId(),
                    "client@example.com",
                    PasswordUtils.hashPassword("client123"),
                     "client"
            );
            userStorage.put("client@example.com", client);
        }
    }

    // поиск пользователя по email
    public static User searchUser(String email) {
        return userStorage.get(email);
    }

    // создание нового пользователя
    public static boolean createUser(User user) {
        if (userStorage.containsKey(user.getEmail())) {
            return false;   // email уже используется
        }
        user.setId(getNextId()); // присваиваем новый id
        userStorage.put(user.getEmail(), user);  // добавляем в map
        return true;
    }

    // обновление хеша пароля для указанного email
    public static boolean updatePassword(String email, String newHash) {
        User user = userStorage.get(email);
        if (user == null) return false;
        user.setPasswordHash(newHash);
        return true;
    }
}