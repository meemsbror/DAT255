package gruppn.kasslr.model;

import java.util.ArrayList;
import java.util.List;

public class Shelf {
    private List<VocabularyItem> items;
    private List<Vocabulary> vocabularies;

    public Shelf() {
        items = new ArrayList<>();
        vocabularies = new ArrayList<>();
    }

    public List<VocabularyItem> getItems() {
        return items;
    }

    public void addItem(VocabularyItem item){
        items.add(item);
    }

    public void addItems(List<VocabularyItem> items) {
        this.items.addAll(items);
    }

    public void removeItem(VocabularyItem item) {
        items.remove(item);
    }

    public List<Vocabulary> getVocabularies() {
        return vocabularies;
    }

    public void addVocabulary(Vocabulary voc){
        vocabularies.add(voc);
    }

    public void addVocabularies(List<Vocabulary> vocabularies) {
        this.vocabularies.addAll(vocabularies);
    }

    @Override
    public String toString() {
        return "Shelf{" +
                "items=" + items +
                ", vocabularies=" + vocabularies +
                '}';
    }
}
