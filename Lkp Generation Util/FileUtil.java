package javaapitest;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

    public static void createWriteFile(String path, String content) throws Exception {
        File file = new File(path);
        file.delete();
        file.createNewFile();
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
    }

    public static List<String> readTxtFile(String path) throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            return stream.collect(Collectors.toList());
        }
    }
}
