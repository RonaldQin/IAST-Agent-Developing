package com.engine.rule;

public class GetHttpRequestParameterSourceRule extends AbstractRule {

	private static GetHttpRequestParameterSourceRule instance;

	public static GetHttpRequestParameterSourceRule getInstance(ClassLoader loader, byte[] classfileBuffer,
			String className) {
		if (instance == null) {
			synchronized (GetHttpRequestParameterSourceRule.class) {
				if (instance == null) {
					instance = new GetHttpRequestParameterSourceRule(loader, classfileBuffer, className);
				}
			}
		}
		return instance;
	}

	public static GetHttpRequestParameterSourceRule getInstance() {
		if (instance == null) {
			return null;
		}
		return instance;
	}

	protected GetHttpRequestParameterSourceRule(ClassLoader loader, byte[] classfileBuffer, String className) {
		super(loader, classfileBuffer, className);
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
