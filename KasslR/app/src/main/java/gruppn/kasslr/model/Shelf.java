package gruppn.kasslr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2016-09-28.
 */

public class Shelf {
    private List items;
    private List vocabularies;

    public Shelf(){
        items = new ArrayList();
        vocabularies = new ArrayList();
    }

    public List getItems() {
        return items;
    }

    public void addItem(VocabularyItem item){
        items.add(item);
    }

    public List getVocabularies() {
        return vocabularies;
    }

    public void addVocabulary(Vocabulary voc){
        vocabularies.add(voc);
    }

    @Override
    public String toString() {
        return "Shelf{" +
                "items=" + items +
                ", vocabularies=" + vocabularies +
                '}';
    }
}
