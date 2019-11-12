package com.engine.bean;

import java.util.Map;

/**
 * 映射JS规则类。
 * 
 * @author lace
 *
 */
public class Rule {

	public static final String INSERT_BEFORE = "insert_before";
	public static final String INSERT_AFTER = "insert_after";

	private boolean isRule;
	private String id;
	private String knowledge_id;
	private String className;
	private Map<String, Map<String, Map<String, String>>> transform;
	private String checker;

	public Map<String, Map<String, Map<String, String>>> getTransform() {
		return transform;
	}

	public void setTransform(Map<String, Map<String, Map<String, String>>> transform) {
		this.transform = transform;
	}

	public Rule() {
	}

	public boolean getIsRule() {
		return isRule;
	}

	public void setIsRule(boolean isRule) {
		this.isRule = isRule;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKnowledge_id() {
		return knowledge_id;
	}

	public void setKnowledge_id(String knowledge_id) {
		this.knowledge_id = knowledge_id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	@Override
	public String toString() {
		return "Rule [isRule=" + isRule + ", id=" + id + ", knowledge_id=" + knowledge_id + ", className=" + className
				+ ", transform=" + transform + ", checker=" + checker + "]";
	}

}
