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
    private BorderPane mainLayout;

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
            mainLayout.setCenter(taskListContainer);
            loadTasksForThreeDays();
        });

        Button calendarButton = new Button("Calendar");
        styleSidebarButton(calendarButton);
        calendarButton.setOnAction(e -> {
            taskListContainer.getChildren().clear();
            DatePicker dueDatePicker = new DatePicker(today);
            dueDatePicker.setPromptText("Select Due Date");
            
            Button loadTasksBtn = new Button("Load Tasks");
            loadTasksBtn.setOnAction(event -> {
                LocalDate selectedDate = dueDatePicker.getValue();
                if (selectedDate != null) {
                    mainLayout.setCenter(taskListContainer);
                    taskListContainer.getChildren().clear();
                    displayTasksForDate(selectedDate);  
                } else {
                     
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a date");
                    alert.show();
                }
            });

            VBox calendarLayout = new VBox(10, dueDatePicker, loadTasksBtn);
            calendarLayout.setPadding(new Insets(20));
            mainLayout.setCenter(calendarLayout);   
        });

        Button addTaskButton = new Button("Add Task");
        styleSidebarButton(addTaskButton);
        addTaskButton.setOnAction(e -> openAddTaskDialog(today));  

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
            double deltaY = event.getDeltaY();  
            double newTranslate = taskListContainer.getTranslateY() + deltaY * 0.5;
            if (newTranslate < SCROLL_TOP)
                taskListContainer.setTranslateY(newTranslate);  
            event.consume();  
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
         
        tagListView.setPrefWidth(100);   
        tagListView.setPrefHeight(20*tagManager.getTagList().size());  

        tagListView.setCellFactory(lv -> new ListCell<Tag>() {        
        
        protected void updateItem(Tag tag, boolean empty) {
            super.updateItem(tag, empty);
            if (empty || tag == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox cellLayout = new HBox(10);   

             
                Circle colorCircle = new Circle(10, tag.getColor());

             
                Label nameLabel = new Label(tag.getName());

                cellLayout.getChildren().addAll(colorCircle, nameLabel);
                setGraphic(cellLayout);
            }
        }
    });

         
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
        
         
        VBox tagFormLayout = new VBox(20, tagListView, rmTagBtn, addTagBtn);
        tagFormLayout.setPadding(new Insets(10));
        mainLayout.setCenter(tagFormLayout);
    }

     
    private void openAddTaskDialog(LocalDate date) {
        TextField taskNameField = new TextField();
        taskNameField.setPromptText("Task Name");
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description");
        descriptionField.setPrefSize(150, 100);  
        descriptionField.setWrapText(true);
        DatePicker dueDatePicker = new DatePicker(date);  
        dueDatePicker.setPromptText("Due Date");

         
        ComboBox<Task.Priority> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll(Task.Priority.HIGH, Task.Priority.MEDIUM, Task.Priority.LOW);
        priorityComboBox.setPromptText("Priority");

        ListView<Tag> tagListView = new ListView<>();
        tagListView.getItems().addAll(tagManager.getTagList());
        tagListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
         
        tagListView.setPrefWidth(100);   
        tagListView.setPrefHeight(20*tagManager.getTagList().size());  

        tagListView.setCellFactory(lv -> new ListCell<Tag>() {
    
        protected void updateItem(Tag tag, boolean empty) {
            super.updateItem(tag, empty);
            if (empty || tag == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox cellLayout = new HBox(10);   

             
                Circle colorCircle = new Circle(10, tag.getColor());

             
                Label nameLabel = new Label(tag.getName());

                cellLayout.getChildren().addAll(colorCircle, nameLabel);
                setGraphic(cellLayout);
            }
        }
    });

         
        Button addTaskBtn = new Button("Add Task");
        addTaskBtn.setOnAction(e -> {
            String taskName = taskNameField.getText();
            String description = descriptionField.getText();
            LocalDate dueDate = dueDatePicker.getValue();
            Task.Priority priority = priorityComboBox.getValue();
            ArrayList<Tag> selectedTags = new ArrayList<>(tagListView.getSelectionModel().getSelectedItems());
             
            if (selectedTags.size()==0) selectedTags=null;

            if (!taskName.isEmpty() && priority != null && dueDate != null) {
                Task newTask = new Task(taskName, description, dueDate, priority, selectedTags);
                taskMap.addTask(dueDate, newTask);
                userProgress.addPending(newTask);
                 
                 
                mainLayout.setCenter(taskListContainer);  
                loadTasksForThreeDays();
            } else {
                 
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields");
                alert.show();
            }
        });

         
        VBox taskFormLayout = new VBox(10, taskNameField, descriptionField, dueDatePicker, priorityComboBox, tagListView, addTaskBtn);
        taskFormLayout.setPadding(new Insets(10));

         
        mainLayout.setCenter(taskFormLayout);
    }

     
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

     
    private void displayTask(Task task) {
        HBox taskBox = new HBox(10);   
        taskBox.setPadding(new Insets(15));
        taskBox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 1);");

        Rectangle pri = new Rectangle(10, 80);   
    
     
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
         
        Label nameLabel = new Label(task.getName());
        nameLabel.setFont(Font.font(16));
        Label descriptionLabel = new Label(task.getDescription());
        Label dueDateLabel = new Label("Due: " + task.getDueDate().toString());
        VBox taskDetails = new VBox(nameLabel, descriptionLabel, dueDateLabel);
    
        HBox tagBox = new HBox(20);
        tagBox.setPadding(new Insets(10));   
        tagBox.setAlignment(Pos.CENTER_RIGHT);   
        if (task.getTags()!=null){
        for (Tag tag : task.getTags()) {   
            Circle tagCircle = new Circle(5);   
            tagCircle.setFill(tag.getColor());   
            
            Label tagName = new Label(tag.getName());   
        
            HBox singleTag = new HBox(5, tagCircle, tagName);   
            tagBox.getChildren().add(singleTag);   
        }}

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            userProgress.deleteTask(task);
            taskMap.removeTask(task.getDueDate(),task);
            mainLayout.setCenter(taskListContainer);
            loadTasksForThreeDays();
        });
    
        HBox actionButtons = new HBox(20, deleteButton);   
        actionButtons.setAlignment(Pos.CENTER_RIGHT);   
    
         
        HBox.setHgrow(taskDetails, Priority.ALWAYS);   
        

        taskBox.getChildren().addAll(pri, check, taskDetails, tagBox, actionButtons);   
        
        taskListContainer.getChildren().add(taskBox);   
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

         
        mainLayout.setCenter(userLayout);
    }

     
    private void styleSidebarButton(Button button) {
        button.setStyle("-fx-background-color: #5A5A5A; -fx-text-fill: white; -fx-font-size: 14px;");
        button.setMinWidth(150);
        button.setMinHeight(40);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
