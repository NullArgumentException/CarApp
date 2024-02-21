package de.bfwbb.carapp;

import de.bfwbb.sql.SQLite;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    static TableView<Car> tblCars = new TableView<>();
    private TextField tfBrand;
    private TextField tfModel;
    private TextField tfHP;
    private TextField tfYear;
    private Car selectedCar;

    public static void main(String[] args) {

        SQLite sqLite = new SQLite();
        sqLite.connect();
        sqLite.createTable();

        Car[] cars = sqLite.readCarsFromDB();

        // vorhandene objekte aus dem car array in die table view einfügen
        tblCars.getItems().addAll(cars);

        // startet die Oberfläche
        launch(args);

        // speichert die Car-Objekte aus der TableView in ein Array
        cars = tblCars.getItems().toArray(new Car[0]);

        sqLite.writeCarsToDB(cars);

    }

    @Override
    public void start(Stage primaryStage) {

        GridPane gridPane = new GridPane();

        // Label erstellen
        Label lblTitle = new Label("Vehicle data entry");
        Label lblBrand = new Label("Brand");
        Label lblModel = new Label("Model");
        Label lblHP = new Label("HRSPRS");
        Label lblYear = new Label("Year");

        // Textfelder erstellen
        tfBrand = new TextField();
        tfModel = new TextField();
        tfHP = new TextField();
        tfYear = new TextField();

        // Knöppe erstellen
        Button btnAdd = new Button("Add");
        btnAdd.setMinWidth(50);
        btnAdd.setPrefWidth(100);
        Button btnSave = new Button("Save");
        btnSave.setMinWidth(50);
        btnSave.setPrefWidth(100);
        Button btnEdit = new Button("Edit");
        btnEdit.setMinWidth(50);
        btnEdit.setPrefWidth(100);
        Button btnDelete = new Button("Delete");
        btnDelete.setMinWidth(50);
        btnDelete.setPrefWidth(100);

        // -- Festlegungen für die Tabelle --
        tblCars.setPlaceholder(new Label("No data to display"));
        tblCars.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Spalten mit den jeweiligen Tabellenüberschriften erstellen
        TableColumn<Car, Integer> colID = new TableColumn<>("ID");
        TableColumn<Car, String> colBrand = new TableColumn<>("Brand");
        TableColumn<Car, String> colModel = new TableColumn<>("Model");
        TableColumn<Car, Integer> colHP = new TableColumn<>("HRSPRS");
        TableColumn<Car, Integer> colYear = new TableColumn<>("Year");
        colID.setMinWidth(20);
        colID.setMaxWidth(30);
        colID.setStyle("-fx-alignment: CENTER-RIGHT;");
        colBrand.setMinWidth(50);
        colModel.setMinWidth(50);
        colHP.setMinWidth(60);
        colHP.setMaxWidth(70);
        colHP.setStyle("-fx-alignment: CENTER-RIGHT;");
        colYear.setMinWidth(40);
        colYear.setMaxWidth(50);
        colYear.setStyle("-fx-alignment: CENTER-RIGHT;");

        // den Spalten die jeweiligen Klassenfelder zuordnen
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colHP.setCellValueFactory(new PropertyValueFactory<>("horsepower"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));

        // Spalten der Tabelle hinzufügen
        tblCars.getColumns().add(colID);
        tblCars.getColumns().add(colBrand);
        tblCars.getColumns().add(colModel);
        tblCars.getColumns().add(colHP);
        tblCars.getColumns().add(colYear);

        // -- Event Handling --
        // Add Button ausgrauen, wenn nicht alle Textfelder ausgefüllt sind
        btnAdd.disableProperty().bind(
                Bindings.isEmpty(tfBrand.textProperty())
                        .or(Bindings.isEmpty(tfModel.textProperty()))
                        .or(Bindings.isEmpty(tfHP.textProperty()))
                        .or(Bindings.isEmpty(tfYear.textProperty()))
        );

        // Add Button fügt neues Objekt mit den Eingaben in die Tabelle hinzu
        // Eingabefelder werden geleert
        btnAdd.setOnAction(e -> {
            tblCars.getItems().add(new Car(
                    tfBrand.getText(),
                    tfModel.getText(),
                    Integer.parseInt(tfHP.getText()),
                    Integer.parseInt(tfYear.getText())
            ));
            clearTextFields();
        });

        // Edit Button ausgrauen, wenn in der Tabelle nichts ausgewählt ist
        btnEdit.disableProperty().bind(tblCars.getSelectionModel().selectedItemProperty().isNull());


        // Add Button wird entfernt, Save Button wird angezeigt, ausgewähltes Objekt kann bearbeitet werden
        btnEdit.setOnAction(e -> {
            selectedCar = tblCars.getSelectionModel().getSelectedItem();
            tblCars.getSelectionModel().clearSelection();
            gridPane.getChildren().remove(btnAdd);
            gridPane.add(btnSave, 2, 2);
            tfBrand.setText(selectedCar.getBrand());
            tfModel.setText(selectedCar.getModel());
            tfHP.setText(Integer.toString(selectedCar.getHorsepower()));
            tfYear.setText(Integer.toString(selectedCar.getYear()));
        });

        // Save Button ausgrauen, wenn mindestens ein Textfeld leer ist
        btnSave.disableProperty().bind(
                Bindings.isEmpty(tfBrand.textProperty())
                        .or(Bindings.isEmpty(tfModel.textProperty()))
                        .or(Bindings.isEmpty(tfHP.textProperty()))
                        .or(Bindings.isEmpty(tfYear.textProperty()))
        );

        // Save Button ändert die Daten des Objekts, das beim Anklicken von "Edit" ausgewählt war
        btnSave.setOnAction(e -> {
            selectedCar.setBrand(tfBrand.getText());
            selectedCar.setModel(tfModel.getText());
            selectedCar.setHorsepower(Integer.parseInt(tfHP.getText()));
            selectedCar.setYear(Integer.parseInt(tfYear.getText()));
            gridPane.getChildren().remove(btnSave);
            gridPane.add(btnAdd, 2, 2);
            clearTextFields();
            tblCars.refresh();
        });

        // Delete Button deaktivieren, wenn nichts ausgewählt ist
        btnDelete.disableProperty().bind(tblCars.getSelectionModel().selectedItemProperty().isNull());

        // Delete Button entfernt das in der Tabelle ausgewählte Objekt
        btnDelete.setOnAction(e -> {
            tblCars.getItems().remove(tblCars.getSelectionModel().getSelectedItem());
            clearTextFields();
        });


        // legt das Eingabeformat für HP und Year fest
        intFormatter(tfHP);
        intFormatter(tfYear);

        // -- Layout --
        // Eingabefelder mit Bezeichnungen und Buttons in einem Grid Layout anlegen
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(lblTitle, 1, 0);
        gridPane.add(lblBrand, 0, 1);
        gridPane.add(tfBrand, 1, 1);
        gridPane.add(btnAdd, 2, 2);
        gridPane.add(lblModel, 0, 2);
        gridPane.add(tfModel, 1, 2);
        gridPane.add(btnEdit, 2, 3);
        gridPane.add(lblHP, 0, 3);
        gridPane.add(tfHP, 1, 3);
        gridPane.add(lblYear, 0, 4);
        gridPane.add(tfYear, 1, 4);
        gridPane.add(btnDelete, 2, 4);

        // Legt den Eingabetext fest, wenn das Textfeld leer ist
        tfBrand.setPromptText("Enter brand name");
        tfModel.setPromptText("Enter model name");
        tfHP.setPromptText("Enter a number");
        tfYear.setPromptText("Enter a year");

        // VBox Layout anlegen: gridPane und Fahrzeugtabelle als Unterobjekte zuordnen
        VBox vbox = new VBox(gridPane, tblCars);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);
        primaryStage.setTitle("de.bfwbb.carapp.Car App");
        primaryStage.getIcons().add(new Image("file:icon/car_icon.jpg"));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    /**
     * Formatiert das angegebene Textfeld so, dass nur Integer eingegeben werden können
     *
     * @param tf Das zu formatierende Textfeld
     */
    private void intFormatter(TextField tf) {
        tf.setTextFormatter(new TextFormatter<>(i -> {
            if (i.getControlNewText().matches("^\\d+$") || i.getControlNewText().isEmpty()) {
                return i;
            } else {
                return null;
            }
        }));
    }

    /**
     * Entfernt den Inhalt der Eingabefelder
     */
    private void clearTextFields() {
        tfBrand.clear();
        tfModel.clear();
        tfHP.clear();
        tfYear.clear();
    }
}