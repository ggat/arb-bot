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

    public List<? extends CategoryInfo> getCategoryInfosForBookie(Long bookieId) {
        Query<CategoryInfo> query = session.createQuery("from CategoryInfo where bookieId=:bookieId", CategoryInfo.class);
        query.setParameter("bookieId", bookieId);
        return query.list();
    }

    public List<? extends CategoryInfo> categoryInfosWithChildren() {
        Query<CategoryInfo> query = session.createQuery("from CategoryInfo where category_info_id IS NOT NULL ", CategoryInfo.class);
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

    /**
     * Flat list is expected here.
     * This method will not recurse on subcategories.
     *
     * @param bookieId
     * @param categoryInfos
     */
    public void updateCategoryInfosForBookie(Long bookieId, List<? extends CategoryInfo> categoryInfos) {

        Transaction tx = session.beginTransaction();

        Query<CategoryInfo> existingInfosQuery = session.createQuery("from CategoryInfo where bookieId=:bookieId");
        existingInfosQuery.setParameter("bookieId", bookieId);
        List<CategoryInfo> existingInfos = existingInfosQuery.list();

        for (CategoryInfo newCategoryInfo : categoryInfos) {

            boolean exists = false;

            for(CategoryInfo existingCategoryInfo : existingInfos) {
                if(existingCategoryInfo.getName().equals(newCategoryInfo.getName())) {
                    exists = true;
                    break;
                }
            }

            if(!exists) {
                session.save(newCategoryInfo);
            }
        }

        tx.commit();
    }

    public List<CategoryInfo> getCategoryInfosByName(String name) {
        Query<CategoryInfo> existingInfosQuery = session.createQuery("from CategoryInfo where name=:name");
        existingInfosQuery.setParameter("name", name);
        return existingInfosQuery.list();
    }
}
