package coursework.itms206.models;

/**
 * Модель записи (бронирования) на сеанс.
 * Хранит данные как из БД, так и дополнительные поля для отображения.
 */
public class Appointment {
    private int id;
    private int userId;
    private int masterId;
    private String masterName;      // для отображения (из masters.name)
    private int serviceId;
    private String serviceName;     // для отображения (из services.name)
    private String date;            // appointment_date
    private String time;            // appointment_time
    private String status;
    private double originalPrice;   // полная цена (из services.price)
    private double finalPrice;      // цена со скидкой (если применима)
    private String size;            // размер тату

    // конструктор по умолчанию
    public Appointment() {}

    // геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getMasterId() { return masterId; }
    public void setMasterId(int masterId) { this.masterId = masterId; }

    public String getMasterName() { return masterName; }
    public void setMasterName(String masterName) { this.masterName = masterName; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}