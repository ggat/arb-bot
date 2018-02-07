package ge.shitbot.core.datatypes.util;

import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * Created by giga on 2/7/18.
 */
public class FileSerializer {

    public static void toFile(String fileName, Object object) throws IOException {

        File yourFile = new File(fileName);
        yourFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
    }

    public static Object fromFile(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object result = ois.readObject();
        ois.close();

        return result;
    }
}
