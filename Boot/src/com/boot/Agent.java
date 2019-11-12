package com.boot;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

public class Agent {

	public static void premain(String agentArg, Instrumentation inst) {
		init(inst);
	}

	public static void agentmain(String agentArg, Instrumentation inst) {
		init(inst);
	}

	public static synchronized void init(Instrumentation inst) {
		try {
			JarFileHelper.addJarToBootstrap(inst);
			ModuleLoader.load(inst);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
