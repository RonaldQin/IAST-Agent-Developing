package com.engine.task;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.List;

import com.engine.bean.Rule;
import com.engine.jni.V8JNI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseRule {

	private static ParseRule instance;

	private ParseRule() {
	}

	public static ParseRule getInstance() {
		if (instance == null) {
			synchronized (ParseRule.class) {
				if (instance == null) {
					instance = new ParseRule();
				}
			}
		}
		return instance;
	}

	private Integer lock = 1;

	public synchronized String getJSON(String[] ruleFilePaths) {
		while (lock != 1) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String result = V8JNI.getInstance().parseRules(ruleFilePaths);
		lock = 2;
		this.notifyAll();
		return result;
	}

	public synchronized List<Rule> mapRuleObj(String json, List<Rule> rules, Instrumentation inst) {
		while (lock != 2) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		List<Rule> res = null;
		try {
			res = mapper.readValue(json, new TypeReference<List<Rule>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (res != null) {
			for (Rule r : res) {
				rules.add(r);
			}
		}
		System.out.println("Inner rules: " + rules);
//		if (rules != null && rules.size() > 0) {
//			for (Rule rule : rules) {
//				new ClassesTransformer(rule, inst).retransform();
//			}
//		}
		lock = 3;
		this.notifyAll();
		return rules;
	}
}
