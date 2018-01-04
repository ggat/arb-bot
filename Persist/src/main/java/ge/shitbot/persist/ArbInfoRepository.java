package ge.shitbot.persist;

import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.ArbInfo;
import ge.shitbot.persist.models.Chain;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by giga on 12/19/17.
 */
public class ArbInfoRepository extends BaseRepository {

    Logger logger = LoggerFactory.getLogger(ArbInfoRepository.class);

    public ArbInfoRepository() throws PersistException {
    }

    public List<Chain> all() {

        Query<Chain> chainQuery = session.createQuery("FROM Chain");
        return chainQuery.list();
    }

    public void saveArbInfo(ArbInfo arbInfo) {
        Transaction tx = session.beginTransaction();
        session.save(arbInfo);
        tx.commit();
    }

    public int truncate() {
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("delete from ArbInfo");
        int affected = query.executeUpdate();
        transaction.commit();

        return affected;
    }
}
