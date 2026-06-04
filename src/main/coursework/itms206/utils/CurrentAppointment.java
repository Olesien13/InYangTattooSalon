package coursework.itms206.utils;

import coursework.itms206.models.Service;
import coursework.itms206.models.Master;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CurrentAppointment {
    private static Service selectedService;
    private static Master selectedMaster;
    private static LocalDate selectedDate;
    private static LocalTime selectedTime;
    private static String selectedDateString;   // для хранения даты в виде строки
    private static String selectedTimeString;   // для хранения времени в виде строки

    // Существующие методы (оставляем)
    public static void setService(Service s) { selectedService = s; }
    public static Service getService() { return selectedService; }
    public static void setMaster(Master m) { selectedMaster = m; }
    public static Master getMaster() { return selectedMaster; }
    public static void setDate(LocalDate d) { selectedDate = d; }
    public static LocalDate getDate() { return selectedDate; }
    public static void setTime(LocalTime t) { selectedTime = t; }
    public static LocalTime getTime() { return selectedTime; }

    // Новые методы для работы со строками (нужны для календаря и формы)
    public static void setSelectedDate(String dateStr) {
        selectedDateString = dateStr;
        // при необходимости конвертируем в LocalDate
        if (dateStr != null && !dateStr.isEmpty()) {
            selectedDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }
    public static String getSelectedDate() { return selectedDateString; }

    public static void setSelectedTime(String timeStr) {
        selectedTimeString = timeStr;
        if (timeStr != null && !timeStr.isEmpty()) {
            selectedTime = LocalTime.parse(timeStr);
        }
    }
    public static String getSelectedTime() { return selectedTimeString; }

    public static void clear() {
        selectedService = null;
        selectedMaster = null;
        selectedDate = null;
        selectedTime = null;
        selectedDateString = null;
        selectedTimeString = null;
    }
}