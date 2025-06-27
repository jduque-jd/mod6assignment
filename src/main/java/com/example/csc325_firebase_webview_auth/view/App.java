package com.example.csc325_firebase_webview_auth.view;


import com.example.csc325_firebase_webview_auth.model.FirestoreContext;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Firestore fstore;
    public static FirebaseAuth fauth;
    public static Scene scene;
    private final FirestoreContext contxtFirebase = new FirestoreContext();

    @Override
    public void start(Stage primaryStage) throws Exception {
        fstore = contxtFirebase.firebase();
        fauth = FirebaseAuth.getInstance();
        scene = new Scene(loadFXML("/files/AccessFBView.fxml"));

        //set up Splash screen
        Label title = new Label("Access FB View");
        title.setId("titleLabel");
        StackPane splashRoot = new StackPane(title);
        splashRoot.setId("splashRoot");
        Scene splashScene = new Scene(splashRoot, 894, 571);

        primaryStage.setScene(splashScene);
        primaryStage.show();

        PauseTransition pause = new PauseTransition(Duration.seconds(2));

        //set up login page
        BorderPane loginRoot = new BorderPane();
        Label loginLabel = new Label("Login");
        loginLabel.setId("loginLabel");
        VBox loginInfo = new VBox(20);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your Email");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter your Phone Number");
        TextField username = new TextField();
        username.setPromptText("Enter Your Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Enter Your Password");

        Button loginButton = new Button("Login");
        loginButton.setId("loginButton");
        loginButton.setPrefHeight(50);
        loginButton.setPrefWidth(150);

        Button registerButton = new Button("Register");
        registerButton.setId("registerButton");
        registerButton.setPrefHeight(50);
        registerButton.setPrefWidth(150);

        registerButton.setOnAction(e -> {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/AccessFBView.fxml"));
                    Parent mainRoot = loader.load();
                    AccessFBView accessFBView = loader.getController();

                    String email = emailField.getText();
                    String phone = phoneField.getText();
                    String usrname = username.getText();
                    String pass = password.getText();

                    accessFBView.registerUser(usrname, pass, phone, email);

                    scene = new Scene(mainRoot);
                    primaryStage.setScene(scene);
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
        });

        loginInfo.getChildren().addAll(loginLabel, username, password, emailField, phoneField, registerButton);
        loginRoot.setCenter(loginInfo);
        Scene loginScene = new Scene(loginRoot, 894, 571);
        scene.getStylesheets().add("src/main/resources/style.css");


        pause.setOnFinished(e-> {
            primaryStage.setScene(loginScene);
            primaryStage.show();
        });
        pause.play();


    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml ));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
