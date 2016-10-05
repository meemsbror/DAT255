package gruppn.kasslr.model;

import org.json.JSONException;
import org.json.JSONObject;

public class VocabularyItem {
    private int id;
    private String name;
    private String imageUrl;

    public VocabularyItem(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public VocabularyItem(String name, String imageUrl, int id) {
        this(name, imageUrl);
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject item = new JSONObject();
        item.put("Word", name);
        item.put("Picture", imageUrl);

        return item;
    }
}
