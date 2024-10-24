import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.paint.Color;
import javafx.util.converter.LocalDateStringConverter;

import java.io.*;

public class TaskMap {
    private HashMap<LocalDate, ArrayList<Task>> taskMap;
    private static final String TASK_FILE = "data/tasks.csv";

    public TaskMap() {
        taskMap = new HashMap<>();
        loadTasksFromCSV();
    }

    private void loadTasksFromCSV() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(TASK_FILE));
            String line;

            while (true) {
                line = br.readLine();
                if (line==null) break;
                
                Task newTask = Task.fromCSV(line);
                this.addTask(newTask.getDueDate(),newTask);
            }
            br.close();
        } 
        catch (IOException e) 
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void saveTasksToCSV() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(TASK_FILE, false));

            for (LocalDate date : taskMap.keySet()) {
                for (Task task : taskMap.get(date)) {
                    bw.write(task.toString());
                    bw.newLine();
                }
            }
            bw.close();
        } 
        catch (IOException e) 
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addTask(LocalDate date, Task task) {
        taskMap.putIfAbsent(date, new ArrayList<>());

        for (Task t: taskMap.get(date))
        {   
            if (t.Equals(task))
                return;
        }
        taskMap.get(date).add(task);
    }

    public void removeTask(LocalDate date, Task task) {
        ArrayList<Task> tasks = taskMap.get(date);
        tasks.remove(task);
        if (tasks.isEmpty())
            taskMap.remove(date);
    }
    public ArrayList<Task> getTasks(LocalDate date) {
        return taskMap.get(date);
    }

    public void displayAllTasks() {
        for (LocalDate date : taskMap.keySet()) {
            System.out.println("Tasks for " + date + ": " + taskMap.get(date));
        }
    }

    public HashMap<LocalDate, ArrayList<Task>> getMap(){
        return taskMap;
    }

    public static void main(String[] args) {
        // Create a TaskMap instance
        TaskMap taskMap = new TaskMap();

        // Adding some tasks to the map
        ArrayList<Tag> tags1 = new ArrayList<>();
        tags1.add(new Tag("Work", Color.BLUE));
        tags1.add(new Tag("Important", Color.RED));
       
        Task task1 = new Task("Complete Project", "Finish project today", LocalDate.now(), Task.Priority.HIGH, tags1, LocalDate.of(2024,10,22));
        Task task2 = new Task("Grocery Shopping", null, LocalDate.of(2024, 10, 22), Task.Priority.MEDIUM, null);

        taskMap.addTask(task1.getDueDate(), task1);
        taskMap.addTask(task2.getDueDate(), task2);

        // Display all tasks
        taskMap.displayAllTasks();
        System.out.println();
        // Save tasks to CSV
        taskMap.saveTasksToCSV();

        // Now loading tasks from the CSV to check if everything works
        TaskMap loadedTaskMap = new TaskMap();
        loadedTaskMap.displayAllTasks();
        System.out.println();

        loadedTaskMap.removeTask(task1.getDueDate(), task1);
        Task task3 = new Task("Study", null, LocalDate.of(2024,10,30), null, null);
        loadedTaskMap.addTask(task3.getDueDate(), task3);
        loadedTaskMap.saveTasksToCSV();

        TaskMap newLoadedTaskMap = new TaskMap();
        newLoadedTaskMap.displayAllTasks();
    }
}