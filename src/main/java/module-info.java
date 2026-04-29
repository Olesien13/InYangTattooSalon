module org.example.inyangtattoo {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.inyangtattoo to javafx.fxml;
    exports org.example.inyangtattoo;
}