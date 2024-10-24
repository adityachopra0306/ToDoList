import javafx.scene.paint.Color;

public class Tag {
    private String name;
    private Color color;

    public Tag(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String toString() {
        return name + ";" + color.toString();
    }

    public static Tag fromCSV(String csvLine) {
        String[] parts = csvLine.split(";");
        if (parts.length == 2) {
            String name = parts[0];
            Color color = Color.web(parts[1]);
            return new Tag(name, color);
        }
        return null;
    }
}
