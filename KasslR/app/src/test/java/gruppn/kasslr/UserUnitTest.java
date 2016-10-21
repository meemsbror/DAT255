package gruppn.kasslr;

import org.junit.Before;
import org.junit.Test;

import gruppn.kasslr.model.User;

import static org.junit.Assert.*;

public class UserUnitTest {

    private static final String ID_STRING = "id";
    private static final String ALTERNATE_ID_STRING = "id2";
    private static final String NAME_STRING = "name";
    private static final String ALTERNATE_NAME_STRING = "name2";
    private static final String PIC_URL_STRING = "picUrl";
    private static final String ALTERNATE_PIC_URL_STRING = "picUrl2";

    User user1, user2, user3;

    @Before
    public void createNewUsers() {
        user1 = new User(NAME_STRING, ID_STRING, PIC_URL_STRING);
        user2 = new User(ALTERNATE_NAME_STRING, ID_STRING, ALTERNATE_PIC_URL_STRING);
        user3 = new User(NAME_STRING, ALTERNATE_ID_STRING, ALTERNATE_PIC_URL_STRING);
    }

    @Test
    public void getAndSetName() {
        user2.setName(NAME_STRING);
        assertEquals(NAME_STRING, user2.getName());
    }

    @Test
    public void getId() {
        assertEquals(ID_STRING, user2.getId());
    }
    
    @Test
    public void setProfilePic() {
        user2.setProfilePic(PIC_URL_STRING);
        assertEquals(PIC_URL_STRING, user2.getProfilePic());
    }

    @Test
    public void testEquals() {
        assertFalse(user1.equals(new Object()));
        assertTrue(user1.equals(user1));
        assertTrue(user1.equals(user2)); // this will never happen outside of tests
        assertFalse(user1.equals(user3));
    }

    @Test
    public void testToString() {
        String expectResult = "User{" +
                "name=" + user1.getName() +
                ",id=" + user1.getId() +
                ",profilePicUrl=" + user1.getProfilePic() +
                "}";

        assertEquals(expectResult, user1.toString());

    }

}
