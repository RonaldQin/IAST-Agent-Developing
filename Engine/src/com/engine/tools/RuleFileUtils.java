package com.engine.tools;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RuleFileUtils {

	/**
	 * 返回指定目录下的所有JS文件的绝对路径。
	 * 
	 * @param path
	 * @return
	 */
	public static List<String> getRuleFilePaths(String path) {
		File file = new File(path);
		List<String> filePaths = new LinkedList<String>();
		LinkedList<File> filesList = new LinkedList<File>();
		if (file.exists()) {
			if (null == file.listFiles()) {
				return filePaths;
			}
			filesList.addAll(Arrays.asList(file.listFiles()));
			while (!filesList.isEmpty()) {
				File first = filesList.getFirst();
				if (first.getName().endsWith(".js")) {
					filePaths.add(first.getAbsolutePath());
				}
				File[] files = filesList.removeFirst().listFiles();
				if (null == files) {
					continue;
				}
				for (File f : files) {
					if (f.isDirectory()) {
						filesList.add(f);
					} else {
						if (f.getName().endsWith(".js")) {
							filePaths.add(f.getAbsolutePath());
						}
					}
				}
			}
		}
		return filePaths;
	}
}
