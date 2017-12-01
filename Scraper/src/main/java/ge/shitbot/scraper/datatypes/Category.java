package ge.shitbot.scraper.datatypes;

import java.util.ArrayList;
import java.util.List;

public class Category {
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