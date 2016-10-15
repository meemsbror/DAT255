package gruppn.kasslr.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vocabulary {
    private User user;
    private int id;
    private String title;
    private List<VocabularyItem> items;
    private int universalId = 0;
    private boolean favourite = false;

    public Vocabulary(User user, String title){
        this.user = user;
        this.title = title;
    }

    public Vocabulary(String owner, String title) {
        user = new User(owner);
        this.title = title;
        items = new ArrayList<>();
    }

    public Vocabulary(String owner, String title, int id, int universalId) {
        this(owner, title);
        this.id = id;
        this.universalId = universalId;
    }

<<<<<<< HEAD
    public Vocabulary(User user, String title, int id, int universalId){
        this(user,title);
        this.id = id;
        this.universalId = universalId;
=======
    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        } else {
            return  this.getUniversalId() == ((Vocabulary) obj).getUniversalId();
        }
>>>>>>> changes made to search and favourit now works
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

    public User getOwner() {
        return user;
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

    public boolean isFavourite(){
        return favourite;
    }

    public void setFavourite(boolean favourite) {

        this.favourite = favourite;
    }

    @Override
    public String toString() {
        return "Vocabulary{" +
                "owner='" + user.getName() + '\'' +
                ", title='" + title + '\'' +
                ", items=" + items +
                '}';
    }

}
