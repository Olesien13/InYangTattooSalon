package utils;

public class UserSession {

    // статические поля
    private static int userId;   // id вошедшего пользователя
    private static String email; // email
    private static String role;  // роль

    // метод для начала сессии (вызывается после успешного входа)
    public static void start(int id, String userEmail, String userRole) {
        userId = id;
        email = userEmail;
        role = userRole;
    }

    // геттеры для получения данных текущего пользователя
    public static int getUserId() { return userId; }
    public static String getEmail() { return email; }
    public static String getRole() { return role; }
    public static boolean isLoggedIn() { return userId != 0; }
    public static boolean isAdmin() { return "admin".equals(role); }

    // очистка сессии (при выходе из аккаунта)
    public static void clear() {
        userId = 0;
        email = null;
        role = null;
    }
}