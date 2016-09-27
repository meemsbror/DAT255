package gruppn.kasslr.model;

import java.util.List;

/**
 * Created by Adam on 2016-09-28.
 */

public class Vocabulary {
    private String owner;
    private String title;
    private List items;

    public Vocabulary(String owner, String title){
        this.owner = owner;
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
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
