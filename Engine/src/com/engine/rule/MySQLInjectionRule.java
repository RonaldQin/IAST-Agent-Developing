package com.engine.rule;

import com.engine.jni.V8JNI;

public class MySQLInjectionRule extends AbstractRule {

	private MySQLInjectionRule(ClassLoader loader, byte[] classfileBuffer, String className) {
		super(loader, classfileBuffer, className);
	}

	private static MySQLInjectionRule instance;

	public String executed_sql;

	public static MySQLInjectionRule getInstance(ClassLoader loader, byte[] classfileBuffer,
			String className) {
		if (instance == null) {
			synchronized (MySQLInjectionRule.class) {
				if (instance == null) {
					instance = new MySQLInjectionRule(loader, classfileBuffer, className);
				}
			}
		}
		return instance;
	}

	public static MySQLInjectionRule getInstance() {
		if (instance == null) {
			return null;
		}
		return instance;
	}

	public String insert_GetExecutedSQL() {
		pool.importPackage(MySQLInjectionRule.class.getCanonicalName());
		StringBuffer code_buffer = new StringBuffer("");
		try {
			dealMethod.addLocalVariable("instance", pool.get(MySQLInjectionRule.class.getCanonicalName()));
			code_buffer.append("instance = " + MySQLInjectionRule.class.getCanonicalName() + ".getInstance();");
			code_buffer.append("if (instance != null) {");
			code_buffer.append("instance.executed_sql = $1;");
			code_buffer.append("System.out.println(instance.executed_sql);");
			code_buffer.append("}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code_buffer.toString();
	}

	@Override
	public String insert_addChecker() {
		pool.importPackage(this.getClass().getCanonicalName());
		pool.importPackage(V8JNI.class.getCanonicalName());
		StringBuffer code_buffer = new StringBuffer("");
//
////		Thread taskCheck = new Thread(new Runnable() {
////
////			@Override
////			public void run() {
////				V8JNIService.getInstance().assignChecker(
////						"function(sql) {			print(\"in checker function!!!\");			return true;		}");
////				
////			}
////		});
////		taskCheck.start();
////		try {
////			taskCheck.join();
////		} catch (InterruptedException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
//		
		try {
//			dealMethod.addLocalVariable("taskCheck", pool.get(Thread.class.getCanonicalName()));
//			dealMethod.addLocalVariable("checkResult", pool.get(String.class.getCanonicalName()));
			code_buffer.append("System.out.println(\"CHECKER POINT@@@@\");");
			code_buffer.append("V8JNI.getInstance().execInterpreter(\"\");");
//			code_buffer.append("V8JNI.getInstance().assignChecker(\"" + getChecker() + "\");");
//			code_buffer.append("taskCheck = new Thread(new RR(\"" + getChecker() + "\"));");
//			code_buffer.append("taskCheck.start();");
//			code_buffer.append("try {taskCheck.join();} catch (InterruptedException e) {e.printStackTrace();}");
//			code_buffer.append("System.out.println(RR.res);");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println("【Source】" + code_buffer.toString());
		return code_buffer.toString();
	}
}
