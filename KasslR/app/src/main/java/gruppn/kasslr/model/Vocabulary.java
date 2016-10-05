package gruppn.kasslr.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class Vocabulary {
    private int id;
    private String owner;
    private String title;
    private List<VocabularyItem> items;

    public Vocabulary(String owner, String title) {
        this.owner = owner;
        this.title = title;
    }

    public Vocabulary(String owner, String title, int id) {
        this(owner, title);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
