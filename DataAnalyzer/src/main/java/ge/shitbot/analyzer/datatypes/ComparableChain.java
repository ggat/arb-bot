package ge.shitbot.analyzer.datatypes;

import java.util.ArrayList;

/**
 * List of CategoryData that can be compared for Arbitrages.
 * i.e. Same league from different bookies.
 *
 * Created by giga on 12/21/17.
 */
public class ComparableChain extends ArrayList<CategoryData> {

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        for(CategoryData categoryData : this) {
            builder.append("| ")
                    .append(categoryData.getBookieName())
                    .append(".")
                    .append(categoryData.getCategory())
                    .append(" ");
        }

        return builder.toString();
    }
}