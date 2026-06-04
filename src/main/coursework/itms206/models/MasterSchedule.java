package coursework.itms206.models;

public class MasterSchedule {
    private int id;
    private int masterId;
    private int dayOfWeek;
    private String startTime;
    private String endTime;

    // Конструкторы, геттеры и сеттеры
    public MasterSchedule() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMasterId() { return masterId; }
    public void setMasterId(int masterId) { this.masterId = masterId; }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}