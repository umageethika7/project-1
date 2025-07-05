package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class studentmanagementproject extends Application {

    private Connection connection;

    @Override
    public void start(Stage stage) {
        connectToDatabase();

        Label nameLabel = new Label("👤 Name:");
        nameLabel.setStyle("-fx-text-fill: #1E88E5; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label rollNoLabel = new Label("🆔 Roll No:");
        rollNoLabel.setStyle("-fx-text-fill: #8E24AA; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label phoneLabel = new Label("📞 Phone:");
        phoneLabel.setStyle("-fx-text-fill: #43A047; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label emailLabel = new Label("📧 Email:");
        emailLabel.setStyle("-fx-text-fill: #E53935; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label courseLabel = new Label("📚 Course:");
        courseLabel.setStyle("-fx-text-fill: #FB8C00; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label semesterLabel = new Label("🗓️ Semester:");
        semesterLabel.setStyle("-fx-text-fill: #6D4C41; -fx-font-size: 18px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        TextField rollNoField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField courseField = new TextField();
        TextField semesterField = new TextField();

        nameField.setPrefWidth(350);
        rollNoField.setPrefWidth(350);
        phoneField.setPrefWidth(350);
        emailField.setPrefWidth(350);
        courseField.setPrefWidth(350);
        semesterField.setPrefWidth(350);

        Button insertButton = new Button("Insert");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");

        insertButton.setPrefWidth(120);
        updateButton.setPrefWidth(120);
        deleteButton.setPrefWidth(120);

        insertButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");
        updateButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 16px;");
        deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 16px;");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        messageLabel.setWrapText(true);

        insertButton.setOnAction(e -> {
            try {
                String sql = "INSERT INTO stdmanagement (name, roll_no, course, semester, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, nameField.getText());
                statement.setString(2, rollNoField.getText());
                statement.setString(3, courseField.getText());
                statement.setString(4, semesterField.getText());
                statement.setString(5, emailField.getText());
                statement.setString(6, phoneField.getText());

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px; -fx-font-weight: bold;");
                    messageLabel.setText(
                            "✅ Student Inserted Successfully!\n" +
                                    "Name: " + nameField.getText() + "\n" +
                                    "Roll No: " + rollNoField.getText() + "\n" +
                                    "Course: " + courseField.getText() + "\n" +
                                    "Semester: " + semesterField.getText() + "\n" +
                                    "Email: " + emailField.getText() + "\n" +
                                    "Phone: " + phoneField.getText()
                    );
                } else {
                    messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
                    messageLabel.setText("❌ Failed to insert data.");
                }

                clearFields(nameField, rollNoField, phoneField, emailField, courseField, semesterField);

            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
                messageLabel.setText("❌ Error inserting data.");
            }
        });

        updateButton.setOnAction(e -> {
            try {
                String sql = "UPDATE stdmanagement SET name=?, course=?, semester=?, email=?, phone=? WHERE roll_no=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, nameField.getText());
                statement.setString(2, courseField.getText());
                statement.setString(3, semesterField.getText());
                statement.setString(4, emailField.getText());
                statement.setString(5, phoneField.getText());
                statement.setString(6, rollNoField.getText());

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px; -fx-font-weight: bold;");
                    messageLabel.setText(
                            "✅ Student Updated Successfully!\n" +
                                    "Name: " + nameField.getText() + "\n" +
                                    "Roll No: " + rollNoField.getText() + "\n" +
                                    "Course: " + courseField.getText() + "\n" +
                                    "Semester: " + semesterField.getText() + "\n" +
                                    "Email: " + emailField.getText() + "\n" +
                                    "Phone: " + phoneField.getText()
                    );
                } else {
                    messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
                    messageLabel.setText("❌ No record found for Roll No.");
                }
                clearFields(nameField, rollNoField, phoneField, emailField, courseField, semesterField);

            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
                messageLabel.setText("❌ Error updating data.");
            }
        });

        deleteButton.setOnAction(e -> {
            try {
                String sql = "DELETE FROM stdmanagement WHERE roll_no=?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, rollNoField.getText());

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px; -fx-font-weight: bold;");
                    messageLabel.setText("✅ Data deleted successfully for Roll No: " + rollNoField.getText());
                } else {
                    messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
                    messageLabel.setText("❌ No record found for Roll No.");
                }
                clearFields(nameField, rollNoField, phoneField, emailField, courseField, semesterField);

            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
                messageLabel.setText("❌ Error deleting data.");
            }
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30));
        grid.setVgap(20);
        grid.setHgap(20);
        grid.setAlignment(Pos.CENTER);

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(rollNoLabel, 0, 1);
        grid.add(rollNoField, 1, 1);

        grid.add(phoneLabel, 0, 2);
        grid.add(phoneField, 1, 2);

        grid.add(emailLabel, 0, 3);
        grid.add(emailField, 1, 3);

        grid.add(courseLabel, 0, 4);
        grid.add(courseField, 1, 4);

        grid.add(semesterLabel, 0, 5);
        grid.add(semesterField, 1, 5);

        HBox buttonBox = new HBox(20, insertButton, updateButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 1, 6);

        grid.add(messageLabel, 1, 7);

        Scene scene = new Scene(grid, 950, 750);
        stage.setScene(scene);
        stage.setTitle("Student Form - JavaFX with MySQL");
        stage.show();
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/stdmanagement?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "2507";

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to database.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Failed to connect to database.");
        }
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
