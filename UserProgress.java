import java.io.*;
import java.time.LocalDate;
import java.util.HashSet;

public class UserProgress {
    private int completed;
    private int pending;
    private int totalToday;
    private int completedToday;
    private int streak;
    private LocalDate lastCompletionDate;
    private static final String USER_FILE = "data/user.csv";

    public UserProgress() {
        loadProgressfromCSV();
        updateStreak();
    }

    // Load progress from CSV
    public void loadProgressfromCSV() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(USER_FILE));

            completed = Integer.parseInt(br.readLine());
            pending = Integer.parseInt(br.readLine());
            totalToday = Integer.parseInt(br.readLine());
            completedToday = Integer.parseInt(br.readLine());
            streak = Integer.parseInt(br.readLine());

            String lastDate = br.readLine().trim();
            lastCompletionDate = "null".equals(lastDate) ? null : LocalDate.parse(lastDate);
            br.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    public void deleteTask(Task t)
    {
        pending--;
        if (t.getDueDate().equals(LocalDate.now()))
            totalToday--;
    }
    // Complete a task
    public void completeTask(Task t) {
        completed++;
        pending--;
        if (t.getDueDate().equals(LocalDate.now()))
            completedToday++;
        lastCompletionDate = LocalDate.now();
    }

    // Update the streak
    public void updateStreak() {
        LocalDate today = LocalDate.now();
        if (lastCompletionDate == null) {
            streak = 0;
        } else if (today.minusDays(1).equals(lastCompletionDate) || lastCompletionDate.equals(today)) {
            streak++;
        } else {
            streak = 0;
        }
    }

    // Add a pending task
    public void addPending(Task t) {
        pending++;
        if (t.getDueDate().equals(LocalDate.now()))
            totalToday++;
    }

    // Getters for progress data
    public int getCompleted() {
        return completed;
    }

    public int getCompletedToday() {
        return completedToday;
    }

    public int getTotalToday() {
        return totalToday;
    }

    public int getPending() {
        return pending;
    }

    public int getStreak() {
        return streak;
    }

    // Save progress to CSV
    public void saveProgressToCSV() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE));

            bw.write(completed + "\n" + pending + "\n" + totalToday + "\n" + completedToday + "\n" + streak);
            bw.newLine();

            // Write last completion date, handle null
            bw.write(lastCompletionDate == null ? "null" : lastCompletionDate.toString());
            bw.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
