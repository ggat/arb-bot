package ge.arb-bot.datasources.source;

import ge.arb-bot.datasources.exceptions.DataSourceException;
import ge.arb-bot.core.datatypes.Arb;

import java.util.List;

/**
 * Created by giga on 10/8/17.
 */
public interface ArbDataSource<T extends Arb> {

    String getRawData() throws DataSourceException;

    List<T> getArbs() throws DataSourceException;
}
