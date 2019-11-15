package com.engine.rule;

public class StringInstrumentationRule extends AbstractRule {

	private static StringInstrumentationRule instance;

	public static StringInstrumentationRule getInstance(ClassLoader loader, byte[] classfileBuffer, String className) {
		if (instance == null) {
			synchronized (StringInstrumentationRule.class) {
				if (instance == null) {
					instance = new StringInstrumentationRule(loader, classfileBuffer, className);
				}
			}
		}
		return instance;
	}

	public static StringInstrumentationRule getInstance() {
		if (instance == null) {
			return null;
		}
		return instance;
	}

	protected StringInstrumentationRule(ClassLoader loader, byte[] classfileBuffer, String className) {
		super(loader, classfileBuffer, className);
		// TODO Auto-generated constructor stub
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
