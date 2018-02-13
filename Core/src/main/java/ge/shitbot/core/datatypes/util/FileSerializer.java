package ge.shitbot.core.datatypes.util;

import ge.shitbot.core.datatypes.exceptions.FileSerializerException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by giga on 2/7/18.
 */
public class FileSerializer {

    public static void toFile(URI fileName, Object object) throws IOException {
        toFile(fileName.toString(), object);
    }

    public static void toFile(String fileName, Object object) throws IOException {

        File yourFile = new File(fileName);
        File parentFile = yourFile.getParentFile();

        if(parentFile != null) {
            yourFile.getParentFile().mkdirs();
        } else {
            new File(System.getProperty("user.dir")).mkdirs();
        }

        yourFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
    }

    public static Object fromFile(URI fileName) throws IOException, ClassNotFoundException {
        return fromFile(fileName.toString());
    }

    public static Object fromFile(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object result = ois.readObject();
        ois.close();

        return result;
    }

    /**
     * Load serialized file from resources. File gets loaded relative to referer package.
     *
     * @param referer
     * @param fileName
     * @return
     * @throws FileSerializerException
     */
    public static Object loadFromResources(Object referer, String fileName) throws FileSerializerException {
        try {
            URL resource = Resources.loadFromSamePackage(referer,fileName);
            return FileSerializer.fromFile(Resources.toAbsName(resource));
        } catch (Exception e) {
            throw new FileSerializerException(e);
        }
    }
}
