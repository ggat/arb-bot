package ge.shitbot.persist.models;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by giga on 12/7/17.
 */
@Entity
@Table(name = "skill")
public class Skill {

    String name;

    public Skill() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "name='" + name + '\'' +
                '}';
    }
}
