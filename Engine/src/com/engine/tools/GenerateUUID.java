package com.engine.tools;

import java.util.UUID;

public class GenerateUUID {
	public static void main(String[] args) {
		String s = UUID.randomUUID().toString();
		System.out.println("s: " + s.toUpperCase()); // 输出 UUID
	}
}
