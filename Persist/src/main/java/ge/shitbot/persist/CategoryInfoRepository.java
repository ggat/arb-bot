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

    public List<CategoryInfo> byNameList(String name) {

        Query<CategoryInfo> query = session.createQuery("from CategoryInfo ci where ci.name=:name", CategoryInfo.class);
        query.setParameter("name", name);
        return query.list();
    }

    /**
     * Flat list of 'root (Parentless)' categories is expected here.
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

        recursiveSave(categoryInfos, existingInfos);

        tx.commit();
    }


    protected void recursiveSave(List<? extends CategoryInfo> categoryInfos, List<CategoryInfo> existingInfos) {

        for (CategoryInfo newCategoryInfo : categoryInfos) {

            CategoryInfo existingInfo = null;

            //Check if this categoryInfo already exists
            for(CategoryInfo existingCategoryInfo : existingInfos) {
                if(existingCategoryInfo.getName().equals(newCategoryInfo.getName())) {

                    existingInfo = existingCategoryInfo;
                    break;
                }
            }

            // If it exists, save it and start saving its subCategories. Then go to next iteration by continue statement
            if(existingInfo != null) {

                newCategoryInfo.setId(existingInfo.getId());

                //it's subCategories must be compared to existingInfos subCategories second argument
                recursiveSave(newCategoryInfo.getSubCategoryInfos(), existingInfo.getSubCategoryInfos());
                continue;
            }

            // If having parent merge it to persist state to avoid problems while saving childs e.g. subcategories
            CategoryInfo parentCategoryInfo = newCategoryInfo.getParent();

            if(parentCategoryInfo != null) {
                CategoryInfo mergedParentCategoryInfo = (CategoryInfo) session.merge(parentCategoryInfo);
                newCategoryInfo.setParent(mergedParentCategoryInfo);
            }

            // Save it
            session.save(newCategoryInfo);

            // When there is no existing categoryInfo it means, newCategoryInfo's subcategories should not be compared
            // to anything cause we know for sure that they does not exist yet
            if(newCategoryInfo.getSubCategoryInfos().size() > 0) {
                recursiveSave(newCategoryInfo.getSubCategoryInfos(), new ArrayList<>());
            }
        }
    }

    public List<CategoryInfo> getCategoryInfosByName(String name) {
        Query<CategoryInfo> existingInfosQuery = session.createQuery("from CategoryInfo where name=:name");
        existingInfosQuery.setParameter("name", name);
        return existingInfosQuery.list();
    }
}
