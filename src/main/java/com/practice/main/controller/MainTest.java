package com.practice.main.controller;

import java.util.Arrays;

public class MainTest {

	public static void main(String[] args) {
		String str = "100";
		Integer strInteger = Integer.parseInt(str);
		int strInt = strInteger.intValue();
		Integer intInteger = strInt;

		System.out.println("integer1 = " + strInteger);
		System.out.println("intValue = " + strInt);
		System.out.println("integer2 = " + intInteger);

	}

}
