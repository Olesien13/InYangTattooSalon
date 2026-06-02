package dao;

import java.sql.*;

public class DatabaseConnection {

    // объявление константы для БД SQLite
    public static final String DB_URL = "jdbc:sqlite:database/InYangTattooSalondb.db";
    private static Connection connection = null;  // добавили статическое поле

    public static Connection getConnection() {
        if (connection == null) {  // проверяем, есть ли уже соединение
            try {
                // проверяем наличие sqlite JDBC драйвера
                Class.forName("org.sqlite.JDBC");
                System.out.println("JDBC драйвер для БД sqlite найден!");

                // создаем соединение с БД
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Соединение с БД выполнено.");

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("JDBC драйвер для БД sqlite не найден!");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Ошибка подключения к БД!");
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Отключение от БД выполнено.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Ошибка при отключении от БД!");
            }
        }
    }
}