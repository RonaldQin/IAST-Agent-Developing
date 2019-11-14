package com.engine.jni;

public class V8JNI {

	private static V8JNI instance;

	private V8JNI() {
		this.initV8();
	}

	public static V8JNI getInstance() {
		if (instance == null) {
			synchronized (V8JNI.class) {
				if (instance == null) {
					instance = new V8JNI();
				}
			}
		}
		return instance;
	}

	public native void initV8();

	public native void completeV8();

	public native String parseRules(String[] ruleFilePaths);

	public native String execInterpreter(String code);

	static {
		System.load(
				"/home/lace/Documents/workspace-sts-3.9.10.RELEASE/IAST-Agent-develop/v8-parser/Release/src/libv8v8-parser.so");
	}
}
