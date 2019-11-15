package com.engine;

import java.lang.instrument.Instrumentation;
import java.util.LinkedList;
import java.util.List;

import com.boot.Module;
import com.engine.bean.Rule;
import com.engine.jni.V8JNI;
import com.engine.tools.RuleFileUtils;
import com.engine.transformer.ClassesTransformer;
import com.engine.transformer.test.TestClassTransfomer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Engine implements Module {

	private static Engine instance;

	private final String ruleFilesStorePath = "/home/lace/Documents/workspace-sts-3.9.10.RELEASE/IAST-Agent-develop/Engine/rules";
	private List<String> rulePathsList;
	private List<Rule> rules = new LinkedList<Rule>();
	private String json;

	@Override
	public void start(Instrumentation inst) throws Throwable {
//		ObjectMapper o = new ObjectMapper();
//		System.out.println("CLASSLOADER: " + o.getClass().getClassLoader().toString());
//		System.out.println(" CLASSLOADER: " + this.getClass().getClassLoader().toString());
//		this.getClass().getClassLoader().loadClass("com.fasterxml.jackson.databind.ObjectMapper");

		V8JNI v8jni = V8JNI.getInstance();
		rulePathsList = RuleFileUtils.getRuleFilePaths(ruleFilesStorePath);
		json = v8jni.parseRules(rulePathsList.toArray(new String[rulePathsList.size()]));
		System.out.println("JSON: " + json);
		ObjectMapper mapper = new ObjectMapper();
		rules = mapper.readValue(json, new TypeReference<List<Rule>>() {
		});
		new ClassesTransformer(rules, inst).retransform();
		new TestClassTransfomer("", inst).retransform();

		System.out.println("OVER");
	}

}
