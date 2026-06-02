package utils;

public class UserSession {

    // статические поля
    private static int userId;   // id вошедшего пользователя
    private static String email; // email
    private static String role;  // роль
    private static int selectedMasterId;
    private static int selectedServiceId;
    private static String selectedDate;
    private static String selectedTime;

    // метод для начала сессии (вызывается после успешного входа)
    public static void start(int id, String userEmail, String userRole) {
        userId = id;
        email = userEmail;
        role = userRole;
        selectedMasterId = -1;
        selectedServiceId = -1;
        selectedDate = null;
        selectedTime = null;
    }

    // геттеры для получения данных текущего пользователя
    public static int getUserId() { return userId; }
    public static String getEmail() { return email; }
    public static String getRole() { return role; }

    public static int getSelectedMasterId() { return selectedMasterId; }
    public static void setSelectedMasterId(int id) { selectedMasterId = id; }

    public static int getSelectedServiceId() { return selectedServiceId; }
    public static void setSelectedServiceId(int id) { selectedServiceId = id; }

    public static String getSelectedDate() { return selectedDate; }
    public static void setSelectedDate(String date) { selectedDate = date; }

    public static String getSelectedTime() { return selectedTime; }
    public static void setSelectedTime(String time) { selectedTime = time; }

    // очистка сессии (при выходе из аккаунта)
    public static void clear() {
        userId = 0;
        email = null;
        role = null;
        selectedMasterId = -1;
        selectedServiceId = -1;
        selectedDate = null;
        selectedTime = null;
    }
}