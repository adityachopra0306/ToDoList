import java.util.ArrayList;
import java.io.*;
import javafx.scene.paint.Color;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

public class TagManager {
    private ArrayList<Tag> taglist;
    private static final String TAG_FILE = "data/tags.csv";

    public TagManager(){
        taglist = new ArrayList<>();
        loadTagsfromCSV();
    }

    private void loadTagsfromCSV(){
        try 
        {
            BufferedReader br = new BufferedReader(new FileReader(TAG_FILE));
            String line;

            while (true)
            {
                line=br.readLine();
                if (line==null) break;
                Tag t = Tag.fromCSV(line.trim());
                if (t==null) break;
                taglist.add(t);
            }
            br.close();
        }
        catch (IOException e)
        {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    public void saveTagsToCSV()
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(TAG_FILE));
            for (Tag t:taglist)
            {
                bw.write(t.toString());
                bw.newLine();
            }
            bw.close();
        }
        catch (IOException e)
        {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    public ArrayList<Tag> getTagList(){
        return taglist;
    }

    public void AddTag(String n, Color color){
        Tag t = new Tag(n,color);
        if (!this.contains(t)){
            taglist.add(t);
            saveTagsToCSV();
        }
        else
            System.out.println("Tag already exists.");
    }

    public void RemoveTag(String n){
        Tag t = null;
        for (Tag tag:taglist)
        {
            if (tag.getName().equalsIgnoreCase(n))
            {
                t = tag;
                break;
            }
        }
        if (t==null)
            System.out.print("Tag Not found");
        else
        {
            taglist.remove(t);
        }
    }

    public boolean contains(Tag t)
    {
        for (Tag tag:taglist)
            if (tag.getName().equalsIgnoreCase(t.getName()))
                return true; 
        return false;
    }

    public Tag find(String name)
    {
        for (Tag tag:taglist)
            if (tag.getName().equalsIgnoreCase(name))
                return tag; 
        return null;
    }


    public static void main(String[] args) {
        TagManager tagManager = new TagManager();
        
        // Test adding tags
        tagManager.AddTag("Work", Color.YELLOW);  // Yellow
        tagManager.AddTag("Personal", Color.GREEN);  // Green
        tagManager.AddTag("Education", Color.BLUE);  // Blue
        tagManager.AddTag("Important", Color.RED); //Red
        // Test removing a tag
        //tagManager.RemoveTag("Personal");

        // Display all tags
        ArrayList<Tag> tags = tagManager.getTagList();
        for (Tag tag : tags) {
            System.out.println(tag);
        }
    }
}