package Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UtilsForDataOfFiles {
    public static ArrayList<String> zipFileNameList(String zipFilePath) throws IOException {
        ArrayList<String> fNameList = new ArrayList<>();

        ZipFile zipFile = new ZipFile(zipFilePath);
        Enumeration e = zipFile.entries();

        while (e.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            fNameList.add(entry.getName());
            fNameList.add(entry.getName().substring(entry.getName().length() - 4));
        }
        zipFile.close();
        return fNameList;
    }
}
