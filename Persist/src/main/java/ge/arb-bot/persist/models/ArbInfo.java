package ge.arb-bot.persist.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 12/19/17.
 */
@Entity
public class ArbInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
