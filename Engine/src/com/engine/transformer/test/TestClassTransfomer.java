package com.engine.transformer.test;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class TestClassTransfomer implements ClassFileTransformer {

	private Instrumentation inst;
	private String targetClassName;

	public TestClassTransfomer(String targetClassName, Instrumentation inst) {
		this.targetClassName = targetClassName;
		this.inst = inst;
		inst.addTransformer(this, true);
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (targetClassName.equals(className.replaceAll("/", "."))) {
			ClassPool pool = ClassPool.getDefault();
			CtClass cc = null;
			try {
				cc = pool.get(targetClassName);
				CtMethod m = cc.getDeclaredMethod("");

				classfileBuffer = cc.toBytecode();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cc.detach();
			}
		}
		return classfileBuffer;
	}

	public TestClassTransfomer retransform() {
		for (Class clazz : inst.getAllLoadedClasses()) {
			if (clazz.getCanonicalName().equals(targetClassName)) {
				try {
					inst.retransformClasses(clazz);
				} catch (UnmodifiableClassException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return this;
	}
}
