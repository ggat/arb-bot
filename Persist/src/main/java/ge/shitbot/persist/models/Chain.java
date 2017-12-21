package ge.shitbot.persist.models;

import javax.persistence.*;

/**
 * Created by giga on 12/21/17.
 */
@Entity
public class Chain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO: Maybe we can handle this directly as JSON
    private String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
