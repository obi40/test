package com.optimiza.core.common.util;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

	/**
	 * 
	 * @param path : file path
	 * @param content : content to write it in the file
	 */
	public static void createWriteFile(String path, String content) {

		try {
			File file = new File(path);
			file.delete();
			file.createNewFile();
			try (FileWriter fw = new FileWriter(file)) {
				fw.write(content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param path : file path
	 * @return Return file contents line by line as a list
	 * @throws Exception
	 */
	public static List<String> readFile(String path) {
		try (Stream<String> stream = Files.lines(Paths.get(path))) {
			return stream.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
