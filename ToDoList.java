import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;

public class ToDoList extends Application {
    private VBox taskListContainer;
    private TaskMap taskMap;
    private TagManager tagManager;
    private UserProgress userProgress;
    private LocalDate today;
    private BorderPane mainLayout; // Declare mainLayout as a class-level variable

    @Override
    public void start(Stage primaryStage) {
        taskMap = new TaskMap();
        tagManager = new TagManager();
        userProgress = new UserProgress();
        today = LocalDate.now();

        taskListContainer = new VBox(20);
        taskListContainer.setPadding(new Insets(20));
        taskListContainer.setStyle("-fx-background-color: #F0F0F0;");

        Button homeButton = new Button("Home");
        styleSidebarButton(homeButton);
        homeButton.setOnAction(e -> {
            mainLayout.setCenter(taskListContainer); // Go back to displaying tasks
            loadTasksForThreeDays();
        });

        Button calendarButton = new Button("Calendar");
        styleSidebarButton(calendarButton);
        calendarButton.setOnAction(e -> {
            taskListContainer.getChildren().clear();  // Clear current tasks
            // Create the DatePicker and allow the user to select a date
            DatePicker dueDatePicker = new DatePicker(today); // Preselect the current date
            dueDatePicker.setPromptText("Select Due Date");
            
            // Button to load tasks for the selected date
            Button loadTasksBtn = new Button("Load Tasks");
            loadTasksBtn.setOnAction(event -> {
                LocalDate selectedDate = dueDatePicker.getValue();  // Get the selected date after user interaction
                if (selectedDate != null) {
                    mainLayout.setCenter(taskListContainer);
                    taskListContainer.getChildren().clear();
                    displayTasksForDate(selectedDate);  // Display tasks for the selected date
                } else {
                    // Show an alert if no date is selected
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a date");
                    alert.show();
                }
            });

            VBox calendarLayout = new VBox(10, dueDatePicker, loadTasksBtn);
            calendarLayout.setPadding(new Insets(20));
            mainLayout.setCenter(calendarLayout);  // Set the new layout with the DatePicker in the center
        });

        Button addTaskButton = new Button("Add Task");
        styleSidebarButton(addTaskButton);
        addTaskButton.setOnAction(e -> openAddTaskDialog(today)); // Open add task form in the same window

        Button tagButton = new Button("Manage Tags");
        styleSidebarButton(tagButton);
        tagButton.setOnAction(e -> openManageTagsDialog());

        Button userButton = new Button("User");
        styleSidebarButton(userButton);
        userButton.setOnAction(e -> openUserDetails());

        VBox sidebar = new VBox(20, homeButton, calendarButton, addTaskButton, tagButton, userButton);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #3A3A3A;");
        sidebar.setAlignment(Pos.TOP_CENTER);

        mainLayout = new BorderPane();
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(taskListContainer);

        loadTasksForThreeDays();

        Scene scene = new Scene(mainLayout, 1024, 600);
        
        scene.setOnScroll(event -> {
            double SCROLL_TOP = 0;
            double deltaY = event.getDeltaY(); // Get the scroll amount
            double newTranslate = taskListContainer.getTranslateY() + deltaY * 0.5;
            if (newTranslate < SCROLL_TOP)
                taskListContainer.setTranslateY(newTranslate); // Adjust the position
            event.consume(); // Consume the event to prevent default scrolling
        });
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("To-Do List");

        primaryStage.setOnCloseRequest(e->{
            e.consume();
            ConfirmDialog(primaryStage);
        });

        primaryStage.show();
    }

    private void ConfirmDialog(Stage stage)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Save changes?");
        
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        ButtonType cancelButton = new ButtonType("Cancel");
        
        alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == noButton) {
                stage.close();
            }
            else if (response == yesButton){
                taskMap.saveTasksToCSV();
                userProgress.saveProgressToCSV();
                tagManager.saveTagsToCSV();
                stage.close();
            }
            else
            {

            }
        });
    }

    private void loadTasksForThreeDays() {
        taskListContainer.getChildren().clear();
        LocalDate date = today;
        
        Label dateLabel1 = new Label("Today, " + toCamelCase(date.getDayOfWeek().toString()));
        dateLabel1.setFont(Font.font(20));
        dateLabel1.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        taskListContainer.getChildren().add(dateLabel1);
        displayTasksForDateHome(date);

        date = date.plusDays(1);
        Label dateLabel2 = new Label("Tomorrow, " + toCamelCase(date.getDayOfWeek().toString()));
        dateLabel2.setFont(Font.font(20));
        dateLabel2.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        taskListContainer.getChildren().add(dateLabel2);
        displayTasksForDateHome(date);

        date = date.plusDays(1);
        Label dateLabel3 = new Label("Day after Tomorrow, " + toCamelCase(date.getDayOfWeek().toString()));
        dateLabel3.setFont(Font.font(20));
        dateLabel3.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        taskListContainer.getChildren().add(dateLabel3);
        displayTasksForDateHome(date);
    }

    private static String toCamelCase(String input) {
        StringBuilder result = new StringBuilder();
    
        result.append(input.charAt(0));
        result.append(input.toLowerCase().substring(1));
    
        return result.toString();
    }
    
    private void openManageTagsDialog() {
        ListView<Tag> tagListView = new ListView<>();
        tagListView.getItems().addAll(tagManager.getTagList());
        tagListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Set preferred width and height for the ListView
        tagListView.setPrefWidth(100);  // Set your desired width
        tagListView.setPrefHeight(20*tagManager.getTagList().size()); // Set your desired height

        tagListView.setCellFactory(lv -> new ListCell<Tag>() {        
        
        protected void updateItem(Tag tag, boolean empty) {
            super.updateItem(tag, empty);
            if (empty || tag == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox cellLayout = new HBox(10);  // Layout for each cell

            // Circle to display the tag color
                Circle colorCircle = new Circle(10, tag.getColor());

            // Label to display the tag name
                Label nameLabel = new Label(tag.getName());

                cellLayout.getChildren().addAll(colorCircle, nameLabel);
                setGraphic(cellLayout);
            }
        }
    });

        // Add Task Button
        Button rmTagBtn = new Button("Remove Tag");
        rmTagBtn.setOnAction(e -> {
            ArrayList<Tag> selectedTags = new ArrayList<>(tagListView.getSelectionModel().getSelectedItems());
            for (Tag t: selectedTags)
                tagManager.RemoveTag(t.getName());
        });

        Button addTagBtn = new Button("Add Tag");
        addTagBtn.setOnAction(e -> {
            TextField tagNameField = new TextField();
            tagNameField.setPromptText("Tag Name");
            ColorPicker tagColorField = new ColorPicker();

            Button addBtn = new Button("Add");
            addBtn.setOnAction(f -> {
                String name = tagNameField.getText();  
                Color tagcolor = tagColorField.getValue();
                tagManager.AddTag(name, tagcolor);
            });

            VBox addTagFormLayout = new VBox(20, tagNameField, tagColorField, addBtn);
            addTagFormLayout.setPadding(new Insets(10));
            mainLayout.setCenter(addTagFormLayout);
        });
        
        // Layout for task input form
        VBox tagFormLayout = new VBox(20, tagListView, rmTagBtn, addTagBtn);
        tagFormLayout.setPadding(new Insets(10));
        mainLayout.setCenter(tagFormLayout);
    }

    // Open a dialog to add a new task
    private void openAddTaskDialog(LocalDate date) {
        TextField taskNameField = new TextField();
        taskNameField.setPromptText("Task Name");
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description");
        descriptionField.setPrefSize(150, 100); // Set preferred size (width and height)
        descriptionField.setWrapText(true);
        DatePicker dueDatePicker = new DatePicker(date); // Preselect the current date
        dueDatePicker.setPromptText("Due Date");

        // Priority dropdown
        ComboBox<Task.Priority> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll(Task.Priority.HIGH, Task.Priority.MEDIUM, Task.Priority.LOW);
        priorityComboBox.setPromptText("Priority");

        ListView<Tag> tagListView = new ListView<>();
        tagListView.getItems().addAll(tagManager.getTagList());
        tagListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Set preferred width and height for the ListView
        tagListView.setPrefWidth(100);  // Set your desired width
        tagListView.setPrefHeight(20*tagManager.getTagList().size()); // Set your desired height

        tagListView.setCellFactory(lv -> new ListCell<Tag>() {
    
        protected void updateItem(Tag tag, boolean empty) {
            super.updateItem(tag, empty);
            if (empty || tag == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox cellLayout = new HBox(10);  // Layout for each cell

            // Circle to display the tag color
                Circle colorCircle = new Circle(10, tag.getColor());

            // Label to display the tag name
                Label nameLabel = new Label(tag.getName());

                cellLayout.getChildren().addAll(colorCircle, nameLabel);
                setGraphic(cellLayout);
            }
        }
    });

        // Add Task Button
        Button addTaskBtn = new Button("Add Task");
        addTaskBtn.setOnAction(e -> {
            String taskName = taskNameField.getText();
            String description = descriptionField.getText();
            LocalDate dueDate = dueDatePicker.getValue();
            Task.Priority priority = priorityComboBox.getValue();
            ArrayList<Tag> selectedTags = new ArrayList<>(tagListView.getSelectionModel().getSelectedItems());
            //System.out.println(selectedTags.toString());
            if (selectedTags.size()==0) selectedTags=null;

            if (!taskName.isEmpty() && priority != null && dueDate != null) {
                Task newTask = new Task(taskName, description, dueDate, priority, selectedTags);
                taskMap.addTask(dueDate, newTask);
                userProgress.addPending(newTask);
                //System.out.println(newTask.getTags().toString());
                //displayTasksForDate(dueDate);  // Refresh the task list
                mainLayout.setCenter(taskListContainer); // Go back to displaying tasks
                loadTasksForThreeDays();
            } else {
                // Error handling
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields");
                alert.show();
            }
        });

        // Layout for task input form
        VBox taskFormLayout = new VBox(10, taskNameField, descriptionField, dueDatePicker, priorityComboBox, tagListView, addTaskBtn);
        taskFormLayout.setPadding(new Insets(10));

        // Set the task form in the center of the main layout
        mainLayout.setCenter(taskFormLayout);
    }

    // Display tasks for a specific date
    private void displayTasksForDate(LocalDate date) {
        ArrayList<Task> tasksForDate = taskMap.getTasks(date);

        if (tasksForDate != null) {
            Label dateLabel = new Label("Tasks for " + date.toString());
            dateLabel.setFont(Font.font(18));
            dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
            taskListContainer.getChildren().add(dateLabel);

            for (Task task : tasksForDate) {
                displayTask(task);
            }
        } else {
            taskListContainer.getChildren().add(new Label("No tasks for " + date.toString()));
        }
    }

    private void displayTasksForDateHome(LocalDate date) {
        ArrayList<Task> tasksForDate = taskMap.getTasks(date);

        if (tasksForDate != null) {
            for (Task task : tasksForDate) {
                displayTask(task);
            }
        } else {
            taskListContainer.getChildren().add(new Label("No tasks for " + date.toString()));
        }
    }

    // Display an individual task with edit and delete options
    private void displayTask(Task task) {
        HBox taskBox = new HBox(10);  // Horizontal box for task details and buttons
        taskBox.setPadding(new Insets(15));
        taskBox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 1);");

        Rectangle pri = new Rectangle(10, 80);  // Width of 10 and height of 80 (adjust height as needed)
    
    // Set the fill color based on a condition
    if (task.getPriority()==Task.Priority.HIGH) {
        pri.setFill(javafx.scene.paint.Color.RED);
    } else if (task.getPriority()==Task.Priority.MEDIUM) {
        pri.setFill(javafx.scene.paint.Color.YELLOW);
    } else {
        pri.setFill(javafx.scene.paint.Color.LIGHTGREEN);
    }

        CheckBox Complete = new CheckBox();
        Complete.setAlignment(Pos.CENTER);

        VBox check = new VBox(Complete);
        check.setAlignment(Pos.CENTER_LEFT);
        check.setPadding(new Insets(10,0,0,0));

        Complete.setOnAction(event -> {
        if (Complete.isSelected()) {
            userProgress.completeTask(task);
            taskMap.removeTask(task.getDueDate(),task);
            mainLayout.setCenter(taskListContainer);
            loadTasksForThreeDays();
        }
    });
        // Task details: Name, Description, Due Date
        Label nameLabel = new Label(task.getName());
        nameLabel.setFont(Font.font(16));
        Label descriptionLabel = new Label(task.getDescription());
        Label dueDateLabel = new Label("Due: " + task.getDueDate().toString());
        VBox taskDetails = new VBox(nameLabel, descriptionLabel, dueDateLabel);
    
        HBox tagBox = new HBox(20);
        tagBox.setPadding(new Insets(10));  // Horizontal box for tags (name + color)
        tagBox.setAlignment(Pos.CENTER_RIGHT);  // Align to the right
        if (task.getTags()!=null){
        for (Tag tag : task.getTags()) {  // Assuming task.getTags() returns a list of Tag objects
            Circle tagCircle = new Circle(5);  // Circle with radius 5 for the color
            tagCircle.setFill(tag.getColor());  // Set the tag's color
            
            Label tagName = new Label(tag.getName());  // Label for the tag name
        
            HBox singleTag = new HBox(5, tagCircle, tagName);  // HBox for each tag (circle + name)
            tagBox.getChildren().add(singleTag);  // Add each tag to the tag box
        }}

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            userProgress.deleteTask(task);
            taskMap.removeTask(task.getDueDate(),task);
            mainLayout.setCenter(taskListContainer);
            loadTasksForThreeDays();
        });
    
        HBox actionButtons = new HBox(20, deleteButton);  // Horizontal box for buttons
        actionButtons.setAlignment(Pos.CENTER_RIGHT);  // Align the buttons to the right
    
        // Make sure the task details take up all available space on the left
        HBox.setHgrow(taskDetails, Priority.ALWAYS);  // Let taskDetails expand to fill space
        

        taskBox.getChildren().addAll(pri, check, taskDetails, tagBox, actionButtons);  // Add details and buttons to the main HBox
        
        taskListContainer.getChildren().add(taskBox);  // Add the entire task box to the container
    }

    private void openUserDetails()
    {
        Label streakLabel = new Label(String.valueOf(userProgress.getStreak()) + " Day Streak!");
        streakLabel.setFont(Font.font(32));
        streakLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        Label progressLabel = new Label(String.valueOf(userProgress.getCompletedToday()) + " out of " + String.valueOf(userProgress.getTotalToday()) + " tasks completed today");
        progressLabel.setFont(Font.font(24));
        double ans;
        try{
             ans = (double) userProgress.getCompletedToday()/userProgress.getTotalToday();
        } catch (ArithmeticException e) {ans=0;}
        ProgressBar prog = new ProgressBar(ans);
        prog.setPrefSize(700,30);

        Label compLabel = new Label("Total tasks completed: " + String.valueOf(userProgress.getCompleted()));
        compLabel.setFont(Font.font(16));
        Label pendLabel = new Label(String.valueOf("Total tasks pending: " + userProgress.getPending()));
        pendLabel.setFont(Font.font(16));
        VBox userLayout = new VBox(20,streakLabel,progressLabel,prog,compLabel,pendLabel);
        userLayout.setPadding(new Insets(10));

        // Set the task form in the center of the main layout
        mainLayout.setCenter(userLayout);
    }

    // Style sidebar buttons
    private void styleSidebarButton(Button button) {
        button.setStyle("-fx-background-color: #5A5A5A; -fx-text-fill: white; -fx-font-size: 14px;");
        button.setMinWidth(150);
        button.setMinHeight(40);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
