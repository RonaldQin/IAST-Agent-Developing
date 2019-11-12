package com.engine.bean;

import java.util.Stack;

public class HttpRequestHelperStack {

	private static Stack<HttpRequestInfo> stack = new Stack<HttpRequestInfo>();

	public static void push(HttpRequestInfo httpRequestInfo) {
		stack.push(httpRequestInfo);
	}

	public static HttpRequestInfo pop() {
		return stack.pop();
	}

	public static HttpRequestInfo peek() {
		return stack.peek();
	}
}
