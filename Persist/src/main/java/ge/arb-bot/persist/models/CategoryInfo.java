package ge.arb-bot.persist.models;

import ge.arb-bot.core.datatypes.Hierarchical;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 12/19/17.
 */
@Entity
public class CategoryInfo implements Hierarchical<CategoryInfo> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bookie_id")
    public Long bookieId;

    @ManyToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_info_id", nullable = true)
    public CategoryInfo parent;

    @OneToMany(mappedBy = "parent")
    public List<CategoryInfo> subCategoryInfos = new ArrayList<>();

    @Column
    public String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookieId() {
        return bookieId;
    }

    public void setBookieId(Long bookieId) {
        this.bookieId = bookieId;
    }

    public CategoryInfo getParent() {
        return parent;
    }

    public void setParent(CategoryInfo parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CategoryInfo> getSubCategoryInfos() {
        return subCategoryInfos;
    }

    public void setSubCategoryInfos(List<CategoryInfo> subCategoryInfos) {
        this.subCategoryInfos = subCategoryInfos;
    }

    public List<CategoryInfo> getSubCategories() {
        return getSubCategoryInfos();
    }
}
