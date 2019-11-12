package com.engine.test;

import java.util.ArrayList;
import java.util.List;

public class TestMain {

	public static void main(String[] args) {
		List<String> ls = null;
		new TestMain().f(ls);
		System.out.println(ls);
	}

	public void f(List<String> ls) {
		ls = new ArrayList<String>();
		ls.add("qqqq");
		ls.add("ttt");
	}
}
