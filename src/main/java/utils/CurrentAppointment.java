package utils;

import models.Service;
import models.Master;
import java.time.LocalDate;
import java.time.LocalTime;

public class CurrentAppointment {
    private static Service selectedService;
    private static Master selectedMaster;
    private static LocalDate selectedDate;
    private static LocalTime selectedTime;

    public static void setService(Service s) { selectedService = s; }
    public static Service getService() { return selectedService; }
    public static void setMaster(Master m) { selectedMaster = m; }
    public static Master getMaster() { return selectedMaster; }
    public static void setDate(LocalDate d) { selectedDate = d; }
    public static LocalDate getDate() { return selectedDate; }
    public static void setTime(LocalTime t) { selectedTime = t; }
    public static LocalTime getTime() { return selectedTime; }

    public static void clear() {
        selectedService = null;
        selectedMaster = null;
        selectedDate = null;
        selectedTime = null;
    }
}