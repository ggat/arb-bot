package ge.arb-bot.persist;

import ge.arb-bot.persist.exceptions.PersistException;
import ge.arb-bot.persist.models.Bookie;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 12/19/17.
 */
public class BookieRepository extends BaseRepository {

    Logger logger = LoggerFactory.getLogger(BookieRepository.class);

    public BookieRepository() throws PersistException {
    }

    public Long bookieIdByName(String name) {
        Bookie bookie = byName(name);

        return bookie != null ? bookie.getId() : null;
    }

    public Bookie find(Long id) {
        return session.get(Bookie.class, id);
    }

    public Bookie byName(String name) {

        Query<Bookie> query = session.createQuery("from Bookie ci where ci.name=:name", Bookie.class);
        query.setParameter("name", name);
        Bookie bookie = query.setMaxResults(1).uniqueResult();

        return bookie;
    }

    public List<Bookie> all() {

        Query<Bookie> chainQuery = session.createQuery("FROM Bookie ");
        return chainQuery.list();
    }
}
