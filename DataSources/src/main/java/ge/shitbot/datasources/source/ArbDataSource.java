package ge.shitbot.datasources.source;

import ge.shitbot.datasources.datatypes.Arb;
import ge.shitbot.datasources.exceptions.DataSourceException;

import java.util.List;

/**
 * Created by giga on 10/8/17.
 */
public interface ArbDataSource<T extends Arb> {

    String getRawData() throws DataSourceException;

    List<T> getArbs() throws DataSourceException;
}
