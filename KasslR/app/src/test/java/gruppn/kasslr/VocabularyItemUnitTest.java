package gruppn.kasslr;

import org.junit.Test;

import gruppn.kasslr.model.VocabularyItem;

public class VocabularyItemUnitTest {

    private static final String NAME_STRING = "name";
    private static final String IMAGE_NAME_STRING = "imageName";
    private static final String ALTERNATE_IMAGE_NAME_STRING = "imageName1";
    private static final int ID_INT = 2;
    private static final boolean MINE_BOOLEAN = false;

    VocabularyItem item1, item2, item3;

    @Test
    public void createNewVocabularyItems() {
        item1 = new VocabularyItem(NAME_STRING, IMAGE_NAME_STRING);
        item2 = new VocabularyItem(NAME_STRING, IMAGE_NAME_STRING, ID_INT, MINE_BOOLEAN);
        item3 = new VocabularyItem(NAME_STRING, ALTERNATE_IMAGE_NAME_STRING, ID_INT, MINE_BOOLEAN);


    }
}
