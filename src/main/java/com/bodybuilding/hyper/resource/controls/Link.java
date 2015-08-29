package com.bodybuilding.hyper.resource.controls;

public class Link {

	private String href;
	private String rel;
	private String name;
	private String type;
	
	public Link(String rel, String href) {
		this.rel = rel;
		this.href = href;
	}
	
	public Link(String rel, String href, String name, String type) {
		this.rel = rel;
		this.href = href;
		this.name = name;
		this.type = type;
	}
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
