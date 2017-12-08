package ge.shitbot.persist.tests;

import ge.shitbot.persist.util.SessionUtil;
import org.hibernate.Session;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by giga on 12/7/17.
 */
public class SessionTest {

    @Test
    public void testSavePerson() {

        try (Session session = SessionUtil.getSession()) {
            assertNotNull(session);
        }
    }
}
