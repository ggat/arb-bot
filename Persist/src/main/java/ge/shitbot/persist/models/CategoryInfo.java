package ge.shitbot.persist.models;

import javax.persistence.*;

/**
 * Created by giga on 12/19/17.
 */
@Entity
public class CategoryInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*@Column(name = "category_info_id", nullable = true)
    public Long categoryInfoId;*/

    @Column(name = "bookie_id")
    public Long bookieId;

    @ManyToOne
    @JoinColumn(name = "category_info_id", nullable = true)
    public CategoryInfo parent;

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
}
