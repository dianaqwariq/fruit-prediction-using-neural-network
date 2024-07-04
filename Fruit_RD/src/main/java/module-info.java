module com.example.fruit_rd {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;


    opens com.example.fruit_rd to javafx.fxml;
    exports com.example.fruit_rd;
}