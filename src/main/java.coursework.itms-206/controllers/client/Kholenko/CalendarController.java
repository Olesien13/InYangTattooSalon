package controllers.client.Kholenko;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import dao.AppointmentDao;
import dao.MasterScheduleDao;
import dao.ServiceDao;
import models.Master;
import models.Service;
import utils.CurrentAppointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CalendarController {

    // определяем элементы интерфейса из fxml
    @FXML private Label infoLabel;              // метка для отображения "услуга | мастер"
    @FXML private Label monthYearLabel;         // метка для текущего месяца и года
    @FXML private GridPane calendarGrid;        // сетка для кнопок дней календаря
    @FXML private FlowPane timeSlotsContainer;  // контейнер для кнопок временных слотов
    @FXML private Label errorLabel;             // метка для вывода сообщений об ошибках
    @FXML private Button prevMonthButton;       // кнопка "предыдущий месяц"
    @FXML private Button nextMonthButton;       // кнопка "следующий месяц"
    @FXML private Button backButton;            // кнопка "назад"
    @FXML private Button nextButton;            // кнопка "далее"

    // объявляем dao-объекты для работы с базой данных
    private AppointmentDao appointmentDao;
    private MasterScheduleDao scheduleDao;
    private ServiceDao serviceDao;

    // поля для хранения идентификаторов выбранных мастера и услуги
    private int masterId;
    private int serviceId;

    // поле для хранения текущего отображаемого месяца
    private LocalDate currentDisplayMonth;

    // поле для хранения выбранной пользователем даты
    private LocalDate selectedDate;

    // константа для шага генерации временных слотов (в минутах)
    private static final int TIME_SLOT_STEP_MINUTES = 90;

    private boolean isRescheduleMode = false;
    private int rescheduleAppointmentId;

    // метод инициализации контроллера
    @FXML
    public void initialize() {

        // создаем экземпляры dao-классов для работы с базой данных
        appointmentDao = new AppointmentDao();
        scheduleDao = new MasterScheduleDao();
        serviceDao = new ServiceDao();

        // вызываем статические методы класса CurrentAppointment для получения выбранных мастера и услуги
        Master currentMaster = CurrentAppointment.getMaster();
        Service currentService = CurrentAppointment.getService();

        // проверяем, что мастер и услуга выбраны (если нет, выводим ошибку и прекращаем выполнение)
        if (currentMaster == null || currentService == null) {
            showError("Ошибка: не выбран мастер или услуга");
            return;
        }

        // сохраняем идентификаторы мастера и услуги в поля
        masterId = currentMaster.getId();
        serviceId = currentService.getId();

        // устанавливаем текст в поле infoLabel в формате "название услуги | имя мастера"
        infoLabel.setText(currentService.getName() + " | " + currentMaster.getName());

        // инициализируем поле currentDisplayMonth текущей датой
        currentDisplayMonth = LocalDate.now();

        // вызываем метод построения календаря для текущего месяца
        buildCalendar(currentDisplayMonth);

        // добавляем обработчики нажатия на кнопки переключения месяцев
        prevMonthButton.setOnAction(e -> changeMonth(-1)); // передаем в метод changeMonth значение -1
        nextMonthButton.setOnAction(e -> changeMonth(1));  // передаем в метод changeMonth значение +1
    }

    // метод для переключения месяца на заданное количество шагов (при delta = -1 - назад, при delta = 1 - вперед)
    private void changeMonth(int delta) {

        // вызываем метод plusMonths у поля currentDisplayMonth, передавая delta, и сохраняем результат
        currentDisplayMonth = currentDisplayMonth.plusMonths(delta);
        buildCalendar(currentDisplayMonth); // перестраиваем календарь для нового месяца
    }

    // метод для построения календаря на указанный месяц (параметр monthDate)
    private void buildCalendar(LocalDate monthDate) {
        calendarGrid.getChildren().clear(); // очищаем сетку от предыдущих элементов

        // создаем массив строк с названиями дней недели (понедельник - воскресенье)
        String[] weekDays = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

        // в цикле проходим по всем дням недели и создаем метки-заголовки
        for (int i = 0; i < weekDays.length; i++) {
            Label header = new Label(weekDays[i]);
            header.getStyleClass().add("calendar-header"); // добавляем css-класс для стилизации заголовка
            calendarGrid.add(header, i, 0); // добавляем метку в сетку: колонка i, строка 0
        }

        // получаем объект YearMonth для года и месяца из переданной даты
        YearMonth yearMonth = YearMonth.of(monthDate.getYear(), monthDate.getMonth());
        LocalDate firstOfMonth = yearMonth.atDay(1);  // получаем первый день месяца

        // вычисляем смещение для первой ячейки: день недели первого дня (1=пн,...,7=вс) -> сдвиг (0-6)
        int startOffset = (firstOfMonth.getDayOfWeek().getValue() + 6) % 7;
        int daysInMonth = yearMonth.lengthOfMonth(); // получаем количество дней в месяце

        // устанавливаем текст в поле monthYearLabel
        String monthName = getMonthNameNominative(monthDate);
        monthYearLabel.setText(monthName.toUpperCase(Locale.forLanguageTag("ru")) + " " + monthDate.getYear());
        int row = 1; // начинаем со строки 1 (строка 0 занята заголовками)
        int col = 0; // начинаем с колонки 0

        // заполняем пустые ячейки перед началом месяца (если первое число не понедельник)
        for (int i = 0; i < startOffset; i++) {
            calendarGrid.add(new Label(""), col++, row);
            if (col == 7) {
                col = 0;
                row++;
            }
        }

        // цикл по дням месяца
        for (int day = 1; day <= daysInMonth; day++) {

            // создаем объект LocalDate для текущего дня месяца
            LocalDate cellDate = LocalDate.of(monthDate.getYear(), monthDate.getMonth(), day);

            // создаем кнопку с текстом, равным номеру дня
            Button dayButton = new Button(String.valueOf(day));
            dayButton.getStyleClass().add("calendar-day");

            // определяем день недели (1 = понедельник, 7 = воскресенье)
            int dayOfWeek = cellDate.getDayOfWeek().getValue();

            // вызываем метод getWorkingHours у scheduleDao для получения рабочих часов мастера в этот день
            String[] hours = scheduleDao.getWorkingHours(masterId, dayOfWeek);
            boolean isWorkingDay = (hours != null); // true, если мастер работает в этот день
            boolean hasFreeSlots = false; // флаг наличия хотя бы одного свободного слота
            boolean isPast = cellDate.isBefore(LocalDate.now()); // true, если дата уже прошла

            // если дата не прошлая и день рабочий, проверяем наличие свободных слотов
            if (!isPast && isWorkingDay) {

                // форматируем дату в строку в формате yyyy-mm-dd
                String dateStr = cellDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

                // генерируем все возможные слоты для этого дня
                List<String> allSlots = generateTimeSlots(hours[0], hours[1], TIME_SLOT_STEP_MINUTES);

                // перебираем все слоты, чтобы найти хотя бы один свободный
                for (String slot : allSlots) {
                    if (appointmentDao.isSlotAvailable(masterId, dateStr, slot)) {
                        hasFreeSlots = true;
                        break; // как только нашли свободный слот, выходим из цикла
                    }
                }
            }

            // устанавливаем внешний вид и доступность кнопки в зависимости от состояния даты
            if (isPast) {
                dayButton.setDisable(true); // кнопка недоступна
                dayButton.getStyleClass().add("calendar-day-disabled"); // стиль для прошедших дат
            } else if (!isWorkingDay) {
                dayButton.setDisable(true); // кнопка недоступна
                dayButton.getStyleClass().add("calendar-day-off"); // стиль для нерабочих дней
            } else if (!hasFreeSlots) {
                dayButton.setDisable(true); // кнопка недоступна
                dayButton.getStyleClass().add("calendar-day-no-slots"); // стиль для дней без свободных слотов
            } else {

                // день доступен для выбора: добавляем обработчик нажатия
                dayButton.setOnAction(e -> onDateSelected(cellDate, dayButton));
            }

            // если дата соответствует выбранной ранее, добавляем класс выделения
            if (cellDate.equals(selectedDate)) {
                dayButton.getStyleClass().add("calendar-day-selected");
            }

            // добавляем кнопку в сетку на текущие позиции col, row
            calendarGrid.add(dayButton, col, row);
            col++;                                  // переходим к следующей колонке
            if (col == 7) {                         // если дошли до воскресенья, переходим на новую строку
                col = 0;
                row++;
            }
        }
    }

    // метод-обработчик выбора даты (вызывается при нажатии на доступную кнопку дня)
    private void onDateSelected(LocalDate date, Button button) {

        // снимаем выделение с предыдущей выбранной даты
        for (Node node : calendarGrid.getChildren()) {
            if (node instanceof Button && node.getStyleClass().contains("calendar-day-selected")) {
                node.getStyleClass().remove("calendar-day-selected");
            }
        }
        selectedDate = date;                                 // сохраняем выбранную дату в поле selectedDate
        button.getStyleClass().add("calendar-day-selected"); // добавляем выделение текущей кнопке

        // вызываем метод загрузки временных слотов для выбранной даты
        loadTimeSlotsForDate(selectedDate);

        // делаем кнопку "далее" активной
        nextButton.setDisable(false);
    }

    // метод для загрузки и отображения временных слотов для конкретной даты
    private void loadTimeSlotsForDate(LocalDate date) {
        timeSlotsContainer.getChildren().clear(); // очищаем контейнер со слотами от предыдущих кнопок

        // определяем день недели (1=пн,...,7=вс)
        int dayOfWeek = date.getDayOfWeek().getValue();

        // получаем рабочие часы мастера для этого дня
        String[] hours = scheduleDao.getWorkingHours(masterId, dayOfWeek);
        if (hours == null) {
            Label label = new Label("В этот день мастер не работает"); // мастер не работает - сообщение об ошибке
            label.setStyle("-fx-text-fill: red;");
            timeSlotsContainer.getChildren().add(label);
            return;
        }

        // форматируем дату в строку
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // генерируем список всех возможных слотов для рабочего дня
        List<String> slots = generateTimeSlots(hours[0], hours[1], TIME_SLOT_STEP_MINUTES);

        // для каждого слота создаем кнопку
        for (String slot : slots) {
            Button timeButton = new Button(slot);
            timeButton.getStyleClass().add("calendar-time");

            // проверяем доступность слота
            if (appointmentDao.isSlotAvailable(masterId, dateStr, slot)) {

                // если слот свободен, добавляем обработчик нажатия
                timeButton.setOnAction(e -> onTimeSelected(slot, dateStr));
            } else {

                // если занят, делаем кнопку недоступной и добавляем стиль занятого слота
                timeButton.setDisable(true);
                timeButton.getStyleClass().add("calendar-time-occupied");
            }
            timeSlotsContainer.getChildren().add(timeButton);
        }
    }

    // метод для генерации списка временных слотов от startTime до endTime с шагом stepMinutes
    private List<String> generateTimeSlots(String startTime, String endTime, int stepMinutes) {

        // создаем новый список для хранения слотов
        List<String> slots = new ArrayList<>();

        // преобразуем строки времени в объекты LocalTime
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        LocalTime current = start;

        // в цикле прибавляем шаг, пока текущее время не достигнет конечного
        while (current.isBefore(end)) {
            slots.add(current.toString()); // добавляем слот в формате "HH:MM"
            current = current.plusMinutes(stepMinutes);
        }
        return slots;
    }

    // метод-обработчик выбора временного слота
    private void onTimeSelected(String time, String dateStr) {
        if (isRescheduleMode) {
            // обновляем запись в БД
            boolean updated = appointmentDao.reschedule(rescheduleAppointmentId, dateStr, time);
            Stage stage = (Stage) timeSlotsContainer.getScene().getWindow();
            stage.close(); // закрываем модальное окно календаря
            if (updated) {
                // можно показать уведомление, но не обязательно
            }
        } else {
            CurrentAppointment.setSelectedDate(dateStr);
            CurrentAppointment.setSelectedTime(time);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/client.Kholenko/booking-form-view.fxml"));
                Stage stage = (Stage) timeSlotsContainer.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showError("Ошибка перехода: " + e.getMessage());
            }
        }
    }

    // метод для кнопки "назад" (возврат к выбору мастера)
    @FXML
    private void goBack() {
        try {

            // создаем загрузчик для fxml-файла выбора мастера
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client.Kholenko/master-selection-view.fxml"));
            Parent root = loader.load();

            // получаем текущее окно из сцены, в которой находится кнопка backButton
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.setScene(new Scene(root)); // устанавливаем новую сцену в текущем окне
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMonthNameNominative(LocalDate date) {
        // Создаём карту (Map), где ключ — номер месяца, значение — название.
        Map<Integer, String> monthNames = new HashMap<>();
        monthNames.put(1, "Январь");
        monthNames.put(2, "Февраль");
        monthNames.put(3, "Март");
        monthNames.put(4, "Апрель");
        monthNames.put(5, "Май");
        monthNames.put(6, "Июнь");
        monthNames.put(7, "Июль");
        monthNames.put(8, "Август");
        monthNames.put(9, "Сентябрь");
        monthNames.put(10, "Октябрь");
        monthNames.put(11, "Ноябрь");
        monthNames.put(12, "Декабрь");

        // Получаем номер месяца из переданной даты и возвращаем название.
        return monthNames.get(date.getMonthValue());
    }

    // метод для отображения сообщения об ошибке в поле errorLabel
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void setRescheduleMode(boolean mode, int appointmentId) {
        this.isRescheduleMode = mode;
        this.rescheduleAppointmentId = appointmentId;
    }
}