package ge.shitbot.persist;

import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.persist.models.Chain;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 12/19/17.
 */
public class ChainRepository extends BaseRepository {

    Logger logger = LoggerFactory.getLogger(ChainRepository.class);

    public ChainRepository() throws PersistException {
    }

    public List<Chain> all() {

        Query<Chain> chainQuery = session.createQuery("FROM Chain");
        return chainQuery.list();
    }
}
