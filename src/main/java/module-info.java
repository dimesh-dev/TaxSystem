module com.iit.tutorials.taxsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.iit.tutorials.taxsystem to javafx.fxml;
    exports com.iit.tutorials.taxsystem;
}