package gruppn.kasslr;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import gruppn.kasslr.model.User;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

import org.json.JSONArray;
import org.json.JSONException;

public class VocabularyUnitTest {

    private static final String USER_ID = "userId";
    private static final String USER_NAME = "userName";
    private static final String USER_PIC = "userPic";
    private static User user;

    private static final int VOCABULARY_ID = 1;
    private static final int VOCABULARY_UNI_ID = 2;
    private static final String VOCABULARY_TITLE = "title";
    private static Vocabulary voc1, voc2;

    private static final String ITEM_NAME = "itemName";
    private static final String ITEM_IMAGE_NAME = "ItemImageName";
    private static final int ITEM_ID = 3;
    private static VocabularyItem item1, item2;

    private static ArrayList<VocabularyItem> ITEMS;

    @Before
    public void initiateObjects() {
        user = new User(USER_NAME, USER_ID, USER_PIC);

        item1 = new VocabularyItem(ITEM_NAME, ITEM_IMAGE_NAME, ITEM_ID, true);
        item2 = new VocabularyItem("name", "imageName");

        ITEMS = new ArrayList<VocabularyItem>();
        ITEMS.add(item1);
        ITEMS.add(item2);

        voc1 = new Vocabulary(user, "something", 1, 1);
        voc2 = new Vocabulary(null, "something", 2, 2);
    }

    @Test
    public void getAndSetID() {
        voc1.setId(VOCABULARY_ID);
        assertEquals(VOCABULARY_ID, voc1.getId());
    }

    @Test
    public void getAndSetUniversalID() {
        voc1.setUniversalId(VOCABULARY_UNI_ID);
        assertEquals(VOCABULARY_UNI_ID, voc1.getUniversalId());
    }

    @Test
    public void getOwner() {
        assertSame(user, voc1.getOwner());
    }

    @Test
    public void getAndSetTitle() {
        voc1.setTitle(VOCABULARY_TITLE);
        assertEquals(VOCABULARY_TITLE, voc1.getTitle());
    }

    @Test
    public void getAndSetItems() {
        voc1.setItems(ITEMS);
        assertSame(ITEMS, voc1.getItems());
    }

    @Test
    public void JSONArray() throws JSONException {
        voc1.setItems(ITEMS);
        JSONArray array = voc1.toJSONArray();
        assertEquals(2, array.length());

        JSONObject obj = array.getJSONObject(0);

        assertEquals(item1.getImageName(), obj.getString("Picture"));
    }

    @Test
    public void testToString() {
        voc1.setTitle(VOCABULARY_TITLE);
        voc1.setItems(ITEMS);
        voc1.setUniversalId(VOCABULARY_UNI_ID);
        String result = "Vocabulary{" +
                "title='" + VOCABULARY_TITLE + '\'' +
                ", id=" + VOCABULARY_ID + '\'' +
                ", univeralId=" + VOCABULARY_UNI_ID + '\'' +
                ", owner='" + user.toString() + '\'' +
                ", items=" + ITEMS +
                '}';

        assertEquals(result, voc1.toString());
    }

    @Test
    public void testEquals() {
        assertFalse(voc1.equals(null));
        assertFalse(voc1.equals(new Object()));
        assertTrue(voc1.equals(voc1));
        assertFalse(voc1.equals(voc2));

        voc2.setUniversalId(1);
        assertTrue(voc1.equals(voc2)); //will never happen outside of tests
    }

    @Test
    public void testHashCode() {
        assertEquals(31, voc1.hashCode());

        voc1.setUniversalId(5);
        assertEquals(5 * 31, voc1.hashCode());
    }
}


