package models;

public class Service {
    private int id;
    private String name;
    private int durationMinutes;
    private double price;
    private String imagePath;   // путь к иконке услуги из БД

    // конструктор по умолчанию
    public Service() {}

    // конструктор со всеми полями
    public Service(int id, String name, int durationMinutes, double price, String imagePath) {
        this.id = id;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.imagePath = imagePath;
    }

    // геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}