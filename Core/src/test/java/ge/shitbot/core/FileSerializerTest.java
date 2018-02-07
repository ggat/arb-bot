package ge.shitbot.core;

import org.junit.Test;
import ge.shitbot.core.datatypes.util.FileSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Created by giga on 2/7/18.
 */
public class FileSerializerTest {

    @Test
    public void test() throws IOException, ClassNotFoundException {

        String fileName = "test-serialization.dump";
        String[] names = {"Ferrari", "BMW", "Bentley"};
        List<String> carNames = new ArrayList<>(Arrays.asList(names));

        // Write objects to file
        FileSerializer.toFile(fileName, carNames);

        // Read objects from file
        List<String> carNamesReadBack = (List<String>) FileSerializer.fromFile(fileName);

        assertEquals(carNamesReadBack, carNames);
    }
}
