package ge.shitbot.persist;

import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.util.SessionUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by giga on 12/19/17.
 */
public class BaseRepository {

    Logger logger = LoggerFactory.getLogger(BaseRepository.class);

    protected Session session;

    public BaseRepository() throws PersistException {
        try {
            session = SessionUtil.getSingletonSession();
        } catch (RuntimeException e) {
            logger.error("Could not get session");
            throw new PersistException(e);
        }
    }
}
