package gruppn.kasslr;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.json.JSONObject;

import gruppn.kasslr.model.VocabularyItem;

public class VocabularyItemUnitTest {

    private static final String NAME_STRING = "name";
    private static final String ALTERNATE_NAME_STRING = "notname";
    private static final String IMAGE_NAME_STRING = "imageName";
    private static final String ALTERNATE_IMAGE_NAME_STRING = "imageName1";
    private static final int ID_INT = 2;
    private static final int ALTERNATE_ID_INT = 3;

    VocabularyItem item1, item2, item3, item4, item5, item6, item7, item8;

    @Before
    public void createNewVocabularyItems() {
        item1 = new VocabularyItem(NAME_STRING, IMAGE_NAME_STRING);
        item2 = new VocabularyItem(NAME_STRING, IMAGE_NAME_STRING, ID_INT, true);
        item3 = new VocabularyItem(ALTERNATE_NAME_STRING, IMAGE_NAME_STRING, ID_INT, true);
        item4 = new VocabularyItem(NAME_STRING, ALTERNATE_IMAGE_NAME_STRING, ID_INT, true);
        item5 = new VocabularyItem(NAME_STRING, IMAGE_NAME_STRING, ALTERNATE_ID_INT, true);
        item6 = new VocabularyItem(NAME_STRING, IMAGE_NAME_STRING, ID_INT, false);
        item7 = new VocabularyItem(null, IMAGE_NAME_STRING, ID_INT, true);
        item8 = new VocabularyItem(NAME_STRING, IMAGE_NAME_STRING, ID_INT, true);
    }

    @Test
    public void getAndSetID() {
        item1.setId(ALTERNATE_ID_INT);
        assertEquals(ALTERNATE_ID_INT, item1.getId());
    }

    @Test
    public void getAndSetName() {
        item1.setName("No");
        assertNotEquals(NAME_STRING, item1.getName());
    }

    @Test
    public void getAndSetImageName() {
        item1.setImageName("No");
        assertEquals("No", item1.getImageName());
    }

    @Test
    public void getAndSetLastModified() {
        item1.setLastModified(131313);
        assertEquals(131313, item1.getLastModified());
    }

    @Test
    public void getAndSetMine() {
        item1.setMine(false);
        assertFalse(item1.isMine());
    }

    @Test
    public void equals() {
        assertTrue(item1.equals(item1));
        assertFalse(item2.equals(new Object()));
        assertFalse(item2.equals(item3));
        assertFalse(item2.equals(item4));
        assertFalse(item2.equals(item5));
        assertFalse(item2.equals(item6));
        assertFalse(item2.equals(item7));
        assertTrue(item2.equals(item8));
    }

    @Test
    public void testHashCode() {
        assertFalse(item2.hashCode() == item4.hashCode());
        assertTrue(item2.hashCode() == item2.hashCode());
        assertTrue(item2.hashCode() == item8.hashCode());
    }

    @Test
    public void JSONObject() throws JSONException {
        JSONObject object = item2.toJSON();
        assertEquals(NAME_STRING, object.get("Word"));
    }

    @Test
    public void testToString() {
        String testResult = "VocabularyItem{"
                + "name=" + NAME_STRING
                + ",image=" + IMAGE_NAME_STRING
                + ",mine=" + true
                + "}";

        assertEquals(testResult, item2.toString());
    }

    @Test
    public void setRemoved() {
        assertFalse(item2.isRemoved());
        item2.setRemoved();
        assertTrue(item2.isRemoved());
    }
}
