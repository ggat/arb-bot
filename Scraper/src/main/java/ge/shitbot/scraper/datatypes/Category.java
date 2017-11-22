package ge.shitbot.scraper.datatypes;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private Integer id;
    private List<Category> subCategories = new ArrayList<>();
    private Category parent;
    private List<Event> events = new ArrayList<>();

    public Category() {
    }

    public Category(String name, Integer id) {
        this.setName(name);
        this.setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void addSubCategory(Category subCategory) {
        this.getSubCategories().add(subCategory);
        subCategory.setParent(this);
    }

    public void removeSubCategory(Category subCategory) {
        this.getSubCategories().remove(subCategory);
        subCategory.setParent(null);
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
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
}