package gruppn.kasslr.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Vocabulary {
    private User user;
    private int id;
    private String owner;
    private String title;
    private List<VocabularyItem> items;
    private int universalId = 0;

    public Vocabulary(String owner, String title) {
        this.owner = owner;
        this.title = title;
        items = new ArrayList<>();
    }

    public Vocabulary(String owner, String title, int id, int universalId) {
        this(owner, title);
        this.id = id;
        this.universalId = universalId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUniversalId() {
        return universalId;
    }

    public void setUniversalId(int universalId) {
        this.universalId = universalId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title.trim();
    }

    public List<VocabularyItem> getItems() {
        return items;
    }

    public void setItems(List<VocabularyItem> items) {
        this.items = items;
    }

    public JSONArray toJSONArray() throws JSONException {
        JSONArray vocabulary = new JSONArray();
        for(VocabularyItem item : items){
            vocabulary.put(item.toJSON());
        }
        return vocabulary;
    }

    @Override
    public String toString() {
        return "Vocabulary{" +
                "owner='" + owner + '\'' +
                ", title='" + title + '\'' +
                ", items=" + items +
                '}';
    }

}
