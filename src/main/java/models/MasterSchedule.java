package models;

public class MasterSchedule {
    private int id;
    private int masterId;
    private int dayOfWeek;      // 1=Пн, 2=Вт, 3=Ср, 4=Чт, 5=Пт, 6=Сб, 7=Вс
    private String startTime;   // "10:00"
    private String endTime;     // "18:00"
    private String dayName;     // Для отображения (Понедельник, Вторник...)

    public MasterSchedule() {}

    // Геттеры
    public int getId() { return id; }
    public int getMasterId() { return masterId; }
    public int getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getDayName() { return dayName; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setMasterId(int masterId) { this.masterId = masterId; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setDayName(String dayName) { this.dayName = dayName; }

    // Вспомогательный метод для получения названия дня
    public static String getDayNameByNumber(int day) {
        switch (day) {
            case 1: return "Понедельник";
            case 2: return "Вторник";
            case 3: return "Среда";
            case 4: return "Четверг";
            case 5: return "Пятница";
            case 6: return "Суббота";
            case 7: return "Воскресенье";
            default: return "";
        }
    }
}