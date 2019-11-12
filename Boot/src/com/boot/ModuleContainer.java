package com.boot;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class ModuleContainer {

	private Module module;
	private String moduleName;

	public ModuleContainer(String jarName) {
		try {
			File originFile = new File(ModuleLoader.baseDirectory + File.separator + jarName);
			JarFile jarFile = new JarFile(originFile);
			Attributes attributes = jarFile.getManifest().getMainAttributes();
			jarFile.close();
			this.moduleName = attributes.getValue("Module-Name");
			String moduleEnterClassName = attributes.getValue("Module-Class");

			Class moduleClass;

			if (ClassLoader.getSystemClassLoader() instanceof URLClassLoader) {
				Method method = Class.forName("java.net.URLClassLoader").getDeclaredMethod("addURL", URL.class);
				method.setAccessible(true);
				method.invoke(ModuleLoader.moduleClassLoader, originFile.toURI().toURL());
				method.invoke(ClassLoader.getSystemClassLoader(), originFile.toURI().toURL());
				moduleClass = ModuleLoader.moduleClassLoader.loadClass(moduleEnterClassName);
				module = (Module) moduleClass.newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start(Instrumentation inst) {
		try {
			module.start(inst);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
