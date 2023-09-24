package ge.arb-bot.persist.models;

import javax.persistence.*;

/**
 * Created by giga on 12/19/17.
 */
@Entity
public class Bookie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
