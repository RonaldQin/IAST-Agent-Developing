package com.engine.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.engine.bean.Rule;
import com.engine.rule.AbstractRule;

public class ClassesTransformer implements ClassFileTransformer {

	private List<Rule> rules;
	private Instrumentation inst;

	public ClassesTransformer(List<Rule> rules, Instrumentation inst) {
		this.rules = rules;
		inst.addTransformer(this, true);
		this.inst = inst;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		String targetClassName = className.replaceAll("/", ".");
		if (rules != null && rules.size() > 0) {
			for (Rule rule : rules) {
				Map<String, Map<String, Map<String, String>>> transform = rule.getTransform();
				for (Map.Entry<String, Map<String, Map<String, String>>> instrument : transform.entrySet()) { // 遍历插桩规则
					if (instrument.getKey().equals(targetClassName)) {
						AbstractRule ruleLogic;
						try {
							Class c = Class.forName(rule.getClassName());
							Method getInstance = c.getMethod("getInstance", ClassLoader.class, byte[].class,
									String.class);
							ruleLogic = (AbstractRule) getInstance.invoke(null, loader, classfileBuffer,
									targetClassName);
							ruleLogic.changeDealClass(targetClassName); // 指定需要修改的类
							if (rule.getChecker() != null) {
								ruleLogic.setChecker(rule.getChecker());
							}
							/* 执行插桩规则 */
							for (Map.Entry<String, Map<String, String>> insertion : instrument.getValue().entrySet()) {
//						System.out.println(targetClassName + " --- " + insertion.getKey());
								ruleLogic.changeDealMethod(insertion.getKey()); // 指定要修改的方法
								for (Map.Entry<String, String> logic : insertion.getValue().entrySet()) { // 遍历插桩逻辑
//							System.out.println("    " + logic.getKey() + " : " + logic.getValue());
									ruleLogic.callInsertion(logic.getKey(), logic.getValue()); // 执行插桩动作
								}
							}
							classfileBuffer = ruleLogic.completeDealClass(); // 完成对类的修改
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		return classfileBuffer;
	}

	public ClassesTransformer retransform() {
		LinkedList<Class> retransformClasses = new LinkedList<Class>();
		Class[] loadedClasses = inst.getAllLoadedClasses();
		for (Class clazz : loadedClasses) {
			if (rules != null && rules.size() > 0) {
				for (Rule rule : rules) {
					Map<String, Map<String, Map<String, String>>> transform = rule.getTransform();
					for (Map.Entry<String, Map<String, Map<String, String>>> instrument : transform.entrySet()) {
						if (clazz.getName().equals(instrument.getKey()) && inst.isModifiableClass(clazz)) {
							try {
								inst.retransformClasses(clazz);
							} catch (UnmodifiableClassException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return this;
	}
}
