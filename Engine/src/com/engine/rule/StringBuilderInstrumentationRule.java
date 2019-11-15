package com.engine.rule;

public class StringBuilderInstrumentationRule extends AbstractRule {

	private static StringBuilderInstrumentationRule instance;

	protected StringBuilderInstrumentationRule(ClassLoader loader, byte[] classfileBuffer, String className) {
		super(loader, classfileBuffer, className);
		// TODO Auto-generated constructor stub
	}

	public static StringBuilderInstrumentationRule getInstance(ClassLoader loader, byte[] classfileBuffer,
			String className) {
		if (instance == null) {
			synchronized (StringBuilderInstrumentationRule.class) {
				if (instance == null) {
					instance = new StringBuilderInstrumentationRule(loader, classfileBuffer, className);
				}
			}
		}
		return instance;
	}

	public static StringBuilderInstrumentationRule getInstance() {
		if (instance == null) {
			return null;
		}
		return instance;
	}

	@Override
	public String insert_addChecker() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String transmit_check_params() {
		// TODO Auto-generated method stub
		return null;
	}

}
