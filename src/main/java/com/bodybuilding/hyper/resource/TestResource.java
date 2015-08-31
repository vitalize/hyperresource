package com.bodybuilding.hyper.resource;

public class TestResource implements HyperResource {

	private String text1;
	String text2;
	
	public TestResource(String text1, String text2) {
		this.text1 = text1;
		this.text2 = text2;
	}
	
	public String getText1() {
		return text1;
	}
	
}