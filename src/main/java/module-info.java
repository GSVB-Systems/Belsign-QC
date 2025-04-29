module dk.easv.belsign {
    requires javafx.controls;
    requires javafx.fxml;


    opens dk.easv.belsign to javafx.fxml;
    exports dk.easv.belsign;
}