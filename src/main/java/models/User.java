package models;

public class User {
    private int id;              // уникальный идентификатор
    private String email;        // email
    private String passwordHash; // хеш пароля
    private String role;         // роль

    // конструктор для создания нового пользователя
    public User(int id, String email, String passwordHash, String role) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}