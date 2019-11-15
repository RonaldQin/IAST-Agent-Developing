package com.engine.rule;

import com.engine.bean.HttpRequestHelperStack;
import com.engine.bean.HttpRequestInfo;
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
			System.out.println("MySQLInjectionRule instance is null...");
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
//			code_buffer.append("System.out.println(\"执行MySQL查询语句： \" + instance.executed_sql);");
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
		try {
			dealMethod.addLocalVariable("rule_instance", pool.get(this.getClass().getCanonicalName()));
			dealMethod.addLocalVariable("checkResult", pool.get(String.class.getCanonicalName()));
			code_buffer.append("rule_instance = " + MySQLInjectionRule.class.getCanonicalName() + ".getInstance();");
			code_buffer.append("System.out.println(\"执行MySQL查询语句： \" + rule_instance.executed_sql);");
			code_buffer
					.append("System.out.println(\"传递参数： \" + rule_instance.transmit_check_params());");
			code_buffer.append("checkResult = V8JNI.getInstance().execInterpreter(\"var check = " + getChecker()
					+ "\\ncheck.call(check, \\\"\" + rule_instance.transmit_check_params() + \"\\\");\");"); // TODO: 判断传递给checker的parameter是否为空
			code_buffer.append("System.out.println(\"检测MySQL执行的查询语句是否存在SQL注入风险: \" + checkResult);");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("[Insert code] \n" + code_buffer.toString() + "\n[Insert code]");
		return code_buffer.toString();
	}

	@Override
	public String transmit_check_params() {
		String[] res = new String[1];
		res[0] = executed_sql;
		HttpRequestInfo request = HttpRequestHelperStack.peek();
//		System.out.println("**************************************");
//		System.out.println("Body: " + request.getBody() + "\nMethod: " + request.getMethod() + "\nParameters: "
//				+ request.getParameters());
//		System.out.println("**************************************");
//		System.out.println(" +++++> parameters ..." + executed_sql);
		return formatTransmitParams(res);
	}

}
