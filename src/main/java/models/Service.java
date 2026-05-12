package models;

public class Service {
    private int id;
    private String name;
    private int durationMinutes;
    private double price;
    private int masterId;      // ID мастера
    private String masterName; // Имя мастера (для отображения)

    public Service() {}

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getMasterId() { return masterId; }
    public void setMasterId(int masterId) { this.masterId = masterId; }

    public String getMasterName() { return masterName; }
    public void setMasterName(String masterName) { this.masterName = masterName; }
}