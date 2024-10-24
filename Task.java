import java.time.LocalDate;
import java.util.ArrayList;

import javafx.beans.binding.StringBinding;
import javafx.scene.paint.Color;

public class Task {
    private String name;
    private String description=null;
    private LocalDate dueDate;
    private Priority priority=null;
    private LocalDate addedDate;
    private ArrayList<Tag> tags=null;

    private static TagManager tagManager = new TagManager();

    public Task(String name, String description, LocalDate dueDate, Priority priority, ArrayList<Tag> tags, LocalDate addedDate) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.addedDate = addedDate;

        if (tags!=null)
        {
            this.tags = new ArrayList<>();

            for (Tag t:tags)
                this.addTag(t);
        }
    }

    public Task(String name, String description, LocalDate dueDate, Priority priority, ArrayList<Tag> tags) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.addedDate = LocalDate.now();
        
        if (tags!=null)
        {
            this.tags = new ArrayList<>();

            for (Tag t:tags)
                this.addTag(t);
        }
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public LocalDate getAddedDate() { return addedDate; }
    public ArrayList<Tag> getTags() { return tags; }

    public void setName(String n) {this.name = n;}
    public void setDescription(String desc) {this.description = desc;}
    public void setDueDate(LocalDate DueDate) {this.dueDate = DueDate;}
    public void setPriority(Priority p) {this.priority = p;}

    public void addTag(Tag tag) { // Method to add a tag
        if (!tags.contains(tag) && tagManager.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(Tag tag) { // Method to remove a tag
        tags.remove(tag);
    }

    public enum Priority {
        HIGH, MEDIUM, LOW;
    
        public static Priority parse(String s) {
            if (s.equals("HIGH")) return Priority.HIGH;
            else if (s.equals("MEDIUM")) return Priority.MEDIUM;
            else return Priority.LOW;
        }
        

        public String toString()
        {
            if (this==Priority.HIGH) return "HIGH";
            else if (this==Priority.MEDIUM) return "MEDIUM";
            else return "LOW";
        }
    }

    public String toString()
    {
        StringBuilder tagString = new StringBuilder();
        if (tags==null || tags.isEmpty()) tagString.append("null");
        else{
            tagString.append(tags.get(0).getName());
            for (int i=1;i<tags.size();i++)
                tagString.append("," + tags.get(i).getName());
        }
        String tagstr = tagString.toString();
        String added = addedDate.toString();
        return (name + ";" + ((description==null)?"null":description) + ";" + dueDate.toString() + ";" + ((priority==null)?"null":priority.toString()) + ";" + tagstr + ";" + added);
    }

    public Boolean Equals(Task t)
    {
        if (this.toString().equals(t.toString()))
            return true;
        return false;
    }

    public static Task fromCSV(String csvLine) {
        String[] parts = csvLine.split(";");
        if (parts.length == 6) {
            String name = parts[0];
            String desc = (parts[1].equals("null")) ? null : parts[1];
            LocalDate due = LocalDate.parse(parts[2]);
            Priority priority = (parts[3].equals("null")) ? null : Priority.parse(parts[3]);
            ArrayList<Tag> tags;
            if (parts[4].equals("null")) {
                tags = null;
            } else {
                tags = new ArrayList<>();
                String tot[] = parts[4].split(",");
                for (String s : tot) {
                    if (tagManager.find(s)!=null)
                        tags.add(tagManager.find(s));
                }
            }
            LocalDate added = LocalDate.parse(parts[5]);
    
            return new Task(name, desc, due, priority, tags, added);
        }
        return null;
    }
    
}