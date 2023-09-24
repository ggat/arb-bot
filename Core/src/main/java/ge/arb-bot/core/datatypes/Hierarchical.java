package ge.arb-bot.core.datatypes;

import java.util.List;

/**
 * Created by giga on 2/7/18.
 */
public interface Hierarchical<T extends Hierarchical> extends Named {

    T getParent();

    List<T> getSubCategories();
}
