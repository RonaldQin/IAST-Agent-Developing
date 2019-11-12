package com.engine.rule;

import java.lang.reflect.Method;

import com.boot.ModuleLoader;
import com.engine.bean.Rule;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public abstract class AbstractRule {

	protected ClassPool pool;
	protected CtClass dealClass;
	protected byte[] classfileBuffer;

	protected CtMethod dealMethod;
	protected String insertPosition;
	protected String checker;
	
	protected AbstractRule(ClassLoader loader, byte[] classfileBuffer, String className) {
		pool = new ClassPool();
		pool.appendClassPath(new ClassClassPath(ModuleLoader.class));
		if (loader != null) {
			pool.appendClassPath(new LoaderClassPath(loader));
		}
		try {
			dealClass = pool.get(className);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void changeDealMethod(String methodName) {
		try {
			this.dealMethod = this.dealClass.getDeclaredMethod(methodName);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void changeDealClass(String className) {
		try {
			dealClass = pool.get(className);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public void setInsertPosition(String insertPosition) {
		this.insertPosition = insertPosition;
	}

	public void callInsertion(String insert_logic, String insertPosition) {
		setInsertPosition(insertPosition);
		String code = "";
		try {
			Method m = this.getClass().getMethod(insert_logic);
			m.setAccessible(true);
			code = (String) m.invoke(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finishInsert(code);
	}

	public void finishInsert(String code) {
		if (code == null || code.length() == 0) {
			return;
		}
		try {
			if (insertPosition.equalsIgnoreCase(Rule.INSERT_BEFORE)) {
				dealMethod.insertBefore(code);
			} else if (insertPosition.equalsIgnoreCase(Rule.INSERT_AFTER)) {
				dealMethod.insertAfter(code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] completeDealClass() {
		try {
			this.classfileBuffer = dealClass.toBytecode();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.dealClass.detach();
		}
		return this.classfileBuffer;
	}

	public abstract String insert_addChecker();
}
