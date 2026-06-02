package models;

public class Consumable {
    private int id;
    private String name;
    private int quantity;
    private double price;
    private String unit;  // шт, мл, кг и т.д.

    // Конструктор по умолчанию
    public Consumable() {}

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}