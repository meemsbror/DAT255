package gruppn.kasslr.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class VocabularyItem {
    private int id;
    private String name;
    private String image;

    public VocabularyItem(String name, String imageName) {
        this.name = name;
        this.image = imageName;
    }

    public VocabularyItem(String name, String imageName, int id) {
        this(name, imageName);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    /**
     * @return The name of the image without file extension
     */
    public String getImageName() {
        return image;
    }

    public void setImageName(String image) {
        this.image = image;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject item = new JSONObject();
        item.put("Word", name);
        item.put("Picture", image);

        return item;
    }
}
