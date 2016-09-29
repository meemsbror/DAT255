package gruppn.kasslr.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Adam on 2016-09-28.
 */

public class VocabularyItem {
    private String name;
    private String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
