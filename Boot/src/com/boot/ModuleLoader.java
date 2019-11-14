package com.boot;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.net.URLDecoder;

public class ModuleLoader {

	public static final String ENGINE_JAR = "IAST-AgentEngine.jar"; // engine 的 jar 包名称
	private static ModuleContainer engineContainer;
	public static String baseDirectory;
	private static ModuleLoader instance;
	public static ClassLoader moduleClassLoader; // 扩展类加载器

	static {
		Class clazz = ModuleLoader.class;
		String path = clazz.getResource("/" + clazz.getName().replace(".", "/") + ".class").getPath();
		if (path.startsWith("file:")) {
			path = path.substring(5);
		}
		if (path.contains("!")) {
			path = path.substring(0, path.indexOf("!"));
		}
		try {
			baseDirectory = URLDecoder.decode(new File(path).getParent(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			baseDirectory = new File(path).getParent();
		}
		// 定位到扩展类加载器
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		while (systemClassLoader.getParent() != null
				&& !systemClassLoader.getClass().getName().equals("sun.misc.Launcher$ExtClassLoader")) {
			systemClassLoader = systemClassLoader.getParent();
		}
		moduleClassLoader = systemClassLoader;

	}

	private ModuleLoader(Instrumentation inst) {
		engineContainer = new ModuleContainer(ENGINE_JAR);
		engineContainer.start(inst);
	}

	public static synchronized void load(Instrumentation inst) {
		if (instance == null) {
			instance = new ModuleLoader(inst);
		}
	}
}
