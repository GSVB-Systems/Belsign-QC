package dk.easv.belsign.GUI;

import com.sun.tools.javac.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class OrderSelectionController implements IParentAware {
    @FXML
    private Button btnSearch;
    @FXML
    private TextField txtSearch;

    private MainframeController mainframeController;

    private MainframeController parent;

    @Override
    public void setParent(MainframeController parent) {
        this.parent = parent;
    }

    public void initialize() {
        btnSearch.setText("🔍");

    }
    private String order;


    public void onSearchButtonClick(javafx.event.ActionEvent actionEvent) {

        order = txtSearch.getText();
        parent.setOrder(order);
        parent.fillMainPane(new FXMLLoader(getClass().getResource("/dk/easv/belsign/OperatorFrameTEMP.fxml")));
    }



}
