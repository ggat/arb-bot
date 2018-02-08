package ge.shitbot.scraper.datatypes;

import ge.shitbot.core.datatypes.Hierarchical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Hierarchical<Category>, Serializable {
    private String name;
    private Long id;
    private List<Category> subCategories = new ArrayList<>();
    private Category parent;
    private List<Event> events = new ArrayList<>();

    public Category() {
    }

    public Category(String name, Long id) {
        this.setName(name);
        this.setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public final List<Category> getSubCategories() {
        return subCategories;
    }

    public final void addSubCategory(Category subCategory) {
        this.getSubCategories().add(subCategory);
        subCategory.setParent(this);
    }

    public final void removeSubCategory(Category subCategory) {
        this.getSubCategories().remove(subCategory);
        subCategory.setParent(null);
    }

    public Category getParent() {
        return parent;
    }

    private void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        this.getEvents().add(event);
        event.setCategory(this);
    }

    public void removeEvent(Event event) {
        this.getEvents().remove(event);
        event.setCategory(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (name != null ? !name.equals(category.name) : category.name != null) return false;
        if (id != null ? !id.equals(category.id) : category.id != null) return false;
        if (!subCategories.equals(category.subCategories)) return false;
        if (parent != null ? !parent.equals(category.parent) : category.parent != null) return false;
        return events != null ? events.equals(category.events) : category.events == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + subCategories.hashCode();
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (events != null ? events.hashCode() : 0);
        return result;
    }

    //TODO: look at fixme
    //FIXME: Some strange recursion or something like that is happening here
    /*@Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", id=" + id +
                //WARNING: Following two can cause subString recursion
                ", subCategories=" + subCategories.size() +
                ", parent=" + parent.getId() +
                ", events=" + events.size() +
                '}';
    }*/
}