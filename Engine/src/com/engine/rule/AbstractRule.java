package com.engine.rule;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.boot.ModuleLoader;
import com.engine.bean.Rule;
import com.engine.bean.StringTypeSource;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

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
		/* 压缩JS代码 */
		StringWriter result = new StringWriter();
		try {
			JavaScriptCompressor compressor = new JavaScriptCompressor(new StringReader(checker), null);
			compressor.compress(result, -1, true, false, false, false);
			result.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		checker = result.toString().replaceAll("\"", "\\\\\""); // 把传递的checker字符串中的双引号进行转义
//		System.out.println("set checker: ###" + checker + "###"); //
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

	public String formatTransmitParams(String[] args) {
		return Arrays.toString(args).replaceAll("(\\[|\\])", "");
	}

	/**
	 * 标记Source点。
	 * 
	 * @return
	 */
	public String insert_LabelSource() {
		pool.importPackage(StringTypeSource.class.getCanonicalName());

		StringBuffer code_buffer = new StringBuffer("");
		try {
			dealMethod.addLocalVariable("source", pool.get(StringTypeSource.class.getCanonicalName()));
			code_buffer.append("source = new StringTypeSource($_);");
//			code_buffer.append("System.out.println(\"Source is: \" + source.toString());");
		} catch (Exception e) {
			e.printStackTrace();
		}

//		System.out.println("Is insert request getParameters method ...");
//		code_buffer.append("System.out.println(\"Source value is: \" + $_);");

		return code_buffer.toString();
	}

	public String insert_transmitAllTaint() {
		pool.importPackage(StringTypeSource.class.getCanonicalName());
		StringBuffer code_buffer = new StringBuffer("");
		try {
			dealMethod.addLocalVariable("mayBeTaint", pool.get("java.lang.Boolean"));
			code_buffer.append("System.out.println(\"All parameters: \" + $1);");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code_buffer.toString();
	}

	/**
	 * 插桩添加检测规则逻辑。
	 * 
	 * @return
	 */
	public abstract String insert_addChecker();

	/**
	 * 添加需要传递给检测规则逻辑的实际参数。
	 * 
	 * @return
	 */
	public abstract String transmit_check_params();

//	public static void main(String[] args) {
//		String str = "function(a){print(\"exec sql: \"+a);return true}";
//		System.out.println(str.replaceAll("\"", "\\\\\""));
//	}
}
