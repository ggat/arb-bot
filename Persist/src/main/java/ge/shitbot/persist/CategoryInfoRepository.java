package ge.shitbot.persist;

import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.persist.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 12/19/17.
 */
public class CategoryInfoRepository extends BaseRepository {

    Logger logger = LoggerFactory.getLogger(CategoryInfoRepository.class);

    public CategoryInfoRepository() throws PersistException {
    }

    public void saveCategoryInfos(List<? extends CategoryInfo> categoryInfos) {
        Transaction tx = session.beginTransaction();

        for (CategoryInfo categoryInfo : categoryInfos) {
            session.save(categoryInfo);
        }

        tx.commit();
    }

    public void saveCategoryInfo(CategoryInfo categoryInfo) {

        ArrayList<CategoryInfo> list = new ArrayList<>();
        list.add(categoryInfo);

        saveCategoryInfos(list);
    }

    public List<? extends CategoryInfo> getCategoryInfos() {
        Query<CategoryInfo> query = session.createQuery("from CategoryInfo", CategoryInfo.class);
        return query.list();
    }

    public CategoryInfo find(Long id) {
        return session.get(CategoryInfo.class, id);
    }

    public CategoryInfo byName(String name) {

        Query<CategoryInfo> query = session.createQuery("from CategoryInfo ci where ci.name=:name", CategoryInfo.class);
        query.setParameter("name", name);
        CategoryInfo categoryInfo = query.setMaxResults(1).uniqueResult();

        return categoryInfo;
    }
}
