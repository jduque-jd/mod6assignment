package com.example.csc325_firebase_webview_auth.view;//package modelview;

import com.example.csc325_firebase_webview_auth.model.Person;
import com.example.csc325_firebase_webview_auth.viewmodel.AccessDataViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AccessFBView {


    public TableView<Person> tableView;
    public TableColumn<Person, String> nameColumn;
    public TableColumn<Person, String> majorColumn;
    public TableColumn<Person, Integer> ageColumn;
    private TextField emailField;
    private TextField phoneField;
    private TextField usernameField;
    private PasswordField passwordField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField ageField;
    @FXML
    private Button writeButton;
    @FXML
    private Button readButton;
    @FXML
    private TextArea outputField;
    private boolean key;
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private Person person;
    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }

    @FXML
    void initialize() {

        AccessDataViewModel accessDataViewModel = new AccessDataViewModel();
        nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
        majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());

        nameColumn.setCellValueFactory(cellData->cellData.getValue().nameProperty());
        majorColumn.setCellValueFactory(cellData->cellData.getValue().majorProperty());
        ageColumn.setCellValueFactory(cellData->cellData.getValue().ageProperty().asObject());

        tableView.setItems(listOfUsers);
    }

    @FXML
    private void addRecord(ActionEvent event) {
        addData();
    }

    @FXML
    private void readRecord(ActionEvent event) {
        readFirebase();
    }

    @FXML
    private void regRecord(ActionEvent event) {
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText().trim();
        String name = usernameField.getText().trim();
        registerUser(email, phone, password, name);
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("/files/WebContainer.fxml");
    }

    public void addData() {

        DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameField.getText());
        data.put("Major", majorField.getText());
        data.put("Age", Integer.parseInt(ageField.getText()));
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }

    public boolean readFirebase() {
        key = false;
        ApiFuture<QuerySnapshot> future = App.fstore.collection("References").get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                listOfUsers.clear(); // << moved here
                for (QueryDocumentSnapshot document : documents) {
                    Person person = new Person(
                            String.valueOf(document.getData().get("Name")),
                            String.valueOf(document.getData().get("Major")),
                            Integer.parseInt(document.getData().get("Age").toString())
                    );
                    listOfUsers.add(person); // TableView updates automatically
                }
            } else {
                System.out.println("No data");
            }
            key = true;
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return key;
    }


    public void sendVerificationEmail() {
        try {
            UserRecord user = App.fauth.getUser("name");
            //String url = user.getPassword();

        } catch (Exception e) {
        }
    }

    public boolean registerUser(String usrname, String pass, String phoneNumber, String email) {
        if(email.isEmpty() || !email.contains("@")) {
            showAlert("Invalid email address", "Please enter a valid email address");
            return false;
        }
        if(pass.length() < 6 || pass.length() > 16) {
            showAlert("Invalid password", "Please enter a valid password");
            return false;
        }
        email = email.trim();
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword(pass)
                .setPhoneNumber(phoneNumber)
                .setDisplayName(usrname)
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = App.fauth.createUser(request);
            showAlert("Success!", "User created successfully");
            return true;

        } catch (FirebaseAuthException ex) {
            showAlert("Failed to create user", ex.getMessage());
            return false;
        }

    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
